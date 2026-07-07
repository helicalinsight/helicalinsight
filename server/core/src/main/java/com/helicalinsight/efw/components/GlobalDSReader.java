package com.helicalinsight.efw.components;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.GlobalConnectionDTO;
import com.helicalinsight.datasource.model.*;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by author on 05-02-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
//TODO need to check the formdata
public class GlobalDSReader extends GlobalXmlReader {
    private static final Logger logger = LoggerFactory.getLogger(GlobalDSReader.class);

    @Override
    public String executeComponent(String formData) {
        logger.info(formData.toString());
        Boolean isDsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
        Boolean dsMigrationIsEnabled = JsonUtils.isDSMigrationIsEnabled();
        //XML
        if (!isDsTypeStorageDatabase) {
            return super.executeComponent(formData);
        }

        GlobalConnectionService globalConnectionService = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
        JSONObject formDataJson = JSONObject.fromObject(formData);
        String type = formDataJson.getString("type");
        String id = formDataJson.getString("id");
        int globalId = Integer.parseInt(id);

        GlobalConnections globalConnectionById = globalConnectionService.findGlobalConnectionById(globalId);
        if(globalConnectionById == null) {
            throw new EfwServiceException("Global/Managed connection with the provided id not found.");
        }
        GlobalConnectionDTO globalConnectionDTO = GlobalConnectionDTOHelper.setGlobalConnectionDTO(globalConnectionById);
        Map<String,String> extraOptions =  globalConnectionService.getExtraOption(globalId);
        globalConnectionDTO.setExtraOptions(extraOptions);
        logger.debug("globalConnections"+globalConnectionDTO);

        String dsType = globalConnectionById.getDsType();
        logger.debug("DSType",dsType);
        JSONObject jsonObject = new JSONObject();

        if (dsType.equalsIgnoreCase(JsonUtils.getDSType(DSTypePlainJDBC.class.getName()))) {
            DSTypePlainJDBC object = globalConnectionService.getPlainJDBCConnectionById(globalId);
            logger.debug("DSTypePlainJDBC"+object);
            globalConnectionDTO.setDsType(GlobalConnectionDTOHelper.setPlainJDBCDTO(object));
        } else if (dsType.equalsIgnoreCase(JsonUtils.getDSType(DSTypeTomcat.class.getName()))) {
            DSTypeTomcat dsTypeTomcat = globalConnectionService.getTomcatConnectionById(globalId);
            logger.debug("DSTypeTomcat"+dsTypeTomcat);
            globalConnectionDTO.setDsType(GlobalConnectionDTOHelper.setTomcatDTO(dsTypeTomcat));
        } else if (dsType.equalsIgnoreCase(JsonUtils.getDSType(DSTypeNoSQL.class.getName()))) {
            DSTypeNoSQL object = globalConnectionService.getNoSQLConnectionById(globalId);
            logger.debug("DSTypeNoSql"+object);
            globalConnectionDTO.setDsType(GlobalConnectionDTOHelper.setNoSqlDTO(object));
        } else if (dsType.equalsIgnoreCase(JsonUtils.getDSType(DSTypeHikari.class.getName()))) {
            DSTypeHikari object = globalConnectionService.getHikariConnectionById(globalId);
            logger.debug("DSTypeHikari"+object);
            globalConnectionDTO.setDsType(GlobalConnectionDTOHelper.setHikariDTO(object));
        } else if (dsType.equalsIgnoreCase(JsonUtils.getDSType(DSTypeJndi.class.getName()))) {
            DSTypeJndi object = globalConnectionService.getJndiConnectionById(globalId);
            logger.debug("DSTypeJndi"+object);
            globalConnectionDTO.setDsType(GlobalConnectionDTOHelper.setJndiDTO(object));
        }

        JSONObject response = JSONObject.fromObject(globalConnectionDTO);
        logger.debug("response",response);
        JSONObject dsTypeJson = response.getJSONObject("dsType");
        logger.debug("dsTypeJson",response);
        response.discard("dsType");
        dsTypeJson.discard("id");
        dsTypeJson.discard("lastUpdatedTime");
        response.putAll(dsTypeJson);
        JSONObject fillData = fillData(response);
        fillData.discard("id");
        fillData.discard("username");
        fillData.discard("url");
        fillData.discard("createdBy");
        fillData.discard("createdDate");
        fillData.discard("share");
        fillData.discard("dsTypeClass");
        fillData.discard("isMigrated");
        fillData.discard("lastUpdatedTime");
        fillData.discard("globalConnections");
        return fillData.toString();
    }


