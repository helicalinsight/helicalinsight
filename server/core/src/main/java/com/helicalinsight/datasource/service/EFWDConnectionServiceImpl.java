package com.helicalinsight.datasource.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.helicalinsight.admin.dao.OrganizationDao;
import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.dao.UserDao;
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
import com.helicalinsight.admin.model.HIEfwdDataMap;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIHcrConnectionsEfwd;
import com.helicalinsight.admin.model.HIHcrQueryParameters;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HiHcrQuery;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.datasource.dao.EFWDConnectionDAO;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;

@Service
public class EFWDConnectionServiceImpl implements EFWDConnectionService {

    @Autowired
    private EFWDConnectionDAO efwdConnectionDAO;

    @Autowired
    private HIResourceServiceDB hiServiceDB;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private OrganizationDao orgDao;
    
    @Autowired
    private ResourceDTOMapper mapper;

    @Transactional
    @Override
	public HIEFWD saveHIResourceEFWD(HIEFWD hiefwd, String directory) {

		HIResource parent = hiServiceDB.getResourceByUrl(directory);
		if (null != parent) {
			hiefwd.setParentResource(parent);
			return efwdConnectionDAO.saveHIResourceEFWD(hiefwd);
		}
		throw new EfwdServiceException(directory + " Not found.");
	}

    @Transactional
    @Override
    public HIEfwdConnection saveEFWDConnection(HIEfwdConnection hiEfwdConnection) {
        return efwdConnectionDAO.saveEFWDConnection(hiEfwdConnection);
    }

    @Transactional
    @Override
    public List<HIEfwdConnection> findAllFEWDConnection() {
        return efwdConnectionDAO.findAllFEWDConnection();
    }

    @Transactional
    @Override
    public EFWDConnSqlJDBC editEFWDConnection(EFWDConnSqlJDBC connection) {
        return efwdConnectionDAO.edit(connection);
    }

    @Transactional
    @Override
    public EFWDConnSqlJDBC save(EFWDConnSqlJDBC efwdConnSqlJDBC) {
        return efwdConnectionDAO.save(efwdConnSqlJDBC);
    }


    @Transactional
    @Override
    public List<PlainConnDTO> findAllFEWDConnectionByType(String type) {
        List<PlainConnDTO> hiEfwdConnections = new ArrayList<>();
        List<Integer> connectionIds = efwdConnectionDAO.findAllFEWDConnections(type);
        prepareHIEFWDConnectionsData(connectionIds, hiEfwdConnections, type);
        return hiEfwdConnections;
    }

    private void prepareHIEFWDConnectionsData(List<Integer> connectionIds, List<PlainConnDTO> connectionList, String type) {
    	if ("sql.jdbc".equals(type)) {
    		for(Integer connectionId : connectionIds ) {
    			connectionList.add(efwdConnectionDAO.findPlainConnection(connectionId));
    		}
    	}
    	else {
    		for(Integer connectionId : connectionIds) {
    			connectionList.add(efwdConnectionDAO.findGroovyConnection(connectionId));
    		}
    	}
    }

    @Transactional
    @Override
    public HIEfwdConnection findConnectionByIDAndType(Integer connectionId, String type) {

        HIEfwdConnection connection = null;
        if(type.equalsIgnoreCase("sql.jdbc")) {
        	 EFWDConnSqlJDBC jdbc = efwdConnectionDAO.findSQLJDBCConnectionByIdAndType(connectionId, type);
        	 if (null != jdbc) {
     			connection = new HIEfwdConnection();
        		List<EFWDConnSqlJDBC> jdbcList = new ArrayList<>();
     			jdbcList.add(jdbc);
     			connection.setEfwdConnSqlJDBC(jdbcList);
     			connection.setId(jdbc.getHiEfwdConnection().getId());
     			connection.setType(type);
     			return connection;
     		}
        }
        else {
        	EFWDConnGroovy groovy = efwdConnectionDAO.findGroovyConnectionByIdAndType(connectionId, type);
        	if (null != groovy) {
        		connection = new HIEfwdConnection();
    			List<EFWDConnGroovy> groovyList = new ArrayList<>();
    			groovyList.add(groovy);
    			connection.setEfwdConnGroovy(groovyList);
    			connection.setId(groovy.getHiEfwdConnection().getId());
     			connection.setType(type);
    			return connection;
    		}
       
        }
        return null;
    }

    @Transactional
    @Override
    public EFWDConnSqlJDBC findConnectionByID(Integer connectionId) {
        return efwdConnectionDAO.findSQLJDBCConnectionById(connectionId);
    }

    @Transactional
    @Override
    public Integer deleteEFConnectionById(Integer connectionId) {
        return efwdConnectionDAO.deleteEFConnectionById(connectionId);
    }

