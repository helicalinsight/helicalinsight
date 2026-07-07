package com.helicalinsight.efw.serviceframework;

import com.helicalinsight.efw.framework.FrameworkObject;
import com.helicalinsight.stream.StreamSession;

/**
 * Created by author on 24-Jan-15.
 *
 * @author Rajasekhar
 */
public interface IService extends FrameworkObject {

    String doService(String type, String serviceType, String service, String formData);

    default Object executeService(String type, String serviceType, String service, String formData){
        return  null;
    }
    
    default void streamResponse(String type, String serviceType, String service, String formData, StreamSession session) {
    }

}
