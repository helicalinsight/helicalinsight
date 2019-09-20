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

package com.helicalinsight.efw.exceptions;

/**
 * Thrown when the setting.xml(in the System/Admin directory) is malformed or
 * consists or inadequate information
 *
 * @author Rajasekhar
 */
public class ImproperXMLConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * No-Arg constructor
     */
    public ImproperXMLConfigurationException() {
        super();
    }

    /**
     * Convenient constructor that explains the exception with a user provided
     * message, and cause
     *
     * @param message user provided message
     * @param cause   The cause of the exception
     */
    public ImproperXMLConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Convenient constructor that explains the exception with a user provided
     * message
     *
     * @param message user provided message
     */
    public ImproperXMLConfigurationException(String message) {
        super(message);
    }

    /**
     * Convenient constructor that explains the cause
     *
     * @param cause The cause of the exception
     */
    public ImproperXMLConfigurationException(Throwable cause) {
        super(cause);
    }
}
