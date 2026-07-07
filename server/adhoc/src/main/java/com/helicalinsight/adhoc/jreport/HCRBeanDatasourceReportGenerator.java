package com.helicalinsight.adhoc.jreport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.cachemanager.HCRPrintCacheManager;
import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.jasperreports.engine.JasperPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * HCRBeanDatasourceReportGenerator class implements {@link IHCRGenerator} interface.
 * Generates report using form data , cache connection information.
 * This class is responsible for creating Helical-Canned reports and JasperPrint objects based on
 * the provided JSON form data, and for generating responses using these objects.
 * Created by author on 12/5/2019.
 * @author Rajesh
 */
@Component("HCRBeanDatasourceReportGenerator")
@Scope("prototype")
public class HCRBeanDatasourceReportGenerator implements IHCRGenerator {
	private static final Logger logger = LoggerFactory.getLogger(HCRBeanDatasourceReportGenerator.class);
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private HCRHelper hcrHelper;

	/**
     * Generates an HCR report based on the provided form data. This method handles caching, refreshing cache if required,
     * and processes the report generation.
     * 
     * @param formData 		 JSON object containing form data for report generation
     * @return the JSON object containing the generated report details
     */
	@Override
	public JsonObject generateHCReport(JsonObject formData) {
		formData= HCRUtils.checkForDefaultValues(formData);
		boolean refreshCache = GsonUtility.optBooleanValue(formData,"refresh", false);
		boolean isFromSaveService = GsonUtility.optBooleanValue(formData,"isFromSaveService", false);
		boolean isPreview = GsonUtility.optBooleanValue(formData,"isPreview", false);
		HCRUtils.loadFontDirectories();
		JasperPrint cachedJasperPrint = null;
		CacheManager cacheManager = null;
		Integer noOfRecordsFoundInCache = null;
		JsonObject saveDetails =GsonUtility. optJsonObject(formData,"saveDetails");
		if (refreshCache) {

/*			JsonObject targetFile = formData.getAsJsonObject("targetFile");
			logger.debug("targetFile :" + targetFile);
			String dir = targetFile.get("dir").getAsString();
			String file = targetFile.get("file").getAsString();
			JsonObject connectionDetails = new Gson().fromJson(formData.getAsJsonObject("connectionDetails"),
					JsonObject.class);
			connectionDetails.remove("efwd");
			connectionDetails.remove("dir");
			connectionDetails.remove("map_id");
			HCReport hcrReport = (HCReport) ReportOpenHelper.getAdhocReport(dir, file);
			// formData = JSONObject.fromObject(hcrReport.getFormData());
			formData = new Gson().fromJson(hcrReport.getFormData(), JsonObject.class);
			JsonObject newConnectionDetails = formData.getAsJsonObject("connectionDetails");
			if (newConnectionDetails != null && !newConnectionDetails.entrySet().isEmpty()) {
				JsonObject efwd = newConnectionDetails.getAsJsonObject("efwd");
				connectionDetails.add("efwd", efwd);

				connectionDetails.addProperty("dir", newConnectionDetails.get("dir").getAsString());
				connectionDetails.addProperty("map_id", newConnectionDetails.get("map_id").getAsString());
			}
			formData.add("connectionDetails", connectionDetails);

			formData.add("saveDetails", saveDetails);
			formData.addProperty("refresh", true);
*/
		}
		if (saveDetails != null && !isPreview) {
			String uuid = GsonUtility.optString(saveDetails,"uuid");
			String saveCacheFileName = uuid + "." + JsonUtils.getHCRExtension();
			cacheManager = CacheUtils.getCacheManager("/hcrPrint");
			cacheManager.setRequestData(formData.toString());
			Cache cache = cacheHelper.prepareCacheFromRequest(cacheManager);
			boolean cacheRequired = cacheHelper.processCache(null, null, saveCacheFileName, refreshCache, cache,
					cacheManager);
			if (isFromSaveService) {
				return null;
			}
			if (cacheRequired && cacheManager instanceof HCRPrintCacheManager) {
				// Checks the authority.
				final Long connectionId = cacheManager.getConnectionId();

				HCRPrintCacheManager hcrObj = (HCRPrintCacheManager) cacheManager;
				cachedJasperPrint = hcrObj.getResult();
				noOfRecordsFoundInCache = hcrObj.getNoOfRecords();
			}
		} else {
			return regularFlow(formData);
		}
		JsonObject jsonObject = hcrHelper.provideResponseUsingPrint(formData, cachedJasperPrint,
				noOfRecordsFoundInCache);
		if (refreshCache) {
			formData.remove("refresh");
			jsonObject.add("updatedPreviewFormData", formData);
		}
		return jsonObject;

	}

