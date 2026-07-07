package com.helicalinsight.admin.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.google.re2j.Pattern;
import com.helicalinsight.admin.dto.EFWDConnGroovyDTO;
import com.helicalinsight.admin.dto.EFWDConnSqlJDBCDTO;
import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.dto.GlobalConnectionDTO;
import com.helicalinsight.admin.dto.HIEfwdConnSecurityDTO;
import com.helicalinsight.admin.dto.HIEfwdConnectionDTO;
import com.helicalinsight.admin.dto.HIMetadataColumnsDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionEFWDDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionGlobalDTO;
import com.helicalinsight.admin.dto.HIMetadataRelationshipsDTO;
import com.helicalinsight.admin.dto.HIMetadataSecurityDTO;
import com.helicalinsight.admin.dto.HIMetadataTableDTO;
import com.helicalinsight.admin.dto.HIMetadataViewDTO;
import com.helicalinsight.admin.dto.MetadataDatabasesDTO;
import com.helicalinsight.admin.dto.OrganizationDTO;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.admin.dto.ProfileDTO;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.dto.RoleDTO;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIEfwdConnSecurity;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIMetadataSecurity;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.admin.model.HIMetadataView;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRelationshipPositions;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.admin.dto.HIEfwdDTO;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.efw.utility.ResourceTypeIDMap;
import com.helicalinsight.resourcedb.HIResourceDTO;

@Component
public class ResourceDTOMapper {

	@Autowired
	private ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

	@Autowired
	private HIResourceServiceDB serviceDb;

	public HIResourceDTO map(HIResource resource) {

		if (resource != null) {
			HIResourceDTO dtoObject = new HIResourceDTO();
			dtoObject.setCreatedBy(resource.getCreatedBy());
			if (null != resource.getParentId()) {
				dtoObject.setParentId(resource.getParentId());
			} else {
				dtoObject.setParentId(0);
			}
			dtoObject.setResourceId(resource.getResourceId());
			dtoObject.setPath(resource.getResourceURL());
			dtoObject.setResourceURL(resource.getResourceURL());
			dtoObject.setVisible(resource.getVisible());
			dtoObject.setDeleted(resource.getDeleted());
			dtoObject.setTitle(resource.getTitle());
			dtoObject.setTitleForRecycleBin(resource.getTitle());
			String[] arr = resource.getResourceURL().split(Pattern.quote("/"));
			String name = arr[arr.length - 1];
			dtoObject.setName(name);
			dtoObject.setResourcePath(name);
			Integer createdBy = null;
			if (null != resource.getCreatedBy()) {
				createdBy = resource.getCreatedBy();
			}
			try {
			if (createdBy == Integer.valueOf(AuthenticationUtils.getUserId())) {
				dtoObject.setPermissionLevel("" + resourcePermissionLevelsHolder.ownerAccessLevel());
			} else {
				Map<Integer, Integer> securityMap = serviceDb.getSecurityMap();
				Integer permission = securityMap.get(resource.getResourceId());
				if (permission != null) {
					dtoObject.setPermissionLevel("" + permission);
				}
			}
			}
			catch (EfwException | UsernameNotFoundException ignore) {
			}
			dtoObject.setCreatedBy(createdBy);
			dtoObject.setLastModified(resource.getLastUpdatedTime().getTime());
			if (resource.getFolder()) {
				dtoObject.setExtension(resource.getResourceType().getExtension());
				dtoObject.setType("folder");
			} else {
				dtoObject.setType("file");
				dtoObject.setExtension(resource.getResourceType().getName());
			}
			return dtoObject;
		}

		return null;
	}

