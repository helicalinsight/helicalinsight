package com.helicalinsight.admin.service;

import com.google.gson.JsonArray;
import com.helicalinsight.admin.model.FileBrowserCache;
import java.util.List;

/**
 * Created by Rajesh on 3/25/2019.
 */
public interface FileBrowserCacheService {
    /**
     * Implementation class method add the user persistent object in database
     *
     * @param fileBrowserCache persistent object
     */

    public int addFileBrowserCache(FileBrowserCache fileBrowserCache);

    /**
     * Implementation class method update the user persistent object
     *
     * @param fileBrowserCache persistent object
     */

    public void editFileBrowserCache(FileBrowserCache fileBrowserCache);

    /**
     * Implementation class method delete persistent object user by user id from
     * database
     *
     * @param filePath filePath
     */

    public void deleteFileBrowserCache(String filePath);

    /**
     * Implementation class method return the user object by user id from
     * database
     *
     * @param filePath catalogId
     * @return FileBrowserCache object
     */

    public FileBrowserCache findFileBrowserCache(String filePath);

    /**
     * Implementation class method return list of all user object from database
     *
     * @return list of user objects
     */

    public List<FileBrowserCache> getAllFileBrowserCaches(int parentId);

    public int getId(String filePath);

    public void deleteAllFileBrowserCache();

    public List<FileBrowserCache> getResultByQuery(String searchString, String filterType);
    public List<FileBrowserCache> getResultByQuery(String searchString, JsonArray filterType);

    public boolean emptyCheckFileBrowserCache();
    public FileBrowserCache getFileBrowserById(int parentId);
    public FileBrowserCache getFileBrowserByFileName(String fileName);
}
