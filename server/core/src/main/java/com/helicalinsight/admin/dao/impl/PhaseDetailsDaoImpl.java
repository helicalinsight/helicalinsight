package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.PhaseDetailsDao;
import com.helicalinsight.admin.model.CubePhaseDetails;
import com.helicalinsight.admin.model.HIAuditDetails;
import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.admin.model.HIResourcePhaseStatus;
import com.helicalinsight.admin.service.AuditService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.exceptions.EfwdServiceException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;

import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * @author Somen
 *         Created on 1/21/2020.
 */
@Repository
public class PhaseDetailsDaoImpl implements PhaseDetailsDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PhaseDetailsDaoImpl.class);

    @Autowired
    private SessionFactory session;
    
    @Autowired
    @Qualifier(value = "entityManager")
    private EntityManager em;

    @Override
    public Long addPhaseDetails(CubePhaseDetails processDetails) {
        try {
            session.getCurrentSession().save(processDetails);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return processDetails.getId();
    }

    @Override
    public void editPhaseDetails(CubePhaseDetails processDetails) {
        try {
            session.getCurrentSession().update(processDetails);

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void deletePhaseDetails(Long processDetailsId) {
        try {
            session.getCurrentSession().delete(getPhaseDetails(processDetailsId));

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public CubePhaseDetails getPhaseDetails(Long processDetailsId) {
        CubePhaseDetails processDetails = null;
        try {
            processDetails = (CubePhaseDetails) session.getCurrentSession().get(CubePhaseDetails.class, processDetailsId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return processDetails;
    }

    @Override
    public CubePhaseDetails findUniquePhaseDetails(CubePhaseDetails sample) {
    	
		CubePhaseDetails cubePhaseDetails = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CubePhaseDetails> cr = cb.createQuery(CubePhaseDetails.class);
			Root<CubePhaseDetails> resource = cr.from(CubePhaseDetails.class);
			cr.select(resource).where(cb.equal(resource.get("cubeId"), sample.getCubeId()));
			cubePhaseDetails=em.createQuery(cr).getSingleResult();
		} catch (Exception e) {
			if(e instanceof NoResultException)
        		return null;
			logger.error("Exception", e);
		}
		return cubePhaseDetails;
    }

    @Override
    public void deleteAllPhaseDetails() {
        try {
            Query query = (Query) session.getCurrentSession().createQuery("delete from CubePhaseDetails");
            query.executeUpdate();
        } catch (Exception e) {
            logger.error("Exception occurred while deleting all processDetails", e);
        }
    }

    @Override
    public List<CubePhaseDetails> fetchAllPhases(LimitOffsetModel limitOffsetModel) {
        List<CubePhaseDetails> userList = null;
        try {

            Query query = (Query) session.getCurrentSession().createQuery("from CubePhaseDetails ");
            List list = query.getResultList();
            if (list.isEmpty()) {
                limitOffsetModel.setTotalCount(0);

            } else {
                limitOffsetModel.setTotalCount(list.size());
            }
            query.setFirstResult(limitOffsetModel.getOffset());
            query.setMaxResults(limitOffsetModel.getLimit());

            userList = (List<CubePhaseDetails>) list;

        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return userList;

    }

	@Override
	public CubePhaseDetails findUniquePhaseDetailsByMetadataId(String metadataId) {
		try {
			Query query = (Query) session.getCurrentSession().createQuery("FROM CubePhaseDetails where  metadataId = :metadataId order by processStartTime DESC");
			query.setParameter("metadataId", metadataId);
			query.setMaxResults(1);
			return (CubePhaseDetails) query.getSingleResult();
		}	
		catch (Exception e) {
			logger.error("Exception ",e);
		}
		return null;
	}

	@Override
	public List<CubePhaseDetails> findAllCubePhaseDetailsByMetadataId(String metadataId) {
		try {
			Query query = (Query) session.getCurrentSession().createQuery("FROM CubePhaseDetails where  metadataId = :metadataId");
			query.setParameter("metadataId", metadataId);
			return query.getResultList();
		}
		catch (Exception e) {
			logger.error("Error occurred while fetching CubePhaseDetails with metadataId");
			return null;
		}
		
	}

	@Override
	public HIPhase findTheLastPhaseId(Integer resourceId) {
		HIAuditDetails auditDetails=null;
		try {
		SelectionQuery<HIAuditDetails> query = session.getCurrentSession().createQuery(
				"FROM HIAuditDetails where hiResource.resourceId = :resourceId ORDER BY phaseEndTime DESC ",HIAuditDetails.class);
		query.setParameter("resourceId", resourceId);
		query.setMaxResults(1);
		auditDetails = (HIAuditDetails) query.uniqueResult();
		}
		catch (Exception e) {
			logger.error("Error occurred while fetching Last Phase Id ");
			if(!(e instanceof NoResultException))
				throw new EfwdServiceException(e.getMessage());
		}
		return auditDetails != null ? auditDetails.getPhaseId() : null;
	}
	
	@Override
	public void addPhase(HIPhase hiPhase) {
		try {
		session.getCurrentSession().saveOrUpdate(hiPhase);
		}
		catch (Exception e) {
			logger.error("Error occurred while inserting HIPhase ");
			throw new EfwdServiceException(e.getMessage());
		}
	}


	@Override
	public HIPhase getPhase(Integer id) {
		try {
			return (HIPhase) session.getCurrentSession().load(HIPhase.class, id);
		} catch (Exception e) {
			logger.error("Error occurred while fetching HIPhase ");
			throw new EfwdServiceException(e.getMessage());
		}
	}

	@Override
	public HIPhase getHIPhaseByTypeAndStatus(String type, String status) {
		try {
			Query query = session.getCurrentSession()
					.createQuery("FROM HIPhase where resourceType =:type and status = :status");
			query.setParameter("type", type);
			query.setParameter("status", status);
			return (HIPhase) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Error occurred while fetching HIPhase ");
			throw new EfwdServiceException(e.getMessage());
		}
	}

	@Override
	public List<HIPhase> getAllPhases() {
		try {
			Query query = session.getCurrentSession().createQuery("FROM HIPhase");
			return query.getResultList();
		} catch (Exception e) {
			logger.error("Error occurred while fetching HIPhase list ");
			throw new EfwdServiceException(e.getMessage());
		}
	}
	
	@Override
	public HIResourcePhaseStatus findHIResourcePhaseStatusByResourceId(Integer resourceId) {
		try {
			SelectionQuery<HIResourcePhaseStatus> query = session.getCurrentSession()
					.createSelectionQuery("FROM HIResourcePhaseStatus where hiResource.resourceId =:resourceId",HIResourcePhaseStatus.class);
			query.setParameter("resourceId", resourceId);
			return  query.uniqueResult();
		} catch (Exception e) {
			logger.error("Error occurred while fetching HIResourcePhaseStatus ");
			if(!(e instanceof NoResultException))
				throw new EfwdServiceException(e.getMessage());
			else
				return null;
		}
	}

	@Override
	public void addHIResourcePhaseStatus(HIResourcePhaseStatus status) {
		try {
			HIResourcePhaseStatus hiResourcePhaseStatus = findHIResourcePhaseStatusByResourceId(status.getHiResource().getResourceId());
			if (hiResourcePhaseStatus == null) {
				hiResourcePhaseStatus = new HIResourcePhaseStatus();
			}
			else {
				HIPhase phase = hiResourcePhaseStatus.getHiPhase();
				String lastStatus = phase.getStatus();
				if(hiResourcePhaseStatus.getAction().contains("DUMP") && (lastStatus.equalsIgnoreCase("COMPLETED") || lastStatus.equalsIgnoreCase("ERROR"))) return ;
			}
			hiResourcePhaseStatus.setAction(status.getAction());
			hiResourcePhaseStatus.setHiPhase(status.getHiPhase());
			hiResourcePhaseStatus.setHiResource(status.getHiResource());
			hiResourcePhaseStatus.setUser(status.getUser());
			hiResourcePhaseStatus.setLastUpdatedDate(status.getLastUpdatedDate());
			hiResourcePhaseStatus.setDbName(status.getDbName());
			session.getCurrentSession().saveOrUpdate(hiResourcePhaseStatus);
			
		} catch (Exception e) {
			logger.error("Error occurred while inserting HIResourcePhaseStatus ");
			if(!(e instanceof NoResultException))
				throw new EfwdServiceException(e.getMessage());
		}
	}

	@Override
	public HIResourcePhaseStatus findHIResourcePhaseById(Integer statusId) {
		try {
		Query query = session.getCurrentSession().createQuery("FROM HIResourcePhaseStatus where id =:statusId");
		query.setParameter("statusId", statusId);
		query.setMaxResults(1);
		return (HIResourcePhaseStatus) query.getSingleResult();
		}
		catch (Exception e) {
			logger.error("Error occurred while fetching HIResourcePhaseStatus ");
			if(!(e instanceof NoResultException))
				throw new EfwdServiceException(e.getMessage());
			return null;
		}
	}

	@Override
	public void deleteHIResourcePhaseDetails(Integer resourceId) {
		try {
			
			HIResourcePhaseStatus status = findHIResourcePhaseStatusByResourceId(resourceId);
			
			Query deleteAudit = session.getCurrentSession().createQuery("DELETE HIAuditDetails where hiResource.resourceId =:resourceId and actionName in (:actionName)");
			deleteAudit.setParameter("actionName",List.of("SAMPLE_DUMP","DEEP_DUMP"));
			deleteAudit.setParameter("resourceId", status.getHiResource().getResourceId());
			deleteAudit.executeUpdate();
			
			HIPhase phase =  findTheLastPhaseId(status.getHiResource().getResourceId());
			status.setHiPhase(phase);
			status.setAction(phase.getStatus());
			status.setDbName(null);
			session.getCurrentSession().update(status);
		}
		catch (Exception e) {
			logger.error("Error occurred while deleting HIResourcePhaseStatus ");
			throw new EfwdServiceException(e.getMessage());
		}
	}

	@Override
	public List<HIResourcePhaseStatus> findAllResourcePhasesByResourceId(Integer resourceId) {
		try {
			Query query = session.getCurrentSession().createQuery("FROM HIResourcePhaseStatus where  hiResource.resourceId =:resourceId");
			query.setParameter("resourceId", resourceId);
			return query.getResultList();
		}
		catch (Exception e) {
			logger.error("Error occurred while deleting HIResourcePhaseStatus ");
			throw new EfwdServiceException(e.getMessage());
		}
	}
}