    @Transactional
    @Override
    public void updateOrInsert(Integer connectionId, Integer userId, Integer roleId, Integer orgId,
                               Integer permissionLevel) {
        HIEfwdConnSecurity security = new HIEfwdConnSecurity();
        String createdBy = AuthenticationUtils.getUserId();
        User user = null;
        Role role = null;
        Organization org = null;

        if (null != userId) {
            user = userDao.findUser(userId);
            security.setUserId(user);
        }
        if (null != roleId) {
            role = roleDao.getRole(roleId);
            security.setRoleId(role);
        }

        if (null != orgId) {
            org = orgDao.getOrganization(orgId);
            security.setOrgId(org);
        }
        security.setCreatedBy(Integer.valueOf(createdBy));
        security.setPermission(permissionLevel);
        security.setLastUpdatedTime(new Date());
        HIEfwdConnection connection = new HIEfwdConnection();
        connection.setId(connectionId);
        security.setHiEfwdConnection(connection);
        efwdConnectionDAO.insertOrUpdate(security);

    }

    @Transactional
    @Override
    public void deleteEFConnectionecurityByConnectionId(Integer connectionId, Integer userId, Integer roleId,
                                                        Integer orgId) {
        efwdConnectionDAO.deleteEFConnectionecurityByConnectionId(connectionId, userId, roleId, orgId);
    }

    @Transactional
    @Override
    public HIEfwdConnSecurity findEfConnectionSecurityByConnectionId(Integer connectionId) {
        return efwdConnectionDAO.findEfConnectionSecurityByConnectionId(connectionId);
    }


    @Transactional
    @Override
    public EFWDConnGroovy save(EFWDConnGroovy efwdConnSqlJDBC) {
        return efwdConnectionDAO.save(efwdConnSqlJDBC);
    }

