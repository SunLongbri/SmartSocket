package com.shoneworn.smartplug.threadpoll;
/**
 * @author chenxiangxiang
 * @email  shoneworn@163.com
 * @param <P> 泛型，这里代指入参类型 类型可以为基本类型，也可以为bean等
 * @param <V> 泛型，这里代指出参类型 类型可以为基本类型，也可以为bean等
 * 出参数据通过RequestListener.onSucceed()体现出来
 */
public abstract class BaseTask<P, V> implements Runnable {
	 
	private P param;
	private RequestListener<V> listener;
 
	public void run() {
		 ControlTimeOut.call(new BaseCall<P, V>(param) {

			@Override
			public V makeCall(P parma) {
				return runTask(param);
			}
		},listener);
		
	}
	
	public abstract V runTask(P parma);
	
 
	public BaseTask(P param,RequestListener<V> listener) {
		super();
		this.param = param;
		this.listener = listener;
	}
 
}