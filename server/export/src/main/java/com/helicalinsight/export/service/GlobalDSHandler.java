package com.helicalinsight.export.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.GlobalDatasourceLookupDTO;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.datasource.model.DSExtraOption;
import com.helicalinsight.datasource.model.DSTypeHikari;
import com.helicalinsight.datasource.model.DSTypeJndi;
import com.helicalinsight.datasource.model.DSTypeNoSQL;
import com.helicalinsight.datasource.model.DSTypeTomcat;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.export.dto.Conflict;
import com.helicalinsight.export.dto.DataSourceWrapper;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;

@Component("globalDSHandler")
public class GlobalDSHandler extends DatasourceHandler {

	@Autowired
	HIMetadataResourceServiceDB mdServiceDB;

	@Autowired
	private GlobalConnectionService connectionService;


	private static final Logger logger = LoggerFactory.getLogger(GlobalDSHandler.class);


	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest) {
List<Integer> globalList = resource.getGlobalIds();
		if(globalList==null) {
			HIResourceMetadata metadata = mdServiceDB.giveHIResourceMetadataByResourceId(resource.getResourceId());
			if (metadata != null) {
				List<HIMetadataConnections> connections = metadata.getHiMetadataConnections();
				if (null != connections && !connections.isEmpty()) {
					List<DataSourceWrapper> dsWrapper = new ArrayList<>();
					for (HIMetadataConnections connection : connections) {
						List<HIMetadataConnectionGlobal> mdGlobalConnections = connection.getMetadataGlobalConnList();
						for (HIMetadataConnectionGlobal mdGlobalConnection : mdGlobalConnections) {
							GlobalConnections globalConnection = mdGlobalConnection.getGlobalConnections();
							int globalId = globalConnection.getGlobalId();
							hadleGlobalConnection(globalId, dsWrapper);
						}
					}
					dataWriter.write(dsWrapper, dir, resource, ResourceSuffix.DATASOURCE);
					manifestUtils.insertDatasource(resource, manifest);
				}
			}
		}else{
			List<DataSourceWrapper> dsWrapper = new ArrayList<>();
			for(Integer globalId : globalList) {
				hadleGlobalConnection(globalId, dsWrapper);
			}
			dataWriter.write(dsWrapper, dir, resource, ResourceSuffix.DATASOURCE);
			manifestUtils.insertDatasource(resource, manifest);
		}
	}

