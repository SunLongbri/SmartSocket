package com.shoneworn.smartplug.threadpoll;

public class StringToIntTask extends BaseTask<String, Integer>{

	public StringToIntTask(String param, RequestListener<Integer> listener) {
		super(param, listener);
	}

	@Override
	public Integer runTask(String parma) {
		if(parma.equals("OK")) {
			return 100000;
		}
		return 111111;
	}

}
