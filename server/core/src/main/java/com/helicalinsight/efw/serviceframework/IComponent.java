package com.helicalinsight.efw.serviceframework;


import com.helicalinsight.callback.CallBack;
import com.helicalinsight.efw.framework.FrameworkObject;
import com.helicalinsight.stream.StreamSession;

import java.sql.ResultSet;
import java.util.Map;


/**
 * Created by author on 20-02-2015.
 *
 * @author Rajasekhar
 */
public interface IComponent extends FrameworkObject {

    String executeComponent(String jsonFormData);

    //noop
    //todo
    default Object componentLogic(String jsonFormData) {
        return null;
    }
    
    default void componentLogic(String jsonFormData, CallBack<ResultSet> callBack) {
    }

    default Map<String,Object> responseJSONMap(String jsonFormData){
       return null;
    }
    
    default void streamResponse(String formData, StreamSession session) {
    	// NOOP
    }
}
