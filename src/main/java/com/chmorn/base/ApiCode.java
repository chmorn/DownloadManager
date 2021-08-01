package com.chmorn.base;

/**
 * @author chenxu
 * @version 1.0
 * @className TradeCode
 * @description 返回代码
 * @date 2021/7/7
 **/
public enum ApiCode {
    SUCC("0000","操作成功"),
    FAIL("0001","操作失败"),
    DISTPATH_NULL("0010","保存目录为空"),
    DISTPATH_ERROR("0011","保存目录校验失败，非正确的路径"),
    FFMPEGPATH_NULL("0020","路径ffmpegPath为空"),
    FFMPEGPATH_ERROR("0021","路径ffmpegPath校验失败，文件不存在"),
    NODEPATH_NULL("0030","路径nodePath为空"),
    NODEPATH_ERROR("0031","路径nodePath校验失败，文件不存在"),
    TIME_ERROR("0040","校验时间格式失败"),
    TIMESTART_ERROR("0041","开始时间错误"),
    TIMEEND_ERROR("0042","结束时间错误"),
    M3U8_ERROR("0043","m3u8地址有误，连接异常"),
    M3U8_TIMEOUT("0044","m3u8地址连接超时"),
    M3U8_FORMAT("0045","m3u8配置文件读取失败，请检查格式"),
    THREAD_MAX("0046","已达到下载线程上限"),
    STOPDOWNLOAD_IDERROR("0047","请输入正确的id"),
    OTHER("other","其他异常");

    private String code;
    private String msg;

    ApiCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsg(String code) {
        for (ApiCode tc : ApiCode.values()) {
            if(tc.code == code){
                return tc.msg;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
