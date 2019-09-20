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

import com.helicalinsight.efw.exceptions.DuplicateJarFileFoundException;
import com.helicalinsight.efw.exceptions.RuntimeIOException;

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

    public static String findJar(File jarsLocation, String className) {
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

    private static boolean search(String className, File jarFile) {
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
