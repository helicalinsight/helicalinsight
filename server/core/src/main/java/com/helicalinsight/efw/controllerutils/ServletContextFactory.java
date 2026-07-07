package com.helicalinsight.efw.controllerutils;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.ServletContextAware;

import jakarta.servlet.ServletContext;

/**
 * When we need to wire a servlet context object in spring xml configuration
 * this class bean can actually be used for that property which is of type
 * <code>ServletContext</code>
 *
 * @author Rajasekhar
 * @since 1.2
 */
public class ServletContextFactory implements FactoryBean<ServletContext>, ServletContextAware {
    private ServletContext servletContext;

    /**
     * Returns the servlet context instance
     *
     * @return The servlet context instance
     * @throws Exception If something goes wrong ):
     */
    @Override
    public ServletContext getObject() throws Exception {
        return servletContext;
    }

    /**
     * Returns the ServletContext class type
     *
     * @return The ServletContext class type
     */
    @NotNull
    @Override
    public Class<?> getObjectType() {
        return ServletContext.class;
    }

    /**
     * Always a singleton. Per application one instance.
     *
     * @return true
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * The ServletContext of the application is supplied by spring container
     * which is set to the property
     *
     * @param servletContext The ServletContext of the application
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}