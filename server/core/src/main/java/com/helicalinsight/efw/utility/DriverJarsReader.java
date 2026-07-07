package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.exceptions.NoJarFileFoundException;
import com.helicalinsight.efw.exceptions.RuntimeIOException;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public static List<String> getAvailableJdbcDrivers(@NotNull String driverPath) {
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

    private static void populateListWithDriversFromJar(String driverPath, @NotNull List<String> availableDrivers,
                                                       @NotNull File jarFile, String serviceProviderLocation) {
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
