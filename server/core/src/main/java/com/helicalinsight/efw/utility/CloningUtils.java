package com.helicalinsight.efw.utility;

import com.google.gson.Gson;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * Created by helical021 on 7/6/2020.
 */
public class CloningUtils {
    @SuppressWarnings("unchecked")
    public <T> T cloneObject(T t) {
        //The jsonobject is not serialized hence the approach is to clone from apache common when the object
        // is serialized.
        if (t instanceof Serializable) {
            return (T) SerializationUtils.clone((java.io.Serializable) t);
        }

        if (t instanceof Gson) {
            return t;
        }
        return t;

    }
}
