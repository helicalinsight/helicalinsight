package com.helicalinsight.efw.serviceframework;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.helicalinsight.stream.StreamSession;

/**
 * Created by author on 08-09-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class GenericServiceImplementation implements IService {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        return ServiceUtils.execute(type, serviceType, service, formData);
    }
    
    @Override
    public void streamResponse(String type, String serviceType, String service, String formData, StreamSession session) {
    	ServiceUtils.stream(type, serviceType, service, formData, session);
    }
    
}
