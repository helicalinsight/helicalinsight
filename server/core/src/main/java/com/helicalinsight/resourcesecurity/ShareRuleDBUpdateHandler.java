package com.helicalinsight.resourcesecurity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.ResourceConstants;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 08-07-2015.
 * Updated by modified on 22-12-2021
 *
 * @author Rajasekhar
 * @modified: karthik
 */
public class ShareRuleDBUpdateHandler extends ShareRuleXmlUpdateHandler implements IComponent {


    @Override
    public String executeComponent(String jsonFormData) {
        Map<String, Object> responseMap = responseJSONMap(jsonFormData);
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        String type = formData.getString("type");
        String dir = formData.optString("dir");
        String file = formData.optString("file");
        String classifier = formData.optString("classifier");

        if (type.equals("file") && StringUtils.isNotEmpty(file)) {
            dir = dir + ResourceConstants.FILE_SEPERATOR + file;
        }
        Map<String, Object> shareMap = new HashMap<>();
        Map<String, Object> revokeMap = new HashMap<>();

        JSONObject shareJson = null;
        if (responseMap.containsKey("share")) {
            shareMap = (Map<String, Object>) responseMap.get("share");
        }

        if (responseMap.containsKey("revoke")) {
            revokeMap = (Map<String, Object>) responseMap.get("revoke");
        }

        if (formData.has("share")) {
            shareJson = formData.getJSONObject("share");
        }

        JSONObject revokeJson = null;
        if (formData.has("revoke")) {
            revokeJson = formData.getJSONObject("revoke");
        }

        JSONObject model = new JSONObject();
        String typeMessage=type;
        if("dataSource".equalsIgnoreCase(type)){
            typeMessage="datasource";
        }
        model.accumulate("message", "The selected " + typeMessage + " privileges are updated " +
                "successfully.");

        if ("efwd".equals(classifier)) {
            new EfwdShareRuleDBUpdateHandler().handleDataSource(type, responseMap, shareMap, revokeMap, dir);
            return model.toString();
        }

        handleDataSource(responseMap, shareMap, revokeMap, dir, type);

        return model.toString();
    }

    private void handleDataSource(Map<String, Object> responseMap, Map<String, Object> shareMap, Map<String, Object> revokeMap, String dir, String type) {
        GlobalConnectionService globalConnectionService = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
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
            globalId = Integer.valueOf((String) responseMap.get("id"));
        }

        boolean isRevoke = false;
        //GlobalConnection Security

        String createdBy = AuthenticationUtils.getUserId();

