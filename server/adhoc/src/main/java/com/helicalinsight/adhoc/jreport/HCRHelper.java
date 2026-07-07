package com.helicalinsight.adhoc.jreport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManagerForResultSet;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.datasource.*;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.fill.JRHorizontalFiller;
import net.sf.jasperreports.engine.fill.JRVerticalFiller;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Helper class to facilitate operations related to Helical Canned Report (HCR).
 * This class provides methods for preparing form data, generating JasperPrint objects,
 * preparing responses, managing connections, exporting reports, and other related tasks.
 * 
 * Created by author on 11/29/2019.
 * @author Rajesh
 */
@Component
@Scope("prototype")
public class HCRHelper {
	private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
	private static final Logger logger = LoggerFactory.getLogger(HCRHelper.class);
	@Autowired
	private CacheHelper cacheHelper;







	/**
     * Prepares form data for HCR parameters by merging URL parameters into existing HCR JSON data.
     *
     * @param urlParametersJson JSON object containing URL parameters
     * @param hcrJsonData       Existing HCR JSON data
     * @return Modified JsonObject with merged parameters
     */
	public static JsonObject prepareFormDataForHCRParameters(JsonObject urlParametersJson, JsonObject hcrJsonData) {
		if (urlParametersJson == null || urlParametersJson.entrySet().isEmpty()) {
			return hcrJsonData;
		}
		// hcrJsonData.getJSONObject("connectionDetails").putAll(urlParametersJson);
		JsonObject connectionDetailsObject = hcrJsonData.getAsJsonObject("connectionDetails");
		if(connectionDetailsObject.has("efwd")) {
			if (connectionDetailsObject.get("efwd").isJsonNull()) {
				connectionDetailsObject.remove("efwd");
			}
		}

		JsonArray parametersJsonArray = hcrJsonData.getAsJsonObject("designerProperties").getAsJsonArray("parameters");
		for (int index = 0; index < parametersJsonArray.size(); index++) {
			JsonObject item = parametersJsonArray.get(index).getAsJsonObject();
			String name = item.get("name").getAsString();
			JsonElement jsonElement = urlParametersJson.get(name);

			if(jsonElement!=null && jsonElement.isJsonArray()){
				JsonArray jsonArray = urlParametersJson.get(name).getAsJsonArray();
				if(!item.get("className").getAsString().equals("java.util.Collection")){
					item.addProperty(name, jsonArray.get(0).getAsString());
					item.addProperty("defaultExpression",jsonArray.get(0).getAsString());
					connectionDetailsObject.addProperty(name, jsonArray.get(0).getAsString());
				}else{
					item.add(name, jsonElement);
					item.add("defaultExpression", jsonElement);
					connectionDetailsObject.add(name, jsonElement);
				}
			}
		}
		if(hcrJsonData.has("emailExportName")){
			String replaced=hcrJsonData.get("emailExportName").getAsString().replace(".hcr","");
			hcrJsonData.addProperty("emailExportName", replaced);
		}
		return hcrJsonData;
	}
	
	
    /**
     * prepareFormDataForHCRExportReport(String hcrFileName, String hcrDirectory, String[] theTotalFormats)
     * Prepares a JsonObject containing HCR properties for exporting a report.
     * @param hcrFileName                name of the hcr file
     * @param hcrDirectory               directory of file
     * @param theTotalFormats			 formats of the attachments of the dashboard
     * @return jsonObject contains HCR properties.
     * @throws ResourceNotFoundException If the specified HCR resource does not exist.
     */
	
