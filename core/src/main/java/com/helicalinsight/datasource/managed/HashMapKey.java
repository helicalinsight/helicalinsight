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

package com.helicalinsight.datasource.managed;


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
    public boolean equals(Object object) {
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