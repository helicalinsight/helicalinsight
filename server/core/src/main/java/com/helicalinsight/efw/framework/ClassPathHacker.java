package com.helicalinsight.efw.framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by author on 18-01-2015.
 *
 * @author Rajasekhar
 */
@Component
final class ClassPathHacker {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathHacker.class);

    // Finds and registers a plugin from a location configured in the
    // project.properties file
    private final PluginFinder pluginFinder = new PluginFinder();

    /**
     * Gets an instance of Class from classpath or from a plugin. If class
     * definition is not found in the classpath, then the Plugins directory will
     * be searched for. If the class definition is found in one of the plugins,
     * then that plugin will be loaded using an instance of URLClassLoader.
     * Which ever plugin first is found with the class definition (inside jars),
     * that plugin will be used. The instance of URLClassLoader is a
     * ParentLastClassLoader meaning all classes required by the plugin will
     * first be searched for in the plugin itself. If one of the required
     * classes is not found in the plugin, then the parent of that class loader
     * will be used to load the class.
     *
     * @param clazz Class name as string
     * @return java.lang.Class instance
     * @throws ClassNotFoundException If the class is not found in classpath and plugins
     */
    public Class<?> getClass(String clazz) throws ClassNotFoundException {
        try {
            return this.getClasspathClass(clazz);
        } catch (FrameworkException ignore) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Info. Could not load a class %s from the classpath. Trying to load from "
                        + "plugin path. Real cause is ", clazz));
            }
            return this.getClassFromPlugin(clazz);
        }
    }

    static Class<?> load(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz);
    }

    public Class<?> getClasspathClass(String clazz) {
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException exception) {
            throw new FrameworkException(String.format("Unable to load the class %s", clazz), exception);
        }
    }

    private Class<?> getClassFromPlugin(String clazz) throws ClassNotFoundException {
        PluginsRegistry registry = PluginsRegistry.getInstance();
        // Check whether the class has already been loaded previously
        ParentLastClassLoader classLoader = registry.getPluginClassLoader(clazz);
        if (classLoader != null) {
            return Class.forName(clazz, true, classLoader);
        } else {
            return loadPlugin(clazz);
        }
    }

    public Class<?> loadPlugin(String clazz) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The current class loader is %s.", this.getClass().getClassLoader().getClass()));
        }
        ParentLastClassLoader pluginClassLoader = null;
        try {
            pluginClassLoader = this.pluginFinder.getPluginClassLoader(clazz);
            return getClassUsingPluginClassLoader(clazz, pluginClassLoader);
        } catch (Throwable veryBad) {
            try {
                if (pluginClassLoader != null) {
                    pluginClassLoader.close();
                }
            } catch (IOException e) {
                logger.debug(String.format("The current class loader is %s resources are cleared",
                        this.getClass().getClassLoader().getClass()));
            }
            throw new FrameworkException(String.format("Unable to load the class %s.", clazz), veryBad);
        }
    }

    static Class<?> getClassUsingPluginClassLoader(String clazz, ParentLastClassLoader pluginClassLoader)
            throws ClassNotFoundException {
        try {

            return Class.forName(clazz, true, pluginClassLoader);
        } catch (ClassNotFoundException | LinkageError ioe) {
            try {
                if (pluginClassLoader != null) {
                    pluginClassLoader.close();
                }
            } catch (IOException ioe2) {
                logger.error("Couldnot close the pluginClassLoader ", ioe);
            }
            throw ioe;
        }

    }
}