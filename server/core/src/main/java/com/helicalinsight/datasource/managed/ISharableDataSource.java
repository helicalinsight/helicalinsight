package com.helicalinsight.datasource.managed;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by author on 02-Jan-15.
 *
 * @author Rajasekhar
 */
interface ISharableDataSource {
    @Nullable
    javax.sql.DataSource shared(HashMapKey hashMapKey, Map<HashMapKey, javax.sql.DataSource> poolingReferences);
}
