package com.helicalinsight.efw.framework;

import java.util.Set;

/**
 * Created by user on 12/26/2016.
 *
 * @author Rajasekhar
 */
public class Directory {

    //Name of the directory
    private String name;

    //List of all resource names(including all types of files) that this directory holds.
    //If a new resource is added to the directory, then this set will be updated. In such case, the
    private Set<String> resources;

    public Directory(String name, Set<String> resources) {
        this.name = name;
        this.resources = resources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getResources() {
        return resources;
    }

    public void setResources(Set<String> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Directory directory = (Directory) object;

        if (resources != null ? !resources.equals(directory.resources) : directory.resources != null) return false;
        if (name != null ? !name.equals(directory.name) : directory.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (resources != null ? resources.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "resources=" + resources +
                ", name='" + name + '\'' +
                '}';
    }
}