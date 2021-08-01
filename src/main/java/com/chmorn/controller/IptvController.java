package com.chmorn.controller;

import com.chmorn.base.ApiCode;
import com.chmorn.base.ApiResult;
import com.chmorn.config.GlobalConfig;
import com.chmorn.config.M3u8Config;
import com.chmorn.iptv.*;
import com.chmorn.model.RequestModel;
import com.chmorn.model.ResponseModel;
import com.chmorn.utils.DownloadUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
import java.util.concurrent.LinkedBlockingQueue;

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

    @PostMapping(value = "/getM3u8List")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "",value = "",dataType = "",required = true)
    })
    @ApiOperation(value = "查询下载地址",notes = "查询下载地址")
    public ApiResult getM3u8List(){
        List<M3u8Model> list = null;
        try {
            list = M3u8Config.getM3u8List();
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.result(ApiCode.M3U8_FORMAT);
        }
        return ApiResult.result(ApiCode.SUCC,list);
    }

    @GetMapping(value = "/getDownloadList")
    @ApiOperation(value = "查询下载列表",notes = "查询下载列表")
    public ApiResult getDownloadList(){
        JSONArray arr = new JSONArray();
        List<QueueModel> list = QueueThreadPool.getQueueThreads();
        for (int i = 0; i < list.size(); i++) {
            QueueModel model = list.get(i);
            arr.add(model.getQueueJson());
        }
        return ApiResult.result(ApiCode.SUCC,arr);
    }

    @GetMapping(value = "/stopDownload")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "downloadId",value = "下载id",dataType = "String",required = true)
    })
    @ApiOperation(value = "停止下载",notes = "停止下载")
    public synchronized ApiResult stopDownload(String downloadId){
        if(StringUtils.isEmpty(downloadId)){
            return ApiResult.result(ApiCode.STOPDOWNLOAD_IDERROR,"error!id为空");
        }
        int id = -1;
        try {
            id = Integer.parseInt(downloadId);
        }catch (NumberFormatException e){
            return ApiResult.result(ApiCode.STOPDOWNLOAD_IDERROR,"id格式错误");
        }
        List<QueueModel> queueThreads = QueueThreadPool.getQueueThreads();
        long start = System.currentTimeMillis();//用来计算时间
        for (int i = 0; i < queueThreads.size(); i++) {
            if(queueThreads.get(i).getDownloadId() == id){
                QueueModel removeModel = queueThreads.get(i);
                //1、停线程(先停写线程，再停读线程，防止读线程停止了，写线程还在往队列写,导致队列数据量大，内存溢出)
                //停写线程
                removeModel.getWriteQueue().setExit(true);
                while(true){
                    if(!removeModel.getWriteThread().isAlive()){
                        removeModel.setWriteQueue(null);
                        //停读线程
                        removeModel.getReadQueue().setExit(true);
                        if(!removeModel.getReadThread().isAlive()){
                            removeModel.setReadQueue(null);
                            break;
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //2、从下载列表中修改该条数据状态为停止下载
                boolean removeFlag = QueueThreadPool.stopThread(removeModel);
                if (!removeFlag){
                    return ApiResult.result(ApiCode.STOPDOWNLOAD_IDERROR,"发生异常，请检查该下载任务是否还在下载，如还在下载，请关闭客户端");
                }
                break;
            }
        }
        long end = System.currentTimeMillis();//用来计算时间
        long userSeconds = (end-start)/1000;
        System.out.println("停止成功，耗时(秒):"+userSeconds);
        return ApiResult.result(ApiCode.SUCC,"停止成功，耗时:"+userSeconds+"秒");
    }

    @PostMapping(value = "/download")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "m3u8url",value = "m3u8地址",required = true,dataType = "String",example = "http://183.207.248.142/ott.js.chinamobile.com/PLTV/3/224/3221227467/index.m3u8"),
            @ApiImplicitParam(name = "distPath",value = "保存目录",required = true,dataType = "String",example = "D:/iqyvideo"),
            @ApiImplicitParam(name = "timeStart",value = "定时开始时间",required = true,dataType = "String",example = "2021-7-26 21:15:00"),
            @ApiImplicitParam(name = "timeEnd",value = "定时结束时间",required = true,dataType = "String",example = "2021-7-27 21:15:00")
    })
    @ApiOperation(value = "定时下载",notes = "定时下载")
    public synchronized ApiResult download(@RequestBody RequestModel requestModel) throws InterruptedException {
        String m3u8url = requestModel.getM3u8url().split(" @ ")[1];
        String distPath = requestModel.getDistPath();
        String timeStart = requestModel.getTimeStart();
        String timeEnd = requestModel.getTimeEnd();
        ApiResult check = checkParams(m3u8url, distPath, timeStart,timeEnd);
        //校验参数
        if(!check.getCode().equals(ApiCode.SUCC.getCode())){
            return check;
        }
        //获取线程和队列信息
        //BlockingQueue<String> queue = QueueThreadPool.getQueue();
        //队列（存放下载地址）
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(16);
        List<QueueModel> queueThreads = QueueThreadPool.getQueueThreads();
        //判断是否已达到最大下载数
        int liveSize = 0;
        for (int i = 0; i < queueThreads.size(); i++) {
            if(queueThreads.get(i).getState() == 0){
                liveSize++;
            }
        }
        if(liveSize >= config.getMaxThreads()){
            return ApiResult.result(ApiCode.THREAD_MAX,"error！！！已达到下载上限："+config.getMaxThreads());
        }
        //防止把下载数配置的太大
        if(config.getMaxThreads() >= 8){
            return ApiResult.result(ApiCode.THREAD_MAX,"error！！！已达到下载上限："+config.getMaxThreads());
        }
        //获取id
        int newId = DownloadUtils.getDownloadId();
        //启动线程
        WriteQueue writeQueue = new WriteQueue(queue, m3u8url,timeStart,timeEnd);
        Thread writeThread = new Thread(writeQueue);
        writeThread.start();
        ReadQueue readQueue = new ReadQueue(queue, distPath,newId,timeStart,timeEnd);
        Thread readThread = new Thread(readQueue);
        readThread.start();

        QueueModel model = new QueueModel(newId,m3u8url, distPath, timeStart,timeEnd,0);
        model.setWriteQueue(writeQueue);
        model.setReadQueue(readQueue);
        model.setWriteThread(writeThread);
        model.setReadThread(readThread);
        //将新线程添加到下载线程池
        QueueThreadPool.addQueueThreads(model);
        return ApiResult.result(ApiCode.SUCC ,model.getQueueJson());
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
       /* try {
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
        }*/
        return ApiResult.success();
    }

}
