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

/**
 * <p>
 * Utility class which finds the type of OS on which the application is running
 * </p>
 *
 * @author Rajasekhar
 */
public final class OsCheck {
    /**
     * Enum type which contains OS names.
     */
    private static OSType detectedOS;

    /**
     * <p>
     * Finds the OS
     * </p>
     *
     * @return One of the values of the enum OSType
     */
    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase();
            if (OS.contains("win")) {
                detectedOS = OSType.Windows;
            } else if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                detectedOS = OSType.MacOS;
            } else {
                detectedOS = OSType.Linux;
            }
        }
        return detectedOS;
    }

    /**
     * Represents the popular os types i.e Windows, or MacOS or Linux
     */
    public enum OSType {
        Windows, MacOS, Linux
    }
}