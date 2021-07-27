package com.chmorn.controller;

import com.chmorn.base.ApiCode;
import com.chmorn.base.ApiResult;
import com.chmorn.config.GlobalConfig;
import com.chmorn.iptv.QueueModel;
import com.chmorn.iptv.QueueThreadPool;
import com.chmorn.iptv.ReadQueue;
import com.chmorn.iptv.WriteQueue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author chenxu
 * @version 1.0
 * @className IptvController
 * @description 下载iptv
 * @date 2021/7/27
 **/
@RestController
@Api(tags = "IptvController")
@RequestMapping(value = "/iptv")
public class IptvController {

    private static Logger logger = LoggerFactory.getLogger(IptvController.class);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");
    @Autowired
    private GlobalConfig config;

    public static void main(String[] args) {
        LocalDateTime l = LocalDateTime.parse("2021-07-06 1:4:1",dateTimeFormatter);
        System.out.println(l.format(dateTimeFormatter));
    }

    @PostMapping(value = "/download")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "m3u8url",value = "m3u8地址",required = true,dataType = "String"),
            @ApiImplicitParam(name = "distPath",value = "保存目录",required = true,dataType = "String"),
            @ApiImplicitParam(name = "timeStart",value = "定时开始时间",required = true,dataType = "String",example = "2021-7-26 21:15:00"),
            @ApiImplicitParam(name = "timeEnd",value = "定时结束时间",required = true,dataType = "String",example = "2021-7-26 21:15:00")
    })
    @ApiOperation(value = "定时下载",notes = "定时下载")
    public ApiResult download(String m3u8url, String distPath, String timeStart,String timeEnd){
        ApiResult check = checkParams(m3u8url, distPath, timeStart,timeEnd);
        //校验参数
        if(!check.getCode().equals(ApiCode.SUCC.getCode())){
            return check;
        }
        //启动线程
        BlockingQueue<String> queue = QueueThreadPool.getQueue();
        List<QueueModel> queueThreads = QueueThreadPool.getQueueThreads();
        Thread writeThread = new Thread(new WriteQueue(queue,m3u8url));
        writeThread.start();
        Thread readThread = new Thread(new ReadQueue(queue,distPath));
        readThread.start();
        long writeId = writeThread.getId();
        long readId = readThread.getId();
        QueueModel model = new QueueModel(m3u8url, distPath, timeStart,timeEnd);
        model.setWriteThreadId(writeId);
        model.setReadThreadId(readId);
        queueThreads.add(model);
        System.out.println("-----------------model:"+model);
        System.out.println("-----------queueThreads"+queueThreads);
        return ApiResult.result(ApiCode.SUCC,model);
    }

    /**
     * 校验参数是否合法
    **/
    private ApiResult checkParams(String m3u8url, String distPath, String timeStart,String timeEnd){
        //1、校验时间
        try {
            LocalDateTime start = LocalDateTime.parse(timeStart,dateTimeFormatter);
            LocalDateTime end = LocalDateTime.parse(timeEnd,dateTimeFormatter);
            LocalDateTime now = LocalDateTime.now();
            if (start.isAfter(end)){
                return ApiResult.result(ApiCode.TIMESTART_ERROR,"开始时间需在结束时间之前");
            }
//        if (start.isBefore(now)){
//            return ApiResult.result(ApiCode.TIMESTART_ERROR,"开始时间已过去，请重新选择");
//        }
            if(end.isBefore(now)){
                return ApiResult.result(ApiCode.TIMEEND_ERROR,"结束时间已过去，请重新选择");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ApiResult.result(ApiCode.TIME_ERROR);
        }
        //2、校验保存路径
        try {
            if(!new File(distPath).exists()){
                return ApiResult.result(ApiCode.DISTPATH_ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ApiResult.result(ApiCode.DISTPATH_ERROR);
        }
        //3、校验m3u8地址
        try {
            URL url = new URL(m3u8url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setConnectTimeout(500);
            int status = conn.getResponseCode();
            if(status!=200){
                return ApiResult.result(ApiCode.M3U8_TIMEOUT);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ApiResult.result(ApiCode.M3U8_ERROR);
        }
        return ApiResult.success();
    }


}
