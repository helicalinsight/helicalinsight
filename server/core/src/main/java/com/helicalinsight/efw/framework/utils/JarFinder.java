package com.helicalinsight.efw.framework.utils;

import com.helicalinsight.efw.exceptions.DuplicateJarFileFoundException;
import com.helicalinsight.efw.exceptions.RuntimeIOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Created by author on 28-Nov-14.
 *
 * @author Rajasekhar
 * @author Muqtar
 */
public class JarFinder {

    @Nullable
    public static String findJar(@NotNull File jarsLocation, String className) {
        File[] listOfFiles = jarsLocation.listFiles();
        String jarFileName = null;
        className = className.replace(".", "/");
        if (listOfFiles != null) {
            int counter = 0;
            for (File jarFile : listOfFiles) {
                if (!jarFile.getAbsolutePath().endsWith(".jar")) {
                    continue;
                }
                if (search(className, jarFile)) {
                    counter++;
                    if ((counter != 0) && (counter > 1)) {
                        throw new DuplicateJarFileFoundException("Duplicate Jar files are found " +
                                "in the Jars directory for the class " + className);
                    }
                    jarFileName = jarFile.getName();
                }
            }
        }
        return jarFileName;
    }

    private static boolean search(String className, @NotNull File jarFile) {
        JarInputStream jarInputStream = null;
        try {
            jarInputStream = new JarInputStream(new FileInputStream(jarFile));
            while (true) {
                JarEntry jarEntry = jarInputStream.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if ((jarEntry.getName().equalsIgnoreCase(className + ".class"))) {
                    return true;
                }
            }
            jarInputStream.close();
        } catch (IOException e) {
            throw new RuntimeIOException("Could not search for the jars in the drivers location", e);
        } finally {
            if (jarInputStream != null) {
                try {
                    jarInputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
        return false;
    }
}
