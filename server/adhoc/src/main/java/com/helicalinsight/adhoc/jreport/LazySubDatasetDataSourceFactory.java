package com.helicalinsight.adhoc.jreport;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.RowSetProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.CustomJRResultSetDataSource;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * Holds subdataset connection metadata and defers query execution until Jasper
 * evaluates a component's dataset run. At resolve time, mapped parameter values
 * (already evaluated by Jasper from $F{}, $V{}, $P{} expressions in
 * dataSetRun.parameters) are merged into connectionDetails before the existing
 * cache / ResultSet pipeline runs.
 */
public class LazySubDatasetDataSourceFactory {

    private final JsonObject connectionDetails;

    public LazySubDatasetDataSourceFactory(JsonObject connectionDetails) {
        this.connectionDetails = copy(connectionDetails);
    }

    public JsonObject getConnectionDetails() {
        return connectionDetails;
    }

    /**
     * Resolves a subdataset with no main-to-subdataset parameter mapping.
     */
    public static JRDataSource resolve(LazySubDatasetDataSourceFactory factory) {
        return resolve(factory, new Object[0]);
    }

    /**
     * Resolves a subdataset with mapped parameters. Arguments after the factory are
     * name-value pairs: {@code "city", $P{city}, "region", $P{region}} so that a
     * single shared factory instance can serve components with different mappings.
     */
    public static JRDataSource resolve(LazySubDatasetDataSourceFactory factory, Object... nameValuePairs) {
        if (factory == null) {
            return emptyDataSource();
        }
        JsonObject request = factory.mergeMappedParameters(nameValuePairs);
        HCRHelper hcrHelper = ApplicationContextAccessor.getBean(HCRHelper.class);
        ResultSet resultSet = hcrHelper.fetchSubdatasetResultSet(request);
        return new CustomJRResultSetDataSource(resultSet != null ? resultSet : emptyResultSet());
    }

    private JsonObject mergeMappedParameters(Object... nameValuePairs) {
        JsonObject request = copy(connectionDetails);
        if (nameValuePairs == null || nameValuePairs.length == 0) {
            return request;
        }
        if (nameValuePairs.length % 2 != 0) {
            throw new EfwServiceException(
                    "Subdataset parameter mapping requires name-value pairs. Received "
                            + nameValuePairs.length + " argument(s).");
        }
        for (int index = 0; index < nameValuePairs.length; index += 2) {
            String parameterName = String.valueOf(nameValuePairs[index]);
            Object parameterValue = nameValuePairs[index + 1];
            if (parameterValue != null) {
                GsonUtility.setMappedValue(request, parameterName, parameterValue);
            }
        }
        return request;
    }

    private static JsonObject copy(JsonObject source) {
        if (source == null) {
            return new JsonObject();
        }
        return new Gson().fromJson(source.toString(), JsonObject.class);
    }

    private static ResultSet emptyResultSet() {
        try {
            return RowSetProvider.newFactory().createCachedRowSet();
        } catch (SQLException exception) {
            throw new EfwServiceException(exception.getMessage());
        }
    }

    private static JRDataSource emptyDataSource() {
        return new CustomJRResultSetDataSource(emptyResultSet());
    }
}
