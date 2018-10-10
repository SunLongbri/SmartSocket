package com.shoneworn.smartplug.threadpoll;

public class RealTask extends BaseTask<String, String>{
	private static final String CORRECT_KEY = "OK";

	public RealTask(String param, RequestListener<String> listener) {
		super(param, listener);
	}

	@Override
	public String runTask(String parma) {
		// TODO:真正的业务逻辑
				if (CORRECT_KEY.equals(parma)) {
					return "SUCCESS";
				} else {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return "FAIL";
				}
	}
	

}
