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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
import com.helicalinsight.datasource.StreamingCacheMarkers;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.TaskExecutorService;

public class StreamingCacheProvider {

	private static final Logger logger = LoggerFactory.getLogger(StreamingCacheProvider.class);

	public String provide(String decodedQuery, String directory, Cache requestCache, CacheManager cacheManager)
			throws SQLException, InterruptedException, IOException, TimeoutException {

		TaskExecutorService taskExecutorService = ApplicationContextAccessor.getBean(TaskExecutorService.class);

		int writers = Math.min(CacheUtils.getNoOfWorkers(), Runtime.getRuntime().availableProcessors());
		CountDownLatch countDownLatch = new CountDownLatch(writers);

		CachedRowSet POISON = RowSetProvider.newFactory().createCachedRowSet();

		int queueCapacity = CacheUtils.getQueueCapacity();
		queueCapacity = queueCapacity == 0 ? writers * 4 : queueCapacity;

		BoundedChannel<ResultSet> channel = new BoundedChannel<ResultSet>(queueCapacity, writers, POISON);
		AtomicInteger batchCounter = new AtomicInteger();

		String cacheUUID = UUID.randomUUID().toString();

		if (directory == null) {
			directory = "TEMP_DIRECTORY";
		}

		String cacheDir = directory + File.separator + cacheUUID;

		logger.debug("Cache directory : {}", cacheDir);

		String cacheAbsDir = CacheUtils.getCacheDirectory() + File.separator + cacheDir;
		File cacheDirFile = new File(cacheAbsDir);
		cacheDirFile.mkdirs();

		String requestId = RequestContext.get();
		logger.debug("Initializing ChunkWriter Pool of Size : {}", writers);
		List<Future<?>> futureList = new ArrayList<>();
		for (int i = 0; i < writers; i++) {
			Runnable runnable = new ChunkWriter<ResultSet>(channel, batchCounter, cacheDirFile, POISON, countDownLatch);
			futureList.add(taskExecutorService.submit(runnable, requestId));
		}
		logger.debug("Request Id : {}", requestId);

		AtomicBoolean producerSucceeded = new AtomicBoolean(false);
		AtomicReference<Throwable> producerFailure = new AtomicReference<>();

		Runnable producer = () -> {
			try {
				cacheManager.streamDataFromDatabase(decodedQuery, rs -> {
					try {
						channel.publish(rs);
					} catch (InterruptedException e) {
						logger.error("Error while publishing the batch", e);
						channel.clear();
						clearPartialData(cacheAbsDir);
						Thread.currentThread().interrupt();
					}
				});
				producerSucceeded.set(true);
				logger.debug("Published all the batches, killing the workers.");
			} catch (Exception e) {
				producerFailure.set(e);
				logger.error("Streaming cache producer failed", e);
			} finally {
				for (int i = 0; i < writers; i++) {
					try {
						channel.publish(POISON);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}

				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

				try {
					if (producerSucceeded.get()) {
						File completeFile = StreamingCacheMarkers.completeFile(cacheDirFile);
						boolean created = completeFile.createNewFile();
						logger.debug("Cache complete marker created: {}", created);
					} else {
						clearChunkFiles(cacheDirFile);
						Throwable failure = producerFailure.get();
						if (failure == null) {
							failure = new EfwServiceException("Streaming cache producer failed");
							producerFailure.set(failure);
						}
						StreamingCacheMarkers.writeError(cacheDirFile, failure);
						logger.debug("Cache error marker written for directory {}", cacheAbsDir);
					}
				} catch (IOException e) {
					producerFailure.compareAndSet(null, e);
					throw new EfwServiceException(e);
				}
			}

			Throwable failure = producerFailure.get();
			if (failure != null) {
				throw new EfwServiceException(failure);
			}
		};

		boolean isStream = CacheUtils.enableParallelReportGeneration();
		try {
			Future<?> resultSetProducerFuture = taskExecutorService.submit(producer, RequestContext.get());
			// Always await so failures keep the live JDBC cause chain for ExceptionUtils.getRootCauseMessage
			resultSetProducerFuture.get(JsonUtils.getThreadPoolTaskTimeOut(), TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			logger.debug("Producer failed, terminating chunk writers - {}.", futureList.size());
			if (StringUtils.isNotBlank(cacheAbsDir)) {
				clearPartialData(cacheAbsDir);
			}
			Throwable cause = e.getCause() != null ? e.getCause() : e;
			if (cause instanceof EfwServiceException) {
				throw (EfwServiceException) cause;
			}
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new EfwServiceException(cause);
		} catch (CancellationException | InterruptedException e) {
			logger.debug("Interruption occurred, terminating all the chunk writers - {}.", futureList.size());
			if (StringUtils.isNotBlank(cacheAbsDir)) {
				logger.debug("Interruption occurred, rolling back the changes.");
				clearPartialData(cacheAbsDir);
			}
			throw new EfwServiceException(e);
		} finally {
			if (!isStream) {
				for (Future<?> future : futureList) {
					try {
						future.get(JsonUtils.getThreadPoolTaskTimeOut(), TimeUnit.SECONDS);
					} catch (Exception ignored) {
					}
				}
			}
		}
		return cacheDir;
	}

	private final void clearPartialData(String cacheAbsDir) {
		try {
			FileUtils.deleteDirectory(new File(cacheAbsDir));
		} catch (Exception ex) {
			logger.warn("Failed to clear partial streaming cache at {}", cacheAbsDir, ex);
		}
	}

	private static void clearChunkFiles(File cacheDirFile) {
		if (cacheDirFile == null || !cacheDirFile.isDirectory()) {
			return;
		}
		File[] chunks = cacheDirFile.listFiles(
				f -> f.isFile() && (f.getName().startsWith("chunk_") || f.getName().startsWith("tmp_")));
		if (chunks == null) {
			return;
		}
		for (File chunk : chunks) {
			if (!chunk.delete()) {
				logger.debug("Unable to delete partial chunk {}", chunk);
			}
		}
	}
}
