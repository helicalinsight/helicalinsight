package com.helicalinsight.efw.externalresources;

import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * this is sub class of ExternalResourceAbstractFactory abstract class which has
 * IExternalResource abstract method this class overload the this abstract
 * method and return the IExternalResource which is interface type.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
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
}
