package com.helicalinsight.datasource;
import net.sf.json.JSONObject;
import org.apache.commons.text.StringEscapeUtils;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.datasource.managed.GlobalXmlReader;
import com.helicalinsight.datasource.managed.IGlobalXmlReader;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.datasource.utils.EFWDDBHandler;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.EfwdDataSourceHandler;
import com.helicalinsight.efw.components.NoSqlDataSourceProperties;
import com.helicalinsight.efw.components.SqlJdbcHandler;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.DatabaseConnectionFailedException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.NoSqlUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * DataSourceUtils
 * Utility class containing various methods for managing data sources and related operations.
 * <p>
 * This class provides methods for creating, updating, deleting, and testing data sources. It also includes methods
 * for gathering information about data sources, generating JSON representations, and performing connection tests.
 * </p>
 * <p>
 * Additionally, this class contains methods for handling metadata retrieval configurations and connections
 * for workflow processes.
 * </p>
 * Created by author on 08-09-2015.
 * @author Rajasekhar
 */
public class DataSourceUtils {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceUtils.class);
    
    static Map<String,String> dsTypeMap;
    static {
    	dsTypeMap = new HashMap<>();
    	dsTypeMap.put("jndi",GlobalJdbcType.STATIC_DATASOURCE);
    	dsTypeMap.put("tomcat",GlobalJdbcType.DYNAMIC_DATASOURCE);
    	dsTypeMap.put("noSql",GlobalJdbcType.NOSQL_DATASOURCE);
    	dsTypeMap.put("hikari",GlobalJdbcType.DYNAMIC_DATASOURCE);
    	dsTypeMap.put("calcite",GlobalJdbcType.DYNAMIC_DATASOURCE);
    }

	/**
	 * globalIdJson(int id)
	 * Returns a JSON representation of a global data source configuration based on its unique identifier (ID).
	 * <p>
	 * This method fetches the global data source configuration corresponding to the provided ID and returns it as
	 * a JSON string. The data source configuration includes information such as type, driver, and connection details.
	 * </p>
	 *
	 * @param id 						unique identifier (ID) of the global data source to retrieve.
	 * @return A JSON string representing the global data source configuration.
	 */
    public static String globalIdJson(int id) {
        IGlobalXmlReader iGlobalXmlReader = ApplicationContextAccessor.getBean(GlobalXmlReader.class);
        Boolean isDsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
        //XML
        if (isDsTypeStorageDatabase && id >= 1) {
            iGlobalXmlReader = ApplicationContextAccessor.getBean(GlobalDsGlobalIdFinder.class);
        }

        return iGlobalXmlReader.getDataSourceJson(id);
    }
	/**
	 *
	 * getConnectionJson(JsonObject formJson)
	 * @param formJson           fromData
	 * @return jsonObject with datasource connection details
	 */
	public static JsonObject getConnectionJson(JsonObject formJson) {
        // isDataSourceAuthenticated(formJson);
		String id = formJson.get("id").getAsString();
		String dir = GsonUtility.optStringValue(formJson,"dir","");
		String type = formJson.get("type").getAsString();
        //IProcessor processor = ResourceProcessorFactory.getIProcessor();
        if (!dir.isEmpty() ) {
            EFWDDBHandler handler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
            Object dataSource = null;
            try {
            	dataSource = handler.readEFWDConnection(Integer.valueOf(id), type);
            }
            catch (Exception e) {
				e.printStackTrace();
			}
            if(dataSource != null) {
            	return makeEfwdConnection(dataSource,type);
				
			}
            return null;
        }
        
         else {
            // call the other one with id
			return new Gson().fromJson(globalIdJson(Integer.parseInt(id)),JsonObject.class);
        }

    }


	/**
	 * makeEfwdConnection(Object dataSource, String type)
	 * @param dataSource        data for EFWD connection
	 * @param type              data source type
	 * @return jsonObject with connection details
	 */
	public static JsonObject makeEfwdConnection(Object dataSource, String type) {
		
			PlainConnDTO connection = (PlainConnDTO) dataSource;
			EfwdDataSourceHandler efwdHandler = new SqlJdbcHandler();
			EfwdConnection conn = efwdHandler.getEfwdConnection(connection.getUrl(), connection.getUserName(),
					connection.getPass(), connection.getDriver());
			JsonObject connectionJson = new JsonObject();
			if(conn.getDriver() != null) {
				connectionJson.addProperty("Driver", conn.getDriver());
			}
			if(conn.getUrl() != null) {
				connectionJson.addProperty("url", conn.getUrl());
			}
			if(connection.getDatabase() != null) {
				connectionJson.addProperty("databaseName", connection.getDatabase());
			}
			return connectionJson;
	}
	/**
	 * addDataObj(String type, String directory, String driverName, ObjectNode model, String maxId)
	 * Adds data parameters to an ObjectNode for creating a data source configuration.
	 * @param type              data source type
	 * @param directory         directory or location of the data source
	 * @param driverName        name of the driver
	 * @param model             instance of objectNode to store data
	 * @param maxId             id
	 */
	public static void addDataObj(String type, String directory, String driverName, ObjectNode model, String maxId) {
        ObjectNode requiredForDataSource = JsonNodeFactory.instance.objectNode();
        requiredForDataSource.put("id", maxId);
        requiredForDataSource.put("dir", directory);
        ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceOfActiveUser allResources = hiResourceServiceDB.getResourceOfActiveUser();
        Map<String, Object> resourcePermission = allResources.getResourcePermission();
        if(null == resourcePermission.get(directory)) {
        	throw new EfwServiceException("Access Denied. You don't have sufficient privileges to access  the requested resource");
        }
        Integer o =  Integer.valueOf(""+resourcePermission.get(directory));
        requiredForDataSource.put("isPublic", o == resourcePermissionLevelsHolder.publicResourceAccessLevel());
        requiredForDataSource.put("type", type);
        if (driverName!=null&&!driverName.isEmpty()) {
            requiredForDataSource.put("driver", driverName);
        }
        model.set("data", requiredForDataSource);
    }

	/**
	 * provideData(JsonObject formData, String id)
	 * Creates a JsonObject containing necessary data for configuring a data source.
	 * @param formData			containing data source configuration information
	 * @param id				id
	 * @return JsonObject with the required data for configuring the data source.
	 */
	public static JsonObject provideData(JsonObject formData, String id) {

		JsonObject requiredJson = new JsonObject();
		requiredJson.addProperty("name",GsonUtility.optString(formData,"name"));//accumulate

		requiredJson.addProperty("id", id);//accumulate
		String type = dsTypeMap.get(formData.get("dataSourceProvider").getAsString());
		requiredJson.addProperty("type", type);//accumulate

        if (formData.has("driverName")) {
			requiredJson.addProperty("driver", formData.get("driverName").getAsString());//accumulate
        }

        /* Bug id 3706, 3608,3609
         if (formData.has("dataSourceProvider")) {
            requiredJson.accumulate("dataSourceProvider", formData.getString("dataSourceProvider"));
        }*/

        return requiredJson;
    }
	/**
	 * getDatabaseDialect(JsonObject formData)
     * Generates a JSON representation of a database dialect based on the provided formData.
     *
     * @param formDataJson 				JSON object containing data source configuration.
     * @return The JSON representation of the database dialect.
     */

	public static String getDatabaseDialect(JsonObject formData) {
		return formData.has("databaseDialect") ? formData.get("databaseDialect").getAsString() : "";

    }
	/**
	 * testNosqlDS(JsonObject formDataJson)
     * Tests the connection to a NoSQL data source based on the provided formData.
     *
     * @param formDataJson 				JSON object containing data source configuration.
     * @return A result message indicating the success or failure of the connection test.
     */
	public static String testNosqlDS(JsonObject formDataJson) {
        String subType = NoSqlDataSourceProperties.getSubType(formDataJson);
        NoSQLLoader noSqlImplementation = NoSqlUtils.getNoSqlImplementation(subType);
        return connectionTestResultBuilder(noSqlImplementation.testConnection(formDataJson));
    }
	/**
	 * connectionTestResultBuilder(boolean isConnectionSuccessful)
     * Builds a connection test result message based on the success of a connection test.
     *
     * @param isConnectionSuccessful 			Indicates whether the connection test was successful.
     * @return A result message indicating the success or failure of the connection test.
     */
    public static String connectionTestResultBuilder(boolean isConnectionSuccessful) {
		JsonObject model = new JsonObject();

        if (!isConnectionSuccessful) {
            throw new DatabaseConnectionFailedException("The data source details provided are incorrect. Could " + "not get database " +
                    "connection.");
        } else {
			model.addProperty("message", "The connection test is successful.");
        }
        return model.toString();
    }
	/**
	 * validate(@Nullable String param)
     * Validates that the given parameter is not null or empty.
     *
     * @param param 				parameter to validate.
     * @throws RequiredParameterIsNullException If the parameter is null or empty.
     */
    public static void validate(@Nullable String param) {
        if (param == null || "".equals(param) || "".equals(param.trim())) {
            throw new RequiredParameterIsNullException("The parameter " + param + " is null or " +
                    "empty.");
        }
    }

	/**
	 * addExtraDataForWorkflowProcess()
	 * addExtraDataForWorkflowProcess using gson
	 * Adds extra data required for a workflow process to the given JsonObject.
     *
	 * @param JsonObject formJson               JsonObject representing the data source configuration.
	 * @param String     dataSourceType			type of the data source.
	 */
	public static void addExtraDataForWorkflowProcess(@NotNull JsonObject formJson, String dataSourceType) {
        if (dataSourceType == null) {
            throw new IllegalArgumentException("The argument dataSourceType is null");
        }

		JsonObject settingsJson = com.helicalinsight.efw.utility.JsonUtils.newGetSettingsJson();
		JsonArray metadataImplementations = settingsJson.getAsJsonObject("workflowImplementations").getAsJsonArray("metadata");
        accumulateConfiguration(formJson, dataSourceType, metadataImplementations);
    }


	/**
	 * accumulateConfiguration(JsonObject formJson, String dataSourceType,
			JsonArray metadataImplementations)
	 * Accumulates configuration data to the given JsonObject for metadata retrieval.
	 * @param JsonObject formJson				   JsonObject to accumulate configuration data into
	 * @param String dataSourceType				   type of the data source.
	 * @param JsonArray metadataImplementations	   metadata information
	 */
	public static void accumulateConfiguration(JsonObject formJson, String dataSourceType,
			JsonArray metadataImplementations) {
        MetadataUtility metadataUtility = MetadataUtils.getMetadataUtility(dataSourceType, metadataImplementations);

        if (logger.isDebugEnabled()) {
            logger.debug("Debug: " + metadataUtility);
        }

        if (metadataUtility == null) {
            throw new ConfigurationException(String.format("There is no metadata retrieval " +
                    "configuration for the type %s. Configure the application properly. " +
                    "Check for typos.", dataSourceType));
        }

        String connectionProvider = metadataUtility.getConnectionProvider();
        String metadataImplementation = metadataUtility.getMetadataImplementation();
        String classifier = metadataUtility.getType();

        Map<String, String> parameters = new HashMap<>();

        parameters.put("classifier", classifier);
        parameters.put("connectionProvider", connectionProvider);
        parameters.put("metadataImplementation", metadataImplementation);

        try {
            ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        } catch (Exception exception) {
            throw new ConfigurationException("The application settings are wrongly " +
                    "configured" + ".", exception);
        }

		formJson.addProperty("classifier", classifier);//accumulate
		formJson.addProperty("connectionProvider", connectionProvider);//accumulate
		formJson.addProperty("metadataImplementation", metadataImplementation);//accumulate
    }

	/**
	 * (Map<String, Object> formMap, String dataSourceType,
			JsonArray metadataImplementations)
     * Accumulates configuration data to the given formMap for metadata retrieval in a database-backed scenario.
     *
     * @param formMap                  formMap to adds configuration data into.
     * @param dataSourceType           The type of the data source.
     * @param metadataImplementations  metadata info .
     */
    public static void accumulateConfigurationDB(Map<String,Object> formMap, String dataSourceType,
			JsonArray metadataImplementations) {
        MetadataUtility metadataUtility = MetadataUtils.getMetadataUtility(dataSourceType, metadataImplementations);

        if (logger.isDebugEnabled()) {
            logger.debug("Debug: " + metadataUtility);
        }

        if (metadataUtility == null) {
			throw new ConfigurationException(String.format("There is no metadata retrieval "
					+ "configuration for the type %s. Configure the application properly. " + "Check for typos.",
					dataSourceType));
        }

        String connectionProvider = metadataUtility.getConnectionProvider();
        String metadataImplementation = metadataUtility.getMetadataImplementation();
        String classifier = metadataUtility.getType();

        Map<String, String> parameters = new HashMap<>();

        parameters.put("classifier", classifier);
        parameters.put("connectionProvider", connectionProvider);
        parameters.put("metadataImplementation", metadataImplementation);

        try {
            ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        } catch (Exception exception) {
            throw new ConfigurationException("The application settings are wrongly " +
                    "configured" + ".", exception);
        }

        formMap.put("classifier", classifier);
        formMap.put("connectionProvider", connectionProvider);
        formMap.put("metadataImplementation", metadataImplementation);
    }
    
    
    public static  String updateFlatFileUrl(String url , String fileName) {
    	if ( StringUtils.isBlank(fileName)  ) {
    		return url;
    	}
        int start = url.lastIndexOf(":");
        int end = url.indexOf("?");
        if ( end == -1 ) {
            end = url.length();
        }
        StringBuilder builder = new StringBuilder(url);
        builder.replace(start+1,end,fileName);
        return builder.toString();
    }
    
    public static void moveFile(String tempFileName , String dataFile , String connectionId) {
    	String tempDir  =  TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
    	File from = new File(String.join(File.separator, tempDir, tempFileName));
    	File destinationDirectory = Paths.get(ApplicationProperties.getInstance().getFlatFilesPath(),connectionId).toFile();
    	if (!destinationDirectory.exists()) {
    		destinationDirectory.mkdirs();
    	}
		
    	//--remove existing files BUG- 7574
    	File[] flatfiles = destinationDirectory.listFiles();
		if (flatfiles != null) {
			for (File file : flatfiles) {
				if(!file.getName().equals(dataFile))
				file.delete();
			}
		}
    	
    	File to = Paths.get(destinationDirectory.getAbsolutePath(),dataFile).toFile();
        if(from.isFile()) {
            FileUtils.move(from, to);
        }
    }
    
	public static Map<String, String> getExtraOptions(String json, String globalId) {

		JSONObject formData = JSONObject.fromObject(json);

		String extraOptions = formData.optString("extraOptions");

		if (StringUtils.isBlank(extraOptions)) {
			if (StringUtils.isNotBlank(globalId)) {
				GlobalConnectionService service = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
				return service.getExtraOption(Integer.parseInt(globalId));
			}
			return Collections.emptyMap();
		} else {
			return JacksonUtility.mapToMap(json);
		}
	}

	public static String getFileName(String fileName, Map<String, String> extraOptions, String connectionId) {
		if (StringUtils.isBlank(fileName)) {
			String dataFileName = extraOptions.get("dataFile");
			if (StringUtils.isNotBlank(connectionId) && StringUtils.isNotBlank(dataFileName)) {
				dataFileName = dataFileName.replace("\"", "");
				fileName = String.join(File.separator, ApplicationProperties.getInstance().getFlatFilesPath(),
						connectionId, dataFileName);
			} else {
				return "";
			}
		} else {
			fileName = TempDirectoryCleaner.getTempDirectory() + File.separator + fileName;
		}
		return fileName;
	}
	
	
	public static final void loadMapToProperties(Properties properties, Map<String, String> map) {
		map.entrySet().forEach(entry -> {
			
			String key = entry.getKey();
			String value = entry.getValue();
			
			if ("config".equalsIgnoreCase(key)) {
				spreadConfigObject(value, properties);
			} else if ("script".equalsIgnoreCase(key)) {
				if (StringUtils.isNoneBlank(value)) {
					String encrypted = Base64.encodeBase64String(value.getBytes());
					properties.put(key, encrypted);
				}
			}
			else {
				properties.put(key, value);
			}
		});
	}
	
	public static final void spreadConfigObject(String value, Properties properties) {
		JSONObject valueNode = JSONObject.fromObject(value);
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = valueNode.keys();
		while (iterator.hasNext()) {
			String key = iterator.next();
			properties.put(key, valueNode.getString(key));
		}
	}
	
	public static String buildConnectionProperties(Map<String, String> map) {
		Properties props  = new Properties();
		loadMapToProperties(props, map);
		StringBuilder connectionPropertyBuilder = new StringBuilder();
		if (!props.isEmpty() ) {
			props.entrySet().stream()
				.forEach(prop ->  {
					connectionPropertyBuilder.append(prop.getKey()+"="+prop.getValue());
					connectionPropertyBuilder.append(";");
				});
			return connectionPropertyBuilder.substring(0,connectionPropertyBuilder.length()-1);
		}
		return  "";
	}

}
