package com.helicalinsight.admin.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.dao.OrganizationDao;
import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.IDumpDeleteHandler;
import com.helicalinsight.admin.service.IScheduleService;
import com.helicalinsight.admin.service.PhaseDetailsService;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceTypeIDMap;
import com.helicalinsight.resourcedb.processor.SecurityUtilsDB;

import org.apache.commons.io.FilenameUtils;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class HIResourceDBDAOImpl implements HIResourceDBDAO {

	private static final Logger logger = LoggerFactory.getLogger(HIResourceDBDAOImpl.class);
	private static final int MAX_UNIQUE_URL_SUFFIX_ATTEMPTS = 999;

	private static final String IS_DELETED_FILTER = "isDeletedFilter";

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private UserDao userDao;

	@Autowired
	private OrganizationDao organizationDao;

	@Autowired
	private RoleDao roleDao;
	@Autowired
	private EFWDConnectionService efwdConnectionService;

	@Autowired
	private HIRecycleBinService recycleBinService;

	@Autowired
	private ResourceTypeServiceDB resourceTypeService;
	
	private static final String RESOURCE_PROJECTION = """
			new com.helicalinsight.admin.model.HIResource(
			    r.resourceId,
			    r.created_date,
			    r.isMigrated,
			    r.lastUpdatedTime,
			    r.parentId,
			    r.createdBy,
			    r.isFolder,
			    r.resourcePath,
			    r.resourceURL,
			    r.resourceTypeId,
			    r.title,
			    r.isVisible,
			    r.isDeleted
			)
			""";

	@Override
	public Integer addHIResource(HIResource hiResource) {
		Session session = null;
		try {
			session = getSession();
			String url = hiResource.getResourceURL();
			if (recordExists(url.toLowerCase())) {
				logger.debug("Record already present with the url, generating unique URL.");
				String newUrl = getUniqueUrl(url, hiResource.getResourceType());
				hiResource.setResourceURL(newUrl);
				hiResource.setResourcePath(FilenameUtils.getBaseName(newUrl));
			}
			session.save(hiResource);
			return hiResource.getResourceId();
		}

		catch (RuntimeException e) {
			if (session != null) {

				/**
				 * This is important step while handling the exceptions. This ensures that
				 * current state will be discarded , otw may get AssertionFailure exception In
				 * case of Manual transaction handling, we need to rollback the transaction
				 * session.getTransaction().rollback(); Since spring managing transactions here,
				 * spring will automatically rollbacks it.
				 */
				session.clear();
			}
			throw e;
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Integer editHIResource(HIResource hiResource) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource " + hiResource);
			}
			getSession().merge(hiResource);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResource.getResourceId();
	}

	@Override
	public void editHIReport(HIResourceHReport hiResourceHReport) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource " + hiResourceHReport);
			}
			getSession().update(hiResourceHReport);
		} catch (Exception e) {
			logger.error("Exception", e);
		}

	}

	@Override
	public void editHIResourceHCR(HIResourceHCR hiResourceHCR) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource " + hiResourceHCR);
			}
			getSession().update(hiResourceHCR);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	public Map<Integer, Integer> getSecurityMapOfAnyUser(Integer userId, List<String> userRolesIds, Integer orgId) {

		Session currentSession = getSession();
		String securitySql = "SELECT security.hiResource.resourceId,max(security.permission) from HIResourceSecurityDB security where security.userId.id=:userId";

		if (orgId != null) {
			securitySql += " or security.orgId.id = :orgId";
		}
		if (!userRolesIds.isEmpty()) {
			securitySql += " or security.roleId.id in (:roleIds)";
		}
		securitySql += " group by security.hiResource.resourceId";

		Query securityQuery = (Query) currentSession.createQuery(securitySql);

		if (orgId != null) {
			securityQuery.setParameter("orgId", orgId);
		}
		if (!userRolesIds.isEmpty()) {
			List<Integer> roleIdList = new ArrayList<>();
			for (String ids : userRolesIds) {
				roleIdList.add(Integer.valueOf(ids));
			}
			securityQuery.setParameter("roleIds", roleIdList);
		}
		securityQuery.setParameter("userId", userId);
		List<Object[]> securityList = securityQuery.getResultList();
		Map<Integer, Integer> securityMap = new HashMap<>();
		for (Object[] obj : securityList) {
			Integer resourceId = (Integer) obj[0];
			Integer permission = (Integer) obj[1];
			securityMap.put(resourceId, permission);
		}
		return securityMap;
	}

	@Override
	public void deleteHIResource(Integer resourceId, List<Integer> resourceIds, Boolean isFolder) {
		try {
			Query deleteHIUrlMapping = (Query) getSession()
					.createQuery("delete HIUrlMapping mapping where mapping.resourceId.resourceId=:id");
			deleteHIUrlMapping.setParameter("id", resourceId);
			deleteHIUrlMapping.executeUpdate();

			Query deleteHIResource = (Query) getSession().createQuery("delete HIResource where resourceId=:id");
			deleteHIResource.setParameter("id", resourceId);
			deleteHIResource.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public Integer addHIResourceSecurity(HIResourceSecurityDB hiResourceSecurityDB) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to save the hi resource security " + hiResourceSecurityDB);
			}
			getSession().save(hiResourceSecurityDB);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIResourceSecurity(HIResourceSecurityDB hiResourceSecurityDB) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource security " + hiResourceSecurityDB);
			}
			getSession().update(hiResourceSecurityDB);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIResourceSecurity(Integer resourceId) {
		try {
			String hql = "DELETE HIResourceSecurityDB where hiResource.resourceId = :resourceId";
			Query query = getSession().createQuery(hql);
			query.setParameter("resourceId", resourceId);
			int records = query.executeUpdate();
			logger.debug("No of records deleted : {} ", records);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public List<HIResource> getAllHIResources() {

		List<HIResource> hiResourceList = new ArrayList<HIResource>();
		try {
			Session currentSession = sessionFactory.openSession();
			currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			Query query = currentSession.createQuery("from HIResource");
			// query.setCacheable(true);
			hiResourceList = (List<HIResource>) query.getResultList();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceList;
	}

	@Override
	public List<HIResourceSecurityDB> getHIResourceSecurityByResourceId(Integer resourceId) {
		List<HIResourceSecurityDB> hiResourceSecurityDB = new ArrayList<>();
		int nativeResourceId = resourceId;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResourceSecurityDB> query = currentSession
					.createQuery("from HIResourceSecurityDB security where security.hiResource.resourceId=:id");
			query.setParameter("id", nativeResourceId);
			// query.setCacheable(true);
			hiResourceSecurityDB = (List<HIResourceSecurityDB>) query.getResultList();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceSecurityDB;
	}

	@Override
	public Integer addHIResourceFolder(HIResourceFolder hiResourceFolder) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add the hi resource folder " + hiResourceFolder);
			}
			getSession().save(hiResourceFolder);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIResourceFolder(HIResourceFolder hiResourceFolder) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource folder " + hiResourceFolder);
			}
			getSession().update(hiResourceFolder);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIResourceFolder(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete the hi resource folder " + id);
			}
			getSession().delete(id);
		} catch (Exception e) {
			logger.error("Exception", e);
		}

	}

	@Override
	public Integer addHIResourceEFW(HIResourceEFW hiResourceEFW) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add the hi resource efw " + hiResourceEFW);
			}
			getSession().save(hiResourceEFW);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIResourceEFW(HIResourceEFW hiResourceEFW) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource efw " + hiResourceEFW);
			}
			getSession().update(hiResourceEFW);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIResourceEFW(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete the hi resource efw " + id);
			}
			getSession().delete(id);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public Integer addHIResourceEFWDD(HIResourceEFWDD hiResourceEFWDD) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add the hi resource efwdd " + hiResourceEFWDD);
			}
			getSession().save(hiResourceEFWDD);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIResourceEFWDD(HIResourceEFWDD hiResourceEFWDD) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to edit the hi resource efwdd " + hiResourceEFWDD);
			}
			getSession().update(hiResourceEFWDD);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIResourceEFWDD(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete the hi resource efwdd " + id);
			}
			getSession().delete(id);
		} catch (Exception e) {
			logger.error("Exception", e);
		}

	}

	@Override
	public Integer addHIResourceEFWCE(HIResourceEFWCE hiResourceEFWCE) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add the hi resource efwce " + hiResourceEFWCE);
			}
			getSession().save(hiResourceEFWCE);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIResourceEFWCE(HIResourceEFWCE hiResourceEFWCE) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource efwce " + hiResourceEFWCE);
			}
			getSession().update(hiResourceEFWCE);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIResourceEFWCE(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete the hi resource efwce " + id);
			}
			getSession().delete(id);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public Integer addHIResourceEFWSR(HIResourceEFWSR hiResourceEFWSR) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add the hi resource efwsr " + hiResourceEFWSR);
			}
			getSession().save(hiResourceEFWSR);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIResourceEFWSR(HIResourceEFWSR hiResourceEFWSR) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource efwsr " + hiResourceEFWSR);
			}
			getSession().update(hiResourceEFWSR);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIResourceEFWSR(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete the hi resource efwsr " + id);
			}
			getSession().delete(id);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public Integer addHIResourceEFWD(HIEFWD hiResourceEFWD) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add the hi resource efwd " + hiResourceEFWD);
			}
			getSession().save(hiResourceEFWD);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIResourceEFWD(HIEFWD hiResourceEFWD) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource efwd " + hiResourceEFWD);
			}
			getSession().update(hiResourceEFWD);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIResourceEFWD(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource efwd " + id);
			}
			getSession().delete(id);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public HIEFWD getHIResourceEFWDByResourceId(Integer resourceId) {
		HIEFWD hiResourceEFWD = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIEFWD> query = currentSession.createQuery("from HIEFWD where id:=:id ");
			query.setParameter("id", resourceId);
			// query.setCacheable(true);
			hiResourceEFWD = (HIEFWD) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceEFWD;
	}

	@Override
	public HIResourceEFWDD getHIResourceEFWDDByResourceId(Integer resourceId) {
		HIResourceEFWDD hiResourceEFWDD = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResourceEFWDD> query = currentSession
					.createQuery("from HIResourceEFWDD where id:=:id ");
			query.setParameter("id", resourceId);
			// query.setCacheable(true);
			hiResourceEFWDD = (HIResourceEFWDD) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceEFWDD;
	}

	@Override
	public HIResourceEFWCE getHIResourceEFWCEByResourceId(Integer resourceId) {
		HIResourceEFWCE hiResourceEFWCE = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResourceEFWCE> query = currentSession
					.createQuery("from HIResourceEFWCE where id:=:id ");
			query.setParameter("id", resourceId);
			// query.setCacheable(true);
			hiResourceEFWCE = (HIResourceEFWCE) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceEFWCE;
	}

	@Override
	public HIResourceEFW getHIResourceEFWByResourceId(Integer resourceId) {
		HIResourceEFW hiResourceEFW = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResourceEFW> query = currentSession
					.createQuery("from HIResourceEFW where id:=:id ");
			query.setParameter("id", resourceId);
			// query.setCacheable(true);
			hiResourceEFW = (HIResourceEFW) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceEFW;
	}

	@Override
	public HIResourceResult getHIResourceResultByResourceId(Integer resourceId) {
		HIResourceResult hiResourceResult = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResourceResult> query = currentSession
					.createQuery("from HIResourceResult where id=:id ");
			query.setParameter("id", resourceId);
			// query.setCacheable(true);
			hiResourceResult = (HIResourceResult) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceResult;
	}

	@Override
	public HIResourceFolder getHIResourceFolderByResourceId(Integer resourceId) {
		HIResourceFolder hiResourceFolder = null;
		try {
			Session currentSession = getSession();
			Query query = (Query) currentSession.createQuery(
					"Select resource.hiResourceFolder From HIResource resource  where resource.resourceId = :resourceId");
			query.setParameter("resourceId", resourceId);
			hiResourceFolder = (HIResourceFolder) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceFolder;
	}

	@Override
	public HIResourceHReport getHIResourceHReportByResultId(Integer resourceId) {
		HIResourceHReport hiResourceReport = null;
		try {
			Session currentSession = getSession();
			CriteriaBuilder cb = currentSession.getCriteriaBuilder();
			CriteriaQuery<HIResourceHReport> cr = cb.createQuery(HIResourceHReport.class);
			Root<HIResourceHReport> resource = cr.from(HIResourceHReport.class);
			cr.select(resource).where(cb.equal(resource.get("hiResource"), resourceId));
			hiResourceReport = currentSession.createQuery(cr).getSingleResult();
		} catch (Exception e) {
			if (e instanceof NoResultException)
				return null;
			logger.error("Exception", e);
		}
		return hiResourceReport;
	}

	@Override
	public Integer addHIResourceHwf(HIResourceHWF hiResourceHWF) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add thehiResourceHWF " + hiResourceHWF);
			}
			getSession().save(hiResourceHWF);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIResourceHwf(HIResourceHWF hiResourceHWF) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add thehiResourceHWF " + hiResourceHWF);
			}
			getSession().update(hiResourceHWF);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIResourceHwfById(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete thehiResourceHWF " + id);
			}
			getSession().delete(getHIResourceByResourceId(id));
		} catch (Exception e) {
			logger.error("Exception", e);
		}

	}

	@Override
	public HIResourceHWF getHIResourceByResourceId(Integer resourceId) {
		HIResourceHWF obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResourceHWF> query = currentSession
					.createQuery("from HIResourceHWF where id:=:id ");
			query.setParameter("id", resourceId);
			// query.setCacheable(true);
			obj = (HIResourceHWF) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return obj;
	}

	@Override
	public Integer addHIHwfExecution(HWFExecution hiHwfExecution) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add thehiResourceHWFExecution " + hiHwfExecution);
			}
			getSession().save(hiHwfExecution);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIHwfExecution(HWFExecution hiHwfExecution) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update hiHwfExecution) " + hiHwfExecution);
			}
			getSession().update(hiHwfExecution);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIHwfExecution(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete thehiResourceHWF " + id);
			}
			getSession().delete(getHIHwfExecutionByResourceId(id));
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public HWFExecution getHIHwfExecutionByResourceId(Integer id) {
		HWFExecution obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HWFExecution> query = currentSession
					.createQuery("from HWFExecution where id:=:id ");
			query.setParameter("id", id);
			// query.setCacheable(true);
			obj = (HWFExecution) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return obj;
	}

	@Override
	public Integer addHIHwfInput(HIHwfInput hiHwfInput) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update hiHwfInput) " + hiHwfInput);
			}
			getSession().save(hiHwfInput);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIHwfInput(HIHwfInput hiHwfInput) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update hiHwfInput) " + hiHwfInput);
			}
			getSession().update(hiHwfInput);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIHwfInput(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add HIHwfInput " + id);
			}
			getSession().delete(getHIHwfInput(id));
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public HIHwfInput getHIHwfInput(Integer id) {
		HIHwfInput obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIHwfInput> query = currentSession.createQuery("from HIHwfInput where id:=:id ");
			query.setParameter("id", id);
			// query.setCacheable(true);
			obj = (HIHwfInput) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return obj;
	}

	@Override
	public Integer addHIHwfOutput(HIHwfOutput hiHwfOutput) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add hiHwfOutput) " + hiHwfOutput);
			}
			getSession().save(hiHwfOutput);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHIHwfOutput(HIHwfOutput hiHwfOutput) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update hiHwfOutput) " + hiHwfOutput);
			}
			getSession().update(hiHwfOutput);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHIHwfOutput(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete hwfoutput " + id);
			}
			getSession().delete(getHIHwfOutput(id));
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public HIHwfOutput getHIHwfOutput(Integer id) {
		HIHwfOutput obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIHwfOutput> query = currentSession
					.createQuery("from HIHwfOutput where id:=:id ");
			query.setParameter("id", id);
			// query.setCacheable(true);
			obj = (HIHwfOutput) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return obj;
	}

	@Override
	public Integer addHwfExecutionInput(HWFExecutionInput hwfExecutionInput) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add hwfExecutionInput " + hwfExecutionInput);
			}
			getSession().save(hwfExecutionInput);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer addHwfExecutionOutput(HWFExecutionOutput hwfExecutionOutput) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to add hwfExecutionOutput " + hwfExecutionOutput);
			}
			getSession().save(hwfExecutionOutput);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHwfExecutionInput(HWFExecutionInput hwfExecutionInput) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update hwfExecutionInput " + hwfExecutionInput);
			}
			getSession().update(hwfExecutionInput);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public Integer editHwfExecutionOutput(HWFExecutionOutput hwfExecutionOutput) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update hwfExecutionOutput " + hwfExecutionOutput);
			}
			getSession().update(hwfExecutionOutput);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 1;
	}

	@Override
	public void deleteHwfExecutionInput(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete HwfExecutionInput " + id);
			}
			getSession().delete(getHwfExecutionInput(id));
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public HWFExecutionInput getHwfExecutionInput(Integer id) {
		HWFExecutionInput obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HWFExecutionInput> query = currentSession
					.createQuery("from HWFExecutionInput where id:=:id ");
			query.setParameter("id", id);
			// query.setCacheable(true);
			obj = (HWFExecutionInput) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return obj;
	}

	@Override
	public void deleteHwfExecutionOutput(Integer id) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to delete HwfExecutionInput " + id);
			}
			getSession().delete(getHwfExecutionOutput(id));
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public HWFExecutionOutput getHwfExecutionOutput(Integer id) {
		HWFExecutionOutput obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HWFExecutionOutput> query = currentSession
					.createQuery("from HWFExecutionOutput where id:=:id ");
			query.setParameter("id", id);
			// query.setCacheable(true);
			obj = (HWFExecutionOutput) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return obj;
	}

	@Override
	public HIResource getResourceByPath(String path, Integer createdBy) {
		HIResource obj = null;
		try {
			Session currentSession = getSession();
			String queryString = "from HIResource where resourceURL=:path and isFolder=:isFolder";
			/*
			 * if(createdBy!=null){ queryString+=" and createdBy=:createdBy"; }
			 */
			org.hibernate.query.Query<HIResource> query = currentSession.createQuery(queryString);
			query.setParameter("path", path);
			query.setParameter("isFolder", Boolean.TRUE);
			/*
			 * if(null!=createdBy){ query.setParameter("createdBy", createdBy); }
			 */
			// query.setCacheable(true);
			obj = (HIResource) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return obj;
	}

	@Override
	public HIResource getHIResourceFolderByName(String name) {
		HIResource obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResource> query = currentSession
					.createQuery("from HIResource where hiResourceFolder.title=:name and isFolder=:isFolder");
			query.setParameter("name", name);
			query.setParameter("isFolder", Boolean.TRUE);
			// query.setCacheable(true);
			obj = (HIResource) query.getSingleResult();
			return obj;
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return obj;
	}

	public boolean deleteDirectory(HIResource directory, List<Integer> resourceIdToDel) {
		Session currentSession = getSession();
		Query query = (Query) currentSession.createQuery("from HIResource where parentId=:id");
		query.setParameter("id", directory.getResourceId());

		List<HIResource> list = (List<HIResource>) query.getResultList();
		if (list != null && list.size() > 0) {
			for (HIResource item : list) {
				resourceIdToDel.add(item.getResourceId());
				if (item.getFolder()) {

					deleteDirectory(item, resourceIdToDel);

				}

			}
		}
		return true;
	}

	@Override
	public void deleteHIResourceById(Integer id) {
		try {
			HIResource hiResource = findResourceById(id, false);
			
			if (hiResource != null) {
				if (hiResource.isDeleted() != null && hiResource.isDeleted())
					hardDelete(hiResource);
				else
					softDelete(hiResource);
			}
		} catch (Exception e) {
			logger.error("Error occurred while delete resource :: root cause {}", e.getMessage());
		}
	}

	@Override
	public boolean hardDelete(HIResource hiResource) {
		deletePlainConnections(hiResource.getResourceId());
		Session currentSession = getSession();
		try {
			List<Integer> resourceIdToDelete = new ArrayList<>();
			resourceIdToDelete.add(hiResource.getResourceId());
			deleteDirectory(hiResource, resourceIdToDelete);
			String resourceType = "";
			for (Integer resId : resourceIdToDelete) {
				
				SelectionQuery<HIResource> delQuery = currentSession.createSelectionQuery("from HIResource where resourceId=:id", HIResource.class);
				delQuery.setParameter("id", resId);
				HIResource delResource =  delQuery.getSingleResultOrNull();
				
				MutationQuery deleteShare = currentSession.createMutationQuery("delete HIResourceSecurityDB rs where rs.hiResource.id=:id");
				deleteShare.setParameter("id", resId);
				deleteShare.executeUpdate();
				if (delResource != null)
					resourceType = delResource.getResourceType().getName();
				if (delResource != null && resourceType.contains("metadata")) {
					deleteMetadata(delResource);
				}
				if (delResource != null && resourceType.contains("cube")) {
					deleteCube(delResource);
				}
				if (delResource != null) {
					hardDeleteFromHiResourceEfwByResourceId(delResource.getParentId());
				}
				if (delResource != null && resourceType.equalsIgnoreCase("hcr")) {
					hardDeleteFromHiHcrConnectionsByResourceId(delResource);
				}

				IScheduleService scheduleService = (IScheduleService) ApplicationContextAccessor
						.getBean("schedulesServiceImpl");
				List<Long> scheduleIds = scheduleService.findAllSchedulesByResourceId(resId);
				scheduleService.deleteScheduleByIds(scheduleIds);

				if (delResource != null) {
					Long binId = getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(delResource.getResourceId());
					if (binId != null) {
						recycleBinService.delete(binId);
					}
					
					currentSession.remove(delResource);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private void hardDeleteFromHiHcrConnectionsByResourceId(HIResource delResource) {
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query query = currentSession
					.createQuery("FROM HIHcrConnections res WHERE res.hiResourceHcr.resourceId=:resId");
			query.setParameter("resId", delResource.getResourceId());
			List<HIHcrConnections> delHcrCon = (List<HIHcrConnections>) query.getResultList();
			for (HIHcrConnections deleH : delHcrCon)
				currentSession.delete(deleH);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean softDelete(HIResource resource) {
		if (Boolean.TRUE.equals(resource.getFolder())) {
			List<HIResource> resources = getResourceByParentId(resource.getResourceId(), null);
			for (HIResource child : resources) {
				softDelete(child);
			}
		}
		if (Boolean.FALSE.equals(resource.getDeleted())) {
			resource.setDeleted(true);
			HIRecycleBin bin = new HIRecycleBin();
			bin.setRecycleBinType(RecycleBinType.HI_RESOURCE_DB);
			bin.setDeletedOn(new Date());
			Integer ownerId = resource.getCreatedBy();
			if (ownerId != null) {
				User user = userDao.findUser(ownerId);
				bin.setCreatedBy(user);
				bin.setOrgId(user.getOrganization());
			}
			User deletedBy = userDao.findUser(Integer.valueOf(AuthenticationUtils.getUserId()));
			bin.setDeletedBy(deletedBy);
			HIRecycleBinHIResourceDB binResource = new HIRecycleBinHIResourceDB();
			binResource.setHiResource(resource);
			binResource.setRecycleBin(bin);
			bin.setHiRecycleBinHIResourceDB(binResource);
			markResourceAsDeleted(resource.getResourceId());
			return recycleBinService.save(bin);
		}
		return false;
	}

	private void deletePlainConnections(int id) {
		List<EfwdConnDTO> plainList = new ArrayList<>();
		getAllPlainConnections(id, plainList);
		plainList.addAll(efwdConnectionService.findConnectionByResourceId(id, Boolean.TRUE, Boolean.FALSE));
		plainList.forEach(con -> {
			efwdConnectionService.harddeleteEFConnectionById((con.getId()));
		});
	}

	private void getAllPlainConnections(int resourceId, List<EfwdConnDTO> plainList) {
		Query query = getSession().createQuery("FROM HIResource where parentId =:id");
		query.setParameter("id", resourceId);
		List<HIResource> children = query.getResultList();
		if (children != null && !children.isEmpty()) {
			for (HIResource child : children) {
				plainList.addAll(efwdConnectionService.findConnectionByResourceId(child.getResourceId(), Boolean.FALSE,
						Boolean.FALSE));
				getAllPlainConnections(child.getResourceId(), plainList);
			}
		}
	}

	private void deleteCube(HIResource delResource) {
		Session currentSession = getSession();
		org.hibernate.query.Query<HIMetadataCube> query = currentSession
				.createQuery("FROM HIMetadataCube cube where  cube.hiResource.resourceId=:cubeRes ");
		query.setParameter("cubeRes", delResource.getResourceId());
		// query.setCacheable(true);
		List<HIMetadataCube> list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			list.forEach(currentSession::delete);
		}
		if (delResource.getDeleted()) {
			Long binId = getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(delResource.getResourceId());
			if (binId != null) {
				recycleBinService.delete(binId);
			}
		}
//        currentSession.delete(delResource);
	}

	@Override
	public void cleanUp() {
		try {
			Query queryForeignKeyDisable = (Query) getSession().createQuery("SET FOREIGN_KEY_CHECKS = :disable");
			queryForeignKeyDisable.setParameter("disable", "0");
			queryForeignKeyDisable.executeUpdate();

			Query queryTruncateTable = (Query) getSession().createQuery("TRUNCATE table hi_resource_db");
			queryTruncateTable.executeUpdate();

			Query queryTruncateFolder = (Query) getSession().createQuery("TRUNCATE table hi_resource_folder");
			queryTruncateFolder.executeUpdate();

			Query queryForeignKeyEnable = (Query) getSession().createQuery("SET FOREIGN_KEY_CHECKS = :enable");
			queryForeignKeyDisable.setParameter("enable", "1");
			queryForeignKeyEnable.executeUpdate();

			sessionFactory.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Integer addHIUrlMapping(HIUrlMapping hiUrlMapping) {
		try {
			getSession().saveOrUpdate(hiUrlMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	public Integer editHIUrlMapping(HIUrlMapping hiUrlMapping) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update hiUrlMapping " + hiUrlMapping);
				getSession().update(hiUrlMapping);

			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiUrlMapping.getId();
	}

	@Override
	public void deleteHIUrlMapping(Integer id) {

	}

	@Override
	public HIUrlMapping getHIUrlMapping() {
		return null;
	}

	@Override
	public List<HIUrlMapping> getUrlMapping(String url) {
		List<HIUrlMapping> obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIUrlMapping> query = currentSession
					.createQuery("from HIUrlMapping mapping where mapping.friendlyURL like :url");
			query.setParameter("url", url + "%");
			// query.setCacheable(true);
			obj = (List<HIUrlMapping>) query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}

	@Override
	public HIUrlMapping getHIUrlMappingByResourceId(Integer resourceId) {
		HIUrlMapping obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIUrlMapping> query = currentSession
					.createQuery("from HIUrlMapping mapping where mapping.resourceId.resourceId=:id");
			query.setParameter("id", resourceId);
			// query.setCacheable(true);
			obj = (HIUrlMapping) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}

	@Override
	public HIResource getIsFolderByResourceId(Integer id) {
		HIResource obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResource> query = currentSession
					.createQuery("from HIResource where resourceId=:id and isFolder=:isFolder");
			query.setParameter("id", id);
			query.setParameter("isFolder", Boolean.TRUE);
			// query.setCacheable(true);
			obj = (HIResource) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}

	@Override
	public HIUrlMapping getUrlMap(String url) {
		HIUrlMapping obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIUrlMapping> query = currentSession
					.createQuery("from HIUrlMapping mapping where mapping.friendlyURL=:url");
			query.setParameter("url", url);
			// query.setCacheable(true);
			obj = (HIUrlMapping) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}

	@Override
	public Long countResourceByUrl(String url, Integer createdBy) {
		Long count = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResource> query = currentSession.createQuery(
					"select count(resourceURL) from HIResource  where resourceURL like :url and createdBy=:createdBy");
			query.setParameter("url", url + "%");
			query.setParameter("createdBy", createdBy);
			// query.setCacheable(true);
			count = ((Integer) query.getResultList().size()).longValue();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return count;
	}

	@Override
	// folder/nestedFolder
	public HIResource getResourceByUrl(String url, boolean applyFilter) {
		HIResource obj = null;
		try {
			Session currentSession = getSession();
			if (applyFilter)
				currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			SelectionQuery<HIResource> query = currentSession
					.createQuery("from HIResource resource where resource.resourceURL=:url", HIResource.class);
			query.setParameter("url", url);
			obj = query.uniqueResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}
	
	@Override
	// folder/nestedFolder
	public HIResource findResourceByUrl(String url) {
		HIResource hiResource = null;
		try {
			Session currentSession = getSession();
			String queryString =  """
					select %s 
					from HIResource r
					where r.resourceURL=:url
					  and r.isVisible = true
					  and r.isDeleted = false
					""".formatted(RESOURCE_PROJECTION);
			SelectionQuery<HIResource> query = currentSession.createSelectionQuery(queryString, HIResource.class);
			query.setParameter("url", url);
			hiResource = query.getSingleResultOrNull();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResource;
	}

	@Override
	public HIResource getResourceByFolderName(String folderName) {
		HIResource obj = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResource> query = currentSession.createQuery(
					"from HIResource resource where resource.isFolder=:isFolder and resource.hiResourceFolder.title=:folderName");
			query.setParameter("isFolder", Boolean.TRUE);
			query.setParameter("folderName", folderName);
			// query.setCacheable(true);
			obj = (HIResource) query.uniqueResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}

	@Override
	public List<HIResource> getResourceListByUrl(String url) {
		List<HIResource> obj = null;
		try {
			Session currentSession = getSession();
			currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			Query query = currentSession.createQuery("from HIResource resource where resource.resourceURL = :url");
			query.setParameter("url", url);
			// query.setCacheable(true);
			query.setParameter("trueValue", true);
			obj = (List<HIResource>) query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}

	public void deleteMetadata(HIResource metadata) {
		try {
			HIResourceMetadata metadataByResourceId = getMetadataByResourceId(metadata.getResourceId());
			if (null != metadataByResourceId) {

				Session session = getSession();

				Query deleteAuditDetails = getSession()
						.createQuery("delete HIAuditDetails where hiResource.resourceId = :resourceId");
				deleteAuditDetails.setParameter("resourceId", metadata.getResourceId());
				deleteAuditDetails.executeUpdate();

				Query phaseDetails = getSession()
						.createQuery("delete HIResourcePhaseStatus where hiResource.resourceId = :resourceId");
				phaseDetails.setParameter("resourceId", metadata.getResourceId());
				phaseDetails.executeUpdate();

				Query deleteMetadataRelationships = session.createQuery(
						"delete HIMetadataRelationships relationships where relationships.hiResourceMetadata.id=:id");

				deleteMetadataRelationships.setParameter("id", metadataByResourceId.getId());
				deleteMetadataRelationships.executeUpdate();

				Query delColumns = (Query) session
						.createQuery("delete HIMetadataColumns mdcol where mdcol.hiResourceMetadata.id=:id");
				delColumns.setParameter("id", metadataByResourceId.getId());
				delColumns.executeUpdate();

				Query deleteMetadataTables = (Query) session
						.createQuery("delete HIMetadataTables mdtables where mdtables.hiResourceMetadata.id=:id");
				deleteMetadataTables.setParameter("id", metadataByResourceId.getId());
				deleteMetadataTables.executeUpdate();

				Query deleteMetadataViews = (Query) session
						.createQuery("delete HIMetadataView view where view.hiResourceMetadata.id=:id");
				deleteMetadataViews.setParameter("id", metadataByResourceId.getId());
				deleteMetadataViews.executeUpdate();

				Query deleteHIResourceDatabase = (Query) session
						.createQuery("delete MetadataDatabases mddb where mddb.hiResourceMetadata.id=:id");
				deleteHIResourceDatabase.setParameter("id", metadataByResourceId.getId());
				deleteHIResourceDatabase.executeUpdate();

				List<HIMetadataConnections> hiMetadataConnections = metadataByResourceId.getHiMetadataConnections();
				for (HIMetadataConnections metadataConnections : hiMetadataConnections) {
					List<HIMetadataConnectionGlobal> metadataGlobalConnList = metadataConnections
							.getMetadataGlobalConnList();
					if (null != metadataGlobalConnList && !metadataGlobalConnList.isEmpty()) {
						Query deleteGlobalConnections = (Query) session.createQuery(
								"delete HIMetadataConnectionGlobal globalConn where globalConn.hiMetadataConnections.id=:id");
						deleteGlobalConnections.setParameter("id", metadataConnections.getId());
						deleteGlobalConnections.executeUpdate();
					}
					List<HIMetadataConnectionEFWD> efwdConnections = metadataConnections.getMetadataConnectionEfwd();
					if (null != efwdConnections && !efwdConnections.isEmpty()) {
						Query deleteEfwdConnections = (Query) session.createQuery(
								"delete HIMetadataConnectionEFWD efwdConn where efwdConn.hiMetadataConnections.id=:id");
						deleteEfwdConnections.setParameter("id", metadataConnections.getId());
						deleteEfwdConnections.executeUpdate();
					}
				}
				Query deleteConnections = (Query) session
						.createQuery("delete HIMetadataConnections conn where conn.hiResourceMetadata.id=:id");
				deleteConnections.setParameter("id", metadataByResourceId.getId());
				deleteConnections.executeUpdate();
				Query deleteMetadataSecurity = (Query) session
						.createQuery("delete HIMetadataSecurity metadata where metadata.hiResourceMetadata.id=:id");
				deleteMetadataSecurity.setParameter("id", metadataByResourceId.getId());
				deleteMetadataSecurity.executeUpdate();

				Query reports = (Query) session.createQuery(
						"FROM HIResource report where report.hiResourceHReport.hiResourceMetadata = :mdId");
				reports.setParameter("mdId", metadata.getResourceId());
				List<HIResource> reportList = reports.getResultList();
				reportList.forEach(this::hardDelete);

				Query cubes = (Query) session.createQuery("FROM HIMetadataCube  where hiResourceMetadata.id = :mdId");
				cubes.setParameter("mdId", metadataByResourceId.getId());
				List<HIMetadataCube> cubesToDelete = cubes.getResultList();
				cubesToDelete.forEach(cube -> {
					cube.setHiResourceMetadata(null);
					deleteCube(cube.getHiResource());
				});
				if (metadataByResourceId.getCached() != null && metadataByResourceId.getCached()) {
					PhaseDetailsService phaseDetailsService = ApplicationContextAccessor
							.getBean(PhaseDetailsService.class);
					List<HIResourcePhaseStatus> details = phaseDetailsService
							.findAllResourcePhasesByResourceId(metadata.getResourceId());
					IDumpDeleteHandler handler = (IDumpDeleteHandler) ApplicationContextAccessor
							.getBean("metadataDumpDeleteHandler");
					for (HIResourcePhaseStatus eachDetail : details) {
						handler.delete(eachDetail.getId());
					}
				}

				Query deleteMetadata = (Query) session
						.createQuery("delete HIResourceMetadata metadata where metadata.id=:mdId");
				deleteMetadata.setParameter("mdId", metadataByResourceId.getId());
				deleteMetadata.executeUpdate();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteResource(Integer resourceId, List<Integer> resourceIds, Boolean isFolder) {
		try {
			HIResourceMetadata metadataByResourceId = getMetadataByResourceId(resourceId);
			String hql = "";
			if (isFolder == Boolean.TRUE) {
				if (resourceIds != null && resourceIds.size() > 0) {
					hql = "and resource.parentId in (:resourceIds)";
				}
			}

			if (null != metadataByResourceId) {
				org.hibernate.query.Query<HIMetadataRelationships> deleteMetadataRelationships = getSession()
						.createQuery(
								"delete HIMetadataRelationships relationships where relationships.hiResourceMetadata.id=:id");
				deleteMetadataRelationships.setParameter("id", metadataByResourceId.getId());
				deleteMetadataRelationships.setCacheable(true);
				deleteMetadataRelationships.executeUpdate();

				org.hibernate.query.Query<HIMetadataColumns> deleteHIResourceMetadata = getSession()
						.createQuery("delete HIMetadataColumns mdcol where mdcol.hiResourceMetadata.id=:id");
				deleteHIResourceMetadata.setParameter("id", metadataByResourceId.getId());
				deleteHIResourceMetadata.setCacheable(true);
				deleteHIResourceMetadata.executeUpdate();

				org.hibernate.query.Query<HIMetadataTables> deleteMetadataTables = getSession()
						.createQuery("delete HIMetadataTables mdtables where mdtables.hiResourceMetadata.id=:id");
				deleteMetadataTables.setParameter("id", metadataByResourceId.getId());
				deleteMetadataTables.setCacheable(true);
				deleteMetadataTables.executeUpdate();

				org.hibernate.query.Query<HIMetadataView> deleteMetadataViews = getSession()
						.createQuery("delete HIMetadataView view where view.hiResourceMetadata.id=:id");
				deleteMetadataViews.setParameter("id", metadataByResourceId.getId());
				deleteMetadataViews.setCacheable(true);
				deleteMetadataViews.executeUpdate();

				org.hibernate.query.Query<MetadataDatabases> deleteHIResourceDatabase = getSession()
						.createQuery("delete MetadataDatabases mddb where mddb.hiResourceMetadata.id=:id");
				deleteHIResourceDatabase.setParameter("id", metadataByResourceId.getId());
				deleteHIResourceDatabase.setCacheable(true);
				deleteHIResourceDatabase.executeUpdate();

				List<HIMetadataConnections> hiMetadataConnections = metadataByResourceId.getHiMetadataConnections();
				for (HIMetadataConnections metadataConnections : hiMetadataConnections) {
					List<HIMetadataConnectionGlobal> metadataGlobalConnList = metadataConnections
							.getMetadataGlobalConnList();
					if (null != metadataGlobalConnList && metadataGlobalConnList.size() > 0) {
						org.hibernate.query.Query<HIMetadataConnectionGlobal> deleteGlobalConnections = getSession()
								.createQuery(
										"delete HIMetadataConnectionGlobal globalConn where globalConn.hiMetadataConnections.id=:id");
						deleteGlobalConnections.setParameter("id", metadataConnections.getId());
						deleteGlobalConnections.setCacheable(true);
						deleteGlobalConnections.executeUpdate();
					}
				}

				org.hibernate.query.Query<HIMetadataConnections> deleteConnections = getSession()
						.createQuery("delete HIMetadataConnections conn where conn.hiResourceMetadata.id=:id");
				deleteConnections.setParameter("id", metadataByResourceId.getId());
				deleteConnections.setCacheable(true);
				deleteConnections.executeUpdate();

				org.hibernate.query.Query<HIResourceMetadata> deleteMetadata = getSession()
						.createQuery("delete HIResourceMetadata metadata where metadata.hiResource.resourceId=:id");
				deleteMetadata.setParameter("id", resourceId);
				deleteMetadata.setCacheable(true);
				deleteMetadata.executeUpdate();
			}

			org.hibernate.query.Query<HIResourceSecurityDB> deleteHIResourceSecurity = getSession()
					.createQuery("delete HIResourceSecurityDB security where security.hiResource.resourceId=:id");
			deleteHIResourceSecurity.setParameter("id", resourceId);
			deleteHIResourceSecurity.setCacheable(true);
			deleteHIResourceSecurity.executeUpdate();

			if (resourceIds != null && !resourceIds.isEmpty()) {
				org.hibernate.query.Query<HIResource> deleteHIResourceChild = getSession()
						.createQuery("delete HIResource resource where resource.parentId in (:resourceIds)");
				deleteHIResourceChild.setParameterList("resourceIds", resourceIds);
				deleteHIResourceChild.setCacheable(true);
				deleteHIResourceChild.executeUpdate();
			}
			org.hibernate.query.Query<HIResource> deleteHIResource = getSession()
					.createQuery("delete HIResource resource where resource.resourceId=:id");
			deleteHIResource.setParameter("id", resourceId);
			deleteHIResource.setCacheable(true);
			deleteHIResource.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isTableEmpty() {
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResource> query = currentSession.createQuery("select 1 from HIResource");
			// query.setCacheable(true);
			boolean empty = query.setMaxResults(1).getResultList().isEmpty();
			return empty;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public HIResource getResourceByParent(Integer parentId) {
		List<HIResource> obj = null;
		try {
			Session currentSession = getSession();
			currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			Query query = currentSession.createQuery("from HIResource resource where resource.resourceId=:id");
			query.setParameter("id", parentId.intValue());
			// query.setCacheable(true);
			obj = (List<HIResource>) query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (obj.size() > 0 && obj != null) {
			return obj.get(0);
		}
		return null;
	}

	@Override
	public List<HIResourceSecurityDB> getHIResourceSecurityByCreatedBy(Integer createdBy) {
		String createdByStr = "" + createdBy;
		List<HIResourceSecurityDB> obj = null;
		try {
			Session currentSession = getSession();
			if (null != createdBy) {
				org.hibernate.query.Query<HIResourceSecurityDB> query = currentSession
						.createQuery("from HIResourceSecurityDB resourceSecurity where resourceSecurity.createdBy=:id");
				query.setParameter("id", createdByStr);
				// query.setCacheable(true);
				obj = (List<HIResourceSecurityDB>) query.getResultList();
			} else {
				org.hibernate.query.Query<HIResourceSecurityDB> query = currentSession.createQuery(
						"from HIResourceSecurityDB resourceSecurity where resourceSecurity.createdBy IS NULL");
				// query.setCacheable(true);
				obj = (List<HIResourceSecurityDB>) query.getResultList();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}

	@Override
	public List<HIResource> getHIResourceByCreatedBy(Integer createdBy, boolean includeFilter) {
		List<HIResource> obj = null;
		try {
			Session currentSession = getSession();

			if (includeFilter)
				currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			org.hibernate.query.Query<HIResource> query = currentSession
					.createQuery("from HIResource resource where  resource.createdBy=:id");

			query.setParameter("id", createdBy);
			// query.setCacheable(true);
			obj = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public List<HIResource> getHIResourceByPath(String path) {
		path = StringUtils.isEmpty(path) ? "/" : path;
		List<HIResource> hiResourceList = null;
		try {

			Session currentSession = getSession();
			HIResource hiResourceById = getResourceByPath(path);
			Map<String, Object> resourceMap = new HashMap<>();
			resourceMap.put("folder", hiResourceById);
			boolean targetReachable = SecurityUtilsDB.isTargetReachable(resourceMap);
			if (targetReachable) {
				org.hibernate.query.Query<HIResource> query = currentSession
						.createQuery("from HIResource resource where resource.parentId=:id");
				query.setParameter("id", hiResourceById.getResourceId());
				// query.setCacheable(true);
				return (List<HIResource>) query.getResultList();
			} else {
				throw new Exception("Unable To access the given resource");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hiResourceList;
	}

	@Override
	public Long countResourceByCreatedBy(Integer createdBy) {
		Long count = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResource> query = currentSession
					.createQuery("select count(resourceId) from HIResource  where createdBy=:id");
			query.setParameter("id", createdBy);
			// query.setCacheable(true);
			count = ((Integer) query.getResultList().size()).longValue();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return count;
	}

	@Override
	public List<HIResource> getResourceByParentId(Integer parentId, Object searchParam) {
		List<HIResource> obj = null;
		try {
			Session currentSession = getSession();
			String queryStr = "from HIResource resource where resource.parentId IS NULL";
			if (parentId != null) {
				queryStr = "from HIResource resource where resource.parentId=:id";
				if (searchParam != null) {
					queryStr += " AND resource.resourceTypeId in :searchParam";
				}
			}
			Query query = currentSession.createQuery(queryStr);
			if (parentId != null)
				query.setParameter("id", parentId.intValue());
			if (searchParam != null)
				query.setParameter("searchParam", (List<Long>) searchParam);
			obj = (List<HIResource>) query.getResultList();
			return obj;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return obj;
	}

	@Override
	public HIResource getResourceByPath(String path) {
		Session currentSession = getSession();
		org.hibernate.query.SelectionQuery<HIResource> query = currentSession
				.createSelectionQuery("from HIResource resource where resource.resourceURL=:url", HIResource.class);
		query.setParameter("url", path);
		return query.uniqueResult();

	}

	public Map<Integer, Integer> getSecurityMap() {
		Integer userId = Integer.valueOf(AuthenticationUtils.getUserId());
		Integer orgId = null;
		if (StringUtils.isNotEmpty(AuthenticationUtils.getOrganizationId())) {
			orgId = Integer.valueOf(AuthenticationUtils.getOrganizationId());
		}

		List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();
		Session currentSession = getSession();
		String securitySql = "SELECT security.hiResource.resourceId,max(security.permission) from HIResourceSecurityDB security where security.userId.id=:userId";

		if (orgId != null) {
			securitySql += " or security.orgId.id = :orgId";
		}
		if (!userRolesIds.isEmpty()) {
			securitySql += " or security.roleId.id in (:roleIds)";
		}
		securitySql += " group by security.hiResource.resourceId";

		Query securityQuery = (Query) currentSession.createQuery(securitySql);

		if (orgId != null) {
			securityQuery.setParameter("orgId", orgId);
		}
		if (!userRolesIds.isEmpty()) {
			List<Integer> roleIdList = new ArrayList<>();
			for (String ids : userRolesIds) {
				roleIdList.add(Integer.valueOf(ids));
			}
			securityQuery.setParameter("roleIds", roleIdList);
		}
		securityQuery.setParameter("userId", userId);
		List<Object[]> securityList = securityQuery.getResultList();
		Map<Integer, Integer> securityMap = new HashMap<>();
		for (Object[] obj : securityList) {
			Integer resourceId = (Integer) obj[0];
			Integer permission = (Integer) obj[1];
			securityMap.put(resourceId, permission);
		}
		return securityMap;
	}

	@Override
	public void deleteHIResourceSecurityByResourceId(int resourceId, Integer userId, Integer orgId, Integer roleId) {
		try {
			String userVal = userId != null ? "userId.id = :userId" : "userId IS NULL";
			String orgVal = orgId != null ? "orgId.id = :orgId" : "orgId IS NULL";
			String roleVal = roleId != null ? "roleId.id = :roleId" : "roleId IS NULL";
			String hql = "delete HIResourceSecurityDB where hiResource.resourceId=:id and " + userVal + " and " + orgVal
					+ " and " + roleVal;
			Query updateResourceQuery = (Query) getSession().createQuery(hql);

			updateResourceQuery.setParameter("id", resourceId);
			if (null != userId) {
				updateResourceQuery.setParameter("userId", userId);
			}
			if (null != orgId) {
				updateResourceQuery.setParameter("orgId", orgId);
			}
			if (null != roleId) {
				updateResourceQuery.setParameter("roleId", roleId);
			}
			int i = updateResourceQuery.executeUpdate();
			logger.info("i value", i);
		} catch (Exception e) {
			logger.error("Exception", e);
			e.printStackTrace();
		}

	}

	@Override
	public void updateOrInsert(int resourceId, Integer userId, Integer orgId, Integer roleId, Integer permission,
			String createdBy) {
		try {
			String userVal = userId != null ? "userId.id = :userId" : "userId IS NULL";
			String orgVal = orgId != null ? "orgId.id = :orgId" : "orgId IS NULL";
			String roleVal = roleId != null ? "roleId.id = :roleId" : "roleId IS NULL";

			Query updateResourceQuery = (Query) getSession().createQuery(
					"from HIResourceSecurityDB resourceSecurity where resourceSecurity.hiResource.resourceId=:id and "
							+ userVal + " and " + orgVal + " and " + roleVal + " and permission=:permission");

			updateResourceQuery.setParameter("id", resourceId);
			if (null != userId) {
				updateResourceQuery.setParameter("userId", userId);
			}
			if (null != orgId) {
				updateResourceQuery.setParameter("orgId", orgId);
			}
			if (null != roleId) {
				updateResourceQuery.setParameter("roleId", roleId);
			}
			updateResourceQuery.setParameter("permission", permission);
			List<HIResourceSecurityDB> list = updateResourceQuery.getResultList();
			if (list.size() > 0) {
				HIResourceSecurityDB hiResourceSecurityDB = list.get(0);
				hiResourceSecurityDB.setPermission(permission);
				hiResourceSecurityDB.setLastUpdatedTime(new Date());
				if (StringUtils.isNotEmpty(createdBy)) {
					Integer createdByInt = Integer.valueOf(createdBy);
					hiResourceSecurityDB.setCreatedBy(createdByInt);
				}
				editHIResourceSecurity(hiResourceSecurityDB);
			} else {
				HIResourceSecurityDB hiResourceSecurityDB1 = new HIResourceSecurityDB();
				HIResource resourceById = getHIResourceById(resourceId, true);
				hiResourceSecurityDB1.setHiResource(resourceById);
				if (null != userId) {
					User user = userDao.findUser(userId);
					hiResourceSecurityDB1.setUserId(user);
				}

				if (null != orgId) {
					Organization organization = organizationDao.getOrganization(orgId);
					hiResourceSecurityDB1.setOrgId(organization);
				}

				if (null != roleId) {
					Role role = roleDao.getRole(roleId);
					hiResourceSecurityDB1.setRoleId(role);
				}
				hiResourceSecurityDB1.setLastUpdatedTime(new Date());
				hiResourceSecurityDB1.setPermission(permission);
				if (StringUtils.isNotEmpty(createdBy)) {
					Integer createdByInt = Integer.valueOf(createdBy);
					hiResourceSecurityDB1.setCreatedBy(createdByInt);
				}
				addHIResourceSecurity(hiResourceSecurityDB1);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public Long countResourceByFolderName(String folderName, Integer userId) {
		Long count = null;
		try {
			Session currentSession = getSession();
			org.hibernate.query.Query<HIResource> query = currentSession.createQuery(
					"select count(resource.hiResourceFolder.title) from HIResource resource where resource.hiResourceFolder.title=folderName and createdBy=:createdBy");
			query.setParameter("folderName", folderName);
			query.setParameter("createdBy", userId);
			// query.setCacheable(true);
			count = ((Integer) query.getResultList().size()).longValue();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return count;
	}

	@Override
	public List<HIResourceSecurityDB> getAllResourceSecurity() {
		List<HIResourceSecurityDB> hiResourceSecurityList = new ArrayList<>();
		try {
			Session currentSession = sessionFactory.openSession();
			org.hibernate.query.Query<HIResourceSecurityDB> query = currentSession
					.createQuery("from HIResourceSecurityDB");
			// query.setCacheable(true);
			hiResourceSecurityList = (List<HIResourceSecurityDB>) query.getResultList();
			currentSession.close();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceSecurityList;
	}

	@Override
	public List<HIResource> getAllResourceListWithExtension(List<Long> resourceIds) {
		try {
			Integer userId = Integer.valueOf(AuthenticationUtils.getUserId());
			String orgId = AuthenticationUtils.getOrganizationId();
			List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();

			Session currentSession = getSession();
			currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			org.hibernate.query.Query query = currentSession.createQuery(
					"from HIResource resource where resource.resourceTypeId in :resourceId order by resource.parentId asc");
			query.setParameterList("resourceId", resourceIds);
			List<HIResource> hiResourceList = query.getResultList();
			return hiResourceList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Integer addHIResourceEFWContents(ResourceEfwContents resourceEfwContents) {
		try {
			getSession().save(resourceEfwContents);
			return resourceEfwContents.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean editHIResourceEFWContents(ResourceEfwContents resourceEfwContents) {
		try {

			getSession().update(resourceEfwContents);
		} catch (Exception e) {
			logger.error("Exception", e);
			return false;
		}
		return true;
	}

	@Override
	public Boolean deleteHIResourceEFWContents(Integer contentId) {
		try {
			Query deleteHIUrlMapping = getSession()
					.createQuery("delete ResourceEfwContents content where content.id=:id");
			deleteHIUrlMapping.setParameter("id", contentId);
			deleteHIUrlMapping.executeUpdate();
			return true;

		} catch (Exception e) {
			logger.error("Exception", e);
			return false;
		}
	}

	@Override
	public ResourceEfwContents getHIResourceEFWContents(Integer contentId) {
		try {
			Session currentSession = getSession();
			Query query = (Query) currentSession.createQuery("from ResourceEfwContents resource where resource.id=:id");
			query.setParameter("id", contentId);
			return (ResourceEfwContents) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public ResourceEfwContents getHIResourceEFWContents(String fileName) {
		try {
			Session currentSession = getSession();
			Query query = (Query) currentSession
					.createQuery("from ResourceEfwContents resource where resource.fileName=:fileName");
			query.setParameter("fileName", fileName);
			return (ResourceEfwContents) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public HIResourceMetadata getMetadataByResourceId(Integer resourceId) {
		try {
			Session currentSession = getSession();
			SelectionQuery<HIResourceMetadata> query = currentSession.createSelectionQuery(
					"from HIResourceMetadata metadata where metadata.hiResource.resourceId=:id",
					HIResourceMetadata.class);
			query.setParameter("id", resourceId);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public HIResource getResourceIdFromTitleAndURL(String dir, String title) {
		try {
			Session currentSession = getSession();
			Query query = (Query) currentSession
					.createQuery("from HIResource resource where resource.resourceURL=:dir and resource.title=:title");
			query.setParameter("dir", dir);
			query.setParameter("title", title);
			return (HIResource) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Integer addHIResourceEFWVF(HIResourceEFWVF hiResourceEFWVF) {
		try {
			getSession().save(hiResourceEFWVF);
			return hiResourceEFWVF.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Integer editHIResourceEFWVF(HIResourceEFWVF hiResourceEFWVF) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to update the hi resource efwvf " + hiResourceEFWVF);
			}
			getSession().update(hiResourceEFWVF);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceEFWVF.getId();
	}

	@Override
	public void deleteHIResourceEFWVF(Integer id) {
		try {
			Query deleteHIResource = (Query) getSession()
					.createQuery("delete HIResourceEFWVF efwvf where efwvf.hiResource.resourceId=:id");
			deleteHIResource.setParameter("id", id);
			deleteHIResource.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	@Override
	public HIResourceEFWVF getHIResourceEFWVFById(Integer resourceId) {
		try {
			Session currentSession = getSession();
			Query query = (Query) currentSession
					.createQuery("from HIResourceEFWVF resource where resource.hiResource.resourceId=:id");
			query.setParameter("id", resourceId);
			return (HIResourceEFWVF) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Boolean deleteDatasoureRelatedResources(Integer globalId) {
		Boolean deletedEverything = false;
		List<Integer> metadataIds = findMetadataResourcesByGlobalConnectionId(globalId);
		List<Integer> hcrIds = findHCRResourcesByGlobalConnectionId(globalId);
		List<Integer> rIds = findHiResourceIdsByMetadataIds(metadataIds);
		if (rIds != null) {
			if (hcrIds != null) {
				rIds.addAll(hcrIds);
			}

			if (rIds.size() == 0)
				return true; // Nothing to delete..
			else {
				List<HIResource> resources = getHIResourcesByIds(rIds, false);
				for (HIResource resource : resources) {
					try {
						if (resource != null) {
							hardDelete(resource);
						}
					} catch (Exception e) {
						logger.error("Exception occured during deletion with message : {} and cause {}", e.getMessage(),e.getCause());
						return false;
					}
				}
				deletedEverything = true;
			}
		}
		return deletedEverything;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findHiResourceIdsByMetadataIds(List<Integer> metadataIds) {
		List<Integer> result = new ArrayList<Integer>();
		if (metadataIds.size() > 0) {
			try {
				Session currentSession = this.getSession();
				String hql = "SELECT hrmd.hiResource.resourceId FROM HIResourceMetadata as hrmd where hrmd.id IN (:metadataIds)";

				Query query = (Query) currentSession.createQuery(hql);
				query.setParameter("metadataIds", metadataIds);
				result = query.getResultList();
				result.addAll(findHReportIdsByMetadataResourceId(result));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.info("No Resource have been created with the current DataSource.");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findHReportIdsByMetadataResourceId(List<Integer> metadataList) {
		try {
			String sql = """
						select distinct r.id
						from HIResource r
						join r.hiResourceHReport hr
						where hr.hiResourceMetadata in (:metadataList)
					""";
					
			Session currentSession = this.getSession();
			SelectionQuery query =  currentSession.createSelectionQuery(sql, Integer.class);
			query.setParameter("metadataList", metadataList);
			List<Integer> ids = query.getResultList();
		} catch (Exception ex) {
			logger.error("Error occurred while fetching hreport ids by metadata resourceId");
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findMetadataResourcesByGlobalConnectionId(int globalId) {
		try {
			Session currentSession = this.getSession();
			String hql = """	
							select mdc.hiResourceMetadata.id  
							FROM HIMetadataConnectionGlobal mcg
							join mcg.hiMetadataConnections mdc
							where mcg.globalConnections.globalId = :globalId
						""";
			SelectionQuery<Integer> query = currentSession.createSelectionQuery(hql,Integer.class);
			query.setParameter("globalId", globalId);
			List<Integer> result = query.getResultList();
			logger.info("Metadata Data IDS :: {} matching Global Connection ID :: {}", result, globalId);
			return result;
		} catch (Exception e) {
			logger.error("Error occurred while fetching metadata ids by global connection ids, Root cause : ", e);
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findHCRResourcesByGlobalConnectionId(int globalId) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			Session currentSession = this.getSession();
			String sql = "select mdc.hiResourceHcr.resourceId  FROM HIHcrConnections as mdc "
					+ " where  mdc.hiHcrConnectionsGlobal.globalConnections.globalId =" + globalId;
			Query query = currentSession.createQuery(sql);
			result = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("HCR Data IDS :: {} matching Global Connection ID :: {}", result, globalId);
		return result;
	}

	@Override
	public HIResource getHIResourceById(Integer resourceId, boolean applyFilter) {
		HIResource hiResource = null;
		try {
			Session currentSession = getSession();
			if (applyFilter)
				currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			SelectionQuery<HIResource> query = currentSession
					.createSelectionQuery("from HIResource where resourceId=:id", HIResource.class);
			query.setParameter("id", resourceId);
			// query.setCacheable(true);
			hiResource = query.getSingleResultOrNull();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResource;
	}

	/*
	 * @inherited
	 */
	@Deprecated
	@Override
	public List<HIResource> getAllResourceList(boolean includeFilter) {
		try {
			Integer userId = Integer.valueOf(AuthenticationUtils.getUserId());
			String orgId = AuthenticationUtils.getOrganizationId();
			List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();

			Session currentSession = getSession();
			if (includeFilter)
				currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			org.hibernate.query.Query query = currentSession.createQuery(
					"from HIResource resource where resource.isVisible=:visible  order by resource.parentId asc");
			query.setParameter("visible", Boolean.TRUE);
			List<HIResource> hiResourceList = query.getResultList();

			return hiResourceList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public HIResource getDeletedResourceById(Integer id) {
		Session session = getSession();
		try {
			return (HIResource) session.get(HIResource.class, id);
		} catch (Exception e) {
			logger.error("Error occurred while fetching deleted HIResource");
		}
		return null;
	}

	private Session getSession() {
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
		} catch (Exception e) {
			session = sessionFactory.openSession();
		}
		session.disableFilter(IS_DELETED_FILTER);
		return session;
	}

	@Override
	public HIResourceEFWSR getHIResourceEFWSRByResourcesId(Integer resourceId) {
		try {
			Session currentSession = getSession();
			SelectionQuery<HIResourceEFWSR> query = currentSession.createSelectionQuery(
					"select res.hiResourceEFWSR from HIResource res where res.resourceId=:id", HIResourceEFWSR.class);
			query.setParameter("id", resourceId);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return null;
	}

	@Override
	public List<HIResourceEFWSR> getAllHIResourceEFWSRByResourcesIds(List<Integer> resourceIds) {
		try {
			Session currentSession = getSession();
			SelectionQuery<HIResourceEFWSR> query = currentSession.createSelectionQuery(
					"select res.hiResourceEFWSR from HIResource res where res.resourceId in (:id)",
					HIResourceEFWSR.class);
			query.setParameterList("id", resourceIds);
			return query.getResultList();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return List.of();
	}

	@Override
	public List<HIResource> getAllConnectedResource(Integer resourceId) {
		try {
			List<HIResource> connectedPaths = new ArrayList<>();

			HIResource targetResource = findAResource(resourceId);

			while (targetResource != null) {
				connectedPaths.add(targetResource);
				Integer parentId = targetResource.getParentId();
				if (parentId == null) {
					break;
				}
				targetResource = findAResource(parentId);
			}

			return connectedPaths;
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return List.of();
	}

	private HIResource findAResource(Integer resourceId) {
		if (resourceId == null)
			return null;
		Session currentSession = getSession();
		SelectionQuery<HIResource> query = currentSession.createSelectionQuery(
				"select  new HIResource(resource.resourceId, resource.created_date, resource.isMigrated, resource.lastUpdatedTime, resource.parentId, resource.createdBy, resource.isFolder, resource.resourcePath, resource.resourceURL, resource.resourceTypeId, resource.title, resource.isVisible, resource.isDeleted) from HIResource resource where resource.isVisible=:visible  and resource.id=:resId",
				HIResource.class);
		query.setParameter("visible", Boolean.TRUE);
		query.setParameter("resId", resourceId);
		HIResource res = query.getSingleResult();
		return res;
	}

	@Override
	public Integer getEfwContentIdByResourceId(Integer resourceId) {
		Integer efwContentId = null;
		Boolean isVisible = Boolean.TRUE;
		Session session = getSession();
		try {
			org.hibernate.query.Query query = session.createQuery(
					"select efw_content_id from hi_resource_efw where resource_id=:resourceId AND is_visible=:flag");
			query.setParameter("resourceId", resourceId);
			query.setParameter("flag", isVisible);
			efwContentId = (Integer) query.uniqueResult();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return efwContentId;
	}

	@Override
	public void hardDeleteFromHiResourceEfw(Integer id) {
		Session session = getSession();
		try {
			Query query = session.createQuery("delete ResourceEfwContents WHERE id=:id");
			query.setParameter("id", id);
			query.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public Long getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(Integer hiResourceId) {
		Long hiRecycleBinId = null;
		Session session = getSession();
		try {
			SelectionQuery<Long> query = session.createSelectionQuery(
					"select hrbin.recycleBin.id from HIRecycleBinHIResourceDB hrbin WHERE hrbin.hiResource.resourceId=:hiResourceId",
					Long.class);
			query.setParameter("hiResourceId", hiResourceId);
			hiRecycleBinId = query.uniqueResult();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hiRecycleBinId;
	}

	public List<HIResource> findAllReportsByMetadataResourceId(Integer resourceId) {
		List<HIResource> resources = new ArrayList<>();
		try {
			String hql = "from HIResource resource where resource.hiResourceHReport.hiResourceMetadata = :resourceId";
			SelectionQuery<HIResource> query = getSession().createSelectionQuery(hql, HIResource.class);
			query.setParameter("resourceId", resourceId);
			resources.addAll(query.getResultList());
		} catch (Exception e) {
			if (logger.isErrorEnabled() || logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			logger.error("Error occured , root cause {}", e.getMessage());
		}
		return resources;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HIResource> findAllEfwsrFilesByReportDirAndFile(String directory, String reportFile) {
		List<HIResource> efwsrFiles = new ArrayList<>();
		try {
			Session session = getSession();
			String hql = "From HIResource res where res.hiResourceEFWSR.reportDirectory =:dir and res.hiResourceEFWSR.reportFile =:report";
			Query query = session.createQuery(hql);
			query.setParameter("dir", directory);
			query.setParameter("report", reportFile);
			efwsrFiles = query.getResultList();
		} catch (Exception e) {
			logger.error("Error occurred while fetching Efwsr resource files , Root cause : {}", e.getMessage());
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
		return efwsrFiles;
	}

	@Override
	public void hardDeleteFromHiResourceEfwByResourceId(Integer resourceId) {
		Session session = getSession();
		try {
			Query query = session.createQuery("delete ResourceEfwContents WHERE resourceId=:id");
			query.setParameter("id", resourceId);
			query.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public HIResourceSecurityDB fetchPermissionBySharedUserIdAndResourceIdTest(Integer userId, Integer resourceId) {
		HIResourceSecurityDB securityInfo = null;

		try {
			Session session = getSession();
			String securitySql = "select hdb.hiResource.resourceId, max(hdb.permission) FROM HIResourceSecurityDB hdb WHERE   hdb.userId.id=:userId  AND hdb.hiResource.resourceId=:resourceId group by hdb.hiResource.resourceId";

			Query query = session.createQuery(securitySql);

			query.setParameter("userId", userId);
			query.setParameter("resourceId", resourceId);
			List<Object[]> securityList = query.getResultList();
			securityInfo = new HIResourceSecurityDB();
			for (Object[] obj : securityList) {
				Integer p = (Integer) obj[1];
				securityInfo.setPermission(p);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return securityInfo;
	}

	@Override
	public HIResourceSecurityDB fetchPermissionBySharedUserIdAndResourceId(Integer userId, Integer resourceId) {
		HIResourceSecurityDB securityInfo = null;
		Integer orgId = null;
		if (StringUtils.isNotEmpty(AuthenticationUtils.getOrganizationId())) {
			orgId = Integer.valueOf(AuthenticationUtils.getOrganizationId());
		}

		List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();
		try {
			Session session = getSession();
			String securitySql = "select hdb.hiResource.resourceId, max(hdb.permission) FROM HIResourceSecurityDB hdb WHERE  ( hdb.userId.id=:userId ";
			if (orgId != null) {
				securitySql += " or hdb.orgId.id = :orgId ";
			}
			if (!userRolesIds.isEmpty()) {
				securitySql += " or hdb.roleId.id in (:roleIds)  ";
			}
			securitySql += " ) AND hdb.hiResource.resourceId=:resourceId group by hdb.hiResource.resourceId";

			Query query = session.createQuery(securitySql);
			if (orgId != null) {
				query.setParameter("orgId", orgId);
			}
			if (!userRolesIds.isEmpty()) {
				List<Integer> roleIdList = new ArrayList<>();
				for (String ids : userRolesIds) {
					roleIdList.add(Integer.valueOf(ids));
				}
				query.setParameter("roleIds", roleIdList);
			}
			query.setParameter("userId", userId);
			query.setParameter("resourceId", resourceId);
			List<Object[]> securityList = query.getResultList();
			securityInfo = new HIResourceSecurityDB();
			for (Object[] obj : securityList) {
				Integer p = (Integer) obj[1];
				securityInfo.setPermission(p);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return securityInfo;
	}

	@Override
	public List<HIEFWD> getHIResourceEFWDByParentResourceId(Integer resourceId) {
		List<HIEFWD> hiResourceEFWDList = null;
		try {
			Session currentSession = getSession();
			SelectionQuery<HIEFWD> query = currentSession
					.createSelectionQuery("from HIEFWD WHERE parentResource.resourceId=:resourceId", HIEFWD.class);
			query.setParameter("resourceId", resourceId);
			// query.setCacheable(true);
			hiResourceEFWDList = query.getResultList();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResourceEFWDList;
	}

	@Override
	public List<String> fetchGeneratedUrlsFromSpecifiedPath(String urlOrTitle, Long resourceTypeId, Integer parentId,
			Boolean isForUrlGen) {
		List<String> data = null;
		String queryStr, sequenceUrl, sequenceUrl2 = null;
		Integer urlOrTitleLen = null;
		try {
			Session currenSession = getSession();
			if (isForUrlGen) {
				urlOrTitle += "_%";
				sequenceUrl = urlOrTitle + "__Copy%";
				sequenceUrl2 = urlOrTitle + "_Copy%";
				String ext = ResourceTypeIDMap.getResourceTypExtension(resourceTypeId);
				if (ext.substring(1).equals("efwfolder")) {
					urlOrTitleLen = urlOrTitle.length();
					queryStr = "SELECT SUBSTR(res.resourceURL,:urlOrTitleLen,LENGTH(res.resourceURL)-:urlOrTitleLen)"
							+ " FROM HIResource res WHERE res.parentId=:parentId "
							+ " AND res.resourceURL LIKE :urlOrTitle" + " AND res.resourceURL NOT LIKE :sequenceUrl "
							+ "	AND res.resourceURL NOT LIKE :sequenceUrl2 "
							+ "	AND res.resourceTypeId=:resourceTypeId";

				} else {
					queryStr = "SELECT res.resourceURL FROM HIResource res WHERE res.parentId=:parentId "
							+ " AND res.resourceURL LIKE :urlOrTitle" + " AND res.resourceURL NOT LIKE :sequenceUrl "
							+ "	AND res.resourceURL NOT LIKE :sequenceUrl2 "
							+ "	AND res.resourceTypeId=:resourceTypeId";
				}
			} else {
				urlOrTitle += "(%";
				urlOrTitleLen = urlOrTitle.length();
				sequenceUrl = urlOrTitle + ") Copy%";
				queryStr = "SELECT SUBSTR(res.title,:urlOrTitleLen,LENGTH(res.title)-:urlOrTitleLen)"
						+ " FROM HIResource res WHERE res.parentId=:parentId " + " AND res.title LIKE :urlOrTitle"
						+ " AND res.title NOT LIKE :sequenceUrl " + " AND res.resourceTypeId=:resourceTypeId";
			}

			org.hibernate.query.Query query = currenSession.createQuery(queryStr);
			query.setParameter("urlOrTitle", urlOrTitle);
			query.setParameter("sequenceUrl", sequenceUrl);
			query.setParameter("parentId", parentId);
			query.setParameter("resourceTypeId", resourceTypeId);
			if (urlOrTitleLen != null)
				query.setParameter("urlOrTitleLen", urlOrTitleLen);
			if (sequenceUrl2 != null)
				query.setParameter("sequenceUrl2", sequenceUrl2);
			data = query.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	@Override
	public HIResource fetchResourceBasedOnTitleAndParentId(String title, Integer parentId) {
		HIResource resource = null;
		try {
			Session currentSession = sessionFactory.getCurrentSession();
			org.hibernate.query.Query query = currentSession
					.createQuery("FROM HIResource res WHERE res.title=:title AND res.parentId=:parentId");
			query.setParameter("title", title);
			query.setParameter("parentId", parentId);
			resource = (HIResource) query.uniqueResult();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resource;
	}

	@Override
	public Map<Integer, Boolean> fetchAllParentResourceIdsForAUser(Integer userId) {

		List<Integer> rolesForLoggedInuser = AuthenticationUtils.getUserRolesIds().stream()
				.map(e -> Integer.parseInt(e)).collect(Collectors.toList());
		String orgForLoggedInUser = AuthenticationUtils.getOrganizationId();
		List<Integer> parentResources = new ArrayList<>();
		Map<Integer, Boolean> map = new HashMap<>();
		try {
			Query query;
			Session currentSession = sessionFactory.getCurrentSession();
			String sharedQuery = "SELECT sec.hiResource.resourceId FROM HIResourceSecurityDB sec WHERE sec.userId.id=:userId";
			if (orgForLoggedInUser != null) {
				sharedQuery += " OR sec.orgId.id=:orgForLoggedInUser";
				query = currentSession.createQuery(sharedQuery);
				query.setParameter("orgForLoggedInUser", Integer.parseInt(orgForLoggedInUser));
			} else if (!rolesForLoggedInuser.isEmpty()) {
				sharedQuery += " OR sec.roleId.id in :rolesForLoggedInuser";
				query = currentSession.createQuery(sharedQuery);
				query.setParameter("rolesForLoggedInuser", rolesForLoggedInuser);
			} else {
				query = currentSession.createQuery(sharedQuery);
			}
			query.setParameter("userId", userId);
			parentResources = query.getResultList();

			Query query2 = currentSession.createQuery(
					"SELECT hiResource.resourceId FROM HIResource hiResource WHERE hiResource.createdBy=:userId");
			query2.setParameter("userId", userId);
			parentResources.addAll(query2.getResultList());
			parentResources.forEach(e -> {
				map.put(e, Boolean.FALSE);
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;
	}

	@Override
	public List<HIResource> fetchResourceIdsBasedOnConTypeAndConId(String conType, Integer conId) {
		List<HIResource> data = new ArrayList<>();
		try {
			Session currSession = getSession();
			String queryStr = "FROM HIResource res WHERE res.resourceId IN (SELECT hcrCon.hiResourceHcr.resourceId FROM HIHcrConnections hcrCon WHERE hcrCon.connectionType=:conType AND hcrCon.id IN ";
			if (conType.equals("global.jdbc")) {
				queryStr += "(SELECT hcrGlobal.hiHcrConnections.id FROM HIHcrConnectionsGlobal hcrGlobal WHERE hcrGlobal.globalConnections.globalId=:conId))";
			} else {
				queryStr += "(SELECT hcrEfw.hiHcrConnections.id FROM HIHcrConnectionsEfwd hcrEfw WHERE hcrEfw.hiEfwdConnection.id=:conId))";
			}
			org.hibernate.query.Query query = currSession.createQuery(queryStr);
			query.setParameter("conType", conType);
			query.setParameter("conId", conId);
			data = query.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	@Override
	public void deleteHIResource(HIResource hiResource) {
		try {
			getSession().delete(hiResource);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String getUniqueUrl(String rawString, ResourceType resourceType) {
		Long resourceTypeId = resolveResourceTypeId(rawString, resourceType);

		String rawUrlWithoutExtension = FilenameUtils.getFullPath(rawString) + FilenameUtils.getBaseName(rawString);
		String rawExtension = FilenameUtils.getExtension(rawString);
		String urlLower = rawString.toLowerCase();
		String extensionLower = FilenameUtils.getExtension(urlLower);
		String urlWithoutExtension = FilenameUtils.getFullPath(urlLower) + FilenameUtils.getBaseName(urlLower);

		SelectionQuery<String> query = getSession().createSelectionQuery("""
				select r.resourceURL from HIResource r
				where lower(r.resourceURL) like :prefix
				  and lower(r.resourceURL) <> :exactUrl
				  and r.resourceTypeId = :resourceTypeId
				""", String.class);
		query.setParameter("prefix", urlWithoutExtension + "_%");
		query.setParameter("exactUrl", urlLower);
		query.setParameter("resourceTypeId", resourceTypeId);

		Pattern suffixPattern = buildSuffixPattern(urlWithoutExtension, extensionLower);
		int maxSuffix = 0;
		for (String candidate : query.getResultList()) {
			maxSuffix = Math.max(maxSuffix, extractSuffixNumber(candidate.toLowerCase(), suffixPattern));
		}

		int nextSuffix = maxSuffix + 1;
		for (int attempt = 0; attempt < MAX_UNIQUE_URL_SUFFIX_ATTEMPTS; attempt++) {
			String formattedNumber = String.format("%02d", nextSuffix + attempt);
			String newUrl = rawUrlWithoutExtension + "_" + formattedNumber;
			if (StringUtils.isNotBlank(rawExtension)) {
				newUrl = newUrl + "." + rawExtension;
			}
			if (!recordExists(newUrl.toLowerCase())) {
				return newUrl;
			}
		}
		throw new IllegalStateException("Unable to generate unique URL for: " + rawString);
	}

	private Long resolveResourceTypeId(String rawString, ResourceType resourceType) {
		if (resourceType != null) {
			return resourceType.getResourceTypeId();
		}
		String extension = FilenameUtils.getExtension(rawString.toLowerCase());
		ResourceType resolvedType;
		if (StringUtils.isBlank(extension)) {
			resolvedType = resourceTypeService.getResourceTypeByTypeAndExtension("folder",
					"." + JsonUtils.getFolderFileExtension());
		} else {
			resolvedType = resourceTypeService.getResourceTypeByTypeAndExtension(extension, "." + extension);
		}
		if (resolvedType == null) {
			throw new IllegalArgumentException("Unable to resolve resource type for URL: " + rawString);
		}
		return resolvedType.getResourceTypeId();
	}

	private static Pattern buildSuffixPattern(String baseLower, String extensionLower) {
		String escapedBase = Pattern.quote(baseLower);
		if (StringUtils.isNotBlank(extensionLower)) {
			return Pattern.compile("^" + escapedBase + "_(\\d+)\\." + Pattern.quote(extensionLower) + "$");
		}
		return Pattern.compile("^" + escapedBase + "_(\\d+)$");
	}

	private static int extractSuffixNumber(String urlLower, Pattern suffixPattern) {
		Matcher matcher = suffixPattern.matcher(urlLower);
		if (matcher.matches()) {
			return Integer.parseInt(matcher.group(1));
		}
		return 0;
	}

	private final boolean recordExists(String url) {
		Session session = getSession();
		SelectionQuery<?> query = session
				.createSelectionQuery("select r.resourceId FROM HIResource r where lower(r.resourceURL) = :url");
		query.setParameter("url", url);
		query.setMaxResults(1);
		return query.uniqueResultOptional().map(id -> id instanceof Number && ((Number) id).intValue() != 0)
				.orElse(false);

	}

	@Override
	public List<HIResource> findAllResources(String url) {

		String[] parts = url.split("/");

		List<String> paths = new ArrayList<>();
		StringBuilder current = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {
			if (current.length() > 0) {
				current.append("/");
			}
			current.append(parts[i]);
			paths.add(current.toString());
		}

		try {

			Session session = getSession();

			String query = """
					select %s
					from HIResource r
					where r.resourceURL in (:paths)
					  and r.isVisible = true
					  and r.isDeleted = false
					""".formatted(RESOURCE_PROJECTION);

			SelectionQuery<HIResource> selectionQuery = session.createSelectionQuery(query, HIResource.class);
			selectionQuery.setParameter("paths", paths);
			return selectionQuery.getResultList();
		} catch (Exception e) {
		}
		return Collections.emptyList();
	}

	@Override
	public List<Integer> getHIResourceIdsOfActiveUser() {
		Integer userId = Integer.valueOf(AuthenticationUtils.getUserId());
		try {
			Session session = getSession();
			String query = "select r.resourceId from HIResource r where r.createdBy =:createdBy or r.createdBy is null";
			SelectionQuery<Integer> selectionQuery = session.createSelectionQuery(query, Integer.class);
			selectionQuery.setParameter("createdBy", userId);
			return selectionQuery.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}


	@Override
	public HIResource findResourceById(Integer resourceId, boolean applyFilter) {
		HIResource hiResource = null;
		try {
			Session currentSession = getSession();
			
			if ( applyFilter ) {
				currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			}
			
			String queryString = """
					select %s
					from HIResource r
					where r.resourceId=:resourceId
					  and r.isVisible = true
					""".formatted(RESOURCE_PROJECTION);
			SelectionQuery<HIResource> query = currentSession.createSelectionQuery(queryString, HIResource.class);
			query.setParameter("resourceId", resourceId);
			hiResource = query.getSingleResultOrNull();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResource;
	}
	
	@Override
	public List<Integer> getChildrenResourceByParentIds(List<Integer> parentIds) {
		try {
			long now = System.currentTimeMillis();
			Session session = getSession();
			Set<Integer> pendingIds =  new HashSet<>(parentIds);
			List<Integer> allChildren = new ArrayList<>();
			while (!pendingIds.isEmpty()) {
				String resourceQuery = "SELECT resourceId , isFolder FROM HIResource  where parentId in (:parentIds)";
				SelectionQuery<Object[]> initialResources = session.createSelectionQuery(resourceQuery,Object[].class);
				initialResources.setParameterList("parentIds", pendingIds);
				List<Object[]> results = initialResources.list();
				pendingIds = new HashSet<>();
				if (results != null && !results.isEmpty()) {
					for(Object[] row : results) {
						Integer resourceId = (Integer) row[0];
						Boolean isFolder = (Boolean) row[1];
						allChildren.add(resourceId);
						if(isFolder) {
							pendingIds.add(resourceId);
						}
					}
				}
			}
			long end = System.currentTimeMillis();
			logger.info("getChildrenResourceByParentIds took : {} .ms",(end-now));
			return allChildren;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public Map<Integer, List<HIResource>> findAllReportsByMetadataResourceIds(List<Integer> metadataResourceIds) {
		Map<Integer, List<HIResource>> reportsByMetadataId = new HashMap<>();
		if (metadataResourceIds == null || metadataResourceIds.isEmpty()) {
			return reportsByMetadataId;
		}
		try {
			String hql = "select " + RESOURCE_PROJECTION + ", h.hiResourceMetadata"
					+ " from HIResource r inner join r.hiResourceHReport h"
					+ " where h.hiResourceMetadata in (:resourceIds)";

			String hqlModel = "select " + RESOURCE_PROJECTION + ", h.hiResourceMetadata"
					+ " from HIResource r inner join r.aiModel h"
					+ " where h.hiResourceMetadata in (:resourceIds)";

			SelectionQuery<Object[]> query = getSession().createSelectionQuery(hql, Object[].class);
			query.setParameterList("resourceIds", metadataResourceIds);
			for (Object[] row : query.getResultList()) {
				HIResource report = (HIResource) row[0];
				Integer metadataId = (Integer) row[1];
				reportsByMetadataId.computeIfAbsent(metadataId, id -> new ArrayList<>()).add(report);
			}

			SelectionQuery<Object[]> queryModel = getSession().createSelectionQuery(hqlModel, Object[].class);
			queryModel.setParameterList("resourceIds", metadataResourceIds);
			for (Object[] row : queryModel.getResultList()) {
				HIResource report = (HIResource) row[0];
				Integer metadataId = (Integer) row[1];
				reportsByMetadataId.computeIfAbsent(metadataId, id -> new ArrayList<>()).add(report);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled() || logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			logger.error("Error fetching reports by metadata resource ids, root cause {}", e.getMessage());
		}
		return reportsByMetadataId;
	}

	@Override
	public Map<Integer, List<HIResource>> findAllInstantReportsByModelResourceIds(List<Integer> modelResourceIds) {
		Map<Integer, List<HIResource>> instantReportsByModelId = new HashMap<>();
		if (modelResourceIds == null || modelResourceIds.isEmpty()) {
			return instantReportsByModelId;
		}
		try {
			String hql = "select " + RESOURCE_PROJECTION + ", ir.hiResourceModel"
					+ " from HIResource r inner join r.hiResourceInstantReport ir"
					+ " where ir.hiResourceModel in (:resourceIds)";

			SelectionQuery<Object[]> query = getSession().createSelectionQuery(hql, Object[].class);
			query.setParameterList("resourceIds", modelResourceIds);
			for (Object[] row : query.getResultList()) {
				HIResource instantReport = (HIResource) row[0];
				Integer modelId = (Integer) row[1];
				instantReportsByModelId.computeIfAbsent(modelId, id -> new ArrayList<>()).add(instantReport);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled() || logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			logger.error("Error fetching instant reports by model resource ids, root cause {}", e.getMessage());
		}
		return instantReportsByModelId;
	}

	@Override
	public List<HIResource> getHIResourcesByIds(List<Integer> resourceIds, Boolean applyFilter) {
		try {
			Session session = getSession();
			
			if ( applyFilter ) {
				session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			}

			String query = """
					select  %s
					from HIResource r
					where r.resourceId in (:resourceIds)
					  and r.isVisible = true
					""".formatted(RESOURCE_PROJECTION);

			SelectionQuery<HIResource> selectionQuery = session.createSelectionQuery(query, HIResource.class);
			selectionQuery.setParameter("resourceIds", resourceIds);
			return selectionQuery.getResultList();
		} catch (Exception e) {
			logger.error("Exception occurred while fetching resources by ids ", e);
		}
		return Collections.emptyList();
	}
	
	@Override
	public void restoreResourcesByIds(List<Integer> resourceIds) {
		if (resourceIds == null || resourceIds.isEmpty()) {
			return;
		}
		try {
			MutationQuery mutationQuery = getSession().createMutationQuery("UPDATE HIResource r SET r.isDeleted = false WHERE r.resourceId in (:resourceIds) AND r.isDeleted = true");
			mutationQuery.setParameterList("resourceIds", resourceIds);
			int noOfUpdatedRecords = mutationQuery.executeUpdate();
			logger.debug("restored resources count : {}", noOfUpdatedRecords);
		} catch (Exception e) {
			logger.error("Error restoring resources by ids, root cause {}", e.getMessage());
		}
	}
	
	private void markResourceAsDeleted(Integer resourceId) {
		try {
			Session session = getSession();
			String hql = "UPDATE HIResource set isDeleted=true where resourceId =:resourceId and isDeleted=false";
			MutationQuery updateQuery = session.createMutationQuery(hql);
			updateQuery.setParameter("resourceId", resourceId);
			int count =  updateQuery.executeUpdate();
		}
		catch (Exception e) {
			logger.error("Error occurred while marking resource as deleted.");
		}
	}
}
