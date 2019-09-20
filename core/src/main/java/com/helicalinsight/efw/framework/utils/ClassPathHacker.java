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

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.NoJarFileFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathHacker {

    @SuppressWarnings("rawtypes")
    private static final Class[] parameters = new Class[]{URL.class};

    private static final Logger logger = LoggerFactory.getLogger(ClassPathHacker.class);

    public static void addUrlToSystemClassLoader(File jarsLocation, String className) {
        inspectJarsLocation(jarsLocation);
        String jar = JarFinder.findJar(jarsLocation, className);
        if (jar == null) {
            throw new NoJarFileFoundException(String.format("The jar file for the class %s is not found in %s.",
                    className, jarsLocation));
        }
        String jarPath = jarsLocation + File.separator + jar;
        add(jarPath);
    }

    private static void inspectJarsLocation(File location) {
        File[] files = location.listFiles();
        if (files != null) {
            int counter = 0;
            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".jar")) {
                    counter++;
                }
            }
            if (counter == 0) {
                throw new NoJarFileFoundException("No jars files are found in the directory.");
            }
        }
    }

    private static void add(String jarPath) {
        URL jarUrl;
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        try {
            jarUrl = new File(jarPath).toURI().toURL();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(urlClassLoader, jarUrl);
            if (logger.isInfoEnabled()) {
                logger.info("The jar " + jarPath + " has been successfully added to the classpath" +
                        ". Its class loader is " + urlClassLoader.getClass());
            }
        } catch (SecurityException ex) {
            throw new ConfigurationException("SecurityException: Security Manager is present " + "and is not allowing" +
                    " the protected method to be accessible.", ex);
        } catch (Exception ex) {
            throw new ConfigurationException("Failed to add the jar file to the classpath.", ex);
        }
    }
}
