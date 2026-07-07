package com.helicalinsight.adhoc.jreport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.stream.StreamEvent;
import com.helicalinsight.stream.StreamSession;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.FillListener;

public class ReportFillListener implements FillListener {
	
	private final Logger logger = LoggerFactory.getLogger(ReportFillListener.class);
	
	private final AtomicInteger pageCounter;
	private final JsonObject formData;
	private final HCRHelper hcrHelper;
	private final StreamSession session;
	private AtomicBoolean isFirstEvent = new AtomicBoolean(true);
	
    private final int batchSize = JsonUtils.getResponseEventBatchSize();
    private final AtomicInteger lastBatch = new AtomicInteger(0);
    
	private final List<Integer> buffer = Collections.synchronizedList(new ArrayList<>());
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private final String updateSendStrategy = JsonUtils.getSseUpdateBatchStrategy();
	private final int batchFreqTimeInMillis = JsonUtils.getSseUpdateBufferTime();
	
	
	public ReportFillListener(StreamSession session, AtomicInteger pageCounter, JsonObject formData, HCRHelper hcrHelper) {
		this.session = session;
		this.pageCounter = pageCounter;
		this.formData = formData;
		this.hcrHelper = hcrHelper;
		if("batch".equals(updateSendStrategy)) {
			startBatchSender();
		}
	}

	@Override
	public void pageGenerated(JasperPrint jasperPrint, int pageIndex) {
			logger.debug("Page generated : {}", pageIndex);
			int currentCount = pageCounter.incrementAndGet();
			processEvent(jasperPrint, pageIndex, currentCount, false);
	}
	
	@Override
	public void pageUpdated(JasperPrint jasperPrint, int pageIndex) {
			logger.debug("Page updated : {}", pageIndex);
			int currentCount = pageCounter.get();
			processEvent(jasperPrint, pageIndex, currentCount, true);
	}
	
	public final void processEvent(JasperPrint jasperPrint, int pageIndex, int currentCount, boolean isUpdate) {
		long start = 0;
		try {
			if(pageIndex<0) pageIndex=0;
			start = System.currentTimeMillis();
			if(isFirstEvent.get()) {
				formData.addProperty("page", pageIndex);
				JsonObject response = hcrHelper.provideResponseUsingPrint(formData, jasperPrint,currentCount);
				response.addProperty("designCacheKey", GsonUtility.optStringValue(formData, "designCacheKey", ""));
				sendEvent("page_"+pageIndex, response.toString());
				isFirstEvent.set(false);
				lastBatch.set(currentCount);
			}
			else {
				int lastSent = lastBatch.get();
				if (isUpdate) {
					processUpdate(pageIndex, currentCount);
				}
				else if ((currentCount - lastSent) >= batchSize) {
					JsonObject response = new JsonObject();
					response.addProperty("totalPageCount", currentCount);
					sendEvent("page_"+pageIndex, response.toString());
					lastBatch.set(currentCount);
				}
			}
			
		} catch (Exception e) {
			logger.error("Error while streaming page {} for request {}", pageIndex, e);
			session.error(e);
		} finally {
			double totalTime = (System.currentTimeMillis() - start) / 1000.0;
			logger.debug("Time taken to process event : " + totalTime + " s.");
		}
	}
	
	
	public void flushRemaining(int pageIndex, int currentCount) {
		flushBuffer();
	    int lastSent = lastBatch.get();
	    if (currentCount > lastSent) {
	        JsonObject response = new JsonObject();
	        response.addProperty("totalPageCount", currentCount);
	        sendEvent("page_"+pageIndex, response.toString());
	        lastBatch.set(currentCount);
	    }
	}
	public void startBatchSender() {
	    scheduler.scheduleAtFixedRate(this::flushBuffer, 0, batchFreqTimeInMillis, TimeUnit.MILLISECONDS);
	}

	private void processUpdate(int pageIndex, int currentCount) {
		buffer.add(pageIndex);
	}
	
	private void flushBuffer() {
	    List<Integer> batch;
	    synchronized (buffer) {
	        if (buffer.isEmpty()) return;
	        batch = new ArrayList<>(buffer);
	        buffer.clear();
	    }
	    if ( batch != null || batch.isEmpty()) {
	    	sendEvent("update", batch.toString());
	    }
	}
	
	public void stopBatchSender() {
	    scheduler.shutdownNow();
	}
	
	private final void sendEvent(String name, String data) {
		session.send(new StreamEvent(name, data));
	}
}
