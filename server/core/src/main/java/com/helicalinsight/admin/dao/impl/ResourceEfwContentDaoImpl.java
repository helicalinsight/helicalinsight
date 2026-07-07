package com.helicalinsight.admin.dao.impl;
import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.helicalinsight.admin.dao.ResourceEfwContentDao;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.service.impl.ResourceEfwContentServiceImpl;
import jakarta.persistence.Query;

@Repository
public class ResourceEfwContentDaoImpl implements ResourceEfwContentDao{

    private static final Logger logger = LoggerFactory.getLogger(ResourceEfwContentServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceEfwContents> fetchResourceEfwContentByResourceId(Integer resourceId) {
		List<ResourceEfwContents> obj = null;
        try {
    		org.hibernate.query.Query<ResourceEfwContents> efwContentList = sessionFactory.getCurrentSession().createQuery("from ResourceEfwContents where resourceId=:id");
    		efwContentList.setParameter("id", resourceId);
    		efwContentList.setCacheable(true);
            obj = (List<ResourceEfwContents>) efwContentList.getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return obj;
	}

	@Override
	public void deleteResourceEwfContentByResourceId(String fileName,Integer resourceId) {
        try {
            Query deleteResourceEfwContent = (Query) sessionFactory.getCurrentSession().createQuery("delete from ResourceEfwContents WHERE fileName=:name AND resourceId=:id");
            deleteResourceEfwContent.setParameter("name", fileName);
            deleteResourceEfwContent.setParameter("id", resourceId);
            deleteResourceEfwContent.executeUpdate();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
	}

	@Override
	public Integer addHIResourceEfwContent(ResourceEfwContents resourceEfwContents) {
		Integer id=null;
        try {
            sessionFactory.getCurrentSession().save(resourceEfwContents);
            id=resourceEfwContents.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
	}

	@Override
	public ResourceEfwContents fetchEfwContentByResourceIdAndFileName(Integer resourceId, String fileName) {
		ResourceEfwContents data=null;
		try {
			String queryStr="FROM ResourceEfwContents WHERE fileName=:name AND resourceId=:id";
			if(resourceId==null) {
				queryStr="FROM ResourceEfwContents WHERE fileName=:name AND resourceId IS NULL";
			}
			org.hibernate.query.Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
			query.setParameter("name", fileName);
			if(resourceId!=null)
				query.setParameter("id", resourceId);
			data=(ResourceEfwContents)query.uniqueResult();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	@Override
	public List<ResourceEfwContents> findAllImageResources() {
		List<ResourceEfwContents> obj = null;
		List<String> imageList = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp", "svg", "heif", "raw");
		try {
			org.hibernate.query.Query<ResourceEfwContents> efwContentList = sessionFactory.getCurrentSession().createQuery("from ResourceEfwContents where contentType in (:contentType) ");
			efwContentList.setParameter("contentType", imageList);
			efwContentList.setCacheable(true);
			obj = (List<ResourceEfwContents>) efwContentList.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}
}
