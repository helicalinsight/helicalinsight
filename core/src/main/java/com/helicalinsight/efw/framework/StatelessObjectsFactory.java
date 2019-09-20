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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by author on 18-01-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unchecked")
@Component
final class StatelessObjectsFactory extends Factory {

    private static final Logger logger = LoggerFactory.getLogger(StatelessObjectsFactory.class);

    @Autowired
    private IFrameworkObjectsRegistry registry;

    public <T extends FrameworkObject> T get(String clazz, Class<? extends T> requestedClassType) {
        synchronized (this) {
            try {
                return newInstance(clazz, requestedClassType);
            } catch (NoSuchMethodException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("The method getInstance is not present in the " + clazz);
                }
                return create(clazz, requestedClassType);
            } catch (NullPointerException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Probably the method getInstance is not static in the class " + clazz);
                }
                return create(clazz, requestedClassType);
            } catch (Exception ex) {
                throw new FrameworkObjectInstantiationException("Could not create the framework object instance.", ex);
            }
        }
    }


    private <T extends FrameworkObject> T newInstance(String clazz, Class<? extends T> requestedClassType)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (registry.contains(clazz)) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Found a cached instance of the class %s. Returning " + "the same.", clazz));
            }
            return (T) registry.get(clazz);
        } else {
            return getSupposedlySingletonObjectUsingGetInstance(clazz, requestedClassType);
        }
    }

    private <T extends FrameworkObject> T create(String clazz, Class<? extends T> requestedClassType) {
        T instance;
        try {
            instance = (T) createObject(clazz, requestedClassType);
            registry.register(clazz, instance);
        } catch (Exception ex) {
            throw new FrameworkObjectInstantiationException("Could not create the instance.", ex);
        }
        return instance;
    }


    private <T extends FrameworkObject> T getSupposedlySingletonObjectUsingGetInstance(String clazz, Class<?
            extends
            T> requestedClassType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getInstanceMethod = requestedClassType.getMethod("getInstance");
        Object instance = getInstanceMethod.invoke(null);
        registry.register(clazz, instance);
        return (T) instance;
    }
}