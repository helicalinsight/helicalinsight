package com.helicalinsight.parallelprocessor;

import com.helicalinsight.parallelprocessor.api.GenericWorker;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Somen
 *         helical021
 */
@Component
public class ServiceThreadDetail {
    public Map<String, List<Thread>> getIdThreadGroupMap() {
        return idThreadGroupMap;
    }

    public void setIdThreadGroupMap(Map<String, List<Thread>> idThreadGroupMap) {
        this.idThreadGroupMap = idThreadGroupMap;
    }

    private Map<String, List<Thread>> idThreadGroupMap = new HashMap<>();

    public void putInMap(String id, Thread thread) {
        List<Thread> threads = idThreadGroupMap.get(id);
        if (threads == null) {
            threads = new ArrayList<>();
        }
        threads.add(thread);
        idThreadGroupMap.put(id,threads);
    }


    public boolean stopThreadsWithId(String serviceId){
        boolean inThreadGroup = this.idThreadGroupMap.containsKey(serviceId);
        if(inThreadGroup){
            List<Thread> threadGroup = this.idThreadGroupMap.get(serviceId);
            if(threadGroup!=null){
                for(Thread item:threadGroup){
                    item.interrupt();
                }
                return true;
            }
            this.idThreadGroupMap.remove(serviceId);
        }
        return false;
    }
}
