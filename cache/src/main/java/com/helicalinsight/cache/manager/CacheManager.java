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

package com.helicalinsight.cache.manager;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Somen
 *         Created  on 9/3/2015.
 */
public abstract class CacheManager {

    public abstract void setRequestData(String data);

    public abstract String getConnectionFilePath();

    public abstract Long getConnectionId();

    public abstract Integer getMapId();

    public abstract String getConnectionType(Long connectionId);

    public abstract String getQuery(String connectionType);

    public abstract JsonObject getDataFromDatabase(String query);

    public abstract String getDirectory();

    public abstract boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                               JsonObject object);
}
