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

package com.helicalinsight.efw.framework;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by author on 18-Jan-15.
 *
 * @author Rajasekhar
 */
enum FrameworkObjectsRegistry implements IFrameworkObjectsRegistry {

    FRAMEWORK_OBJECTS_REGISTRY;

    private static final Logger logger = LoggerFactory.getLogger(FrameworkObjectsRegistry.class);

    private static final ConcurrentHashMap<String, Object> instances;

    static {//Put as static block as Intellij was formatting it badly causing compilation error.
        instances = new ConcurrentHashMap<>(16, 0.9f, 1);
    }

    FrameworkObjectsRegistry() {
    }

    @Override
    public Object get(String className) {
        check(className);
        return instances.get(className);
    }

    private void check(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("The object is null.");
        }
    }

    @Override
    public long getCount() {
        return instances.size();
    }

    @Override
    public List<Object> list() {
        return new ArrayList<>(instances.values());
    }

    @Override
    public void remove(String className) {
        check(className);
        synchronized (this) {
            Object value = instances.get(className);
            //Ignore the returned value
            boolean removed = instances.remove(className, value);
            if (logger.isDebugEnabled()) {
                if (removed) {
                    logger.debug(String.format("Registry Info: The object %s has been " + "successfully removed.",
                            className));
                }
            }
        }
    }

    @Override
    public boolean contains(String className) {
        check(className);
        synchronized (this) {
            return instances.containsKey(className);
        }
    }

    @Override
    public void register(String className, Object instance) {
        check(className);
        check(instance);
        //Thread safe. Ignore the returned value
        instances.putIfAbsent(className, instance);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Registry Info: The object %s has been successfully " + "registered.",
                    className));
        }
    }
}
