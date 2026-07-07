package com.helicalinsight.resourcesecurity;

import java.util.List;
import java.util.Map;

import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;

public class EfwdShareRuleDBUpdateHandler {

	String type = "";

	public void handleDataSource(String type, Map<String, Object> responseMap, Map<String, Object> shareMap,
			Map<String, Object> revokeMap, String dir) {
		this.type = type;
		EfwdDatasourceUtils.validatePermission(dir);
		EFWDConnectionService datasourceService = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
		HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
		String id = "";
		Integer globalId = null;
		HIResourceOfActiveUser allResources = hiResourceServiceDB.getResourceOfActiveUser();

		if (shareMap != null && !shareMap.isEmpty()) {
			responseMap.put("share", shareMap);
		}

		if (revokeMap != null && !revokeMap.isEmpty()) {
			responseMap.put("revoke", revokeMap);
		}

		if (responseMap.containsKey("id")) {
			globalId = Integer.valueOf(""+ responseMap.get("id"));
		}

		boolean isRevoke = false;
		// GlobalConnection Security

		String createdBy = AuthenticationUtils.getUserId();

		if (responseMap.containsKey("revoke")) {
			isRevoke = true;
			Map<String, Object> revoke = (Map<String, Object>) responseMap.get("revoke");
			if (revoke.containsKey("role")) {
				List<Map<String, Object>> roleData = (List<Map<String, Object>>) revoke.get("role");
				if (roleData != null && !roleData.isEmpty()) {
					iterateUserOrgRole("" + globalId, roleData, "role", isRevoke, datasourceService, createdBy,
							hiResourceServiceDB, allResources.getResourceDTOList(), dir);
				}
			}

			if (revoke.containsKey("user")) {
				List<Map<String, Object>> userData = (List<Map<String, Object>>) revoke.get("user");
				if (userData != null && !userData.isEmpty()) {
					if (userData != null || !userData.isEmpty()) {
						iterateUserOrgRole("" + globalId, userData, "user", isRevoke, datasourceService, createdBy,
								hiResourceServiceDB, allResources.getResourceDTOList(), dir);
					}
				}
			}

			if (revoke.containsKey("organization")) {
				List<Map<String, Object>> orgData = (List<Map<String, Object>>) revoke.get("organization");
				if (orgData != null && !orgData.isEmpty()) {
					iterateUserOrgRole("" + globalId, orgData, "organization", isRevoke, datasourceService, createdBy,
							hiResourceServiceDB, allResources.getResourceDTOList(), dir);
				}
			}
		}
		if (responseMap.containsKey("share")) {
			isRevoke = false;
			Map<String, Object> share = (Map<String, Object>) responseMap.get("share");
			if (share.containsKey("role")) {
				List<Map<String, Object>> roleData = (List<Map<String, Object>>) share.get("role");
				if (roleData != null && roleData.size() > 0) {
					iterateUserOrgRole("" + globalId, roleData, "role", isRevoke, datasourceService, createdBy,
							hiResourceServiceDB, allResources.getResourceDTOList(), dir);
				}
			}

			if (share.containsKey("user")) {
				List<Map<String, Object>> userData = (List<Map<String, Object>>) share.get("user");
				if (userData != null && !userData.isEmpty()) {
					iterateUserOrgRole("" + globalId, userData, "user", isRevoke, datasourceService, createdBy,
							hiResourceServiceDB, allResources.getResourceDTOList(), dir);
				}
			}

			if (share.containsKey("organization")) {
				List<Map<String, Object>> orgData = (List<Map<String, Object>>) share.get("organization");
				if (orgData != null && !orgData.isEmpty()) {
					iterateUserOrgRole("" + globalId, orgData, "organization", isRevoke, datasourceService, createdBy,
							hiResourceServiceDB, allResources.getResourceDTOList(), dir);
				}
			}
		}
	}

	private void iterateUserOrgRole(String id, List<Map<String, Object>> shareData, String shareType, boolean isRevoke,
			EFWDConnectionService datasourceService, String createdBy, HIResourceServiceDB hiResourceServiceDB,
			List<HIResourceDTO> dtoList, String dir) {
		Integer globalId = null;
		for (int i = 0; i < shareData.size(); i++) {
			Map<String, Object> data = shareData.get(i);
			Integer id1 = null;
			Integer permission = null;
			if (data.get("id") != null) {
				if (data.get("id") instanceof String) {
					id1 = Integer.valueOf((String) data.get("id"));
				} else {
					id1 = (Integer) data.get("id");
				}

			}
			if (null != data.get("permission")) {
				if (data.get("permission") instanceof String) {
					permission = Integer.valueOf((String) data.get("permission"));
				} else {
					permission = (Integer) data.get("permission");
				}

			}

			//DBProcessor dbProcessor = new DBProcessor();
//			Integer idFromResourcePath = dbProcessor.findIdFromResource(dtoList, dir);
			globalId = Integer.parseInt(id);
			if (isRevoke) {
				if (shareType.equalsIgnoreCase("user")) {
					datasourceService.deleteEFConnectionecurityByConnectionId(globalId, id1, null, null);

				}
				if (shareType.equalsIgnoreCase("organization")) {
					datasourceService.deleteEFConnectionecurityByConnectionId(globalId, null, null, id1);
				}
				if (shareType.equalsIgnoreCase("role")) {
					datasourceService.deleteEFConnectionecurityByConnectionId(globalId, null, id1, null);
				}
			} else {

				if ("user".equalsIgnoreCase(shareType)) {
					datasourceService.updateOrInsert(globalId, id1, null, null, permission);
				}
				if ("organization".equalsIgnoreCase(shareType)) {
					datasourceService.updateOrInsert(globalId, null, null, id1, permission);
				}
				if ("role".equalsIgnoreCase(shareType)) {
					datasourceService.updateOrInsert(globalId, null, id1, null, permission);
				}
			}
		}
	}

}
