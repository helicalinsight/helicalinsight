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

package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.exceptions.NoJarFileFoundException;
import com.helicalinsight.efw.exceptions.RuntimeIOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by author on 29-01-2015.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
public class DriverJarsReader {

    private static final String META_INF_SERVICES_JAVA_SQL_DRIVER = "META-INF/services/java.sql.Driver";


    public static List<String> getAvailableJdbcDrivers(String driverPath) {
        File folder = new File(driverPath);
        File[] listOfFiles = folder.listFiles();
        List<String> availableDrivers = new ArrayList<>();
        if (listOfFiles != null) {
            for (File jarFile : listOfFiles) {
                if (!jarFile.getAbsolutePath().endsWith(".jar")) {
                    continue;
                }
                populateListWithDriversFromJar(driverPath, availableDrivers, jarFile,
                        META_INF_SERVICES_JAVA_SQL_DRIVER);
            }
        } else {
            throw new NoJarFileFoundException("Jdbc drivers are not available.");
        }
        return availableDrivers;
    }

    private static void populateListWithDriversFromJar(String driverPath, List<String> availableDrivers,
                                                       File jarFile, String serviceProviderLocation) {
        try {
            String jarPathAsString = driverPath + File.separator + jarFile.getName();
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(jarPathAsString));
            for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream
                    .getNextEntry()) {
                String entryName = zipEntry.getName();
                if (entryName.equals(serviceProviderLocation)) {
                    Scanner scanner = new Scanner(zipInputStream);
                    while (scanner.hasNext()) {
                        String jdbcDriver = scanner.next();
                        if (!availableDrivers.contains(jdbcDriver)) {
                            availableDrivers.add(jdbcDriver);
                        }
                    }
                }
            }
            zipInputStream.close();
        } catch (IOException e) {
            throw new RuntimeIOException("Could not read the jdbc driver", e);
        }
    }
}
