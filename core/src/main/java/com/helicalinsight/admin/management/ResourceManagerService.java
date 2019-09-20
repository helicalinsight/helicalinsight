/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.admin.management;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * created by Author on 13-05-15
 *
 * @author Somen
 *         This particular service class is used to manage the monitoring activity.
 */
public class ResourceManagerService implements IService {

    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        if (AuthenticationUtils.isSuperAdmin()) {
            return ServiceUtils.execute(type, serviceType, service, formData);
        } else {
            throw new AccessDeniedException("The service is not available to current user");
        }
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