	public static JsonObject prepareFormDataForHCRExportReport(String hcrFileName, String hcrDirectory, String[] theTotalFormats) {

        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
       HIResource hiResource = serviceDB.getResourceByUrl(hcrDirectory+"/"+hcrFileName);
       if (hiResource == null) {
           throw new ResourceNotFoundException("There is no HCR " + "resource with the " +
                   "specified name: " + hcrFileName);
       }
       String fileName = hcrFileName.replace(".hcr","") + "_" + System.currentTimeMillis();
       JsonObject jsonFormData =  GsonUtility.parseString(hiResource.getHiResourceHCR().getPreviewFormData(), JsonObject.class);
       JsonArray jsonArray = new JsonArray();
       for (String str : theTotalFormats) {
           JsonElement element = new JsonPrimitive(str);
           jsonArray.add(element);
       }
       jsonFormData.add("format",jsonArray);
       jsonFormData.addProperty("isExport", true);
       jsonFormData.addProperty("isMail", true);
       jsonFormData.addProperty("emailExportName", fileName);
       return jsonFormData;
   }

	
	/**
     * Generates a JasperPrint object based on the provided form data and generic query result.
     *
     * @param formData            Form data in JSON format
     * @param genericQueryResult  Result of a generic query (can be JSON array or ResultSet)
     * @return JasperPrint object
     */
	public JasperPrint provideJasperPrint(JsonObject formData, Object genericQueryResult) {
		String uuid = getUuid(formData);
		JasperPrint jasperPrint;
		JsonObject hcrData = new JsonObject();
		Boolean generateXML = GsonUtility.optBooleanValue(formData, "generateXML", false);
		String targetJRXMLDIR = null;
		try {

			if (formData.has("jrxmlData")) {
				targetJRXMLDIR = getTargetJrxmlDir(formData, uuid);
			}
			JasperDesign jasperDesign;
			JDesignHelper helper = new JDesignHelper(formData);
			long now = System.currentTimeMillis();
			if (targetJRXMLDIR != null) {
				jasperDesign = JRXmlLoader.load(targetJRXMLDIR);
				generateXML = false;
			} else {
				jasperDesign = helper.createDesigner();
			}
			long time = System.currentTimeMillis();
			logger.debug("Time taken for creating the print preparing Designer :" + (time - now) + ".ms");
			jasperPrint = getJasperPrint(formData, uuid, generateXML, genericQueryResult, jasperDesign, helper,
					hcrData);
			logger.debug("providing jasperPrint");
		} catch (Exception ex) {
			logger.error("Exception occurred while Generating HCReport {}", ex);
			throw new HCRException(ex.getMessage());
		}
		return jasperPrint;
	}


	/**
     * Prepares the response JSON object containing report page information, UUID, JRXML data,
     * and the response (exported report content or other response).
     *
     * @param formData             Form data in JSON format
     * @param genericQueryResult   Result of a generic query (can be JSON array or ResultSet)
     * @param originalJasperPrint  Original JasperPrint object (if already generated)
     * @param printUUID            UUID for the print
     * @return Response JSON object
     */
	public JsonObject prepareResponse(JsonObject formData, Object genericQueryResult, JasperPrint originalJasperPrint,
			String printUUID) {
		String uuid = getUuid(formData);
		JsonObject hcrData = new JsonObject();
		JsonObject response = new JsonObject();
		Boolean generateXML =  GsonUtility.optBooleanValue(formData, "generateXML", false);
		boolean isExport =  GsonUtility.optBooleanValue(formData, "isExport", false);
		Integer page = GsonUtility.optIntValue(formData, "page", 0);
		String targetJRXMLDIR = null;
		printUUID = printUUID != null ? printUUID : uuid;
		try {
			if (originalJasperPrint == null) {
				if (formData.has("jrxmlData")) {
					targetJRXMLDIR = getTargetJrxmlDir(formData, uuid);
				}
				JasperDesign jasperDesign;
				JDesignHelper helper = new JDesignHelper(formData);
				if (targetJRXMLDIR != null) {
					jasperDesign = JRXmlLoader.load(targetJRXMLDIR);
					generateXML = false;
				} else {
					jasperDesign = helper.createDesigner();
				}
				originalJasperPrint = getJasperPrint(formData, uuid, generateXML, genericQueryResult, jasperDesign,
						helper, hcrData);

			}
			Object format;
			Object exportFormat=null;
			if (formData.has("format")) {
				format = formData.get("format");
			} else {
				format = GsonUtility.optStringValue(formData, "format", "html").toUpperCase();
			}

			JsonObject reportPagesJson = new JsonObject();
			Integer reportSize = originalJasperPrint.getPages().size();
			reportPagesJson.addProperty("totalPageCount", reportSize);
			reportPagesJson.addProperty("currentPageNo", GsonUtility.optStringValue(formData, "page", "0"));
			if(format instanceof String || format instanceof JsonPrimitive) {
				exportFormat = new Gson().toJsonTree(format).getAsString();
				reportPagesJson.addProperty("format", exportFormat.toString());
			}else if(format instanceof JsonArray){
				 exportFormat = new Gson().toJsonTree(format).getAsJsonArray();
				reportPagesJson.add("format",(JsonArray) exportFormat);
			}
			HCRExportHelper exportHelper = new HCRExportHelper(formData, originalJasperPrint, uuid);
			String result = "";

			if (isExport) {
				if (exportFormat instanceof String) {
					if (exportHelper.exportIntoFiles((String) exportFormat, page, hcrData))
						result = "File successfully exported";
					else
						result = "Could not able to export the file";
				} else if (exportFormat instanceof JsonArray) {
					result = exportHelper.handleMultiExport((JsonArray) exportFormat, page, hcrData);
				}

			} else {
				byte[] export = exportHelper.exportInBytes("HTML", reportSize, page, true);
				result = new String(export, StandardCharsets.UTF_8);
			}
			
			response.add("reportPageInfo", reportPagesJson);
			response.addProperty("printUUID", printUUID);
			response.add("jrxmlData", hcrData);
			response.addProperty("response", result);
			if (null != originalJasperPrint.getProperty("lastModifiedCache")) {
				response.addProperty("lastModified",
						Long.valueOf(originalJasperPrint.getProperty("lastModifiedCache")));
			}
		} catch (Exception ex) {
			logger.error("Exception occurred while Generating HCReport {}", ex);
			throw new HCRException(ex.getMessage());
		}
		return response;
	}
	
