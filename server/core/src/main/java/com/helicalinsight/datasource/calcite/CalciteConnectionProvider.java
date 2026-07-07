package com.helicalinsight.datasource.calcite;

import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.datasource.managed.ConnectionProviderUtility;
import com.helicalinsight.efw.ApplicationProperties;
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