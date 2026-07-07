package com.helicalinsight.datasource.dao;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.dao.OrganizationDao;
import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.dto.DataSourceTypeDTO;
import com.helicalinsight.admin.dto.GlobalConnectionDTO;
import com.helicalinsight.admin.dto.GlobalDatasourceLookupDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.model.HIHcrConnectionsGlobal;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinDSGlobalConnections;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.model.*;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.components.JsonResultSetTransformer;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.Query;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by Helical on 5/18/2021.
 */
@Repository
public class GlobalConnectionDAOImpl implements GlobalConnectionDAO {

    private static final Logger logger = LoggerFactory.getLogger(GlobalConnectionDAOImpl.class);
    private static final String IS_DELETED_FILTER = "isDeletedFilter";

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    UserDao userDao;

    @Autowired
    OrganizationDao organizationDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    HIResourceDBDAO dbDao;

    @Autowired
    HIRecycleBinService recycleBinService;

    @Override
    public int addGlobalConnections(GlobalConnections globalConnections) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the global connection " + globalConnections);
            }
            globalConnections.setDeleted(false);
            if(StringUtils.isBlank(globalConnections.getVendor())) {
                globalConnections.setVendor(null);
            }
            getSession().save(globalConnections);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return globalConnections.getGlobalId();
    }

    @Override
    public void editGlobalConnections(GlobalConnections globalConnections) {
        try {
            getSession().update(globalConnections);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }


    private boolean softDelete(GlobalConnections globalConnections) {
        globalConnections.setDeleted(true);

        HIRecycleBin bin = new HIRecycleBin();
        bin.setRecycleBinType(RecycleBinType.DS_GLOBAL_CONNECTIONS);
        bin.setDeletedBy(userDao.findUser(Integer.valueOf(SecurityUtils.securityObject().getCreatedBy())));
        User owner = userDao.findUser(Integer.valueOf(globalConnections.getCreatedBy()));
        bin.setCreatedBy(owner);
        bin.setOrgId(owner.getOrganization());
        HIRecycleBinDSGlobalConnections binConnection = new HIRecycleBinDSGlobalConnections();
        binConnection.setGlobalConnection(globalConnections);
        binConnection.setRecycleBin(bin);
        bin.setHiRecycleBinDsGlobalConnections(binConnection);
        bin.setDeletedOn(new Date());
        getSession().update(globalConnections);
        return recycleBinService.save(bin);
    }

    @Override
    public boolean hardDelete(GlobalConnections globalConnections) {
        Integer globalId = globalConnections.getGlobalId();
        try {
            recycleBinService.deleteRecycleBinByGlobalId(globalId);
            Boolean deletedRelatedResources = dbDao.deleteDatasoureRelatedResources(globalId);
            if (Boolean.TRUE.equals(deletedRelatedResources)) {
                GlobalConnections globalConnection = findGlobalConnectionById(globalId);
                String dsType = globalConnection.getDsType();
                String dsTypeClass = JsonUtils.getDSTypeClass(dsType);


                Query updateResourceQuery = getSession().createQuery ("delete GlobalConnectionSecurity where globalConnections.globalId=:id");
                updateResourceQuery.setParameter("id", globalId);
                updateResourceQuery.executeUpdate();

                Query<?> updateResourceQueryDSType = getSession()
                        .createQuery("delete " + dsTypeClass + " where globalConnections.globalId=:id");
                updateResourceQueryDSType.setParameter("id", globalId);
                updateResourceQueryDSType.executeUpdate();

                deleteExtraOptionByGloablId(globalId);

                Query<GlobalConnections> updateResourceQuerySecurity = getSession()
                        .createQuery("delete GlobalConnections where globalId=:id");
                updateResourceQuerySecurity.setParameter("id", globalId);
                updateResourceQuerySecurity.executeUpdate();
                return true;
            } else {
                logger.error("Failed to deleted related resources");
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteGlobalConnections(int globalId) {
        GlobalConnections globalConnection = findGlobalConnectionById(globalId);
        if(globalConnection.isDeleted() != null && globalConnection.isDeleted()) return hardDelete(globalConnection);
        else return  softDelete(globalConnection);
    }

    @Override
    public GlobalConnections findGlobalConnectionById(int globalId) {
        GlobalConnections globalConnections = null;
        try {
            Session currentSession = getSession();
            Query query=currentSession.createQuery("FROM GlobalConnections WHERE globalId=:globalId");
            query.setParameter("globalId", globalId);
            globalConnections=(GlobalConnections) query.uniqueResult();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return globalConnections;
    }

    @Override
    public List<GlobalConnections> getAllConnections() {
        List<GlobalConnections> globalConnectionsList = new ArrayList<>();
        try {
            Session currentSession = getSession();
            currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
            org.hibernate.query.Query<GlobalConnections> query =  currentSession.createQuery("from GlobalConnections ");
            query.setCacheable(true);
            globalConnectionsList = query.list();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return globalConnectionsList;
    }


    @Override
    public int addGlobalConnectionSecurity(GlobalConnectionSecurity globalConnectionSecurity) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the global connection security " + globalConnectionSecurity);
            }
            getSession().save(globalConnectionSecurity);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return globalConnectionSecurity.getId();
    }

    @Override
    public void editGlobalConnectionSecurity(GlobalConnectionSecurity globalSecurity) {
        try {
            getSession().update(globalSecurity);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }


    @Override
    public void deleteGlobalConnectionSecurityByGlobalConnectionId(int globalConnectionId, Integer userId, Integer orgId, Integer roleId) {
        try {
            String userVal = userId != null ? "userId.id = :userId" : "userId IS NULL";
            String orgVal = orgId != null ? "orgId.id = :orgId" : "orgId IS NULL";
            String roleVal = roleId != null ? "roleId.id = :roleId" : "roleId IS NULL";
            String hql = "delete GlobalConnectionSecurity where globalConnections.globalId=:id and " + userVal + " and " + orgVal + " and " + roleVal;
            Query updateResourceQuery = (Query) getSession().createQuery(hql);

            updateResourceQuery.setParameter("id", globalConnectionId);
            if (null != userId) {
                updateResourceQuery.setParameter("userId", userId);
            }
            if (null != orgId) {
                updateResourceQuery.setParameter("orgId", orgId);
            }
            if (null != roleId) {
                updateResourceQuery.setParameter("roleId", roleId);
            }
            updateResourceQuery.executeUpdate();
        } catch  (Exception e) {
            logger.error("Exception", e);
        }

    }

    @Override
    public void updateOrInsert(int globalId, Integer userId, Integer orgId, Integer roleId, Integer permission, String createdBy) {
        try {
            String userVal = userId != null ? "userId.id = :userId" : "userId IS NULL";
            String orgVal = orgId != null ? "orgId.id = :orgId" : "orgId IS NULL";
            String roleVal = roleId != null ? "roleId.id = :roleId" : "roleId IS NULL";

            Query updateResourceQuery = (Query) getSession().createQuery("from GlobalConnectionSecurity where globalConnections.globalId=:id and " + userVal + " and " + orgVal + " and " + roleVal + " and permission=:permission");

            updateResourceQuery.setParameter("id", globalId);
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
            List<GlobalConnectionSecurity> list = updateResourceQuery.getResultList();
            if (list.size() > 0) {
                GlobalConnectionSecurity globalConnectionSecurityObj = list.get(0);
                globalConnectionSecurityObj.setPermission(permission);
                globalConnectionSecurityObj.setLastUpdatedTime(new Date());
                globalConnectionSecurityObj.setCreatedBy(createdBy);
                editGlobalConnectionSecurity(globalConnectionSecurityObj);
            } else {
                GlobalConnectionSecurity globalConnectionSecurity1 = new GlobalConnectionSecurity();
                GlobalConnections globalConnectionById = findGlobalConnectionById(globalId);
                globalConnectionSecurity1.setGlobalConnections(globalConnectionById);
                if (null != userId) {
                    User user = userDao.findUser(userId);
                    globalConnectionSecurity1.setUserId(user);
                }

                if (null != orgId) {
                    Organization organization = organizationDao.getOrganization(orgId);
                    globalConnectionSecurity1.setOrgId(organization);
                }

                if (null != roleId) {
                    Role role = roleDao.getRole(roleId);
                    globalConnectionSecurity1.setRoleId(role);
                }
                globalConnectionSecurity1.setLastUpdatedTime(new Date());
                globalConnectionSecurity1.setPermission(permission);
                globalConnectionSecurity1.setCreatedBy(createdBy);
                addGlobalConnectionSecurity(globalConnectionSecurity1);
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public List getAllConnectionOfLoggedInUser(Integer createdBy, List<Integer> sharedGlobalId) {
        try {
            Session currentSession = this.getSession();
            currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
            String queryString = "select globalId as globalId , name as name, type as type, baseType as baseType, createdBy as createdBy ,dsType as dsTypeClass, isMigrated as isMigrated, vendor as vendorName  from GlobalConnections where (createdBy=:createdBy or createdBy is null) ";

            if (!sharedGlobalId.isEmpty()) {
                queryString += " or globalId in (:globalIds)";
            }
            org.hibernate.query.Query<GlobalConnections> query = currentSession.createQuery(queryString,GlobalConnections.class);
            if (!sharedGlobalId.isEmpty()) {
                query.setParameterList("globalIds", sharedGlobalId);
            }
            query.setParameter("createdBy", "" + createdBy);
            query.setResultTransformer(new JsonResultSetTransformer());
           // query.setCacheable(true);
            // query.setResultTransformer(Transformers.aliasToBean(GlobalConnectionDTO.class));
            return query.list();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return new ArrayList<>();
    }

    @Override
    public List<GlobalConnectionSecurity> getAllConnectionsByShared() {
        return null;
    }

    @Override
    public List getAllConnectionsFromShared() {
        List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();
        String userId = AuthenticationUtils.getUserId();
        String organizationId = AuthenticationUtils.getOrganizationId();


        try {
            String userVal = !userId.isEmpty() ? "userId.id = :userId" : "";
            String orgVal = organizationId != null ? " or orgId.id = :orgId" : "";
            String roleVal = !userRolesIds.isEmpty() ? " or roleId.id in  (:roleId) " : "";
            String hql = "select  distinct globalConnections.id as id, max(permission) as maxPermission from GlobalConnectionSecurity where  " + userVal + orgVal + roleVal + "  group by globalConnections.id";


            Session currentSession = this.getSession();
            org.hibernate.query.Query<GlobalConnectionSecurity> query =currentSession.createQuery(hql);
            if (StringUtils.isNotEmpty(organizationId)) {
                query.setParameter("orgId", Integer.valueOf(organizationId));
            }

            query.setParameter("userId", Integer.valueOf(userId));

            List<Integer> roleId = new ArrayList<>();
            if (userRolesIds.size() > 0) {
                for (String role : userRolesIds) {
                    roleId.add(Integer.valueOf(role));
                }
                query.setParameter("roleId", roleId);
            }
            query.setResultTransformer(new JsonResultSetTransformer());
            //query.setCacheable(true);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getAllConnectionsFromSharedIfId(Integer forId) {
        List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();
        String userId = AuthenticationUtils.getUserId();
        String organizationId = AuthenticationUtils.getOrganizationId();


        try {
            String userVal = !userId.isEmpty() ? "userId.id = :userId" : "";
            String orgVal = organizationId != null ? " or orgId.id = :orgId" : "";
            String roleVal = !userRolesIds.isEmpty() ? " or roleId.id in  (:roleId) " : "";
            String globalId = " and globalConnections.id = :forId";
            String hql = "select  distinct globalConnections.id as id, max(permission) as maxPermission from GlobalConnectionSecurity where  (" + userVal + orgVal + roleVal +")"+ globalId + "  group by globalConnections.id";


            Session currentSession = this.getSession();
            org.hibernate.query.Query<Map<String, Object>> query = currentSession.createQuery(hql);
            query.setParameter("forId", forId);
            if (StringUtils.isNotEmpty(organizationId)) {
                query.setParameter("orgId", Integer.valueOf(organizationId));
            }

            query.setParameter("userId", Integer.valueOf(userId));

            List<Integer> roleId = new ArrayList<>();
            if (userRolesIds.size() > 0) {
                for (String role : userRolesIds) {
                    roleId.add(Integer.valueOf(role));
                }
                query.setParameter("roleId", roleId);
            }
            query.setResultTransformer(new JsonResultSetTransformer());
           // query.setCacheable(true);
            List<Map<String, Object>> list = query.getResultList();
            return list.size() > 0 ? list.get(0) : Collections.emptyMap();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getAConnectionById(Integer globalId) {
        try {
            Session currentSession = this.getSession();
            currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
            String queryString = "select globalId as globalId , name as name, type as type, baseType as baseType, createdBy as createdBy ,dsType as dsTypeClass, isMigrated as isMigrated  from GlobalConnections  where  ";


            queryString += "  globalId = :globalId";
            org.hibernate.query.Query<Map<String, Object>> query = currentSession.createQuery(queryString);

            query.setParameter("globalId", globalId);

            query.setResultTransformer(new JsonResultSetTransformer());
            //query.setCacheable(true);
            List list = query.getResultList();

            return list.size() > 0 ? (Map<String, Object>) list.get(0) : Collections.emptyMap();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getTypeByGlobalId(Integer globalId, String className) {
        Session currentSession = this.getSession();
        JsonObject classJson = JsonUtils.getDSTypeClassJson(className);
        String value = classJson.get("name").getAsString();
        String settingQuery = classJson.get("query").getAsString();
        if (StringUtils.isNotEmpty(value)) {
            String queryString = "select  " + settingQuery + " from " + value;
            queryString += " where globalConnections.globalId =:globalId  and visible=:visible";
            org.hibernate.query.Query<Map<String, Object>> query = currentSession.createQuery(queryString);
            query.setParameter("visible", Boolean.TRUE);
            query.setParameter("globalId", globalId);
            query.setResultTransformer(new JsonResultSetTransformer());
           // query.setCacheable(true);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Map<String, Object>) list.get(0);
            }
        }
        return Collections.emptyMap();

    }


    @Override
    public List<GlobalConnections> migratedConnectionsList() {
        try {
            Session currentSession = getSession();
            currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<GlobalConnections> cr=cb.createQuery(GlobalConnections.class);
            Root<GlobalConnections> resource = cr.from(GlobalConnections.class);
            cr.select(resource).where(cb.equal(resource.get("isMigrated"), true));
            return getSession().createQuery(cr).getResultList();
        } catch (Exception e) {
            if(e instanceof NoResultException)
                return null;
            logger.error("Exception", e);
        }
        return null;
    }

    @Override
    public GlobalConnectionSecurity findGCSByGlobalId(int globalId) {
        GlobalConnectionSecurity globalConnectionSecurity = null;
        try {

            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<GlobalConnectionSecurity> cr=cb.createQuery(GlobalConnectionSecurity.class);
            Root<GlobalConnectionSecurity> resource = cr.from(GlobalConnectionSecurity.class);
            cr.select(resource).where(cb.equal(resource.get("globalConnections").get("globalId"), globalId));
            globalConnectionSecurity = getSession().createQuery(cr).uniqueResult();
        } catch (Exception e) {
            if(e instanceof NoResultException)
                return null;
            logger.error("Exception", e);
            e.printStackTrace();
        }
        return globalConnectionSecurity;
    }


    @Override
    public List<GlobalConnectionSecurity> findPermissionByConnectionId(int globalId) {
        List<GlobalConnectionSecurity> globalConnectionSecurityList=null;
        try {
            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<GlobalConnectionSecurity> cr=cb.createQuery(GlobalConnectionSecurity.class);
            Root<GlobalConnectionSecurity> resource = cr.from(GlobalConnectionSecurity.class);
            cr.select(resource).where(cb.equal(resource.get("globalConnections").get("globalId"), globalId));
            globalConnectionSecurityList = getSession().createQuery(cr).getResultList();
            return globalConnectionSecurityList;
        } catch (Exception e) {
            if(e instanceof NoResultException)
                return null;
            e.printStackTrace();
            logger.error("Exception", e);
        }
        return globalConnectionSecurityList;
    }


    @Override
    public int addTomcatConnections(DSTypeTomcat dsTypeTomcat) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the global connection " + dsTypeTomcat);
            }
            getSession().save(dsTypeTomcat);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dsTypeTomcat.getId();
    }

    @Override
    public void editTomcatConnections(DSTypeTomcat tomcatId) {
        try {
            getSession().update(tomcatId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception", e);
        }
    }


    @Override
    public int addHikariConnections(DSTypeHikari dsTypeHikari) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the global connection " + dsTypeHikari);
            }
            getSession().save(dsTypeHikari);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dsTypeHikari.getId();
    }

    @Override
    public void editHIkariConnections(DSTypeHikari dsTypeHikari) {
        try {
            getSession().update(dsTypeHikari);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }


    @Override
    public int addJndiConnections(DSTypeJndi dsTypeJndi) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the global connection " + dsTypeJndi);
            }
            getSession().save(dsTypeJndi);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dsTypeJndi.getId();
    }

    @Override
    public void editJndiConnections(DSTypeJndi dsTyeJndi) {
        try {
            getSession().update(dsTyeJndi);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }


    @Override
    public int addPlainJdbcConnections(DSTypePlainJDBC dsTypePlainJDBC) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the global connection " + dsTypePlainJDBC);
            }
            getSession().save(dsTypePlainJDBC);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dsTypePlainJDBC.getId();
    }

    @Override
    public void editPlainJdbcConnections(DSTypePlainJDBC dsTypePlainJDBC) {
        try {
            getSession().update(dsTypePlainJDBC);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }


    @Override
    public int addNoSqlConnections(DSTypeNoSQL dsTypeNoSQL) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the global connection " + dsTypeNoSQL);
            }
            getSession().save(dsTypeNoSQL);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dsTypeNoSQL.getId();
    }

    @Override
    public void editNoSqlConnections(DSTypeNoSQL dsTypeNoSQL) {
        try {
            getSession().update(dsTypeNoSQL);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public List<DSTypeTomcat> getAllTomcatConnections() {
        List<DSTypeTomcat> tomcatList = new ArrayList<>();
        try {

            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypeTomcat> cr=cb.createQuery(DSTypeTomcat.class);
            tomcatList =getSession().createQuery(cr).getResultList();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return tomcatList;
    }

    @Override
    public List<DSTypeHikari> getAllHikariConnections() {
        List<DSTypeHikari> hikariList = new ArrayList<>();
        try {
            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypeHikari> cr=cb.createQuery(DSTypeHikari.class);
            hikariList =getSession().createQuery(cr).getResultList();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return hikariList;
    }

    @Override
    public List<DSTypeJndi> getAllJndiConnections() {
        List<DSTypeJndi> jndiList = new ArrayList<>();
        try {

            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypeJndi> cr=cb.createQuery(DSTypeJndi.class);
            jndiList = getSession().createQuery(cr).getResultList();

        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return jndiList;
    }

    @Override
    public List<DSTypeNoSQL> getAllNoSqlConnections() {
        List<DSTypeNoSQL> noSqlList = new ArrayList<>();
        try {
            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypeNoSQL> cr=cb.createQuery(DSTypeNoSQL.class);
            noSqlList =getSession().createQuery(cr).getResultList();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return noSqlList;
    }


    @Override
    public DSTypeTomcat getTomcatConnectionById(int id) {
        DSTypeTomcat dsTypeTomcat = null;
        try {

            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypeTomcat> cr=cb.createQuery(DSTypeTomcat.class);
            Root<DSTypeTomcat> resource = cr.from(DSTypeTomcat.class);
            cr.where(cb.equal(resource.get("globalConnections").get("globalId"), id));
            dsTypeTomcat =getSession().createQuery(cr).uniqueResult();
        } catch (Exception e) {
            if(e instanceof NoResultException)
                return null;
            logger.error("Exception", e);
        }
        return dsTypeTomcat;
    }

    @Override
    public DSTypeJndi getJndiConnectionById(int id) {
        DSTypeJndi dsTypeJndi = null;
        try {
            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypeJndi> cr=cb.createQuery(DSTypeJndi.class);
            Root<DSTypeJndi> resource = cr.from(DSTypeJndi.class);
            cr.where(cb.equal(resource.get("globalConnections").get("globalId"), id));
            dsTypeJndi =getSession().createQuery(cr).uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dsTypeJndi;
    }

    @Override
    public DSTypeNoSQL getNoSQLConnectionById(int id) {
        DSTypeNoSQL dsTypeNoSQL = null;
        try {
            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypeNoSQL> cr=cb.createQuery(DSTypeNoSQL.class);
            Root<DSTypeNoSQL> resource = cr.from(DSTypeNoSQL.class);
            cr.where(cb.equal(resource.get("globalConnections").get("globalId"), id));
            dsTypeNoSQL =getSession().createQuery(cr).uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dsTypeNoSQL;
    }

    @Override
    public DSTypeHikari getHikariConnectionById(int id) {
        DSTypeHikari dsTypeHikari = null;
        try {
            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypeHikari> cr=cb.createQuery(DSTypeHikari.class);
            Root<DSTypeHikari> resource = cr.from(DSTypeHikari.class);
            cr.where(cb.equal(resource.get("globalConnections").get("globalId"), id));
            dsTypeHikari =getSession().createQuery(cr).uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dsTypeHikari;
    }

    @Override
    public DSTypePlainJDBC getPlainJDBCConnectionById(int id) {
        DSTypePlainJDBC globalPlainJDBC = null;
        try {

            CriteriaBuilder cb = getSession().getCriteriaBuilder();
            CriteriaQuery<DSTypePlainJDBC> cr=cb.createQuery(DSTypePlainJDBC.class);
            Root<DSTypePlainJDBC> resource = cr.from(DSTypePlainJDBC.class);
            cr.where(cb.equal(resource.get("globalConnections").get("globalId"), id));
            globalPlainJDBC =getSession().createQuery(cr).uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return globalPlainJDBC;
    }

    @Override
    public GlobalConnections getGlobalConnectionBy(Integer id, String name, String type) {
        try {
            String hql = "FROM GlobalConnections where globalId = :globalId and  name =:name and  type =:type";
            Query query = getSession().createQuery(hql);
            query.setParameter("name", name);
            query.setParameter("globalId", id);
            query.setParameter("type", type);
            return (GlobalConnections) query.uniqueResult();
        }
        catch (Exception e) {
            logger.error("Exception",e);
        }
        return null;
    }

    @Override
    public void editTomcatConnections(DSTypeHikari dsTypeHikari) {
        try {
            getSession().update(dsTypeHikari);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception", e);
        }

    }

    @Override
    public void editTomcatConnections(DSTypeJndi dsTypeJndi) {
        try {
            getSession().update(dsTypeJndi);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception", e);
        }

    }

    @Override
    public void editTomcatConnections(DSTypeNoSQL dsTypeNoSQL) {
        try {
            getSession().update(dsTypeNoSQL);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception", e);
        }

    }

    @Override
    public GlobalConnections getDeletedGlobalConnectionById(Integer id) {
        try {
            return (GlobalConnections) getSession().get(GlobalConnections.class, id);
        }
        catch (Exception e) {
            logger.error("Error occurred while fetching Global Connections");
        }
        return null;
    }

    @Override
    public List<GlobalConnections> findConnectionsByCreatedBy(String userId) {

        try {
            Session session = getSession();
            Query query = session.createQuery("FROM GlobalConnections where createdBy = :userId");
            query.setParameter("userId", userId);
            return query.getResultList();
        }
        catch (Exception e) {
            logger.error("Error occurred while fetching GlobalConnections  of user");
            return new ArrayList<>();
        }

    }

    @Override
    public List getTypeByAllGlobalIds(List<Integer> globalIds, Set<String> classNameList, String driver,String vendorName) {
        String driverColumnName;
        List<Map<String, Object>> dsTypes = new ArrayList<>();
        Session currentSession = getSession();
        for (String className : classNameList) {
            JsonObject classJson = JsonUtils.getDSTypeClassJson(className);
            String value = classJson.get("name").getAsString();
            String settingQuery = "driverClassName as driverClassName";
            String dtoClass = classJson.get("dtoClass").getAsString();
            if (dtoClass.contains("Hikari") || dtoClass.contains("Plain")) {
                settingQuery = "driverName as driverName";
            }
            final boolean notEmpty = StringUtils.isNotEmpty(value);
            final boolean notEmptyVendor = StringUtils.isNotEmpty(vendorName);
            if (notEmpty) {
                String vendorQuery="";
                org.hibernate.query.Query query;
                if (driver == null) {
                    query = currentSession.createQuery("select  globalConnections.globalId as globalId,  dataSourceProvider as dataSourceProvider,  "
                            + settingQuery + " from " + value+" where globalConnections.globalId in (:globalIds)  and visible=:visible");
                }else {
                    driverColumnName=!settingQuery.contains("driverClassName")?"driverName=:driver":"driverClassName=:driver";

                    if(notEmptyVendor){
                        vendorQuery= driverColumnName+" and  globalConnections.vendor=:vendorName ";

                    }else{
                        vendorQuery="("
                                +driverColumnName + " and globalConnections.vendor is null )";
                    }
                    String hql = "select  globalConnections.globalId as globalId,  dataSourceProvider as dataSourceProvider,  "
                            + settingQuery + " from " + value+" where globalConnections.globalId in (:globalIds)  and visible=:visible and "+ vendorQuery;
                    query = currentSession.createQuery(hql);
                    query.setParameter("driver", driver);

                }
                if(notEmptyVendor){
                    query.setParameter("vendorName", vendorName);
                }

                query.setParameterList("globalIds", globalIds);
                query.setParameter("visible", Boolean.TRUE);
                query.setResultTransformer(new JsonResultSetTransformer());
                //query.setCacheable(true);
                List list = query.list();
                if (!list.isEmpty()) {
                    dsTypes.addAll(list);
                }
            }

        }

        return dsTypes;
    }

    @Override
    public String getDataSourceProvider(Integer globalId, String tableName) {
        Session session=getSession();
        org.hibernate.query.Query query=session.createQuery("select dataSourceProvider from "+tableName+" WHERE globalConnections.globalId=:globalId AND visible=:visible");
        query.setParameter("globalId", globalId);
        query.setParameter("visible", Boolean.TRUE);
        return (String) query.uniqueResult();
    }

    @Override
    public List<GlobalConnections> getAllRequiredConnections(Set<Integer> connIds) {
        List<GlobalConnections> globalConnectionsList = new ArrayList<>();
        try {
            Session currentSession = getSession();
            currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
            org.hibernate.query.Query query = currentSession.createQuery("from GlobalConnections WHERE globalId in (:connIds)");
            query.setParameterList("connIds", connIds);
            //query.setCacheable(true);
            globalConnectionsList = query.list();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return globalConnectionsList;
    }

    @Override
    public GlobalConnections getGlobalConnectionBy(GlobalDatasourceLookupDTO lookup) {
        try {
            String type = lookup.getDsType();
            StringBuilder hql = new StringBuilder();
            hql.append("SELECT  con.globalConnections FROM ");
            String userName = "username";
            String jdbcUrl = "jdbcUrl";
            boolean isJndi = DataSourceTypeDTO.JNDI.equals(type);
            switch(type) {
                case DataSourceTypeDTO.TOMCAT:
                    hql.append("DSTypeTomcat con JOIN con.globalConnections gcon");
                    jdbcUrl = "url";
                    break;
                case DataSourceTypeDTO.HIKARI:
                    hql.append("DSTypeHikari con JOIN con.globalConnections gcon");
                    userName="userName";
                    break;
                case DataSourceTypeDTO.JNDI:
                    hql.append("DSTypeJndi con  JOIN con.globalConnections gcon");
                    userName="lookUpName";
                    break;
                case DataSourceTypeDTO.NOSQL:
                    hql.append("DSTypeNoSQL con JOIN con.globalConnections gcon");
                    jdbcUrl = "url";
                    break;
                default:

                    break;

            }
            hql.append(" WHERE " +String.join(".","con",userName)+  "=:userName");
            if(!isJndi) {
                hql.append(" and con.password =:password and "+String.join(".","con",jdbcUrl)+"=:jdbcUrl and con.databaseName =:databaseName");
            }
            else {
                hql.append(" and con.lookupName =:lookupName ");
            }

            hql.append(" and gcon.name =:name");

            Session session = getSession();
            Query query = session.createQuery(hql.toString());
            if(!isJndi) {
                query.setParameter("jdbcUrl", lookup.getJdbcUrl());
                query.setParameter("databaseName",lookup.getDbName());
                query.setParameter("password",CipherUtils.encrypt(lookup.getPassword()));
                query.setParameter("userName", lookup.getUserName());
            }
            else {
                query.setParameter("lookupName", lookup.getJdbcUrl());
                query.setParameter("name", "");
            }
            query.setParameter("name", lookup.getName());

            List<GlobalConnections> globalConnections =  query.getResultList();
            if(globalConnections != null && !globalConnections.isEmpty()) {
                return globalConnections.get(0);
            }
        }
        catch (Exception e) {
            if(logger.isErrorEnabled()) {
                logger.error("Error occurred while fetching GlobalConnection. Root cause : {}",e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int deleteGlobalConnectionSecurityById(int globalId) {
        try {
            String hql = "DELETE GlobalConnectionSecurity security where security.globalConnections.globalId=:globalId";
            Query query = getSession().createQuery(hql);
            query.setParameter("globalId", globalId);
            return query.executeUpdate();
        }
        catch (Exception e) {
            logger.error("Error occurred while deleting securities, Root cause : {} ",e.getMessage());
        }
        return 0;
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
    public void addHiHcrGlobalConnection(HIHcrConnectionsGlobal hiHcrConnectionsGlobal) {
        try {
            getSession().save(hiHcrConnectionsGlobal);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public DSExtraOption saveExtraOption(DSExtraOption extraOption) {
        Session session = getSession();
        session.save(extraOption);
        return extraOption;
    }
	//BUG-7630
	@Override
	public DSExtraOption updateExtraOption(DSExtraOption updatedValue) {
		Session session = getSession();
		session.update(updatedValue);
		return updatedValue;
	}

    @Override
    public boolean deleteExtraOptionByGloablId(Integer globalId) {
        Session session = getSession();
        try {
            Query<DSExtraOption> deleteQuery = 	session.createQuery("DELETE From DSExtraOption where globalConnection.globalId =:globalId");
            deleteQuery.setParameter("globalId", globalId);
            deleteQuery.executeUpdate();
        }
        catch (Exception e) {
            logger.error("Error occurred while deleting extra options {}", e);
            return false;
        }
        return true;
    }

    @Override
    public Map<String, String> getExtraOption(Integer globalId) {
        Map<String, String> extraOptions = new HashMap<>();
        Session session = getSession();
        try {
            Query query = session.createQuery("FROM DSExtraOption where  globalConnection.globalId =:globalId");
            query.setParameter("globalId", globalId);

            @SuppressWarnings("unchecked")
            List<DSExtraOption> extraOptionList =   query.list();
            if ( extraOptionList != null && !extraOptionList.isEmpty()) {
                extraOptionList.stream()
                        .forEach(option -> extraOptions.put(option.getKey(), option.getValue()));
            }

        } catch (Exception e) {
            logger.error("Error occurred while fetching extra options. {}",e);
        }

        return extraOptions;
    }
	
		//BUG-7548
	@Override
	public List<DSExtraOption> getExtraOptions(Integer globalID) {
	    List<DSExtraOption> extraDSOptions = new ArrayList<>();
	    Session session = getSession();
	    try {
	        Query query = session.createQuery("FROM DSExtraOption where globalConnection.globalId = :globalId");
	        query.setParameter("globalId", globalID);
	        
	        @SuppressWarnings("unchecked")
	        List<DSExtraOption> extraOptionList = query.list();
	        if (extraOptionList != null && !extraOptionList.isEmpty()) {
	        	extraDSOptions.addAll(extraOptionList);
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while fetching extra options as List. {}", e);
	    } 
	    return extraDSOptions;
	}
	
	
}





