package com.helicalinsight.parallelprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Rajesh
 *         Created by helical019 on 1/21/2019.
 */
@Component
public class WorkerRejectionExecutionHandler implements RejectedExecutionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WorkerRejectionExecutionHandler.class);

    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {

        logger.debug(runnable.toString() + " : has been rejected");
    }
}
