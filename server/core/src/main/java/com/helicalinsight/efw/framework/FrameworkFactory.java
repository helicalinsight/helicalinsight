package com.helicalinsight.efw.framework;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by author on 20-Jan-15.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unchecked")
@Component
final class FrameworkFactory implements TypeAwareFactory, IClasspathExtensibleFactory {

    private static final Logger logger = LoggerFactory.getLogger(FrameworkFactory.class);

    @Autowired
    private StatelessObjectsFactory statelessObjectsFactory;

    @Autowired
    @Qualifier("factory")
    private Factory factory;

    @Autowired
    private ObjectStateChecker objectStateChecker;

    @Autowired
    private IFrameworkObjectsRegistry registry;

    @Autowired
    private ClassPathHacker classPathHacker;

    @Nullable
    @Override
    public <T extends FrameworkObject> T getInstanceOfType(@Nullable String clazz, @Nullable Class<T> baseType) {
        if (clazz == null || baseType == null) {
            throw new UnSupportedObjectTypeException("The parameters clazz and/or baseType null");
        }

        T object;
        try {
            Class<?> requestedClass = this.classPathHacker.getClass(clazz);
            boolean assignable = (requestedClass != null) && (baseType.isAssignableFrom(requestedClass));
            if (!assignable) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("The requested class %s is not assignable to the baseType %s.", clazz,
                            baseType));
                }
                throw new FrameworkException("Requested class is not assignable to the Interface." + baseType.getName
                        ());
            } else {
                //Modified by author Rajasekhar on 14/07/2015
                //Fixed an issue of unnecessary object creation if it is already cached.
                if (this.registry != null && this.registry.contains(clazz)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Returning instance of the class %s for reuse" + ".", clazz));
                    }
                    object = (T) this.registry.get(clazz);
                } else {
                    object = getObject(clazz, (Class<? extends T>) requestedClass);
                }
            }

            if (object == null) {//Still object is null? Hmm. Throw exception.
                throw new FrameworkException("Unable to produce the requested class object.");
            }
            if (object.isThreadSafeToCache()) {
                if (this.registry != null && !this.registry.contains(clazz)) {//No need to cache if it already
                    //cached
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Caching instance of the class %s for reuse.", clazz));
                    }
                    this.registry.register(clazz, object);
                }
            }
        } catch (Exception exception) {
            if ((this.registry != null) && (this.registry.contains(clazz))) {
                this.registry.remove(clazz);
            }
            throw new FrameworkObjectInstantiationException(String.format("Could not create the instance of " +
                    "baseType %s", clazz), exception);
        }
        return object;
    }

    private <T extends FrameworkObject> T getObject(String clazz, @NotNull Class<?
            extends T> requestedClassType) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        if (this.objectStateChecker.isStateful(requestedClassType)) {
            return (T) this.factory.createObject(clazz, requestedClassType);
        } else {
            return this.statelessObjectsFactory.get(clazz, requestedClassType);
        }
    }

    @Nullable
    public Object getObject(@Nullable String clazz) {
        try {
            Class<?> requestedClass = getClass(clazz);
            if (requestedClass != null) {
                return this.factory.createObject(clazz, requestedClass);
            } else {
                throw new FrameworkException("Could not load the class from external location.");
            }
        } catch (Exception exception) {
            throw new FrameworkException("Could not load the class from external location", exception);
        }
    }

    public Class<?> getClass(String clazz) throws ClassNotFoundException {
        if (clazz == null) {
            throw new IllegalArgumentException("The value of class is null.");
        }
        return this.classPathHacker.getClass(clazz);
    }
}