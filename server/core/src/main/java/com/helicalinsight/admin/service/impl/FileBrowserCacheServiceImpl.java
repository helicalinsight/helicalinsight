package com.helicalinsight.admin.service.impl;

import com.google.gson.JsonArray;
import com.helicalinsight.admin.dao.FileBrowserCacheDao;
import com.helicalinsight.admin.model.FileBrowserCache;
import com.helicalinsight.admin.service.FileBrowserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Created by Rajesh on 3/25/2019.
 */
@Service
public class FileBrowserCacheServiceImpl implements FileBrowserCacheService {

    @Autowired
    private FileBrowserCacheDao cacheDao;

    @Transactional
    @Override
    public int addFileBrowserCache(FileBrowserCache fileBrowserCache) {
        return cacheDao.addFileBrowserCache(fileBrowserCache);
    }

    @Transactional
    @Override
    public void editFileBrowserCache(FileBrowserCache fileBrowserCache) {
        cacheDao.editFileBrowserCache(fileBrowserCache);
    }

    @Transactional
    @Override
    public void deleteFileBrowserCache(String filePath) {
        cacheDao.deleteFileBrowserCache(filePath);
    }

    @Transactional
    @Override
    public FileBrowserCache findFileBrowserCache(String filePath) {
        return cacheDao.findFileBrowserCache(filePath);
    }

    @Transactional
    @Override
    public List<FileBrowserCache> getAllFileBrowserCaches(int parentId) {
        return cacheDao.getAllFileBrowserCache(parentId);
    }

    @Transactional
    @Override
    public int getId(String filePath) {
        return cacheDao.getId(filePath);
    }

    @Transactional
    @Override
    public void deleteAllFileBrowserCache() {
        cacheDao.deleteAllFileBrowserCache();
    }

    @Transactional
    @Override
    public List<FileBrowserCache> getResultByQuery(String searchString, String filterType) {
        return cacheDao.getResultByQuery(searchString, filterType);
    }

    @Transactional
    @Override
    public List<FileBrowserCache> getResultByQuery(String searchString, JsonArray filterType) {
        return cacheDao.getResultByQuery(searchString, filterType);
    }

    @Transactional
    @Override
    public boolean emptyCheckFileBrowserCache() {
        return cacheDao.emptyCheckFileBrowserCache();
    }

    @Transactional
    @Override
    public FileBrowserCache getFileBrowserById(int parentId) {
        return cacheDao.getFileBrowserById(parentId);
    }

    @Transactional
    @Override
    public FileBrowserCache getFileBrowserByFileName(String fileName) {
        return cacheDao.getFileBrowserByFileName(fileName);
    }
}
