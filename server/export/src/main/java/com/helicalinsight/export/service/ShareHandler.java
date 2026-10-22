package com.helicalinsight.export.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.export.dto.Conflict;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * Handles sharing of resources by exporting and importing security information.
 * ShareHandler class extends {@link ResourceIOHandler}
 * class is annotated with {@code @Component} to indicate that it is a Spring bean.
 * it can be automatically discovered and managed by the Spring
 * container.
 */
@Component
public class ShareHandler extends ResourceIOHandler {

	private static final String CREATED_BY = "createdBy";
	private static final String ALL_USER = "orgUsers";
	private static final Logger LOG = LoggerFactory.getLogger(ShareHandler.class);

	@Autowired
	private ResourceDTOMapper dtoMapper;

	/**
     * Writes security information to a file for a given resource.
     *
     * @param resource 		HIResourceDTO instance to provide information about path, id, name.
     * @param dir     	    directory where the security information will be exported.
     * @param manifest      manifest file to store data source related info.
     */
	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest) {

		if (!manifest.getShares().containsKey(resource.getPath())) {
			Integer resourceId = resource.getResourceId();
			List<HIResourceSecurityDB> shareList = serviceDb.getHIResourceSecurityByResourceId(resourceId);
			ArrayNode shareListNode = JacksonUtility.arrayNode();
			if (!shareList.isEmpty()) {
				for (HIResourceSecurityDB security : shareList) {
					ObjectNode securityJson = JacksonUtility.mapToJson(security);
					User user = userService.findUser(security.getCreatedBy());
					recycleBinHandler.writeUser(security.getCreatedBy(), dir, manifest);
					if(security.getOrgId()!=null) {
						recycleBinHandler.writeOrganization(security.getOrgId().getId(), dir, manifest);
						putId(securityJson.get("orgId"), security.getOrgId().getId());
						List<User> users = userService.getAllUsersOfOrganization(security.getOrgId().getId());
						if(users.size()>0) {
							ArrayNode orgUsers = JacksonUtility.arrayNode();
							for(User u: users){
								ObjectNode uObject = withUserId(u);
								orgUsers.add(uObject);
								recycleBinHandler.writeUser(u.getId(), dir, manifest);
							}
							securityJson.putPOJO(ALL_USER, orgUsers);
						}
					}
					if (security.getUserId() != null) {
						recycleBinHandler.writeUser(security.getUserId().getId(), dir, manifest);
						putId(securityJson.get("userId"), security.getUserId().getId());
					}
					securityJson.putPOJO(CREATED_BY, withUserId(user));
					shareListNode.add(securityJson);
				}
				dataWriter.write(shareListNode, dir, resource,ResourceSuffix.SHARE);
				manifestUtils.insertShare(resource, manifest);
			}
		}
	}
	

	public void write(HIResource resource, String dir, Manifest manifest) {
		write(dtoMapper.map(resource),dir,manifest);
	}
	
	/**
     * Imports security information for a given resource from a file.
     *
     * @param resource       		HIResource object for which security information needs to be imported.
     * @param path           		path of the resource being imported.
     * @param request        		import request containing options and onConflict strategy.
     * @param manifest       		manifest file containing information about the exported resources.
     */
	public void importResource(HIResource resource, ImportRequest request ,Manifest manifest) {
		String resourceUrl = resource != null ? resource.getResourceURL() : null;
		boolean shareExists = manifestUtils.compareOptions(request.getOptions(), manifest,"share");
		if(!shareExists) {
			return;
		}
		String shareFileName = manifestUtils.getShare(context.removeDestination(resource.getResourceURL()), manifest);
		if(shareFileName != null) {
			if (context != null && resourceUrl != null) {
				context.addLog(resourceUrl, "[share] Importing share settings…");
			}
			importResource(resource, shareFileName,request.getOnConflict());
			if (context != null && resourceUrl != null) {
				context.addLog(resourceUrl, "[share] Share settings imported.");
			}
		} else if (context != null && resourceUrl != null) {
			context.addLog(resourceUrl, "[share] No share sidecar found; skipped.");
		}
	}
	/**
     * Method responsible for security information for a given resource from a file.
     *
     * @param resource     		resource for which security information needs to be imported.
     * @param path        		path of the resource being imported.
     * @param shareFileName		name of the file containing security information.
     * @param onConflict   		conflict resolution strategy.
     */
	@Override
	public void importResource(HIResource resource, String shareFileName, String onConflict) {
		String resourcePath = context.getResourcesDirectory();
		try (FileInputStream inputStream = new FileInputStream(
				String.join(File.separator, resourcePath, shareFileName))) {
			ArrayNode shareList = mapperUtils.mapToArray(inputStream);
			List<HIResourceSecurityDB> listToImport = new ArrayList<>();
			ArrayNode userListNode = JacksonUtility.arrayNode();

			for (JsonNode node : shareList) {
				ObjectNode nodeObject =  (ObjectNode) node;
				ObjectNode owner = (ObjectNode) nodeObject.get(CREATED_BY);
				Integer ownerId = saveOwner(owner);
				nodeObject.remove(CREATED_BY);
				if(nodeObject.has(ALL_USER)) {
					ArrayNode allUsers = (ArrayNode) nodeObject.get(ALL_USER);
					for (JsonNode nd : allUsers) {
						ObjectNode ndObj = (ObjectNode) nd;
						userListNode.add(ndObj);
					}
					nodeObject.remove(ALL_USER);
				}

				HIResourceSecurityDB share = mapperUtils.mapToDTO(nodeObject.toString(), HIResourceSecurityDB.class);
				share.setHiResource(resource);
				share.setCreatedBy(ownerId);
				listToImport.add(share);
			}
			
			List<HIResourceSecurityDB> existingShareList = serviceDb.getHIResourceSecurityByResourceId(resource.getResourceId());
			
			if (!existingShareList.isEmpty()) {
				if(onConflict.equalsIgnoreCase(Conflict.UPDATE)) {
					serviceDb.deleteHIResourceSecurity(resource.getResourceId());
				}else if (onConflict.equalsIgnoreCase(Conflict.SKIP)) {
					listToImport.clear();
				}else {
					for (HIResourceSecurityDB existingShare : existingShareList) {
						for (HIResourceSecurityDB shareToImport : listToImport) {
							if (compare(shareToImport, existingShare) == 1) {
								updateShare(shareToImport, existingShare);
								listToImport.remove(shareToImport);
								break;
							}
						}
					}
					for (JsonNode nd : userListNode) {
						ObjectNode ndObj = (ObjectNode) nd;
						saveOwner(ndObj);
					}
				}
			}			
			if (!listToImport.isEmpty()) {
				for (HIResourceSecurityDB share : listToImport) {
					share.setHiResource(resource);
					createNewShare(share);
				}
				for (JsonNode nd : userListNode) {
					ObjectNode ndObj = (ObjectNode) nd;
					saveOwner(ndObj);
				}
			}

		} catch (IOException ex) {
			LOG.error("Error occurred : {}", ex.getMessage());
			if (context != null && resource != null && resource.getResourceURL() != null
					&& !resource.getResourceURL().isEmpty()) {
				context.addError(resource.getResourceURL(), "Failed to import share settings.");
			}
		}
	}
	/**
     * Compares two security configurations to check if they are equal.
     *
     * @param o1 			first security configuration.
     * @param o2 			second security configuration.
     * @return 1 if the configurations are equal, -1 otherwise.
     */
	private int compare(HIResourceSecurityDB o1, HIResourceSecurityDB o2) {

		if (o1.getUserId() != null && o2.getUserId() != null) {
			if (o1.getUserId().getUsername().equalsIgnoreCase(o2.getUserId().getUsername())
					&& o1.getPermission().equals(o2.getPermission())) {
				return 1;
			}
		}
		if (o1.getRoleId() != null && o2.getRoleId() != null) {
			if (o1.getRoleId().getRole_name().equalsIgnoreCase(o2.getRoleId().getRole_name())
					&& o1.getPermission().equals(o2.getPermission())) {
				return 1;
			}
		}
		if (o1.getOrgId() != null && o2.getOrgId() != null) {
			if (o1.getOrgId().getOrg_name().equalsIgnoreCase(o2.getOrgId().getOrg_name())
					&& o1.getPermission().equals(o2.getPermission())) {
				return 1;
			}
		}

		return -1;
	}
	/**
     * Creates a new share with the given security configuration.
     * @param share 	security configuration.
     */
	private void createNewShare(HIResourceSecurityDB share) {
		User user = share.getUserId();
		Role sharedRole = share.getRoleId();
		Organization org = share.getOrgId();
		Date date = new Date();
		share.setLastUpdatedTime(date);

		if (null != user && null == org) {
			user = shareUtils.getOrInsertUser(dtoMapper.map(user), context);
			share.setUserId(user);
		} else if (null != sharedRole) {
			sharedRole = shareUtils.getOrInsertRole(dtoMapper.map(sharedRole));
			share.setRoleId(sharedRole);

		} else if(null != org ) {
			org = shareUtils.getOrInsertOrganiation(dtoMapper.map(org), context);
			share.setOrgId(org);
		}
		serviceDb.addHIResourceSecurity(share);

	}
	/**
     * Updates an existing share with the given security configuration.
     * @param share    		new security configuration.
     * @param existing 	    existing security configuration.
     */
	private void updateShare(HIResourceSecurityDB share, HIResourceSecurityDB existing) {
		Date date = new Date();
		existing.setCreatedBy(share.getCreatedBy());
		existing.setLastUpdatedTime(date);
		existing.setPermission(share.getPermission());
		if( share.getOrgId() != null  ) existing.setOrgId(shareUtils.getOrInsertOrganiation(dtoMapper.map(share.getOrgId()), context));
		if( share.getRoleId() != null ) existing.setRoleId(shareUtils.getOrInsertRole(dtoMapper.map((share.getRoleId()))));
		if( share.getUserId() != null ) existing.setUserId(shareUtils.getOrInsertUser(dtoMapper.map(share.getUserId()), context));
		serviceDb.editHIResourceSecurity(existing);
	}
	/**
     * Saves the owner of a shared resource.
     *
     * @param owner 		owner information.
     * @return The ID of the owner.
     */
	public final Integer saveOwner(JsonNode owner) {
		User user = mapperUtils.mapToDTO(owner.toString(), User.class);
		user = shareUtils.getOrInsertUser(dtoMapper.map(user), context);
		return user.getId();
	}

	/**
	 * User/Organization id getters are @JsonIgnore, so export JSON omits them.
	 * Recycle-bin sidecars are keyed by export id (user_{id} / org_{id}), so write id explicitly.
	 */
	private static ObjectNode withUserId(User user) {
		ObjectNode userObject = JacksonUtility.mapToJson(user);
		if (user != null) {
			userObject.put("id", user.getId());
		}
		return userObject;
	}

	private static void putId(JsonNode node, int id) {
		if (node instanceof ObjectNode objectNode) {
			objectNode.put("id", id);
		}
	}

}
