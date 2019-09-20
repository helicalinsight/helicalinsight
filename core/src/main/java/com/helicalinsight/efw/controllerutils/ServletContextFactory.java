/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.controllerutils;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * When we need to wire a servlet context object in spring xml configuration
 * this class bean can actually be used for that property which is of type
 * <code>ServletContext</code>
 *
 * @author Rajasekhar
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