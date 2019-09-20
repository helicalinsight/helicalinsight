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

package com.helicalinsight.efw.framework.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by author on 21-01-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("ALL")
public class ClassLoaderUtility {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtility.class);

    public static boolean isClassLoaded(String clazz) {
        Method findLoadedClassMethod = getMethod();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        debug(classLoader);
        if (check(clazz, findLoadedClassMethod, classLoader)) {
            return true;
        }
        classLoader = ClassLoaderUtility.class.getClassLoader();
        debug(classLoader);
        return check(clazz, findLoadedClassMethod, classLoader);
    }

    private static Method getMethod() {
        Method findLoadedClassMethod;
        try {
            findLoadedClassMethod = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("NoSuchMethodException", ex);
        }
        if (findLoadedClassMethod != null) {
            findLoadedClassMethod.setAccessible(true);
        } else {
            throw new RuntimeException("Instance of findLoadedClass could not be obtained.");
        }
        return findLoadedClassMethod;
    }

    private static void debug(ClassLoader classLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Using the class loader %s to inspect whether the class " + "is loaded.",
                    classLoader));
        }
    }

    private static boolean check(String clazz, Method findLoadedClassMethod,
                                 ClassLoader classLoader) {
        if (find(clazz, findLoadedClassMethod, classLoader)) {
            log(clazz, classLoader);
            return true;
        }
        return false;
    }

    private static boolean find(String clazz, Method findLoadedClassMethod, ClassLoader classLoader) {
        try {
            Object object = findLoadedClassMethod.invoke(classLoader, clazz);
            if (object != null) {
                return true;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not determine whether driver class is loaded", ex);
        }
        return false;
    }

    private static void log(String clazz, ClassLoader classLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The class loader %s is registered as the class loader " +
                    "for" + " the class %s", classLoader.getClass(), clazz));
        }
    }

    public static ClassLoader findClassLoader(String clazz) {
        Method findLoadedClassMethod = getMethod();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        if (find(clazz, findLoadedClassMethod, classLoader)) {
            return classLoader;
        }
        classLoader = ClassLoaderUtility.class.getClassLoader();
        if (find(clazz, findLoadedClassMethod, classLoader)) {
            return classLoader;
        } else {
            throw new RuntimeException(String.format("None of the class loaders were registered " + "as the class " +
                    "loader of the class %s", clazz));
        }
    }
}
