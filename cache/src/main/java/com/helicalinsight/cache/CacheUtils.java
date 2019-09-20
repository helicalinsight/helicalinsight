/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.cache;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Date;

@Component("cacheUtils")
public class CacheUtils {
    static final long ONE_MINUTE_IN_MILLIS = 60000;
    private static final Logger logger = LoggerFactory.getLogger(CacheUtils.class);
    private static Long cacheExpireDuration;
    private static String durationUnit;
    private static JSONObject cacheXmlJson;

    public static JsonObject readFileContent(String filePath) throws IOException {
        JsonObject recoveredJsonObject = null;
        InputStream buffer = new BufferedInputStream(new FileInputStream(filePath));
        try (ObjectInput input = new ObjectInputStream(buffer)) {
            String object = (String) input.readObject();
            JsonElement element = new Gson().fromJson(object, JsonElement.class);
            recoveredJsonObject = element.getAsJsonObject();
        } catch (ClassNotFoundException ex) {
            logger.error("The class not found ", ex);
        } finally {
            buffer.close();
        }
        return recoveredJsonObject;
    }

    public static synchronized void deleteOldCache(Cache enquiryCache, String physicalCacheFile,
                                                   CacheService cacheService) {
        logger.info("Deleting old Cache file");
        File file = new File(physicalCacheFile);
        if (file.exists()) {
            file.delete();
        }
        cacheService.deleteCache(enquiryCache.getCacheId());
    }

    public static Boolean saveFileToDisk(JsonObject jsonObject, String cacheFileName) {
        Boolean processStatus = false;
        logger.info("Saving file to disk fileName: " + cacheFileName);
        try {
            File file = new File(cacheFileName);
            file.getParentFile().mkdirs();
            OutputStream buffer = new BufferedOutputStream(new FileOutputStream(cacheFileName));
            try (ObjectOutput output = new ObjectOutputStream(buffer)) {
                output.writeObject(jsonObject.toString());
            } finally {
                buffer.close();
            }
            processStatus = true;
        } catch (IOException e) {
            logger.error("Error occurred in file creation", e);
        }
        return processStatus;
    }

    public static Long getActualCacheExpireDuration() {
        logger.info("Actual Expiry date is being computed");

        long currentTime = System.currentTimeMillis();

        if ("hours".equals(durationUnit)) {
            currentTime = currentTime + (cacheExpireDuration * ONE_MINUTE_IN_MILLIS *
                    60);
        } else if ("minutes".equals(durationUnit)) {
            currentTime = currentTime + (cacheExpireDuration * ONE_MINUTE_IN_MILLIS);
        }
        return new Date(currentTime).getTime();
    }


    public static boolean isCacheEnabled() {
        return cacheXmlJson.getString("enableCache").equals("true");
    }

    public static boolean isThreadingEnabled() {
        return cacheXmlJson.getString("enableThread").equals("true");
    }


    public static String getCacheExtension() {
        return cacheXmlJson.getString("cacheExtension");
    }

    public static String getCacheDirectory() {
        return ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Cache";
    }


    public static CacheManager getCacheManager(String url) {
        String beanName = getChildClass(url);
        if (beanName != null) {
            return (CacheManager) ApplicationContextAccessor.getBean(beanName);
        } else {
            return null;
        }
    }

    private static String getChildClass(String url) {
        JSONArray classList = cacheXmlJson.getJSONArray("cacheManager");
        for (int index = 0; index < classList.size(); index++) {
            JSONObject urlJson = classList.getJSONObject(index);
            String pattern = urlJson.getString("@pattern");
            if (url.equalsIgnoreCase(pattern)) {
                return urlJson.getString("@bean");
            }
        }

        return null;
    }

    @PostConstruct
    public static void init() {
        String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
        String cachePath = systemDirectory + File.separator + "Admin" + File.separator +
                "cache.xml";
        if (logger.isInfoEnabled()) {
            logger.info("Cache path is " + cachePath);
        }
        cacheXmlJson = ResourceProcessorFactory.getIProcessor().getJSONObject(cachePath, false);
        cacheExpireDuration = Long.parseLong(cacheXmlJson.getString("cacheExpireDuration"));
        durationUnit = cacheXmlJson.getString("durationUnit");
    }

    public static JSONArray getRefreshUrl() {
        return cacheXmlJson.getJSONArray("refreshUrl");
    }

    public static JSONObject getCacheXmlJson() {
        return cacheXmlJson;
    }
}