	public HIResource toEntity(HIResourceDTO dto) {

		if (dto == null) {
			return null;
		}

		HIResource entity = new HIResource();

		entity.setResourceId(dto.getResourceId());
		entity.setParentId(dto.getParentId());
		entity.setVisible(dto.isVisible());
		entity.setTitle(dto.getTitle());
		entity.setDeleted(dto.getDeleted());
		entity.setCreatedBy(dto.getCreatedBy());
		entity.setResourcePath(dto.getName());
		entity.setResourceURL(dto.getPath());

		if (dto.getLastModified() > 0) {
			entity.setLastUpdatedTime(new Date(dto.getLastModified()));
		}

		if (dto.getType() != null) {
			ResourceType resourceType = ResourceTypeIDMap
					.getResourceTypeById(ResourceTypeIDMap.getResourceIdByName(dto.getType()));
			if (resourceType != null) {
				entity.setResourceType(resourceType);
			}
		}

		entity.setFolder("folder".equalsIgnoreCase(dto.getType()));

		if (entity.getDeleted() == null) {
			entity.setDeleted(false);
		}

		if (entity.getVisible() == null) {
			entity.setVisible(true);
		}

		return entity;
	}

	public HIMetadataSecurityDTO map(HIMetadataSecurity security) {

		HIMetadataSecurityDTO dto = new HIMetadataSecurityDTO();

		dto.setId(security.getId());
		dto.setExpressionId(security.getExpressionId());
		dto.setType(security.getType());
		dto.setExpressionOn(security.getExpressionOn());
		dto.setExpressionName(security.getExpressionName());
		dto.setExpressionType(security.getExpressionType());
		dto.setExpressionCondition(security.getExpressionCondition());
		dto.setExpressionFilter(security.getExpressionFilter());
		dto.setAccessType(security.getAccessType());

		if (security.getHiResourceMetadata() != null) {
			dto.setMetadataId(security.getHiResourceMetadata().getId());
		}

		return dto;
	}

	public HIMetadataSecurity map(HIMetadataSecurityDTO dto) {

		if (dto == null) {
			return null;
		}

		HIMetadataSecurity entity = new HIMetadataSecurity();
		entity.setId(dto.getId());
		entity.setExpressionId(dto.getExpressionId());
		entity.setType(dto.getType());
		entity.setExpressionOn(dto.getExpressionOn());
		entity.setExpressionName(dto.getExpressionName());
		entity.setExpressionType(dto.getExpressionType());
		entity.setExpressionCondition(dto.getExpressionCondition());
		entity.setExpressionFilter(dto.getExpressionFilter());
		entity.setAccessType(dto.getAccessType());

		return entity;
	}
	
	public  HIEfwdConnectionDTO toDTO(HIEfwdConnection entity) {

	    HIEfwdConnectionDTO dto = new HIEfwdConnectionDTO();

	    dto.setId(entity.getId());
	    dto.setType(entity.getType());
	    dto.setConnectionId(entity.getConnectionId());
	    dto.setDeleted(entity.isDeleted());

	    if (entity.getHiResourceEFWD() != null) {
	        dto.setEfwdId(entity.getHiResourceEFWD().getId());
	        dto.setHiResourceEFWD(map(entity.getHiResourceEFWD()));
	    }
	    
	    return dto;
	}

