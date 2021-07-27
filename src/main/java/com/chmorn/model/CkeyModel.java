package com.chmorn.model;

/**
 * @author chenxu
 * @version 1.0
 * @className CkeyModel
 * @description cKey结果model
 * @date 2021/7/21
 **/
public class CkeyModel {
    //生成的cKey
    private String cKey;
    //时间戳:String.valueOf(System.currentTimeMillis()).substring(0,10)
    private String tm;
    //vid
    private String vid;
    //url:https://v.qq.com/x/cover/gehpfier9upkqz5/i0038won4fq.html
    private String url;
    //url编码:https%3A%2F%2Fv.qq.com%2Fx%2Fcover%2Fgehpfier9upkqz5%2Fi0038won4fq.html
    private String encodeUrl;

    public String getcKey() {
        return cKey;
    }

    public void setcKey(String cKey) {
        this.cKey = cKey;
    }

    public String getTm() {
        return tm;
    }


    public void setTm(String tm) {
        this.tm = tm;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEncodeUrl() {
        return encodeUrl;
    }

    public void setEncodeUrl(String encodeUrl) {
        this.encodeUrl = encodeUrl;
    }

    @Override
    public String toString() {
        return "CkeyModel{" +
                "cKey='" + cKey + '\'' +
                ", tm='" + tm + '\'' +
                ", vid='" + vid + '\'' +
                ", url='" + url + '\'' +
                ", encodeUrl='" + encodeUrl + '\'' +
                '}';
    }

}
