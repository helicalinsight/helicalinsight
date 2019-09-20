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

package com.helicalinsight.efw.framework;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParentLastClassLoader extends ClassLoader implements AutoCloseable {
    private ChildURLClassLoader childClassLoader;

    public ParentLastClassLoader(List<URL> classpath) {
        super(ParentLastClassLoader.class.getClassLoader());
        URL[] urls = classpath.toArray(new URL[classpath.size()]);
        this.childClassLoader = new ChildURLClassLoader(urls, this.getParent());
    }

    public List<String> getRegistry() {
        return Collections.unmodifiableList(this.childClassLoader.getRegistry());
    }

    public void close() throws IOException {
        this.childClassLoader.close();
    }

    public void addURL(File file) throws MalformedURLException {
        this.childClassLoader.addURL(file.toURI().toURL());
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) {
        try {
            return this.childClassLoader.findClass(name);
        } catch (ClassNotFoundException ex) {
            throw new PluginLoadingFailedException("Unable to load " + name, ex);
        }
    }

    private static class ChildURLClassLoader extends URLClassLoader {
        private ClassLoader realParent;
        private List<String> registry;

        public ChildURLClassLoader(URL[] urls, ClassLoader realParent) {
            super(urls, null);
            this.realParent = realParent;
            this.registry = new ArrayList<>();
        }

        public void addURL(URL url) {
            super.addURL(url);
        }

        public List<String> getRegistry() {
            return registry;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                // Check system JDK classes
                try {
                    Class<?> systemClass = findSystemClass(name);
                    if (systemClass != null) {
                        return systemClass;
                    }
                } catch (ClassNotFoundException ignore) {
                }

                // Check if this class loader has already loaded class
                Class<?> loadedClass = super.findLoadedClass(name);
                if (loadedClass != null) {
                    return loadedClass;
                }

                // First try to use the URLClassLoader findClass
                loadedClass = super.findClass(name);
                registry.add(loadedClass.getName());
                return loadedClass;
            } catch (ClassNotFoundException ignore) {
                // If that fails, we ask our real parent class loader to load the class (we give up)
                return realParent.loadClass(name);
            }
        }
    }

    private static class PluginLoadingFailedException extends RuntimeException {
        public PluginLoadingFailedException(String message, ClassNotFoundException exception) {
            super(message, exception);
        }
    }
}