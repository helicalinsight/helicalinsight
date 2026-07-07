package com.helicalinsight.export.components;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import jakarta.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.AdhocCacheManager;
import com.helicalinsight.adhoc.jreport.IHCRGenerator;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.manager.EfwvfCacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManagerForResultSet;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.datasource.ActiveQueryRegistry;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.IDownload;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The `DownloadCacheManager` class is a cache manager for handling downloads. It extends the `CacheManager` class.
 * This class is responsible for serving cached content, saving content to a
 * temporary directory, and managing various download formats such as XLS, CSV, etc.
 */
@Component
@Scope("prototype")
public class DownloadCacheManager extends com.helicalinsight.cache.manager.CacheManager {

    private final static Map<String, String> propertyMap = ControllerUtils.getPropertyMap();

    private static final Logger logger = LoggerFactory.getLogger(DownloadCacheManager.class);
    private static Map<String, String> settingsDownloadMap = new HashMap<>();

    private JsonObject requestParameterJson;
    private String contentType;
    private IDownload iDownload;
    private String fileExtension;
    private boolean isAdhoc;
    private boolean isHCR;
    private String hcrReportName;

    @Autowired
    private AdhocCacheManager adhocCacheManager;

    @Autowired
    private EfwvfCacheManager efwvfManager;

    private CacheManager cacheManager;
    
    

    /**
     * Initializes the content of the `DownloadCacheManager`. Reads settings from the cache XML.
     */
	@PostConstruct
	public void init() {
        logger.debug("initializing content of Download Manager");

        JsonArray downloadMapArray = CacheUtils.getCacheXmlJson().getAsJsonObject("downloadManager").getAsJsonArray("contentType");
        for (int mapIndex = 0; mapIndex < downloadMapArray.size(); mapIndex++) {
            JsonObject settingsDataSource = downloadMapArray.get(mapIndex).getAsJsonObject();
            String downloadFormatType = settingsDataSource.get("type").getAsString();
            String clazz = settingsDataSource.get("bean").getAsString();
            settingsDownloadMap.put(downloadFormatType, clazz);
        }
    }
	/**
	 * Retrieves the appropriate instance of the CacheManager based on the current state.
	 *
	 * @return The instance of CacheManager (AdhocCacheManager, HCRQueryProcessCacheManager, or EfwvfCacheManager).
	 */
    private CacheManager getCacheManager() {
        if (isAdhoc) {
            return adhocCacheManager;
        } else if (isHCR) {
        	return JsonUtils.getHCRDefaultGeneratorType().equalsIgnoreCase("regular") ? 
        			ApplicationContextAccessor.getBean(HCRQueryProcessCacheManagerForResultSet.class) 
        			:  ApplicationContextAccessor.getBean(HCRQueryProcessCacheManager.class);  
        } else {
            return efwvfManager;
        }
    }
    /**
     * Retrieves the file path associated with the connection of the cache manager.
     * @return A string representing the file path of the connection.
     */
    @Override
    public String getConnectionFilePath() {
        return cacheManager.getConnectionFilePath();
    }
    /**
     * Fetch the unique identifier (ID) associated with the connection of the cache manager.
     * @return A Long representing the connection ID.
     */
    @Override
    public Long getConnectionId() {
        return cacheManager.getConnectionId();
    }
    /**
     * Retrieves the map ID associated with the cache manager.
     * @return An Integer representing the map ID.
     */
    @Override
    public Integer getMapId() {
        return cacheManager.getMapId();
    }
    /**
     * Retrieves the type of connection using connection ID.
     *
     * @param connectionId 		Long representing the connection ID.
     * @return A string representing the connection type.
     */
    @Override
    public String getConnectionType(Long connectionId) {
        return cacheManager.getConnectionType(connectionId);
    }
    /**
     * Retrieves the query associated with the given connection type.
     *
     * @param connectionType 		String representing the connection type.
     * @return A string representing the query associated with the connection type.
     */
    @Override
    public String getQuery(String connectionType) {
        return cacheManager.getQuery(connectionType);
    }
    /**
     * @param query		sql query
     * @return the ResultSet of query.
     */
    @Override
    public Object getDataFromDatabase(String query) {
        return cacheManager.getDataFromDatabase(query);
    }
    
