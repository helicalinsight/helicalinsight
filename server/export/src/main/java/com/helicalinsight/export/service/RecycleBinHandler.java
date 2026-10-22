package com.helicalinsight.export.service;

import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.dao.HIRecycleBinDao;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceShareUtils;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;

/**
 * Exports/imports recycle-bin sidecars for soft-deleted entities.
 */
@Component
public class RecycleBinHandler {

	private static final Logger logger = LoggerFactory.getLogger(RecycleBinHandler.class);

	public static final String KEY_DS_GLOBAL = "ds_global_";
	public static final String KEY_DS_EFWD = "ds_efwd_";
	public static final String KEY_USER = "user_";
	public static final String KEY_ORG = "org_";

	@Autowired
	private HIRecycleBinService recycleBinService;

	@Autowired
	private HIRecycleBinDao recycleBinDao;

	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	private ResourceDataWriter dataWriter;

	@Autowired
	private ManifestUtils manifestUtils;

	@Autowired
	private ResourceDTOMapper dtoMapper;

	@Autowired
	private JsonMapperUtils mapperUtils;

	@Autowired
	private ResourceShareUtils shareUtils;

	@Autowired
	private GlobalConnectionService globalConnectionService;

	@Autowired
	private EFWDConnectionService efwdConnectionService;

	@Autowired
	@Qualifier("userDetailsService")
	private UserService userService;

	@Autowired
	private OrganizationService organizationService;

	@Transactional(readOnly = true)
	public void writeResource(HIResource resource, HIResourceDTO dto, String dir, Manifest manifest) {
		if (dto == null) {
			return;
		}
		writeResource(resource, dto.getPath(), dir, manifest);
	}

	@Transactional(readOnly = true)
	public void writeResource(HIResource resource, String key, String dir, Manifest manifest) {
		if (!isEnabled(manifest) || resource == null || key == null) {
			return;
		}
		if (Boolean.TRUE.equals(resource.isDeleted())) {
			writeBin(recycleBinService.findHIRecycleBinByResourceId(resource.getResourceId()), key, dir, manifest);
		}
		if (resource.getCreatedBy() != null) {
			writeUser(resource.getCreatedBy(), dir, manifest);
		}
	}

	@Transactional(readOnly = true)
	public void writeGlobalConnection(GlobalConnections connection, String dir, Manifest manifest) {
		if (connection == null) {
			return;
		}
		writeGlobalConnection(connection.getGlobalId(), dir, manifest);
	}

	@Transactional(readOnly = true)
	public void writeGlobalConnection(Integer globalId, String dir, Manifest manifest) {
		if (!isEnabled(manifest) || globalId == null) {
			return;
		}
		String key = KEY_DS_GLOBAL + globalId;
		recycleBinDao.findHIRecycleBinByGlobalId(globalId)
				.ifPresent(bin -> writeBin(bin, key, dir, manifest));
	}

	@Transactional(readOnly = true)
	public void writeEfwdConnection(HIEfwdConnection connection, String dir, Manifest manifest) {
		if (connection == null) {
			return;
		}
		writeEfwdConnection(connection.getId(), dir, manifest);
	}

	@Transactional(readOnly = true)
	public void writeEfwdConnection(Integer efwdId, String dir, Manifest manifest) {
		if (!isEnabled(manifest) || efwdId == null) {
			return;
		}
		String key = KEY_DS_EFWD + efwdId;
		recycleBinDao.findHIRecycleBinByEFWDId(efwdId)
				.ifPresent(bin -> writeBin(bin, key, dir, manifest));
	}

	@Transactional(readOnly = true)
	public void writeUser(Integer userId, String dir, Manifest manifest) {
		if (!isEnabled(manifest) || userId == null) {
			return;
		}
		String key = KEY_USER + userId;
		recycleBinDao.findHIRecycleBinBYUserId(userId)
				.ifPresent(bin -> writeBin(bin, key, dir, manifest));
	}

	@Transactional(readOnly = true)
	public void writeUser(User user, String dir, Manifest manifest) {
		if (user == null) {
			return;
		}
		writeUser(user.getId(), dir, manifest);
	}

	@Transactional(readOnly = true)
	public void writeOrganization(Organization organization, String dir, Manifest manifest) {
		if (organization == null) {
			return;
		}
		writeOrganization(organization.getId(), dir, manifest);
	}

