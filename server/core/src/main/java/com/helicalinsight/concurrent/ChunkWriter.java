package com.helicalinsight.concurrent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.efw.exceptions.EfwServiceException;


public class ChunkWriter<T> implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(ChunkWriter.class);

	private final BoundedChannel<T> channel;
	private final AtomicInteger batchCounter;
	private final File outputDir;
	private final T poisonPill;
	private final CountDownLatch countDownLatch;
	
	public ChunkWriter(BoundedChannel<T> channel, AtomicInteger batchCounter, File outputDir, 
			T poisonPill, CountDownLatch countDownLatch) {
		this.channel = channel;
		this.batchCounter = batchCounter;
		this.outputDir = outputDir;
		this.poisonPill = poisonPill;
		this.countDownLatch = countDownLatch;
	}
	
	
	@Override
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				T batch = channel.consume();
				if ( batch == poisonPill ) {
					logger.debug("{} Dumped all the batches", Thread.currentThread());
					break;
				}
				int current = batchCounter.getAndIncrement();
				File finalFile = new File(outputDir, "chunk_" + current + ".cache");
				File tempFile  = new File(outputDir, "tmp_" + current + ".cache");

				write(batch, tempFile);
				
				try {
	                java.nio.file.Files.move(
	                    tempFile.toPath(),
	                    finalFile.toPath(),
	                    java.nio.file.StandardCopyOption.ATOMIC_MOVE,
	                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
	                );
	            } catch (java.io.IOException ex) {
	                logger.debug("Atomic move not supported, falling back to rename: {}", tempFile);
	                if (!tempFile.renameTo(finalFile)) {
	                    throw new EfwServiceException("Failed to rename temp file to final file: " + finalFile, ex);
	                }
	            }
			}
		}
		catch (InterruptedException | CancellationException e) {
			Thread.currentThread().interrupt();
		}
		catch (Exception e) {
			throw new EfwServiceException(e);
		}
		finally {
			countDownLatch.countDown();
	        logger.debug("Writer finished. Remaining: {}", countDownLatch.getCount());
		}
	}
	
	private void write(T batch, File file) throws IOException {

	    try (FileOutputStream fos = new FileOutputStream(file);
	         BufferedOutputStream bos = new BufferedOutputStream(fos);
	         ObjectOutputStream oos = new ObjectOutputStream(bos)) {

	        oos.writeUnshared(batch);
	        oos.flush();
	        bos.flush();
	        fos.getFD().sync();

	    } catch (IOException e) {
	        throw e;
	    }
	}
}