	public HIMetadataConnectionDTO map(HIMetadataConnections entity) {

		HIMetadataConnectionDTO dto = new HIMetadataConnectionDTO();

		dto.setId(entity.getId());
		dto.setConnectionType(entity.getConnectionType());

		if (entity.getHiResourceMetadata() != null) {
			dto.setMetadataId(entity.getHiResourceMetadata().getId());
		}

		if (entity.getMetadataGlobalConnList() != null && !entity.getMetadataGlobalConnList().isEmpty()) {
			List<HIMetadataConnectionGlobalDTO> globalDtoList = new ArrayList<>();
			for (HIMetadataConnectionGlobal global : entity.getMetadataGlobalConnList()) {
				HIMetadataConnectionGlobalDTO globalDTO = new HIMetadataConnectionGlobalDTO();
				globalDTO.setId(global.getId());
				globalDTO.setGlobalConnections(map(global.getGlobalConnections()));
				globalDTO.setDialect(global.getDialect());
				globalDTO.setDriverClass(global.getDriverClass());
				globalDTO.setDriverClassReference(global.getDriverClassReference());
				globalDtoList.add(globalDTO);
			}
			dto.setMetadataGlobalConnList(globalDtoList);
		}

		if (entity.getMetadataConnectionEfwd() != null && !entity.getMetadataConnectionEfwd().isEmpty()) {
			List<HIMetadataConnectionEFWDDTO> efwdDtoList = new ArrayList<>();
			for (HIMetadataConnectionEFWD efwd : entity.getMetadataConnectionEfwd()) {
				HIMetadataConnectionEFWDDTO efwdDTO = new HIMetadataConnectionEFWDDTO();
				efwdDTO.setId(efwd.getId());
				efwdDTO.setConnectionId(efwd.getHiMetadataConnections().getId());
				efwdDTO.setDialect(efwd.getDialect());
				efwdDTO.setDriverClass(efwd.getDriverClass());
				efwdDTO.setDriverClassReference(efwd.getDriverClassReference());
				efwdDTO.setHiEfwdConnection(toDTO(efwd.getHiEfwdConnection()));
				efwdDtoList.add(efwdDTO);
			}
			dto.setMetadataConnectionEfwd(efwdDtoList);
		}

		if (entity.getMetadataDatabases() != null && !entity.getMetadataDatabases().isEmpty()) {
			MetadataDatabases db = entity.getMetadataDatabases().get(0);
			MetadataDatabasesDTO dbDTO = new MetadataDatabasesDTO();
			dbDTO.setId(db.getId());
			dbDTO.setCatalog(db.getCatalog());
			dbDTO.setSchema(db.getSchema());
			dbDTO.setName(db.getName());
			dbDTO.setMetadataId(db.getHiResourceMetadata().getId());
			dto.setMetadataDatabases(List.of(dbDTO));
		}

		return dto;
	}

	public HIEfwdConnSecurityDTO map(HIEfwdConnSecurity entity) {

		HIEfwdConnSecurityDTO dto = new HIEfwdConnSecurityDTO();

		dto.setId(entity.getId());
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setLastUpdatedTime(entity.getLastUpdatedTime());
		dto.setPermission(entity.getPermission());

		if (entity.getHiEfwdConnection() != null) {
			dto.setHiEfwdConnection(toDTO(entity.getHiEfwdConnection()));
		}

		if (entity.getOrgId() != null) {
			dto.setOrgId(map(entity.getOrgId()));
		}

		if (entity.getUserId() != null) {
			dto.setUserId(map(entity.getUserId()));
		}

		if (entity.getRoleId() != null) {
			dto.setRoleId(map(entity.getRoleId()));
		}

		return dto;
	}

	public OrganizationDTO map(Organization organization) {
		if (organization == null)
			return null;

		OrganizationDTO dto = new OrganizationDTO();
		dto.setOrg_name(organization.getOrg_name());
		dto.setOrg_desc(organization.getOrg_desc());
		dto.setDeleted(organization.isDeleted());
		dto.setId(organization.getId());		
		return dto;
	}

	public Organization map(OrganizationDTO dto) {
		if (dto == null)
			return null;
		Organization organization = new Organization();
		organization.setOrg_name(dto.getOrg_name());
		organization.setOrg_desc(dto.getOrg_desc());
		organization.setDeleted(dto.getDeleted());
		organization.setId(dto.getId());
		return organization;
	}

