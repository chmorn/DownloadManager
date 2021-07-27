package com.chmorn.iptv;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.io.FileUtils;

/**
 * @author chenxu
 * @version 1.0
 * @className ReadQueue
 * @description 读取队列并下载
 * @date 2021/7/27
 **/
public class ReadQueue implements Runnable {

	/**
	 * 队列，存放下载地址
	 * BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10);
	**/
	private BlockingQueue<String> queue;
	/**
	 * 下载目录
	 * 例如：M:/iptv/hntv_high/
	 **/
	private String distPath;

	private ReadQueue(){

	}
	public ReadQueue(BlockingQueue<String> queue,String distPath){
		this.queue = queue;
		this.distPath = distPath;
	}

	@Override
	public void run() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String path = distPath+df.format(new Date())+"/";
		File dir = new File(path);
		if(!dir.exists()) {
			dir.mkdir();
		}
		String filename = null;
		String urlstr = null;
		URL url = null;
		File file = null;
        while(true) {
        	if(queue.size()>0) {
        		urlstr = queue.poll();
        		filename = urlstr.substring(urlstr.lastIndexOf("/")+1);
        		file = new File(path+filename);
        		//System.out.println("poll-----"+file.getPath());
        		/*if(!file.exists()) {
        			try {
                		url = new URL(urlstr);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        				conn.setConnectTimeout(3 * 1000);
        				conn.setUseCaches(true);
        				FileUtils.copyURLToFile(url, file);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}*/
        	}
        	filename = null;
    		urlstr = null;
    		url = null;
    		file = null;
        }
		
	}

}
