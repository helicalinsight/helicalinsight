package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.CubeMetadataDao;
import com.helicalinsight.admin.model.CubeMetadataInformation;
import com.helicalinsight.admin.model.CubePhaseDetails;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Somen
 *         Created on 1/21/2020.
 */
@Repository
public class CubeMetadataInformationDaoImpl implements CubeMetadataDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CubeMetadataInformationDaoImpl.class);

    @Autowired
    private SessionFactory session;

    @Autowired
    @Qualifier(value = "entityManager")
    private EntityManager em;
    
    @Override
    public Long addCubeMetadataInformation(CubeMetadataInformation cubeCubeMetadataInformation) {
        try {
            session.getCurrentSession().save(cubeCubeMetadataInformation);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cubeCubeMetadataInformation.getId();
    }

    @Override
    public void editCubeMetadataInformation(CubeMetadataInformation cubeCubeMetadataInformation) {
        try {
            session.getCurrentSession().update(cubeCubeMetadataInformation);

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void deleteCubeMetadataInformation(Long id) {
        try {
            session.getCurrentSession().delete(getCubeMetadataInformation(id));

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public CubeMetadataInformation getCubeMetadataInformation(Long id) {
        CubeMetadataInformation processDetails = null;
        try {
            processDetails = (CubeMetadataInformation) session.getCurrentSession().get(CubeMetadataInformation.class, id);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return processDetails;
    }

    @Override
    public CubeMetadataInformation findUniqueCubeMetadataInformation(CubeMetadataInformation cubeMetadataInformation) {
		CubeMetadataInformation info=null;
    	try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CubeMetadataInformation> cr = cb.createQuery(CubeMetadataInformation.class);
			Root<CubeMetadataInformation> resource = cr.from(CubeMetadataInformation.class);
			cr.select(resource).where(cb.equal(resource.get("cubeId"), cubeMetadataInformation.getCubeId()));
			info=em.createQuery(cr).getSingleResult();
		} catch (Exception ex) {
			if (ex instanceof NoResultException)
				return null;
            logger.error("Exception in finding cube metadata info"+ex);
		}
		return info;
    }

    @Override
    public void deleteAllCubeMetadataInformation() {
        try {
            Query query = (Query) session.getCurrentSession().createQuery("delete from CubeMetadataInformation");
            query.executeUpdate();
        } catch (Exception e) {
            logger.error("Exception occurred while deleting all cubeMetadataInformation", e);
        }
    }

    @Override
    public List<CubeMetadataInformation> fetchAllPhases(LimitOffsetModel limitOffsetModel) {
        List<CubeMetadataInformation> userList = null;
        try {

            Query query = (Query) session.getCurrentSession().createQuery("from CubeMetadataInformation ");
            List list = query.getResultList();
            if (list.isEmpty()) {
                limitOffsetModel.setTotalCount(0);

            } else {
                limitOffsetModel.setTotalCount(list.size());
            }
            query.setFirstResult(limitOffsetModel.getOffset());
            query.setMaxResults(limitOffsetModel.getLimit());

            userList = (List<CubeMetadataInformation>) list;

        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return userList;

    }

	@Override
	public void deleteCubeMetadataInformationByLastPhaseId(long phaseId) {
		   try {
	            Query query = (Query) session.getCurrentSession().createQuery("delete from CubeMetadataInformation where lastPhaseId =:phaseId");
	            query.setParameter("phaseId", phaseId);
	            query.executeUpdate();
	        } catch (Exception e) {
	            logger.error("Exception occurred while deleting  cubeMetadataInformation", e);
	        }
	}
}
