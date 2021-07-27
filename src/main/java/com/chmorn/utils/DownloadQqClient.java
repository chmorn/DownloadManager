package com.chmorn.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.chmorn.config.GlobalConfig;
import com.chmorn.model.TencentModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 腾讯视频下载
 * 找到https://vd.l.qq.com/proxyhttp
 * 的响应，即proxyinfo
 * java -Dfile.encoding=utf-8 -jar demo.jar
 */
public class DownloadQqClient {
	//ffmpeg 位置
	//private static String ffmpegPath = "D:/tools/ffmpeg/bin/ffmpeg.exe";
	private static String ffmpegPath = "";//ffmpeg路径
	private static String root = "";//视频存放路径

	/**视频剪切命令 从136秒开始，持续剪切2394秒，E15.mp4是原视频，new.mp4是结果视频
	 * ffmpeg -ss 136 -t 2394 -accurate_seek -i E15.mp4 -codec copy -avoid_negative_ts 1 new.mp4
	 */
	public static void main(String[] args) throws Exception {
		String path =System.getProperty("user.dir");
		System.out.println(path);
		BufferedReader br = new BufferedReader(new FileReader(path+ "/source/list.txt"));
		String line = null;
		while (StringUtils.isNotBlank(line = br.readLine())) {
			TencentModel model = new TencentModel();
			model.setProxyinfo(line);
			start(model);
			System.out.println("完成下载视频信息："+model);
			//下载一个视频则休眠1秒，控制下载速度
			Thread.sleep(1000);
		}
		br.close();
		System.out.println("over======over================================================================");
	}

	public static void download(GlobalConfig config) throws Exception {
		ffmpegPath = config.getFfmpegPath();
		root = config.getQqPath();
		String path =System.getProperty("user.dir");
		BufferedReader br = new BufferedReader(new FileReader(path+ "/source/list.txt"));
		String line = null;
		while (StringUtils.isNotBlank(line = br.readLine())) {
			TencentModel model = new TencentModel();
			model.setProxyinfo(line);
			start(model);
			System.out.println("完成下载视频信息："+model);
			//下载一个视频则休眠1秒，控制下载速度
			Thread.sleep(1000);
		}
		br.close();
		System.out.println("over======over================================================================");
	}
	
	public static void start(TencentModel model) throws Exception {
		long start = System.currentTimeMillis();
		initVinfo(model);
		downloadByFullPath(model);
		mixTs(model);
		getVideoDetail(model);
		convertTs(model);
		long end = System.currentTimeMillis();
		System.out.println("一共耗时（秒）："+(end-start)/1000);
	}
	
