package com.helicalinsight.efw.framework;

/**
 * Created by author on 21-01-2015.
 * <p/>
 * Parent interface of all reflection based configuration classes in the project(starting from adhoc
 * reporting feature).
 * <p/>
 * The framework cashes the object instance created in a ConcurrentHashMap. If the object created
 * has state, then the object reference is not stored in the map.
 *
 * @author Rajasekhar
 * @since 1.3
 */
public interface FrameworkObject {

    /**
     * If the developer provides the return value as true the the framework will store the
     * instance in the cache. Else the created instance reference is not stored in the
     * ConcurrentHashMap and each time an object instance is needed, it will be created. Hence it
     * is advised carefully think and then provide the return value as it involves complex
     * multi-threading related issues.
     *
     * @return True if the object instance can be stored. Else false.
     */
    boolean isThreadSafeToCache();

}