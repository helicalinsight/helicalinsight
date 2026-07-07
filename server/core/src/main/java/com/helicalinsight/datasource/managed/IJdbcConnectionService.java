package com.helicalinsight.datasource.managed;

import java.sql.Connection;

/**
 * Created by author on 28-02-2015.
 *
 * @author Rajasekhar
 */
public interface IJdbcConnectionService {

    Connection getDatabaseConnection(String json);

}