	/**
	 * 初始化信息:文件名、转换MP4文件名、下载路径、下载url
	 */
	public static void initVinfo(TencentModel model) throws IOException {
		JSONObject js = JSONObject.fromObject(model.getProxyinfo());
		String vinfo = String.valueOf(js.get("vinfo")).replace("QZOutputJson=", "").replace(";", "");
		JSONObject vjson = JSONObject.fromObject(vinfo);
		JSONArray vilist = vjson.getJSONObject("vl").getJSONArray("vi");
		JSONObject vi = vilist.getJSONObject(0);
		//文件名
		//model.setFilm(String.valueOf(vi.get("ti")).replaceAll(" ", ""));
		model.setFilm(new String(String.valueOf(vi.get("ti")).getBytes(),"UTF-8").replaceAll(" ", ""));
		System.out.println(model.getFilm()+"----------------------------");
		model.setMp4Name(model.getFilm()+".mp4");
		//下载路径
		model.setFilepath(root+model.getFilm()+"/");
		String firsturl = String.valueOf(vi.getJSONObject("ul").getJSONArray("ui").getJSONObject(1).get("url"));
		model.setPreurl(firsturl.substring(0, firsturl.lastIndexOf("/")+1));
		String m3u8 = String.valueOf(vi.getJSONObject("ul").get("m3u8"));
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(m3u8.getBytes())));
		String line = "";
		//下载url
		while((line=br.readLine())!=null) {
			if(!line.startsWith("#")) {
				model.getDownloadList().add(model.getPreurl()+line);
			}
		}
	}
	//下载视频
	public static void downloadByFullPath(TencentModel model) throws Exception {
		File file = new File(model.getFilepath());
		if(file.exists()) {
			FileUtils.deleteDirectory(file);
		}
		try {
			Thread.sleep(500);
			file.mkdir();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("开始下载视频.............");
		for (int i = 0; i < model.getDownloadList().size(); i++) {
			String u = model.getDownloadList().get(i);
			URL url = new URL(u);
			String filename = url.getPath().substring(url.getPath().lastIndexOf("/")+1);
			if(i==0) {
				model.setSourceType(filename.substring(filename.lastIndexOf(".")));
				model.setMergeFileName(model.getFilm()+model.getSourceType());
			}
			downVideo(model,url, i+model.getSourceType());
			//下载一段视频休眠0.2秒，控制下载速度
			Thread.sleep(200);
		}
		System.out.println("视频下载完成............."+model.getDownloadList().size()+"段");
	}
	// ts视频合并
	public static void mixTs(TencentModel model) throws Exception {
		System.out.println("开始合并.............");
		model.setNewPath(model.getFilepath() + "new/");
		File newdir = new File(model.getNewPath());
		if (!newdir.exists()) {
			newdir.mkdir();
		}
		FileInputStream fis = null;
		FileOutputStream fos = new FileOutputStream(model.getNewPath() + File.separator + model.getMergeFileName());
		File dir = new File(model.getFilepath());
		File[] files = dir.listFiles();
		byte[] buffer = new byte[1024];// 一次读取1K
		int len;
		// 长度减1（有个new文件夹）
		for (int i = model.getSkipnum(); i < files.length - 1; i++) {
			fis = new FileInputStream(new File(model.getFilepath() + i + model.getSourceType()));
			len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);// buffer从指定字节数组写入。buffer:数据中的起始偏移量,len:写入的字数。
			}
			fis.close();
		}
		fos.flush();
		fos.close();
		System.out.println("合并完成.............");
		System.out.println("开始删除分段视频.............");
		for (File tsfile : files) {
			if(tsfile.exists() && tsfile.isFile()) {
				tsfile.delete();
			}
		}
		System.out.println("删除完成.............");
	}
	
	//获取视频、音频格式
	public static void getVideoDetail(TencentModel model) {
		String f = model.getNewPath()+model.getMergeFileName();
		String command1 = ffmpegPath + " -i " + f;
		System.out.println(command1);
		ProcessCommandUtil p1 = new ProcessCommandUtil(command1);
		p1.start();
		if(p1.isProcessSuccess()) {
			System.out.println("p1完成.............");
			model.setVideoType("."+p1.getVtype().get("Video"));
			model.setAudioType("."+p1.getVtype().get("Audio"));
			model.setVideoName(model.getFilm()+model.getVideoType());
			model.setAudioName(model.getFilm()+model.getAudioType());
		}
	}
	
	// 转换格式（new文件夹）
	public static void convertTs(TencentModel model) throws IOException {
		// ffmpeg命令
		String mixts = model.getNewPath() + model.getMergeFileName();
		String newvideo = model.getNewPath() + model.getVideoName();
		String newaudio = model.getNewPath() + model.getAudioName();
		String newmp4 = model.getNewPath() + model.getMp4Name();
		File f1 = new File(newvideo);
		if (f1.exists())
			f1.delete();
		File f2 = new File(newaudio);
		if (f2.exists())
			f2.delete();
		File f3 = new File(newmp4);
		if (f3.exists())
			f3.delete();
		String command1 = ffmpegPath +" -i " + mixts + " -vcodec copy -an " + newvideo;
		String command2 = ffmpegPath +" -i " + mixts + " -acodec copy -vn " + newaudio;
		String command3 = ffmpegPath +" -i " + newvideo + " -i " + newaudio + " -c copy " + newmp4;
		System.out.println(command1);
		System.out.println(command2);
		System.out.println(command3);
		ProcessCommandUtil p1 = new ProcessCommandUtil(command1);
		ProcessCommandUtil p2 = new ProcessCommandUtil(command2);
		ProcessCommandUtil p3 = new ProcessCommandUtil(command3);
		System.out.println("开始提取视频.............");
		p1.start();
		if(p1.isProcessSuccess()) {
			System.out.println("开始提取音频.............");
			p2.start();
			if(p2.isProcessSuccess()) {
				System.out.println("开始转换mp4.............");
				p3.start();
			}
		}
		if(p1.isProcessSuccess() && p2.isProcessSuccess() && p3.isProcessSuccess()) {
			System.out.println("转换mp4完成.............");
			System.out.println("success-----------------");
		}else {
			System.out.println("转换mp4异常.............");
			System.out.println("fail-----------------");
		}
		System.out.println("开始删除临时视频.............");
		FileUtils.copyFile(new File(model.getNewPath() + model.getMp4Name()), new File(model.getFilepath() + model.getMp4Name()));
		FileUtils.deleteDirectory(new File(model.getNewPath()));
		System.out.println("删除完成.............");
	}

	public static void downVideo(TencentModel model,URL url, String fileName) {
		// 判断目标文件夹是否存在
		File files = new File(model.getFilepath());
		if (!files.exists()) {
			files.mkdirs();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			FileUtils.copyURLToFile(url, new File(model.getFilepath() + fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}