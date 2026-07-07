package com.helicalinsight.datasource.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.helicalinsight.admin.dto.EFWDConnGroovyDTO;
import com.helicalinsight.admin.dto.EFWDConnSqlJDBCDTO;
import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.dto.EfwdDataSourceLookupDTO;
import com.helicalinsight.admin.dto.HIEfwdConnSecurityDTO;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIEfwdConnSecurity;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIHcrConnectionsEfwd;
import com.helicalinsight.admin.model.HIHcrQueryParameters;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HiHcrQuery;

public interface EFWDConnectionService {

	// save
	HIEFWD saveHIResourceEFWD(HIEFWD hiefwd, String directory);
	HIEfwdConnection saveEFWDConnection(HIEfwdConnection hiEfwdConnection);
	EFWDConnSqlJDBC save(EFWDConnSqlJDBC efwdConnSqlJDBC);
	EFWDConnGroovy save(EFWDConnGroovy efwdConnSqlJDBC);

	// update
	EFWDConnSqlJDBC editEFWDConnection(EFWDConnSqlJDBC connection);
	EFWDConnGroovy editEFWDConnection(EFWDConnGroovy connection);
	Integer edit(HIEfwdConnection connection);

	// delete
	Integer deleteEFConnectionById(Integer connectionId);
	Integer harddeleteEFConnectionById(Integer connectionId);
	Integer deleteConnectionSecuritiesById(int connectionId);
	void restoreConnection(Integer connectionId);
	// list
	List<HIEfwdConnection> findAllFEWDConnection();
	List<PlainConnDTO> findAllFEWDConnectionByType(String type);

	// read
	EFWDConnSqlJDBC findConnectionByID(Integer connectionId);
	EFWDConnSqlJDBCDTO findSqlConnectionByID(Integer connectionId);
	EFWDConnGroovyDTO findGroovyConnectionById(Integer connectionId);
	List<EFWDConnSqlJDBCDTO> findConnectionByParentId(int parentId);
	List<EFWDConnGroovyDTO> findGroovyByParentId(int parentId);
	
	
	EFWDConnGroovy findGroovyByEFWDConnectionId(Integer connectionId);
	HIEfwdConnection findConnectionByIDAndType(Integer connectionId, String type);
	HIEfwdConnection findConnectionByConnectionIdAndType(String connectionId,String type);
	HIEfwdConnection findConnectionByLookup(EfwdDataSourceLookupDTO lookup);
	List<EfwdConnDTO> findConnectionByResourceId(int resourceId,Boolean isRequiredForRecycleBinFetch,Boolean isRequiredToCheckTopLevelRootDeletion);
	List<EfwdConnDTO> findConnectionByResourceIds(List<Integer> resourceId,Boolean isRequiredForRecycleBinFetch,Boolean isRequiredToCheckTopLevelRootDeletion);
	//TODO : Deprecate these 2
	HIEfwdConnection findConnectionById(String connectionId);
	HIEfwdConnection findConnectionById(String connectionId, boolean applyFilter);
	
	EfwdConnDTO getConnectionById(String connectionId);
	PlainConnDTO findPlainConnection(Integer connectionId);
	PlainConnDTO findGroovyConnection(Integer connectionId);
	
	// security
	void updateOrInsert(Integer connectionId ,Integer userId , Integer roleId ,Integer orgId , Integer permissionLevel);
	void deleteEFConnectionecurityByConnectionId(Integer connectionId, Integer userId , Integer roleId, Integer orgId);
	HIEfwdConnSecurity findEfConnectionSecurityByConnectionId(Integer connectionId);
	List<HIEfwdConnSecurity> findEFWDSecurityByConnectionId(Integer connectionId);
	List<HIEfwdConnSecurityDTO> findEFWDConnectionSecurityByConnectionId(Integer connectionId);
	List<HIEfwdConnSecurity> getAllConnectionsFromShared(String type);
	List<EfwdConnDTO> getAllConnectionOfLoggedInUser(Integer createdBy, List<Integer> collect,String type);
	List<String> getTypeByAllEfwdIds(List<Integer> efwdIdList, Set<String> efwdTypeList);
	List<HIEfwdConnection> findAllRequiredEFWDConnection(Set<Integer> connIds);
	Boolean isDeleted(String id);
	HIRecycleBin getHiRecycleBinIdOfSoftDeletedItemByEfwdConnId(Integer connId);
	void addHiHcrConnection(HIHcrConnections hiHcrConnections);
	void addHiHcrEfwdConnection(HIHcrConnectionsEfwd hiHcrConnectionsEfwd);
	void addHiHcrQuery(HiHcrQuery hcrQuery);
	void addHiHcrQueryParams(HIHcrQueryParameters hiHcrQueryParameters);
	void deleteHiHcrConnection(HIHcrConnections hiHcrConnections);

	List<HIHcrConnections> fetchAllHcrConnectionsByResourceId(Integer hcrResId);
	HIHcrConnections fetchHIHcrConnectionsById(Integer hcrResId);
	
	List<PlainConnDTO> fetchSqlConnections(List<Integer> connectionIds);
	List<PlainConnDTO> fetchGroovyConnections(List<Integer> connectionIds);
	
	
}
