package com.helicalinsight.datasource.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.dto.EfwdDataSourceLookupDTO;
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

public interface EFWDConnectionDAO {

	// save
	HIEFWD saveHIResourceEFWD(HIEFWD hiefwd);
	HIEfwdConnection saveEFWDConnection(HIEfwdConnection hiEfwdConnection);
	EFWDConnSqlJDBC save(EFWDConnSqlJDBC efwdConnSqlJDBC);
	EFWDConnGroovy save(EFWDConnGroovy efwdConnSqlJDBC);

	// update
	EFWDConnSqlJDBC edit(EFWDConnSqlJDBC efwdConnSqlJDBC);
	EFWDConnGroovy edit(EFWDConnGroovy groovyConnection);

	List<EfwdConnDTO> findConnectionByResourceIds(List<Integer> resourceIds, Boolean isRequiredForRecycleBinFetch, Boolean isRequiredToCheckTopLevelRootDeletion);

	Integer edit(HIEfwdConnection connection);
	
	// delete
	Integer deleteEFConnectionById(Integer connectionId);
	Integer hardDeleteEFConnectionById(Integer connectionId);
	
	// list
	List<HIEfwdConnection> findAllFEWDConnection();
	List<Integer> findAllFEWDConnections(String type);
	
	// read
	EFWDConnSqlJDBC findSQLJDBCConnectionById(Integer connectionId);
	EFWDConnGroovy  findGroovyByEFWDConnectionId(Integer connectionId);
	EFWDConnGroovy findGroovyConnectionByIdAndType(Integer connectionId, String type);
	EFWDConnSqlJDBC findSQLJDBCConnectionByIdAndType(Integer connectionId, String type);
	PlainConnDTO findPlainConnection(Integer connectionId);
	PlainConnDTO findGroovyConnection(Integer connectionId);
	List<EFWDConnSqlJDBC> findConnectionByParentId(int parentId);
	List<EfwdConnDTO> findConnectionByResourceId(int resourceId,Boolean isRequiredForRecycleBinFetch,Boolean isRequiredToCheckTopLevelRootDeletion);
	
	//security
	void insertOrUpdate(HIEfwdConnSecurity security);
	void deleteEFConnectionecurityByConnectionId(Integer connectionId,Integer userId,Integer roleId,Integer orgId);
	Boolean deleteEFConnectionecurityByConnectionId(Integer connectionId);
	HIEfwdConnSecurity findEfConnectionSecurityByConnectionId(Integer connectionId);
	List<HIEfwdConnSecurity> findEFWDSecurityByConnectionId(Integer connectionId);
	List<EFWDConnGroovy> findGroovyConnectionByParentId(int parentId);
	List<HIEfwdConnSecurity> getAllConnectionsFromShared(String type);
	List<EfwdConnDTO> getAllConnectionOfLoggedInUser(Integer createdBy, List<Integer> collect, String type);
	HIEfwdConnection findConnectionByConnectionIdAndType(String connectionId, String type);
	List<HIEfwdConnection> findAllRequiredEFWDConnection(Set<Integer> connIds);
	Optional<HIEfwdConnection> findConnectionById(String connectionId, boolean applyFilter);
	Boolean isDeleted(String id);
	HIRecycleBin getHiRecycleBinIdOfSoftDeletedItemByEfwdConnId(Integer connId);
	Integer deleteConnectionSecuritiesById(int connectionId);
	HIEfwdConnection findConnectionByLookup(EfwdDataSourceLookupDTO lookup);
	void addHiHcrConnection(HIHcrConnections hiHcrConnections);
	void addHiHcrEfwdConnection(HIHcrConnectionsEfwd hiHcrConnectionsEfwd);
	void addHiHcrQuery(HiHcrQuery hcrQuery);
	void addHiHcrQueryParams(HIHcrQueryParameters hiHcrQueryParameters);
	void deleteHiHcrConnection(HIHcrConnections hiHcrConnections);
	List<HIHcrConnections> fetchAllHcrConnectionsByResourceId(Integer hcrResId);

	HIHcrConnections fetchHIHcrConnectionsById(Integer hcrResId);

	List<PlainConnDTO> fetchSqlConnections(List<Integer> connectionIds);

	List<PlainConnDTO> fetchGroovyConnections(List<Integer> connectionIds);
	void restoreConnection(Integer connectionId);
}
