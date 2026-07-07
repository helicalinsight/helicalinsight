package com.helicalinsight.export.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.EFWDConnGroovyDTO;
import com.helicalinsight.admin.dto.EFWDConnSqlJDBCDTO;
import com.helicalinsight.admin.dto.EfwdDataSourceLookupDTO;
import com.helicalinsight.admin.dto.HIEfwdConnSecurityDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.AdvancedDatasourceWrapper;
import com.helicalinsight.export.dto.Conflict;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;

/**
 * Handles the export and import of advanced datasources, including Plain JDBC
 * and Groovy connections. Provides methods to write datasources to a directory
 * and import datasources from a file.
 */
@Component("efwdDSHandler")
@Scope("prototype")
public class AdvancedDSHandler extends DatasourceHandler {

	private static final Logger logger = LoggerFactory.getLogger(AdvancedDSHandler.class);

	@Autowired
	private EFWDConnectionService connectionService;
	@Autowired
	private HIMetadataResourceServiceDB mdServiceDb;

	/**
	 * write method Writes the given HIResourceDTO to the specified directory,
	 * creating a manifest entry for the datasource.
	 * 
	 * @param resource HIResourceDTO object provides resourceId,file type, name etc.
	 * @param dir      directory to write resources.
	 * @param manifest storing DataSource information.
	 */
	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest) {

		List<Integer> efwdIdList = resource.getEfwdIds();
		if (efwdIdList == null)
			efwdIdList = new ArrayList<>();
		Set<Integer> efwdIds = new HashSet<>(efwdIdList);
		List<EFWDConnSqlJDBCDTO> sqlConnections = new ArrayList<>();
		List<EFWDConnGroovyDTO> groovyConnections = new ArrayList<>();
		AdvancedDatasourceWrapper wrapper = new AdvancedDatasourceWrapper();
		if (efwdIds != null && efwdIds.size() > 0) {
			for (Integer ids : efwdIds) {
				EFWDConnSqlJDBCDTO found = connectionService.findSqlConnectionByID(ids);
				if (found != null) {
					sqlConnections.add(found);
				}
				EFWDConnGroovyDTO foundGroovy = connectionService.findGroovyConnectionById(ids);
				if (foundGroovy != null) {
					groovyConnections.add(foundGroovy);
				}

			}

			if ((sqlConnections == null || sqlConnections.isEmpty())
					&& (groovyConnections == null || groovyConnections.isEmpty())) {
				logger.debug("No Datasources found.");
				return;
			}

			if (sqlConnections != null && !sqlConnections.isEmpty()) {
				logger.info("No of Plain JDBC Connections : {}", sqlConnections.size());
				for (EFWDConnSqlJDBCDTO sqlJdbc : sqlConnections) {
					wrapper.getJdbc().add(sqlJdbc);
					List<HIEfwdConnSecurityDTO> securityList = shareHandler.getAdvancedConnectionShare(sqlJdbc.getHiEfwdConnection().getId());
					if (securityList != null && !securityList.isEmpty()) {
						wrapper.getSecurities().put(sqlJdbc.getHiEfwdConnection().getId(), securityList);
					}
				}
			}
			if (groovyConnections != null && !groovyConnections.isEmpty()) {
				logger.info("No of Groovy Connections : {}", groovyConnections.size());
				for (EFWDConnGroovyDTO groovy : groovyConnections) {
					wrapper.getGroovy().add(groovy);
					List<HIEfwdConnSecurityDTO> securityList = shareHandler.getAdvancedConnectionShare(groovy.getHiEfwdConnection().getId());
					if (securityList != null && !securityList.isEmpty()) {
						wrapper.getSecurities().put(groovy.getHiEfwdConnection().getId(), securityList);
					}
				}
			}
			dataWriter.write(wrapper, dir, resource, ResourceSuffix.DATASOURCE_EFWD);
			manifestUtils.insertDatasourceEFWD(resource, manifest);

			return;

		}

		if (resource.getType().equalsIgnoreCase("file")) {
			HIResourceMetadata metadata = mdServiceDb.giveHIResourceMetadataByResourceId(resource.getResourceId());
			for (HIMetadataConnections mdConnection : metadata.getHiMetadataConnections()) {
				for (HIMetadataConnectionEFWD efwdConn : mdConnection.getMetadataConnectionEfwd()) {
					if (efwdConn.getHiEfwdConnection() != null) {
						Integer connId = efwdConn.getHiEfwdConnection().getId();
						EFWDConnSqlJDBCDTO sql = connectionService.findSqlConnectionByID(connId);
						if (sql != null) {
							sqlConnections.add(sql);
						}
						EFWDConnGroovyDTO groovy = connectionService.findGroovyConnectionById(connId);
						if (groovy != null) {
							groovyConnections.add(groovy);
						}
					}
				}
			}
		}

		else {
			sqlConnections = connectionService.findConnectionByParentId(resource.getResourceId());
			groovyConnections = connectionService.findGroovyByParentId(resource.getResourceId());
		}

		if ((sqlConnections == null || sqlConnections.isEmpty())
				&& (groovyConnections == null || groovyConnections.isEmpty())) {
			logger.debug("No Datasources found.");
			return;
		}

		if (sqlConnections != null && !sqlConnections.isEmpty()) {
			logger.info("No of Plain JDBC Connections : {}", sqlConnections.size());
			for (EFWDConnSqlJDBCDTO sqlJdbc : sqlConnections) {
				wrapper.getJdbc().add(sqlJdbc);
				List<HIEfwdConnSecurityDTO> securityList = shareHandler.getAdvancedConnectionShare(sqlJdbc.getHiEfwdConnection().getId());
				if (securityList != null && !securityList.isEmpty()) {
					wrapper.getSecurities().put(sqlJdbc.getHiEfwdConnection().getId(), securityList);
				}
			}
		}
		if (groovyConnections != null && !groovyConnections.isEmpty()) {
			logger.info("No of Groovy Connections : {}", groovyConnections.size());
			for (EFWDConnGroovyDTO groovy : groovyConnections) {
				wrapper.getGroovy().add(groovy);
				List<HIEfwdConnSecurityDTO> securityList = shareHandler.getAdvancedConnectionShare(groovy.getHiEfwdConnection().getId());
				if (securityList != null && !securityList.isEmpty()) {
					wrapper.getSecurities().put(groovy.getHiEfwdConnection().getId(), securityList);
				}
			}
		}
		dataWriter.write(wrapper, dir, resource, ResourceSuffix.DATASOURCE);
		manifestUtils.insertDatasource(resource, manifest);
	}

	/**
	 * Imports a datasource from the specified file, updating or creating
	 * connections based on the onConflict parameter.
	 *
	 * @param resource   HIResource representing the datasource.
	 * @param path       path to the directory containing the datasource file.
	 * @param dsFileName name of the datasource file.
	 * @param onConflict conflict resolution strategy (UPDATE, SKIP, etc.).
	 */
	@Override
	public void importResource(HIResource resource, String dsFileName, String onConflict) {
		String resourcePath = context.getResourcesDirectory();
		try (FileInputStream inputStream = new FileInputStream(String.join(File.separator, resourcePath, dsFileName))) {

			ObjectNode objectNode = mapperUtils.mapToDTO(inputStream, ObjectNode.class);
			ArrayNode jdbcArray = objectNode.withArray("jdbc");
			ArrayNode groovyArray = objectNode.withArray("groovy");
			JsonNode securityNode = objectNode.path("securities");
			TypeReference<HashMap<Integer, List<HIEfwdConnSecurityDTO>>> typeRef = new TypeReference<HashMap<Integer, List<HIEfwdConnSecurityDTO>>>() {
			};
			Map<Integer, List<HIEfwdConnSecurityDTO>> securities = new ObjectMapper().readValue(securityNode.toString(),typeRef);
			for (JsonNode jdbcNode : jdbcArray) {
				EFWDConnSqlJDBC conn = mapperUtils.mapToDTO(jdbcNode.toString(), EFWDConnSqlJDBC.class);
				if (conn.getPass() != null)
					conn.setPass(CipherUtils.encrypt(conn.getPass()));
				HIEfwdConnection efwdConnection = conn.getHiEfwdConnection();
				Integer oldId = efwdConnection.getId();
				if (context.getProcessed(efwdConnection.getType(), oldId))
					continue;

				EfwdDataSourceLookupDTO lookup = new EfwdDataSourceLookupDTO();

				lookup.setJdbcUrl(conn.getUrl());
				lookup.setName(conn.getName());
				lookup.setPassword(conn.getPass());
				lookup.setType(efwdConnection.getType());
				lookup.setUserName(conn.getUserName());
				lookup.setDirectory(context
						.addDestination(efwdConnection.getHiResourceEFWD().getParentResource().getResourceURL()));

				HIEfwdConnection existingConnection = connectionService.findConnectionByLookup(lookup);

				if (existingConnection == null) {
					efwdConnection.setDeleted(false);
					HIEFWD efwd =  efwdConnection.getHiResourceEFWD();
					HIResource parentResource = efwd.getParentResource();
					parentResource.setResourceURL(context.addDestination(parentResource.getResourceURL()));
					getOrInsertParent(parentResource, efwdConnection);
					connectionService.saveHIResourceEFWD(efwd, parentResource.getResourceURL());
					connectionService.saveEFWDConnection(efwdConnection);
					connectionService.save(conn);
					handleSecurity(securities, efwdConnection.getId(), oldId);
				} else if (Conflict.UPDATE.equalsIgnoreCase(onConflict)) {
					context.recover(existingConnection);
					connectionService.edit(existingConnection);
					efwdConnection = existingConnection;
					List<EFWDConnSqlJDBC> sqlJdbcList = existingConnection.getEfwdConnSqlJDBC();
					if (!sqlJdbcList.isEmpty()) {
						sqlJdbcList.forEach(jdbc -> {
							jdbc.setDatabase(conn.getDatabase());
							jdbc.setDriver(conn.getDriver());
							jdbc.setHiEfwdConnection(existingConnection);
							jdbc.setName(conn.getName());
							jdbc.setPass(CipherUtils.encrypt(conn.getPass()));
							jdbc.setUrl(conn.getUrl());
							jdbc.setUserName(conn.getUserName());
							connectionService.editEFWDConnection(jdbc);
						});
					} else {
						conn.setHiEfwdConnection(existingConnection);
						connectionService.save(conn);
					}
					handleSecurity(securities, efwdConnection.getId(), oldId);
				} else {
					// just skip
				}
				context.setProcessed(efwdConnection.getType(), oldId);
				context.putEfwdConnection(oldId, efwdConnection);
			}

			for (JsonNode groovyNode : groovyArray) {
				EFWDConnGroovy groovy = mapperUtils.mapToDTO(groovyNode.toString(), EFWDConnGroovy.class);
				if (groovy.getPass() != null)
					groovy.setPass(CipherUtils.encrypt(groovy.getPass()));
				HIEfwdConnection efwdConnection = groovy.getHiEfwdConnection();
				Integer oldId = efwdConnection.getId();
				if (context.getProcessed(efwdConnection.getType(), oldId))
					continue;

				EfwdDataSourceLookupDTO lookup = new EfwdDataSourceLookupDTO();

				lookup.setJdbcUrl(groovy.getUrl());
				lookup.setName(groovy.getName());
				lookup.setPassword(groovy.getPass());
				lookup.setType(efwdConnection.getType());
				lookup.setUserName(groovy.getUserName());
				lookup.setCondition(groovy.getCondition());
				lookup.setDirectory(context
						.addDestination(efwdConnection.getHiResourceEFWD().getParentResource().getResourceURL()));

				HIEfwdConnection existingConnection = connectionService.findConnectionByLookup(lookup);

				if (existingConnection == null) {
					efwdConnection.setDeleted(false);
					HIEFWD efwd =  efwdConnection.getHiResourceEFWD();
					HIResource parentResource = efwd.getParentResource();
					parentResource.setResourceURL(context.addDestination(parentResource.getResourceURL()));
					getOrInsertParent(parentResource, efwdConnection);
					connectionService.saveHIResourceEFWD(efwd, parentResource.getResourceURL());
					connectionService.saveEFWDConnection(efwdConnection);
					groovy.setHiEfwdConnection(efwdConnection);
					connectionService.save(groovy);
					handleSecurity(securities, efwdConnection.getId(), oldId);
				} else if (Conflict.UPDATE.equalsIgnoreCase(onConflict)) {
					context.recover(existingConnection);
					connectionService.edit(existingConnection);
					efwdConnection = existingConnection;
					List<EFWDConnGroovy> groovyList = existingConnection.getEfwdConnGroovy();
					if (!groovyList.isEmpty())
						groovyList.forEach(con -> {
							con.setCondition(groovy.getCondition());
							con.setDatabase(groovy.getDatabase());
							con.setDriver(groovy.getDriver());
							con.setHiEfwdConnection(existingConnection);
							con.setName(groovy.getName());
							String password = groovy.getPass() != null ? CipherUtils.encrypt(groovy.getPass()) : null;
							con.setPass(password);
							con.setUrl(groovy.getUrl());
							con.setUserName(groovy.getUserName());
							connectionService.editEFWDConnection(con);
						});
					else {
						groovy.setHiEfwdConnection(existingConnection);
						connectionService.editEFWDConnection(groovy);
					}
					handleSecurity(securities, efwdConnection.getId(), oldId);
				}
				context.setProcessed(efwdConnection.getType(), oldId);
				context.putEfwdConnection(oldId, efwdConnection);
			}
		}

		catch (Exception e) {
			logger.error("Error occurred : {}", e.getMessage());
			if (logger.isErrorEnabled()) {
				e.printStackTrace();
			}
			throw new ResourceImportException("Error occurred while importing datasource.");
		}
	}

	/**
	 * Imports a datasource from the specified file, updating or creating
	 * connections based on the onConflict parameter.
	 *
	 * @param resource   HIResource representing the datasource.
	 * @param path       path to the directory containing the datasource file.
	 * @param dsFileName name of the datasource file.
	 * @param onConflict conflict resolution strategy (UPDATE, SKIP, etc.).
	 */
	@Override
	public List<String> importResourceHCR(String dsFileName, String onConflict) {
		List<String> oldMappingNewMapping = new ArrayList<>();
		String resourcePath = context.getResourcesDirectory();
		try (FileInputStream inputStream = new FileInputStream(String.join(File.separator, resourcePath, dsFileName))) {

			ObjectNode objectNode = mapperUtils.mapToDTO(inputStream, ObjectNode.class);
			ArrayNode jdbcArray = objectNode.withArray("jdbc");
			ArrayNode groovyArray = objectNode.withArray("groovy");
			JsonNode securityNode = objectNode.path("securities");
			TypeReference<HashMap<Integer, List<HIEfwdConnSecurityDTO>>> typeRef = new TypeReference<HashMap<Integer, List<HIEfwdConnSecurityDTO>>>() {};
			Map<Integer, List<HIEfwdConnSecurityDTO>> securities = new ObjectMapper().readValue(securityNode.toString(),typeRef);
			for (JsonNode jdbcNode : jdbcArray) {
				EFWDConnSqlJDBC conn = mapperUtils.mapToDTO(jdbcNode.toString(), EFWDConnSqlJDBC.class);
				if (conn.getPass() != null)
					conn.setPass(CipherUtils.encrypt(conn.getPass()));
				HIEfwdConnection efwdConnection = conn.getHiEfwdConnection();
				Integer oldId = efwdConnection.getId();
				if (context.getProcessed(efwdConnection.getType(), oldId)) {
					String mappingString = "" + oldId + ":" + context.getEfwdConnection(oldId).getId();
					oldMappingNewMapping.add(mappingString);
					continue;
				}

				EfwdDataSourceLookupDTO lookup = new EfwdDataSourceLookupDTO();

				lookup.setJdbcUrl(conn.getUrl());
				lookup.setName(conn.getName());
				lookup.setPassword(conn.getPass());
				lookup.setType(efwdConnection.getType());
				lookup.setUserName(conn.getUserName());
				lookup.setDirectory(context
						.addDestination(efwdConnection.getHiResourceEFWD().getParentResource().getResourceURL()));

				HIEfwdConnection existingConnection = connectionService.findConnectionByLookup(lookup);

				if (existingConnection == null) {
					efwdConnection.setDeleted(false);
					HIEFWD efwd =  efwdConnection.getHiResourceEFWD();
					HIResource parentResource = efwd.getParentResource();
					parentResource.setResourceURL(context.addDestination(parentResource.getResourceURL()));
					getOrInsertParent(parentResource, efwdConnection);
					connectionService.saveHIResourceEFWD(efwd, parentResource.getResourceURL());
					connectionService.saveEFWDConnection(efwdConnection);
					connectionService.save(conn);
					handleSecurity(securities, efwdConnection.getId(), oldId);
				} else if (Conflict.UPDATE.equalsIgnoreCase(onConflict)) {
					context.recover(existingConnection);
					connectionService.edit(existingConnection);
					efwdConnection = existingConnection;
					List<EFWDConnSqlJDBC> sqlJdbcList = existingConnection.getEfwdConnSqlJDBC();
					if (!sqlJdbcList.isEmpty()) {
						sqlJdbcList.forEach(jdbc -> {
							jdbc.setDatabase(conn.getDatabase());
							jdbc.setDriver(conn.getDriver());
							jdbc.setHiEfwdConnection(existingConnection);
							jdbc.setName(conn.getName());
							jdbc.setPass(CipherUtils.encrypt(conn.getPass()));
							jdbc.setUrl(conn.getUrl());
							jdbc.setUserName(conn.getUserName());
							connectionService.editEFWDConnection(jdbc);
						});
					} else {
						conn.setHiEfwdConnection(existingConnection);
						connectionService.save(conn);
					}
					handleSecurity(securities, efwdConnection.getId(), oldId);
				} else {
					// just skip
				}
				String mappingString = "" + oldId + ":" + efwdConnection.getId();
				oldMappingNewMapping.add(mappingString);
				context.setProcessed(efwdConnection.getType(), oldId);
				context.putEfwdConnection(oldId, efwdConnection);
			}

			for (JsonNode groovyNode : groovyArray) {
				EFWDConnGroovy groovy = mapperUtils.mapToDTO(groovyNode.toString(), EFWDConnGroovy.class);
				if (groovy.getPass() != null)
					groovy.setPass(CipherUtils.encrypt(groovy.getPass()));
				HIEfwdConnection efwdConnection = groovy.getHiEfwdConnection();
				Integer oldId = efwdConnection.getId();
				if (context.getProcessed(efwdConnection.getType(), oldId)) {
					String mappingString = "" + oldId + ":" + context.getEfwdConnection(oldId).getId();
					oldMappingNewMapping.add(mappingString);
					continue;
				}

				EfwdDataSourceLookupDTO lookup = new EfwdDataSourceLookupDTO();

				lookup.setJdbcUrl(groovy.getUrl());
				lookup.setName(groovy.getName());
				lookup.setPassword(groovy.getPass());
				lookup.setType(efwdConnection.getType());
				lookup.setUserName(groovy.getUserName());
				lookup.setCondition(groovy.getCondition());
				lookup.setDirectory(context
						.addDestination(efwdConnection.getHiResourceEFWD().getParentResource().getResourceURL()));

				HIEfwdConnection existingConnection = connectionService.findConnectionByLookup(lookup);

				if (existingConnection == null) {
					efwdConnection.setDeleted(false);
					HIEFWD efwd =  efwdConnection.getHiResourceEFWD();
					HIResource parentResource = efwd.getParentResource();
					parentResource.setResourceURL(context.addDestination(parentResource.getResourceURL()));
					getOrInsertParent(parentResource, efwdConnection);
					connectionService.saveHIResourceEFWD(efwd, parentResource.getResourceURL());
					connectionService.saveEFWDConnection(efwdConnection);
					groovy.setHiEfwdConnection(efwdConnection);
					connectionService.save(groovy);
					handleSecurity(securities, efwdConnection.getId(), oldId);
				} else if (Conflict.UPDATE.equalsIgnoreCase(onConflict)) {
					context.recover(existingConnection);
					connectionService.edit(existingConnection);
					efwdConnection = existingConnection;
					List<EFWDConnGroovy> groovyList = existingConnection.getEfwdConnGroovy();
					if (!groovyList.isEmpty())
						groovyList.forEach(con -> {
							con.setCondition(groovy.getCondition());
							con.setDatabase(groovy.getDatabase());
							con.setDriver(groovy.getDriver());
							con.setHiEfwdConnection(existingConnection);
							con.setName(groovy.getName());
							String password = groovy.getPass() != null ? CipherUtils.encrypt(groovy.getPass()) : null;
							con.setPass(password);
							con.setUrl(groovy.getUrl());
							con.setUserName(groovy.getUserName());
							connectionService.editEFWDConnection(con);
						});
					else {
						groovy.setHiEfwdConnection(existingConnection);
						connectionService.editEFWDConnection(groovy);
					}
					handleSecurity(securities, efwdConnection.getId(), oldId);
				}
				context.setProcessed(efwdConnection.getType(), oldId);
				context.putEfwdConnection(oldId, efwdConnection);
				String mappingString = "" + oldId + ":" + efwdConnection.getId();
				oldMappingNewMapping.add(mappingString);

			}
		}

		catch (Exception e) {
			logger.error("Error occurred : {}", e.getMessage());
			if (logger.isErrorEnabled()) {
				e.printStackTrace();
			}
			throw new ResourceImportException("Error occurred while importing datasource.");
		}
		return oldMappingNewMapping;
	}

	/**
	 * Method does the retrieval or insertion of the parent resource for the given
	 * HIResource and HIEfwdConnection.
	 *
	 * @param parentResource HIResource object provides url.
	 * @param efwdConnection HIEfwdConnection object provides resouce for EFWD.
	 */
	private void getOrInsertParent(HIResource parentResource, HIEfwdConnection efwdConnection) {
		HIResource dbResource = serviceDb.getResourceByUrl(parentResource.getResourceURL());
		if (dbResource != null) {
			efwdConnection.getHiResourceEFWD().setParentResource(dbResource);
		} else {
			String[] urls = parentResource.getResourceURL().split("/");
			HIResource parent = null;
			StringBuilder prevUrl = new StringBuilder();
			for (String url : urls) {
				prevUrl.append(url);
				HIResource resource = serviceDb.getResourceByUrl(prevUrl.toString());
				if (resource == null) {
					HIResource newResource = ResourceUtils.newHIResource(JsonUtils.getFolderFileExtension(),
							context.getDate(), null, prevUrl.toString(), url, url,
							parent != null ? parent.getResourceId() : null, false);
					serviceDb.addHIResource(newResource);
					parent = newResource;
				} else {
					parent = resource;
				}
				prevUrl.append("/");
			}
			efwdConnection.getHiResourceEFWD().setParentResource(parent);
		}
	}

	/**
	 * Handles the security information for the given connection id and old
	 * connection id.
	 *
	 * @param securities Map of connection id to a list of security information.
	 * @param id         connection id.
	 * @param oldId      old connection id to get Security details.
	 */
	private final void handleSecurity(Map<Integer, List<HIEfwdConnSecurityDTO>> securities, Integer id, Integer oldId) {
		connectionService.deleteConnectionSecuritiesById(id);
		if (securities != null && !securities.isEmpty()) {
			List<HIEfwdConnSecurityDTO> securityArray = securities.get(oldId);
			if (securityArray != null && !securityArray.isEmpty()) {
				shareHandler.importEFWDConnectionPermissions(id, securityArray);
			}
		}
	}

}