	/**
     * Generates a JasperPrint object based on the provided form data.
     * 
     * @param formData 		 JSON object containing form data for report generation
     * @return the generated JasperPrint object
     */
	@Override
	public JasperPrint generateHCRPrint(JsonObject formData) {
		JasperPrint jasperPrint = null;
		JsonArray jsonData = null;
		jsonData = prepareExecuteJasperReport(formData);
		jasperPrint = hcrHelper.provideJasperPrint(formData, jsonData);
		return jasperPrint;
	}

	
	private JsonObject regularFlow(JsonObject formData) {
		JasperPrint jasperPrint = null;
		String uuid = hcrHelper.getUuid(formData);
		JsonObject designerChange = GsonUtility.optJsonObject(formData, "designerChange");
		String printUUID =designerChange!=null?GsonUtility.optStringValue(designerChange, "printUUID", null):null;
		JsonArray jsonData = null;
		logger.debug("requestedPrintId :" + printUUID);
		printUUID = printUUID != null ? printUUID : uuid;
		jsonData = prepareExecuteJasperReport(formData);
		JsonObject jsonObject = hcrHelper.prepareResponse(formData, jsonData, jasperPrint, printUUID);
		return jsonObject;
	}
	/**
     * Provides a response using the JasperPrint object and number of records found in cache.
     * 
     * @param formData           JSON object containing form data for report generation
     * @param jasperPrint        JasperPrint object
     * @param noOfRecords        number of records found in cache
     * @return the JSON object containing the response
     */
	@Override
	public JsonObject getResponseUsingPrint(JsonObject formData, JasperPrint jasperPrint, Integer noOfRecords) {
		return hcrHelper.provideResponseUsingPrint(formData, jasperPrint, noOfRecords);
	}

	/**
     * Prepares and executes a Jasper report based on the provided form data.
     * 
     * <p>This method prepares a JasperReport object from the form data and executes it to generate
     * a JasperPrint object. This involves compiling the report, filling it with data, and creating
     * the final print object.</p>
     *
     * @param formData the JSON object containing the form data
     * @return a JasperPrint object generated from the form data
     * @throws Exception if an error occurs during report preparation or execution
     */
	public JsonArray prepareExecuteJasperReport(JsonObject formData) {
		boolean refreshCache = GsonUtility.optBooleanValue(formData,"refresh", false);
		Boolean cacheRequired = false;
		CacheManager cacheManager = null;
		JsonArray jsonData = null;
		JsonObject connectionDetails = formData.getAsJsonObject("connectionDetails");
		if (!connectionDetails.entrySet().isEmpty()) {
			String cacheName = getCacheNameFromConnection(formData);
			cacheManager = CacheUtils.getCacheManager("/hcr");
			cacheManager.setRequestData(formData.toString());
			Cache cache = cacheHelper.prepareCacheFromRequest(cacheManager);
			cacheRequired = cacheHelper.processCache(null, null, cacheName, refreshCache, cache, cacheManager);
			if (cacheRequired && cacheManager instanceof HCRQueryProcessCacheManager) {
				HCRQueryProcessCacheManager hcrObj = (HCRQueryProcessCacheManager) cacheManager;
				//jsonData = JSONArray.fromObject(hcrObj.getResult().toString());
				jsonData = hcrObj.getResult();
			}
		}
		return jsonData;
	}
	
	private String getCacheNameFromConnection(JsonObject formData) {
		JsonObject connectionDetails = formData.getAsJsonObject("connectionDetails");
		String cacheName = null;
		if (!connectionDetails.entrySet().isEmpty() && connectionDetails.has("temp_uuid"))
			cacheName = connectionDetails.get("temp_uuid").getAsString();
		else {
			if (formData.has("saveDetails") && !formData.getAsJsonObject("saveDetails").entrySet().isEmpty()) {
				cacheName = formData.getAsJsonObject("saveDetails").get("uuid").getAsString() + "." + JsonUtils.getHCRExtension();
				
			}
			// cacheName = connectionDetails.getJSONObject("efwd").getString("file");
		}
		return cacheName;
	}

}
