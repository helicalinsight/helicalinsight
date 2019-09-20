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

import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * this is sub class of ExternalResourceAbstractFactory abstract class which has
 * IExternalResource abstract method this class overload the this abstract
 * method and return the IExternalResource which is interface type.
 *
 * @author Muqtar Ahmed
 */
public class ExternalResourceFactory extends ExternalResourceAbstractFactory {

    private static final Logger logger = LoggerFactory.getLogger(ExternalResourceFactory.class);

    private File file;
    private String genClass;
    private HttpServletResponse response;

    /**
     * overloaded constructor with 3 arguments
     *
     * @param file     File
     * @param genClass class name
     * @param response HttpServletResponse
     */
    public ExternalResourceFactory(File file, String genClass, HttpServletResponse response) {
        this.file = file;
        this.genClass = genClass;
        this.response = response;
    }

    /**
     * This method checks for passed class and return the boolean value if class is found in the classpath
     *
     * @param className passed class name
     * @return boolean value
     */
    private static boolean isClass(String className) {
        boolean exist = true;
        try {
            FactoryMethodWrapper.getClass(className);
        } catch (ClassNotFoundException ex) {
            exist = false;
            logger.error("ClassNotFoundException", ex);
            //handle error
        }
        return exist;
    }

    /**
     * this overloaded method is responsible for creating the instance of passed
     * class type and return the instance as interface type
     *
     * @return IExternalResource
     */

    public IExternalResource getExternalResource() {
        IExternalResource obj;
        if (isClass(genClass)) {
            obj = (IExternalResource) FactoryMethodWrapper.getUntypedInstance(genClass);
            obj.setFile(file);
            obj.setResponse(response);
        } else {
            logger.error("Class not found in JVM Classpath.");
            throw new ClassNotConfiguredException(genClass);
        }
        return obj;
    }
}
