package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.IQueryGenerator;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.AbstractSecurityRules;
import com.helicalinsight.adhoc.genericsql.AdhocQuerySecurityCheckService;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.HISparkContext;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code DatabaseQueryGenerator} class is an implementation of the {@link IComponent} interface,
 * designed to generate queries based on the provided form data and metadata information.
 *
 * @author Rajasekhar
 * Created by author on 08-03-2015.
 */
public class DatabaseQueryGenerator implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseQueryGenerator.class);

    private AdhocQuerySecurityCheckService adhocQuerySecurityCheckService;
    /**
     * Method generates query is then returned as a JSON-formatted string.
     *
     * @param formData 		 form data containing parameters for generating query.
     * @return  string containing the generated query.
     */
    @Override
    public String executeComponent(String formData) {
        JsonObject formDataJson = JsonParser.parseString(formData).getAsJsonObject();

        FormDataDefaultValuesRuleHandler.setDefaultValuesInFormData(formDataJson);
        //FormDataDefaultValuesRuleHandler.ignoreFilter(formDataJson);

        String queryGeneratorImplementation = formDataJson.get("queryGeneratorImplementation").getAsString();
        JsonObject metadataFileJson1 = formDataJson.getAsJsonObject("metadataFileJson");
        JsonObject connectionDetails = metadataFileJson1.getAsJsonObject("connectionDetails");
        String cacheMode = GsonUtility.optString(connectionDetails,"fetchMode");
        if ("cache".equalsIgnoreCase(cacheMode)) {
            //  metadataFileJson1.getJSONObject("database").put("name","derby");
            JsonObject driverClass = connectionDetails.getAsJsonObject("driverClass");
            driverClass.addProperty("reference", "spark");

            formDataJson.addProperty("driverClass", HISparkContext.getDriverClass());
        }
        boolean isCached = GsonUtility.optBooleanValue(metadataFileJson1,"isCached",false);
        if (isCached && AppStatistics.isMASTER_STARTED() && AppStatistics.isSPARK_STARTED()) {
            JsonObject driverClass = connectionDetails.getAsJsonObject("driverClass");
            driverClass.addProperty("reference", "default");
            modifyFormDataForSpark(formDataJson);
        }
        Object object = formDataJson.get("metadataFileJson");
        String metadataFileJson = new Gson().toJson(object);

        IQueryGenerator queryGenerator = FactoryMethodWrapper.getTypedInstance(queryGeneratorImplementation,
                IQueryGenerator.class);

        if (queryGenerator == null) {
            throw new ConfigurationException("The application configuration(setting.xml) is " + "incorrect.");
        }

        //Karthik Security Related Changes
        /*adhocQuerySecurityCheckService = new AdhocQuerySecurityCheckService();
        try {
            Metadata metadata = JaxbUtils.jsonStringToObject(Metadata.class, metadataFileJson);
            formDataJson = adhocQuerySecurityCheckService.check_for_custom_columns(formDataJson,metadata);
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }*/
        Map<String,String> propsMap=ApplicationUtilities.getDefaultsMap();
        for(Map.Entry<String,String> entryMap:propsMap.entrySet()){
            if(entryMap.getKey().startsWith("$.")) {
                String key = entryMap.getKey();
                String value = entryMap.getValue();
                String className= AbstractSecurityRules.getAdhocSecurityRulesClass(value);
                try {
                    Metadata metadata = JaxbUtils.jsonStringToObject(Metadata.class, metadataFileJson);
                    if(ApplicationUtilities.isClass(className)){
                        Object aClass = FactoryMethodWrapper.getClass(className);
                        AbstractSecurityRules rules=(AbstractSecurityRules)FactoryMethodWrapper.getUntypedInstance(className);
                        try {
                            formDataJson=rules.checkSqlInjection(formDataJson,key,metadata);
                            
                        } catch (Exception e) {
                            throw new SecurityException(e.getMessage());
                        }
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        String query = queryGenerator.prepareQuery(metadataFileJson, formDataJson.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("The generated adhoc report query is \n" + query);
        }

        String classifier = formDataJson.get("classifier").getAsString();

        JsonObject result;
        result = new JsonObject();
        GsonUtility.accumulate(result,"classifier", classifier);
        GsonUtility.accumulate(result,"query", query);
        return result.toString();
    }
    /**
     * Modifies the form data for Spark processing by removing file extensions and special characters from table names,
     * aliases, and selected columns.
     *
     * @param formDataJson 			form data to be modified for Spark processing.
     */
    private void modifyFormDataForSpark(JsonObject formDataJson) {
    	 JsonObject metadataFileJson = formDataJson.getAsJsonObject("metadataFileJson");
         JsonObject database = metadataFileJson.getAsJsonObject("database");
         JsonObject tables = database.getAsJsonObject("tables");
         JsonArray tableList = tables.getAsJsonArray("tableList");
         JsonArray userSelectedColumns = formDataJson.getAsJsonArray("columns");
         Set<String> extensions = new HashSet<>();
			for (JsonElement eachTableItem : tableList) {
				JsonObject table =  eachTableItem.getAsJsonObject();
				String tableName = table.get("name").getAsString();
				String extension = FilenameUtils.getExtension(tableName);
				if (!StringUtils.isBlank(extension)) {
					extensions.add(extension);
					tableName = FilenameUtils.removeExtension(tableName);
					tableName = tableName.replaceAll("-", "_");
					String alias = table.get("aliasName").getAsString();
					alias = FilenameUtils.removeExtension(alias);
					alias = DBProcessor.checkAndReplaceSpecialChars(alias);
					table.addProperty("name", tableName);
					table.addProperty("aliasName", alias);
				}
			}
			if (!extensions.isEmpty()) {
				if (userSelectedColumns != null && !userSelectedColumns.isEmpty()) {
					userSelectedColumns.forEach(entry -> removeExtension(entry, extensions));
				}
				JsonObject functions = GsonUtility.optJsonObject(formDataJson,"functions");
				if (functions != null && !functions.entrySet().isEmpty()) {
					JsonArray aggregateArray = GsonUtility.optJsonArray(functions,"aggregate");
					if (aggregateArray != null && !aggregateArray.isEmpty()) {
						aggregateArray.forEach(array -> removeExtension(array, extensions));
					}
				}
				JsonArray filters = GsonUtility.optJsonArray(formDataJson,"filters");
				if (filters != null && !filters.isEmpty()) {
					filters.forEach(filter -> removeExtension(filter, extensions));
				}
				JsonArray havingAray = GsonUtility.optJsonArray(formDataJson,"having");
				if (havingAray != null && !havingAray.isEmpty()) {
					havingAray.forEach(having -> removeExtension(having, extensions));
				}
			}
         
    }
    /**
     * Method removes or replace the extension.
     * @param rawObj         provides column details
     * @param extensions    extensions
     */
    private void removeExtension(Object rawObj , Set<String> extensions) {
    	JsonObject object = (JsonObject) rawObj;
    	String name = object.get("column").getAsString();
    	for(String extension : extensions) {
    		if(name.contains(extension)) {
    			name = name.replace("."+extension, "");
    			name = name.replace("-", "_");
    		}
    	}
    	object.addProperty("column", name);
    }
    /**
     * Determines whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}