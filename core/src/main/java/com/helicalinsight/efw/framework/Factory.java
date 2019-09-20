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
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

/**
 * Created by author on 21-01-2015.
 *
 * @author Rajasekhar
 */
@Component
class Factory {

    private static final Logger logger = LoggerFactory.getLogger(Factory.class);


    Object createObject(String clazz, Class<?> requestedClassType) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Constructor<?> constructor = requestedClassType.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object type = constructor.newInstance();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Successfully created the object instance of type %s.", clazz));
        }
        return type;
    }
}
