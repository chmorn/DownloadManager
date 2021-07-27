package com.chmorn.iptv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author chenxu
 * @version 1.0
 * @className QueueThreadPool
 * @description 存放开启的下载线程，每个下载add一个Map，每个map含write和read两个线程
 * @date 2021/7/26
 **/
public class QueueThreadPool {
    //队列（存放下载地址）
    private static BlockingQueue<String> queue;
    //下载线程池
    private static List<QueueModel> queueThreads;

    private QueueThreadPool(){
    }

    public static synchronized BlockingQueue<String> getQueue(){
        if(queue == null){
            return new LinkedBlockingQueue<String>(10);
        }
        return queue;
    }

    public static synchronized List<QueueModel> getQueueThreads(){
        if(queueThreads == null){
            return new ArrayList<QueueModel>();
        }
        return queueThreads;
    }

}
