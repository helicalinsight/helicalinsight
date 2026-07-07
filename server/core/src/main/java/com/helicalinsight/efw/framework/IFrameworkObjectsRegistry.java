package com.helicalinsight.efw.framework;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by author on 18-01-2015.
 *
 * @author Rajasekhar
 */
interface IFrameworkObjectsRegistry {

    boolean contains(String className);

    void register(String className, Object instance);

    Object get(String className);

    void remove(String className);

    @NotNull
    List<Object> list();

    long getCount();

}