	@Transactional(readOnly = true)
	public void writeOrganization(Integer orgId, String dir, Manifest manifest) {
		if (!isEnabled(manifest) || orgId == null) {
			return;
		}
		String key = KEY_ORG + orgId;
		recycleBinDao.findHIRecycleBinByOrgId(orgId)
				.ifPresent(bin -> writeBin(bin, key, dir, manifest));
	}

	/**
	 * Soft-deletes the imported resource when a recycle-bin sidecar is present and the option is enabled.
	 * SKIP leaves destination state unchanged.
	 */
	@Transactional
	public void importResource(HIResource resource, ImportManagerContext context) {
		if (resource == null || context == null) {
			return;
		}
		if (context.getResponse() != null && context.getResponse().getSkips() != null
				&& context.getResponse().getSkips().contains(resource.getResourceURL())) {
			return;
		}
		String key = resolveResourceKey(resource, context);
		importSidecar(key, resource.getResourceURL(), context, () -> {
			if (!Boolean.TRUE.equals(resource.isDeleted())) {
				serviceDb.deleteHIResource(resource);
			}
			return recycleBinService.findHIRecycleBinByResourceId(resource.getResourceId());
		});
	}

	@Transactional
	public void importGlobalConnection(Integer exportGlobalId, GlobalConnections connection, ImportManagerContext context) {
		if (!canImport(exportGlobalId, connection, context)) {
			return;
		}
		String key = KEY_DS_GLOBAL + exportGlobalId;
		importSidecar(key, key, context, () -> softDeleteConnection(connection));
	}

	@Transactional
	public void importEfwdConnection(Integer exportEfwdId, HIEfwdConnection connection, ImportManagerContext context) {
		if (!canImport(exportEfwdId, connection, context)) {
			return;
		}
		String key = KEY_DS_EFWD + exportEfwdId;
		importSidecar(key, key, context, () -> softDeleteConnection(connection));
	}

	@Transactional
	public void importUser(Integer exportUserId, User user, ImportManagerContext context) {
		if (!canImport(exportUserId, user, context) || exportUserId <= 0) {
			return;
		}
		String key = KEY_USER + exportUserId;
		importSidecar(key, key, context, () -> softDeleteUser(user));
	}

	@Transactional
	public void importOrganization(Integer exportOrgId, Organization organization, ImportManagerContext context) {
		if (!canImport(exportOrgId, organization, context) || exportOrgId <= 0) {
			return;
		}
		String key = KEY_ORG + exportOrgId;
		importSidecar(key, key, context, () -> softDeleteOrganization(organization));
	}

	private boolean canImport(Integer exportId, Object entity, ImportManagerContext context) {
		return exportId != null && entity != null && context != null;
	}

	private HIRecycleBin softDeleteConnection(GlobalConnections connection) {
		if (!Boolean.TRUE.equals(connection.isDeleted())) {
			globalConnectionService.deleteGlobalConnections(connection.getGlobalId());
			connection.setDeleted(true);
		}
		return recycleBinDao.findHIRecycleBinByGlobalId(connection.getGlobalId()).orElse(null);
	}

	private HIRecycleBin softDeleteConnection(HIEfwdConnection connection) {
		if (!Boolean.TRUE.equals(connection.isDeleted())) {
			efwdConnectionService.deleteEFConnectionById(connection.getId());
			connection.setDeleted(true);
		}
		return recycleBinDao.findHIRecycleBinByEFWDId(connection.getId()).orElse(null);
	}

	private HIRecycleBin softDeleteUser(User user) {
		if (!Boolean.TRUE.equals(user.isDeleted())) {
			userService.deleteUser(user.getId());
			user.setDeleted(true);
		}
		return recycleBinDao.findHIRecycleBinBYUserId(user.getId()).orElse(null);
	}

	private HIRecycleBin softDeleteOrganization(Organization organization) {
		if (!Boolean.TRUE.equals(organization.isDeleted())) {
			organizationService.delete(organization.getId());
			organization.setDeleted(true);
		}
		return recycleBinDao.findHIRecycleBinByOrgId(organization.getId()).orElse(null);
	}

	@FunctionalInterface
	private interface SoftDeleteAndFindBin {
		HIRecycleBin apply();
	}

