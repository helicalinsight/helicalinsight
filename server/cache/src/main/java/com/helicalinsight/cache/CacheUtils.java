package com.helicalinsight.cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.HCRPrintCacheConfigurationDTO;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.cache.store.ApplicationCacheStore;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * its an common class or utility  for cacheHelper
 * The {@code @Component("cacheUtils")} annotation is used to mark a class as a Spring bean/component and bean name is cacheUtils.
 */
@Component("cacheUtils")
public class CacheUtils {
	static final long ONE_MINUTE_IN_MILLIS = 60000;
	private static final Logger logger = LoggerFactory.getLogger(CacheUtils.class);
	private static Long cacheExpireDuration;
	private static String durationUnit;
	private static JsonObject cacheXmlJson;

	/**
	 * deleteOldCache(Cache enquiryCache, String physicalCacheFile,
			CacheService cacheService)
	 * this method Deletes old Cache file
	 * @param enquiryCache      a cache object provides cache records
	 * @param physicalCacheFile cache query result
	 * @param cacheService      to performs crud operations on cache object
	 */

	public static synchronized void deleteOldCache(Cache enquiryCache, String physicalCacheFile,
			CacheService cacheService) {
		logger.info("Deleting old Cache file");
		String cacheExtension = CacheUtils.getCacheExtension();
		if (enquiryCache != null) {
			Integer noOfRecords = enquiryCache.getNoOfRecords();
			// todo know the actual usecase from rajesh about
			// if (noOfRecords != null && noOfRecords > 1) {
			if (noOfRecords != null && noOfRecords >= 1) {
				List<String> fileStringsToBeDeleted = new ArrayList<>();
				String designerCacheFilePath = physicalCacheFile.replace("." + cacheExtension,
						"designer." + cacheExtension);
				fileStringsToBeDeleted.add(designerCacheFilePath);
				for (int index = 0; index < noOfRecords; index++) {
					String eachPageIndexPath = physicalCacheFile.replace("." + cacheExtension,
							"page_" + index + "." + cacheExtension);
					fileStringsToBeDeleted.add(eachPageIndexPath);
				}
				deleteAll(fileStringsToBeDeleted);
			} else {
				File file = new File(physicalCacheFile);
				if (file.exists()) {
					file.delete();
				}
			}
			cacheService.deleteCache(enquiryCache.getCacheId());
		}
	}

	/**
	 * deleteAll(List<String> fileStringsToBeDeleted)
	 * This method deletes all the files in the list.
	 * @param fileStringsToBeDeleted     list of caches files.
	 */
	private static void deleteAll(List<String> fileStringsToBeDeleted) {
		fileStringsToBeDeleted.forEach(item -> {
			File file = new File(item);
			boolean exists = file.exists() && file.delete();
		});
	}

	/**
	 * saveFileToDisk(Object object, String cacheFileName)
	 * this method stores the cache Query result.
	 * @param object           query ResultSet value in Object Format
	 * @param cacheFileName    it stores the ResultSet object
	 * {@return True if the object was saved successfully}{@code false} otherwise.
	 * @throws IOException If an error occurs during file creation
	 */
	public static Boolean saveFileToDisk(Object object, String cacheFileName) {
		Boolean processStatus = false;
		logger.debug("Saving file to disk fileName: " + cacheFileName);
		if (!(object instanceof Serializable)) {

			if (object instanceof JsonObject) {
				object = object.toString();
			}

		}
		try {
			File file = new File(cacheFileName);
			file.getParentFile().mkdirs();
			OutputStream buffer = new BufferedOutputStream(new FileOutputStream(cacheFileName));
			try (ObjectOutput output = new ObjectOutputStream(buffer)) {
				if (!(object instanceof Throwable)) {
					output.writeObject(object);
				} else {
					logger.error("The object for saving is actually an error ", object);
				}
			} finally {
				buffer.close();
			}
			processStatus = true;
		} catch (IOException e) {

			logger.error("Error occurred in file creation", e);
		}
		return processStatus;
	}

	/**
	 * getActualCacheExpireDuration()
	 * Calculates the actual expiration duration for the cache.
	 * @return Long 	The actual expiration duration in milliseconds.
	 */
	public static Long getActualCacheExpireDuration() {
		logger.info("Actual Expiry date is being computed");

		long currentTime = System.currentTimeMillis();

		if ("hours".equals(durationUnit)) {
			currentTime = currentTime + (cacheExpireDuration * ONE_MINUTE_IN_MILLIS * 60);
		} else if ("minutes".equals(durationUnit)) {
			currentTime = currentTime + (cacheExpireDuration * ONE_MINUTE_IN_MILLIS);
		}
		return new Date(currentTime).getTime();
	}

	/**
	 * isCacheEnabled()
	 * @return boolean True if the cache is enabled in xml
	 */
	public static boolean isCacheEnabled() {
		return cacheXmlJson.get("enableCache").getAsString().equals("true");
	}

	/**
	 * isThreadingEnabled()
	 *@return True if the threading is enabled in xml file
	 */
	public static boolean isThreadingEnabled() {
		return cacheXmlJson.get("enableThread").getAsString().equals("true");
	}

	/**
	* getCacheExtension()
	* @return The cache extension form xml file
	*/
	public static String getCacheExtension() {
		return cacheXmlJson.get("cacheExtension").getAsString();
	}
	
	
	public static int getNoOfWorkers() {
		return GsonUtility.optIntValue(cacheXmlJson, "noOfWorkers", 2);
	}
	
	/**
	 * getCacheDirectory()
	 * returns the cahe directory from System directory of EFW-Project
	 * @return String	
	 */
	public static String getCacheDirectory() {
		return JsonUtils.getCacheDirectory();
	}
	
