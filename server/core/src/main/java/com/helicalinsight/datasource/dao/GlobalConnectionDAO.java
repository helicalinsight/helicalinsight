package com.helicalinsight.datasource.dao;

import com.helicalinsight.admin.dto.DsTypeDTO;
import com.helicalinsight.admin.dto.GlobalConnectionDTO;
import com.helicalinsight.admin.dto.GlobalDatasourceLookupDTO;
import com.helicalinsight.admin.model.HIHcrConnectionsGlobal;
import com.helicalinsight.datasource.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Helical on 5/18/2021.
 */
public interface GlobalConnectionDAO {
    //GlobalConnections
    int addGlobalConnections(GlobalConnections globalConnections);

    void editGlobalConnections(GlobalConnections globalConnections);

    boolean deleteGlobalConnections(int globalId);

    GlobalConnections findGlobalConnectionById(int globalId);

    List<GlobalConnections> getAllConnections();




    //GlobalConnectionSecurity

    int addGlobalConnectionSecurity(GlobalConnectionSecurity globalConnections);


    void editGlobalConnectionSecurity(GlobalConnectionSecurity globalSecurity);


    GlobalConnectionSecurity findGCSByGlobalId(int globalId);



    //DsType

    int addTomcatConnections(DSTypeTomcat dsTypeTomcat);

    void editTomcatConnections(DSTypeTomcat tomcatId);


    int addHikariConnections(DSTypeHikari dsTypeHikari);

    void editHIkariConnections(DSTypeHikari dsTypeHikari);


    int addJndiConnections(DSTypeJndi dsTypeJndi);

    void editJndiConnections(DSTypeJndi dsTyJndiHikari);


    int addPlainJdbcConnections(DSTypePlainJDBC dsTypePlainJDBC);

    void editPlainJdbcConnections(DSTypePlainJDBC dsTypePlainJDBC);


    int addNoSqlConnections(DSTypeNoSQL dsTypeNoSQL);

    void editNoSqlConnections(DSTypeNoSQL dsTypeNoSQL);


    List<DSTypeTomcat> getAllTomcatConnections();

    List<DSTypeHikari> getAllHikariConnections();

    List<DSTypeJndi> getAllJndiConnections();

    List<DSTypeNoSQL> getAllNoSqlConnections();



    DSTypeTomcat getTomcatConnectionById(int id);

    DSTypeJndi getJndiConnectionById(int id);

    DSTypeNoSQL getNoSQLConnectionById(int id);

    DSTypeHikari getHikariConnectionById(int id);

    DSTypePlainJDBC getPlainJDBCConnectionById(int id);


    public List<GlobalConnectionSecurity> findPermissionByConnectionId(int globalId);

    public void deleteGlobalConnectionSecurityByGlobalConnectionId(int globalId,Integer userId,Integer orgId,Integer roleId);

    void updateOrInsert(int globalId, Integer userId, Integer orgId, Integer roleId,Integer permission,String createdBy);

    List<GlobalConnectionDTO> getAllConnectionOfLoggedInUser(Integer createdBy, List<Integer> secureIds);

    List<GlobalConnectionSecurity> getAllConnectionsByShared();


    List<GlobalConnections> migratedConnectionsList();

    List getTypeByAllGlobalIds(List<Integer> globalIds, Set<String> classNameList,String driver,String vendorName);

    List getAllConnectionsFromShared();


    Map<String,Object> getAllConnectionsFromSharedIfId(Integer forId);

    Map<String, Object> getAConnectionById(Integer globalId);

    Map<String, Object> getTypeByGlobalId(Integer globalId, String className);

	GlobalConnections getGlobalConnectionBy(Integer id, String name, String type);

	void editTomcatConnections(DSTypeHikari dsTypeHikari);

	void editTomcatConnections(DSTypeJndi dsTypeJndi);

	void editTomcatConnections(DSTypeNoSQL dsTypeNoSQL);

	GlobalConnections getDeletedGlobalConnectionById(Integer id);

	List<GlobalConnections> findConnectionsByCreatedBy(String userId);
	
	boolean hardDelete(GlobalConnections globalConnections);
	String getDataSourceProvider(Integer globalId,String tableName);

	List<GlobalConnections> getAllRequiredConnections(Set<Integer> connIds);

	GlobalConnections getGlobalConnectionBy(GlobalDatasourceLookupDTO lookup);

	int deleteGlobalConnectionSecurityById(int globalId);

	DSExtraOption saveExtraOption(DSExtraOption extraOption);

	boolean deleteExtraOptionByGloablId(Integer globalId);

	Map<String, String> getExtraOption(Integer globalId);
    void addHiHcrGlobalConnection(HIHcrConnectionsGlobal hiHcrConnectionsGlobal);
	
	List<DSExtraOption> getExtraOptions(Integer globalID);//BUG-7548
	
	DSExtraOption updateExtraOption(DSExtraOption updatedValue);//BUG-7630


}
