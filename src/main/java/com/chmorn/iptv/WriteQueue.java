package com.chmorn.iptv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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

	private WriteQueue(){

	}
	public WriteQueue(BlockingQueue<String> queue,String m3u8url){
		this.queue = queue;
		this.m3u8url = m3u8url;
	}

	@Override
	public void run() {
		Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        Document res = null;
        String m3u8 = null;
		String line = null;
        BufferedReader br = null;

		String rooturl = null;
		try {
			URL m3 = new URL(m3u8url);
			rooturl = m3.getPath().substring(0, m3.getPath().lastIndexOf("/")+1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        while(true) {
        	try {
    			res = Jsoup.connect(m3u8url).headers(headers).ignoreContentType(true).get();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		m3u8 = res.body().html().replaceAll(" ", "\n");
    		br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(m3u8.getBytes())));
    		try {
				while((line=br.readLine())!=null) {
					if(StringUtils.isNotEmpty(line) && !line.startsWith("#")) {
						//System.out.println("put-----"+rooturl+line);
						queue.put(rooturl+line);
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
    			//休眠20秒
				Thread.sleep(20*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		
	}

}
