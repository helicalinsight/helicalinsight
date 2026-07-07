package com.helicalinsight.resourcesecurity.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Created by author on 21-12-2021.
 *
 * @author Karthik
 */
public final class ShareData {
    private Integer id;
    private Integer permission;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "ShareData{" +
                "id=" + id +
                ", permission=" + permission +
                '}';
    }
}
