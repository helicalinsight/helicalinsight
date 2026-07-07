package com.helicalinsight.admin.dao.impl;

import com.google.common.base.Predicates;
import com.helicalinsight.admin.dao.ProcessDetailsDao;
import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Somen
 *         Created on 1/21/2020.
 */
@Repository
public class ProcessDetailsDaoImpl implements ProcessDetailsDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProcessDetailsDaoImpl.class);

    @Autowired
    private SessionFactory session;
    
    @Autowired
    @Qualifier(value = "entityManager")
    private EntityManager em;

    @Override
    public Long addProcessDetails(ProcessDetails processDetails) {
        try {
            session.getCurrentSession().save(processDetails);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return processDetails.getProcessId();
    }

    @Override
    public void editProcessDetails(ProcessDetails processDetails) {
        try {
            session.getCurrentSession().update(processDetails);

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void deleteProcessDetails(Long processDetailsId) {
        try {
            session.getCurrentSession().delete(getProcessDetails(processDetailsId));

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public ProcessDetails getProcessDetails(Long processDetailsId) {
        ProcessDetails processDetails = null;
        try {
            processDetails = (ProcessDetails) session.getCurrentSession().get(ProcessDetails.class, processDetailsId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return processDetails;
    }

    @Override
    public ProcessDetails findUniqueProcessDetails(ProcessDetails sample) {
    	
    	ProcessDetails processDetails=null;
		List<Predicate> predicates=new ArrayList<>();
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<ProcessDetails> cr=cb.createQuery(ProcessDetails.class);
    	Root<ProcessDetails> resource = cr.from(ProcessDetails.class);
    	
        if (StringUtils.isNotBlank(sample.getCubeId())) {
            predicates.add(cb.equal(resource.get("cubeId"), sample.getCubeId()));
        }
        if (StringUtils.isNotBlank(sample.getMetadataFile())) {
            predicates.add(cb.equal(resource.get("metadataFile"), sample.getMetadataFile()));
        }
        if (StringUtils.isNotBlank(sample.getMetadatDir())) {
            predicates.add(cb.equal(resource.get("metadataDir"), sample.getMetadatDir()));
        }
        if (predicates.size()==0) {
            return null;
        } else {
			try {
				cr.select(resource).where(predicates.toArray(new Predicate[] {}));
				processDetails = em.createQuery(cr).getSingleResult();
			} catch (Exception e) {
				if (e instanceof NoResultException)
					return null;
				logger.error("Exception", e);
			}
        }
        return processDetails;
    }

    @Override
    public void deleteAllProcessDetails() {
        try {
            Query query = (Query) session.getCurrentSession().createQuery("delete from ProcessDetails");
            query.executeUpdate();
        } catch (Exception e) {
            logger.error("Exception occurred while deleting all processDetails", e);
        }
    }

    @Override
    public List<ProcessDetails> fetchAllCubes(LimitOffsetModel limitOffsetModel) {
        List<ProcessDetails> userList = null;
        try {

            Query query = (Query) session.getCurrentSession().createQuery("from ProcessDetails ");
            List list = query.getResultList();
            if (list.isEmpty()) {
                limitOffsetModel.setTotalCount(0);

            } else {
                limitOffsetModel.setTotalCount(list.size());
            }
            query.setFirstResult(limitOffsetModel.getOffset());
            query.setMaxResults(limitOffsetModel.getLimit());

            userList = (List<ProcessDetails>) list;

        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return userList;

    }
}
