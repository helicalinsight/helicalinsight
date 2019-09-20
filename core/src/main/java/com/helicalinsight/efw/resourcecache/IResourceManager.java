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

package com.helicalinsight.efw.resourcecache;

import net.sf.json.JSONObject;

import java.util.Map;
import java.util.Properties;

public interface IResourceManager {

    int CACHE_MAX_SIZE = 1000;

    //Properties is in place if it may be used to increase or decrease the CACHE_MAX_SIZE
    Properties properties = new Properties();

    JSONObject getResource(String resource, boolean flag);

    void updateResource(String resource, boolean flag, JSONObject obj);

    boolean deleteResource(String key);

    boolean deleteAll();

    @SuppressWarnings("rawtypes")
    Map dump();

    void compressResource();

    Object deCompressResource();

    Integer getSize();
}
