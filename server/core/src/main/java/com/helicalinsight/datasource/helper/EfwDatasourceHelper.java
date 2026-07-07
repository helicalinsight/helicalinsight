package com.helicalinsight.datasource.helper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.dto.HIEfwdDTO;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
@Component
public class EfwDatasourceHelper {

	public EFWDConnGroovy buildPlainGroovyObject(ObjectNode commonParams) {

		EFWDConnGroovy groovy = new EFWDConnGroovy();
		if(commonParams.has("driverName"))
		groovy.setDriver(commonParams.required("driverName").asText());
		if(commonParams.has("userName"))
		groovy.setUserName(commonParams.required("userName").asText());
		if(commonParams.has("password"))
		groovy.setPass(CipherUtils.encrypt(commonParams.required("password").asText()));
		if(commonParams.has("condition"))
		groovy.setCondition(commonParams.path("condition").asText(""));
		if(commonParams.has("database"))
		groovy.setDatabase(commonParams.required("database").asText());
		if(commonParams.has("jdbcUrl"))
		groovy.setUrl(commonParams.required("jdbcUrl").asText());
		if(commonParams.has("name"))
		groovy.setName(commonParams.required("name").asText());
		if(commonParams.has("id"))
		groovy.setId((commonParams.path("id").asInt()));
		return groovy;
	}

	public EFWDConnSqlJDBC buildPlainJdbcObject(ObjectNode commonParams) {

		EFWDConnSqlJDBC jdbc = new EFWDConnSqlJDBC();
		if(commonParams.has("driverName"))
			jdbc.setDriver(commonParams.required("driverName").asText());
		if(commonParams.has("userName"))
			jdbc.setUserName(commonParams.required("userName").asText());
		if(commonParams.has("password"))
			jdbc.setPass(CipherUtils.encrypt(commonParams.required("password").asText()));
		if(commonParams.has("database"))
			jdbc.setDatabase(commonParams.required("database").asText());
		if(commonParams.has("jdbcUrl"))
			jdbc.setUrl(commonParams.required("jdbcUrl").asText());
		if(commonParams.has("name"))
			jdbc.setName(commonParams.required("name").asText());
		if(commonParams.has("id"))
			jdbc.setId(commonParams.path("id").asInt());
		return jdbc;
	}

	public void prepareResponseObject(PlainConnDTO connection, List<ObjectNode> dataHolder, String type) {

		HIResourceServiceDB service = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
		ResourcePermissionLevelsHolder permissionLevelHolder = ApplicationContextAccessor
				.getBean(ResourcePermissionLevelsHolder.class);

		Integer permissionLevel = null;
		HIEfwdDTO resource = null;
		String driver = "";
		String name = "";
		int id = 0;
		String dir = "";
		Integer createdBy = null;
		String userName = "";
		String password = "";
		String condition = "";
		String url = "";
		if (null != connection) {
				driver = connection.getDriver();
				name = connection.getName();
				resource = connection.getEfwd();
				id = connection.getEfwdId();
				createdBy = resource.getCreatedBy();
				if( resource != null) {
					dir =  resource.getResourceUrl();
				}
				userName = connection.getUserName();
				password = connection.getPass();
				condition = connection.getCondition();
				url = connection.getUrl();
		} 

		if (createdBy == null) {
			permissionLevel = permissionLevelHolder.publicResourceAccessLevel();
		} else if (createdBy.equals(Integer.valueOf(AuthenticationUtils.getUserId()))) {
			permissionLevel = permissionLevelHolder.ownerAccessLevel();
		} else {
			Map<Integer, Integer> securityMap = service.getSecurityMap();
			permissionLevel = securityMap.get(resource.getResourceId());
		}

		ObjectNode  response =  JsonNodeFactory.instance.objectNode();
		ObjectNode data = JsonNodeFactory.instance.objectNode();
		
		response.put("permissionLevel", permissionLevel);
		response.put("driver", driver);
		data.put("dir", dir);
		data.put("driverName", driver);
		data.put("type", type);
		data.put("id", id);
		data.put("userName", userName);
		data.put("password", password);
		if(!("".equals(condition))) {
			data.put("condition", condition);
		}
		data.put("jdbcUrl", url);
		response.set("data", data);
		response.put("name", name);
		response.put("classifier", "efwd");
		response.put("type", type);
		response.put("dataSourceType", "sql.jdbc".equals(type)?"Plain Jdbc DataSource":("sql.jdbc.groovy".equals(type)?"Plain Groovy DataSource":"sql.jdbc.groovy.managed".equals(type)?"Plain Groovy Managed DataSource":""));
		dataHolder.add(response);
	}

}
