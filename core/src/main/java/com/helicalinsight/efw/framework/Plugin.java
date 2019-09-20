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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by user on 12/26/2016.
 *
 * @author Rajasekhar
 */
public class Plugin {

    private String uuid;

    private String name;

    //By default it is false
    private boolean isEnabled;

    private boolean isDriver;

    private List<String> entryPointClasses;

    //A plugin may or may not have a directory. A single jar file also can be treated as a Plugin
    //So, in case of a single jar file for example ingres jar, the directory will be null.
    private Directory directory;

    //Stores all the fully qualified class names from all the jar files
    private Set<String> classes;

    //Used for jar plugins
    public Plugin(String name, Set<String> classes) {
        this.name = name;
        this.classes = classes;
        this.uuid = UUID.randomUUID().toString();
        this.entryPointClasses = new ArrayList<>();
        this.isEnabled = false;
        this.isDriver = false;
    }

    //Used for Directory plugins
    public Plugin(String name, Directory directory, Set<String> classes) {
        this.name = name;
        this.directory = directory;
        this.classes = classes;
        this.uuid = UUID.randomUUID().toString();
        this.entryPointClasses = new ArrayList<>();
        this.isEnabled = false;
        this.isDriver = false;
    }

    //Adds any file to a plugin folder. Non jar files which are direct children of Drivers, Plugins are ignored
    public void addFile(String file) {
        if (PluginUtils.isJar(file)) {
            boolean exists = new File(file).exists();
            if (exists) {
                Set<String> jarClasses = PluginUtils.listClasses(file);
                this.classes.addAll(jarClasses);
            }
        }

        if (this.directory != null) {
            PluginsRegistry registry = PluginsRegistry.getInstance();
            PluginEntry pluginEntry = registry.getPluginEntry(this.name);
            //Only if entry is registered
            if (pluginEntry != null) {
                ParentLastClassLoader parentLastClassLoader = pluginEntry.getParentLastClassLoader();
                try {
                    parentLastClassLoader.addURL(new File(file));
                } catch (MalformedURLException ex) {
                    throw new PluginServiceException("Could not add the file to the class loader", ex);
                }
            }

            Set<String> resources = this.directory.getResources();
            resources.add(file);
        }
    }

    public void deleteFile(String file) {
        if (PluginUtils.isJar(file)) {
            boolean exists = new File(file).exists();
            if (exists) {
                Set<String> jarClasses = PluginUtils.listClasses(file);
                this.classes.removeAll(jarClasses);
            }
        }

        if (this.directory != null) {
            Set<String> resources = this.directory.getResources();
            resources.remove(file);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Set<String> getClasses() {
        return classes;
    }

    public void setClasses(Set<String> classes) {
        this.classes = classes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public List<String> getEntryPointClasses() {
        return entryPointClasses;
    }

    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean isDriver) {
        this.isDriver = isDriver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plugin plugin = (Plugin) o;

        if (isDriver != plugin.isDriver) return false;
        if (classes != null ? !classes.equals(plugin.classes) : plugin.classes != null) return false;
        if (directory != null ? !directory.equals(plugin.directory) : plugin.directory != null) return false;
        if (entryPointClasses != null ? !entryPointClasses.equals(plugin.entryPointClasses) : plugin
                .entryPointClasses != null)
            return false;
        if (name != null ? !name.equals(plugin.name) : plugin.name != null) return false;
        if (uuid != null ? !uuid.equals(plugin.uuid) : plugin.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isDriver ? 1 : 0);
        result = 31 * result + (entryPointClasses != null ? entryPointClasses.hashCode() : 0);
        result = 31 * result + (directory != null ? directory.hashCode() : 0);
        result = 31 * result + (classes != null ? classes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", isEnabled=" + isEnabled +
                ", isDriver=" + isDriver +
                ", entryPointClasses=" + entryPointClasses +
                ", directory=" + directory +
                ", classes=" + classes +
                '}';
    }
}