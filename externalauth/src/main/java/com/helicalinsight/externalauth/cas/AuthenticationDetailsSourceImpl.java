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

package com.helicalinsight.externalauth.cas;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;


public class AuthenticationDetailsSourceImpl implements AuthenticationDetailsSource<Object, Object> {

    private Class<?> clazz = AuthenticationDetails.class;


    public Object buildDetails(Object context) {
        Object result = null;
        try {
            Constructor<?> constructor = getFirstMatchingConstructor(context);
            result = constructor.newInstance(context);
        } catch (Exception ex) {
            ReflectionUtils.handleReflectionException(ex);
        }

        return result;
    }

    
    private Constructor<?> getFirstMatchingConstructor(Object object) throws NoSuchMethodException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> constructor = null;
        for (Constructor<?> tryMe : constructors) {
            Class<?>[] parameterTypes = tryMe.getParameterTypes();
            if (parameterTypes.length == 1 && (object == null || parameterTypes[0].isInstance(object))) {
                constructor = tryMe;
                break;
            }
        }

        if (constructor == null) {
            if (object == null) {
                throw new NoSuchMethodException("No constructor found that can take a single argument");
            } else {
                throw new NoSuchMethodException("No constructor found that can take a single argument of type " +
                        object.getClass());
            }
        }
        return constructor;
    }

    public void setClazz(Class<?> clazz) {
        Assert.notNull(clazz, "Class required");
        this.clazz = clazz;
    }
}
