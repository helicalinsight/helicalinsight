package com.helicalinsight.efw.framework.utils;

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.NoJarFileFoundException;
import org.jetbrains.annotations.NotNull;
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

    public static void addUrlToSystemClassLoader(@NotNull File jarsLocation, String className) {
        inspectJarsLocation(jarsLocation);
        String jar = JarFinder.findJar(jarsLocation, className);
        if (jar == null) {
            throw new NoJarFileFoundException(String.format("The jar file for the class %s is not found in %s.",
                    className, jarsLocation));
        }
        String jarPath = jarsLocation + File.separator + jar;
        add(jarPath);
    }

    private static void inspectJarsLocation(@NotNull File location) {
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

    private static void add(@NotNull String jarPath) {
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