	public UserDTO map(User user) {

		if (user == null)
			return null;

		UserDTO dto = new UserDTO();
		dto.setUsername(user.getUsername());
		dto.setPassword(user.getPassword());
		dto.setOrg_id(user.getOrg_id());
		dto.setOrganization(map(user.getOrganization()));
		dto.setIsExternallyAuthenticated(user.getIsExternallyAuthenticated());
		dto.setEnabled(user.isEnabled());
		dto.setEmailAddress(user.getEmailAddress());
		dto.setDeleted(user.isDeleted());
		dto.setId(user.getId());
		dto.setRoles(Optional.ofNullable(user.getRoles()).orElse(Collections.emptyList()).stream()
				.map(role -> map(role)).toList());

		dto.setProfile(Optional.ofNullable(user.getProfile()).orElse(Collections.emptyList()).stream()
				.map(profile -> map(profile)).toList());

		return dto;
	}

	public User map(UserDTO dto) {

		if (dto == null)
			return null;

		User user = new User();
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		user.setOrg_id(dto.getOrg_id());
		user.setOrganization(map(dto.getOrganization()));
		user.setIsExternallyAuthenticated(dto.getIsExternallyAuthenticated());
		user.setEnabled(dto.isEnabled());
		user.setEmailAddress(dto.getEmailAddress());
		user.setDeleted(dto.getDeleted());
		user.setId(dto.getId());
		user.setRoles(Optional.ofNullable(dto.getRoles()).orElse(Collections.emptyList()).stream()
				.map(role -> map(role)).toList());

		user.setProfile(Optional.ofNullable(dto.getProfile()).orElse(Collections.emptyList()).stream()
				.map(profile -> map(profile)).toList());

		return user;
	}

	public RoleDTO map(Role role) {

		if (role == null)
			return null;

		RoleDTO dto = new RoleDTO();
		dto.setOrg_id(role.getOrg_id());
		dto.setRole_name(role.getRole_name());
		dto.setId(role.getId());
		return dto;
	}

	public Role map(RoleDTO dto) {
		if (dto == null)
			return null;

		Role role = new Role();
		role.setOrg_id(dto.getOrg_id());
		role.setRole_name(dto.getRole_name());
		role.setId(dto.getId());
		return role;
	}

	public ProfileDTO map(Profile entity) {

		if (entity == null) {
			return null;
		}

		ProfileDTO dto = new ProfileDTO();

		dto.setId(entity.getId());
		dto.setProfile_name(entity.getProfile_name());
		dto.setProfile_value(entity.getProfile_value());
		return dto;
	}

	public Profile map(ProfileDTO dto) {

		if (dto == null) {
			return null;
		}

		Profile entity = new Profile();
		entity.setId(dto.getId());
		entity.setProfile_name(dto.getProfile_name());
		entity.setProfile_value(dto.getProfile_value());
		return entity;
	}

	public HIMetadataTables map(HIMetadataTableDTO dto) {
		if (dto == null)
			return null;

		HIMetadataTables table = new HIMetadataTables();
		table.setOriginalName(dto.getOriginalName());
		table.setTableAliasName(dto.getTableAliasName());
		table.setTableName(dto.getTableName());
		table.setView(dto.getView());

		List<HIMetadataColumns> columnList = dto.getColumnsList().stream().map(column -> map(column)).toList();

		table.setColumnsList(columnList);

		return table;
	}

	public HIMetadataTableDTO map(HIMetadataTables table) {

		if (table == null)
			return null;

		HIMetadataTableDTO dto = new HIMetadataTableDTO();
		dto.setId(table.getId());

		List<HIMetadataColumnsDTO> columnList = table.getColumnsList().stream().map(column -> map(column)).toList();
		dto.setColumnsList(columnList);
		dto.setView(table.getView());
		dto.setOriginalName(table.getOriginalName());
		dto.setTableAliasName(table.getTableAliasName());
		dto.setTableName(table.getTableName());
		return dto;
	}

