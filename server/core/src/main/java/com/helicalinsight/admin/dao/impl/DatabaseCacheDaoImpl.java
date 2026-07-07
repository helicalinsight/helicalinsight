package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.DatabaseCacheDao;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.efw.utility.ApplicationUtilities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
/**
 * @author Somen
 */
@Repository
public class DatabaseCacheDaoImpl implements DatabaseCacheDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DatabaseCacheDaoImpl.class);

    @Autowired
    private SessionFactory session;
    

    @Override
    public List<String> findUnClickedTables(DataSourceMapping dataSourceMapping, List<String> addList) {
        List<DataSourceMapping> listOfDataSourceMapping;
        List<String> keyList = new ArrayList<>();
        try {
        	List<Predicate> predicates=new ArrayList<>();
        	CriteriaBuilder cb = session.getCurrentSession().getCriteriaBuilder();
        	CriteriaQuery<DataSourceMapping> cr=cb.createQuery(DataSourceMapping.class);
        	Root<DataSourceMapping> resource = cr.from(DataSourceMapping.class);
        	predicates.add(cb.equal(resource.get("connectionId"), dataSourceMapping.getConnectionId()));
        	predicates.add(cb.equal(resource.get("type"), dataSourceMapping.getType()));
        	cr.select(resource).where(predicates.toArray(new Predicate[] {}));
            listOfDataSourceMapping = session.getCurrentSession().createQuery(cr).getResultList();

            if (listOfDataSourceMapping != null) {
                List<String> tableIndexList = new ArrayList<>();
                listOfDataSourceMapping.forEach(
                        item -> {
                            String tableIdIndex = item.getTableIdIndex();
                            if (tableIdIndex != null) {
                                tableIndexList.addAll(Arrays.asList(tableIdIndex.split(",")));
                            }
                        });

                for (String it : addList) {
                    it = "\"" + it + "\"";
                    if (!tableIndexList.contains(it)) {
                        keyList.add(it);
                    }
                }
            }

        } catch (HibernateException e) {
            logger.error("Exception", e);
        }
        return keyList;
    }


    @Override
    public List<ApplicationCache> findPartialCache(DataSourceMapping dataSourceMapping, List<String> addList) {
        List<ApplicationCache> listOfCache = new ArrayList<>();
        String file = dataSourceMapping.getFile();
        boolean ignore = (file != null && file.equals("ignore"));

        List<DataSourceMapping> listOfDataSourceMapping;
        try {
            Session currentSession = session.getCurrentSession();
            List<String> keyList = new ArrayList<>();

            List<Predicate> predicates=new ArrayList<>();
        	CriteriaBuilder cb = session.getCurrentSession().getCriteriaBuilder();
        	CriteriaQuery<DataSourceMapping> cr=cb.createQuery(DataSourceMapping.class);
        	Root<DataSourceMapping> resource = cr.from(DataSourceMapping.class);
        	predicates.add(cb.equal(resource.get("connectionId"), dataSourceMapping.getConnectionId()));
        	predicates.add(cb.equal(resource.get("type"), dataSourceMapping.getType()));
        	cr.select(resource).where(predicates.toArray(new Predicate[] {}));
            listOfDataSourceMapping = session.getCurrentSession().createQuery(cr).getResultList();

            if (listOfDataSourceMapping != null) {
                if (listOfDataSourceMapping.size() == 0) {
                    if (ignore) {
                        return listOfCache;
                    }
                    return null;
                }
                listOfDataSourceMapping.forEach(item -> {
                    String tableIdIndex = item.getTableIdIndex();
                    if (tableIdIndex != null) {
                        List<String> tableIndexList = (List<String>) Arrays.asList(tableIdIndex.split(","));
                        for (String it : addList) {
                            it = "\"" + it + "\"";
                            if (tableIdIndex.contains(it)) {
                                keyList.add(item.getKey());
                            }
                        }
                    }
                });
            }
            predicates.clear();
        	CriteriaQuery<ApplicationCache> cq=cb.createQuery(ApplicationCache.class);
        	Root<ApplicationCache> resource2 = cq.from(ApplicationCache.class);
        	Expression<String> expression=resource2.get("key");
        	predicates.add(expression.in(keyList));
        	predicates.add(cb.equal(resource2.get("resultStatus"), 1));
        	cr.select(resource).where(predicates.toArray(new Predicate[] {}));
        	listOfCache=session.getCurrentSession().createQuery(cq).getResultList();

        } catch (Exception e) {
			if (e instanceof NoResultException)
				return null;
            logger.error("Exception", e);
        }
        return listOfCache;
    }


    @Override
    public List<ApplicationCache> findApplicationCacheByDataSourceMapping(DataSourceMapping dataSourceMapping) {
        List<ApplicationCache> listOfCache = new ArrayList<>();
        String file = dataSourceMapping.getFile();
        boolean ignore = (file != null && file.equals("ignore"));

        List<DataSourceMapping> listOfDataSourceMapping;
        try {
            Session currentSession = session.getCurrentSession();
            String searchType = dataSourceMapping.getType();
            List<String> keyList = new ArrayList<>();

            List<Predicate> predicates=new ArrayList<>();
        	CriteriaBuilder cb = session.getCurrentSession().getCriteriaBuilder();
        	CriteriaQuery<DataSourceMapping> cr=cb.createQuery(DataSourceMapping.class);
        	Root<DataSourceMapping> resource = cr.from(DataSourceMapping.class);
        	predicates.add(cb.equal(resource.get("connectionId"), dataSourceMapping.getConnectionId()));
        	predicates.add(cb.equal(resource.get("type"), searchType));
        	cr.select(resource).where(predicates.toArray(new Predicate[] {}));
        	listOfDataSourceMapping=session.getCurrentSession().createQuery(cr).getResultList();

            if (listOfDataSourceMapping != null) {
                if (listOfDataSourceMapping.size() == 0) {
                    if (ignore) {
                        return listOfCache;
                    }
                    return null;
                }
                listOfDataSourceMapping.forEach(item -> keyList.add(item.getKey()));
            }
            DataSourceMapping resultDataSourceMapping = listOfDataSourceMapping.get(0);
            //Integer totalMatches = resultDataSourceMapping.getMaxPages();
            
            predicates.clear();
        	CriteriaQuery<ApplicationCache> cq=cb.createQuery(ApplicationCache.class);
        	Root<ApplicationCache> resource2 = cq.from(ApplicationCache.class);
        	predicates.add(cb.equal(resource2.get("resultStatus"), "1"));
        	Expression<String> expression=resource2.get("key");
        	predicates.add(expression.in(keyList));
        	cq.select(resource2).where(predicates.toArray(new Predicate[] {}));
        	listOfCache=session.getCurrentSession().createQuery(cq).getResultList();
        	
        } catch (Exception e) {
			if (e instanceof NoResultException)
				return null;
            logger.error("Exception", e);
        }
        return listOfCache;
    }

    @Override
    public String addDatabaseConnection(DataSourceMapping dataSourceMapping) {
        try {
            session.getCurrentSession().save(dataSourceMapping);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return dataSourceMapping.getKey();
    }

    @Override
    public void deleteAllCacheWithId(DataSourceMapping dataSourceMapping) {

        List<DataSourceMapping> listOfDataSourceMapping;
        try {
            Session currentSession = session.getCurrentSession();
            List<String> keyList = new ArrayList<>();
            org.hibernate.query.Query qr =  currentSession.createQuery("from DataSourceMapping  ye WHERE ye.connectionId = :connectionId");
            qr.setParameter("connectionId", dataSourceMapping.getConnectionId());


        	listOfDataSourceMapping =qr.list();
        	
            if (listOfDataSourceMapping != null && listOfDataSourceMapping.size() >= 1) {
                for (DataSourceMapping item : listOfDataSourceMapping) {
                    keyList.add(item.getKey());
                }


                org.hibernate.query.Query q = currentSession.createQuery("DELETE ApplicationCache ye WHERE ye.key IN (:list)");
                q.setParameter("list", keyList);
                q.executeUpdate();

                org.hibernate.query.Query t =  currentSession.createQuery("DELETE DataSourceMapping ye WHERE ye.key IN (:list)");
                t.setParameter("list", keyList);
                t.executeUpdate();
            }
        } catch (HibernateException e) {
            logger.error("Exception", e);
        }
    }

    public List<DataSourceMapping> getAllConnectionEntries() {
        try {
        	CriteriaBuilder cb = session.getCurrentSession().getCriteriaBuilder();
        	CriteriaQuery<DataSourceMapping> cr=cb.createQuery(DataSourceMapping.class);
        	Root<DataSourceMapping> resource = cr.from(DataSourceMapping.class);
        	cr.select(resource);
            return session.getCurrentSession().createQuery(cr).getResultList();

        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("There was an exception ", e);
        }
        return null;
    }

    @Override
    public String connectionStatus(DataSourceMapping dataSourceMapping) {
        String cacheStatus = "connection";
        Session currentSession = session.getCurrentSession();
        List<Predicate> predicates=new ArrayList<>();
    	CriteriaBuilder cb = session.getCurrentSession().getCriteriaBuilder();
    	CriteriaQuery<DataSourceMapping> cr=cb.createQuery(DataSourceMapping.class);
    	Root<DataSourceMapping> resource = cr.from(DataSourceMapping.class);
        addConditionalCriteria(dataSourceMapping,predicates,cb,resource);
        List<DataSourceMapping> listOfDataSourceMapping = session.getCurrentSession().createQuery(cr).getResultList();
        
        predicates.clear();
        predicates.add(cb.equal(resource.get("connectionId"), dataSourceMapping.getConnectionId()));
        predicates.add(cb.equal(resource.get("type"), "table"));        
        addConditionalCriteria(dataSourceMapping,predicates,cb,resource);
        List tableList = session.getCurrentSession().createQuery(cr).getResultList();

        predicates.clear();
        predicates.add(cb.equal(resource.get("connectionId"), dataSourceMapping.getConnectionId()));
        predicates.add(cb.equal(resource.get("type"), "partial_column"));
        addConditionalCriteria(dataSourceMapping,predicates,cb,resource);
        List<DataSourceMapping> partialTableList = session.getCurrentSession().createQuery(cr).getResultList();


        if (partialTableList.size() > 0) {
            Integer expectedCount = 0;
            List<String> cacheKey = new ArrayList<String>();
            JSONArray tableNamesArray = new JSONArray();
            for (DataSourceMapping partialDatasource : partialTableList) {
                int maxPages = partialDatasource.getMaxPages();
                expectedCount += maxPages;
                cacheKey.add(partialDatasource.getKey());
                tableNamesArray.addAll(JSONArray.fromObject("[" + partialDatasource.getTableNameIndex() + "]"));
            }
            
            
            List<ApplicationCache> listOfGenericCache=new ArrayList<>();
//            Criteria applicationCriteria = currentSession.createCriteria(ApplicationCache.class)
//                    .setProjection(Projections.distinct(Projections.projectionList()
//                            .add(Projections.property("key"), "key")))
//                    .setResultTransformer(Transformers.aliasToBean(ApplicationCache.class));
//            List<ApplicationCache> listOfGenericCache = applicationCriteria.add(Restrictions.in("key", cacheKey))
//                    .add(Restrictions.eq("resultStatus", "1"))
//                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
//                    .list();


            Map<String, String> idNameMap = new HashMap<>();
            dataSourceMapping.setType("table");
            List<ApplicationCache> applicationCacheByDataSourceMapping = findApplicationCacheByDataSourceMapping(dataSourceMapping);
            if (applicationCacheByDataSourceMapping != null) {
                for (ApplicationCache a : applicationCacheByDataSourceMapping) {
                    Object deSerialize = ApplicationUtilities.unCompressObject(a);
                    JSONObject tableName = JSONObject.fromObject(deSerialize.toString());
                    //todo check for status 1 only
                    JSONArray jsonArray = tableName.getJSONObject("response").getJSONObject("metadata").getJSONArray("catalogs").getJSONObject(0).getJSONArray("schemas").getJSONObject(0).getJSONArray("tables");
                    for (Object o : jsonArray) {
                        JSONObject obj = (JSONObject) o;
                        idNameMap.put(obj.getString("id"), obj.getString("name"));
                    }

                }
            }


            if (listOfGenericCache.size() == expectedCount && idNameMap.size() == tableNamesArray.size()) {
                return "full";
            } else {
                return "partial";
            }


            //check if it is full

        } else if (listOfDataSourceMapping.size() == 1 && tableList.size() >= 1) {
            return "datasource";
        }


//        Criteria cr = currentSession.createCriteria(DataSourceMapping.class)
//                .setProjection(Projections.distinct(Projections.projectionList()
//                        .add(Projections.property("connectionId"), "connectionId")
//                        .add(Projections.property("connectionName"), "connectionName")
//                        .add(Projections.property("dir"), "dir")))
//                .setResultTransformer(Transformers.aliasToBean(DataSourceMapping.class));

        //      return cr.list();
//
        return cacheStatus;


    }

    private void addConditionalCriteria(DataSourceMapping dataSourceMapping,List<Predicate> predicates,
    		CriteriaBuilder cb,Root<DataSourceMapping> resource) {
    	
    	String dir = dataSourceMapping.getDir();
        String schema = dataSourceMapping.getSchema();
        String catalog = dataSourceMapping.getCatalog();    	
        if (StringUtils.isNotBlank(dir)) {
            predicates.add(cb.equal(resource.get("dir"), dir));
        }
        if (StringUtils.isNotBlank(catalog)) {
            predicates.add(cb.equal(resource.get("catalog"), catalog));
        }
        if (StringUtils.isNotBlank(schema)) {
            predicates.add(cb.equal(resource.get("schema"), schema));
        }
	}


	@Override
    public boolean isDatabaseCachedFully(DataSourceMapping dataSourceMapping) {
        try {
            Session currentSession = session.getCurrentSession();
            List<String> keyList = new ArrayList<>();
            long matchingRecords = 0l;
            List<Predicate> predicates=new ArrayList<>();

            CriteriaBuilder cb = session.getCurrentSession().getCriteriaBuilder();
        	CriteriaQuery<DataSourceMapping> cr=cb.createQuery(DataSourceMapping.class);
        	Root<DataSourceMapping> resource = cr.from(DataSourceMapping.class);
        	predicates.add(cb.equal(resource.get("connectionId"), dataSourceMapping.getConnectionId()));
        	addConditionalCriteria(dataSourceMapping,predicates,cb,resource);
        	cr.select(resource);
            List<DataSourceMapping> listOfDataSourceMapping = session.getCurrentSession().createQuery(cr).getResultList();

            int subCount = 0;
            String[] types = {"catschema", "table"};
            List<String> type = Arrays.asList(types);
            if (listOfDataSourceMapping != null && listOfDataSourceMapping.size() >= 1) {
                for (DataSourceMapping item : listOfDataSourceMapping) {
                    keyList.add(item.getKey());
                    matchingRecords += item.getMaxPages();
                    if (type.contains(item.getType())) {
                        subCount++;
                    }
                }
            }
            if (subCount < 2) {
                return false;
            }
            return true;
        } catch (HibernateException e) {
            logger.error("Exception", e);
        }
        return false;
    }

    @Override
    public Boolean findMappingKey(String key) {
    	
    	SelectionQuery<DataSourceMapping> query =   session.getCurrentSession().createSelectionQuery("from DataSourceMapping t where t.key = :key",DataSourceMapping.class);
        query.setParameter("key", key);
        try {
            return query.uniqueResult() != null;
        }catch (Exception e){
            return  query.list()!=null;
        }
    }

    @Override
    public String findKeyDataSourceMapping(DataSourceMapping dataSourceMapping) {

        try {
            Session currentSession = session.getCurrentSession();
            String searchType = dataSourceMapping.getType();

            List<Predicate> predicates=new ArrayList<>();

            CriteriaBuilder cb = session.getCurrentSession().getCriteriaBuilder();
        	CriteriaQuery<DataSourceMapping> cr=cb.createQuery(DataSourceMapping.class);
        	Root<DataSourceMapping> resource = cr.from(DataSourceMapping.class);
        	predicates.add(cb.equal(resource.get("connectionId"), dataSourceMapping.getConnectionId()));
        	predicates.add(cb.equal(resource.get("type"), searchType));
        	addConditionalCriteria(dataSourceMapping,predicates,cb,resource);
        	cr.select(resource);
            List<DataSourceMapping> listOfDataSourceMapping = session.getCurrentSession().createQuery(cr).getResultList();
            
            if (listOfDataSourceMapping != null && !listOfDataSourceMapping.isEmpty()) {
                String key = listOfDataSourceMapping.get(0).getKey();
                return key;
            }

        } catch (HibernateException e) {
            logger.error("Exception", e);
        }
        return null;

    }

    public Boolean isAConnectionCached(DataSourceMapping dataSourceMapping) {

        try {
            Session currentSession = session.getCurrentSession();

            List<String> keyList = new ArrayList<>();
            Integer noOfExpectedRecords = 0;
            
            CriteriaBuilder cb = session.getCurrentSession().getCriteriaBuilder();
        	CriteriaQuery<DataSourceMapping> cr=cb.createQuery(DataSourceMapping.class);
        	Root<DataSourceMapping> resource = cr.from(DataSourceMapping.class);
        	cr.select(resource).where(cb.equal(resource.get("connectionId"), dataSourceMapping.getConnectionId()));
            List<DataSourceMapping> listOfDataSourceMapping = session.getCurrentSession().createQuery(cr).getResultList();

            for (DataSourceMapping dsMapping : listOfDataSourceMapping) {
                keyList.add(dsMapping.getKey());
                noOfExpectedRecords += dsMapping.getMaxPages();
            }
            if (!keyList.isEmpty()) {
            	
            	CriteriaQuery<ApplicationCache> cq=cb.createQuery(ApplicationCache.class);
            	Root<ApplicationCache> resource2 = cq.from(ApplicationCache.class);
            	Expression<String> expression=resource2.get("key");
            	cr.select(resource).where(cb.equal(resource2.get("resultStatus"),"1"),expression.in(keyList));
                List list = session.getCurrentSession().createQuery(cq).getResultList();

                return list.size() == noOfExpectedRecords;
            } else {
                return false;
            }

        } catch (HibernateException e) {
            logger.error("Exception", e);
        }
        return false;
    }


}
