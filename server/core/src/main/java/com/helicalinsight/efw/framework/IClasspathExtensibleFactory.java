package com.helicalinsight.efw.framework;

import org.jetbrains.annotations.Nullable;

/**
 * Created by author on 23-01-2015.
 *
 * @author Rajasekhar
 */
interface IClasspathExtensibleFactory {

    @Nullable
    public Object getObject(String clazz);

    public Class<?> getClass(String clazz) throws ClassNotFoundException;
}
