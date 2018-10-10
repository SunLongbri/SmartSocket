package com.shoneworn.smartplug.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/9/1.
 */

public class ThreadPool  {

    private static final ThreadPool pool = new ThreadPool();
    private ThreadPoolExecutor threadpool =  new ThreadPoolExecutor(2, 3, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

   public static ThreadPool getInstance (){
       return pool;
   }

   public void excute( Runnable runable){
       threadpool.execute(runable);
   }



}
