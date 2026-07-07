package com.helicalinsight.admin.management;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.admin.model.FileBrowserCache;
import com.helicalinsight.admin.service.FileBrowserCacheService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.JsonUtils;
import com.helicalinsight.efw.HIManagedCallableThread;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.DirectoryLoader;
import com.helicalinsight.efw.resourceloader.RulesInjector;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Rajesh
 *         Created by helical019 on 4/4/2019.
 */
public class FileBrowserSearchHandler implements IComponent {
    //private static ThreadPoolExecutor executor;
    private static Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.getProjectPropertiesFile();
    private String paginationThreshold = mapFromClasspathPropertiesFile.get("cache.file_browser_search_pagination_threshold");

    @Override
    public String executeComponent(String formData) {
        if (!com.helicalinsight.efw.utility.JsonUtils.isFileBrowserCacheEnabled()) {
            return prepareEmptyResult();
        }
        JsonObject jsonFormData = new Gson().fromJson(formData,JsonObject.class);
        String searchElement = jsonFormData.get("searchElement").getAsString();
        String filterType = GsonUtility.optString(jsonFormData, "type");
        Boolean isForReportStats = GsonUtility.optBoolean(jsonFormData, "isForReportStats");
        if (searchElement == null) {
            throw new EfwdServiceException("Search element is empty.");
        }
        if ("".equalsIgnoreCase(filterType) && "".equalsIgnoreCase(searchElement)) {
            return prepareEmptyResult();
        }
        JsonObject settingsJson = com.helicalinsight.efw.utility.JsonUtils.newGetSettingsJson();
        JsonObject jsonOfExtensions = settingsJson.getAsJsonObject("Extentions");

        JsonArray searchTypeArray = null;
        if (filterType.contains("[") && filterType.contains("]")) {
            searchTypeArray = new Gson().fromJson(filterType,JsonArray.class);
            if (searchTypeArray.isEmpty()) {
                throw new EfwdServiceException("Search element is empty.");
            }
            if (searchTypeArray.size() == 1) {
                filterType = searchTypeArray.get(0).getAsString();
                searchTypeArray = null;
            }
        }
        JsonArray listOfKeys = new JsonArray();
        if (searchTypeArray != null) {
            List<String> visibleExtension = checkVisibleExtension(searchTypeArray, jsonOfExtensions);
            String extension = new Gson().toJson(visibleExtension);
            searchTypeArray = new Gson().fromJson(extension,JsonArray.class);
            listOfKeys.addAll(searchTypeArray);
        } else {
            if (!filterType.isEmpty() && !"all".equalsIgnoreCase(filterType)) {
                List<String> visibleExtension = checkVisibleExtension(filterType, jsonOfExtensions);
                if (visibleExtension.size() == 0) {
                    return prepareEmptyResult();
                }
                filterType = visibleExtension.get(0);
                listOfKeys.add(filterType);
            }
        }


       /* if ("efwvf".equalsIgnoreCase(filterType)||"efwd".equalsIgnoreCase(filterType)) {
            listOfKeys.add(filterType);
        }*/


        JsonArray jsonArrayResult = new JsonArray();
        FileBrowserCacheService fileBrowserCacheService = ApplicationContextAccessor.getBean(FileBrowserCacheService.class);
        List<FileBrowserCache> listOfFileBrowserCache;
        if (searchTypeArray != null) {
            listOfFileBrowserCache = fileBrowserCacheService.getResultByQuery(searchElement, searchTypeArray);
        } else {
            listOfFileBrowserCache = fileBrowserCacheService.getResultByQuery(searchElement, filterType);
        }
        if (listOfFileBrowserCache.isEmpty()) {
            return prepareEmptyResult();
        }
        // Inject rules for not visible extensions.
        List<String> listKeys =  new Gson().fromJson(listOfKeys, new TypeToken<List<String>>() {}.getType());
        RulesInjector rulesInjector = new RulesInjector(listKeys, jsonOfExtensions);
        rulesInjector.injectRules();
        List<Map<String, String>> listOfMap;
        if (isForReportStats)
            listOfMap = prepareResultInParallel(listOfFileBrowserCache, true, true, jsonOfExtensions);
        else {
            //prepare multi threaded way
            listOfMap = prepareResultInParallel(listOfFileBrowserCache, true, false, jsonOfExtensions);
            // listOfMap = directoryLoader.prepareOnlyPermission(listOfFileBrowserCache, true, false);
        }
        jsonArrayResult = new Gson().fromJson(new Gson().toJson(listOfMap),JsonArray.class);

        JsonObject result = new JsonObject();
        result.add("searchResult", jsonArrayResult);
        return result.toString();
    }

    private String prepareEmptyResult() {
        JsonObject result = new JsonObject();
        result.add("searchResult", new JsonArray());
        return result.toString();
    }

    private List<String> checkVisibleExtension(Object key, JsonObject jsonOfExtensions) {
        List<String> visibleExtensions = new ArrayList<>();
        Map<String, List<String>> visibleExtensionsMap = JsonUtils.prepareExtensionsMap(jsonOfExtensions);
        if (key instanceof String) {
            String requestedExtension = (String) key;
            prepareArray(visibleExtensions, visibleExtensionsMap, requestedExtension);

        } else if (key instanceof JsonArray) {
            JsonArray requestedExtension = (JsonArray) key;
            for (int index = 0; index < requestedExtension.size(); index++) {
                prepareArray(visibleExtensions, visibleExtensionsMap, requestedExtension.get(index).getAsString());
            }
        }
        return visibleExtensions;
    }

    private void prepareArray(List<String> visibleExtensions, Map<String, List<String>> visibleExtensionsMap, String requestedExtension) {
        List<String> entryResult = visibleExtensionsMap.get(requestedExtension);
        if (entryResult != null && entryResult.size() > 0) {
            visibleExtensions.add(entryResult.get(0));
        }
    }

    private List<Map<String, String>> prepareResultInParallel(List<FileBrowserCache> listOfFileBrowserCache, boolean flag1, boolean flag2, JsonObject visibleExtensionTags) {
        List<List<FileBrowserCache>> listOfPaginatedList = JsonUtils.preparePaginatedList(listOfFileBrowserCache, Integer.parseInt(paginationThreshold));
        List<Map<String, String>> listOfMap = new ArrayList<>();
        List<Callable<List<Map<String, String>>>> listOfCallable = new ArrayList<>();
        List<Future<List<Map<String, String>>>> futures = new ArrayList<>();
        String threshold = mapFromClasspathPropertiesFile.get("cache.file_browser_search_threshold");
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.valueOf(threshold));
        for (int index = 0; index < listOfPaginatedList.size(); index++) {
            List<FileBrowserCache> fileBrowserCaches = listOfPaginatedList.get(index);
            listOfCallable.add(new HIManagedCallableThread(fileBrowserCaches, flag1, flag2, new DirectoryLoader(visibleExtensionTags)));
        }
        try {
            futures = executor.invokeAll(listOfCallable);
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Future<List<Map<String, String>>> eachFuture : futures) {
            try {
                listOfMap.addAll(eachFuture.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        return listOfMap;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }


}