	/**
     * Provides a response JSON object using a cached JasperPrint object, typically used for reusing cached reports.
     *
     * @param formData       Form data in JSON format
     * @param cacheJasperPrint Cached JasperPrint object
     * @param reportSize     Total size of the report
     * @return Response JSON object
     */
	public JsonObject provideResponseUsingPrint(JsonObject formData, JasperPrint cacheJasperPrint, Integer reportSize) {
		String uuid = getUuid(formData);
		JsonObject hcrData = new JsonObject();
		JsonObject response = new JsonObject();
		boolean isExport = GsonUtility.optBooleanValue(formData,"isExport", false);
		int page =  GsonUtility.optIntValue(formData,"page", 0);
		try {

			Object format;
			if (formData.has("format")) {
				format = formData.get("format");
			} else {
				format = GsonUtility.optStringValue(formData,"format", "html").toUpperCase();
			}
			JsonObject reportPagesJson = new JsonObject();
			reportPagesJson.addProperty("totalPageCount", reportSize);
			reportPagesJson.addProperty("currentPageNo", ""+page);
			
			if(format instanceof String || format instanceof JsonPrimitive) {
				reportPagesJson.addProperty("format", new Gson().toJsonTree(format).getAsString());
			}else {
				reportPagesJson.add("format", (JsonElement)format);
			}
		
			HCRExportHelper exportHelper = new HCRExportHelper(formData, cacheJasperPrint, uuid);
			String result = "";

			if (isExport) {
				if (format instanceof String) {
					if (exportHelper.exportIntoFiles((String) format, page, hcrData))
						result = "File successfully exported";
					else
						result = "Could not able to export the file";
				} else if (format instanceof JsonArray) {
					result = exportHelper.handleMultiExport((JsonArray) format, page, hcrData);
				}

			} else {
				if (reportSize == null) {
					result = "";
				} else {
					byte[] export = exportHelper.exportInBytes("HTML", reportSize, page, true);
					result = new String(export, StandardCharsets.UTF_8);
				}
			}

			response.add("reportPageInfo", reportPagesJson);
			response.add("jrxmlData", hcrData);
			response.addProperty("response", result);
			if (cacheJasperPrint != null && null != cacheJasperPrint.getProperty("lastModifiedCache")) {
				response.addProperty("lastModified", Long.valueOf(cacheJasperPrint.getProperty("lastModifiedCache")));
			}
		} catch (Exception ex) {
			logger.error("Exception occurred while Generating HCReport {}", ex);
			throw new HCRException(ex.getMessage());
		}
		return response;
	}

	private JasperPrint getJasperPrint(JsonObject formData, String uuid, Boolean generateXML, Object genericQueryResult,
			JasperDesign jasperDesign, JDesignHelper helper, JsonObject jrxmlData) throws JRException, ParseException {
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

		if (generateXML) {
			generateJRXML(uuid, formData, jasperReport, jrxmlData);
		}
		
		Boolean isCompile = GsonUtility.optBooleanValue(formData, "compile", false);
		
		if(isCompile) {
			String compileFilePath =  Paths.get(TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),UUIDGenerator.getUuid()+".jasper").toString();
			JRSaver.saveObject(jasperReport, compileFilePath);
			jrxmlData.addProperty("compiledFilePath", compileFilePath);
		}
		
