package com.helicalinsight.datasource.managed;

import org.jetbrains.annotations.Nullable;

/**
 * Created by author on 21-Dec-14.
 *
 * @author Rajasekhar
 */

public interface IDataSourceFactory {

    @Nullable
    javax.sql.DataSource sqlDataSource(String json);

}
