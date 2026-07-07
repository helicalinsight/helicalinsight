package com.helicalinsight.parallelprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;

import jakarta.annotation.PreDestroy;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * @author Rajesh
 *         Created by helical019 on 1/17/2019.
 */
@Component
public class ThreadPoolTaskExecutorImpl extends ThreadPoolTaskExecutor implements TaskExecutorService {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTaskExecutorImpl.class);
	private Map<String, Set<Future<?>>> requestIdToFutureMap = new ConcurrentHashMap<>();
	private Map<Future<?>, String> futureToRequestMap = new ConcurrentHashMap<>();


    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        super.setMaxPoolSize(maxPoolSize);
    }
    

    @Override
    public void setCorePoolSize(int corePoolSize) {
        super.setCorePoolSize(corePoolSize);
    }

    @Override
    public void setThreadNamePrefix(String threadNamePrefix) {
        super.setThreadNamePrefix(threadNamePrefix);
    }

    @Override
    public void setWaitForTasksToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        super.setWaitForTasksToCompleteOnShutdown(waitForJobsToCompleteOnShutdown);
    }

    @Override
    public void setQueueCapacity(int queueCapacity) {
        super.setQueueCapacity(queueCapacity);
    }

    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        super.setRejectedExecutionHandler(rejectedExecutionHandler);
    }
    @Override
    public void execute(final Runnable r) {
        final Authentication a = SecurityContextHolder.getContext().getAuthentication();

        super.execute(getNewWrappedThread(r, a));
    }

    @Override
    public Future<?> submit(Runnable r) {
        final Authentication a = SecurityContextHolder.getContext().getAuthentication();

        return  super.submit(getNewWrappedThread(r, a));
    }
    
    
    public Future<?> submit(Runnable r , String requestId) {
    	
    	validateRequest(requestId);
    	
    	final Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	
        Runnable wrappedTask = () -> {
            try {
                r.run();
                if (Thread.currentThread().isInterrupted()) {
                	throw new InterruptedException("Task interrupted.");
                }
            }
            catch (InterruptedException e) {  
            	Thread.currentThread().interrupt();
            	logger.warn("Task interrupted for requestId : {}", requestId);
			}
            catch (Exception e) {
            	logger.error("Task execution failed for requestId : {}", requestId,e);
            	throw e;
            }
            
        };
    	
        Future<?> future =  super.submit(getNewWrappedThread(wrappedTask, a));
        futureToRequestMap.put(future, requestId);
        requestIdToFutureMap.computeIfAbsent(requestId, k -> ConcurrentHashMap.newKeySet())
        .add(future);
        return future;
    }
    
    public Future<?> submit(Callable<?> c , String requestId) {
    	
    	validateRequest(requestId);
    	
    	final Authentication a = SecurityContextHolder.getContext().getAuthentication();
 
    	Callable<?> wrappedTask = () -> {
            try {
                return c.call();
            } catch (InterruptedException e) {
                logger.warn("Task interrupted for requestId: {}", requestId);
                Thread.currentThread().interrupt();
                throw e;
            } 
        };
    	
    	Future<?> future =  super.submit(getNewWrappedCallable(wrappedTask, a));
    	futureToRequestMap.put(future, requestId);
        requestIdToFutureMap.computeIfAbsent(requestId, k -> ConcurrentHashMap.newKeySet())
        .add(future);
        return future;
    }

    public Runnable getNewWrappedThread(final Runnable r, final Authentication a) {
    	String requestId = RequestContext.get();
        return new Runnable() {
            public void run() {
                try {
                	RequestContext.set(requestId);
                    SecurityContext ctx = SecurityContextHolder.createEmptyContext();
                    ctx.setAuthentication(a);
                    SecurityContextHolder.setContext(ctx);

                    r.run();
                } finally {
                    SecurityContextHolder.clearContext();
                    RequestContext.clear();
                }
            }
        };
    }
    
    
	public <T> Callable<T> getNewWrappedCallable(final Callable<T> c, final Authentication a) {

		String requestId = RequestContext.get();

		return new Callable<T>() {
			@Override
			public T call() throws Exception {
				try {
					RequestContext.set(requestId);
					SecurityContext ctx = SecurityContextHolder.createEmptyContext();
					ctx.setAuthentication(a);
					SecurityContextHolder.setContext(ctx);
					return c.call();
				} finally {
					SecurityContextHolder.clearContext();
					RequestContext.clear();
				}
			}
		};
	}

	@Override
	public boolean cancelTaskByRequestId(String requestId) {
		
		Set<Future<?>> futureList =  requestIdToFutureMap.get(requestId);
		
		if (futureList == null || futureList.isEmpty()) {
			if (requestId != null && RequestRegistryFilter.cancelledRequests.contains(requestId)) {
				logger.info("No active task for requestId {} yet; cancellation already registered", requestId);
				return true;
			}
			logger.warn("No active task found for requestId : {}", requestId);
			throw new EfwServiceException("No active task found for requestId : " +  requestId);
		}
		
		logger.warn("Cancelling {} task(s) for requestId : {}", futureList.size(), requestId);
		
		futureList.forEach(future -> {
			if (!future.isDone()) {
				future.cancel(true);
			}
		});
		
		deRegisterTask(requestId);
		
		return true;
	}

	@Override
	@PreDestroy
	public void cancelAllTasks() {
		logger.debug("Cancelling all the tasks.");
		requestIdToFutureMap.keySet().forEach(requestId -> {
			cancelTaskByRequestId(requestId);
		});
		
	}

	@Override
	public void deRegisterTask(String requestId) {
		logger.debug("Deregistering future : {}", requestId);
		requestIdToFutureMap.remove(requestId);
	}
	
	private void validateRequest(String requestId) {
		if(requestId != null &&  RequestRegistryFilter.cancelledRequests.contains(requestId)) {
			RequestRegistryFilter.cancelledRequests.remove(requestId);
			//TODO : Create custom exception -> AbortException
			throw new EfwServiceException("Request has already been cancelled.");
		}
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
	    super.afterExecute(r, t);

	    if (r instanceof Future<?>) {
	        Future<?> future = (Future<?>) r;
	        String requestId = futureToRequestMap.remove(future);
	        if (requestId != null) {
	            Set<Future<?>> futures = requestIdToFutureMap.get(requestId);
	            if (futures != null) {
	                futures.remove(future);
	                if (futures.isEmpty()) {
	                    requestIdToFutureMap.remove(requestId);
	                    logger.debug("Cleaned up completed tasks for requestId : {}", requestId);
	                }
	            }
	        }
	    }
	}
	
	@Override
	public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
		super.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
	}
	
	@Override
	public void setKeepAliveSeconds(int keepAliveSeconds) {
		super.setKeepAliveSeconds(keepAliveSeconds);
	}

}
