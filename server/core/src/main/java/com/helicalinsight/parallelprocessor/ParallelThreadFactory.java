package com.helicalinsight.parallelprocessor;

import com.helicalinsight.parallelprocessor.api.GenericWorker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * @author Somen
 *         Created  on 6/12/2019.
 */
@Component
public class ParallelThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable runnable) {
  /*      GenericWorker genericWorker = (GenericWorker) runnable;
        String id = genericWorker.getId();
        ThreadGroup threadGroup = idThreadGroupMap.get(id);
        if (threadGroup == null) {
            threadGroup = new ThreadGroup(id);
        }
        Thread thread = new Thread(threadGroup, runnable);
        idThreadGroupMap.put(id,threadGroup);
        return thread;*/
        return  null;
    }
}
