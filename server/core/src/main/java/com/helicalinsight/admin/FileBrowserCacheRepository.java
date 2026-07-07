package com.helicalinsight.admin;

import com.helicalinsight.admin.model.FileBrowserCache;
import com.helicalinsight.admin.service.FileBrowserCacheService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileBrowserContext;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by Rajesh on 3/25/2019.
 */
@Component
public class FileBrowserCacheRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileBrowserCacheRepository.class);
    static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    @Autowired
    private FileBrowserCacheService fileBrowserCacheService;
    @Autowired
    private FileBrowserContext fileBrowserContext;

    private List<String> allVisibleExtension = JsonUtils.getAllVisibleExtension();
    private String folderExtension = JsonUtils.getFolderFileExtension();
    private String efwvfExtension = JsonUtils.getEfwvfExtension();

    public static String replaceBackWardSlashToForward(String path) {
        if (path != null) path = path.replaceAll("\\\\", "/");
        return path;
    }


    public void processFilesAndFolder(String path, int parentId, String parentLogicalPath, boolean isFromWatcher) {
        final int childParent = parentId;
        final String childLogicalPath = parentLogicalPath;
        if (path == null) {
            path = applicationProperties.getSolutionDirectory();
        }
        logger.debug("ProcessFileBrowser :" + path);
        File hiRepositoryPath = new File(path);
        if (hiRepositoryPath.isFile()) {
            processFile(hiRepositoryPath, parentId, parentLogicalPath);
            return;
        }
        String rootDirectoryName = hiRepositoryPath.getName();
        if (notInSystemAndImages(rootDirectoryName)) {
            String relativeSolutionPath = ApplicationUtilities.getRelativeSolutionPath(hiRepositoryPath.toString());
            if (isNotBlank(relativeSolutionPath)) {
                FileBrowserCache fileBrowserCache = saveToDb(hiRepositoryPath, parentId, parentLogicalPath, isFromWatcher);
                parentId = fileBrowserCache.getId();
                parentLogicalPath = fileBrowserCache.getLogicalPath();
            }
        }

        caseIsDirectory(parentId, parentLogicalPath, childParent, childLogicalPath, hiRepositoryPath);
    }

    /* public void processNewFolder(File path, JSONObject efwdFolder, String logicalFolderName) {
         String folderName = path.getName();
         String relativeSolutionPath = ApplicationUtilities.getRelativeSolutionPath(path.getAbsolutePath());
         String relativePath = replaceBackWardSlashToForward(relativeSolutionPath);
         String relativeSolutionPathParent = ApplicationUtilities.getRelativeSolutionPath(path.getParent());
         String relativePathParent = replaceBackWardSlashToForward(relativeSolutionPathParent);
         //FileBrowserCacheService fileBrowserCacheService = ApplicationContextAccessor.getBean(FileBrowserCacheService.class);
         FileBrowserCache parentFileBrowser = fileBrowserCacheService.getFileBrowserByFileName(relativePathParent);
         FileBrowserCache fileBrowserCache = new FileBrowserCache();
         fileBrowserCache.setFileName(folderName);
         fileBrowserCache.setFilePath(relativePath);
         fileBrowserCache.setFileType("FOLDER");
         fileBrowserCache.setParentId(parentFileBrowser != null ? String.valueOf(parentFileBrowser.getId()) : "0");
         fileBrowserCache.setLogicalPath(parentFileBrowser != null ? parentFileBrowser.getLogicalPath() + "/" + logicalFolderName : logicalFolderName);
         fileBrowserCache.setTitle(logicalFolderName);
         int folderId = fileBrowserCacheService.addFileBrowserCache(fileBrowserCache);
         FileBrowserCache indexFolder = new FileBrowserCache();
         indexFolder.setJson(efwdFolder.toString());
         indexFolder.setFileName("index." + JsonUtils.getFolderFileExtension());
         indexFolder.setParentId(String.valueOf(folderId));
         indexFolder.setFileType("FILE");
         indexFolder.setFilePath(relativePath + "/index." + JsonUtils.getFolderFileExtension());
         fileBrowserCacheService.addFileBrowserCache(indexFolder);

     }
 */
    private void caseIsDirectory(int parentId, String parentLogicalPath, int childParent, String childLogicalPath, File hiRepositoryPath) {
        File[] files = hiRepositoryPath.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isFile()) {
                processFile(file, parentId, parentLogicalPath);
            } else {
                String name = file.getName();
                if (notInSystemAndImages(name)) {
                    fileBrowserContext.triggerFileBrowserCache(file.toString(), childParent, childLogicalPath, false);
                }
            }
        }
    }

    private boolean notInSystemAndImages(String rootDirectoryName) {
        return !rootDirectoryName.equalsIgnoreCase("system") && !rootDirectoryName.equalsIgnoreCase("images");
    }

    private void processFile(File file, int parentId, String parentLogicalPath) {
        String actualFileExtension = FilenameUtils.getExtension(file.getAbsolutePath());
        allVisibleExtension.add(folderExtension);
        allVisibleExtension.add(efwvfExtension);
        if (allVisibleExtension.contains(actualFileExtension)) {
            saveToDb(file, parentId, parentLogicalPath, false);
        }
    }

    private FileBrowserCache saveToDb(File file, int parentId, String parentLogicalPath, boolean isFromWatcher) {
        String logicalPath = null;
        String path = file.getAbsolutePath();
        String relativePath = replaceBackWardSlashToForward(ApplicationUtilities.getRelativeSolutionPath(path));
        FileBrowserCache browserCache = new FileBrowserCache();
        String fileName = file.getName();
        browserCache.setFileName(fileName);
        browserCache.setFilePath(relativePath);
        browserCache.setFileType(file.isFile() ? "FILE" : "FOLDER");
        String logicalName = null;
        if (file.isDirectory()) {
            if (isFromWatcher) {
                FileBrowserCache fileBrowserByFileName = fileBrowserCacheService.getFileBrowserByFileName(relativePath);
                logicalName = findFolderLogicalName(file, browserCache, isFromWatcher, fileBrowserByFileName);
            } else
                logicalName = findFolderLogicalName(file, browserCache);
        }
        String jsonData = null;
        if (file.isFile()) {
            String extension = FilenameUtils.getExtension(file.getName());
            JSONObject fileAsJson = JsonUtils.getAsJson(file, true);
            jsonData = fileAsJson.toString();
            if (!(fileName.equals("index." + folderExtension)) && !(extension.equals(efwvfExtension))) {
                logicalName = findFileLogicalName(fileAsJson);
                logicalName = logicalName == null ? FilenameUtils.removeExtension(fileName) : logicalName;
            } else if (extension.equals(efwvfExtension)) {
                logicalName = findFileLogicalNameForEfwvf(fileAsJson);
            }
        }
        browserCache.setTitle(logicalName);
        if (jsonData != null) {
            boolean isSmallerThan2Mb = jsonData.length() < (2 * FileUtils.ONE_MB);
            browserCache.setJson(isSmallerThan2Mb ? jsonData : null);
            browserCache.setIsFileLarge(!isSmallerThan2Mb);
        }

        String parent = file.getParent();
        String parentPath = replaceBackWardSlashToForward(ApplicationUtilities.getRelativeSolutionPath(parent));
        browserCache.setLastModified(file.lastModified());
        if (parentId == 0 || parentLogicalPath == null) {
            FileBrowserCache parentFileBrowserCache = fileBrowserCacheService.findFileBrowserCache(parentPath);
            if (parentFileBrowserCache != null) {
                parentId = parentFileBrowserCache.getId();
                parentLogicalPath = parentFileBrowserCache.getLogicalPath();
                if (logicalName != null)
                    logicalPath = parentLogicalPath + "/" + logicalName;
            } else {
                logicalPath = logicalName;
            }
        } else if (logicalName != null) {
            logicalPath = parentLogicalPath + "/" + logicalName;
        }

        browserCache.setLogicalPath(logicalPath);
        browserCache.setParentId(String.valueOf(parentId));
        try {
            fileBrowserCacheService.addFileBrowserCache(browserCache);
        } catch (CannotCreateTransactionException e) {
            fileBrowserCacheService.addFileBrowserCache(browserCache);
        } catch (Exception e) {
            logger.debug("some Exception..");
            //logger.error("Unable to save the FileBrowserCache :" + browserCache);
        }
        return browserCache;
    }

    private String findFileLogicalNameForEfwvf(JSONObject fileAsJson) {
        String logicalName = "";
        if (fileAsJson.has("Charts")) {
            JSONArray charts = fileAsJson.getJSONArray("Charts");
            JSONArray chartsJsonArray = prepareChartsForEfwvf(charts);
            logicalName = prepareStringFromArray(chartsJsonArray);

        }
        return logicalName;
    }

    private String prepareStringFromArray(JSONArray chartsJsonArray) {
        Set<String> uniqueSet = new TreeSet<>();
        for (int index = 0; index < chartsJsonArray.size(); index++) {
            JSONObject eachJson = chartsJsonArray.getJSONObject(index);
            uniqueSet.add(eachJson.getString("name"));
        }
        return StringUtils.join(uniqueSet, ',');
    }

    private JSONArray prepareChartsForEfwvf(JSONArray charts) {
        JSONArray array = new JSONArray();
        for (int chartNumber = 0; chartNumber < charts.size(); chartNumber++) {
            JSONObject chart = charts.getJSONObject(chartNumber);

            JSONObject aChart = new JSONObject();
            String chartName;
            JSONObject properties = chart.getJSONObject("prop");
            if (properties.has("name")) {
                chartName = properties.getString("name");
            } else {
                chartName = "VF Chart";
            }
            aChart.accumulate("name", chartName);
            aChart.accumulate("vf_id", chart.getInt("@id"));
            array.add(aChart);
        }
        return array;
    }

    private boolean checkIndexFolderInDb(FileBrowserCache fileBrowserCache) {
        if (fileBrowserCache != null) {
            List<FileBrowserCache> allFileBrowserCaches = fileBrowserCacheService.getAllFileBrowserCaches(fileBrowserCache.getId());
            String indexFolderName = "index." + folderExtension;
            for (FileBrowserCache eachFileBrowser : allFileBrowserCaches) {
                if (indexFolderName.equals(eachFileBrowser.getFileName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean prepareFolderRule(File file, FileBrowserCache browserCache) {
        if ("0".equals(browserCache.getParentId())) {
            String indexPath = file.getPath() + File.separator + "index." + folderExtension;
            File indexFile = new File(indexPath);
            if (indexFile.exists()) {
                JSONObject jsonContent = JsonUtils.getAsJson(indexFile, true);
            }

        }

        return null;
    }

    private String findFolderLogicalName(File file, FileBrowserCache fileBrowserCache, boolean value, FileBrowserCache existingFileBrowser) {
        String indexPath = file.getPath() + File.separator + "index." + folderExtension;
        //logger.error("indexPath :" + indexPath);
        File indexFile = new File(indexPath);
        logger.debug("indexFileExists :" + indexFile.exists());
        if (checkIndexFolderInDb(existingFileBrowser)) {
            return existingFileBrowser.getTitle();
        }
        if (!(indexFile.exists())) {
            fileBrowserCache.setFolderType("PUBLIC");
            return file.getName();
        }
        JSONObject jsonContent = JsonUtils.getAsJson(indexFile, true);
        return findFileLogicalName(jsonContent);
    }

    private String findFolderLogicalName(File file, FileBrowserCache fileBrowserCache) {
        String indexPath = file.getPath() + File.separator + "index." + folderExtension;
        File indexFile = new File(indexPath);
        if (!(indexFile.exists())) {
            fileBrowserCache.setFolderType("PUBLIC");
            return file.getName();
        }
        JSONObject jsonContent = JsonUtils.getAsJson(indexFile, true);
        return findFileLogicalName(jsonContent);
    }

    private String findFileLogicalName(JSONObject jsonContent) {
        JSONObject contentJson = new JSONObject();
        String firstKey = "";
        TreeSet<String> setOfKeys = null;
        try {
            setOfKeys = new TreeSet<String>(jsonContent.keySet());
            firstKey = setOfKeys.first();
            contentJson = jsonContent.getJSONObject(firstKey);
        } catch (NoSuchElementException e) {
            logger.error("No elements found for the key :" + firstKey + "in set :" + setOfKeys);
        }
        return extractTitle(contentJson);
    }

    private String extractTitle(JSONObject contentJson) {
        String title = contentJson.has("title") ? "title" :
                contentJson.has("fileName") ? "fileName" :
                        contentJson.has("name") ? "name" :
                                contentJson.has("resultName") ? "resultName" :
                                        contentJson.has("reportName") ? "reportName" : "";
        return contentJson.optString(title);
    }

    public void deleteFromDb(String path) {
        fileBrowserCacheService.deleteFileBrowserCache(path);
    }

    public void dropFileBrowserCache() {
        fileBrowserCacheService.deleteAllFileBrowserCache();
    }

    public boolean isFileBrowserCacheEmpty() {
        return fileBrowserCacheService.emptyCheckFileBrowserCache();
    }

}
