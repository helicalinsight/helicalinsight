package com.helicalinsight.admin.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.helicalinsight.admin.model.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.helicalinsight.admin.dao.HIRecycleBinDao;
import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.core.request.RecycleBinDatasource;
import com.helicalinsight.core.request.RecycleBinItem;
import com.helicalinsight.core.request.RecycleBinItem.Data;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.core.request.RecycleBinUser;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class HIRecycleBinDaoImpl implements HIRecycleBinDao {

	@Autowired
	private SessionFactory factory;

	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	private HIResourceDBDAO hiResourceDao;

	@Autowired
	private GlobalConnectionService globalConnectionService;

	@Qualifier("userDetailsService")
	@Autowired
	private UserService userService;

	@Autowired
	private ResourceTypeServiceDB resourceTypeService;

	@Autowired
	private EFWDConnectionService efwdConnectionService;
	
	@Autowired
	private HIResourceConstituentMappingService mappingService;
	
	@Autowired
	private ResourceDTOMapper hiResourceToDtoMapper;



	private static final String DATA_SOURCES="dataSources";
	private static final String RESOURCES = "resources";
	private static final String USERS = "users";


	@Override
	public boolean save(HIRecycleBin recycleBin) {
		try {
			factory.getCurrentSession().persist(recycleBin);
			return true;
		} catch (Exception e) {
			log.error("Error occurred while saving recyclebin object");
			return false;
		}
	}

	@Override
	public boolean delete(Long id) {
		try {
			HIRecycleBin bin = findHIRecycleBinById(id);
			delete(bin);
			return true;
		} catch (Exception e) {
			log.info("Error occurred while deleting RecycleBin entry. root cause : {}",e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean delete(HIRecycleBin bin) {

		try {
			Long recycleBinId = bin.getId();

			Session session = factory.getCurrentSession();

			if (bin.getHiRecycleBinHIResourceDB() != null) {
				session.createMutationQuery("delete from HIRecycleBinHIResourceDB r where r.recycleBin.id = :id")
						.setParameter("id", recycleBinId).executeUpdate();
			}

			if (bin.getHiRecycleBinDsGlobalConnections() != null) {
				session.createMutationQuery("delete from HIRecycleBinDSGlobalConnections r where r.recycleBin.id = :id")
						.setParameter("id", recycleBinId).executeUpdate();
			}

			if (bin.getHiRecycleBinHIEfwdConnection() != null) {
				session.createMutationQuery("delete from HIRecycleBinHIEfwdConnection r where r.recycleBin.id = :id")
						.setParameter("id", recycleBinId).executeUpdate();
			}

			if (bin.getHiRecycleBinHUsers() != null) {
				session.createMutationQuery("delete from HIRecycleBinHUsers r where r.recycleBin.id = :id")
						.setParameter("id", recycleBinId).executeUpdate();
			}
			if (bin.getHiRecycleBinOrganization() != null) {
				session.createMutationQuery("delete from HIRecycleBinOrganization r where r.recycleBin.id = :id")
						.setParameter("id", recycleBinId).executeUpdate();
			}
			session.createMutationQuery("delete from HIRecycleBin r where r.id = :id").setParameter("id", recycleBinId)
					.executeUpdate();

			return true;
		} catch (Exception e) {
			log.info("Error occurred while deleting RecycleBin entry. root cause : {}", e.getMessage());
			return false;
		}
	}

	@Override
	public List<RecycleBinItem> list() {
		List<RecycleBinItem> binList = new ArrayList<>();
		Map<Long,RecycleBinDTO> idMap=new HashMap<>();
		RecycleBinType type;
		try {
			List<RecycleBinDTO> binItems=getAllRecycleBinItems();
			getAllLinkedData(idMap);
			if (!binItems.isEmpty()) {
				for(RecycleBinDTO dto : binItems){
					RecycleBinItem binItem=new RecycleBinItem();
					binItem.setRecycleBinId(dto.getRecycleBinId());
					binItem.setDeletedOn(dto.getDeletedOn());
					binItem.setDeletedBy(""+dto.getDeletedBy());
					type=dto.getType();
					setResourceNameByRecycleBinType(type,binItem,idMap);
					binList.add(binItem);
				}

			}
		} catch (Exception e) {
			if( log.isErrorEnabled() || log.isDebugEnabled() ) {
				e.printStackTrace();
			}
			log.error("Error occurred while fetching list of RecycleBin Items");
		}
		return binList;
	}

	
	

	private void getAllLinkedData(Map<Long,RecycleBinDTO> idMap) {
		int count=0;
		Session currentSession = factory.getCurrentSession();
		RecycleBinType[] values=RecycleBinType.values();
		while (count< values.length) {
			String hql = "";
			switch (values[count]) {
				case HI_RESOURCE_DB:
					hql = """ 
							SELECT 
							resource.resourceId as resourceId, 
							resource.title as title,
							resource.resourceURL as resourceUrl, 
							resource.createdBy as createdBy, 
							resource.resourceTypeId as resourceTypeId,
							rbin.id as recycleBinId 
							FROM HIRecycleBinHIResourceDB rb 
							JOIN rb.hiResource as resource 
							JOIN rb.recycleBin as rbin
						""";
					break;
				case DS_GLOBAL_CONNECTIONS:
					hql = "SELECT " +
							"gc.globalId as resourceId, " +
							"gc.name as name, " +
							"gc.createdBy as createdBy, " +
							"rbin.id as recycleBinId " +
							"FROM HIRecycleBinDSGlobalConnections rb " +
							"JOIN rb.globalConnection gc " +
							"JOIN rb.recycleBin as rbin";
					break;
				case HI_EFWD_CONNECTION:
					hql = "SELECT " +
							"ec.id as resourceId, " +
							"rbin.id as recycleBinId " +
							"FROM HIRecycleBinHIEfwdConnection rb " +
							"JOIN rb.efwdConnection ec " +
							"JOIN rb.recycleBin as rbin";
					break;
				case H_USERS:
					hql = "SELECT " +
							"u.id as resourceId, " +
							"u.username as name, " +
							"rbin.id as recycleBinId " +
							"FROM HIRecycleBinHUsers rb " +
							"JOIN rb.user u " +
							"JOIN rb.recycleBin as rbin";
					break;
				case ORGANIZATION:
					hql = "SELECT " +
							"o.id as resourceId, " +
							"o.org_name as name, " +
							"rbin.id as recycleBinId " +
							"FROM HIRecycleBinOrganization rb " +
							"JOIN rb.organization o " +
							"JOIN rb.recycleBin as rbin";
					break;

				default:
					throw new EfwServiceException("Invalid recyclebin type.");
			}

			org.hibernate.query.Query query = currentSession.createQuery(hql);
			query.setResultTransformer(Transformers.aliasToBean(RecycleBinDTO.class));
			List allLinkedData = query.list();
			if (!allLinkedData.isEmpty()) {
				for (Object e : allLinkedData) {
					RecycleBinDTO dto = (RecycleBinDTO) e;
					idMap.put(dto.getRecycleBinId(), dto);
				}
			}
			count++;
		}
	}

	public List<RecycleBinDTO> getAllRecycleBinItems() {
		Session currentSession = factory.getCurrentSession();
		String orgId = AuthenticationUtils.getOrganizationId();
		StringBuilder hql = new StringBuilder("SELECT new com.helicalinsight.admin.dto.RecycleBinDTO(r.id as recycleBinId, r.recycleBinType as type, r.deletedOn as deletedOn , u.username as deletedBy)"
				+ " FROM HIRecycleBin r JOIN r.deletedBy u ");
		if(StringUtils.isNotBlank(orgId)) {
			hql.append(" WHERE  r.orgId.id = :orgId or ( r.orgId.id is null and r.createdBy is null and  r.recycleBinType not in (:params))");
		}
		Query query = currentSession.createQuery(hql.toString());

		if(orgId != null ) {
			query.setParameter("orgId", Integer.valueOf(orgId));
			query.setParameterList("params", List.of(RecycleBinType.ORGANIZATION ,RecycleBinType.H_USERS));
		}
		return query.list();
	}


	@Override
	public HIRecycleBin findHIRecycleBinByResourceId(Integer resourceId) {
		Session session = factory.getCurrentSession();
		try {
			SelectionQuery<HIRecycleBin> query = session.createSelectionQuery("FROM HIRecycleBin where hiRecycleBinHIResourceDB.hiResource.resourceId =:resourceId",HIRecycleBin.class);
			query.setParameter("resourceId", resourceId);
			return query.uniqueResult();
		}
		catch (Exception e) {
			log.error("Error occurred while fetching HIRecycleBin by resourceId");
		}
		return null;
	}
	
	
	@Override
	public Map<Integer, RecycleBinDTO> findHIRecycleBinsByResourceIds(List<Integer> resourceIds) {
			Map<Integer, RecycleBinDTO> binsByResourceId = new HashMap<>();
			if (resourceIds == null || resourceIds.isEmpty()) {
				return binsByResourceId;
			}
			try {
				Session session = factory.getCurrentSession();
			
			String hql = """
						SELECT resource.resourceId as resourceId, 
						rbin.id as recycleBinId,
						resource.resourceTypeId as resourceTypeId 
						FROM HIRecycleBinHIResourceDB rb 
						JOIN rb.hiResource as resource 
						JOIN rb.recycleBin as rbin 
						WHERE resource.resourceId in (:resourceIds)
					""";
			
			Query query = session.createQuery(hql);
			query.setParameterList("resourceIds", resourceIds);
			query.setResultTransformer(Transformers.aliasToBean(RecycleBinDTO.class));
			for (Object row : query.list()) {
				RecycleBinDTO dto = (RecycleBinDTO) row;
				binsByResourceId.put(dto.getResourceId(), dto);
			}
			
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return binsByResourceId;
	}
	
	

	@Override
	public boolean deleteHIRecycleBinByResourceId(Integer resourceId) {
		HIRecycleBin bin =  findHIRecycleBinByResourceId(resourceId);
		if( bin != null ) {
			return delete(bin.getId());
		}
		return true;
	}
	
	
	private String buildQuery(RecycleBinType type) {
	    
	    String base = """
	    	    select new com.helicalinsight.admin.dto.RecycleBinDTO(
	    	        rb.id,
	    	        rb.recycleBinType,
	    	        rb.deletedOn,
	    	        rb.deletedBy.username,
	    	""";

	    String join = "";
	    String projectionTail = "";

	    switch (type) {

	        case HI_RESOURCE_DB -> {
	        	join = "left join rb.hiRecycleBinHIResourceDB r ";
	            projectionTail = """ 
	                    r.hiResource.id, 
	                    r.hiResource.resourcePath, 
	                    r.hiResource.title,
	                    r.hiResource.resourceURL,
	                  """;
	        }

	        case DS_GLOBAL_CONNECTIONS -> {
	        	join = "left join rb.hiRecycleBinDsGlobalConnections g";
	        	projectionTail = """
	            		g.globalConnection.id,
	                    g.globalConnection.name,
	                    '', 
	                   '' ,
	            		""";
	        }

	        case H_USERS -> {
	            join = " left join rb.hiRecycleBinHUsers u ";
	            projectionTail = """
	            		u.user.id,
	            		u.user.username,
	            		'',
	            		'',
	            		""";
	        }

	        case ORGANIZATION -> {
	            join = " left join rb.hiRecycleBinOrganization org2 ";
	            projectionTail ="""
	            		 org2.organization.id,
	            		 org2.organization.org_name,
	            		 '',
	            		 '',
	            		""";
	        }

	        case HI_EFWD_CONNECTION -> {
	            join = " left join rb.hiRecycleBinHIEfwdConnection e ";
	            projectionTail = """
	            			e.efwdConnection.id,
	            			'',
	            			'',
	            			'',
	            		""";
	        }
	        
	        default -> throw new IllegalArgumentException(
	                "Unsupported recycle bin type: " + type);
	    }

	    return base + projectionTail + """
	    		rb.createdBy.id )
	        from HIRecycleBin rb
	    """ + join + " where rb.id = :id";
	}
	
	@Override
	public HIRecycleBin findHIRecycleBinById(Long id) {
		String hql = "FROM HIRecycleBin  where id = :id";
		SelectionQuery<HIRecycleBin> query  = factory.getCurrentSession().createSelectionQuery(hql,HIRecycleBin.class);
		query.setParameter("id", id);
		HIRecycleBin bin =   query.uniqueResult();
		if(bin == null) {
			throw new ResourceNotFoundException("Resource not present in RecycleBin");
		}
		return bin;
	}

	@Override
	public RecycleBinDTO getHIRecycleBinById(Long id) {
		String hql = """
				select recycleBinType
				FROM HIRecycleBin  where id = :id
				""" ;
		Session session  = factory.getCurrentSession();
		SelectionQuery<RecycleBinType> query  = session.createSelectionQuery(hql,RecycleBinType.class);
		query.setParameter("id", id);
		RecycleBinType binType =   query.uniqueResult();
		String binQuery = buildQuery(binType);
		SelectionQuery<RecycleBinDTO> selectionQuery = session.createSelectionQuery(binQuery, RecycleBinDTO.class);
		selectionQuery.setParameter("id", id);
		RecycleBinDTO dto = selectionQuery.uniqueResult();
		return dto;
	}
	
	@Override
	public HIRecycleBin findHIRecycleBinByIdPlain(Long id) {
		String hql = "FROM HIRecycleBin  where id = :id";
		Query query  = factory.getCurrentSession().createQuery(hql);
		query.setParameter("id", id);
		HIRecycleBin bin =  (HIRecycleBin) query.uniqueResult();

		return bin;
	}

	@Override
	public List<HIRecycleBin> findAll() {
		String orgId = AuthenticationUtils.getOrganizationId();
		Session currentSession = factory.getCurrentSession();
		StringBuilder hql = new StringBuilder("FROM HIRecycleBin r");
		if(StringUtils.isNotBlank(orgId)) {
			hql.append(" WHERE  r.orgId.id = :orgId or ( r.orgId.id is null and r.createdBy is null and  r.recycleBinType not in (:params))");
		}
		Query query = currentSession.createQuery(hql.toString());

		if(orgId != null ) {
			query.setParameter("orgId", Integer.valueOf(orgId));
			query.setParameterList("params", List.of(RecycleBinType.ORGANIZATION ,RecycleBinType.H_USERS));
		}
		return query.list();
	}

	private final void setResourceNameByRecycleBinType(RecycleBinType type,RecycleBinItem item,Map<Long,RecycleBinDTO> idMap) {
		RecycleBinItem.Data data=new Data();
		RecycleBinDTO recycleBinDto=idMap.get(item.getRecycleBinId());;
		switch (type) {
			case HI_RESOURCE_DB:
				data.setName(recycleBinDto.getTitle());
				data.setPath(recycleBinDto.getResourceUrl());
				data.setId(recycleBinDto.getResourceId());
				data.setOwnerId(recycleBinDto.getCreatedBy());
				item.setData(data);
				ResourceType resourceType=resourceTypeService.getResourceType(recycleBinDto.getResourceTypeId());
				String fileOrFolder = "folder".equalsIgnoreCase(resourceType.getName()) ? "Folders" : "Files";
				item.setRecycleBinType(fileOrFolder);
				break;
			case DS_GLOBAL_CONNECTIONS:
				data.setName(recycleBinDto.getName());
				data.setId(recycleBinDto.getResourceId());
				data.setOwnerId(recycleBinDto.getCreatedBy());
				item.setData(data);
				item.setRecycleBinType("Datasources[Managed]");
				break;

			case HI_EFWD_CONNECTION:
				HIEfwdConnection plainConnection = efwdConnectionService.findConnectionById(idMap.get(item.getRecycleBinId()).getResourceId().toString(), false);
				String name = !plainConnection.getEfwdConnGroovy().isEmpty() ? plainConnection.getEfwdConnGroovy().get(0).getName()
						: plainConnection.getEfwdConnSqlJDBC().get(0).getName();
				data.setName(name);
				data.setId(plainConnection.getId());
				data.setDir(plainConnection.getHiResourceEFWD().getParentResource().getResourceURL());
				item.setData(data);
				item.setRecycleBinType("Datasources[Efwd]");
				break;
			case H_USERS:
				data.setName(recycleBinDto.getName());
				data.setId(recycleBinDto.getResourceId());
				item.setData(data);
				item.setRecycleBinType("Users");
				break;
			case ORGANIZATION:
				data.setName(recycleBinDto.getName());
				data.setId(recycleBinDto.getResourceId());
				item.setData(data);
				item.setRecycleBinType("Organizations");
				break;
			default:
				throw new EfwServiceException("Invalid") ;
		}
	}

	@Override
	public Map<String,List<Object>> findAllResourceOfRecycleBinItem(Long recycleBinId) {


		RecycleBinDTO bin = getHIRecycleBinById(recycleBinId); 
		
		if ( bin == null) return Collections.emptyMap();
		
		switch(bin.getType()) {
			case HI_RESOURCE_DB:
				 HIResource hiResource = serviceDb.findResourceById(bin.getResourceId(),false);
				 return prepareHIResources(hiResource,false,hiResource.getCreatedBy());
			case DS_GLOBAL_CONNECTIONS:
				 return  getGlobalConnectionResources(bin.getResourceId(),null);
			case HI_EFWD_CONNECTION:
				 return  getEfwdConnectionResources(bin.getResourceId(),null);
			case H_USERS:
				 return findAllResourceOfUser(bin.getResourceId());
			case ORGANIZATION:
				 return  findAllResourceOfOrganization(bin.getResourceId());
			default:
				throw new EfwServiceException("Invalid Resource");
		}
	}

	@SuppressWarnings("unchecked")
	private <T>  Map<String,List<T>> findAllResourceOfOrganization(Integer orgId) {
		Map<String,List<T>> map = new HashMap<>();
		LimitOffsetModel limitOffsetModel = new LimitOffsetModel();
		limitOffsetModel.setSearchPhrase(ApplicationProperties.getInstance().getNullValue());
		limitOffsetModel.setOnlySuperOrganization(true);
		List<User> usersOfOrganization = userService.getAllUsersOfOrganization(orgId);
		List<RecycleBinUser> recycleBinUsers = new ArrayList<>();
		for(User user : usersOfOrganization ) {
			RecycleBinUser rbUser = new RecycleBinUser();
			rbUser.setName(user.getUsername());
			rbUser.setDeleted(user.isDeleted());
			rbUser.setId(user.getId());
			recycleBinUsers.add(rbUser);
			Map<String,List<T>>  userResources = findAllResourceOfUser(user.getId());
			addItemsToMap(RESOURCES, map, userResources.getOrDefault(RESOURCES, null));
			addItemsToMap(DATA_SOURCES, map, userResources.getOrDefault(DATA_SOURCES, null));
		}
		addItemsToMap(USERS, map, (List<T>) recycleBinUsers);
		return map;
	}

	private <T> Map<String,List<T>> findAllResourceOfUser(Integer userId) {

		List<HIResource> resourceList =  serviceDb.getHIResourceByCreatedBy(userId);
		List<GlobalConnections> connections =  globalConnectionService.findConnectionsByCreatedBy(String.valueOf(userId));
		Map<String,List<T>> resourceMap = new HashMap<>();
		Map<String,List<T>> globals =  findResourcesOfGlobalConnections(connections,userId);
		Map<String,List<T>> resourcesWithPlains = getResourceOf(resourceList,userId);
		addItemsToMap(DATA_SOURCES, resourceMap, globals.getOrDefault(DATA_SOURCES, null));
		addItemsToMap(DATA_SOURCES,resourceMap,resourcesWithPlains.getOrDefault(DATA_SOURCES,null));
		addItemsToMap(RESOURCES, resourceMap, globals.getOrDefault(RESOURCES, null));
		addItemsToMap(RESOURCES, resourceMap, resourcesWithPlains.getOrDefault(RESOURCES, null));

		return resourceMap;
	}

	@SuppressWarnings("unchecked")
	public <T> Map<String,List<T>> getGlobalConnectionResources(Integer connectionId,Integer userId) {
		List<Integer> mdIds =  hiResourceDao.findMetadataResourcesByGlobalConnectionId(connectionId);
		List<HIResource> resources = new ArrayList<>();
		if(!mdIds.isEmpty()) {
			Session session  = factory.getCurrentSession();
			SelectionQuery<HIResource> query = session.createSelectionQuery("select md.hiResource FROM HIResourceMetadata  md inner join  md.hiResource where md.id in ( :mdIds )",HIResource.class);
			query.setParameterList("mdIds", mdIds);
			resources =  query.list();
		}
		
		
		List<Integer> metadataResourceIds = resources.stream().map(HIResource::getResourceId).toList();
		Map<Integer, List<HIResource>> reportsByMetadataId = metadataResourceIds.isEmpty()
				? Collections.emptyMap()
				: serviceDb.findAllReportsByMetadataResourceIds(metadataResourceIds);
		
		List<HIResource> reportsList = new ArrayList<>();
		for(HIResource it:resources){
			reportsList.addAll(reportsByMetadataId.getOrDefault(it.getResourceId(), Collections.emptyList()));
		}
		resources.addAll(hiResourceDao.fetchResourceIdsBasedOnConTypeAndConId("global.jdbc", connectionId));

		Map<String, List<T>> filteredResource = getResourceOf(resources, userId);
		resources.addAll(reportsList);
		filteredResource.put("unfiltered", (List<T>)resources);
		return filteredResource;
	}


	@SuppressWarnings("unchecked")
	private <T> Map<String,List<T>> findResourcesOfGlobalConnections(List<GlobalConnections> connections,Integer userId) {

		Map<String,List<T>> resourceMap = new HashMap<>();
		List<T> dataSources = (List<T>) prepareGlobalConnection(connections);
		Map<String,List<T>> resources = new HashMap<>();
		for(GlobalConnections connection : connections ) {
			addItemsToMap(RESOURCES, resources, (List<T>) getGlobalConnectionResources(connection.getGlobalId(), userId).getOrDefault(RESOURCES, null));
		}
		addItemsToMap(DATA_SOURCES, resourceMap,dataSources);
		addItemsToMap(RESOURCES,resourceMap,resources.getOrDefault(RESOURCES, null));
		return resourceMap;
	}


	private List<RecycleBinDatasource> prepareGlobalConnection(List<GlobalConnections> connections) {
		List<RecycleBinDatasource> dataSources = new ArrayList<>();
		for(GlobalConnections connection : connections ) {
			RecycleBinDatasource datasource = new RecycleBinDatasource();
			datasource.setConnectionId(connection.getGlobalId());
			datasource.setDeleted(connection.isDeleted());
			datasource.setName(connection.getName());
			datasource.setType("Datasources[Managed]");
			dataSources.add(datasource);
		}
		return dataSources;
	}

	private <T> Map<String,List<T>> getResourceOf(List<HIResource> listOfResources,Integer userId) {
		Map<String, List<T>> resourceMap = new HashMap<>();
		listOfResources.forEach(resource ->  {
			Map<String,List<T>> map =  prepareHIResources(resource, true, userId);
			addItemsToMap(RESOURCES, resourceMap,map.getOrDefault(RESOURCES,null));
			addItemsToMap(DATA_SOURCES, resourceMap,map.getOrDefault(DATA_SOURCES,null));

		});
		return resourceMap;
	}

	public <T> Map<String,List<T>> getEfwdConnectionResources(Integer connectionId,Integer userId) {
		HIEfwdConnection connection =  efwdConnectionService.findConnectionById(String.valueOf(connectionId), Deleted.FALSE);
		List<HIMetadataConnectionEFWD> metadataConnections =  connection.getMetadataConnections();
		List<HIResource> metadataResources = new ArrayList<>();
		if(metadataConnections != null  && !metadataConnections.isEmpty()) {

			for(HIMetadataConnectionEFWD mdConnection : metadataConnections ) {
				for (MetadataDatabases db : mdConnection.getHiMetadataConnections().getMetadataDatabases()) {
					metadataResources.add(db.getHiResourceMetadata().getHiResource());
				}
			}

		}
		
		List<Integer> metadataResourceIds = metadataResources.stream().map(HIResource::getResourceId).toList();
		Map<Integer, List<HIResource>> reportsByMetadataId = metadataResourceIds.isEmpty()
				? Collections.emptyMap()
				: serviceDb.findAllReportsByMetadataResourceIds(metadataResourceIds);
		
		List<HIResource> reportsList = new ArrayList<>();
		for(HIResource it:metadataResources){
			reportsList.addAll(reportsByMetadataId.getOrDefault(it.getResourceId(), Collections.emptyList()));
		}
		List<HIResource> hcrs=hiResourceDao.fetchResourceIdsBasedOnConTypeAndConId(connection.getType(), connectionId);
		if(hcrs!=null && hcrs.size()>0){
			metadataResources.addAll(hcrs);
		}
		Map<String, List<T>> filteredResources = getResourceOf(metadataResources, userId);
		if(!reportsList.isEmpty()) {
			metadataResources.addAll(reportsList);
		}
		filteredResources.put("unfiltered",(List<T>) metadataResources);
		return filteredResources;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<String,List<T>> prepareHIResources(HIResource resource, boolean includeParent, Integer userId) {

		Map<String,List<T>> map = new HashMap<>();
		Set<Integer> folders = new HashSet<>();
		List<T> resources = (List<T>) getHIResourceTree(resource,folders,includeParent,userId);
		List<T> datasources = (List<T>) newFindEfwdConnectionsOfResources(folders);
		addItemsToMap(RESOURCES, map, resources);
		addItemsToMap(DATA_SOURCES, map, datasources);
		return map;
	}
	
	private List<RecycleBinResourceItem> getHIResourceTree(HIResource hiResource, Set<Integer> folders,boolean includeParent, Integer userId) {
		
		Map<Integer,RecycleBinResourceItem> itemMap = new LinkedHashMap<>();
		List<HIResource> descendants = getAllRelatedFiles(hiResource.getResourceId());
		collectFolderIds(hiResource, descendants, folders);
		
		List<Integer> metadataIds = collectMetadataResourceIds(hiResource, descendants);
		List<Integer> modelIds = collectModelResourceIds(hiResource, descendants);
		Map<Integer, List<HIResource>> reportsByMetadataId = metadataIds.isEmpty()
				? Collections.emptyMap()
				: serviceDb.findAllReportsByMetadataResourceIds(metadataIds);
		Map<Integer, List<HIResource>> instantReportsByModelId = modelIds.isEmpty()
				? Collections.emptyMap()
				: serviceDb.findAllInstantReportsByModelResourceIds(modelIds);

		
		Set<Integer> seedMappingChildIds = collectSeedMappingChildIds(hiResource, descendants, reportsByMetadataId,
				instantReportsByModelId);
		Map<Integer, List<HIResource>> mappingsByChildId = loadAllMappingsForSubtree(seedMappingChildIds);
		Map<Integer, List<HIResourceDTO>> mappingDtosByChildId = buildMappingDtosByChildId(mappingsByChildId);
		
		HIResourceOfActiveUser allResources = serviceDb.getResourceOfActiveUser();
		Map<Integer, Integer> securityMap = serviceDb.getSecurityMap();
		
		getHIResourceTreeHelper(hiResource, itemMap, allResources, securityMap, descendants, reportsByMetadataId,
				instantReportsByModelId, mappingDtosByChildId, includeParent, userId);
		
		return new ArrayList<>(itemMap.values());

	}
	
	
	
	private void collectFolderIds(HIResource hiResource, List<HIResource> descendants, Set<Integer> folders) {
		if (Boolean.TRUE.equals(hiResource.getFolder())) {
			folders.add(hiResource.getResourceId());
		}
		for (HIResource descendant : descendants) {
			if (Boolean.TRUE.equals(descendant.getFolder())) {
				folders.add(descendant.getResourceId());
			}
		}
	}
	
	private List<Integer> collectMetadataResourceIds(HIResource hiResource, List<HIResource> descendants) {
		List<Integer> metadataIds = new ArrayList<>();
		if (isMetadataResource(hiResource)) {
			metadataIds.add(hiResource.getResourceId());
		}
		for (HIResource descendant : descendants) {
			if (isMetadataResource(descendant)) {
				metadataIds.add(descendant.getResourceId());
			}
		}
		return metadataIds;
	}

	private List<Integer> collectModelResourceIds(HIResource hiResource, List<HIResource> descendants) {
		List<Integer> modelIds = new ArrayList<>();
		if (isAiModelResource(hiResource)) {
			modelIds.add(hiResource.getResourceId());
		}
		for (HIResource descendant : descendants) {
			if (isAiModelResource(descendant)) {
				modelIds.add(descendant.getResourceId());
			}
		}
		return modelIds;
	}

	private boolean isMetadataResource(HIResource resource) {
		return resource.getResourceType() != null
				&& resource.getResourceType().getExtension().contains("metadata");
	}



	private boolean isAiModelResource(HIResource resource) {
		return resource.getResourceType() != null
				&& resource.getResourceType().getExtension().contains("model");
	}
	private final void getHIResourceTreeHelper(HIResource hiResource, Map<Integer, RecycleBinResourceItem> itemMap,
			HIResourceOfActiveUser allResources, Map<Integer, Integer> securityMap, List<HIResource> descendantResources,
			Map<Integer, List<HIResource>> reportsByMetadataId, Map<Integer, List<HIResource>> instantReportsByModelId,
			Map<Integer, List<HIResourceDTO>> mappingDtosByChildId,
			boolean includeParent, Integer userId) {
		
		Map<String,Object> resourcePermission = allResources.getResourcePermission();
		Integer parentId = hiResource.getParentId();
		Boolean isFolder = hiResource.getFolder();
		ResourceType resourceType = hiResource.getResourceType();
		
		List<HIResource> hiResources = new ArrayList<>();
		hiResources.add(hiResource);
		
		if (Boolean.TRUE.equals(isFolder)) {
			hiResources.addAll(descendantResources);
		}
		
		hiResource.setParentId(null);
		List<HIResource> reports = null;
		Map<Integer,Integer> reportParentIdMap = new HashMap<>();
		if(isMetadataResource(hiResource)) {
			reports = new ArrayList<HIResource>(reportsByMetadataId.getOrDefault(hiResource.getResourceId(), Collections.emptyList()));
			reports.forEach(report -> {
				reportParentIdMap.put(report.getResourceId(), report.getParentId());
				report.setParentId(hiResource.getResourceId());
			});		
		
		hiResources.addAll(reports);
		hiResource.setFolder(true);
		
		hiResource.setResourceType(resourceTypeService.getResourceTypeByTypeAndExtension("folder", "." + JsonUtils.getFolderFileExtension()));
		
		}
		else if (isAiModelResource(hiResource)) {
			reports = new ArrayList<>(instantReportsByModelId.getOrDefault(hiResource.getResourceId(), Collections.emptyList()));
			reports.forEach(report -> {
				reportParentIdMap.put(report.getResourceId(), report.getParentId());
				report.setParentId(hiResource.getResourceId());
			});

			hiResources.addAll(reports);
			hiResource.setFolder(true);
			hiResource.setResourceType(resourceTypeService.getResourceTypeByTypeAndExtension("folder",
					"." + JsonUtils.getFolderFileExtension()));
		}
		else if (Boolean.TRUE.equals(isFolder)) {
			attachReportsForMetadataDescendants(descendantResources, hiResources, reportsByMetadataId, reportParentIdMap);
			attachInstantReportsForModelDescendants(descendantResources, hiResources, instantReportsByModelId,
					reportParentIdMap);
		}
		
		for(HIResource it : hiResources) {
			String resourcePath = it.getResourceURL();
			Object permission = resourcePermission.get(resourcePath);
			if (permission != null ) {
				securityMap.put(it.getResourceId(), Integer.valueOf("" + permission));
			}
		}
		
		HIResourceOfActiveUser activeUser = new HIResourceOfActiveUser(securityMap, hiResources, userId);
		List<HIResourceDTO> resources = activeUser.getResourceDTOList();

		hiResource.setParentId(parentId);
		hiResource.setFolder(isFolder);
		hiResource.setResourceType(resourceType);
		
		restoreReportParentIds(reports, reportParentIdMap);
		restoreReportParentIdsFromMap(reportsByMetadataId, reportParentIdMap);
		restoreReportParentIdsFromMap(instantReportsByModelId, reportParentIdMap);

		Set<Integer> visitedResourceIds = new HashSet<>();
		for (HIResourceDTO dto : resources) {
			addResource(dto, hiResource, itemMap, includeParent, mappingDtosByChildId, visitedResourceIds);
		}
	}
	
	private final void addResource(HIResourceDTO dto, HIResource resource,
	        Map<Integer, RecycleBinResourceItem> itemMap, boolean includeParent,
	        Map<Integer, List<HIResourceDTO>> mappingDtosByChildId, Set<Integer> visitedResourceIds) {

	    if (!visitedResourceIds.add(dto.getResourceId())) {
	        return;
	    }

	    if (includeParent || !dto.getResourceId().equals(resource.getResourceId())) {
	        RecycleBinResourceItem item = new RecycleBinResourceItem();
	        item.setName(dto.getTitleForRecycleBin());
	        item.setPath(dto.getPath());
	        item.setDeleted(dto.getDeleted());
	        item.setResourceId(dto.getResourceId());
	        itemMap.putIfAbsent(dto.getResourceId(), item);
	    }

	    List<HIResourceDTO> mappingDtoList = mappingDtosByChildId.getOrDefault(
	            dto.getResourceId(), Collections.emptyList());
	    if (!mappingDtoList.isEmpty()) {
	        dto.setChildren(new ArrayList<>(mappingDtoList));
	    }

	    if (dto.getChildren() != null) {
	        for (HIResourceDTO child : dto.getChildren()) {
	        	addResource(child, resource, itemMap, includeParent, mappingDtosByChildId, visitedResourceIds);
	        }
	    }
	}
	
	
	private List<HIResource> getAllRelatedFiles(Integer parentId) {
		List<Integer> descendantIds = hiResourceDao.getChildrenResourceByParentIds(List.of(parentId));
		if (descendantIds == null || descendantIds.isEmpty()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(hiResourceDao.getHIResourcesByIds(descendantIds, false));
	}
	
	private List<RecycleBinDatasource> newFindEfwdConnectionsOfResources(Set<Integer> resourceIds) {
		final List<RecycleBinDatasource> datasourceItems = new ArrayList<>();
		if (resourceIds == null || resourceIds.isEmpty()) {
			return datasourceItems;
		}

		List<EfwdConnDTO> connections = efwdConnectionService.findConnectionByResourceIds(new ArrayList<>(resourceIds),
				Boolean.TRUE, Boolean.FALSE);
		connections.forEach(connection -> {
			RecycleBinDatasource ds = new RecycleBinDatasource();
			ds.setName(connection.getName());
			ds.setConnectionId(connection.getId());
			ds.setDeleted(connection.isDeleted());
			ds.setType("Datasources[Efwd]");
			ds.setDirectory(connection.getResource().getResourceUrl());
			if (!datasourceItems.contains(ds)) {
				datasourceItems.add(ds);
			}
		});
		return datasourceItems;
	}
	
	private void restoreReportParentIdsFromMap(Map<Integer, List<HIResource>> reportsByMetadataId,
			Map<Integer, Integer> reportsParentIdMap) {
		for (List<HIResource> reports : reportsByMetadataId.values()) {
			restoreReportParentIds(reports, reportsParentIdMap);
		}
	}
	
	private void restoreReportParentIds(List<HIResource> reports, Map<Integer, Integer> reportsParentIdMap) {
		if (reports == null) {
			return;
		}
		reports.forEach(report -> {
			Integer parent = reportsParentIdMap.get(report.getResourceId());
			if (parent != null) {
				report.setParentId(parent);
			}
		});
	}
	
	private void attachReportsForMetadataDescendants(List<HIResource> descendants, List<HIResource> hiResources,
			Map<Integer, List<HIResource>> reportsByMetadataId, Map<Integer, Integer> reportsParentIdMap) {
		for (HIResource resource : descendants) {
			if (!isMetadataResource(resource)) {
				continue;
			}
			List<HIResource> reports = reportsByMetadataId.get(resource.getResourceId());
			if (reports == null || reports.isEmpty()) {
				continue;
			}
			for (HIResource report : reports) {
				reportsParentIdMap.put(report.getResourceId(), report.getParentId());
				report.setParentId(resource.getResourceId());
				hiResources.add(report);
			}
		}
	}

	private void attachInstantReportsForModelDescendants(List<HIResource> descendants, List<HIResource> hiResources,
			Map<Integer, List<HIResource>> instantReportsByModelId, Map<Integer, Integer> reportsParentIdMap) {
		for (HIResource resource : descendants) {
			if (!isAiModelResource(resource)) {
				continue;
			}
			List<HIResource> instantReports = instantReportsByModelId.get(resource.getResourceId());
			if (instantReports == null || instantReports.isEmpty()) {
				continue;
			}
			for (HIResource instantReport : instantReports) {
				reportsParentIdMap.put(instantReport.getResourceId(), instantReport.getParentId());
				instantReport.setParentId(resource.getResourceId());
				hiResources.add(instantReport);
			}
		}
	}


	@Override
	public Optional<HIRecycleBin> findHIRecycleBinByGlobalId(Integer globalId) {

		HIRecycleBin bin = null;
		try {
			Session session = factory.getCurrentSession();
			String hql = "FROM HIRecycleBin bin where bin.hiRecycleBinDsGlobalConnections.globalConnection.globalId =:globalId";
			SelectionQuery<HIRecycleBin> query = session.createSelectionQuery(hql,HIRecycleBin.class);
			query.setParameter("globalId", globalId);
			bin =  query.uniqueResult();
		}
		catch (Exception e) {
			if(log.isErrorEnabled()) {
				e.printStackTrace();
			}
		}
		return Optional.ofNullable(bin);
	}

	@Override
	public Optional<HIRecycleBin> findHIRecycleBinByEFWDId(Integer efwdId) {
		HIRecycleBin bin = null;
		try {
			Session session = factory.getCurrentSession();
			String hql = "FROM HIRecycleBin bin where bin.hiRecycleBinHIEfwdConnection.efwdConnection.id =:efwdId";
			SelectionQuery<HIRecycleBin> query = session.createSelectionQuery(hql,HIRecycleBin.class);
			query.setParameter("efwdId", efwdId);
			bin =  query.uniqueResult();
		}
		catch (Exception e) {
			if(log.isErrorEnabled()) {
				e.printStackTrace();
			}
		}
		return Optional.ofNullable(bin);
	}

	private final <T> void addItemsToMap(String key, Map<String,List<T>> map, List<T> items) {

		if (items == null || items.isEmpty())
			return;

		if (map.containsKey(key)) {
			List<T> list = map.get(key);
			for (T object : items) {
				if (!list.contains(object)) {
					list.add(object);
				}
			}
		}
		else {
			map.put(key, items);
		}
	}

	@Override
	public Optional<HIRecycleBin> findHIRecycleBinBYUserId(int userId) {
		HIRecycleBin bin = null;
		try {
			Session session = factory.getCurrentSession();
			String hql = "FROM HIRecycleBin bin where bin.hiRecycleBinHUsers.user.id =:userId";
			SelectionQuery<HIRecycleBin> query = session.createSelectionQuery(hql,HIRecycleBin.class);
			query.setParameter("userId", userId);
			bin =  query.uniqueResult();
		}
		catch (Exception e) {
			if(log.isErrorEnabled()) {
				e.printStackTrace();
			}
		}
		return Optional.ofNullable(bin);
	}

	@Override
	public void deleteHIRecycleByEfwdId(Integer connectionId) {
		Optional<HIRecycleBin> bin =  findHIRecycleBinByEFWDId(connectionId);
		if(bin.isPresent()) {
			delete(bin.get());
		}
	}

	@Override
	public void deleteRecycleBinByGlobalId(Integer globalId) {
		Optional<HIRecycleBin> bin =  findHIRecycleBinByGlobalId(globalId);
		if(bin.isPresent()) {
			delete(bin.get());
		}
	}

	@Override
	public boolean isRecycleBinPresent(Long id) {
		try {
			Session session = factory.getCurrentSession();
			String hql = "SELECT COUNT(*) FROM HIRecycleBin where id =:id";
			SelectionQuery<Long> query = session.createSelectionQuery(hql,Long.class);
			query.setParameter("id",id);
			Long count =  query.uniqueResult();
			if(count > 0 ) {
				return true;
			}
		}
		catch (Exception e) {
			if(log.isErrorEnabled()) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private Set<Integer> collectSeedMappingChildIds(HIResource hiResource, List<HIResource> descendants,
			Map<Integer, List<HIResource>> reportsByMetadataId, Map<Integer, List<HIResource>> instantReportsByModelId) {
		Set<Integer> resourceIds = new HashSet<>();
		resourceIds.add(hiResource.getResourceId());
		for (HIResource descendant : descendants) {
			resourceIds.add(descendant.getResourceId());
		}
		for (List<HIResource> reports : reportsByMetadataId.values()) {
			for (HIResource report : reports) {
				resourceIds.add(report.getResourceId());
			}
		}
		for (List<HIResource> instantReports : instantReportsByModelId.values()) {
			for (HIResource instantReport : instantReports) {
				resourceIds.add(instantReport.getResourceId());
			}
		}
		return resourceIds;
	}
	
	private Map<Integer, List<HIResource>> loadAllMappingsForSubtree(Set<Integer> seedChildIds) {
		Map<Integer, List<HIResource>> mappingsByChildId = new HashMap<>();
		if (seedChildIds == null || seedChildIds.isEmpty()) {
			return mappingsByChildId;
		}
		Set<Integer> fetchedChildIds = new HashSet<>();
		Set<Integer> pendingChildIds = new HashSet<>(seedChildIds);
		while (!pendingChildIds.isEmpty()) {
			List<Integer> batchIds = pendingChildIds.stream().filter(id -> !fetchedChildIds.contains(id)).toList();
			if (batchIds.isEmpty()) {
				break;
			}
			Map<Integer, List<HIResource>> batchResult = mappingService.findChildMappingsByChildResourceIds(batchIds);
			for (Integer childId : batchIds) {
				fetchedChildIds.add(childId);
				mappingsByChildId.put(childId, batchResult.getOrDefault(childId, Collections.emptyList()));
			}
			pendingChildIds.removeAll(batchIds);
			for (List<HIResource> parentResources : batchResult.values()) {
				for (HIResource parentResource : parentResources) {
					Integer parentId = parentResource.getResourceId();
					if (!fetchedChildIds.contains(parentId)) {
						pendingChildIds.add(parentId);
					}
				}
			}
		}
		return mappingsByChildId;
	}

	private Map<Integer, List<HIResourceDTO>> buildMappingDtosByChildId(
			Map<Integer, List<HIResource>> mappingsByChildId) {
		Map<Integer, List<HIResourceDTO>> mappingDtosByChildId = new HashMap<>();
		for (Map.Entry<Integer, List<HIResource>> entry : mappingsByChildId.entrySet()) {
			List<HIResourceDTO> mappingDtos = new ArrayList<>();
			for (HIResource mapping : entry.getValue()) {
				mappingDtos.add(hiResourceToDtoMapper.map(mapping));
			}
			mappingDtosByChildId.put(entry.getKey(), mappingDtos);
		}
		return mappingDtosByChildId;
	}
	
	@Override
	public void deleteRecycleBinsByIds(List<Long> recycleBinIds) {
		if (recycleBinIds == null || recycleBinIds.isEmpty()) {
			return;
		}
		try {
			Session session = factory.getCurrentSession();
			MutationQuery deleteLinks = session.createMutationQuery("DELETE FROM HIRecycleBinHIResourceDB rb WHERE rb.recycleBin.id in (:recycleBinIds)");
			deleteLinks.setParameterList("recycleBinIds", recycleBinIds);
			deleteLinks.executeUpdate();
			
			MutationQuery deleteBins = session.createMutationQuery("DELETE FROM HIRecycleBin r WHERE r.id in (:recycleBinIds)");
			deleteBins.setParameterList("recycleBinIds", recycleBinIds);
			deleteBins.executeUpdate();
			
		} catch (Exception e) {
			log.error("Error occurred while bulk deleting RecycleBin entries");
		}
	}
	
	@Override
	public List<RecycleBinDTO> getAllRecycleBinDTOs() {
	    List<RecycleBinDTO> binItems = getAllRecycleBinItems();
	    if (binItems.isEmpty()) {
	        return binItems;
	    }

	    Map<Long, RecycleBinDTO> linkedByBinId = new HashMap<>();
	    getAllLinkedData(linkedByBinId);

	    for (RecycleBinDTO dto : binItems) {
	        RecycleBinDTO linked = linkedByBinId.get(dto.getRecycleBinId());
	        if (linked == null) {
	            continue;
	        }
	        dto.setResourceId(linked.getResourceId());
	        dto.setTitle(linked.getTitle());
	        dto.setResourceUrl(linked.getResourceUrl());
	        dto.setResourceTypeId(linked.getResourceTypeId());
	        dto.setName(linked.getName());
	        dto.setCreatedBy(linked.getCreatedBy());
	    }
	    return binItems;
	}
}
