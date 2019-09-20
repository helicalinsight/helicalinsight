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

package com.helicalinsight.efw.externalresources;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * this is producer class which responsible for producing the class type of
 * ExternalResourceFactory
 *
 * @author Muqtar Ahmed
 */

public class ExternalResourceFactoryProducer {

    /**
     * this is static method which return the ExternalResourceFactory class type
     *
     * @param file     File name
     * @param genClass class name which instance to be create
     * @param response HttpServletResponse
     * @return ExternalResourceFactory class
     */
    public static ExternalResourceAbstractFactory getFactory(File file, String genClass, HttpServletResponse response) {
        return new ExternalResourceFactory(file, genClass, response);
    }
}