	public HIMetadataViewDTO map(HIMetadataView view) {

		if (view == null)
			return null;
		HIMetadataViewDTO dto = new HIMetadataViewDTO();
		dto.setId(view.getId());
		dto.setHasStoredProcedure(view.getHasStoredProcedure());
		dto.setViewAlias(view.getViewAlias());
		dto.setViewId(view.getViewId());
		dto.setId(view.getId());
		dto.setViewName(view.getViewName());
		dto.setViewQuery(view.getViewQuery());
		dto.setViewType(view.getViewType());
		return dto;
	}

	public HIMetadataView map(HIMetadataViewDTO view) {

		if (view == null)
			return null;
		HIMetadataView entity = new HIMetadataView();
		entity.setId(view.getId());
		entity.setHasStoredProcedure(view.getHasStoredProcedure());
		entity.setViewAlias(view.getViewAlias());
		entity.setViewId(view.getViewId());
		entity.setViewName(view.getViewName());
		entity.setViewQuery(view.getViewQuery());
		entity.setViewType(view.getViewType());
		return entity;
	}

	public HIMetadataColumnsDTO map(HIMetadataColumns entity) {

		if (entity == null)
			return null;

		HIMetadataColumnsDTO dto = new HIMetadataColumnsDTO();
		dto.setColumnAliasName(entity.getColumnAliasName());
		dto.setColumnName(entity.getColumnName());
		dto.setColumn_type(entity.getColumn_type());
		dto.setDefaultFunction(entity.getDefaultFunction());
		dto.setId(entity.getId());
		dto.setOriginalName(entity.getOriginalName());
		dto.setTableId(entity.getHiMetadataTables().getId());
		return dto;
	}

	public HIMetadataColumns map(HIMetadataColumnsDTO dto) {

		if (dto == null)
			return null;

		HIMetadataColumns entity = new HIMetadataColumns();
		entity.setColumn_type(dto.getColumn_type());
		entity.setColumnAliasName(dto.getColumnAliasName());
		entity.setColumnName(dto.getColumnName());
		entity.setDefaultFunction(dto.getDefaultFunction());
		entity.setOriginalName(dto.getOriginalName());
		return entity;
	}

	public HIMetadataRelationships map(HIMetadataRelationshipsDTO dto) {

		if (dto == null) {
			return null;
		}

		HIMetadataRelationships entity = new HIMetadataRelationships();
		entity.setId(dto.getId());
		entity.setJoinType(dto.getJoinType());
		entity.setOperator(dto.getOperator());
		entity.setExternal(dto.isExternal());

		HIRelationshipPositions position = new HIRelationshipPositions();
		position.setPosition(dto.getPosition());
		entity.setJoinsPositions(position);

		return entity;
	}

	public HIMetadataRelationshipsDTO map(HIMetadataRelationships entity) {

		if (entity == null) {
			return null;
		}

		HIMetadataRelationshipsDTO dto = new HIMetadataRelationshipsDTO();
		dto.setId(entity.getId());
		dto.setJoinType(entity.getJoinType());
		dto.setOperator(entity.getOperator());
		dto.setExternal(entity.getExternal());
		dto.setPosition(entity.getJoinsPositions().getPosition());
		dto.setLeftMetadataColumns(map(entity.getLeftMetadataColumns()));
		dto.setRightMetadataColumns(map(entity.getRightMetadataColumns()));
		return dto;
	}

	public HIMetadataConnectionGlobal map(HIMetadataConnectionGlobalDTO dto) {
		if (dto == null)
			return null;
		HIMetadataConnectionGlobal global = new HIMetadataConnectionGlobal();
		global.setDialect(dto.getDialect());
		global.setDriverClass(dto.getDriverClass());
		global.setDriverClassReference(dto.getDriverClassReference());
		return global;
	}

	public HIMetadataConnectionGlobalDTO map(HIMetadataConnectionGlobal global) {
		if (global == null)
			return null;

		HIMetadataConnectionGlobalDTO dto = new HIMetadataConnectionGlobalDTO();
		dto.setHiMetadataConnections(map(global.getHiMetadataConnections()));
		dto.setDialect(global.getDialect());
		dto.setDriverClass(global.getDriverClass());
		dto.setDriverClassReference(global.getDriverClassReference());
		dto.setId(global.getId());
		return dto;
	}

