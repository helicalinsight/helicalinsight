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
 * Thrown when the user of a particular service doesn't provide the requisite
 * parameters that are vital for processing
 *
 * @author Rajasekhar
 */
public class RequiredParametersNotProvidedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * No-Arg constructor
     */

    public RequiredParametersNotProvidedException() {
    }

    /**
     * Convenient constructor that explains the exception with a user provided
     * message
     *
     * @param message The user's custom message
     */
    public RequiredParametersNotProvidedException(String message) {
        super(message);
    }
}
