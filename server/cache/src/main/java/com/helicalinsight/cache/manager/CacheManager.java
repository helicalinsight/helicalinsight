package com.helicalinsight.cache.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.concurrent.StreamedResultset;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import javax.sql.rowset.CachedRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

/**
 * @author Somen
 *         Created  on 9/3/2015.
 */
public abstract class CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    public abstract void setRequestData(String data);

    public abstract String getRequestData();

    public abstract String getConnectionFilePath();

    public abstract Long getConnectionId();

    public abstract Integer getMapId();

    public abstract String getConnectionType(Long connectionId);

    public abstract String getQuery(String connectionType);

    public abstract Object getDataFromDatabase(String query);
    
    public void  streamDataFromDatabase(String query, CallBack<ResultSet> callBack) {
    	
    }

    public abstract String getDirectory();

    public abstract boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                               Object object);

    public void saveToDisk(Cache requestCache, String directory, Object jsonData) {
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        String cacheUUID = UUID.randomUUID().toString();
        if (directory == null) {
            directory = "TEMP_DIRECTORY";
        }
        String cacheFilePath = directory +"/"+ cacheUUID + "." + CacheUtils.getCacheExtension();
        String cacheFileSavePath = CacheUtils.getCacheDirectory() + "/" + cacheFilePath;
        requestCache.setCacheFilePath(cacheFilePath);
        if (logger.isInfoEnabled()) {
            logger.info("File created with the file name " + cacheFilePath);
        }
        boolean saved = true;
        try {

            Boolean aBoolean = CacheUtils.saveFileToDisk(jsonData, cacheFileSavePath);
            //Save the details in the database
            if (aBoolean) {
                File file = new File(cacheFileSavePath);
                requestCache.setCacheFileTimeStamp(new Date(file.lastModified()));

                requestCache.setCacheExpiryTime(new Date(CacheUtils.getActualCacheExpireDuration()));

                requestCache.setCacheFileSize(file.length());

                cacheService.editCache(requestCache);
            }else{
                logger.error("Could not save the cache file ");
                cacheService.deleteCache(requestCache.getCacheId());
            }
        } catch (Exception ex) {
            saved = false;
            logger.error("Exception occurred ", ex.getMessage());
        } finally {
            if (!saved) {
                cacheService.deleteCache(requestCache.getCacheId());
            }
        }
    }

    public Object readFileContent(String filePath) throws IOException {
        JsonObject recoveredJsonObject = null;
        
        File file = new File(filePath);
        if ( file.isDirectory()) {
        	return new StreamedResultset(filePath);
        }
        
        InputStream buffer = new BufferedInputStream(new FileInputStream(filePath));
        try (ObjectInput input = new ObjectInputStream(buffer)) {
            Object undeterminedObject = input.readObject();
            if (undeterminedObject instanceof String) {
                String object = (String) undeterminedObject;
                JsonElement element = new Gson().fromJson(object, JsonElement.class);
                recoveredJsonObject = element.getAsJsonObject();
            }
            if (undeterminedObject instanceof CachedRowSet) {
                return undeterminedObject;
            }
        } catch (ClassNotFoundException ex) {
            logger.error("The class not found ", ex);
        } catch (FileNotFoundException  ex) {
        	logger.error("File not found ", ex);
		} 
        finally {
            buffer.close();
        }
        return recoveredJsonObject;
    }
}