	public HIMetadataConnectionEFWD map(HIMetadataConnectionEFWDDTO dto) {
		if (dto == null)
			return null;
		HIMetadataConnectionEFWD efwd = new HIMetadataConnectionEFWD();
		efwd.setDialect(dto.getDialect());
		efwd.setDriverClass(dto.getDriverClass());
		efwd.setDriverClassReference(dto.getDriverClassReference());
		return efwd;
	}

	public HIMetadataConnectionEFWDDTO map(HIMetadataConnectionEFWD efwd) {
		if (efwd == null)
			return null;
		HIMetadataConnectionEFWDDTO dto = new HIMetadataConnectionEFWDDTO();
		dto.setConnectionId(efwd.getHiMetadataConnections().getId());
		dto.setDialect(efwd.getDialect());
		dto.setDriverClass(efwd.getDriverClass());
		dto.setDriverClassReference(efwd.getDriverClassReference());
		dto.setId(efwd.getId());
		return dto;
	}

	public MetadataDatabasesDTO map(MetadataDatabases database) {

		MetadataDatabasesDTO dto = new MetadataDatabasesDTO();
		dto.setCatalog(database.getCatalog());
		dto.setName(database.getName());
		dto.setSchema(database.getSchema());

		dto.setMetadataRelationShipList(Optional.ofNullable(database.getMetadataRelationShipList()).orElse(Collections.emptyList())
				.stream().map(relation -> map(relation)).toList());

		dto.setMetadataTablesList(Optional.ofNullable(database.getMetadataTablesList()).orElse(Collections.emptyList()).stream()
				.map(table -> map(table)).toList());

		dto.setMetadataViewList(Optional.ofNullable(database.getMetadataViewList()).orElse(Collections.emptyList()).stream()
				.map(view -> map(view)).toList());

		return dto;

	}

	public HIEFWD map(HIEfwdDTO dto) {
		HIEFWD hiefwd = new HIEFWD();
		hiefwd.setCreatedBy(dto.getCreatedBy());
		hiefwd.setCreatedDate(new Date());

		HIResource resource = new HIResource();
		resource.setResourcePath(dto.getResourcePath());
		resource.setResourceURL(dto.getResourceUrl());
		resource.setTitle(dto.getTitle());
		resource.setDeleted(dto.getIsDeleted());
		resource.setCreatedBy(dto.getCreatedBy());
		resource.setResourceId(dto.getResourceId());

		hiefwd.setParentResource(resource);
		hiefwd.setLastUpdatedTime(new Date());
		return hiefwd;
	}

	public HIEfwdDTO map(HIEFWD entity) {

		HIEfwdDTO dto = new HIEfwdDTO();
		HIResource resource = entity.getParentResource();
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setIsDeleted(resource.isDeleted());
		dto.setResourceId(resource.getResourceId());
		dto.setResourcePath(resource.getResourcePath());
		dto.setResourceUrl(resource.getResourceURL());
		dto.setTitle(resource.getTitle());
		
		HIResource parentResource = entity.getParentResource();
		
		if ( parentResource != null ) {
			dto.setParentResource(map(parentResource));
		}

		return dto;
	}

	public HIEfwdConnection map(EfwdConnDTO dto) {
		HIEfwdConnection entity = new HIEfwdConnection();
		entity.setDeleted(dto.isDeleted());
		entity.setType(dto.getType());
		return entity;
	}

