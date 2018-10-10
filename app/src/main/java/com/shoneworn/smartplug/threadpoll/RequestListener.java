package com.shoneworn.smartplug.threadpoll;
/**
 * @author chenxiangxiang
 * @email  shoneworn@163.com
 */
public interface RequestListener<T> {
	
	void onSucceed(T t);
	void onFailed(String msg);
	void onTimeOut(String msg);

}
