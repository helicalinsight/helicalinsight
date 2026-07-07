package com.helicalinsight.efw.framework;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

/**
 * Created by author on 21-01-2015.
 *
 * @author Rajasekhar
 */
@Component
class Factory {

    private static final Logger logger = LoggerFactory.getLogger(Factory.class);

    @NotNull
    Object createObject(String clazz, @NotNull Class<?> requestedClassType) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Constructor<?> constructor = requestedClassType.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object type = constructor.newInstance();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Successfully created the object instance of type %s.", clazz));
        }
        return type;
    }
}
