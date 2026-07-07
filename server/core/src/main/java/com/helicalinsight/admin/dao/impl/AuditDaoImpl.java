package com.helicalinsight.admin.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.springframework.stereotype.Repository;
import com.helicalinsight.admin.dao.AuditDao;
import com.helicalinsight.admin.model.HIAuditDetails;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class AuditDaoImpl implements AuditDao {

	private final SessionFactory sessionFactory;
	
	@Override
	public void audit(HIAuditDetails audit) {
		Session session = sessionFactory.getCurrentSession();
		session.save(audit);
	}

	@Override
	public List<HIAuditDetails> fetchAllBasedOnResourceId(Integer resourceId) {
		List<HIAuditDetails> auditDetails=null;
		try {
			Session session = sessionFactory.getCurrentSession();
			SelectionQuery<HIAuditDetails> query=session.createSelectionQuery("FROM HIAuditDetails audit WHERE audit.hiResource.resourceId=:resourceId",HIAuditDetails.class);
			query.setParameter("resourceId", resourceId);
			auditDetails=query.list();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return auditDetails;
	}

	@Override
	public void save(HIAuditDetails auditDetails) {
		try {
			Session session = sessionFactory.getCurrentSession();
			session.save(auditDetails);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