private void hadleGlobalConnection(Integer globalId,List<DataSourceWrapper> dsWrapper){


	List<DSExtraOption> extraOptions = connectionService.getExtraOptions(globalId);// BUG-7548

	DSTypeJndi jndi = connectionService.getJndiConnectionById(globalId);
	DSTypeHikari hikari = connectionService.getHikariConnectionById(globalId);
	DSTypeTomcat tomcat = connectionService.getTomcatConnectionById(globalId);
	DSTypeNoSQL noSql = connectionService.getNoSQLConnectionById(globalId);
	DataSourceWrapper wrapper = new DataSourceWrapper();
	if (null != jndi) {
		wrapper.setJndi(jndi);
		Map<Integer, List<GlobalConnectionSecurity>> securities = shareHandler
				.getGlobalConnectionShare(globalId, jndi.getId());
		wrapper.setSecurities(securities);
	} else if (null != hikari) {
		wrapper.setHikari(hikari);
		Map<Integer, List<GlobalConnectionSecurity>> securities = shareHandler
				.getGlobalConnectionShare(globalId, hikari.getId());
		wrapper.setSecurities(securities);
	} else if (null != noSql) {
		wrapper.setNoSql(noSql);
		Map<Integer, List<GlobalConnectionSecurity>> securities = shareHandler
				.getGlobalConnectionShare(globalId, noSql.getId());
		wrapper.setSecurities(securities);
	} else {
		wrapper.setTomcat(tomcat);
		Map<Integer, List<GlobalConnectionSecurity>> securities = shareHandler
				.getGlobalConnectionShare(globalId, tomcat.getId());
		wrapper.setSecurities(securities);
	}

	wrapper.setExtraOptions(extraOptions);//BUG - 7548

	dsWrapper.add(wrapper);




}


	@Override
	public List<String> importResourceHCR( String dsFileName, String onConflict) {
		List<String> oldMappingNewMapping=new ArrayList<>();
		String resourcePath = context.getResourcesDirectory();
		try (FileInputStream inputStream = new FileInputStream(String.join(File.separator, resourcePath, dsFileName))) {
			ArrayNode dsList = mapperUtils.mapToArray(inputStream);
			String mappingString="";
			Integer oldId=null;
			for (JsonNode node : dsList) {
				GlobalConnections globalID = null;// BUG-7548
				DataSourceWrapper wrapper = mapperUtils.mapToDTO(node.toString(), DataSourceWrapper.class);
				if (wrapper.getHikari() != null) {
					oldId=node.get("hikari").get("globalConnections").get("id").asInt();
					globalID = processHikariDatasource(wrapper.getHikari(), wrapper.getSecurities());
				} else if (wrapper.getJndi() != null) {
					oldId=node.get("jndi").get("globalConnections").get("id").asInt();
					globalID = processJndiDatasource(wrapper.getJndi(), wrapper.getSecurities());
				} else if (wrapper.getNoSql() != null) {
					oldId=node.get("noSql").get("globalConnections").get("id").asInt();
					globalID = processNoSqlDatasource(wrapper.getNoSql(), wrapper.getSecurities());
				} else {
					oldId=node.get("tomcat").get("globalConnections").get("id").asInt();
					globalID = processTomcatDatasource(wrapper.getTomcat(), wrapper.getSecurities());
				}
				mappingString=""+oldId +":"+globalID.getGlobalId();
				oldMappingNewMapping.add(mappingString);
				// BUG-7548
				List<DSExtraOption> extraOptions = wrapper.getExtraOptions();
				List<DSExtraOption> existingOptions = connectionService.getExtraOptions(globalID.getId());
				if (extraOptions != null && !(extraOptions.isEmpty())) {
					for (DSExtraOption extraDSOption : extraOptions) {
						// BUG7630
						DSExtraOption matchingOption = existingOptions.stream()
								.filter(existingOption -> existingOption.getKey().equals(extraDSOption.getKey()))
								.findFirst().orElse(null);

						if (matchingOption != null) {
							if (matchingOption.getValue().equals(extraDSOption.getValue())) {
								logger.warn(
										"Duplicate extra option found for key: {}, value: {} for GlobalConnection ID: {}",
										extraDSOption.getKey(), extraDSOption.getValue(), globalID);
								continue;
							}else{
								if(!onConflict.equals("skip")) {
									matchingOption.setValue(extraDSOption.getValue());
									connectionService.updateExtraOption(matchingOption);
								}
							}



						} else {
							extraDSOption.setGlobalConnection(globalID);
							connectionService.saveExtraOption(extraDSOption);
						}
					}
				}
			}
return  oldMappingNewMapping;
		} catch (Exception e) {
			logger.error("Error occurred : {}", e.getMessage());
			throw new ResourceImportException("Error occurred while importing datasource.");
		}
	}


	@Override
	public void importResource(HIResource resource, String dsFileName, String onConflict) {
		String resourcePath = context.getResourcesDirectory();
		try (FileInputStream inputStream = new FileInputStream(String.join(File.separator, resourcePath, dsFileName))) {
			ArrayNode dsList = mapperUtils.mapToArray(inputStream);
			for (JsonNode node : dsList) {
				GlobalConnections globalID = null;// BUG-7548
				DataSourceWrapper wrapper = mapperUtils.mapToDTO(node.toString(), DataSourceWrapper.class);
				if (wrapper.getHikari() != null) {
					globalID = processHikariDatasource(wrapper.getHikari(), wrapper.getSecurities());
				} else if (wrapper.getJndi() != null) {
					globalID = processJndiDatasource(wrapper.getJndi(), wrapper.getSecurities());
				} else if (wrapper.getNoSql() != null) {
					globalID = processNoSqlDatasource(wrapper.getNoSql(), wrapper.getSecurities());
				} else {
					globalID = processTomcatDatasource(wrapper.getTomcat(), wrapper.getSecurities());
				}
				// BUG-7548
				List<DSExtraOption> extraOptions = wrapper.getExtraOptions();
				List<DSExtraOption> existingOptions = connectionService.getExtraOptions(globalID.getId());
				if (extraOptions != null && !(extraOptions.isEmpty())) {
					for (DSExtraOption extraDSOption : extraOptions) {
						// BUG7630
						DSExtraOption matchingOption = existingOptions.stream()
								.filter(existingOption -> existingOption.getKey().equals(extraDSOption.getKey()))
								.findFirst().orElse(null);

						if (matchingOption != null) {
							if (matchingOption.getValue().equals(extraDSOption.getValue())) {
								logger.warn(
										"Duplicate extra option found for key: {}, value: {} for GlobalConnection ID: {}",
										extraDSOption.getKey(), extraDSOption.getValue(), globalID);
								continue;
							}else{
								if(!onConflict.equals("skip")) {
									matchingOption.setValue(extraDSOption.getValue());
									connectionService.updateExtraOption(matchingOption);
								}
							}



						} else {
							extraDSOption.setGlobalConnection(globalID);
							connectionService.saveExtraOption(extraDSOption);
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error occurred : {}", e.getMessage());
			throw new ResourceImportException("Error occurred while importing datasource.");
		}
	}


	private final GlobalConnections processTomcatDatasource(DSTypeTomcat tomcat,Map<Integer, List<GlobalConnectionSecurity>> securityMap) {

		GlobalConnections gConnection = tomcat.getGlobalConnections();
		GlobalDatasourceLookupDTO lookup = prepareLookup(gConnection.getName(), gConnection.getDsType(), tomcat.getUrl(),tomcat.getDatabaseName(),
				tomcat.getUsername(),tomcat.getPassword(), gConnection.getType());

		List<GlobalConnectionSecurity> tomcatSecurity = securityMap != null ?securityMap.get(tomcat.getId()): null;
		gConnection = getOrInsertGlobalConnection(lookup,gConnection);
		tomcat.setGlobalConnections(gConnection);
		tomcat.setLastUpdatedTime(context.getDate());
		String password = tomcat.getPassword() != null ? CipherUtils.encrypt(tomcat.getPassword()):"";
		tomcat.setPassword(password);
		DSTypeTomcat dbTomcat = connectionService.getTomcatConnectionById(gConnection.getGlobalId());
		String onConflict = context.getRequest().getOnConflict();

		if (dbTomcat != null) {
			if (onConflict.equalsIgnoreCase(Conflict.UPDATE)) {
				dbTomcat.setLastUpdatedTime(context.getDate());
				connectionService.editTomcatConnections(dbTomcat);
				handleSecurity(gConnection.getGlobalId(), tomcatSecurity);
			}
		}
		else {
			connectionService.addTomcatConnections(tomcat);
			handleSecurity(gConnection.getGlobalId(), tomcatSecurity);
		}
		return gConnection;
	}

	private final GlobalConnections processHikariDatasource(DSTypeHikari hikari,Map<Integer, List<GlobalConnectionSecurity>> securityMap) {

		GlobalConnections gConnection = hikari.getGlobalConnections();

		GlobalDatasourceLookupDTO lookup = prepareLookup(gConnection.getName(), gConnection.getDsType(), hikari.getJdbcUrl(),hikari.getDatabaseName(),
				hikari.getUserName(),hikari.getPassword(), gConnection.getType());

		gConnection = getOrInsertGlobalConnection(lookup,gConnection);
		List<GlobalConnectionSecurity> hikariSecurity = securityMap != null ?securityMap.get(hikari.getId()): null;
		hikari.setLastUpdatedTime(context.getDate());
		hikari.setGlobalConnections(gConnection);
		String password = hikari.getPassword() != null ? CipherUtils.encrypt(hikari.getPassword()):"";
		hikari.setPassword(password);
		DSTypeHikari dbHikari = connectionService.getHikariConnectionById(gConnection.getGlobalId());
		String onConflict = context.getRequest().getOnConflict();

		if (dbHikari != null) {
			if (onConflict.equalsIgnoreCase(Conflict.UPDATE)) {
				dbHikari.setLastUpdatedTime(context.getDate());
				connectionService.editHikariConnections(dbHikari);
				handleSecurity(gConnection.getGlobalId(), hikariSecurity);
			}
		}
		else  {
			connectionService.addHikariConnections(hikari);
			handleSecurity(gConnection.getGlobalId(), hikariSecurity);
		}
		return gConnection;
	}

	private final GlobalConnections processJndiDatasource(DSTypeJndi jndi , Map<Integer, List<GlobalConnectionSecurity>> securityMap) {

		GlobalConnections gConnection = jndi.getGlobalConnections();
		GlobalDatasourceLookupDTO lookup = prepareLookup(gConnection.getName(), gConnection.getDsType(), jndi.getLookUpName(),jndi.getDatabaseName(),
				"","",gConnection.getType());

		gConnection = getOrInsertGlobalConnection(lookup,gConnection);

		List<GlobalConnectionSecurity> jndiSecurity = securityMap != null ?securityMap.get(jndi.getId()): null;
		jndi.setLastUpdatedTime(context.getDate());
		jndi.setGlobalConnections(gConnection);
		DSTypeJndi dbJndi = connectionService.getJndiConnectionById(gConnection.getGlobalId());
		String onConflict = context.getRequest().getOnConflict();
		if (dbJndi != null) {
			if (onConflict.equalsIgnoreCase(Conflict.UPDATE)) {
				dbJndi.setLastUpdatedTime(context.getDate());
				connectionService.editJndiConnections(dbJndi);
				handleSecurity(gConnection.getGlobalId(), jndiSecurity);
			}
		}
		else  {
			connectionService.addJndiConnections(jndi);
			handleSecurity(gConnection.getGlobalId(), jndiSecurity);
		}
		return gConnection;
	}

	private final GlobalConnections processNoSqlDatasource(DSTypeNoSQL noSql , Map<Integer, List<GlobalConnectionSecurity>> securityMap) {
		GlobalConnections gConnection = noSql.getGlobalConnections();
		GlobalDatasourceLookupDTO lookup = prepareLookup(gConnection.getName(), gConnection.getDsType(), noSql.getUrl(),noSql.getDatabaseName(),
				noSql.getUsername(),noSql.getPassword(), gConnection.getType());

		gConnection = getOrInsertGlobalConnection(lookup,gConnection);
		List<GlobalConnectionSecurity> noSqlSecurity = securityMap != null ?securityMap.get(noSql.getId()): null;
		noSql.setLastUpdatedTime(context.getDate());
		noSql.setGlobalConnections(gConnection);
		String password = noSql.getPassword() != null ? CipherUtils.encrypt(noSql.getPassword()):"";
		noSql.setPassword(password);
		DSTypeNoSQL dbNosql = connectionService.getNoSQLConnectionById(gConnection.getGlobalId());
		String onConflict = context.getRequest().getOnConflict();

		if (dbNosql != null) {
			if (onConflict.equalsIgnoreCase(Conflict.UPDATE)) {
				dbNosql.setLastUpdatedTime(context.getDate());
				connectionService.editNoSqlConnections(dbNosql);
				handleSecurity(gConnection.getGlobalId(), noSqlSecurity);
			}
		}
		else  {
			connectionService.addNoSqlConnections(noSql);
			handleSecurity(gConnection.getGlobalId(), noSqlSecurity);
		}
		return gConnection;
	}

	private final void  handleSecurity(Integer globalId , List<GlobalConnectionSecurity> security) {
		shareHandler.importGlobalConnectionPermissions(globalId, security);
	}

	@NotNull
	private GlobalConnections getOrInsertGlobalConnection(GlobalDatasourceLookupDTO lookup, GlobalConnections connection) {
		int connectionId = connection.getGlobalId();
		GlobalConnections gConnection = connectionService.getGlobalConnectionBy(lookup);
		if( gConnection == null ) {
			connection.setGlobalId(null);
			connectionService.addGlobalConnections(connection);
			gConnection = connection;
		}
		else if(Boolean.TRUE.equals(gConnection.isDeleted())) {
			context.recover(gConnection);
			connectionService.editGlobalConnections(gConnection);
		}
		context.putGlobalConnection(connectionId, gConnection);
		return gConnection;
	}

	private GlobalDatasourceLookupDTO prepareLookup(String name, String dsType, String url , String dbName,
												 String userName, String password, String type) {
		GlobalDatasourceLookupDTO lookup = new GlobalDatasourceLookupDTO();
		lookup.setDbName(dbName);
		lookup.setDsType(dsType);
		lookup.setJdbcUrl(url);
		lookup.setName(name);
		lookup.setPassword(password);
		lookup.setType(type);
		lookup.setUserName(userName);
		return lookup;
	}

}
