package com.helicalinsight.datasource.managed;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by author on 29-Nov-14.
 *
 * @author Rajasekhar
 */
public final class HashMapKey {

    //All fields are final as the object is being used as the key
    private final String jdbcUrl;
    private final String dataSourceProvider;
    private final String userName;
    private final String password;
    private final String driverName;

    public HashMapKey(String jdbcUrl, String dataSourceProvider, String userName, String password, String driverName) {
        this.jdbcUrl = jdbcUrl;
        this.dataSourceProvider = dataSourceProvider;
        this.userName = userName;
        this.password = password;
        this.driverName = driverName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getDataSourceProvider() {
        return dataSourceProvider;
    }

    public String getDriverName() {
        return driverName;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        HashMapKey that = (HashMapKey) object;

        return !(jdbcUrl != null ? !jdbcUrl.equals(that.jdbcUrl) : that.jdbcUrl != null) && !(dataSourceProvider !=
                null ? !dataSourceProvider.equals(that.dataSourceProvider) : that.dataSourceProvider != null) && !
                (userName != null ? !userName.equals(that.userName) : that.userName != null) && !(password != null ?
                !password.equals(that.password) : that.password != null);
    }

    @Override
    public int hashCode() {
        int result = jdbcUrl != null ? jdbcUrl.hashCode() : 0;
        result = 31 * result + (dataSourceProvider != null ? dataSourceProvider.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "HashMapKey{" +
                "jdbcUrl='" + jdbcUrl + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}