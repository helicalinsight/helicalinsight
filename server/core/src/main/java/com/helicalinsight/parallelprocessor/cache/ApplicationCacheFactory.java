package com.helicalinsight.parallelprocessor.cache;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * @author
 * Created by helical021 on 1/31/2019.
 */
public class ApplicationCacheFactory {

    public static ICache getCacheManager(String name){
        return  (ICache) ApplicationContextAccessor.getBean(name);
    }

}
