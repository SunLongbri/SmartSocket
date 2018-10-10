package com.shoneworn.smartplug.threadpoll;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * @author chenxiangxiang
 * @email  shoneworn@163.com
 */
public class ThreadPool {
	private ThreadPoolExecutor executor;
	private static class SingletonHolder{
		public static ThreadPool instance = new ThreadPool();
	}

	private ThreadPool() {
		if(executor==null) {
			executor = new ThreadPoolExecutor(10, 50, 100, TimeUnit.MILLISECONDS,
					new ArrayBlockingQueue<Runnable>(100),
					Executors.defaultThreadFactory());
		}
		
	}
	public static ThreadPool getInstance() {
		return SingletonHolder.instance;
	}
	
	//Ϊ�˷�ֹ���õĳ�ʱ���쳣�޷�������ָ��ʹ��submit
	public void submit(Runnable runnable) {
		if(executor==null) {
			executor = new ThreadPoolExecutor(10, 50, 100, TimeUnit.MILLISECONDS,
					new ArrayBlockingQueue<Runnable>(100),
					Executors.defaultThreadFactory());
		}
		executor.submit(runnable);
	}
	
	//Ϊ�˷�ֹ���õĳ�ʱ���쳣�޷�������ָ��ʹ��submit
	public void execute(Runnable runnable) {
		if(executor==null) {
			executor = new ThreadPoolExecutor(10, 50, 100, TimeUnit.MILLISECONDS,
					new ArrayBlockingQueue<Runnable>(100),
					Executors.defaultThreadFactory());
		}
		executor.submit(runnable);
	}
	
	public int getActiveCount() {
		return executor.getActiveCount();
	}
	
	public void shutdown() {
		executor.shutdown();
	}
}
