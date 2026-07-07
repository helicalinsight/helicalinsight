package com.helicalinsight.efw.externalresources;

/**
 * this is abstract class which is used to implements the abstract design
 * factory design pattern for reading the different files such as images, jss,
 * css.
 *
 * @author muqtar ahmed
 * @version 1.1
 * @since 1.0
 */

public abstract class ExternalResourceAbstractFactory {

    /**
     * this is abstract method which return the interface type of
     * IExternalResource
     *
     * @return IExternalResource interface
     */

    public abstract IExternalResource getExternalResource();

}