		List<JsonObject> requiredPramsExtractedFromReport = extractRequiredParametersNames(jasperReport);
		JsonObject requiredParamsExtractedFromFromData = helper.prepareParameterForReportExecution(formData);
		Map<String, Object> parameters = prepareParameters(requiredPramsExtractedFromReport,requiredParamsExtractedFromFromData, uuid);
		JasperPrint jasperPrint = null;
		JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(50);
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		long start = System.currentTimeMillis();
		if (genericQueryResult != null) {
			if (genericQueryResult instanceof JsonArray) {
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,  new JREmptyDataSource());
			} else if (genericQueryResult instanceof ResultSet) {
					jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new CustomJRResultSetDataSource((ResultSet)genericQueryResult));
			}
		} else {
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
		}
		long end = System.currentTimeMillis();		
		logger.debug("Time taken to fill the report : {} s", (end-start)/1000.0);
		return jasperPrint;
	}

	/**
	 * Provides a database connection based on the provided query text, connection parameters, and whether
	 * the connection is temporary or regular.
	 *
	 * @param queryText     		The SQL query text. Used to determine if a connection is necessary.
	 * @param isTemp        		Indicates whether the connection is temporary (true) or regular (false).
	 * @param data          		JsonObject containing connection details and parameters required to establish the database connection.
	 * @return Connection   		A java.sql.Connection object representing the established database connection.
	 * @throws IllegalArgumentException if the dataSourceType provided in connectionParameters is invalid.
	 * @throws RuntimeException         if there is an issue during connection retrieval or if required parameters are missing.
	 */
	public Connection connectionProvider(String queryText, boolean isTemp, JsonObject data) {
		JsonObject connectionParameters = null;
		String dataSourceType = null;
		Connection connection = null;
		if (queryText != null) {
			connectionParameters = data.has("connectionDetails") ? data.getAsJsonObject("connectionDetails") : null;
			dataSourceType = connectionParameters.get("type").getAsString();
			DataSourceUtils.validate(dataSourceType);
			DataSourceUtils.addExtraDataForWorkflowProcess(connectionParameters, dataSourceType);
			connectionParameters.addProperty("access", DataSourceSecurityUtility.EXECUTE);
			if (isTemp) {
				connectionParameters.addProperty("isTemp", isTemp);
				DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory
						.getConnectionFromTemp(connectionParameters, dataSourceType);
				connection = driverConnection.getConnection();
			} else {
				DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory
						.getConnection(connectionParameters, dataSourceType);
				connection = driverConnection.getConnection();
			}
		}
		return connection;
	}
	/**
	 * Prepares the query for report generation based on the provided formData and connectionFormData.
	 * Modifies the formData with necessary query and connection details.
	 *
	 * @param formData            The JsonObject containing connection details.
	 * @param connectionFormData  The JsonObject containing dir and temp_uuid
	 * @param targetJRXMLDIR      The target directory path
	 * @return true if the query preparation involved temporary data; otherwise, false.
	 */
	public Boolean prepareQueryForReport(JsonObject formData, JsonObject connectionFormData, String targetJRXMLDIR) {
		String queryString;
		JsonObject connectionPart;
		JsonObject dataMapTagContent;
		Boolean isTemp = false;
		if (!connectionFormData.entrySet().isEmpty()) {
			if (connectionFormData.has("temp_uuid") && !connectionFormData.has("dir")) {
				isTemp = true;
				connectionFormData.addProperty("dir", TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
				JsonObject efwd = new JsonObject();
				efwd.addProperty("file", connectionFormData.get("temp_uuid").getAsString() + "." + JsonUtils.getEfwdExtension());
				connectionFormData.add("efwd", efwd);
				EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(connectionFormData.toString(),
						applicationProperties);
				queryString = queryExecutor.getUnProcessedQueryFromTemp();
				connectionPart = queryExecutor.getConnectionPartFromTemp();
				dataMapTagContent = queryExecutor.getDataMapTagContentFromTemp();
			} else {
				EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(connectionFormData.toString(),
						applicationProperties);
				queryString = queryExecutor.getUnProcessedQuery();
				connectionPart = queryExecutor.getConnectionPart();
				dataMapTagContent = queryExecutor.getDataMapTagContent();
			}
			if (dataMapTagContent != null && dataMapTagContent.has("Parameters")) {
				queryString = queryString.contains("${") ? queryString.replace("${", "$P{") : queryString;
			}
			String type = connectionPart.get("type").getAsString();
			String id = connectionPart.get("id").getAsString();
			formData.getAsJsonObject("connectionDetails").addProperty("type", type);
			formData.getAsJsonObject("connectionDetails").addProperty("id", id);
			if (queryString != null && !queryString.isEmpty() && targetJRXMLDIR == null) {
				formData.getAsJsonObject("designerProperties").addProperty("query", queryString);
			}
		}
		return isTemp;
	}
	
	/**
	 * Retrieves executed data from a database or data source based on the provided connectionFormData.
	 *
	 * @param connectionFormData The JsonObject containing temp_uuid, dir etc.
	 * @return JsonArray containing the fetched data if successful; otherwise, an empty JsonArray.
	 */
	public JsonArray getExecutedData(JsonObject connectionFormData) {
		JsonObject resultSet = null;
		if (!connectionFormData.entrySet().isEmpty()) {
			if (connectionFormData.has("temp_uuid") && !connectionFormData.has("dir")) {
				connectionFormData.addProperty("dir", TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
				JsonObject efwd = new JsonObject();
				efwd.addProperty("file", connectionFormData.get("temp_uuid").getAsString() + "." + JsonUtils.getEfwdExtension());
				connectionFormData.add("efwd", efwd);
				EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(connectionFormData.toString(),
						applicationProperties);
				resultSet = queryExecutor.getResultSetFromTemp();
			} else {
				EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(connectionFormData.toString(),
						applicationProperties);
				resultSet = queryExecutor.getResultSet();
			}
		}
		if (resultSet != null && resultSet.size() > 0) {
			return resultSet.getAsJsonArray("data");
		}
		return new JsonArray();
	}

	
	/**
     * Retrieves the target JRXML directory from the provided form data and UUID.
     *
     * @param formData Form data in JSON format provides jrxml details.
     * @param uuid     UUID as a string
     * @return Target JRXML directory path
     */
	public String getTargetJrxmlDir(JsonObject formData, String uuid) {
		String targetJrxmlDir;
		JsonObject jrxmlDataJson = formData.getAsJsonObject("jrxmlData");
		String jrxmlDir = GsonUtility.optStringValue(jrxmlDataJson, "jrxmlDir", null);
		if (jrxmlDir != null)
			targetJrxmlDir = applicationProperties.getSolutionDirectory() + File.separator + jrxmlDir + File.separator
					+ uuid + ".jrxml";
		else
			targetJrxmlDir = TempDirectoryCleaner.getTempDirectory().getAbsolutePath() + File.separator + uuid
					+ ".jrxml";
		if (!FileUtils.isFilePresent(new File(targetJrxmlDir)))
			return null;
		return targetJrxmlDir;
	}

	
	/**
     * Retrieves the UUID from the provided form data.
     *
     * @param formData Form data in JSON format
     * @return UUID as a string
     */
	public String getUuid(JsonObject formData) {
		String uuid = UUID.randomUUID().toString();
		if (formData.has("jrxmlData")) {
			JsonObject jrxmlDataJson = formData.getAsJsonObject("jrxmlData");
			uuid = GsonUtility.optStringValue(jrxmlDataJson, "uuid", uuid);
		}
		if (formData.has("saveId") && GsonUtility.optBooleanValue(formData, "isFromSave", false)) {
			uuid = formData.get("saveId").getAsString();
		}
		formData.addProperty("uuid", uuid);
		return uuid;

	}

	
	
	
	/**
     * Generates JRXML (JasperReports XML) based on the provided form data and updates the JasperReport object.
     *
     * @param uuid         Unique identifier for the report generation (optional).
     * @param formData     Form data in JSON format containing necessary details for JRXML generation like dir.
     * @param jasperReport JasperReport object representing the report template.
     * @param jrxmlData    JSON object containing additional data needed for JRXML generation (optional).
     * @throws JRException If an error occurs during JRXML generation or compilation.
     */
	public void generateJRXML(String uuid, JsonObject formData, JasperReport jasperReport, JsonObject jrxmlData)
			throws JRException {

		String dir = GsonUtility.optStringValue(formData, "dir", null);
		String parentPath;
		if (dir != null) {
			parentPath = applicationProperties.getSolutionDirectory() + File.separator + dir;
			jrxmlData.addProperty("jrxmlDir", dir);
		} else
			parentPath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();

		String targetFile = File.separator + uuid + ".jrxml";
		String targetFolder = parentPath + targetFile;
		JasperCompileManager.writeReportToXmlFile(jasperReport, targetFolder);
		jrxmlData.addProperty("uuid", uuid);
	}

	public List<JsonObject> extractRequiredParametersNames(JasperReport jasperMasterReport) {
		JRParameter[] parametersArray = jasperMasterReport.getParameters();
		List<JsonObject> parametersList = new ArrayList<>();
		JsonObject eachParameter;
		for (JRParameter param : parametersArray) {
			if (!param.isSystemDefined()) {
				eachParameter = new JsonObject();
				eachParameter.addProperty("name", param.getName());
				eachParameter.addProperty("valueClassName", param.getValueClassName());
				eachParameter.addProperty("description", param.getDescription());
				eachParameter.addProperty("valueExpression",
						param.getDefaultValueExpression() != null ? param.getDefaultValueExpression().getText() : null);
				eachParameter.addProperty("nestedTypeName", param.getNestedTypeName());
				parametersList.add(eachParameter);
			}
		}
		return parametersList;
	}
	


	public Map<String, Object> prepareParameters(List<JsonObject> requiredParamsExtractedFromReport,
			JsonObject requiredParamsExtractedFromFormData, String uuid) throws ParseException {
		Map<String, Object> parameter = new HashMap<>();
		JsonObject requestParameters = requiredParamsExtractedFromFormData.getAsJsonObject("parameters");
		requiredParamsExtractedFromReport.forEach((eachJson) -> {
			String parameterName = eachJson.get("name").getAsString();
			String valueClassName = eachJson.get("valueClassName").getAsString();
			
			// TODO : This is unnecessary check, fix the testcases and remove this
			if (java.sql.Connection.class.getName().equalsIgnoreCase(valueClassName)) {
				valueClassName = net.sf.jasperreports.engine.JRDataSource.class.getName();
			}
			
			Object parameterValue = prepareGenericValue(valueClassName, requestParameters, parameterName,
					requiredParamsExtractedFromFormData, uuid);
			if (parameterValue != null)
				parameter.put(parameterName, parameterValue);
		});

		return parameter;
	}


	public Object prepareGenericValue(String valueClassName, JsonObject requestParameters, String parameterName,
			JsonObject requiredParamsExtractedFromFormData, String uuid) {
		if (!requestParameters.has(parameterName)) {
			return null;
		}
		Object value = null;
		JsonElement parameterValue = requestParameters.get(parameterName);
		switch (valueClassName) {
		case "java.lang.Integer":
			value = GsonUtility.optInt(requestParameters, parameterName);
			break;
		case "java.lang.String":
				value = GsonUtility.optString(requestParameters, parameterName);
				break;

		case "java.lang.Double":
		case "java.lang.Float":
			value = GsonUtility.optDouble(requestParameters, parameterName);
			break;

		case "net.sf.jasperreports.engine.JasperReport":
			value = prepareSubReport(parameterName, requiredParamsExtractedFromFormData);
			break;

		case "java.util.Date":
			try {
				value = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(parameterValue.getAsString());
			} catch (ParseException e) {
				value = null;
			}
			break;

		case "java.util.Collection":
			value = GsonUtility.jsonElementToCollection(parameterValue);
			break;

		case "com.helicalinsight.adhoc.jreport.LazySubDatasetDataSourceFactory":
			value = handleLazySubdataset(requestParameters, parameterName);
			break;

		case "net.sf.jasperreports.engine.JRDataSource":
		case "com.helicalinsight.datasource.ResultSetDataSourceFactory":
			String generationType = JsonUtils.getHCRDefaultGeneratorType();
			if ("regular".equalsIgnoreCase(generationType)) {
				value = handleRegular(requestParameters, parameterName, uuid);
			} else {
				value = handleBeanDatasource(requestParameters, parameterName, uuid);
			}
			break;
			
		case "java.sql.Connection":
			JsonObject connectionJson = requestParameters.getAsJsonObject(parameterName);
			JsonObject connectionDetailsFormData = new JsonObject();
			if(!connectionJson.has("connectionDetails")) {
				connectionDetailsFormData.add("connectionDetails", connectionJson);
			}
			else {
				connectionDetailsFormData = connectionJson;
			}
			value = prepareSubdataSetConnection(connectionJson);
			break;

		default:
			value = parameterValue;
			break;

		}
		return value;
	}


	private Object handleBeanDatasource(JsonObject requestParameters, String parameterName, String uuid) {
		CacheManager cacheManager = CacheUtils.getCacheManager("/hcr");
		Object value;
		JsonObject connectionDetails = requestParameters.getAsJsonObject(parameterName);
		cacheManager.setRequestData(connectionDetails.toString());
		Cache cache1 = cacheHelper.prepareCacheFromRequest(cacheManager);
		cacheHelper.processCache(null, null, uuid, false, cache1, cacheManager);
		if (cacheManager instanceof HCRQueryProcessCacheManager) {
			HCRQueryProcessCacheManager hcrObj = (HCRQueryProcessCacheManager) cacheManager;
			JsonArray jsonData = hcrObj.getResult();
			value = new HCRJsonDataSource(jsonData);
		} else {
			value = prepareSubdatasetBeanDatasource(connectionDetails);
		}
		return value;
	}


	/**
	 * Returns a lazy factory for a subdataset report parameter. Query execution is
	 * deferred until Jasper evaluates the component dataset run.
	 */
	private Object handleLazySubdataset(JsonObject requestParameters, String parameterName) {
		JsonObject connectionDetails = extractConnectionDetails(requestParameters, parameterName);
		if (connectionDetails == null || connectionDetails.entrySet().isEmpty()) {
			return null;
		}
		return new LazySubDatasetDataSourceFactory(connectionDetails);
	}

	/**
	 * Executes a subdataset query through the existing /hcrResultSet cache layer.
	 * Reuses the same path as {@link #handleRegular(JsonObject, String, String)}.
	 */
	public ResultSet fetchSubdatasetResultSet(JsonObject connectionDetails) {
		JsonObject connectionDetailsFormData = new JsonObject();
		connectionDetailsFormData.add("connectionDetails", connectionDetails);
		String reportName = CacheUtils.getCacheNameFromConnection(connectionDetailsFormData);
		CacheManager resultsetCacheManager = CacheUtils.getCacheManager("/hcrResultSet");
		resultsetCacheManager.setRequestData(connectionDetailsFormData.toString());
		Cache cache = cacheHelper.prepareCacheFromRequest(resultsetCacheManager);
		final String cacheName = StringUtils.isBlank(reportName) ? "" : reportName;
		boolean cacheRequired = cacheHelper.processCache(null, null, cacheName, false, cache,
				resultsetCacheManager);
		if (cacheRequired && resultsetCacheManager instanceof HCRQueryProcessCacheManagerForResultSet hcrObj) {
			return hcrObj.getResult();
		}
		return null;
	}

	private JsonObject extractConnectionDetails(JsonObject requestParameters, String parameterName) {
		if (!requestParameters.has(parameterName)) {
			return null;
		}
		JsonObject connectionJson = requestParameters.getAsJsonObject(parameterName);
		if (connectionJson.has("connectionDetails")) {
			return connectionJson.getAsJsonObject("connectionDetails");
		}
		return connectionJson;
	}

	private Object handleRegular(JsonObject requestParameters, String parameterName, String uuid) {
		JsonObject connectionDetails = extractConnectionDetails(requestParameters, parameterName);
		if (connectionDetails == null) {
			return null;
		}
		JsonObject connectionDetailsFormData = new JsonObject();
		connectionDetailsFormData.add("connectionDetails", connectionDetails);
		String reportName = CacheUtils.getCacheNameFromConnection(connectionDetailsFormData);
		CacheManager resultsetCacheManager = CacheUtils.getCacheManager("/hcrResultSet");
		resultsetCacheManager.setRequestData(connectionDetailsFormData.toString());
		Cache cache = cacheHelper.prepareCacheFromRequest(resultsetCacheManager);
		final String cacheName = StringUtils.isBlank(reportName) ? uuid : reportName;
		boolean cacheRequired = cacheHelper.processCache(null, null,cacheName, false, cache, resultsetCacheManager);
		if (cacheRequired && resultsetCacheManager instanceof HCRQueryProcessCacheManagerForResultSet) {
			HCRQueryProcessCacheManagerForResultSet hcrObj = (HCRQueryProcessCacheManagerForResultSet) resultsetCacheManager;
			ResultSet rs =  hcrObj.getResult();
			return new ResultSetDataSourceFactory(rs);
			
		}
		return null;
	}


	private Object prepareSubdatasetBeanDatasource(JsonObject connectionDetails) {
		JsonArray jsonDataArray = getExecutedData(connectionDetails);
		return new HCRJsonDataSource(jsonDataArray);
	}



	private Object prepareSubdataSetConnection(JsonObject connectionDetails) {
		Object value;
		Boolean isTemp = prepareQueryForSubReport(connectionDetails);
		JsonObject connectionJson = new JsonObject();
		connectionJson.add("connectionDetails", connectionDetails);
		value = connectionProvider("JUST_TO_IGNORE_NULL", isTemp, connectionJson);
		return value;
	}


	public Boolean prepareQueryForSubReport(JsonObject connectionFormData) {
		JsonObject connectionPart;
		Boolean isTemp = false;
		if (!connectionFormData.entrySet().isEmpty()) {
			if (connectionFormData.has("temp_uuid") && !connectionFormData.has("dir")) {
				isTemp = true;
				connectionFormData.addProperty("dir", TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
				JsonObject efwd = new JsonObject();
				efwd.addProperty("file",
						connectionFormData.get("temp_uuid").getAsString() + "." + JsonUtils.getEfwdExtension());
				connectionFormData.add("efwd", efwd);
				EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(connectionFormData.toString(),
						applicationProperties);
				connectionPart = queryExecutor.getConnectionPartFromTemp();
			} else {
				EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(connectionFormData.toString(),
						applicationProperties);
				connectionPart = queryExecutor.getConnectionPart();
			}
			String type = connectionPart.get("type").getAsString();
			String id = connectionPart.get("id").getAsString();
			connectionFormData.addProperty("type", type);
			connectionFormData.addProperty("id", id);
		}
		return isTemp;
	}

	public Object prepareSubReport(String parameterName, JsonObject data) {
		Object parameterValue = null;
		try {
			JsonObject parameters = data.getAsJsonObject("parameters");
			String subReportLoc = parameters.get(parameterName).getAsString();
			String subReportFile = applicationProperties.getSolutionDirectory() + File.separator + subReportLoc;
			JasperDesign jasperSubDesign = JRXmlLoader.load(subReportFile);
			parameterValue = JasperCompileManager.compileReport(jasperSubDesign);
		} catch (JRException e) {
			e.printStackTrace();
		}
		return parameterValue;
	}
	
	
	public PrintContext provideJasperFillerForStreaming( JsonObject formData,  Object genericQueryResult,   JDesignHelper helper,     JasperDesign jasperDesign, JsonObject jrxmlData) throws JRException, ParseException {

	    String uuid = getUuid(formData);

	    JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

	    Boolean generateXML = GsonUtility.optBooleanValue(formData, "generateXML", false);
	    String cacheKey  = GsonUtility.optStringValue(formData, "designCacheKey", UUID.randomUUID().toString());
	    
	    String dir = GsonUtility.optStringValue(formData, "dir", "TEMP_DIRECTORY") + File.separator + cacheKey;
	    if (generateXML) {
	        generateJRXML(uuid, formData, jasperReport, jrxmlData);
	    }

	    List<JsonObject> requiredParams = extractRequiredParametersNames(jasperReport);
	    JsonObject preparedParams = helper.prepareParameterForReportExecution(formData);

	    Map<String, Object> parameters = prepareParameters(requiredParams, preparedParams, uuid);

	    JRVirtualizer virtualizer =  new VirtualizerFactory().create(GsonUtility.optBooleanValue(formData, "isExport", false),dir);
	    
	    parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

	    JasperReportsContext context = DefaultJasperReportsContext.getInstance();
	    
	    JRBaseFiller filler  =    getFiller(context, jasperReport, jasperDesign); 
	    
	    JRDataSource datasource = new JREmptyDataSource();
	    
	    if (genericQueryResult != null && genericQueryResult instanceof ResultSet) {
				 datasource = new CustomJRResultSetDataSource((ResultSet)genericQueryResult);
		}
	    return new PrintContext(filler, parameters, datasource, virtualizer, dir);
	}
	
	
	
	private JRBaseFiller getFiller(JasperReportsContext context, JasperReport jasperReport,JasperDesign jasperDesign) {
		PrintOrderEnum printOrderEnum =  jasperDesign.getPrintOrderValue();
		try {
		if ( printOrderEnum.equals(PrintOrderEnum.HORIZONTAL)) {
			logger.debug("Using Horizontal filler.");
			return new JRHorizontalFiller(context, jasperReport, null);
		}
		
			logger.debug("Using Vertical filler.");
			return new JRVerticalFiller(context, jasperReport, null);
		}
		catch (JRException  e) {
			throw new EfwServiceException(e.getMessage());
		}
	}
}
