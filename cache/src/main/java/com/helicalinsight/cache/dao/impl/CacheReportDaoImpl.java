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

package com.helicalinsight.cache.dao.impl;

import com.helicalinsight.cache.dao.CacheReportDao;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Somen on 5/28/2015.
 */
@Repository
public class CacheReportDaoImpl implements CacheReportDao {
    private static final Logger logger = LoggerFactory.getLogger(CacheReportDaoImpl.class);


    @Autowired
    private SessionFactory session;


    @Override
    public Long addCacheReport(CacheReport cacheReport) {
        try {
            Session currentSession = session.getCurrentSession();
            currentSession.save(cacheReport);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return cacheReport.getCacheId();
    }


    @Override
    public void editCacheReport(CacheReport cacheReport) {
        try {
            session.getCurrentSession().update(cacheReport);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

    }


    @Override
    public void deleteCacheReport(Long cacheReportId) {
        try {
            session.getCurrentSession().delete(getCacheReport(cacheReportId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }


    @Override
    public CacheReport getCacheReport(Long cacheReportId) {
        CacheReport cacheReport = null;
        try {
            cacheReport = (CacheReport) session.getCurrentSession().get(CacheReport.class, cacheReportId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cacheReport;
    }


    @Override
    public List<CacheReport> getCacheReportByCacheId(Long cacheId) {
        List<CacheReport> cacheReportList = null;
        try {
            cacheReportList = ApplicationUtilities.castList(CacheReport.class, session.getCurrentSession().
                    createQuery("from  CacheReport where  cacheId=:cacheId").setParameter("cacheId", cacheId).list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cacheReportList;
    }

    @Override
    public List<CacheReport> getAllReports() {
        List<CacheReport> cacheReportList = null;
        try {
            cacheReportList = ApplicationUtilities.castList(CacheReport.class, session.getCurrentSession().
                    createQuery("from  CacheReport").list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cacheReportList;
    }

    @Override
    public List<CacheReport> getReports(String directory) {
        List<CacheReport> cacheReportList = null;
        try {
            cacheReportList = ApplicationUtilities.castList(CacheReport.class, session.getCurrentSession().
                    createQuery("from  CacheReport where reportPath = :directory").setParameter("directory",
                    directory).list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cacheReportList;
    }

    @Override
    public List<String> getAllUniqueReports() {
        List<String> cacheReportList = null;
        try {
            cacheReportList = ApplicationUtilities.castList(String.class, session.getCurrentSession().
                    createQuery("select distinct  cr.reportPath from  CacheReport cr where cr.reportPath is not " +
                            "null").list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cacheReportList;
    }

    @Override
    public List<String> getUniqueReports(String directory) {
        List<String> cacheReportList = null;
        try {
            cacheReportList = ApplicationUtilities.castList(String.class, session.getCurrentSession().
                    createQuery("select distinct cr.reportPath from  CacheReport  cr " +
                            "where reportPath like " + ":directory and reportPath is not null").setParameter
                    ("directory", "%" + directory + "%").list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cacheReportList;
    }

    @Override
    public void deleteAllCacheReport() {
        try {
            session.getCurrentSession().
                    createQuery("delete from  CacheReport").executeUpdate();
        } catch (Exception e) {
            logger.error("Exception occurred while deleting all cache report", e);
        }

    }

}
