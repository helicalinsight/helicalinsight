package com.helicalinsight.parallelprocessor;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface TaskExecutorService {

	Future<?> submit(Runnable r, String requestId);
	Future<?> submit(Callable<?> c , String requestId);

	boolean cancelTaskByRequestId(String requestId);

	void cancelAllTasks();
	
	void deRegisterTask(String requestId);
	
	Thread createThread(Runnable runnable);
	
	Executor getThreadPoolExecutor();

}
