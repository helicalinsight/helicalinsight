package com.helicalinsight.efw.externalresources;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * this is producer class which responsible for producing the class type of
 * ExternalResourceFactory
 *
 * @author muqtar ahmed
 * @version 1.1
 * @since 1.0
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
