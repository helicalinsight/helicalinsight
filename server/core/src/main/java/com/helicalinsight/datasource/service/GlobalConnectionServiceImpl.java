package com.helicalinsight.datasource.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.helicalinsight.admin.dto.GlobalConnectionDTO;
import com.helicalinsight.admin.dto.GlobalDatasourceLookupDTO;
import com.helicalinsight.admin.model.HIHcrConnectionsGlobal;
import com.helicalinsight.datasource.dao.GlobalConnectionDAO;
import com.helicalinsight.datasource.model.*;
import com.helicalinsight.efw.exceptions.DataSourceConnectionNotFoundException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.dao.GlobalConnectionDAO;
import com.helicalinsight.datasource.model.DSTypeHikari;
import com.helicalinsight.datasource.model.DSTypeJndi;
import com.helicalinsight.datasource.model.DSTypeNoSQL;
import com.helicalinsight.datasource.model.DSTypePlainJDBC;
import com.helicalinsight.datasource.model.DSTypeTomcat;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;
import com.helicalinsight.datasource.model.GlobalConnections;

/**
 * Created by Helical on 5/18/2021.
 */
@Service
public class GlobalConnectionServiceImpl implements GlobalConnectionService {
    private static final Logger logger = LoggerFactory.getLogger(GlobalConnectionServiceImpl.class);

    @Autowired
    GlobalConnectionDAO globalConnectionDAO;

