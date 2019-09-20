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

package com.helicalinsight.cache.service;

import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;

/**
 * @author Somen
 */
public interface CacheService {
    Long addCache(Cache cache);

    void editCache(Cache cache);

    void deleteCache(Long cacheId);

    void deleteAllCache();

    Cache findCache(Long cacheId);

    Cache findUniqueCache(Cache sampleCache);

    void addReport(CacheReport report);
}
