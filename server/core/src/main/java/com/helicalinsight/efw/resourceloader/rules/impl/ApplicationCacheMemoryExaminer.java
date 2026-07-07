package com.helicalinsight.efw.resourceloader.rules.impl;

import com.helicalinsight.efw.resourceloader.rules.ApplicationCacheDumpRule;

/**
 * @author Rajesh
 *         Created by helical019 on 2/20/2019.
 */
@Deprecated
public class ApplicationCacheMemoryExaminer implements ApplicationCacheDumpRule {

    @Override
    public void dumpCachedDataToDB(Object object) {

    }

    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
}