    @Override
    @Transactional
    public int addGlobalConnections(GlobalConnections globalConnections) {

        int i=0;
        try {
            i=globalConnectionDAO.addGlobalConnections(globalConnections);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return i;
    }


    @Override
    @Transactional
    public boolean deleteGlobalConnections(int globalId) {
        return globalConnectionDAO.deleteGlobalConnections(globalId);
    }
    

    @Override
    @Transactional
    public GlobalConnections findGlobalConnectionById(int globalId) {
            GlobalConnections globalConnections = globalConnectionDAO.findGlobalConnectionById(globalId);
            if(globalConnections != null &&  globalConnections.isDeleted()) {
            	throw new DataSourceConnectionNotFoundException("Datasource not found");
            }
        return globalConnections;
    }
    @Override
    @Transactional
    public GlobalConnections findGlobalConnectionById(int globalId,boolean applyFilter) {
            GlobalConnections globalConnections = globalConnectionDAO.findGlobalConnectionById(globalId);
            if(applyFilter && globalConnections != null &&  globalConnections.isDeleted()) {
            	throw new DataSourceConnectionNotFoundException("Datasource not found");
            }
        return globalConnections;
    }



    @Override
    @Transactional
    public List<GlobalConnections> getAllConnections() {
            return globalConnectionDAO.getAllConnections();
    }

    @Override
    @Transactional
    public List getAllConnectionOfLoggedInUser(Integer createdBy, List<Integer> securedIds) {
        return globalConnectionDAO.getAllConnectionOfLoggedInUser(createdBy,securedIds);
    }
    @Override
    @Transactional
    public Map<String,Object> getAConnectionById(Integer globalId) {
        return globalConnectionDAO.getAConnectionById(globalId);
    }


    @Override
    @Transactional
    public int addTomcatConnections(DSTypeTomcat dsTypeTomcat) {
        return globalConnectionDAO.addTomcatConnections(dsTypeTomcat);
    }

    @Override
    @Transactional
    public void editTomcatConnections(DSTypeTomcat tomcatId) {
        globalConnectionDAO.editTomcatConnections(tomcatId);
    }

    @Override
    @Transactional
    public int addHikariConnections(DSTypeHikari dsTypeHikari) {
        return globalConnectionDAO.addHikariConnections(dsTypeHikari);
    }

    @Override
    @Transactional
    public int addJndiConnections(DSTypeJndi dsTypeJndi) {
        return globalConnectionDAO.addJndiConnections(dsTypeJndi);
    }

    @Override
    @Transactional
    public int addPlainJdbcConnections(DSTypePlainJDBC dsTypePlainJDBC) {
        return globalConnectionDAO.addPlainJdbcConnections(dsTypePlainJDBC);
    }

    @Override
    @Transactional
    public int addNoSqlConnections(DSTypeNoSQL dsTypeNoSQL) {
        return globalConnectionDAO.addNoSqlConnections(dsTypeNoSQL);
    }
    @Transactional
    @Override
    public  List getTypeByAllGlobalIds(List<Integer> globalIds, Set<String> classNameList,String driver,String vendorName){
            return globalConnectionDAO.getTypeByAllGlobalIds(globalIds,classNameList,driver,vendorName);

    }
    @Transactional
    @Override
    public Map<String, Object> getTypeByGlobalId(Integer globalId, String className){
        return globalConnectionDAO.getTypeByGlobalId(globalId,className);

    }


    @Override
    @Transactional
    public DSTypePlainJDBC getPlainJDBCConnectionById(int id) {
        return globalConnectionDAO.getPlainJDBCConnectionById(id);
    }

    @Override
    @Transactional
    public DSTypeHikari getHikariConnectionById(int id) {
        return globalConnectionDAO.getHikariConnectionById(id);
    }

    @Override
    @Transactional
    public DSTypeNoSQL getNoSQLConnectionById(int id) {
        return globalConnectionDAO.getNoSQLConnectionById(id);
    }

    @Override
    @Transactional
    public DSTypeTomcat getTomcatConnectionById(int id) {
        return globalConnectionDAO.getTomcatConnectionById(id);
    }

    @Override
    @Transactional
    public DSTypeJndi getJndiConnectionById(int id) {
        return globalConnectionDAO.getJndiConnectionById(id);
    }

    @Override
    @Transactional
    public List<GlobalConnectionSecurity> findPermissionByConnectionId(int globalId) {
        return globalConnectionDAO.findPermissionByConnectionId(globalId);
    }

    @Override
    @Transactional
    public void deleteGlobalConnectionSecurityByGlobalConnectionId(int globalId,Integer userId,Integer orgId,Integer roleId) {
        globalConnectionDAO.deleteGlobalConnectionSecurityByGlobalConnectionId(globalId, userId, orgId, roleId);
    }

    @Override
    @Transactional
    public void updateOrInsert(int globalId, Integer userId, Integer orgId, Integer roleId, Integer permission,String createdBy) {
        globalConnectionDAO.updateOrInsert(globalId,userId,orgId,roleId,permission,createdBy);
    }

    @Override
    @Transactional
    public List<GlobalConnectionSecurity> getAllConnectionsByShared() {
        return globalConnectionDAO.getAllConnectionsByShared();
    }


    @Override
    @Transactional
    public boolean addDetails(GlobalConnections globalConnections, GlobalConnectionSecurity globalConnectionSecurity, Object dsType,JsonObject formData) {
        Integer globalConnectionId = globalConnectionDAO.addGlobalConnections(globalConnections);
        if(null!=globalConnectionSecurity){
            Integer globalConnectionSecurityId = globalConnectionDAO.addGlobalConnectionSecurity(globalConnectionSecurity);
        }
        Integer dsTypeId=0;
        if(dsType instanceof DSTypePlainJDBC){

            dsTypeId=globalConnectionDAO.addPlainJdbcConnections((DSTypePlainJDBC)dsType);
        }else if(dsType instanceof DSTypeTomcat){

            dsTypeId=globalConnectionDAO.addTomcatConnections((DSTypeTomcat)dsType);
        }else if(dsType instanceof DSTypeHikari){

            dsTypeId=globalConnectionDAO.addHikariConnections((DSTypeHikari)dsType);
        }else if(dsType instanceof DSTypeJndi){

            dsTypeId=globalConnectionDAO.addJndiConnections((DSTypeJndi)dsType);
        }else if(dsType instanceof DSTypeNoSQL){

            dsTypeId=globalConnectionDAO.addNoSqlConnections((DSTypeNoSQL)dsType);
        }
        return false;
    }


    @Override
    @Transactional
    public Boolean addDataSourceDetails(GlobalConnections connection, Object dsType) {
        Integer i = globalConnectionDAO.addGlobalConnections(connection);
        Integer dsTypeId=0;
        if(dsType instanceof DSTypePlainJDBC){
            ((DSTypePlainJDBC) dsType).setGlobalConnections(connection);
            dsTypeId=globalConnectionDAO.addPlainJdbcConnections((DSTypePlainJDBC)dsType);
        }else if(dsType instanceof DSTypeTomcat){
            ((DSTypeTomcat) dsType).setGlobalConnections(connection);
            ((DSTypeTomcat) dsType).setDataSourcePoolId("tomcat_" + i);
            dsTypeId=globalConnectionDAO.addTomcatConnections((DSTypeTomcat)dsType);
        }else if(dsType instanceof DSTypeHikari){
            String dataSourcePoolId = ((DSTypeHikari) dsType).getDataSourcePoolId();
            String dataSourcePoolReplaced = dataSourcePoolId.replaceAll("replace", "" + i);
            String poolName = ((DSTypeHikari) dsType).getPoolName();
            poolName = poolName.replace("replace", "" + i);
            ((DSTypeHikari) dsType).setDataSourcePoolId(dataSourcePoolReplaced);
            ((DSTypeHikari) dsType).setPoolName(poolName);
            ((DSTypeHikari) dsType).setGlobalConnections(connection);
            dsTypeId=globalConnectionDAO.addHikariConnections((DSTypeHikari)dsType);
        }else if(dsType instanceof DSTypeJndi){
            ((DSTypeJndi) dsType).setGlobalConnections(connection);
            dsTypeId=globalConnectionDAO.addJndiConnections((DSTypeJndi)dsType);
        }else if(dsType instanceof DSTypeNoSQL){
            String dataSourcePoolId = ((DSTypeNoSQL) dsType).getDataSourcePoolId();
            if(!StringUtils.isEmpty(dataSourcePoolId)){
                dataSourcePoolId=dataSourcePoolId.replace("replace",""+i);
            }
            ((DSTypeNoSQL) dsType).setDataSourcePoolId(dataSourcePoolId);
            ((DSTypeNoSQL) dsType).setGlobalConnections(connection);
            dsTypeId=globalConnectionDAO.addNoSqlConnections((DSTypeNoSQL)dsType);
        }

        return i != null;
    }

    @Override
    @Transactional
    public Boolean editDataSourceDetails(GlobalConnections connections, Object dsType) {
        try{
            globalConnectionDAO.editGlobalConnections(connections);
            Integer dsTypeId=0;
            if(dsType instanceof DSTypePlainJDBC){
                globalConnectionDAO.editPlainJdbcConnections((DSTypePlainJDBC)dsType);
            }else if(dsType instanceof DSTypeTomcat){
                globalConnectionDAO.editTomcatConnections((DSTypeTomcat)dsType);
            }else if(dsType instanceof DSTypeHikari){
                globalConnectionDAO.editHIkariConnections((DSTypeHikari) dsType);
            }else if(dsType instanceof DSTypeJndi){
                globalConnectionDAO.editJndiConnections((DSTypeJndi)dsType);
            }else if(dsType instanceof DSTypeNoSQL){
                globalConnectionDAO.editNoSqlConnections((DSTypeNoSQL)dsType);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public List<GlobalConnections> migratedConnectionsList() {
        return globalConnectionDAO.migratedConnectionsList();
    }

    @Override
    @Transactional
    public List getAllConnectionsFromShared() {
        return  globalConnectionDAO.getAllConnectionsFromShared();
    } @Override

    @Transactional
    public Map<String,Object> getAllConnectionsFromSharedIfId(Integer forId) {
        return  globalConnectionDAO.getAllConnectionsFromSharedIfId(forId);
    }

    @Transactional
	@Override
	public GlobalConnections getGlobalConnectionBy(Integer id, String name, String type) {
		
		return globalConnectionDAO.getGlobalConnectionBy(id,name,type);
	}

    @Transactional
	@Override
	public void editHikariConnections(DSTypeHikari dsTypeHikari) {
		 globalConnectionDAO.editTomcatConnections(dsTypeHikari);
		
	}

	@Transactional
	@Override
	public void editJndiConnections(DSTypeJndi dsTypeJndi) {
		 globalConnectionDAO.editTomcatConnections(dsTypeJndi);
		
	}

	@Transactional
	@Override
	public void editNoSqlConnections(DSTypeNoSQL dsTypeNoSQL) {
		 globalConnectionDAO.editTomcatConnections(dsTypeNoSQL);
		
	}

	@Transactional
	@Override
	public void addGlobalConnectionSecurity(GlobalConnectionSecurity security) {
		globalConnectionDAO.addGlobalConnectionSecurity(security);
	}

	@Transactional
	@Override
	public void editGlobalConnectionSecurity(GlobalConnectionSecurity security) {
		globalConnectionDAO.editGlobalConnectionSecurity(security);
		
	}


	@Transactional
	@Override
	public GlobalConnections getDeletedGlobalConnectionById(Integer id) {
		return globalConnectionDAO.getDeletedGlobalConnectionById(id);
	}

	@Transactional
	@Override
	public void editGlobalConnections(GlobalConnections globalConnections) {
		 globalConnectionDAO.editGlobalConnections(globalConnections);
	}

	@Transactional
	@Override
	public List<GlobalConnections> findConnectionsByCreatedBy(String userId) {
		return globalConnectionDAO.findConnectionsByCreatedBy(userId);
	}

	@Override
	@Transactional
	public String getDataSourceProvider(Integer globalId, String tableName) {
		return globalConnectionDAO.getDataSourceProvider(globalId, tableName);
	}


	@Override
	@Transactional
	public List<GlobalConnections> getAllRequiredConnections(Set<Integer> connIds) {
		return globalConnectionDAO.getAllRequiredConnections(connIds);
	}

	@Transactional
	@Override
	public GlobalConnections getGlobalConnectionBy(GlobalDatasourceLookupDTO lookup) {
		return globalConnectionDAO.getGlobalConnectionBy(lookup);
	}

	@Transactional
	@Override
	public int deleteGlobalConnectionSecurityById(int globalId) {
		return globalConnectionDAO.deleteGlobalConnectionSecurityById(globalId);
	}

	@Transactional
	@Override
	public DSExtraOption saveExtraOption(DSExtraOption extraOption) {
		return globalConnectionDAO.saveExtraOption(extraOption);
	}
	//BUG-7630
	@Transactional
	@Override
	public DSExtraOption updateExtraOption(DSExtraOption updatedValue) {
		return globalConnectionDAO.updateExtraOption(updatedValue);
	}

	@Transactional
	@Override
	public Map<String,String> getExtraOption(Integer globalId) {
		return globalConnectionDAO.getExtraOption(globalId);
	}

	@Transactional
	@Override
	public boolean deleteExtraOptionByGloablId(Integer globalId) {
		return globalConnectionDAO.deleteExtraOptionByGloablId(globalId);
	}


    @Transactional
    @Override
    public void addHiHcrGlobalConnection(HIHcrConnectionsGlobal hiHcrConnectionsGlobal) {
        globalConnectionDAO.addHiHcrGlobalConnection(hiHcrConnectionsGlobal);
    }
	
	//BUG-7548
	@Transactional
	@Override
	public List<DSExtraOption> getExtraOptions(Integer globalID) {
	    return globalConnectionDAO.getExtraOptions(globalID);
	}

	
}
