package com.helicalinsight.adhoc.cachemanager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.jreport.IHCRGenerator;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * HCRPrintCacheManager class extends {@link CacheManager}
 * This class is responsible for serving cache details , connection details ,Provides data or resources of JasperPrint instances,
 * for viewing or exporting reports .
 * Created by author on 1/21/2020.
 * @author Rajesh
 */
@Component
@Scope("prototype")
public class HCRPrintCacheManager extends CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(HCRPrintCacheManager.class);
    private JsonObject formData;
    private IHCRGenerator generator = (IHCRGenerator) ApplicationContextAccessor.getBean(JsonUtils.getHCRGeneratorType());
    private JasperPrint jasperOriginalPrint;
    private JasperPrint clonedJasperPrint;
    private JsonObject saveDetails;
    private JsonObject requestParameterJson;
    private JsonObject efwdFileAsJson;
    private EnhancedQueryExecutor queryExecutor;
    private String efwdFile;
    private Integer mapId;
    private ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private int requestedPage;
    private Integer noOfRecords;

    public int getRequestedPage() {
        return requestedPage;
    }

    public void setRequestedPage(int requestedPage) {
        this.requestedPage = requestedPage;
    }

    public Integer getNoOfRecords() {
        return noOfRecords;
    }

    public void setNoOfRecords(Integer noOfRecords) {
        this.noOfRecords = noOfRecords;
    }

    public JasperPrint getResult() {
        return this.clonedJasperPrint;
    }
    /**
     * Method returns the path of saved HCR file.
     * @return file path in string format.
     */
    @Override
    public String getConnectionFilePath() {
        JsonObject saveDetails = formData.getAsJsonObject("saveDetails");
        this.saveDetails = saveDetails;
        String dir = saveDetails.get("dir").getAsString();
        String uuid = saveDetails.get("uuid").getAsString();
        return dir + File.separator + uuid + "." + JsonUtils.getHCRExtension();
    }
    /**
     * Retrieves connection details from formData, including directory and efwd.
     * @return connection id in long format.
     */
    @Override
    public Long getConnectionId() {
        JsonObject connectionDetails = formData.getAsJsonObject("connectionDetails");
        if (!connectionDetails.entrySet().isEmpty()) {
            if (!connectionDetails.has("dir")) {
                return -404l;
            }
            int mapId =Integer.parseInt(connectionDetails.get("map_id").getAsString());
            int connectionId = 0;
            JsonObject efwdData= GsonUtility.optJsonObject(connectionDetails, "efwd");
            if(efwdData==null) {
				JsonObject efwdJson = GsonUtility.optJsonObject(this.requestParameterJson,"efwd");
				String outerDir =  GsonUtility.optStringValue(this.requestParameterJson,"dir", "_TEMP_DIR_");
				String file = null;
				if (efwdJson != null) {
					file = GsonUtility.optString(efwdJson,"file");
					String dir = GsonUtility.optString(efwdJson,"dir");
					if (dir != null && !dir.isEmpty()) {
						outerDir = dir;
					}
				}
				queryExecutor = new EnhancedQueryExecutor(requestParameterJson.toString(), applicationProperties);
				efwdFileAsJson = queryExecutor.newReadEFWD(outerDir, file, applicationProperties.getSolutionDirectory());
				efwdFile = efwdFileAsJson.get("_efwdFileName_").getAsString();

				if (efwdFile != null) {
                    JsonElement dataMaps = this.efwdFileAsJson.get("DataMaps");
                    JsonArray dataMapsArray= new JsonArray();
                    if(dataMaps instanceof JsonObject){
                        dataMapsArray.add(((JsonObject)dataMaps).getAsJsonObject("DataMap"));
                    }
                    if(dataMaps instanceof  JsonArray){
                        dataMapsArray=this.efwdFileAsJson.getAsJsonArray("DataMaps");
                    }
					for (int counter = 0; counter < dataMapsArray.size(); counter++) {
						JsonObject dataMapTag = dataMapsArray.get(counter).getAsJsonObject();
						if (mapId == dataMapTag.get("id").getAsInt()) {
							this.mapId = mapId;
							// dataMapTagContent = (JSONObject) dataMapsArray.get(counter);
							connectionId = dataMapTag.get("connection").getAsInt();
							break;
						}
					}

				}
            }else {
                JsonArray dataMapsJSONArray = efwdData.getAsJsonArray("dataMaps");
				for (int counter = 0; counter < dataMapsJSONArray.size(); counter++) {
					JsonObject dataMapTag = dataMapsJSONArray.get(counter).getAsJsonObject().getAsJsonObject("dataMap");
					if (mapId == dataMapTag.get("id").getAsInt()) {
						this.mapId = mapId;
						connectionId = dataMapTag.get("connection").getAsInt();
						break;
					}
				}
            }
            JsonObject accessChecker = new Gson().fromJson(connectionDetails,JsonObject.class);
            accessChecker.addProperty("id", connectionId);
            accessChecker.addProperty("access", DataSourceSecurityUtility.EXECUTE);
            DataSourceSecurityUtility.isDataSourceAuthenticated(accessChecker);

            return (long) connectionId;
        }
        return -404l;

    }
    /**
     * @return mapId;
     */
    @Override
    public Integer getMapId() {
        if (this.mapId == null)
            return -401;
        return this.mapId;
    }
    /**
     * Provides efwd type .
     * @return connectionType
     */
    @Override
    public String getConnectionType(Long connectionId) {
        String type = "JUST_TO_IGNORE";
        if (connectionId != null && connectionId != 0 && efwdFileAsJson != null) {
            JsonArray dataSources = efwdFileAsJson.getAsJsonArray("DataSources");
            for (int counter = 0; counter < dataSources.size(); counter++) {
                JsonObject connection = dataSources.get(counter).getAsJsonObject();
                if (connectionId == connection.get("id").getAsInt()) {
                    //  connectionDetails = connection;
                    type = connection.get("type").getAsString();
                    break;
                }
            }
        }
        return type;
    }
    /**
     * Method returns query.
     * @param connectionType    not used in this method.
     * @return sql query from if queryExecutor is {@code null} then it returns "JUST_TO_IGNORE".
     */
    @Override
    public String getQuery(String connectionType) {
        String query = "JUST_TO_IGNORE";
        if (!requestParameterJson.entrySet().isEmpty() && queryExecutor != null) {
            query = queryExecutor.getQuery();
        }
        return query;
        // return SplitterUtils.prepareServiceId(requestParameterJson.toString() + formData + query);
    }
    /**
     * Retrieves a JasperPrint instance.
     * @param query         not used in this method.
     * @return {@code JasperPrint} instance.
     */
    @Override
    public JasperPrint getDataFromDatabase(String query) {
    	
        JasperPrint jasperPrint = generator.generateHCRPrint(formData);
        setNoOfRecords(jasperPrint.getPages().size());
        return jasperPrint;
    }

    @Override
    public String getDirectory() {
        return GsonUtility.optStringValue(this.saveDetails,"dir", null);
    }
    /**
     * Provides data or resources of JasperPrint instances, for viewing or exporting reports .
     * @param request 		  not used
     * @param response        not used
     * @param object 		  JasperPrint instance.
     * @return  {@code true} if the cached content is successfully served, false otherwise.
     */
    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response, Object object) {
        boolean isExport = GsonUtility.optBooleanValue(formData,"isExport", false);
        if (formData.has("noOfRecords")) {
            setNoOfRecords(formData.get("noOfRecords").getAsInt());
            clonedJasperPrint = (JasperPrint) object;
            return (clonedJasperPrint != null && !clonedJasperPrint.getPages().isEmpty());
        } else {
            jasperOriginalPrint = (JasperPrint) object;
            if (isExport) {
                clonedJasperPrint = new JasperPrint();
                clonedJasperPrint.copyFrom(jasperOriginalPrint);
            } else {
                clonedJasperPrint = new JasperPrint();
                clonedJasperPrint.copyFrom(jasperOriginalPrint);
                logger.info("designCopyJasperPrint Size before delete:" + clonedJasperPrint.getPages().size());
                int size = jasperOriginalPrint.getPages().size();
                if (size == 0) {
                    return false;
                }
                for (int index = 0; index < size; index++) {
                    clonedJasperPrint.removePage(0);
                }
                clonedJasperPrint.addPage(0, jasperOriginalPrint.getPages().get(requestedPage));
            }
            return (clonedJasperPrint != null && !clonedJasperPrint.getPages().isEmpty());
        }
    }
    /**
     * Saves cache details to disk.
     * @param requestCache       it sets the cache details such as filePath, paramters, date etc.
     * @param directory			 directory of file
     * @param jsonData           JasperPrint instance.
     */
    @Override
    public void saveToDisk(Cache requestCache, String directory, Object jsonData) {
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        String cacheUUID = UUID.randomUUID().toString();
        if (directory == null) {
            directory = "TEMP_DIRECTORY";
        }
        final String finalDirectory = directory;
        boolean saved = true;
        try {
            String cacheFilePath = finalDirectory + File.separator + cacheUUID + "." + CacheUtils.getCacheExtension();
            String cacheDirectory = CacheUtils.getCacheDirectory();
            String cacheFileSavePath;
            requestCache.setCacheFilePath(cacheFilePath);
            if (logger.isInfoEnabled()) {
                logger.info("File created with the file name " + cacheFilePath);
            }
            JasperPrint originalJasperPrint = (JasperPrint) jsonData;
            logger.info("originalJasperPrint Size :" + originalJasperPrint.getPages().size());
            JasperPrint designCopyJasperPrint = new JasperPrint();
            designCopyJasperPrint.copyFrom(originalJasperPrint);
            logger.info("designCopyJasperPrint Size before delete:" + designCopyJasperPrint.getPages().size());
            int size = originalJasperPrint.getPages().size();
            for (int index = 0; index < size; index++) {
                designCopyJasperPrint.removePage(0);
            }
            logger.info("designCopyJasperPrint Size after delete:" + designCopyJasperPrint.getPages().size());
            List<JRPrintPage> pages = originalJasperPrint.getPages();
            JsonObject designerProperties = formData.getAsJsonObject("designerProperties");
            String parameters = GsonUtility.optString(designerProperties, "parameters");
            if(parameters==null){
                requestCache.setParameters(GsonUtility.optString(formData,"parameters"));
            }else {
                requestCache.setParameters(parameters);
            }
            requestCache.setNoOfRecords(pages.size());
            cacheFilePath = directory + File.separator + cacheUUID + "designer." + CacheUtils.getCacheExtension();
            cacheFileSavePath = cacheDirectory + File.separator + cacheFilePath;
            CacheUtils.saveFileToDisk(designCopyJasperPrint, cacheFileSavePath);
            new HIManagedThread(() -> saveAllCacheInDisk(finalDirectory, cacheUUID, cacheDirectory, designCopyJasperPrint, pages)).start();

            File file = new File(cacheFileSavePath);
            requestCache.setCacheFileTimeStamp(new Date(file.lastModified()));
            requestCache.setCacheExpiryTime(new Date(CacheUtils.getActualCacheExpireDuration()));
            requestCache.setCacheFileSize(file.length());
            cacheService.editCache(requestCache);
        } catch (Exception ex) {
            saved = false;
            logger.error("Exception occurred ", ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (!saved) {
                cacheService.deleteCache(requestCache.getCacheId());
            }
        }
    }
    /**
     * Saves cache in disk.
     * @param directory              directory of cache file    
     * @param cacheUUID			     name of cache
     * @param cacheDirectory         directory to save cache
     * @param designCopyJasperPrint  JasperPrint instance.
     * @param pages
     */
    private void saveAllCacheInDisk(String directory, String cacheUUID, String cacheDirectory, JasperPrint designCopyJasperPrint, List<JRPrintPage> pages) {
        String cacheFilePath;
        String cacheFileSavePath;
        for (int index = 0; index < pages.size(); index++) {
            cacheFilePath = directory + File.separator + cacheUUID + "page_" + index + "." + CacheUtils.getCacheExtension();
            cacheFileSavePath = cacheDirectory + File.separator + cacheFilePath;
            JRPrintPage jrPrintPage = pages.get(index);
            CacheUtils.saveFileToDisk(jrPrintPage, cacheFileSavePath);
        }

    }
    /**
     * Reads the content of a cache file stored at the specified file path and returns a JasperPrint instance containing the recovered data.
     *
     * @param filePath 			 path to the cache file containing the serialized JasperPrint data.
     * @return A JasperPrint instance containing the recovered data from the cache file.
     * @throws IOException If an I/O error occurs while reading the cache file.
     */
    @Override
    public JasperPrint readFileContent(String filePath) throws IOException {
        boolean isExport = GsonUtility.optBooleanValue(formData,"isExport", false);
        String cacheExtension = "." + CacheUtils.getCacheExtension();
        String replaceStr = "page_" + this.requestedPage + cacheExtension;
        String replaceStrForDesigner = "designer" + cacheExtension;

        String jasperDesignPrintFilePath = filePath.replace(cacheExtension, replaceStrForDesigner);
        JasperPrint recoveredJasperPrint = null;
        if (isExport) {
            recoveredJasperPrint = (JasperPrint) readFileContent(jasperDesignPrintFilePath, true);
            for (int index = 0; index < noOfRecords; index++) {
                replaceStr = "page_" + index + cacheExtension;
                String filePathWithPageIndex = filePath.replace(cacheExtension, replaceStr);
                recoveredJasperPrint.addPage((JRPrintPage) readFileContent(filePathWithPageIndex, false));
            }
        } else {
            String filePathWithPageIndex = filePath.replace(cacheExtension, replaceStr);
            recoveredJasperPrint = (JasperPrint) readFileContent(jasperDesignPrintFilePath, true);
            recoveredJasperPrint.addPage((JRPrintPage) readFileContent(filePathWithPageIndex, false));
        }

        return recoveredJasperPrint;
    }
    /**
     * Reads the content of a cache file stored at the specified file path and returns the deserialized object.
     *
     * @param filePath 				 path to the cache file.
     * @param isPrint 				 Specifies whether the object to be read is a JasperPrint or JRPrintPage.
     *                               If true, a JasperPrint object is expected; otherwise, a JRPrintPage object is expected.
     * @return The object read from the cache file.
     * @throws IOException If an I/O error occurs while reading the cache file.
     */
    public Object readFileContent(String filePath, boolean isPrint) throws IOException {
        Object returnValue = null;
        InputStream buffer = new BufferedInputStream(new FileInputStream(filePath));
        try (ObjectInput input = new ObjectInputStream(buffer)) {
            if (isPrint)
                returnValue = (JasperPrint) input.readObject();
            else
                returnValue = (JRPrintPage) input.readObject();
        } catch (ClassNotFoundException ex) {
            logger.error("The class not found ", ex);
        } finally {
            buffer.close();
        }
        return returnValue;
    }
    /**
     * @return formData in string format.
     */
    @Override
    public String getRequestData() {
        return formData.toString();
    }
    /**
     * Sets the data from formData to the respective instance variables.
     *
     * @param data The JSON string containing the formData.
     */
    @Override
    public void setRequestData(String data) {
        formData = new Gson().fromJson(data, JsonObject.class);
        requestedPage = GsonUtility.optIntValue(formData,"page", 0);
        if (formData.has("noOfRecords"))
            setNoOfRecords(formData.get("noOfRecords").getAsInt());
        requestParameterJson = formData.getAsJsonObject("connectionDetails");
    }
}
