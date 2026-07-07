package com.helicalinsight.efw.framework;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by author on 18-01-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unchecked")
@Component
final class StatelessObjectsFactory extends Factory {

    private static final Logger logger = LoggerFactory.getLogger(StatelessObjectsFactory.class);

    @Autowired
    private IFrameworkObjectsRegistry registry;

    public <T extends FrameworkObject> T get(String clazz, @NotNull Class<? extends T> requestedClassType) {
        synchronized (this) {
            try {
                return newInstance(clazz, requestedClassType);
            } catch (NoSuchMethodException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("The method getInstance is not present in the " + clazz);
                }
                return create(clazz, requestedClassType);
            } catch (NullPointerException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Probably the method getInstance is not static in the class " + clazz);
                }
                return create(clazz, requestedClassType);
            } catch (Exception ex) {
                throw new FrameworkObjectInstantiationException("Could not create the framework object instance.", ex);
            }
        }
    }

    @NotNull
    private <T extends FrameworkObject> T newInstance(String clazz, @NotNull Class<? extends T> requestedClassType)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (registry.contains(clazz)) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Found a cached instance of the class %s. Returning " + "the same.", clazz));
            }
            return (T) registry.get(clazz);
        } else {
            return getSupposedlySingletonObjectUsingGetInstance(clazz, requestedClassType);
        }
    }

    private <T extends FrameworkObject> T create(String clazz, @NotNull Class<? extends T> requestedClassType) {
        T instance;
        try {
            instance = (T) createObject(clazz, requestedClassType);
            registry.register(clazz, instance);
        } catch (Exception ex) {
            throw new FrameworkObjectInstantiationException("Could not create the instance.", ex);
        }
        return instance;
    }

    @NotNull
    private <T extends FrameworkObject> T getSupposedlySingletonObjectUsingGetInstance(String clazz, @NotNull Class<?
            extends
            T> requestedClassType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getInstanceMethod = requestedClassType.getMethod("getInstance");
        Object instance = getInstanceMethod.invoke(null);
        registry.register(clazz, instance);
        return (T) instance;
    }
}