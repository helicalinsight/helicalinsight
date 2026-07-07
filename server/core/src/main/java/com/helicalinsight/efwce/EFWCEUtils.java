package com.helicalinsight.efwce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.ConnectionForCe;
import com.helicalinsight.datasource.managed.jaxb.DataMap;
import com.helicalinsight.datasource.managed.jaxb.DataMaps;
import com.helicalinsight.datasource.managed.jaxb.DataSourcesForCe;
import com.helicalinsight.datasource.managed.jaxb.EfwdForCe;
import com.helicalinsight.datasource.managed.jaxb.Parameter;
import com.helicalinsight.datasource.managed.jaxb.Parameters;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.SecurityUtils;

/**
 * Created by author on 10/28/2019.
 *
 * @author Rajesh
 */
public class EFWCEUtils {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    public static EfwdForCe saveEfwd(JsonObject efwdJsonObject, String dir, String uuid, String efwdExtension) {
        EfwdForCe efwdJaxb = prepareEfwd(efwdJsonObject);
        File efwdFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + efwdExtension);
        try {
            synchronized (EFWCEUtils.class) {
                JaxbUtils.marshal(efwdJaxb, efwdFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
        return efwdJaxb;
    }

    public static boolean saveTempEfwd(JsonObject efwdJsonObject, String dir, String uuid, String efwdExtension) {
        EfwdForCe efwdJaxb = prepareEfwd(efwdJsonObject);
        File tempEfwdLoc = new File(dir + File
                .separator + uuid + "." + efwdExtension);
        try {
            synchronized (EFWCEUtils.class) {
                JaxbUtils.marshal(efwdJaxb, tempEfwdLoc);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
        return tempEfwdLoc.exists();
    }

    public static EfwdForCe prepareEfwd(JsonObject efwdJsonObject) {
        EfwdForCe efwdJaxb = ApplicationContextAccessor.getBean(EfwdForCe.class);
        efwdJaxb.setDataSources(prepareDataSource(efwdJsonObject));
        efwdJaxb.setDataMaps(prepareDataMaps(efwdJsonObject));
        return efwdJaxb;
    }

    public static DataMaps prepareDataMaps(JsonObject efwdJsonObject) {
        JsonArray dataMapsJSONArray = GsonUtility.optJsonArray(efwdJsonObject,"dataMaps");
        DataMaps dataMapsJaxb = ApplicationContextAccessor.getBean(DataMaps.class);
        if (dataMapsJSONArray == null || dataMapsJSONArray.isEmpty()) {
            return null;
        }
        List<DataMap> listOfDataMaps = new ArrayList<>();
        for (int index = 0; index < dataMapsJSONArray.size(); index++) {
            JsonObject dataMap = dataMapsJSONArray.get(index).getAsJsonObject().getAsJsonObject("dataMap");
            DataMap dataMapJaxb = ApplicationContextAccessor.getBean(DataMap.class);
            //dataMapJaxb.setId(dataMap.get("id").getAsString());
            dataMapJaxb.setId("1");
            dataMapJaxb.setName(dataMap.get("name").getAsString());
            dataMapJaxb.setType(dataMap.get("type").getAsString());
            dataMapJaxb.setConnection(dataMap.get("connection").getAsString());
            dataMapJaxb.setQuery(dataMap.get("query").getAsString());
            Parameters parameters = prepareParameters(dataMap);
            if (parameters != null)
                dataMapJaxb.setParameters(parameters);
            listOfDataMaps.add(dataMapJaxb);
        }
        dataMapsJaxb.setDataMapList(listOfDataMaps);
        return dataMapsJaxb;
    }

    public static Parameters prepareParameters(JsonObject dataMapJSON) {
        JsonArray parameters = GsonUtility.optJsonArray(dataMapJSON,"parameters");
        Parameters parametersJaxb = null;
        List<Parameter> listOfParameter = new ArrayList<>();
        if (parameters != null && !parameters.isEmpty()) {
            parametersJaxb = ApplicationContextAccessor.getBean(Parameters.class);
            for (int index = 0; index < parameters.size(); index++) {
                JsonObject parameterJSON = parameters.get(index).getAsJsonObject().getAsJsonObject("parameter");
                Parameter parameterJaxb = ApplicationContextAccessor.getBean(Parameter.class);
                parameterJaxb.setType(parameterJSON.get("type").getAsString());
                parameterJaxb.setName(parameterJSON.get("name").getAsString());
                parameterJaxb.setDefaultValue(parameterJSON.get("default").getAsString());
                if (parameterJSON.has("openQuote") && parameterJSON.has("closeQuote")) {
                    parameterJaxb.setOpenQuote(parameterJSON.get("openQuote").getAsString());
                    parameterJaxb.setCloseQuote(parameterJSON.get("closeQuote").getAsString());
                }
                listOfParameter.add(parameterJaxb);
            }
            parametersJaxb.setParameter(listOfParameter);
        }
        return parametersJaxb;
    }

    public static DataSourcesForCe prepareDataSource(JsonObject efwdJsonObject) {
        JsonObject dataSourcesJSON = GsonUtility.optJsonObject(efwdJsonObject,"dataSources");
        if (dataSourcesJSON == null) {
            return null;
        }
        DataSourcesForCe dataSourceJaxb = ApplicationContextAccessor.getBean(DataSourcesForCe.class);
        JsonArray connectionsJSONArray = dataSourcesJSON.getAsJsonArray("connections");
        List<ConnectionForCe> listOfConnection = new ArrayList<>();
        for (int index = 0; index < connectionsJSONArray.size(); index++) {
            ConnectionForCe connectionJaxb = ApplicationContextAccessor.getBean(ConnectionForCe.class);
            JsonObject connectionJSON = connectionsJSONArray.get(index).getAsJsonObject().getAsJsonObject("connection");
            JsonObject connDetailsJSON = connectionJSON.getAsJsonObject("connDetails");
            if (connDetailsJSON.has("globalId")) {
                connectionJaxb.setGlobalId(connDetailsJSON.get("globalId").getAsString());
            }else if (connDetailsJSON.has("efwdId")) {
                connectionJaxb.setEfwdId(connDetailsJSON.get("efwdId").getAsString());
            } else if (connDetailsJSON.has("metadataFileName") && connDetailsJSON.has("location")) {
                connectionJaxb.setLocation(connDetailsJSON.get("location").getAsString());
                connectionJaxb.setMetadataFileName(connDetailsJSON.get("metadataFileName").getAsString());
            } else {
                connectionJaxb.setDriver(connDetailsJSON.get("driver").getAsString());
                connectionJaxb.setUrl(connDetailsJSON.get("url").getAsString());
                connectionJaxb.setUser(connDetailsJSON.get("user").getAsString());
                connectionJaxb.setPass(connDetailsJSON.get("pass").getAsString());
            }
            connectionJaxb.setId(connectionJSON.get("id").getAsString());
            connectionJaxb.setType(connectionJSON.get("type").getAsString());
            if (connectionJaxb.getSecurity() == null) {
                connectionJaxb.setSecurity(SecurityUtils.securityObject());
            }
            if (connDetailsJSON.has("condition"))
                connectionJaxb.setCondition(connDetailsJSON.get("condition").getAsString());
            listOfConnection.add(connectionJaxb);
        }
        dataSourceJaxb.setConnectionList(listOfConnection);
        return dataSourceJaxb;
    }

    public static EfwdForCe moveEfwdFromTempToReal(String temp_uuid, String dir, String uuid, String efwdExtension) {
        File temporaryEfwdFile = new File(TempDirectoryCleaner.getTempDirectory() + File.separator + temp_uuid + "." + efwdExtension);
        EfwdForCe efwd = null;
        if (temporaryEfwdFile.exists()) {
            efwd = JaxbUtils.unMarshal(EfwdForCe.class, temporaryEfwdFile);
            File efwdFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                    .separator + uuid + "." + efwdExtension);
            try {
                synchronized (EFWCEUtils.class) {
                    JaxbUtils.marshal(efwd, efwdFile);
                }
            } catch (Exception e) {
                throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                        "" + "" + "Reason is ", e);
            }
            temporaryEfwdFile.delete();
        }
        return efwd;
    }
}
