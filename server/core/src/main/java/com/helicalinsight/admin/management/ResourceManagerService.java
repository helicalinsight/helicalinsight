package com.helicalinsight.admin.management;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * created by Author on 13-05-15
 *
 * @author Somen
 *         This particular service class is used to manage the monitoring activity. The component calls by this
 *         service is
 *         very sepcific to the null Organizaion.
 */
public class ResourceManagerService implements IService {

    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        AuthenticationUtils.allowSuperAdminOnly();
        return ServiceUtils.execute(type, serviceType, service, formData);
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
