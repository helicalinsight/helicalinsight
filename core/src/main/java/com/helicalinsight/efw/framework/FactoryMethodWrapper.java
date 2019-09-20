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

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;


/**
 * A Useful Wrapper class for the reflection API usage. getTypedInstance method will cache the objects if the object
 * does not have any properites except the public static final field Logger. For an object to be cached,
 * it should not have any state. So, to check whether an object has state or not Class ObjectStateChecker is used.
 * <p/>
 * Except a supported set of loggers as fields, the other fields are supposed to make the object to hold state. In
 * such a case, the object instance is not cached.
 * <p/>
 * If a developer wishes to make the object cacheable(inspite of having fileds which hold state of the object),
 * then FrameworkObject#isThreadSafeToCache() should return true.
 * <p/>
 * Created by author on 22-01-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("ALL")
public class FactoryMethodWrapper {

    /**
     * Returns an instance of type clazz. Clients which use this method should have an interface that extends
     * FrameworkObject as the second parameter. The first parameter should be a concrete implementation of the second
     * parameter interface.
     * <p/>
     * The class definition can be present in the classpath of the application or it can be in one of the plugins.
     * If the class definition is present in the classpath, then the current class class loader will be used to
     * define the class.
     * <p/>
     * If the class definition is not found in the application classpath, then a new URLClassLoader (see
     * ParentLastClassLoader) will be used to define the class. The class loader is cached for further usage.
     *
     * @param clazz    A class of type FrameworkObject
     * @param baseType An interface that extends FrameworkObject
     * @param <T>      Type parameter
     * @return An object of type clazz
     */
    public static <T extends FrameworkObject> T getTypedInstance(String clazz, Class<T> baseType) {
        TypeAwareFactory factory = ApplicationContextAccessor.getBean(TypeAwareFactory.class);
        return factory.getInstanceOfType(clazz, baseType);
    }

    /**
     * The class definition can be present in the classpath of the application or it can be in one of the plugins.
     * If the class definition is present in the classpath, then the current class class loader will be used to
     * define the class.
     * <p/>
     * Whichever plugin is first found with the class definition will be used.
     * <p/>
     * If the class definition is not found in the application classpath, then a new URLClassLoader (see
     * ParentLastClassLoader) will be used to define the class. The class loader is cached for further usage.
     *
     * @param clazz A class name as string
     * @return An object created out of the class instance using reflection.
     */
    public static Object getUntypedInstance(String clazz) {
        IClasspathExtensibleFactory factory = getClasspathExtensibleFactory();
        return factory.getObject(clazz);
    }

    /**
     * The class definition can be present in the classpath of the application or it can be in one of the plugins.
     * If the class definition is present in the classpath, then the current class class loader will be used to
     * define the class.
     * <p/>
     * Whichever plugin is first found with the class definition will be used.
     * <p/>
     * If the class definition is not found in the application classpath, then a new URLClassLoader (see
     * ParentLastClassLoader) will be used to define the class. The class loader is cached for further usage.
     *
     * @param clazz A class name as string
     * @return A java.lang.Class instance for the class clazz
     * @throws ClassNotFoundException If class definition is not found in the current classpath or the plugins
     */
    public static Class<?> getClass(String clazz) throws ClassNotFoundException {
        IClasspathExtensibleFactory factory = getClasspathExtensibleFactory();
        return factory.getClass(clazz);
    }

    private static IClasspathExtensibleFactory getClasspathExtensibleFactory() {
        return ApplicationContextAccessor.getBean(IClasspathExtensibleFactory.class);
    }

    public static boolean isClasspathClass(String clazz) {
        ClassPathHacker extender = ApplicationContextAccessor.getBean(ClassPathHacker.class);
        try {
            extender.getClasspathClass(clazz);
            return true;
        } catch (FrameworkException ignore) {
            return false;
        }
    }

    //Just a wrapper. To have have reflection usage from only one place in the code. Works only with classpath classes
    public static Class<?> forName(String clazz) throws ClassNotFoundException {
        return ClassPathHacker.load(clazz);
    }

    //Just a wrapper. To have have reflection usage from only one place in the code. Works with non classpath classes
    // also
    public static Class<?> extendedForName(String clazz) throws ClassNotFoundException {
        ClassPathHacker hacker = ApplicationContextAccessor.getBean(ClassPathHacker.class);
        return hacker.getClass(clazz);
    }
}