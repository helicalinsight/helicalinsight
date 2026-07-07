package com.helicalinsight.efwce;

import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EFWCEUtilsDb {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    public static EfwdForCe saveEfwd(JSONObject efwdJsonObject, String dir, String uuid, String efwdExtension) {
        EfwdForCe efwdJaxb = prepareEfwd(efwdJsonObject);
        File efwdFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + efwdExtension);
        try {
            synchronized (EFWCEUtilsDb.class) {
                JaxbUtils.marshal(efwdJaxb, efwdFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
        return efwdJaxb;
    }

    public static boolean saveTempEfwd(JSONObject efwdJsonObject, String dir, String uuid, String efwdExtension) {
        EfwdForCe efwdJaxb = prepareEfwd(efwdJsonObject);
        File tempEfwdLoc = new File(dir + File
                .separator + uuid + "." + efwdExtension);
        try {
            synchronized (EFWCEUtilsDb.class) {
                JaxbUtils.marshal(efwdJaxb, tempEfwdLoc);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
        return tempEfwdLoc.exists();
    }

    public static EfwdForCe prepareEfwd(JSONObject efwdJsonObject) {
        EfwdForCe efwdJaxb = ApplicationContextAccessor.getBean(EfwdForCe.class);
        efwdJaxb.setDataSources(prepareDataSource(efwdJsonObject));
        efwdJaxb.setDataMaps(prepareDataMaps(efwdJsonObject));
        return efwdJaxb;
    }

    public static DataMaps prepareDataMaps(JSONObject efwdJsonObject) {
        JSONArray dataMapsJSONArray = efwdJsonObject.optJSONArray("dataMaps");
        DataMaps dataMapsJaxb = ApplicationContextAccessor.getBean(DataMaps.class);
        if (dataMapsJSONArray == null || dataMapsJSONArray.isEmpty()) {
            return null;
        }
        List<DataMap> listOfDataMaps = new ArrayList<>();
        for (int index = 0; index < dataMapsJSONArray.size(); index++) {
            JSONObject dataMap = dataMapsJSONArray.getJSONObject(index).getJSONObject("dataMap");
            DataMap dataMapJaxb = ApplicationContextAccessor.getBean(DataMap.class);
            dataMapJaxb.setId(dataMap.getString("id"));
            dataMapJaxb.setName(dataMap.getString("name"));
            dataMapJaxb.setType(dataMap.getString("type"));
            dataMapJaxb.setConnection(dataMap.getString("connection"));
            dataMapJaxb.setQuery(dataMap.getString("query"));
            Parameters parameters = prepareParameters(dataMap);
            if (parameters != null)
                dataMapJaxb.setParameters(parameters);
            listOfDataMaps.add(dataMapJaxb);
        }
        dataMapsJaxb.setDataMapList(listOfDataMaps);
        return dataMapsJaxb;
    }

    public static Parameters prepareParameters(JSONObject dataMapJSON) {
        JSONArray parameters = dataMapJSON.optJSONArray("parameters");
        Parameters parametersJaxb = null;
        List<Parameter> listOfParameter = new ArrayList<>();
        if (parameters != null && !parameters.isEmpty()) {
            parametersJaxb = ApplicationContextAccessor.getBean(Parameters.class);
            for (int index = 0; index < parameters.size(); index++) {
                JSONObject parameterJSON = parameters.getJSONObject(index).getJSONObject("parameter");
                Parameter parameterJaxb = ApplicationContextAccessor.getBean(Parameter.class);
                parameterJaxb.setType(parameterJSON.getString("type"));
                parameterJaxb.setName(parameterJSON.getString("name"));
                parameterJaxb.setDefaultValue(parameterJSON.getString("default"));
                if (parameterJSON.has("openQuote") && parameterJSON.has("closeQuote")) {
                    parameterJaxb.setOpenQuote(parameterJSON.getString("openQuote"));
                    parameterJaxb.setCloseQuote(parameterJSON.getString("closeQuote"));
                }
                listOfParameter.add(parameterJaxb);
            }
            parametersJaxb.setParameter(listOfParameter);
        }
        return parametersJaxb;
    }

    public static DataSourcesForCe prepareDataSource(JSONObject efwdJsonObject) {
        JSONObject dataSourcesJSON = efwdJsonObject.optJSONObject("dataSources");
        if (dataSourcesJSON == null) {
            return null;
        }
        DataSourcesForCe dataSourceJaxb = ApplicationContextAccessor.getBean(DataSourcesForCe.class);
        JSONArray connectionsJSONArray = dataSourcesJSON.getJSONArray("connections");
        List<ConnectionForCe> listOfConnection = new ArrayList<>();
        for (int index = 0; index < connectionsJSONArray.size(); index++) {
            ConnectionForCe connectionJaxb = ApplicationContextAccessor.getBean(ConnectionForCe.class);
            JSONObject connectionJSON = connectionsJSONArray.getJSONObject(index).getJSONObject("connection");
            JSONObject connDetailsJSON = connectionJSON.getJSONObject("connDetails");
            if (connDetailsJSON.has("globalId")) {
                connectionJaxb.setGlobalId(connDetailsJSON.getString("globalId"));
            } else if (connDetailsJSON.has("metadataFileName") && connDetailsJSON.has("location")) {
                connectionJaxb.setLocation(connDetailsJSON.getString("location"));
                connectionJaxb.setMetadataFileName(connDetailsJSON.getString("metadataFileName"));
            } else {
                connectionJaxb.setDriver(connDetailsJSON.getString("driver"));
                connectionJaxb.setUrl(connDetailsJSON.getString("url"));
                connectionJaxb.setUser(connDetailsJSON.getString("user"));
                connectionJaxb.setPass(connDetailsJSON.getString("pass"));
            }
            connectionJaxb.setId(connectionJSON.getString("id"));
            connectionJaxb.setType(connectionJSON.getString("type"));
            if (connectionJaxb.getSecurity() == null) {
                connectionJaxb.setSecurity(SecurityUtils.securityObject());
            }
            if (connDetailsJSON.has("condition"))
                connectionJaxb.setCondition(connDetailsJSON.getString("condition"));
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
                synchronized (EFWCEUtilsDb.class) {
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
