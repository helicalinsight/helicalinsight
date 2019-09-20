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

/**
 * Created by user on 2/2/2017.
 *
 * @author Rajasekhar
 */
public class PluginEntry {

    private Plugin plugin;
    private ParentLastClassLoader parentLastClassLoader;

    public PluginEntry(Plugin plugin, ParentLastClassLoader parentLastClassLoader) {
        this.plugin = plugin;
        this.parentLastClassLoader = parentLastClassLoader;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public ParentLastClassLoader getParentLastClassLoader() {
        return parentLastClassLoader;
    }

    public void setParentLastClassLoader(ParentLastClassLoader parentLastClassLoader) {
        this.parentLastClassLoader = parentLastClassLoader;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        PluginEntry that = (PluginEntry) other;

        if (parentLastClassLoader != null ? !parentLastClassLoader.equals(that.parentLastClassLoader) : that
                .parentLastClassLoader != null)
            return false;
        if (plugin != null ? !plugin.equals(that.plugin) : that.plugin != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = plugin != null ? plugin.hashCode() : 0;
        result = 31 * result + (parentLastClassLoader != null ? parentLastClassLoader.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PluginEntry{" +
                "plugin=" + plugin +
                ", parentLastClassLoader=" + parentLastClassLoader +
                '}';
    }
}