	private void importSidecar(String key, String logKey, ImportManagerContext context, SoftDeleteAndFindBin softDeleteAndFind) {
		ImportRequest request = context.getRequest();
		Manifest manifest = context.getManifest();
		if (!manifestUtils.compareOptions(request.getOptions(), manifest, "recyclebin")) {
			return;
		}
		String fileName = manifestUtils.getRecycleBin(key, manifest);
		if (fileName == null) {
			return;
		}
		context.addLog(logKey, "[recycleBin] Importing recycle-bin entry…");
		ObjectNode node = readSidecar(context, fileName);
		if (node == null) {
			context.addLog(logKey, "[recycleBin] Sidecar unreadable; skipped.");
			return;
		}
		HIRecycleBin bin = softDeleteAndFind.apply();
		if (bin == null) {
			context.addLog(logKey, "[recycleBin] No recycle-bin record after soft-delete; skipped.");
			return;
		}
		applySidecarMetadata(bin, node);
		recycleBinService.save(bin);
		context.addLog(logKey, "[recycleBin] Recycle-bin entry imported.");
	}

	private void applySidecarMetadata(HIRecycleBin bin, ObjectNode node) {
		User deletedBy = resolveSidecarUser(node.get("deletedBy"));
		User createdBy = resolveSidecarUser(node.get("createdBy"));
		if (deletedBy != null) {
			bin.setDeletedBy(deletedBy);
		}
		if (createdBy != null) {
			bin.setCreatedBy(createdBy);
			bin.setOrgId(createdBy.getOrganization());
		}
		if (node.has("deletedOn") && !node.get("deletedOn").isNull()) {
			RecycleBinDTO dto = mapperUtils.mapToDTO(node.toString(), RecycleBinDTO.class);
			if (dto.getDeletedOn() != null) {
				bin.setDeletedOn(dto.getDeletedOn());
			}
		}
	}

	private boolean isEnabled(Manifest manifest) {
		if (manifest == null) {
			return false;
		}
		ResourceOptions options = manifest.getOptions();
		return options != null && Boolean.TRUE.equals(options.getRecycleBin());
	}

	private void writeBin(HIRecycleBin bin, String key, String dir, Manifest manifest) {
		if (bin == null || key == null || manifestUtils.getRecycleBin(key, manifest) != null) {
			return;
		}
		RecycleBinDTO payload = toSidecarDto(bin);
		HIResourceDTO pathDto = new HIResourceDTO();
		pathDto.setPath(key);
		dataWriter.write(payload, dir, pathDto, ResourceSuffix.RECYCLE_BIN);
		manifestUtils.insertRecycleBin(key, manifest);
	}

	private RecycleBinDTO toSidecarDto(HIRecycleBin bin) {
		RecycleBinDTO dto = new RecycleBinDTO();
		dto.setRecycleBinId(bin.getId());
		dto.setType(bin.getRecycleBinType());
		dto.setDeletedOn(bin.getDeletedOn());
		if (bin.getDeletedBy() != null) {
			dto.setDeletedBy(dtoMapper.map(bin.getDeletedBy()));
		}
		if (bin.getCreatedBy() != null) {
			dto.setCreatedBy(dtoMapper.map(bin.getCreatedBy()));
		}
		return dto;
	}

	private String resolveResourceKey(HIResource resource, ImportManagerContext context) {
		String key = context.removeDestination(resource.getResourceURL());
		Manifest manifest = context.getManifest();
		if (manifest.getRecycleBins() != null && manifest.getRecycleBins().containsKey(key)) {
			return key;
		}
		String folderKey = key + ResourceExtension.FOLDER.getValue();
		if (manifest.getRecycleBins() != null && manifest.getRecycleBins().containsKey(folderKey)) {
			return folderKey;
		}
		return key;
	}

	private ObjectNode readSidecar(ImportManagerContext context, String fileName) {
		String path = String.join(File.separator, context.getResourcesDirectory(), fileName);
		try (FileInputStream inputStream = new FileInputStream(path)) {
			return mapperUtils.mapToDTO(inputStream, ObjectNode.class);
		} catch (Exception e) {
			logger.warn("Could not read recycle-bin sidecar {}: {}", fileName, e.getMessage());
			return null;
		}
	}

	private User resolveSidecarUser(JsonNode userNode) {
		if (userNode == null || userNode.isNull() || !userNode.isObject()) {
			return null;
		}
		UserDTO userDto = mapperUtils.mapToDTO(userNode.toString(), UserDTO.class);
		if (userDto == null) {
			return null;
		}
		return shareUtils.getOrInsertUser(userDto);
	}
}
