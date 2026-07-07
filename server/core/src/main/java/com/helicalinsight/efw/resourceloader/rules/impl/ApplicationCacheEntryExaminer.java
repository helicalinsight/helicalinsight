package com.helicalinsight.efw.resourceloader.rules.impl;

import com.helicalinsight.efw.resourceloader.rules.ApplicationCacheDumpRule;
import net.sf.ehcache.Ehcache;

/**
 * @author Rajesh
 *         Created by helical019 on 2/20/2019.
 */
@Deprecated
public class ApplicationCacheEntryExaminer implements ApplicationCacheDumpRule {

    @Override
    public void dumpCachedDataToDB(Object object) {
        Ehcache applicationCache = (Ehcache) object;
        long maxEntriesLocalHeap = applicationCache.getCacheConfiguration().getMaxEntriesLocalHeap();
        int cacheSize = applicationCache.getSize();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
}