        if (responseMap.containsKey("revoke")) {
            isRevoke = true;
            Map<String, Object> revoke = (Map<String, Object>) responseMap.get("revoke");
            if (revoke.containsKey("role")) {
                List<Map<String, Object>> roleData = (List<Map<String, Object>>) revoke.get("role");
                if (roleData != null && roleData.size() > 0) {
                    iterateUserOrgRole("" + globalId, roleData, "role", isRevoke, globalConnectionService, createdBy, hiResourceServiceDB, allResources.getResourceDTOList(), dir, type);
                }
            }

            if (revoke.containsKey("user")) {
                List<Map<String, Object>> userData = (List<Map<String, Object>>) revoke.get("user");
                if (userData != null && userData.size() > 0) {
                    iterateUserOrgRole("" + globalId, userData, "user", isRevoke, globalConnectionService, createdBy, hiResourceServiceDB, allResources.getResourceDTOList(), dir, type);
                }
            }

            if (revoke.containsKey("organization")) {
                List<Map<String, Object>> orgData = (List<Map<String, Object>>) revoke.get("organization");
                if (orgData != null && orgData.size() > 0) {
                    iterateUserOrgRole("" + globalId, orgData, "organization", isRevoke, globalConnectionService, createdBy, hiResourceServiceDB, allResources.getResourceDTOList(), dir, type);
                }
            }
        }
        if (responseMap.containsKey("share")) {
            isRevoke = false;
            Map<String, Object> share = (Map<String, Object>) responseMap.get("share");
            if (share.containsKey("role")) {
                List<Map<String, Object>> roleData = (List<Map<String, Object>>) share.get("role");
                if (roleData != null && roleData.size() > 0) {
                    iterateUserOrgRole("" + globalId, roleData, "role", isRevoke, globalConnectionService, createdBy, hiResourceServiceDB, allResources.getResourceDTOList(), dir, type);
                }
            }

            if (share.containsKey("user")) {
                List<Map<String, Object>> userData = (List<Map<String, Object>>) share.get("user");
                if (userData != null && userData.size() > 0) {
                    iterateUserOrgRole("" + globalId, userData, "user", isRevoke, globalConnectionService, createdBy, hiResourceServiceDB, allResources.getResourceDTOList(), dir, type);
                }
            }


            if (share.containsKey("organization")) {
                List<Map<String, Object>> orgData = (List<Map<String, Object>>) share.get("organization");
                if (orgData != null && orgData.size() > 0) {
                    iterateUserOrgRole("" + globalId, orgData, "organization", isRevoke, globalConnectionService, createdBy, hiResourceServiceDB, allResources.getResourceDTOList(), dir, type);
                }
            }
        }
    }

    private void iterateUserOrgRole(String id, List<Map<String, Object>> shareData, String shareType, boolean isRevoke, GlobalConnectionService globalConnectionService, String createdBy, HIResourceServiceDB hiResourceServiceDB, List<HIResourceDTO> dtoList, String dir, String type) {
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
            if (data.get("permission") != null) {
                if (data.get("permission") instanceof String) {
                    permission = Integer.valueOf((String) data.get("permission"));
                } else {
                    permission = (Integer) data.get("permission");
                }

            }


            DBProcessor dbProcessor = new DBProcessor();
            Integer idFromResourcePath = dbProcessor.findIdFromResource(dtoList, dir);
            boolean dataSource = type.equals("dataSource");
            if (isRevoke) {
                if (shareType.equalsIgnoreCase("user")) {
                    if (dataSource) {
                        globalId = Integer.parseInt(id);
                        globalConnectionService.deleteGlobalConnectionSecurityByGlobalConnectionId(globalId, id1, null, null);
                    } else {
                        hiResourceServiceDB.deleteHIResourceSecurityByResourceId(idFromResourcePath, id1, null, null);
                    }
                }
                if (shareType.equalsIgnoreCase("organization")) {

                    if (dataSource) {
                        globalId = Integer.parseInt(id);
                        globalConnectionService.deleteGlobalConnectionSecurityByGlobalConnectionId(globalId, null, id1, null);
                    } else {
                        hiResourceServiceDB.deleteHIResourceSecurityByResourceId(idFromResourcePath, null, id1, null);
                    }
                }
                if (shareType.equalsIgnoreCase("role")) {
                    if (dataSource) {
                        globalId = Integer.parseInt(id);
                        globalConnectionService.deleteGlobalConnectionSecurityByGlobalConnectionId(globalId, null, null, id1);
                    } else {
                        hiResourceServiceDB.deleteHIResourceSecurityByResourceId(idFromResourcePath, null, null, id1);
                    }
                }
            } else {
                if (shareType.equalsIgnoreCase("user")) {
                    if (dataSource) {
                        globalId = Integer.parseInt(id);
                        globalConnectionService.updateOrInsert(globalId, id1, null, null, permission, createdBy);
                    } else {
                        hiResourceServiceDB.updateOrInsert(idFromResourcePath, id1, null, null, permission, createdBy);
                    }
                }
                if (shareType.equalsIgnoreCase("organization")) {
                    if (dataSource) {
                        globalId = Integer.parseInt(id);
                        globalConnectionService.updateOrInsert(globalId, null, id1, null, permission, createdBy);
                    } else {
                        hiResourceServiceDB.updateOrInsert(idFromResourcePath, null, id1, null, permission, createdBy);
                    }
                }
                if (shareType.equalsIgnoreCase("role")) {
                    if (dataSource) {
                        globalId = Integer.parseInt(id);
                        globalConnectionService.updateOrInsert(globalId, null, null, id1, permission, createdBy);
                    } else {
                        hiResourceServiceDB.updateOrInsert(idFromResourcePath, null, null, id1, permission, createdBy);
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> responseJSONMap(String jsonFormData) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };
        HashMap<String, Object> jsonFormDataMap = new HashMap<>();
        try {
            jsonFormDataMap = mapper.readValue(jsonFormData, typeRef);
            return jsonFormDataMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}