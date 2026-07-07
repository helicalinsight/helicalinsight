package com.helicalinsight.admin.management;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.parallelprocessor.TaskExecutorService;

/**
 * Created by author on 12/3/2019.
 *
 * @author Rajesh
 */
@Component
public class JasperMultiReportManager {

	private static final Logger logger = LoggerFactory.getLogger(JasperMultiReportManager.class);
	
    @Autowired
    private TaskExecutorService taskExecutorService;

    public Future<?> registerAndExecute(CancellableRunnable runnable, String requestId) {
        HIJasperThread hiJasperThread = new HIJasperThread(runnable);
        Future<?> future = taskExecutorService.submit(hiJasperThread, requestId);
        logger.debug("Registered HCR  request {}", requestId);
        return future;
    }

}
