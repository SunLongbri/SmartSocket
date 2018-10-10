package com.shoneworn.smartplug.threadpoll;

import java.util.concurrent.Callable;
/**
 * @author chenxiangxiang
 * @email  shoneworn@163.com
 * @param <P> 泛型，这里代指入参类型 类型可以为基本类型，也可以为bean等
 * @param <V> 泛型，这里代指出参类型 类型可以为基本类型，也可以为bean等
 * 出参数据通过RequestListener.onSucceed()体现出来
 */
public abstract class BaseCall<P,V> implements Callable<V> {
 
	private P parma;
	public BaseCall(P parma) {
		this.parma = parma;
	}
 
	public V call() {
		// 真正的业务处理代码，这里交给子类去实现并处理

		return makeCall(parma);
	}
	
	public abstract V makeCall(P parma);
 

}