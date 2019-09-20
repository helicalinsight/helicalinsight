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

package com.helicalinsight.efw.framework;

/**
 * Created by author on 21-01-2015.
 * <p/>
 * Parent interface of all reflection based configuration classes in the project.
 * <p/>
 * The framework cashes the object instance created in a ConcurrentHashMap. If the object created
 * has state, then the object reference is not stored in the map.
 *
 * @author Rajasekhar
 */
public interface FrameworkObject {

    /**
     * If the developer provides the return value as true the the framework will store the
     * instance in the cache. Else the created instance reference is not stored in the
     * ConcurrentHashMap and each time an object instance is needed, it will be created. Hence it
     * is advised carefully think and then provide the return value as it involves complex
     * multi-threading related issues.
     *
     * @return True if the object instance can be stored. Else false.
     */
    boolean isThreadSafeToCache();

}