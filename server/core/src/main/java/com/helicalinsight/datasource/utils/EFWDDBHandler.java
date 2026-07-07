package com.helicalinsight.datasource.utils;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.datasource.helper.EfwDatasourceHelper;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.exceptions.EfwdServiceException;

@Component
public class EFWDDBHandler {
	
	@Autowired
	private EFWDConnectionService efwdService;
	
	@Autowired
	private EfwDatasourceHelper helper;

	public Integer saveHIEFWDConnection(ObjectNode jsonObject) {
		if(!jsonObject.has("directory")){
			throw new EfwdServiceException("Missing required Parameter Directory");
		}

		
		String directory  = jsonObject.required("directory").asText();
		String type  = jsonObject.required("type").asText();
		
		if(null == directory || "".equals(directory)) {
			throw new EfwdServiceException("Missing required Parameter Directory");
		}
		if(null == type || "".equals(type)) {
			throw new EfwdServiceException("Missing required Parameter type");
		}
			
		HIEFWD efwd = saveHiResourceEFWD(directory);
		HIEfwdConnection hiEfwdConnection = new HIEfwdConnection();
		hiEfwdConnection.setType(type);
		hiEfwdConnection.setHiResourceEFWD(efwd);
		hiEfwdConnection.setConnectionId(UUIDGenerator.getUuid());
		hiEfwdConnection = efwdService.saveEFWDConnection(hiEfwdConnection);
		
		if ("sql.jdbc".equalsIgnoreCase(type)) {
			 EFWDConnSqlJDBC sqlPojo = helper.buildPlainJdbcObject(jsonObject);
				sqlPojo.setHiEfwdConnection(hiEfwdConnection);
				efwdService.save(sqlPojo);
		} else if ("sql.jdbc.groovy".equalsIgnoreCase(type) || "sql.jdbc.groovy.managed".equalsIgnoreCase(type)) {
			 EFWDConnGroovy groovyPojo = helper.buildPlainGroovyObject(jsonObject);
				groovyPojo.setHiEfwdConnection(hiEfwdConnection);
				efwdService.save(groovyPojo);
		} 
		return hiEfwdConnection.getId();
	}

	public HIEFWD saveHiResourceEFWD(String directory) {
		HIEFWD hiEfwdResource = new HIEFWD();
		String createdBy = AuthenticationUtils.getUserId();
		Date date = new Date();
		hiEfwdResource.setCreatedBy(Integer.valueOf(createdBy));
		hiEfwdResource.setCreatedDate(date);
		hiEfwdResource.setLastUpdatedTime(date);
		hiEfwdResource = efwdService.saveHIResourceEFWD(hiEfwdResource, directory);
		return hiEfwdResource;
	}

	public PlainConnDTO readEFWDConnection(Integer id, String type) {
//		HIEfwdConnection connection = efwdService.findConnectionByIDAndType(id, type);
//		if (null!= connection && !connection.getEfwdConnSqlJDBC().isEmpty()) {
//			return connection.getEfwdConnSqlJDBC().get(0);
//		} else if (null != connection && !connection.getEfwdConnGroovy().isEmpty()) {
//			return connection.getEfwdConnGroovy().get(0);
//		}
//		return null;
		
		if ( "sql.jdbc".equals(type)) {
			return efwdService.findPlainConnection(id);
		}
		else {
			return efwdService.findGroovyConnection(id);
		}
		
	}
	

	public List<PlainConnDTO> findAllConnectionsByType(String type) {
		return efwdService.findAllFEWDConnectionByType(type);
	}

	public Integer deleteConnection(Integer id) {
		return efwdService.deleteEFConnectionById(id);
	}

	public EFWDConnGroovy updateGroovy(EFWDConnGroovy connection) {
		int id = connection.getId();
		EFWDConnGroovy existing = efwdService.findGroovyByEFWDConnectionId(id);
		if (null != existing) {
			connection.setHiEfwdConnection(existing.getHiEfwdConnection());
			connection.setId(existing.getId());
			efwdService.editEFWDConnection(connection);
		}
		return connection;
	}

	public EFWDConnSqlJDBC updatePlainJDBC(EFWDConnSqlJDBC connection) {
		Integer id = connection.getId(); 
		EFWDConnSqlJDBC existing = efwdService.findConnectionByID(id);
		if (null != existing) {
			connection.setHiEfwdConnection(existing.getHiEfwdConnection());
			connection.setId(existing.getId());
			efwdService.editEFWDConnection(connection);
		}
		return connection;
	}

}
