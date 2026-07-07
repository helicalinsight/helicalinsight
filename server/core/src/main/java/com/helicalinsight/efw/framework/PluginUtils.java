package com.helicalinsight.efw.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by user on 12/23/2016.
 *
 * @author Rajasekhar
 */
public class PluginUtils {
    private static Logger logger = LoggerFactory.getLogger(PluginUtils.class);

    public static Set<String> listClasses(String jarPath) {

        while (fileInUse(jarPath)) {
            fileInUse(jarPath);
        }

        Set<String> classNames = new HashSet<>();

        ZipInputStream zip = null;
        try {
            zip = new ZipInputStream(new FileInputStream(jarPath));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    classNames.add(className.substring(0, className.length() - ".class".length()));
                }
            }
        } catch (IOException ex) {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                }
            }
            throw new RuntimeException(ex);
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                }
            }
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
                @SuppressWarnings("UnusedDeclaration")
                Driver driver = (Driver) loadedClass.newInstance();
            }
            return true;
        } catch (ClassNotFoundException | LinkageError ex) {
            try {
                parentLastClassLoader.close();
            } catch (IOException e) {
                logger.debug("closed the ClassLoader.");
            }
            throw new FrameworkException(String.format("Unable to load the class %s.", clazz), ex);
        } catch (InstantiationException | IllegalAccessException | ClassCastException ex) {
            return false;
        }
    }

    public static List<String> getLoadedClasses() {    	
    	 return  DriverManager.drivers().map(Driver::getClass)
    	    	.map(Class::getName)
    	    	.toList();
    }

    public static boolean fileInUse(String absolutePath) {
        boolean inUse = true;
        File file = new File(absolutePath);
        FileChannel channel = null;
        FileLock lock = null;

        try {
            channel = new RandomAccessFile(file, "rw").getChannel();

            // Get an exclusive lock on the whole file

            lock = channel.tryLock();
            inUse = false;
            // Ok. You get the lock
        } catch (IOException ignore) {

        } finally {
            try {
                if (lock != null) {

                    lock.release();
                }
                if (channel != null) {
                    channel.close();
                }
                return inUse;
            } catch (IOException e) {

            }
        }
        return inUse;
    }
}
