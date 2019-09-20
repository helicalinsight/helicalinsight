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

package com.helicalinsight.export;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * Created by author on 07-Dec-16.
 *
 * @author Somen
 */

public class ExportService implements IService {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        return ServiceUtils.execute(type, serviceType, service, formData);
    }
}