    private Object findConnectionByType(String type, Integer id) {

        if ("sql.jdbc".equals(type)) {
            return efwdConnectionDAO.findSQLJDBCConnectionById(id);
        } else if ("sql.jdbc.groovy".equals(type) || "sql.jdbc.groovy.managed".equals(type) ) {
            return efwdConnectionDAO.findGroovyByEFWDConnectionId(id);
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public EFWDConnGroovy editEFWDConnection(EFWDConnGroovy connection) {
        return efwdConnectionDAO.edit(connection);
    }

    @Transactional
    @Override
    public EFWDConnGroovy findGroovyByEFWDConnectionId(Integer connectionId) {
        return efwdConnectionDAO.findGroovyByEFWDConnectionId(connectionId);
    }

    @Transactional
    @Override
    public List<HIEfwdConnSecurity> findEFWDSecurityByConnectionId(Integer connectionId) {
        return efwdConnectionDAO.findEFWDSecurityByConnectionId(connectionId);
    }
    
    @Transactional
    @Override
    public List<HIEfwdConnSecurityDTO> findEFWDConnectionSecurityByConnectionId(Integer connectionId) {
    	List<HIEfwdConnSecurity> permissions = efwdConnectionDAO.findEFWDSecurityByConnectionId(connectionId);
    	List<HIEfwdConnSecurityDTO> permissionsDtos =  permissions.stream()
    			.map(permission -> mapper.map(permission))
    			.toList();
    	return permissionsDtos;
    }
    
    
    
    @Transactional
	@Override
	public List<EFWDConnSqlJDBCDTO> findConnectionByParentId(int parentId) {
    	return efwdConnectionDAO.findConnectionByParentId(parentId)
    			.stream().map(con -> mapper.map(con)).toList();
    	
	}
	@Transactional
	@Override
	public List<EFWDConnGroovyDTO> findGroovyByParentId(int parentId) {
		return  efwdConnectionDAO.findGroovyConnectionByParentId(parentId)
				.stream().map(con -> mapper.map(con)).toList();
	}
	
	@Transactional
	@Override
	public List<HIEfwdConnSecurity> getAllConnectionsFromShared(String type) {
		return efwdConnectionDAO.getAllConnectionsFromShared(type);
	}
	
	@Transactional
	@Override
	public List<EfwdConnDTO> getAllConnectionOfLoggedInUser(Integer createdBy, List<Integer> collect, String type) {
		return 	efwdConnectionDAO.getAllConnectionOfLoggedInUser(createdBy, collect, type);
	}

	@Override
	public List<String> getTypeByAllEfwdIds(List<Integer> efwdIdList, Set<String> efwdTypeList) {
		return null;
	}

	@Override
	@Transactional
	public HIEfwdConnection findConnectionByConnectionIdAndType(String connectionId, String type) {
		return efwdConnectionDAO.findConnectionByConnectionIdAndType(connectionId,type);
	}

	@Override
	@Transactional
	public List<EfwdConnDTO> findConnectionByResourceId(int resourceId,Boolean isRequiredForRecycleBinFetch,Boolean isRequiredToCheckTopLevelRootDeletion) {
		return efwdConnectionDAO.findConnectionByResourceId(resourceId,isRequiredForRecycleBinFetch,isRequiredToCheckTopLevelRootDeletion);
	}
    @Override
	@Transactional
	public List<EfwdConnDTO> findConnectionByResourceIds(List<Integer> resourceId,Boolean isRequiredForRecycleBinFetch,Boolean isRequiredToCheckTopLevelRootDeletion) {
		return efwdConnectionDAO.findConnectionByResourceIds(resourceId,isRequiredForRecycleBinFetch,isRequiredToCheckTopLevelRootDeletion);
	}

	@Override
	@Transactional
	public HIEfwdConnection findConnectionById(String connectionId) {
		
		return efwdConnectionDAO.findConnectionById(connectionId,true)
				.orElseThrow(() -> new EfwServiceException("Datasource not found."));
	}
	
	@Override
	@Transactional
	public EfwdConnDTO getConnectionById(String connectionId) {
		
		HIEfwdConnection connection =  efwdConnectionDAO.findConnectionById(connectionId,true)
				.orElseThrow(() -> new EfwServiceException("Datasource not found."));
		return mapper.map(connection);
	}
	
	@Transactional
	@Override
	public Integer edit(HIEfwdConnection connection) {
		return efwdConnectionDAO.edit(connection);
	}

	@Transactional
	@Override
	public Integer harddeleteEFConnectionById(Integer connectionId) {
		return efwdConnectionDAO.hardDeleteEFConnectionById(connectionId);
	}

	@Override
	@Transactional
	public List<HIEfwdConnection> findAllRequiredEFWDConnection(Set<Integer> connIds) {
		return efwdConnectionDAO.findAllRequiredEFWDConnection(connIds);
	}
	
	@Transactional
	@Override
	public HIEfwdConnection findConnectionById(String connectionId, boolean applyFilter) {
		return efwdConnectionDAO.findConnectionById(connectionId,applyFilter)
				.orElseThrow(() -> new EfwServiceException("Datasource not found."));
	}

	@Transactional
	@Override
	public Boolean isDeleted(String id) {
		return efwdConnectionDAO.isDeleted(id);
	}

	@Override
	@Transactional
	public HIRecycleBin getHiRecycleBinIdOfSoftDeletedItemByEfwdConnId(Integer connId) {
		return efwdConnectionDAO.getHiRecycleBinIdOfSoftDeletedItemByEfwdConnId(connId);
	}

	@Transactional
	@Override
	public Integer deleteConnectionSecuritiesById(int connectionId) {
		return efwdConnectionDAO.deleteConnectionSecuritiesById(connectionId);
	}

	@Transactional
	@Override
	public HIEfwdConnection findConnectionByLookup(EfwdDataSourceLookupDTO lookup) {
		return efwdConnectionDAO.findConnectionByLookup(lookup);
	}

	@Transactional
	@Override
	public void addHiHcrConnection(HIHcrConnections hiHcrConnections) {
		efwdConnectionDAO.addHiHcrConnection(hiHcrConnections);
	}

	@Transactional
	@Override
	public void addHiHcrEfwdConnection(HIHcrConnectionsEfwd hiHcrConnectionsEfwd) {
		efwdConnectionDAO.addHiHcrEfwdConnection(hiHcrConnectionsEfwd);
	}

	@Transactional
	@Override
	public void addHiHcrQuery(HiHcrQuery hcrQuery) {
		efwdConnectionDAO.addHiHcrQuery(hcrQuery);
	}

	@Transactional
	@Override
	public void addHiHcrQueryParams(HIHcrQueryParameters hiHcrQueryParameters) {
		efwdConnectionDAO.addHiHcrQueryParams(hiHcrQueryParameters);
	}

	@Transactional
	@Override
	public void deleteHiHcrConnection(HIHcrConnections hiHcrConnections) {
		efwdConnectionDAO.deleteHiHcrConnection(hiHcrConnections);
	}

	@Transactional
	@Override
	public List<HIHcrConnections> fetchAllHcrConnectionsByResourceId(Integer hcrResId) {
		return efwdConnectionDAO.fetchAllHcrConnectionsByResourceId(hcrResId);
	}
	@Transactional
	@Override
	public HIHcrConnections fetchHIHcrConnectionsById(Integer hcrResId){
		return efwdConnectionDAO.fetchHIHcrConnectionsById(hcrResId);
	}
	
	@Transactional
	@Override
	public PlainConnDTO findPlainConnection(Integer connectionId) {
		return efwdConnectionDAO.findPlainConnection(connectionId);
	}
	
	@Transactional
	@Override
	public PlainConnDTO findGroovyConnection(Integer connectionId) {
		return efwdConnectionDAO.findGroovyConnection(connectionId);
	}
	
	@Transactional
	@Override
	public List<PlainConnDTO> fetchSqlConnections(List<Integer> connectionIds) {
		return efwdConnectionDAO.fetchSqlConnections(connectionIds);
	}

	@Transactional
	@Override
	public List<PlainConnDTO> fetchGroovyConnections(List<Integer> connectionIds) {
		return efwdConnectionDAO.fetchGroovyConnections(connectionIds);
	}

	@Transactional
	@Override
	public void restoreConnection(Integer connectionId) {
		 efwdConnectionDAO.restoreConnection(connectionId);
	}

	@Override
	@Transactional
	public EFWDConnSqlJDBCDTO findSqlConnectionByID(Integer connectionId) {
		return mapper.map(findConnectionByID(connectionId));
	}

	@Override
	@Transactional
	public EFWDConnGroovyDTO findGroovyConnectionById(Integer connectionId) {
		return mapper.map(findGroovyByEFWDConnectionId(connectionId));
	}
	
	
}