	public EfwdConnDTO map(HIEfwdConnection connection) {

		if (connection == null)
			return null;

		EfwdConnDTO dto = new EfwdConnDTO();
		dto.setDeleted(connection.isDeleted());
		dto.setId(connection.getId());

		HIResource resource = connection.getHiResourceEFWD().getParentResource();
		HIEfwdDTO resourceDto = new HIEfwdDTO();
		resourceDto.setResourceId(resource.getResourceId());
		resourceDto.setResourceUrl(resource.getResourceURL());
		resourceDto.setCreatedBy(resource.getCreatedBy());
		resourceDto.setIsDeleted(resource.isDeleted());
		dto.setResource(resourceDto);
		return dto;
	}

	public EFWDConnSqlJDBC toSqlEntity(PlainConnDTO dto) {

		if (dto == null) {
			return null;
		}

		EFWDConnSqlJDBC entity = new EFWDConnSqlJDBC();

		entity.setId(dto.getId());
		entity.setDriver(dto.getDriver());
		entity.setUrl(dto.getUrl());
		entity.setUserName(dto.getUserName());
		entity.setPass(dto.getPass());
		entity.setDatabase(dto.getDatabase());
		entity.setName(dto.getName());
		return entity;
	}

	public EFWDConnGroovy toGroovyEntity(PlainConnDTO dto) {

		if (dto == null) {
			return null;
		}

		EFWDConnGroovy entity = new EFWDConnGroovy();

		entity.setId(dto.getId());
		entity.setDriver(dto.getDriver());
		entity.setUrl(dto.getUrl());
		entity.setUserName(dto.getUserName());
		entity.setPass(dto.getPass());
		entity.setDatabase(dto.getDatabase());
		entity.setName(dto.getName());
		entity.setCondition(dto.getCondition());

		return entity;
	}
	
	public RecycleBinDTO map(HIRecycleBin bin) {
		RecycleBinDTO dto = new RecycleBinDTO();
		dto.setRecycleBinId(bin.getId());
		
		if(bin.getCreatedBy() != null) {
			dto.setCreatedBy(bin.getCreatedBy().getId());
		}
		
		dto.setDeletedBy(bin.getDeletedBy().getUsername());
		dto.setDeletedOn(bin.getDeletedOn());
		dto.setType(bin.getRecycleBinType());
		
		if ( bin.getHiRecycleBinHIResourceDB() != null ) {
		    	HIResource resource = bin.getHiRecycleBinHIResourceDB().getHiResource();
		        dto.setResourceId(resource.getResourceId());
		        dto.setResourceTypeId(resource.getResourceTypeId());
		        dto.setResourceUrl(resource.getResourceURL());
		        dto.setTitle(resource.getTitle());
		}
		if ( bin.getHiRecycleBinDsGlobalConnections() != null) {
		    	dto.setResourceId(bin.getHiRecycleBinDsGlobalConnections()
		    			.getGlobalConnection().getGlobalId());
		}
		
		if (bin.getHiRecycleBinHIEfwdConnection() != null ) {
		    	dto.setResourceId(bin.getHiRecycleBinHIEfwdConnection()
		    			.getEfwdConnection().getId());
		}
		 if ( bin.getHiRecycleBinHUsers() != null ) {
		        dto.setResourceId(bin.getHiRecycleBinHUsers()
		                    .getUser()
		                    .getId());
		 }
		  if ( bin.getHiRecycleBinOrganization() != null ) {
		        dto.setResourceId(bin.getHiRecycleBinOrganization()
		                    .getOrganization()
		                    .getId());
		  }
		return dto;
	}
	
	public EFWDConnGroovyDTO map(EFWDConnGroovy entity) {

        if (entity == null) {
            return null;
        }

        EFWDConnGroovyDTO dto = new EFWDConnGroovyDTO();

        dto.setId(entity.getId());
        dto.setHiEfwdConnection(toDTO(entity.getHiEfwdConnection()));
        dto.setDriver(entity.getDriver());
        dto.setUrl(entity.getUrl());
        dto.setUserName(entity.getUserName());
        dto.setPass(entity.getPass());
        dto.setCondition(entity.getCondition());
        dto.setDatabase(entity.getDatabase());
        dto.setName(entity.getName());

        return dto;
    }

