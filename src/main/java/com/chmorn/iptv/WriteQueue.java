package com.chmorn.iptv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author chenxu
 * @version 1.0
 * @className ReadQueue
 * @description 将下载地址放到队列
 * @date 2021/7/27
 **/
public class WriteQueue implements Runnable {

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");

	//终止线程标志（volatile修饰符用来保证其它线程读取的总是该变量的最新的值）
	private volatile boolean exit = false;

	/**
	 * 队列，存放下载地址
	 * BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10);
	 **/
	private BlockingQueue<String> queue;
	/**
	 * m3u8地址
	 * 例如：http://183.207.248.237/ott.js.chinamobile.com/PLTV/3/224/3221227482/index.m3u8
	 **/
	private String m3u8url;
	/**
	 * 定时开始时间
	 **/
	private String timeStart;
	/**
	 * 定时结束时间
	 **/
	private String timeEnd;

	private WriteQueue(){

	}
	public WriteQueue(BlockingQueue<String> queue,String m3u8url,String timeStart,String timeEnd){
		this.queue = queue;
		this.m3u8url = m3u8url;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
	}

	@Override
	public void run() {
		System.out.println("==================="+m3u8url);
		/*while (!exit){
			try {
				String str = m3u8url+"---------"+LocalTime.now().toString();
				queue.put(str);
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        Document res = null;
        String m3u8 = null;
		String line = null;
        BufferedReader br = null;

		//this.timeStart = timeStart;
		//this.timeEnd = 0;

		LocalDateTime start = LocalDateTime.parse(timeStart,dateTimeFormatter);
		LocalDateTime stop = LocalDateTime.parse(timeEnd,dateTimeFormatter);

		String rooturl = null;
		try {
			URL m3 = new URL(m3u8url);
			rooturl = m3.getProtocol()+"://"+m3.getHost()+m3.getPath();
			rooturl = rooturl.substring(0, rooturl.lastIndexOf("/")+1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        while(!exit) {
        	//还未到开始时间
			if(LocalDateTime.now().isBefore(start)){
				try {
					Thread.sleep(10*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			//结束时间已过,退出
			if (LocalDateTime.now().isAfter(stop)){
				exit = true;
				break;
			}

        	try {
    			res = Jsoup.connect(m3u8url).headers(headers).ignoreContentType(true).get();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		m3u8 = res.body().html().replaceAll(" ", "\n");
    		br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(m3u8.getBytes())));
    		int tsCount = 0;
    		try {
				while((line=br.readLine())!=null) {
					if(StringUtils.isNotEmpty(line) && !line.startsWith("#")) {
						queue.put(rooturl+line);
						tsCount++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    		if(br!=null) {
    			try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			br = null;
    		}
    		res = null;
            m3u8 = null;
    		line = null;
    		try {
    			//休眠
				Thread.sleep((tsCount-1)*10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		
	}

	public boolean isExit() {
		return exit;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}
}
