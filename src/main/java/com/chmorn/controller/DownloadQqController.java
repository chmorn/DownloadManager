package com.chmorn.controller;

import com.chmorn.base.ApiCode;
import com.chmorn.base.ApiResult;
import com.chmorn.config.GlobalConfig;
import com.chmorn.model.CkeyModel;
import com.chmorn.utils.HttpPostUtil;
import com.chmorn.utils.WasmUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * @author chenxu
 * @version 1.0
 * @className DownloadQqController
 * @description TODO
 * @date 2021/7/21
 **/
@RestController
@Api(tags = "DownloadQqController")
@RequestMapping(value = "/qq")
public class DownloadQqController {

    private static Logger logger = LoggerFactory.getLogger(DownloadQqController.class);
    @Autowired
    private GlobalConfig config;

    @GetMapping(value = "/test")
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "url",value = "地址",required = true,dataType = "String")
    })
    @ApiOperation(value = "生成m3u8",notes = "生成m3u8")
    public ApiResult genM3u82(){
        String distPath = config.getQqPath();
        String ffmpegPath = config.getFfmpegPath();
        String nodePath = config.getNodePath();
        logger.info(config.toString());
        //校验distPath
        if(StringUtils.isEmpty(distPath)){
            return ApiResult.result(ApiCode.DISTPATH_NULL);
        }
        if(!distPath.endsWith("/")){
            distPath += "/";
        }
        try {
            File dist = new File(distPath);
            if(!dist.exists()){
                dist.mkdir();
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.DISTPATH_ERROR);
        }
        //校验ffmpegPath
        if(StringUtils.isEmpty(ffmpegPath)){
            return ApiResult.result(ApiCode.DISTPATH_NULL);
        }
        try {
            File ffmpeg = new File(ffmpegPath);
            if(!ffmpeg.exists()){
                throw new Exception("ffmpegPath不存在");
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.DISTPATH_ERROR);
        }
        //校验nodePath
        if(StringUtils.isEmpty(nodePath)){
            return ApiResult.result(ApiCode.NODEPATH_NULL);
        }
        try {
            File node = new File(nodePath);
            if(!node.exists()){
                throw new Exception("nodePath不存在");
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.NODEPATH_ERROR);
        }
        //获取m3u8地址
        return ApiResult.result(ApiCode.SUCC);
    }

    @GetMapping(value = "/genM3u8")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url",value = "地址",required = true,dataType = "String")
    })
    @ApiOperation(value = "生成m3u8",notes = "生成m3u8")
    public ApiResult genM3u8(String url){
        String distPath = config.getQqPath();
        String ffmpegPath = config.getFfmpegPath();
        String nodePath = config.getNodePath();
        //校验distPath
        if(StringUtils.isEmpty(distPath)){
            return ApiResult.result(ApiCode.DISTPATH_NULL);
        }
        if(!distPath.endsWith("/")){
            distPath += "/";
        }
        try {
            File dist = new File(distPath);
            if(!dist.exists()){
                dist.mkdir();
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.DISTPATH_ERROR);
        }
        //校验ffmpegPath
        if(StringUtils.isEmpty(ffmpegPath)){
            return ApiResult.result(ApiCode.DISTPATH_NULL);
        }
        try {
            File ffmpeg = new File(ffmpegPath);
            if(!ffmpeg.exists()){
                throw new Exception("ffmpegPath不存在");
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.DISTPATH_ERROR);
        }
        //校验nodePath
        if(StringUtils.isEmpty(nodePath)){
            return ApiResult.result(ApiCode.NODEPATH_NULL);
        }
        try {
            File node = new File(nodePath);
            if(!node.exists()){
                throw new Exception("nodePath不存在");
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.NODEPATH_ERROR);
        }
        //获取cKey
        CkeyModel ckeyModel = null;
        try {
            ckeyModel = WasmUtil.getCkeyModel(url,config);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.result(ApiCode.FAIL,"发生异常："+e.getMessage());
        }
        //获取m3u8地址
        //return ApiResult.result(ApiCode.SUCC,HttpPostUtil.doPost(ckeyModel));
        return null;
    }

    @GetMapping(value = "/resolver")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url",value = "视频播放地址",required = true,dataType = "String")
    })
    @ApiOperation(value = "根据url解析ckey",notes = "根据url解析ckey")
    public ApiResult resolver(String url){
        String distPath = config.getQqPath();
        String ffmpegPath = config.getFfmpegPath();
        String nodePath = config.getNodePath();
        //校验distPath
        if(StringUtils.isEmpty(distPath)){
            return ApiResult.result(ApiCode.DISTPATH_NULL);
        }
        if(!distPath.endsWith("/")){
            distPath += "/";
        }
        try {
            File dist = new File(distPath);
            if(!dist.exists()){
                dist.mkdir();
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.DISTPATH_ERROR);
        }
        //校验ffmpegPath
        if(StringUtils.isEmpty(ffmpegPath)){
            return ApiResult.result(ApiCode.DISTPATH_NULL);
        }
        try {
            File ffmpeg = new File(ffmpegPath);
            if(!ffmpeg.exists()){
                throw new Exception("ffmpegPath不存在");
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.DISTPATH_ERROR);
        }
        //校验nodePath
        if(StringUtils.isEmpty(nodePath)){
            return ApiResult.result(ApiCode.NODEPATH_NULL);
        }
        try {
            File node = new File(nodePath);
            if(!node.exists()){
                throw new Exception("nodePath不存在");
            }
        }catch (Exception e){
            return ApiResult.result(ApiCode.NODEPATH_ERROR);
        }
        CkeyModel ckeyModel = null;
        try {
            ckeyModel = WasmUtil.getCkeyModel(url,config);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.result(ApiCode.FAIL,"发生异常："+e.getMessage());
        }
        return ApiResult.result(ApiCode.SUCC,ckeyModel);
    }
}