    public  EFWDConnGroovy map(EFWDConnGroovyDTO dto) {

        if (dto == null) {
            return null;
        }

        EFWDConnGroovy entity = new EFWDConnGroovy();
        entity.setId(dto.getId());
        entity.setDriver(dto.getDriver());
        entity.setUrl(dto.getUrl());
        entity.setUserName(dto.getUserName());
        entity.setPass(dto.getPass());
        entity.setCondition(dto.getCondition());
        entity.setDatabase(dto.getDatabase());
        entity.setName(dto.getName());

        return entity;
    }
    
    public  EFWDConnSqlJDBCDTO map(EFWDConnSqlJDBC entity) {

        if (entity == null) {
            return null;
        }

        EFWDConnSqlJDBCDTO dto = new EFWDConnSqlJDBCDTO();

        dto.setId(entity.getId());
        dto.setHiEfwdConnection(toDTO(entity.getHiEfwdConnection()));
        dto.setDriver(entity.getDriver());
        dto.setUrl(entity.getUrl());
        dto.setUserName(entity.getUserName());
        dto.setPass(entity.getPass());
        dto.setDatabase(entity.getDatabase());
        dto.setName(entity.getName());

        return dto;
    }

    public  EFWDConnSqlJDBC map(EFWDConnSqlJDBCDTO dto) {

        if (dto == null) {
            return null;
        }

        EFWDConnSqlJDBC entity = new EFWDConnSqlJDBC();

        entity.setId(dto.getId());
        entity.setDriver(dto.getDriver());
        entity.setUrl(dto.getUrl());
        entity.setUserName(dto.getUserName());
        entity.setPass(dto.getPass());
        entity.setDatabase(dto.getDatabase());
        entity.setName(dto.getName());
        return entity;
    }
    
    public  GlobalConnectionDTO map(GlobalConnections entity) {

        if (entity == null) {
            return null;
        }

        GlobalConnectionDTO dto = new GlobalConnectionDTO();

        dto.setGlobalId(entity.getGlobalId());
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setVendorName(entity.getVendor());
        dto.setBaseType(entity.getBaseType());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastUpdatedTime(entity.getLastUpdatedTime());
        dto.setDsTypeClass(entity.getDsType());
        dto.setIsMigrated(entity.getIsMigrated());

        return dto;
    }

    public  GlobalConnections toEntity(GlobalConnectionDTO dto) {

        if (dto == null) {
            return null;
        }

        GlobalConnections entity = new GlobalConnections();
        
        entity.setGlobalId(dto.getId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setVendor(dto.getVendorName());
        entity.setBaseType(dto.getBaseType());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setLastUpdatedTime(dto.getLastUpdatedTime());
        entity.setDsType(dto.getDsTypeClass());
        entity.setIsMigrated(dto.getIsMigrated());

        return entity;
    }
    
	public HIEfwdConnection toEntity(HIEfwdConnectionDTO dto) {

		if (dto == null) {
			return null;
		}

		HIEfwdConnection entity = new HIEfwdConnection();

		entity.setId(dto.getId());
		entity.setType(dto.getType());
		entity.setConnectionId(dto.getConnectionId());
		entity.setDeleted(dto.isDeleted());

		if (dto.getEfwdConnSqlJDBC() != null) {
			entity.setEfwdConnSqlJDBC(dto.getEfwdConnSqlJDBC().stream().map(con -> map(con)).toList());
			entity.getEfwdConnSqlJDBC().forEach(jdbc -> jdbc.setHiEfwdConnection(entity));
		}

		if (dto.getEfwdConnGroovy() != null) {
			entity.setEfwdConnGroovy(dto.getEfwdConnGroovy().stream().map(con -> map(con)).toList());
			entity.getEfwdConnGroovy().forEach(groovy -> groovy.setHiEfwdConnection(entity));
		}
		return entity;
	}

}
