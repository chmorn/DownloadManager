package com.chmorn.iptv;

/**
 * @author chenxu
 * @version 1.0
 * @className M3u8Model
 * @description 解析application.m3u8,存放名称和地址
 * @date 2021/7/27
 **/
public class M3u8Model {

    private String name;
    private String url;

    public M3u8Model() {
    }

    public M3u8Model(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "M3u8Model{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String toUrlString(){
        return name+" @ "+url;
    }
}
