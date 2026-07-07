package com.helicalinsight.parallelprocessor.cache;

import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen
 * Created by helical019 on 2/15/2019.
 */
public class ConnectionCacheUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionCacheUtils.class);


    public static void deleteConnectionCache(DataSourceMapping dataSourceMapping) {
        DatabaseCacheService databaseCacheService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);
        databaseCacheService.deleteAllCacheWithId(dataSourceMapping);

    }

    public static boolean detectCacheByServiceIdMap(DataSourceMapping dataSourceMapping) {
        DatabaseCacheService databaseCacheService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);
        return databaseCacheService.isDatabaseCachedFully(dataSourceMapping);
    }

}
