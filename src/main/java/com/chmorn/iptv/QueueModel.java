package com.chmorn.iptv;

/**
 * @author chenxu
 * @version 1.0
 * @className QueueModel
 * @description 队列model，每开启一次下载则对应一个新的队列model
 * @date 2021/7/26
 **/
public class QueueModel {
    /**
     * 队列读线程id
    **/
    private long readThreadId;
    /**
     * 队列写线程id
     **/
    private long writeThreadId;
    /**
     * m3u8地址
     **/
    private String m3u8url;
    /**
     * 保存目录
     **/
    private String distPath;
    /**
     * 定时开始时间
     **/
    private String timeStart;
    /**
     * 定时结束时间
     **/
    private String timeEnd;

    public QueueModel(){

    }
    public QueueModel(String m3u8url,String distPath,String timeStart,String timeEnd){
        this.m3u8url = m3u8url;
        this.distPath = distPath;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public long getReadThreadId() {
        return readThreadId;
    }

    public void setReadThreadId(long readThreadId) {
        this.readThreadId = readThreadId;
    }

    public long getWriteThreadId() {
        return writeThreadId;
    }

    public void setWriteThreadId(long writeThreadId) {
        this.writeThreadId = writeThreadId;
    }

    public String getM3u8url() {
        return m3u8url;
    }

    public void setM3u8url(String m3u8url) {
        this.m3u8url = m3u8url;
    }

    public String getDistPath() {
        return distPath;
    }

    public void setDistPath(String distPath) {
        this.distPath = distPath;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    @Override
    public String toString() {
        return "QueueModel{" +
                "readThreadId=" + readThreadId +
                ", writeThreadId=" + writeThreadId +
                ", m3u8url='" + m3u8url + '\'' +
                ", distPath='" + distPath + '\'' +
                ", timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                '}';
    }
}
