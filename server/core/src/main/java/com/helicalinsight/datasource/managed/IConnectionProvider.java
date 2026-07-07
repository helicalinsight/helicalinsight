package com.helicalinsight.datasource.managed;

import java.sql.Connection;

/**
 * Created by author on 11-Dec-14.
 *
 * @author Rajasekhar
 */
interface IConnectionProvider {

    Connection newConnection(String connectionPropertiesJson);

}
