package com.helicalinsight.datasource.managed;

import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DatabaseConnectionFactory;
import com.helicalinsight.datasource.GlobalDsGlobalIdFinder;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by author on 28-02-2015.
 *
 * @author Rajasekhar
 */
@Component
public class GlobalXmlReader implements IGlobalXmlReader {

    public String getDataSourceJson(Integer connectionId) {
        File file = new File(JsonUtils.getGlobalConnectionsPath());
        if (connectionId == -1) {
            return DatabaseConnectionFactory.getMinus1DataSource();
        }
        String json;
        IConnectionSettings connectionSettings = ApplicationContextAccessor.getBean(IConnectionSettings.class);
        json = connectionSettings.getJson(file, connectionId);

        if (json == null) {
            throw new ConfigurationException("The application configuration is incorrect. Check the connections " +
                    "xml.");
        }

        return json;
    }
}
