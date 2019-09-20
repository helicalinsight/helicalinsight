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

package com.helicalinsight.datasource.calcite;

import com.helicalinsight.datasource.managed.ConnectionProviderUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.utility.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by user on 11/24/2015.
 *
 * @author Rajasekhar
 */
@Component
public class CalciteConnectionProvider {

    private static final String URL = "jdbc:calcite:";
    private static final String CALCITE_DRIVER = "org.apache.calcite.jdbc.Driver";

    public Connection getConnection(String model) {
        if (model == null || "".equals(model.trim())) {
            throw new IllegalArgumentException("Parameter model is empty or null");
        }
        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        String modelFilePath = solutionDirectory + File.separator + model;
        File modelFile = new File(modelFilePath);

        if (!modelFile.exists()) {
            throw new DataSourceModelMissingException("Couldn't find the configuration file(model) for the connection");
        }

        return calciteConnection(modelFile);
    }

    private Connection calciteConnection(File modelFile) {
        try {
            ConnectionProviderUtility.registerDriver(CALCITE_DRIVER);
            Properties info = FileUtils.getProperties(modelFile);
            info.setProperty("lex", "JAVA");
            return DriverManager.getConnection(URL, info);
        } catch (SQLException ex) {
            throw new EfwdServiceException("Couldn't obtain connection.", ex);
        }
    }

    private class DataSourceModelMissingException extends RuntimeException {
        public DataSourceModelMissingException(String message) {
            super(message);
        }
    }
}