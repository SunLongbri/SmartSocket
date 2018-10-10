package com.shoneworn.smartplug.threadpoll;


public class DemoTest {
 
	public static void main(String[] args) throws InterruptedException {
 
		Runnable task1 = new RealTask("OK",listener);
		Runnable task2 = new RealTask("not OK",listener);
		Runnable task3 = new RealTask("not OK",listener);
		Runnable task4 = new RealTask("not OK",listener);
		Runnable task5 = new RealTask("OK",listener);
		Runnable task6 = new StringToIntTask("not OK",Integerlistener);
		Runnable task7 = new StringToIntTask("OK",Integerlistener);
		BaseTask task8 = new RealTask("not OK",listener);
		Runnable task9 = new RealTask("OK",listener);
 
		ThreadPool executor = ThreadPool.getInstance();
 
		executor.submit(task1);
		executor.submit(task2);
		executor.submit(task3);
		executor.submit(task4);
		executor.submit(task5);
		executor.submit(task6);
		executor.submit(task7);
		executor.submit(task8);
		executor.submit(task9);
		while (executor.getActiveCount() > 0) {
			System.out.println("活跃线程为:" + executor.getActiveCount());
			Thread.sleep(3000);
		}
		System.out.println("活跃线程为:" + executor.getActiveCount());
		executor.shutdown();
	}
 
	static RequestListener<String> listener = new RequestListener<String>() {
		
		public void onSucceed(String t) {
			System.out.println(t);
		}
		public void onFailed(String msg) {
			System.out.println(msg);

		}

		public void onTimeOut(String msg) {
			System.out.println(msg);

		}
	};
	
	static RequestListener<Integer> Integerlistener = new RequestListener<Integer>() {
		
		public void onTimeOut(String msg) {
			System.out.println(msg);

		}
		
		public void onSucceed(Integer t) {
			System.out.println(t);
		}
		
		public void onFailed(String msg) {
			System.out.println(msg);

		}
	};
	
}