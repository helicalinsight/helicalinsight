package com.helicalinsight.datasource.managed;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @Nullable
    @Override
    public javax.sql.DataSource shared(@NotNull HashMapKey hashMapKey, @NotNull Map<HashMapKey,
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