    @Override
    public void streamDataFromDatabase(String query, CallBack<ResultSet> callBack) {
    	cacheManager.streamDataFromDatabase(query, callBack);
    }
    
    /**
     * @return the directory of file.
     */
    @Override
    public String getDirectory() {
        return cacheManager.getDirectory();
    }
    /**
     * This method Writes the content to the
     * response output stream. Handles different download formats and manages the temporary directory.
     *
     * @param request      The `HttpServletRequest` object provides report name, id, type,path;
     * @param response     The `HttpServletResponse` object to set the content.
     * @param rawObject    The raw object containing the cached content type.
     * @return {@code true} if the content is successfully served, otherwise {@code false}
     */
    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      Object rawObject) {
        Object object = null;
        String requestId = null;
        ActiveQueryRegistry registry=ActiveQueryRegistry.getRegistry();
        try {
            JsonObject fileContent = null;
            ResultSet resultSet = null;

            if (rawObject instanceof JsonObject) {
                fileContent = (JsonObject) rawObject;

            }

            if (rawObject instanceof ResultSet) {
                resultSet = (ResultSet) rawObject;
            }
            String attachmentName = null;
            if (request != null && response != null) {
                ControllerUtils.addCookie(request, response, "hi_report_downloadStatus", "1");
                response.setContentType(getContentTypeString());
                HttpSession session = request.getSession(false);
                String repName=request.getParameter("reportName");
                attachmentName = ReportsUtility.getReportName(repName);

            }
            logger.info("Downloading the content......");
            JsonArray jsonDataArray = null;
            if (isAdhoc) {
                if (resultSet != null) {
                    //                 Object object = getBinaryObject(resultSet);
                    object = getBinaryObject(resultSet);
                }
                //               jsonDataArray = fileContent.getAsJsonObject("response").getAsJsonArray("data");
            } else if (isHCR) {
                attachmentName = ReportsUtility.getReportName(hcrReportName);
                if (rawObject instanceof JsonObject) {
                    fileContent.remove("lastModified");

                    if (fileContent.size() == 0) {
                        jsonDataArray = new JsonArray();
                    } else {
                        jsonDataArray = fileContent.getAsJsonArray("data");
                    }
                } else {
                    jsonDataArray = new JsonArray();
                }
                object = getBinaryObjectJsonArray(jsonDataArray);

            } else {

                jsonDataArray = fileContent.getAsJsonArray("data");
                object = getBinaryObjectJsonArray(jsonDataArray);
            }

            if (request == null && response == null) {
                //Object object, File fileToDownload, String tempDir
                return saveToTemp(object);
            }
            OutputStream outputStream = response.getOutputStream();
            String sendFileName = request.getParameter("sendFileName");
            requestId=request.getParameter("requestId");
            if (sendFileName != null && "true".equals(sendFileName)) {
                response.setHeader("Content-Type", "text/html");
               // this.requestParameterJson.put("reportName", attachmentName);
                saveToTemp(object);
                outputStream.write(this.requestParameterJson.get("fileToDownload").getAsString().getBytes());
                ApplicationUtilities.closeResource(outputStream);
                return true;
            }
            String dispositionType = request.getParameter("print") != null ? "inline; " : "attachment; ";
            response.setHeader("Content-Disposition", String.format(dispositionType + "filename=\"%s\"",
                    attachmentName + getFileExtension()));
            String resultNameTag = attachmentName == null ? ReportsUtility.getReportName(null) : attachmentName;
            String tempDir = TempDirectoryCleaner.getTempDirectory() + File.separator +
                    attachmentName + getFileExtension();
            File fileToDownload = new File(tempDir);
            FileInputStream fileInputStream;
            if (object instanceof StringBuilder) {
                StringBuilder responseObject = (StringBuilder) object;
                ApplicationUtilities.createAFile(fileToDownload, responseObject.toString());
                fileInputStream=new FileInputStream(fileToDownload);
                outputStream.write(responseObject.toString().getBytes());
                fileInputStream.close();
            } else if (object instanceof XSSFWorkbook) {
                try {
                    XSSFWorkbook workbook = (XSSFWorkbook) object;
                    FileOutputStream fileOutputStream = new FileOutputStream(tempDir);
                    workbook.write(fileOutputStream);
                    fileInputStream=new FileInputStream(fileToDownload);
                    workbook.write(outputStream);
                    ApplicationUtilities.closeResource(fileOutputStream);
                    ApplicationUtilities.closeResource(fileInputStream);
                } catch (ClassCastException exception) {
                    logger.error("Casting exception occurred {}", exception);
                    return false;
                }
            } else if (object instanceof JsonObject) {
                JsonObject responseFromHCR = (JsonObject) object;
                String uuid = responseFromHCR.getAsJsonObject("jrxmlData").get("uuid").getAsString();
                String dir = TempDirectoryCleaner.getTempDirectory() + File.separator +
                        uuid + getFileExtension();
                logger.debug("temp-dir_exported_file_name: " + dir);
                fileInputStream = new FileInputStream(dir);

                IOUtils.copy(fileInputStream, outputStream);
                fileInputStream.close();
                outputStream.close();
            } else if (object != null) {
            	FileOutputStream fileOutputStream = new FileOutputStream(tempDir);
                outputStream.write(object.toString().getBytes());
                fileOutputStream.write(object.toString().getBytes());
            }
            if(fileToDownload!=null)
            	response.setHeader("Content-Length", Long.toString(fileToDownload.length()));
            outputStream.close();
            outputStream.flush();
            ControllerUtils.saveFile(request, resultNameTag, fileToDownload);
        } catch (Exception ignore) {
            logger.error("Exception occurred at download cache manager ", ignore);

            return false;
        }
        finally {
        	if(requestId!=null)
        		registry.deregisterFileProcessThread(requestId);
        }
        return true;
    }
    /**
     * Saves the given object to a temporary directory. The path of the saved file is added to the request parameter JSON.
     *
     * @param object 		The object to be saved.
     * @return {@code true} if the object is successfully saved to the temporary directory, otherwise {@code false}.
     */
    public boolean saveToTemp(Object object) {
        String attachmentName = ReportsUtility.getReportName(GsonUtility.optString(this.requestParameterJson,"reportName"));
        String tempDir = TempDirectoryCleaner.getTempDirectory() + File.separator +
                attachmentName + getFileExtension();
        File fileToDownload = new File(tempDir);
        if (object instanceof StringBuilder) {
            StringBuilder responseObject = (StringBuilder) object;
            ApplicationUtilities.createAFile(fileToDownload, responseObject.toString());
        } else if (object instanceof XSSFWorkbook) {
            try {
                XSSFWorkbook workbook = (XSSFWorkbook) object;
                FileOutputStream fileOutputStream = new FileOutputStream(tempDir);
                workbook.write(fileOutputStream);
                ApplicationUtilities.closeResource(fileOutputStream);
            } catch (ClassCastException exception) {
                logger.error("Casting exception occurred {}", exception);
                return false;
            } catch (IOException e) {
                logger.error("I/O exception occurred {}", e);
                return false;
            }
        }
        this.requestParameterJson.addProperty("fileToDownload", tempDir);
        return true;
    }

    public String getContentTypeString() {
        return this.contentType;
    }
    /**
     * Retrieves the binary object from a JsonArray for download.
     *
     * @param dataArray 		 JsonArray containing the data.
     * @return The binary object for download.
     */
    public Object getBinaryObjectJsonArray(JsonArray dataArray) {

        if (iDownload != null) {
            return iDownload.downloadFormat(dataArray, requestParameterJson);
        } else {
            logger.info("Cannot instantiate the class for IDownload");
            return null;
        }
    }
    /**
     * Retrieves the binary object from a ResultSet for download.
     *
     * @param resultSet 		ResultSet containing the data.
     * @return The binary object for download.
     */
    public Object getBinaryObject(ResultSet resultSet) {

        if (iDownload != null) {
            return iDownload.downloadFormat(resultSet, requestParameterJson);
        } else {
            logger.info("Cannot instantiate the class for IDownload");
            return null;
        }
    }
    /**
     * @return the formdata in string format;
     */
    @Override
    public String getRequestData() {
        return this.requestParameterJson.toString();
    }
    /**
     * Method initializes properties (content type, file extension, etc.), sets up the cache manager, and handles special cases for reports (HCR) and XLS format.
     * @param data 		JSON data representing the request parameters.
     */
    @Override
    public void setRequestData(String data) {
        requestParameterJson = new Gson().fromJson(data,JsonObject.class);
        String downloadType = GsonUtility.optString(requestParameterJson, "type");
        isHCR = GsonUtility.optBooleanValue(requestParameterJson, "isHCR", false);
        // Set the content type for the response from the properties file
        this.contentType = (propertyMap.get(downloadType));

        if (downloadType == null || downloadType.isEmpty()) {
            //Default type being csv format
            this.fileExtension = ".csv";
            downloadType = "csv";
        } else if (isHCR) {
            this.fileExtension = "." + GsonUtility.optStringValue(requestParameterJson,"format", "pdf").toLowerCase();
            downloadType = JsonUtils.getHCRExtension();
            this.hcrReportName = GsonUtility.optStringValue(requestParameterJson,"reportName", "");
        } else if(downloadType.equalsIgnoreCase("xls")||downloadType.equalsIgnoreCase("xlsx")) {
            //To fix:Bug 3187
            this.fileExtension = "."+downloadType;
            downloadType = "xls";
        }else{
            this.fileExtension = "." + downloadType;
        }

        String adhocTrue = GsonUtility.optString(requestParameterJson,"isAdhoc");
        String beanName = settingsDownloadMap.get(downloadType);
        iDownload = (IDownload) ApplicationContextAccessor.getBean(beanName);
        isAdhoc = "true".equalsIgnoreCase(adhocTrue);
        cacheManager = this.getCacheManager();
        cacheManager.setRequestData(data);
    }
    /**
     * @return file extension in string format.
     */
    public String getFileExtension() {
        return this.fileExtension;
    }
    /**
     * Writes the content of a file from a FileInputStream to an OutputStream in chunks of 4096 bytes.
     *
     * @param totalBytes     The total number of bytes to be written.
     * @param outputStream   The OutputStream to which the file content is written.
     * @param fileInputStream The FileInputStream from which the file content is read.
     * @param requestId      A unique identifier for tracking the progress, can be null if not needed.
     * @param registry       The ActiveQueryRegistry for managing thread-specific file processing data.
     * @throws IOException   If an I/O error occurs during the file streaming process.
     */
    public static void writeFileToStream(Long totalBytes,OutputStream outputStream,FileInputStream fileInputStream,
    		String requestId,ActiveQueryRegistry registry) throws IOException {
    	
    	JsonObject response;
    	int rounds=(int) (totalBytes/4096L);
    	int bytesRead,eachRound=1;
    	if(rounds==0 && requestId!=null)
    		registry.registerOrPutThreadFileProcessData(requestId, 99);
    	if(rounds>0 && rounds<100)
    		eachRound=(int)Math.ceil(100/rounds);
        byte[] buffer = new byte[4096];
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
			if (requestId!=null) {
				response = registry.getFileProcessedPercentage(requestId);
				if (response.entrySet().isEmpty() || response.get("percentage").getAsInt() < 100)
					registry.registerOrPutThreadFileProcessData(requestId, eachRound);
			}
        }
    }

}
