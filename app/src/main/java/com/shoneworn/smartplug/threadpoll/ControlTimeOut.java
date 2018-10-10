package com.shoneworn.smartplug.threadpoll;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * @author chenxiangxiang
 * @email  shoneworn@163.com
 */
public class ControlTimeOut {

	
	public static <T> void call(Callable<T> callable,RequestListener<T> listener) {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<T> future = executor.submit(callable);
		try {
			T t = future.get(3000, TimeUnit.MILLISECONDS);
			executor.shutdown();
			listener.onSucceed(t);
		} catch (InterruptedException e) {
			listener.onFailed("InterruptedException");
		} catch (ExecutionException e) {
			listener.onFailed("ExecutionException");

		} catch (TimeoutException e) {
			// TODO�� coding here...
			listener.onTimeOut("TimeoutException");

		}
	}
}