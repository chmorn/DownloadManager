package com.chmorn.iptv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.chmorn.javafx.DownloadController;
import com.chmorn.utils.SpringUtil;
import org.apache.commons.io.FileUtils;

/**
 * @author chenxu
 * @version 1.0
 * @className ReadQueue
 * @description 读取队列并下载
 * @date 2021/7/27
 **/
public class ReadQueue implements Runnable {

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");

	//终止线程标志（volatile修饰符用来保证其它线程读取的总是该变量的最新的值）
	private volatile boolean exit = false;
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
	private int downloadId;
	/**
	 * 定时开始时间
	 **/
	private String timeStart;
	/**
	 * 定时结束时间
	 **/
	private String timeEnd;

	/**
	 * 临时文件保存目录
	 * 取值：distPath+"temp"+downloadId
	 **/
	private String piecePath;

	/**
	 * 合并后的结果文件
	 * 存放在目录：distPath，文件名：downloadId+"ts"
	 **/
	private String mergeFileName;

	private ReadQueue(){

	}
	public ReadQueue(BlockingQueue<String> queue,String distPath,int downloadId,String timeStart,String timeEnd){
		this.queue = queue;
		if(!distPath.endsWith(File.separator)){
			this.distPath = distPath+File.separator;
		}else{
			this.distPath = distPath;
		}
		this.downloadId = downloadId;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
		this.piecePath = this.distPath+"temp"+downloadId+File.separator;
		this.mergeFileName = this.distPath + downloadId+".ts";
	}

	@Override
	public void run() {
		/*while (!exit){
			try {
				String str = queue.poll()+"---------"+ LocalTime.now().toString();
				Thread.sleep(8*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		File dir = new File(piecePath);
		if(!dir.exists()) {
			dir.mkdir();
		}

		LocalDateTime start = LocalDateTime.parse(timeStart,dateTimeFormatter);
		LocalDateTime stop = LocalDateTime.parse(timeEnd,dateTimeFormatter);

		String filename = null;
		String urlstr = null;
		URL url = null;
		File file = null;
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
			//结束时间已过，并且队列里的地址都已下载完,退出
			if (LocalDateTime.now().isAfter(stop) && queue.size()==0){
				exit = true;
				List<QueueModel> queueThreads = QueueThreadPool.getQueueThreads();
				for (int i = 0; i < queueThreads.size(); i++) {
					if(queue == queueThreads.get(i).getReadQueue()){
						queueThreads.get(i).setState(1);
						System.out.println("---------正常结束");
						break;
					}
				}
				try {
					startMix();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
        	if(queue.size()>0) {
        		urlstr = queue.poll();
        		filename = urlstr.substring(urlstr.lastIndexOf("/")+1);
        		file = new File(piecePath+filename);
        		if(!file.exists()) {
        			try {
                		url = new URL(urlstr);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        				conn.setConnectTimeout(3 * 1000);
        				conn.setUseCaches(true);
        				FileUtils.copyURLToFile(url, file);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
        	}
        	filename = null;
    		urlstr = null;
    		url = null;
    		file = null;
			DownloadController bean = SpringUtil.getBean(DownloadController.class);
			bean.refreshGrid();
        }
		
	}

	// ts视频合并+（new文件夹）
	private void startMix() throws IOException {
		System.out.println("开始合并.............");
		File newdir = new File(distPath);
		/*if (!newdir.exists()) {
			newdir.mkdir();
		}*/
		File dir = new File(piecePath);
		File[] files = dir.listFiles();
		//files读取可能顺序乱了，重新排序
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			list.add(files[i].getName());
		}
		Collections.sort(list);
		//排序结束
		FileInputStream fis = null;
		FileOutputStream fos = new FileOutputStream(mergeFileName);
		byte[] buffer = new byte[1024];// 一次读取1K
		int len;
		System.out.println(files.length);
		for (int i = 0; i < list.size(); i++) {
			fis = new FileInputStream(new File(piecePath+list.get(i)));
			len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);// buffer从指定字节数组写入。buffer:数据中的起始偏移量,len:写入的字数。
			}
			fis.close();
		}
		fos.flush();
		fos.close();
		System.out.println("合并完成.............");

	}

	public boolean isExit() {
		return exit;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}
}
