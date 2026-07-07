package com.helicalinsight.datasource.managed;

import java.io.File;

/**
 * Created by author on 28-Dec-14.
 *
 * @author Rajasekhar
 */
interface IConnectionSettings {

    String getJson(File connectionsXmlFile, Integer connectionId);

}
