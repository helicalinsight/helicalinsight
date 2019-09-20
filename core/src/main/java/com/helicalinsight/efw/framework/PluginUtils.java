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

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by user on 12/23/2016.
 *
 * @author Rajasekhar
 */
public class PluginUtils {

    public static Set<String> listClasses(String jarPath) {
        Set<String> classNames = new HashSet<>();
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath))) {
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    classNames.add(className.substring(0, className.length() - ".class".length()));
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return classNames;
    }

    public static boolean isJar(String absolutePath) {
        return absolutePath.endsWith(".jar");
    }

    public static boolean isDriver(String clazz, ParentLastClassLoader parentLastClassLoader) {
        try {
            Class<?> loadedClass = ClassPathHacker.getClassUsingPluginClassLoader(clazz, parentLastClassLoader);
            boolean assignableFrom = Driver.class.isAssignableFrom(loadedClass);
            if (!assignableFrom) {
                @SuppressWarnings("UnusedDeclaration") Driver driver = (Driver) loadedClass.newInstance();
            }
            return true;
        } catch (ClassNotFoundException ex) {
            throw new FrameworkException(String.format("Unable to load the class %s.", clazz), ex);
        } catch (InstantiationException | IllegalAccessException | ClassCastException ex) {
            return false;
        }
    }

    public static List<String> getLoadedClasses(ClassLoader classLoader) throws NoSuchFieldException,
            IllegalAccessException {
        Field f = ClassLoader.class.getDeclaredField("classes");
        f.setAccessible(true);
        @SuppressWarnings("unchecked") Vector<Class> classes = (Vector<Class>) f.get(classLoader);

        List<String> loadedClasses = new ArrayList<>();
        for (Class clazz : classes) {
            loadedClasses.add(clazz.getName());
        }

        return loadedClasses;
    }
}
