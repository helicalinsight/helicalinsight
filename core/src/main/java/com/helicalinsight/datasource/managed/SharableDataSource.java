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

package com.helicalinsight.datasource.managed;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Created by author on 02-Jan-15.
 *
 * @author Rajasekhar
 */
@Component
class SharableDataSource implements ISharableDataSource {

    @Override
    public javax.sql.DataSource shared(HashMapKey hashMapKey, Map<HashMapKey,
            javax.sql.DataSource> pooledReferences) {
        //Get the cashed references
        //Take a key. inspect the provider, jdbcUrl. If they are same return the value
        Set<Map.Entry<HashMapKey, javax.sql.DataSource>> entries = pooledReferences.entrySet();
        for (Map.Entry<HashMapKey, javax.sql.DataSource> entry : entries) {
            HashMapKey mapKey = entry.getKey();
            if (mapKey.equals(hashMapKey)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
