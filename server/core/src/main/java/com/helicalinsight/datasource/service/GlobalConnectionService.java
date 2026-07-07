package com.helicalinsight.datasource.service;

import com.helicalinsight.admin.dto.GlobalConnectionDTO;
import com.helicalinsight.admin.dto.GlobalDatasourceLookupDTO;
import com.helicalinsight.admin.model.HIHcrConnectionsGlobal;
import com.helicalinsight.datasource.model.*;


import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;
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
public interface GlobalConnectionService {

    //GlobalConnections
    int addGlobalConnections(GlobalConnections globalConnections);
    void editGlobalConnections(GlobalConnections globalConnections);

    boolean deleteGlobalConnections(int globalId);

    GlobalConnections findGlobalConnectionById(int globalId);
    GlobalConnections findGlobalConnectionById(int globalId, boolean applyFilter);

    List<GlobalConnections> getAllConnections();
    List<GlobalConnections> findConnectionsByCreatedBy(String userId);

    List getAllConnectionOfLoggedInUser(Integer createdBy, List<Integer> securedIds);

    Map<String, Object> getAConnectionById(Integer globalId);


    //GlobalConnectionSecurity

    //DsType


    int addTomcatConnections(DSTypeTomcat dsTypeTomcat);


    void editTomcatConnections(DSTypeTomcat tomcatId);
    
    


    int addHikariConnections(DSTypeHikari dsTypeHikari);
    void editHikariConnections(DSTypeHikari dsTypeHikari);

    int addJndiConnections(DSTypeJndi dsTypeJndi);
    void editJndiConnections(DSTypeJndi dsTypeJndi);


    int addPlainJdbcConnections(DSTypePlainJDBC dsTypePlainJDBC);
    

    int addNoSqlConnections(DSTypeNoSQL dsTypeNoSQL);
    void editNoSqlConnections(DSTypeNoSQL dsTypeNoSQL);

    List getTypeByAllGlobalIds(List<Integer> globalIds, Set<String> classNameList,String driver,String vendorName);

    Map<String, Object> getTypeByGlobalId(Integer globalId, String classNameList);


    DSTypePlainJDBC getPlainJDBCConnectionById(int id);

    DSTypeHikari getHikariConnectionById(int id);

    DSTypeNoSQL getNoSQLConnectionById(int id);

    DSTypeTomcat getTomcatConnectionById(int id);

    DSTypeJndi getJndiConnectionById(int id);

    public List<GlobalConnectionSecurity> findPermissionByConnectionId(int globalId);

    public void deleteGlobalConnectionSecurityByGlobalConnectionId(int globalId, Integer userId, Integer orgId, Integer roleId);

    public void updateOrInsert(int globalId, Integer userId, Integer orgId, Integer roleId, Integer permission, String createdBy);


    public List<GlobalConnectionSecurity> getAllConnectionsByShared();


    public boolean addDetails(GlobalConnections globalConnections, GlobalConnectionSecurity globalConnectionSecurity, Object dsType, JsonObject formData);

    public Boolean addDataSourceDetails(GlobalConnections connection, Object dsType);

    public Boolean editDataSourceDetails(GlobalConnections connections, Object dsType);

    public List<GlobalConnections> migratedConnectionsList();

    List getAllConnectionsFromShared();

    Map<String, Object> getAllConnectionsFromSharedIfId(Integer id);
    GlobalConnections getGlobalConnectionBy(Integer id ,String name , String type);
    GlobalConnections getGlobalConnectionBy(GlobalDatasourceLookupDTO lookup);


	void addGlobalConnectionSecurity(GlobalConnectionSecurity security);
	void editGlobalConnectionSecurity(GlobalConnectionSecurity security);
	
	GlobalConnections getDeletedGlobalConnectionById(Integer id);
	int deleteGlobalConnectionSecurityById(int globalId);
	String getDataSourceProvider(Integer globalId,String tableName);
	List<GlobalConnections> getAllRequiredConnections(Set<Integer> connIds);


	// DS_EXTRA_OPTION
	DSExtraOption  saveExtraOption(DSExtraOption extraOption);
	Map<String,String> getExtraOption(Integer globalId);
	boolean deleteExtraOptionByGloablId(Integer globalId);
    void addHiHcrGlobalConnection(HIHcrConnectionsGlobal hiHcrConnectionsGlobal);
	List<DSExtraOption> getExtraOptions(Integer globalID);//BUG-7548
	DSExtraOption updateExtraOption(DSExtraOption updatedValue);//BUG-7630



}