	/**
	 * getCacheManager(String url)
	
	 * @param url 		  URL to match the patterns in xml file
     * {@return  CacheManager object } {@code null} if url pattern doesn't match
	 */
	public static CacheManager getCacheManager(String url) {
		String beanName = getChildClass(url,"cacheManager");
		if (beanName != null) {
			return (CacheManager) ApplicationContextAccessor.getBean(beanName);
		} else {
			return null;
		}
	}
	
	/**
	 * getChildClass(String url)
	 * Retrieves the cache object associated with  URL from the Xml configuration.
     * @param url 		 URL to match against the patterns in the Xml configuration.
     * (@returns the bean/object of cache}{@code null} url is not present in the xml configuration.
     */
	
	private static String getChildClass(String url, String key) {
		if (cacheXmlJson == null) {
			return null; // to handle the null case
		}
		JsonArray classList = cacheXmlJson.getAsJsonObject(key).getAsJsonArray("url");
		for (int index = 0; index < classList.size(); index++) {
			JsonObject urlJson = classList.get(index).getAsJsonObject();
			String pattern = urlJson.get("pattern").getAsString();
			if (url.equalsIgnoreCase(pattern)) {
				return urlJson.get("bean").getAsString();
			}
		}
		return null;
	}

	/**
	 * init()
	 * This method is called after the bean is initialized
	 * Retrieves cache configuration from cache.xml file and initializes cache settings.
	 *  
	 */
	
	public static void init() {
		String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
		String cachePath = systemDirectory + File.separator + "Admin" + File.separator + "cache.xml";
		if (logger.isInfoEnabled()) {
			logger.info("Cache path is " + cachePath);
		}
		IProcessor processor = ResourceProcessorFactory.getIProcessor();
		cacheXmlJson = processor.getJsonObject(cachePath, false);

		cacheExpireDuration = Long.parseLong(cacheXmlJson.get("cacheExpireDuration").getAsString());
		durationUnit = cacheXmlJson.get("durationUnit").getAsString();

	}

	@PostConstruct
	public void initialize() {
		init();
	}
	
	
	/**
	 * getRefreshUrl()
	 * @return returns  refreshUrl property from the Xml json object in JsonArray format.
	 */
	public static JsonArray getRefreshUrl() {
		return cacheXmlJson.getAsJsonObject("refreshUrl").getAsJsonArray("url");
	}

	/**
	 * getCacheXmlJson()
	 * @return simply returns the cache Xml file into JsonObject
	 */
	public static JsonObject getCacheXmlJson() {
		return cacheXmlJson;
	}
	
	public static boolean isStreamingCache() {
		return GsonUtility.optStringValue(cacheXmlJson, "cacheStrategy", "").equals("stream");
	}
	
	public static int getInMemoryCacheSize() {
		return GsonUtility.optIntValue(cacheXmlJson, "inMemoryCacheSize", 1000);
	}
	
	public static boolean enableParallelReportGeneration() {
		return GsonUtility.optBooleanValue(cacheXmlJson, "enableParallelReportGeneration", false);
	}
	
	public static int getQueueCapacity() {
		return GsonUtility.optIntValue(cacheXmlJson, "queueCapacity",0);
	}
	
	public static HCRPrintCacheConfigurationDTO getHCRPrintCacheConfig() {
		JsonObject hcr =  GsonUtility.optJsonObjectOrEmpty(cacheXmlJson, "hcr");
		JsonObject print = GsonUtility.optJsonObjectOrEmpty(hcr, "print");
		return GsonUtility.deserialize(print, HCRPrintCacheConfigurationDTO.class);
	}
	
	public static ApplicationCacheStore getCacheStore() {
		String url  = "in-memory";
		String beanName = getChildClass(url, "cacheStore");
		if (beanName != null) {
			return (ApplicationCacheStore) ApplicationContextAccessor.getBean(beanName);
		} else {
			return null;
		}
	}
		
	/**
	 * Retrieves the cache name from the connection details in the form data.
	 * 
	 * @param formData JSON object containing form data for report generation
	 * @return the cache name as a string
	 */
	public static String getCacheNameFromConnection(JsonObject formData) {
		JsonObject connectionDetails = formData.getAsJsonObject("connectionDetails");
		String cacheName = getCacheName(connectionDetails);
		
		if ( StringUtils.isNotBlank(cacheName)) return cacheName;
		
		else if (formData.has("designerProperties")) {
			JsonObject designerProperties = GsonUtility.optJsonObject(formData, "designerProperties");
			JsonArray array = GsonUtility.optJsonArray(designerProperties, "dataSets");
			if ( array != null && !array.isEmpty()) {
				for(JsonElement dataSetObj : array) {
					if(dataSetObj instanceof JsonObject dataSetJson) {
						JsonObject dataSetConnectionDetails =  GsonUtility.optJsonObjectOrEmpty(dataSetJson, "connectionDetails");
						cacheName = getCacheName(dataSetConnectionDetails);
						if ( StringUtils.isNotBlank(cacheName)) {
							return cacheName;
						}
					}
				}
			}
		}
		else {
			if (formData.has("saveDetails") && !formData.getAsJsonObject("saveDetails").entrySet().isEmpty()) {
				cacheName = formData.getAsJsonObject("saveDetails").get("uuid").getAsString() + "."
						+ JsonUtils.getHCRExtension();
			}
		}
		return cacheName;
	}
	
	private static final String getCacheName(JsonObject connectionDetails) {
		if (!connectionDetails.entrySet().isEmpty() && connectionDetails.has("temp_uuid")) {
			return  connectionDetails.get("temp_uuid").getAsString();
		}
		return "";
	}
	
}
