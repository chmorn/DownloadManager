package com.chmorn.model;

import java.util.ArrayList;
import java.util.List;

public class TencentModel {

	private String proxyinfo = "";
	private List<String> downloadList = new ArrayList<String>();// preurl+m3u8url
	private String preurl = "";// url 前缀
	private String film = "";// 电影名称
	private String filepath = "";// 下载的目标路径
	private String newPath = "";
	private String sourceType = "";// 源文件类型，合并文件类型同源文件。ts或265ts
	private String mergeFileName = "";// 合并文件名。
	private String videoType = "";// 视频类型
	private String videoName = "";// 分离视频文件名
	private String audioType = "";// 音频类型
	private String audioName = "";// 分离音频文件名
	private String mp4Name = "";// 最终视频名

	private int skipnum = 0;// 跳过片头分段数（片头的广告，根据不同电视设置不同值）

	public String getProxyinfo() {
		return proxyinfo;
	}

	public void setProxyinfo(String proxyinfo) {
		this.proxyinfo = proxyinfo;
	}

	public List<String> getDownloadList() {
		return downloadList;
	}

	public void setDownloadList(List<String> downloadList) {
		this.downloadList = downloadList;
	}

	public String getPreurl() {
		return preurl;
	}

	public void setPreurl(String preurl) {
		this.preurl = preurl;
	}

	public String getFilm() {
		return film;
	}

	public void setFilm(String film) {
		this.film = film;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getNewPath() {
		return newPath;
	}

	public void setNewPath(String newPath) {
		this.newPath = newPath;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getMergeFileName() {
		return mergeFileName;
	}

	public void setMergeFileName(String mergeFileName) {
		this.mergeFileName = mergeFileName;
	}

	public String getVideoType() {
		return videoType;
	}

	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getAudioType() {
		return audioType;
	}

	public void setAudioType(String audioType) {
		this.audioType = audioType;
	}

	public String getAudioName() {
		return audioName;
	}

	public void setAudioName(String audioName) {
		this.audioName = audioName;
	}

	public String getMp4Name() {
		return mp4Name;
	}

	public void setMp4Name(String mp4Name) {
		this.mp4Name = mp4Name;
	}

	public int getSkipnum() {
		return skipnum;
	}

	public void setSkipnum(int skipnum) {
		this.skipnum = skipnum;
	}

	@Override
	public String toString() {
		return "TencentModel [film=" + film + ", videoType=" + videoType + ", audioType="
				+ audioType + ", mp4Name=" + mp4Name + "]";
	}

}
