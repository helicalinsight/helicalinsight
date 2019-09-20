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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by author on 20-Jan-15.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unchecked")
@Component
final class FrameworkFactory implements TypeAwareFactory, IClasspathExtensibleFactory {

    private static final Logger logger = LoggerFactory.getLogger(FrameworkFactory.class);

    @Autowired
    private StatelessObjectsFactory statelessObjectsFactory;

    @Autowired
    @Qualifier("factory")
    private Factory factory;

    @Autowired
    private ObjectStateChecker objectStateChecker;

    @Autowired
    private IFrameworkObjectsRegistry registry;

    @Autowired
    private ClassPathHacker classPathHacker;


    @Override
    public <T extends FrameworkObject> T getInstanceOfType(String clazz, Class<T> baseType) {
        if (clazz == null || baseType == null) {
            throw new UnSupportedObjectTypeException("The parameters clazz and/or baseType null");
        }

        T object;
        try {
            Class<?> requestedClass = this.classPathHacker.getClass(clazz);
            boolean assignable = (requestedClass != null) && (baseType.isAssignableFrom(requestedClass));
            if (!assignable) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("The requested class %s is not assignable to the baseType %s.", clazz,
                            baseType));
                }
                throw new FrameworkException("Requested class is not assignable to the Interface." + baseType.getName
                        ());
            } else {
                //Modified by author Rajasekhar on 14/07/2015
                //Fixed an issue of unnecessary object creation if it is already cached.
                if (this.registry != null && this.registry.contains(clazz)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Returning instance of the class %s for reuse" + ".", clazz));
                    }
                    object = (T) this.registry.get(clazz);
                } else {
                    object = getObject(clazz, (Class<? extends T>) requestedClass);
                }
            }

            if (object == null) {//Still object is null? Hmm. Throw exception.
                throw new FrameworkException("Unable to produce the requested class object.");
            }
            if (object.isThreadSafeToCache()) {
                if (this.registry != null && !this.registry.contains(clazz)) {//No need to cache if it already
                    //cached
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Caching instance of the class %s for reuse.", clazz));
                    }
                    this.registry.register(clazz, object);
                }
            }
        } catch (Exception exception) {
            if ((this.registry != null) && (this.registry.contains(clazz))) {
                this.registry.remove(clazz);
            }
            throw new FrameworkObjectInstantiationException(String.format("Could not create the instance of " +
                    "baseType %s", clazz), exception);
        }
        return object;
    }

    private <T extends FrameworkObject> T getObject(String clazz, Class<?
            extends T> requestedClassType) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        if (this.objectStateChecker.isStateful(requestedClassType)) {
            return (T) this.factory.createObject(clazz, requestedClassType);
        } else {
            return this.statelessObjectsFactory.get(clazz, requestedClassType);
        }
    }


    public Object getObject(String clazz) {
        try {
            Class<?> requestedClass = getClass(clazz);
            if (requestedClass != null) {
                return this.factory.createObject(clazz, requestedClass);
            } else {
                throw new FrameworkException("Could not load the class from external location.");
            }
        } catch (Exception exception) {
            throw new FrameworkException("Could not load the class from external location", exception);
        }
    }

    public Class<?> getClass(String clazz) throws ClassNotFoundException {
        if (clazz == null) {
            throw new IllegalArgumentException("The value of class is null.");
        }
        return this.classPathHacker.getClass(clazz);
    }
}