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

package com.helicalinsight.datasource;


/**
 * Created by author on 17-Jan-15.
 *
 * @author Rajasekhar
 */
final class Efwd {

    private final int globalId;
    private final String serviceType;

    Efwd(int globalId, String serviceType) {
        this.globalId = globalId;
        this.serviceType = serviceType;
    }

    public int getGlobalId() {
        return globalId;
    }

    public String getServiceType() {
        return serviceType;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Efwd efwd = (Efwd) object;

        return globalId == efwd.globalId && !(serviceType != null ? !serviceType.equals(efwd.serviceType) : efwd
                .serviceType != null);
    }

    @Override
    public int hashCode() {
        int result = globalId;
        result = 31 * result + (serviceType != null ? serviceType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Efwd{" +
                "globalId=" + globalId +
                ", serviceType='" + serviceType + '\'' +
                '}';
    }
}