    private JSONObject fillData(JSONObject datasource) {
        logger.debug("datasource",datasource);
        JSONObject aDataSource = new JSONObject();
        Iterator keys = datasource.keys();
        while (keys.hasNext()) {
            //TODO change this
            String key = keys.next().toString();
            if (key.equalsIgnoreCase("id")) {
                aDataSource.put("@id", datasource.get(key).toString());
            } else if (key.equalsIgnoreCase("userName")) {
                aDataSource.put("userName", datasource.get(key));
            } else if (key.equalsIgnoreCase("password")) {
                String decryptedPWD = CipherUtils.decrypt(datasource.get(key).toString());
                aDataSource.put("password", decryptedPWD);
            } else if (key.equalsIgnoreCase("jdbcUrl") || key.equalsIgnoreCase("url")) {
                aDataSource.put("jdbcUrl", datasource.get(key));
            } else if (key.equalsIgnoreCase("driverClassName")) {
                aDataSource.put("driverName", datasource.get(key));
            } else if (key.equalsIgnoreCase("globalId")) {
                aDataSource.put("@id", datasource.get("globalId").toString());
            } else if (key.equalsIgnoreCase("name")) {
                aDataSource.put("@name", datasource.get("name"));
            }
            else if (key.equalsIgnoreCase("type")) {
                aDataSource.put("@type", datasource.get("type"));
            }
            else if (key.equalsIgnoreCase("baseType")) {
                aDataSource.put("@baseType", datasource.get("baseType"));
            }else if(key.equalsIgnoreCase("databaseDialect")){
                if(datasource.getString("databaseDialect").equals(""))
                    aDataSource.put("databaseDialect",new JSONArray());
            }
            else if(key.equalsIgnoreCase("forceAlternateUsername")){
                aDataSource.put("forceAlternateUsername",datasource.getString("forceAlternateUsername"));
            }else if(key.equalsIgnoreCase("initialSize")){
                aDataSource.put("initialSize",datasource.getString("initialSize"));
            }else if(key.equalsIgnoreCase("jmxEnabled")){
                aDataSource.put("jmxEnabled",datasource.getString("jmxEnabled"));
            }else if(key.equalsIgnoreCase("logAbandoned")){
                aDataSource.put("logAbandoned",datasource.getString("logAbandoned"));
            }else if(key.equalsIgnoreCase("maxActive")){
                aDataSource.put("maxActive",datasource.getString("maxActive"));
            }else if(key.equalsIgnoreCase("maxWait")){
                aDataSource.put("maxWait",datasource.getString("maxWait"));
            }else if(key.equalsIgnoreCase("minEvictableIdleTimeMillis")){
                aDataSource.put("minEvictableIdleTimeMillis",datasource.getString("minEvictableIdleTimeMillis"));
            }else if(key.equalsIgnoreCase("minIdle")){
                aDataSource.put("minIdle",datasource.getString("minIdle"));
            }else if(key.equalsIgnoreCase("removeAbandoned")){
                aDataSource.put("removeAbandoned",datasource.getString("removeAbandoned"));
            }else if(key.equalsIgnoreCase("removeAbandonedTimeout")){
                aDataSource.put("removeAbandonedTimeout",datasource.getString("removeAbandonedTimeout"));
            }else if(key.equalsIgnoreCase("testOnBorrow")){
                aDataSource.put("testOnBorrow",datasource.getString("testOnBorrow"));
            }else if(key.equalsIgnoreCase("testOnReturn")){
                aDataSource.put("testOnReturn",datasource.getString("testOnReturn"));
            }else if(key.equalsIgnoreCase("testWhileIdle")){
                aDataSource.put("testWhileIdle",datasource.getString("testWhileIdle"));
            }else if(key.equalsIgnoreCase("timeBetweenEvictionRunsMillis")){
                aDataSource.put("timeBetweenEvictionRunsMillis",datasource.getString("timeBetweenEvictionRunsMillis"));
            }else if(key.equalsIgnoreCase("validationInterval")){
                aDataSource.put("validationInterval",datasource.getString("validationInterval"));
            }else if(key.equalsIgnoreCase("visible")){
                aDataSource.put("visible",datasource.getString("visible"));
            }else if(key.equalsIgnoreCase("maximumPoolSize")){
                aDataSource.put("maximumPoolSize",datasource.getString("maximumPoolSize"));
            }else if(key.equalsIgnoreCase("minimumIdle")){
                aDataSource.put("minimumIdle",datasource.getString("minimumIdle"));
            }else if(key.equalsIgnoreCase("maxLifetime")){
                aDataSource.put("maxLifetime",datasource.getString("maxLifetime"));
            }else {
                aDataSource.put(key, datasource.get(key));
            }
        }
        aDataSource.discard("maxPermission");
        aDataSource.discard("migrated");
        return aDataSource;
    }
}
