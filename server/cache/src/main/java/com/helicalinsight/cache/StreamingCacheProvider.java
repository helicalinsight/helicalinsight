package com.helicalinsight.cache;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.concurrent.BoundedChannel;
import com.helicalinsight.concurrent.ChunkWriter;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.TaskExecutorService;

public class StreamingCacheProvider {

	private static final Logger logger = LoggerFactory.getLogger(StreamingCacheProvider.class);
	
	public String provide(String decodedQuery, String directory,  Cache requestCache, CacheManager cacheManager) throws SQLException, InterruptedException, IOException, TimeoutException {

		TaskExecutorService taskExecutorService = ApplicationContextAccessor.getBean(TaskExecutorService.class);
		
		int writers =  Math.min(CacheUtils.getNoOfWorkers(), Runtime.getRuntime().availableProcessors());
		CountDownLatch countDownLatch = new CountDownLatch(writers);

		// POISON PILL TO KILL ALL THE WORKERS THREADS
		CachedRowSet POISON = RowSetProvider.newFactory().createCachedRowSet();

		int queueCapacity =  CacheUtils.getQueueCapacity();
		queueCapacity = queueCapacity == 0 ? writers * 4 : queueCapacity;
		
		BoundedChannel<ResultSet> channel = new BoundedChannel<ResultSet>(queueCapacity, writers, POISON);
		AtomicInteger batchCounter = new AtomicInteger();
		
		 String cacheUUID = UUID.randomUUID().toString();
	     
		 if (directory == null) {
	           directory = "TEMP_DIRECTORY";
	     }
		
		String cacheDir =  directory + File.separator + cacheUUID;
		
		logger.debug("Cache directory : {}", cacheDir);
		
		String cacheAbsDir = CacheUtils.getCacheDirectory() + File.separator + cacheDir;
		File cacheDirFile = new File(cacheAbsDir);
		cacheDirFile.mkdirs();
		
		String requestId = RequestContext.get();
		logger.debug("Initializing ChunkWriter Pool of Size : {}", writers);
		List<Future<?>> futureList = new ArrayList<>();
		for (int i = 0; i < writers; i++) {
			Runnable runnable  = new ChunkWriter<ResultSet>(channel, batchCounter, cacheDirFile, POISON, countDownLatch);
			futureList.add(taskExecutorService.submit(runnable , requestId));
		}
		logger.debug("Request Id : {}",  requestId);
		
		Runnable producer = () -> {
			try {
				cacheManager.streamDataFromDatabase(decodedQuery,rs -> {
					try {
						channel.publish(rs);
					} catch (InterruptedException e) {
						logger.error("Error while publishing the batch",e);
						channel.clear();
						clearPartialData(cacheAbsDir);
						Thread.currentThread().interrupt();
					}
				});
				logger.debug("Published all the batches, killing the workers.");
			} catch (Exception e) {
				throw new EfwServiceException(e);
			}
			finally {
				for(int i=0; i<writers; i++) {
					try {
						channel.publish(POISON);
					}
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				
				try {
			        countDownLatch.await();
			    } catch (InterruptedException e) {
			        Thread.currentThread().interrupt();
			    }

			    try {
			        File completeFile = new File(cacheDirFile, ".cache_complete");
			        boolean created = completeFile.createNewFile();
			        logger.debug("Cache complete marker created: {}", created);
			    } catch (IOException e) {
			        throw new EfwServiceException(e);
			    }
			}
		};
		
		boolean isStream = CacheUtils.enableParallelReportGeneration();
			try {
				Future<?> resultSetProducerFuture = taskExecutorService.submit(producer, RequestContext.get());
				if (!isStream) {
					resultSetProducerFuture.get(JsonUtils.getThreadPoolTaskTimeOut(),TimeUnit.SECONDS);
				}
			}
			catch (ExecutionException | CancellationException | InterruptedException e) {
				logger.debug("Interruption occurred, terminating all the chunk writers - {}.", futureList.size());
				if (StringUtils.isNotBlank(cacheAbsDir)) {
					logger.debug("Interruption occurred, rolling back the changes.");
					clearPartialData(cacheAbsDir);
				}
				throw new EfwServiceException(e);
			}
			finally {
				if(!isStream) {
					for (Future<?> future : futureList) {
						 try {
							 future.get(JsonUtils.getThreadPoolTaskTimeOut(), TimeUnit.SECONDS);
						 }
						 catch (Exception ignored) {
						}
					 }
				}
			}
		return cacheDir;
	}
	
	private final void clearPartialData(String cacheAbsDir) {
		try {
			FileUtils.deleteDirectory(new File(cacheAbsDir));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
