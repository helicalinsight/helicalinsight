package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinHIResourceDB;
import com.helicalinsight.admin.model.HIRecycleBinHUsers;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.datasource.dao.GlobalConnectionDAO;
import com.helicalinsight.datasource.model.DSTypeTomcat;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.ApplicationUtilities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import com.helicalinsight.resourcesecurity.SecurityUtils;


import org.apache.commons.lang.StringUtils;
import org.audit4j.core.util.Log;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the UserDao interface and responsible for user
 * activities like add, edit, delete, list all user.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */
@SuppressWarnings("unchecked")
@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
    private static final String IS_DELETED_FILTER = "isDeletedFilter";

    /**
     * Spring's dependency injection facilities which instantiate the
     * hibernate session factory bean from spring container
     */
    @Autowired
    private SessionFactory sessionFactory;
    
    /**
     * This is override method get the current session from session factory
     * object and save the user object
     */
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private HIRecycleBinService hiRecycleBinService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private GlobalConnectionDAO dbDao;
    
    @Autowired
    private HIResourceServiceDB serviceDb;
    
    @Override
    public int addUser(User user) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the user object " + user);
            }
            sessionFactory.getCurrentSession().save(user);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return user.getId();
    }

    /**
     * this is override method get the current session from session factory
     * object and update the user object
     *
     * @param user User object
     */

    @Override
    public void editUser(User user) {
        try {
            sessionFactory.getCurrentSession().update(user);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the user object by user name and who's organization is
     * null
     *
     * @param username user name
     * @return user object
     */

    @Override
    public User findUserByNameNorgNull(String username, boolean applyFilter) {
        User user = null;
        try {

        	Session session = this.getSession();
        	session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
        	org.hibernate.query.Query query=session.createQuery("FROM User where username=:username AND org_id is null");
        	query.setParameter("username", username);
        	user =  (User) query.uniqueResult();

        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return user;
    }

    /**
     * this is override method get the current session from session factory
     * object and return the user object by it's user name and organization
     *
     * @param username user name
     * @param org_id   organization id
     * @return user object
     */

    @Override
    public User findUserByNameNOrgId(String username, Integer org_id, boolean applyFilter) {

    	Session session = getSession();
    	session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
    	org.hibernate.query.Query query=session.createQuery("FROM User where username=:username AND org_id=:org_id");
    	query.setParameter("username", username);
    	query.setParameter("org_id", org_id);
        return (User) query.uniqueResult();

    }

    /**
     * this is override method get the current session from session factory
     * object and return the user object by user name
     *
     * @param username user name
     * @return user object
     */

    @Override
    public User searchUserByname(String username) {

    	Session session = getSession();
    	session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
    	Query query=session.createQuery("FROM User where username=:username");
    	query.setParameter("username", username);
    	return  (User) query.uniqueResult();
    }

    @Override
    public void getUserByOrgIdNdelete(Integer org_id) {

		/*
        Query query = session.getCurrentSession().createQuery("delete from User where
		org_id=:org_id");
		query.setParameter("org_id", org_id);
		query.executeUpdate();*/
    	Session session = getSession();
    	session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
        List<Integer> query = session.createQuery("select id from User where " +
                "" + "org_id=:org_id").setParameter("org_id", org_id).list();
        for (Integer ids : query) {
            deleteUser(ids);
        }

    }
    
    /**
     * This method sets the is_deleted property to <b> True </b>  and saves the persistent object User </br>
     * Creates a new record in <b>HIRecycleBin</b> table which is associated with <b>HIRecycleBinHUsers<b>
     * @since 6.0 
     */
	private void softDelete(User user) {
		try {
			user.setDeleted(true);
			editUser(user);
			HIRecycleBin bin = new HIRecycleBin();
			bin.setRecycleBinType(RecycleBinType.H_USERS);
			bin.setDeletedOn(new Date());
			bin.setOrgId(user.getOrganization());
			User deletedBy = findUser(Integer.valueOf(SecurityUtils.securityObject().getCreatedBy()));
			bin.setDeletedBy(deletedBy);
			HIRecycleBinHUsers binItem = new HIRecycleBinHUsers();
			binItem.setUser(user);
			binItem.setRecycleBin(bin);
			bin.setHiRecycleBinHUsers(binItem);
			hiRecycleBinService.save(bin);
		} catch (Exception e) {
			logger.error("Error occurred , root cause : {}", e.getMessage());
		}
	}
    
    /**
     * this is override method get the current session from session factory
     * object and delete the user object by user id
     *
     * @param userId user id
     */

	@Override
    public void deleteUser(int userId) {
        try {
            Session currentSession = getSession();
            User user = findUser(userId);
            
            if(Boolean.FALSE.equals(user.isDeleted())) {
            	softDelete(user);
            }
            else {
            Query deleteAudit = currentSession.createQuery("DELETE HIAuditDetails where triggeredBy = :id");
            deleteAudit.setParameter("id", user);
            deleteAudit.executeUpdate();
            
            Query deleteHIResourcePhase  = currentSession.createQuery("DELETE HIResourcePhaseStatus where user =:id");
            deleteHIResourcePhase.setParameter("id", user);
            deleteHIResourcePhase.executeUpdate();
            
            List<GlobalConnections> connections =  dbDao.findConnectionsByCreatedBy(""+userId);
            
            for(GlobalConnections connection : connections) {
            	dbDao.hardDelete(connection);
            }
            
            List<HIResource> hiResources = serviceDb.getHIResourceByCreatedBy(user.getId());
            for(HIResource eachResource : hiResources) {
            	serviceDb.hardDelete(eachResource);
            }
            
            currentSession.remove(user);

            }
        }catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the user object by it's user idi
     *
     * @param userId user id
     * @return user object
     */

    @Override
    public User findUser(int userId) {
        User user = null;
        try {
        	Session session = getSession();
        	session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
            user = (User) session.get(User.class, userId);
            if (user == null) {
                return null;
            }
            else {
            	if ( user.getOrganization() != null && user.getOrganization().isDeleted()) {
            		user.setDeleted(true);
            	}
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return user;
    }

    /**
     * this is override method get the current session from session factory
     * object and return the list of user's by it's organization id
     *
     * @return list of user objects
     */

    @Override
    public List<User> getOrganizationUsers(int org_id, int offset, int limit, LimitOffsetModel limitOffsetModel) {
        List<User> userList = null;
        try {

        	Session session = getSession();
        	session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
        	Query query=session.createQuery("FROM User where org_id=:org_id ORDER BY username ASC LIMIT :limit OFFSET :offset");
    		query.setParameter("org_id", org_id);
    		userList = query.getResultList();
        } catch (Exception e) {
			if (e instanceof NoResultException)
				return null;
			logger.error("Exception", e);
		}
        return userList;
    }

    @Override
    public User findByIdAndFetchRolesEagerly(int userId) {
        User rolesList = null;
        try {
            rolesList = (User) sessionFactory.getCurrentSession().createQuery("SELECT p FROM User p JOIN" +
                    " FETCH p.roles WHERE" + " p.id=:userId").setParameter("userId", userId).getSingleResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return rolesList;
    }

    @Override
    public List<User> findUsers(LimitOffsetModel limitOffsetModel) {
        String searchOn = limitOffsetModel.getSearchOn();
        String searchPhrase = limitOffsetModel.getSearchPhrase();
        Integer offset = limitOffsetModel.getOffset();
        Integer limit = limitOffsetModel.getLimit();

        if ((searchOn == null) || ("".equals(searchOn)) || (searchOn.trim().length() <= 0)) {
            return getAllUsers(offset, limit, limitOffsetModel,true);
        } else {
            List<User> userList = null;
            Query findQuery = null;
            Query countQuery = null;
            if (!limitOffsetModel.isOnlySuperOrganization()) {
                searchPhrase = "%" + searchPhrase + "%";
            }
            Session currentSession = getSession();
            if ("email".equalsIgnoreCase(searchOn)) {
                findQuery = (Query) currentSession.createQuery("from User user where " +
                        "emailAddress" +
                        " like :emailAddress ORDER BY " + "user.username ASC");
                findQuery.setParameter("emailAddress", searchPhrase);

                countQuery = (Query) currentSession.createQuery("select  count(*) from User user where " +
                        "emailAddress" +
                        " like :emailAddress");
                countQuery.setParameter("emailAddress", searchPhrase);


            } else if ("user".equalsIgnoreCase(searchOn)) {
                findQuery = (Query) currentSession.createQuery("from User user where username" +
                        " like :username ORDER BY " +
                        "user.username ASC");
                findQuery.setParameter("username", searchPhrase);

                countQuery = (Query) currentSession.createQuery("select count(*) from User user where username" +
                        " like :username");
                countQuery.setParameter("username", searchPhrase);


            } else if ("organisation".equalsIgnoreCase(searchOn)) {
                searchPhrase = searchPhrase.replaceAll("%%", "%");
                String nullValue = "%" + ApplicationProperties.getInstance().getNullValue() + "%";
                if (nullValue.equalsIgnoreCase(searchPhrase)) {
                    findQuery = (Query) currentSession.createQuery("from User user where organization" + "" +
                            ".id is null ORDER BY user.username ASC");

                    countQuery = (Query) currentSession.createQuery("select count(*) from User user where organization" + "" +
                            ".id is null");

                } else {
                    findQuery = (Query) currentSession.createQuery("from User user where " + "organization" +
                            ".org_name like :org_name ORDER BY user.username ASC");
                    findQuery.setParameter("org_name", searchPhrase);


                    countQuery = (Query) currentSession.createQuery("select count(*) from User user where " + "organization" +
                            ".org_name like :org_name ");
                    countQuery.setParameter("org_name", searchPhrase);
                }
            } else if ("roles".equalsIgnoreCase(searchOn)) {
                findQuery = (Query) currentSession.createQuery("select distinct user  from User" +
                        "                user  join user.roles r where " +
                        "r.role_name" +
                        " like :role_name ORDER BY " +
                        "user.username ASC");
                findQuery.setParameter("role_name", searchPhrase);

                countQuery = (Query) currentSession.createQuery("select distinct count(user)   from User" +
                        "                user  join user.roles r where " +
                        "r.role_name" +
                        " like :role_name");
                countQuery.setParameter("role_name", searchPhrase);
            }
            if (findQuery != null) {
                Long totalCount = (Long) countQuery.getSingleResult();

                limitOffsetModel.setTotalCount(Integer.valueOf("" + totalCount));
                findQuery.setFirstResult(offset);
                findQuery.setMaxResults(limit);
                List list = findQuery.getResultList();
                return list;
            }
            return userList;
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the list of all existing user objects
     *
     * @return list of user objects
     */

    @Override
    public List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel, boolean applyFilter) {
        List<User> userList = null;
        try {
            logger.info("This query get all user is being executed");

            Session session = getSession();
        	if(applyFilter) {
        		session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
        	}
            Query query = session.createQuery("from User " + "user ORDER BY " +
                    "user.username ASC");
            if (query.getResultList().isEmpty()) {
                limitOffsetModel.setTotalCount(0);

            } else {
                limitOffsetModel.setTotalCount(query.getResultList().size());
            }
            query.setFirstResult(offset);
            query.setMaxResults(limit);


            userList = query.getResultList();
            userList = userList.stream().filter(user -> user.getOrganization() == null ||  !user.getOrganization().isDeleted()).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return userList;
    }

    @Override
    public List<User> searchUserByOrgId(int offset, int limit, Integer org_id, LimitOffsetModel limitOffsetModel) {

    	Session session = getSession();
    	session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
    	Query query=session.createQuery("FROM User where org_id=:org_id ORDER BY username LIMIT :limit OFFSET :offset");
		query.setParameter("org_id", org_id);
		List<User> users = query.getResultList();
        return users;
    }

    @Override
    public List<User> searchUserByRole(int offset, int limit, String role, LimitOffsetModel limitOffsetModel) {
        return null;
    }

    @Override
    public void initializeData() {
        DSTypeTomcat dsTypeTomcat = null;
        try {
        	CriteriaBuilder cb = getSession().getCriteriaBuilder();
        	CriteriaQuery<DSTypeTomcat> cr=cb.createQuery(DSTypeTomcat.class);
        	Root<DSTypeTomcat> resource = cr.from(DSTypeTomcat.class);
        	cr.select(resource).where(cb.equal(resource.get("globalConnections").get("globalId"), 1));
        	dsTypeTomcat = getSession().createQuery(cr).getSingleResult();
			if (dsTypeTomcat != null) {
				String url = dsTypeTomcat.getUrl();
				if (StringUtils.isNotBlank(url)) {
                    String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
                    if(dsTypeTomcat.getDriverClassName().equalsIgnoreCase("org.apache.derby.jdbc.AutoloadedDriver")
                    		&& (dsTypeTomcat.getLastUpdatedTime().before(getDBReleaseDate()))) {
                    	String replace = solutionDirectory.replace("hi-repository", "");
                        String replaceUrl = "jdbc:derby:"+replace+"/db/SampleTravelData";
                        dsTypeTomcat.setUrl(replaceUrl);
                    }
                    sessionFactory.getCurrentSession().update(dsTypeTomcat);
				}
			}
        } catch (Exception e) {
            logger.error("Exception", e);
        }

    }

    @Override
    public List<User> findOrganisationUsers(Integer orgId, LimitOffsetModel limitOffsetModel) {
        String searchOn = limitOffsetModel.getSearchOn();
        String searchPhrase = limitOffsetModel.getSearchPhrase();
        Integer offset = limitOffsetModel.getOffset();
        Integer limit = limitOffsetModel.getLimit();

        if ((searchOn == null) || ("".equals(searchOn)) || (searchOn.trim().length() <= 0)) {
            searchOn = "user";
        }

        List<User> userList;
        Query query = null;
        searchPhrase = "%" + searchPhrase + "%";
        Session currentSession = getSession();
    	currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
        if ("email".equalsIgnoreCase(searchOn)) {
            query = (Query) currentSession.createQuery("from User user where " +
                    "emailAddress" +
                    " like :emailAddress and user.org_id=:org_id  ORDER BY " + "user.username ASC" +
                    " ");
            query.setParameter("emailAddress", searchPhrase);
            query.setParameter("org_id", orgId);
        } else if ("user".equalsIgnoreCase(searchOn)) {
            if (limitOffsetModel.isRequestedWithSpecificOrganization()) {
                //All users of that organization.
                query = (Query) currentSession.createQuery("from User user where user.org_id=:org_id  " + "ORDER BY user" +
                        ".username ASC");
                query.setParameter("org_id", orgId);
            } else {
                query = (Query) currentSession.createQuery("from User user where username" +
                        " like :username and user.org_id=:org_id  ORDER BY " +
                        "user.username ASC");
                query.setParameter("username", searchPhrase);
                query.setParameter("org_id", orgId);
            }
        } else if ("roles".equalsIgnoreCase(searchOn)) {
            query = (Query) currentSession.createQuery("select distinct user  from User" +
                    "                user  join user.roles r where " +
                    "r.role_name" +
                    " like :role_name  and r.org_id=:org_id and user.org_id=:org_id ORDER BY " +
                    "user.username ASC");
            query.setParameter("role_name", searchPhrase);
            query.setParameter("org_id", orgId);
        }

        if (query.getResultList().isEmpty()) {
            limitOffsetModel.setTotalCount(0);
            return null;
        } else {
            limitOffsetModel.setTotalCount(query.getResultList().size());
        }
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        userList = query.getResultList();
        return userList;
    }
    
    private Date getDBReleaseDate() {
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.parse("2023-05-08");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

    }

	@Override
	public List<User> getAllUsersOfOrganization(Integer orgId) {
		try {
			Session session = getSession();
			Query query = session.createQuery("from User user where user.org_id =:orgId  ORDER BY user.username ASC");
			query.setParameter("orgId", orgId);
			return query.getResultList();
		} catch (Exception e) {
			logger.error("Error occurred while fetching the Organization users");
			return List.of();
		}
		
	}
	
	@Override
	public User isUserAlreadyExisted(String uname, Integer orgId) {
		User existedUser=null;
		try {
			Session session = getSession();
			org.hibernate.query.Query query;
			if(orgId==null) {
				query = session.createQuery("from User user where user.org_id is null AND user.username=:uname");
			}
			else {
				query = session.createQuery("from User user where user.org_id =:orgId AND user.username=:uname");
				query.setParameter("orgId", orgId);	
			}
			query.setParameter("uname", uname);
			existedUser=(User)query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return existedUser;
	}
	
	 private Session getSession() {
			Session currentSession = null;
			try {
				currentSession =  sessionFactory.getCurrentSession();
			}
			catch (Exception e) {
				currentSession = sessionFactory.openSession();
			}
			currentSession.disableFilter(IS_DELETED_FILTER);
			return currentSession;
		}
	
	@Override
    public User getUser(int userId) {
        User user = null;
        try {
        	Session session = getSession();
            user = (User) session.get(User.class, userId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return user;
    }
	
	@Override
	public Long findUserCount() {
		
		try {
			Session session = getSession();
			Query<Long> query = session.createQuery("select count(e.username) from User e",Long.class);
			return query.getSingleResult();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	@Override
	public void restoreUser(Integer userId) {
		try {
			Session session = getSession();
			MutationQuery query = session.createMutationQuery(""" 
					update User
					set deleted = false
					where id = :id
					  and deleted = true
					
					""");
			query.setParameter("id", userId);
			query.executeUpdate();
		}
		catch (Exception e) {
			logger.error("Error occurred while restoring the user : {}", userId);
		}
	}
}