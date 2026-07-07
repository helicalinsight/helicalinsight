package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.utility.ApplicationUtilities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the RoleDao interface and responsible for role
 * activities like add, edit, delete, list all roles.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */
@SuppressWarnings("unchecked")
@Repository
public class RoleDoaImpl implements RoleDao {

    private static final Logger logger = LoggerFactory.getLogger(RoleDoaImpl.class);

    /**
     * Springs dependency injection facilities which instantiate the
     * hibernate session factory bean from spring container
     */

    
    @Autowired
    private SessionFactory sessionFactory;
    
    
    private Session getSession() {
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
		}
		catch (Exception e) {
			session = sessionFactory.openSession();
		}
		return session;
	}

    /**
     * this is override method get the current session from session factory
     * object and save the role object
     *
     * @param role role object
     */

    @Override
    public int add(Role role) {
        try {
            getSession().save(role);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return role.getId();
    }

    /**
     * this is override method get the current session from session factory
     * object and update the role object
     *
     * @param role role object
     */

    @Override
    public void edit(Role role) {
        try {
            getSession().update(role);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and delete the role object by role id
     *
     * @param roleId role id
     */

    @Override
    public void delete(int roleId) {
        try {
            Session currentSession = getSession();
            Role role = getRole(roleId);
            Query query = (Query) currentSession.createQuery("delete from GlobalConnectionSecurity where roleId.id=:roleId");
            query.setParameter("roleId",roleId);
            query.executeUpdate();


            Query hiResourceQuery = (Query) currentSession.createQuery("delete from HIResourceSecurityDB security where security.roleId.id=:roleId");
            hiResourceQuery.setParameter("roleId",roleId);
            hiResourceQuery.executeUpdate();

            currentSession.delete(role);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the role object by role id
     *
     * @param roleId role id
     * @return role object
     */

    @Override
    public Role getRole(int roleId) {
        Role role = null;
        try {
            role = (Role) getSession().get(Role.class, roleId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception", e);
        }
        return role;
    }

    /**
     * this is override method get the current session from session factory
     * object and return the list of all existing roles
     *
     * @return list of role objects
     */


    @Override
    public List<Role> findAllRoles(int offset, int limit, String searchPhrase, String searchOn,
                                   LimitOffsetModel pageCount) {
        if ((searchOn == null) || ("".equalsIgnoreCase(searchOn)) || searchOn.trim().length() <= 0) {

            return getAllRole(offset, limit, pageCount);
        } else {
            List<Role> roleList=new ArrayList<>();
            List<Predicate> predicates=new ArrayList<>();
        	CriteriaBuilder cb = getSession().getCriteriaBuilder();
        	jakarta.persistence.criteria.CriteriaQuery<Role> cr=cb.createQuery(Role.class);
        	Root<Role> resource = cr.from(Role.class);
            if ("name".equalsIgnoreCase(searchOn)) {
                searchPhrase = "%" + searchPhrase + "%";
                predicates.add(cb.like(resource.get("role_name"), searchPhrase));
				try {
					cr.select(resource).where(predicates.toArray(new Predicate[] {}));
					roleList = getSession().createQuery(cr).getResultList();
				} catch (Exception e) {
					if (e instanceof NoResultException)
						return null;
					logger.error("Exception", e);
				}
            }

            if (roleList.size()==0) {
                pageCount.setTotalCount(0);
            } else {
                pageCount.setTotalCount(roleList.size());
            }
            return roleList;
        }
    }

    @Override
    public List<Role> getAllRole(int offset, int limit, LimitOffsetModel pageCount) {
        List<Role> roleList = null;
        try {
            roleList = ApplicationUtilities.castList(Role.class, getSession().createQuery
                    ("from" + " Role order by org_id").setFirstResult(offset).setMaxResults(limit).list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        if (roleList != null) {
        	// TODO : Try to implement this in Query.
        	roleList = roleList.stream().filter(role -> role.getOrganization() == null ||  !role.getOrganization().isDeleted())
        			.collect(Collectors.toList());
            pageCount.setTotalCount(roleList.size());
        }
        return roleList;
    }

    @Override
    public List<Role> getAllRole(LimitOffsetModel pageCount) {
        List<Role> roleList = null;
        try {
            roleList = ApplicationUtilities.castList(Role.class, getSession().createQuery
                    ("from" + " Role").list());

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        if (roleList != null) {
            pageCount.setTotalCount(roleList.size());
        }
        return roleList;
    }

    @Override
    public List<Role> getAllRoles() {
        List<Role> roleList = null;
        try {
            Session currentSession = getSession();
            Query query = (Query) currentSession.createQuery("from Role");
            roleList = ApplicationUtilities.castList(Role.class, query.getResultList());
        } catch (Exception ex) {
            logger.error("Exception", ex);
        }
        return roleList;
    }

    /**
     * this is override method get the current session from session factory
     * object and return only those role matches by role name and organization
     * is is null
     *
     * @param role_name role name
     * @return role object
     */

    @Override
    public Role findByName(String role_name) {
        Role role = null;
        List<Predicate> predicates=new ArrayList<>();
        try {
        	CriteriaBuilder cb = getSession().getCriteriaBuilder();
        	CriteriaQuery<Role> cr=cb.createQuery(Role.class);
        	Root<Role> resource = cr.from(Role.class);
        	predicates.add(cb.equal(resource.get("role_name"), role_name));
        	predicates.add(cb.isNull(resource.get("org_id")));        	
        	cr.select(resource).where(predicates.toArray(new Predicate[] {}));
        	role=getSession().createQuery(cr).getSingleResultOrNull();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return role;
    }

    /*
     * public Role findByName(String role_name) { Role role = null; try {
     * Criteria criteria =
     * getSession().createCriteria(Role.class);
     * criteria.add(Restrictions.eq("role_name", role_name)); role = (Role)
     * criteria.uniqueResult(); } catch (Exception e) {
     * logger.error(e.getMessage()); } return role; }
     */

    /**
     * this is override method get the current session from session factory
     * object and return the role object matches by role name and organization
     * id
     *
     * @param role_name role name
     * @param org_id    organization id
     * @return role object
     */

    @Override
    public Role findRoleByNameNOrgId(String role_name, Integer org_id) {
		Role role = null;
		try {
			List<Predicate> predicates = new ArrayList<>();
			CriteriaBuilder cb = getSession().getCriteriaBuilder();
			CriteriaQuery<Role> cr = cb.createQuery(Role.class);
			Root<Role> resource = cr.from(Role.class);
			predicates.add(cb.equal(resource.get("role_name"), role_name));
			predicates.add(cb.equal(resource.get("org_id"), org_id));
			cr.select(resource).where(predicates.toArray(new Predicate[] {}));
			role = getSession().createQuery(cr).getSingleResultOrNull();
		} catch (Exception e) {
			if (e instanceof NoResultException)
				return null;
			logger.error("Exception", e);
		}
		return role;
    }

    /**
     * this is override method get the current session from session factory
     * object and delete the role object by it's organization id
     *
     * @param org_id organization id
     */

    @Override
    public void deleteOrganization(int org_id) {
        List<Integer> query = getSession().createQuery("select id from Role where " +
                "" + "org_id=:org_id").setParameter("org_id", org_id).list();
        for (Integer id : query) {
            deleteRoles(id);
        }
    }

    private void deleteRoles(Integer id) {
        try {
            Session currentSession = getSession();
            Role role = findRole(id);
            currentSession.delete(role);

        } catch (Exception e) {
            logger.error("Exception", e);
        }
	}

	private Role findRole(Integer id) {
        Role role = null;
        try {
        	role = (Role) getSession().get(Role.class, id);
            if (role == null) {
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return role;
	}

	/**
     * Returns a list of roles of a particular organization. If org_id is null then
     * only super user organization roles are returned.
     *
     * @param orgId organization id
     * @return list of role objects
     */
    @Override
    public List<Role> getOrganizationRoles(Integer orgId, LimitOffsetModel limitOffsetModel) {
        String searchOn = limitOffsetModel.getSearchOn();
        Integer offset = limitOffsetModel.getOffset();
        Integer limit = limitOffsetModel.getLimit();
        String searchPhrase = limitOffsetModel.getSearchPhrase();

        if ((searchOn == null) || ("".equalsIgnoreCase(searchOn))) {
            searchOn = "name";
        }

        if (logger.isDebugEnabled()) {
            logger.info("The organization id is " + orgId + ". LimitOffsetModel is " + limitOffsetModel);
        }

        List<Role> roleList;
        StringBuilder jpql = new StringBuilder("SELECT r FROM Role r WHERE 1=1");

        if ("name".equalsIgnoreCase(searchOn)) {
            if (!limitOffsetModel.isRequestedWithSpecificOrganization()) {
                jpql.append(" AND r.role_name Like :searchPhrase");
            }

            if (orgId == null && limitOffsetModel.isOnlySuperOrganization()) {
                jpql.append(" AND r.org_id IS NULL");
            } else {
                jpql.append(" AND r.org_id = :orgId");
            }
        }

        org.hibernate.query.Query<Role> query = getSession().createQuery(jpql.toString(), Role.class);

        if (!limitOffsetModel.isRequestedWithSpecificOrganization()) {
            query.setParameter("searchPhrase", searchPhrase);
        }

        if (orgId != null || limitOffsetModel.isOnlySuperOrganization()) {
            query.setParameter("orgId", orgId);
        }

        roleList = query.setFirstResult(offset).setMaxResults(limit).getResultList();

        if (roleList.size() == 0) {
            limitOffsetModel.setTotalCount(0);
        } else {
            limitOffsetModel.setTotalCount(roleList.size());
        }

        return roleList;
    }
    @Override
    public List<Role> searchUserRoles(String roleName, int offset, int limit, Integer orgId,
                                      LimitOffsetModel pageCount) {

        List<Role> roles = null;     
        try {
            List<Predicate> predicates=new ArrayList<>();
        	CriteriaBuilder cb = getSession().getCriteriaBuilder();
        	CriteriaQuery<Role> cr=cb.createQuery(Role.class);
        	Root<Role> resource = cr.from(Role.class);
            roleName = "%" + roleName + "%";
        	predicates.add(cb.like(resource.get("role_name"), roleName));
        	predicates.add(cb.equal(resource.get("org_id"), orgId));
            cr.select(resource).where(predicates.toArray(new Predicate[] {}));
            TypedQuery<Role> tq=getSession().createQuery(cr);
            roles = tq.setFirstResult(offset).setMaxResults(limit).getResultList();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return roles;

    }

    @Override
    public Long getRoleCount() {
    	CriteriaBuilder cb = getSession().getCriteriaBuilder();
    	CriteriaQuery<Long> cr=cb.createQuery(Long.class);
    	cr.select(cb.count(cr.from(Role.class)));
        return getSession().createQuery(cr).getSingleResultOrNull();
    }

    @Override
    public boolean isRoleExists(int roleId) {
        return false;
    }

	@Override
	public Role findRoleByNameNOrgName(String roleName, String orgName) {
		 Session session=getSession();
		 SelectionQuery<Role> query=session.createQuery("FROM Role where role_name=:roleName AND org_name=:orgName", Role.class);
		 query.setParameter("roleName", roleName);
		 query.setParameter("orgName", orgName);
		 return query.getSingleResultOrNull();
	}

	@Override
	public Role findRoleByNameNullOrg(String roleName) {
		Session session=getSession();
		SelectionQuery<Role> query=session.createSelectionQuery("FROM Role where role_name=:roleName AND org_id is null", Role.class);
		query.setParameter("roleName", roleName);
		return  query.getSingleResultOrNull();
	}
}
