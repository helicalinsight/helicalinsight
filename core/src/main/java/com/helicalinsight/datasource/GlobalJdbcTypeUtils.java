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
 * Created by user on 11/27/2015.
 *
 * @author Rajasekhar
 */
public class GlobalJdbcTypeUtils {

    public static boolean isTypeGlobal(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is null");
        }

        return (type.equalsIgnoreCase(GlobalJdbcType.TYPE) || type.equalsIgnoreCase(GlobalJdbcType
                .DYNAMIC_DATASOURCE) || type.equalsIgnoreCase(GlobalJdbcType.NON_POOLED) || type.equalsIgnoreCase
                (GlobalJdbcType.STATIC_DATASOURCE));
    }
}
