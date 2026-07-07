package com.helicalinsight.adhoc.services;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * Created by author on 20-03-2015.
 *
 * @author Rajasekhar
 */
public class FunctionsListService implements IService {

    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        return ServiceUtils.executeService(type, serviceType, service, formData);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}