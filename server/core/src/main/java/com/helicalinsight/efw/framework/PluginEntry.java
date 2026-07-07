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
