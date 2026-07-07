package com.helicalinsight.datasource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Efwd stands for Enterprise Framework dataSource.
 * Created by author on 17-Jan-15.
 * @author Rajasekhar
 */
final class Efwd {

    private final int globalId;
    private final String serviceType;

    Efwd(int globalId, String serviceType) {
        this.globalId = globalId;
        this.serviceType = serviceType;
    }

    public int getGlobalId() {
        return globalId;
    }

    public String getServiceType() {
        return serviceType;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Efwd efwd = (Efwd) object;

        return globalId == efwd.globalId && !(serviceType != null ? !serviceType.equals(efwd.serviceType) : efwd
                .serviceType != null);
    }

    @Override
    public int hashCode() {
        int result = globalId;
        result = 31 * result + (serviceType != null ? serviceType.hashCode() : 0);
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "Efwd{" +
                "globalId=" + globalId +
                ", serviceType='" + serviceType + '\'' +
                '}';
    }
}
