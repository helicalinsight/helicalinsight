package com.helicalinsight.admin.management;

import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.DirectoryLoaderProxy;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.efw.utility.ResourceTypeIDMap;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Somen
 *         Created  on 17/4/2017.
 */
@SuppressWarnings("unused")
public class ReportStatisticsProvider implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        JSONArray extensionArray = formData.optJSONArray("extension");
        Integer totalSize = formData.optInt("depth");
        String searchKey = formData.optString("searchKey");
        searchKey = searchKey == null ? "" : searchKey;
        if ((totalSize == 0)) {
            totalSize = 4;
        }
        List<String> extensionList = prepareExtensionsList(extensionArray);

            return prepareResultPlain(searchKey, extensionList, totalSize);
    }

    private List<String> prepareExtensionsList(JSONArray extensionArray) {
        List<String> extensionList;
        if (extensionArray != null) {
            extensionList = extensionArray;
        } else {
            extensionList = new ArrayList<>();
            extensionList.add(JsonUtils.getDesignerExtension());
            extensionList.add(JsonUtils.getHrReportExtension());
        }

        return extensionList;
    }



    private String prepareResultForCache(String searchKey, List<String> extensionList, Integer totalSize) {

        searchKey = searchKey == null ? "" : searchKey;

        JSONObject responseJson = new JSONObject();
        IComponent fileBrowserSearchHandler = FactoryMethodWrapper.getTypedInstance("com.helicalinsight.admin.management.FileBrowserSearchHandler", IComponent.class);
        JSONObject preparedFormData = new JSONObject();
        preparedFormData.put("searchElement", searchKey);
        preparedFormData.put("type", extensionList);
        preparedFormData.put("isForReportStats", true);
        String resources = fileBrowserSearchHandler.executeComponent(preparedFormData.toString());
        JSONObject result = JSONObject.fromObject(resources);
        JSONArray requiredList = result.getJSONArray("searchResult");
        int size = requiredList.size();
        responseJson.put("reportsCount", size);
        if (size > 0) {

            Collections.sort(requiredList);
            List efwReportVOs = requiredList.subList(0, Math.min(size, Math.min(totalSize, size)));
            responseJson.put("latestReports", efwReportVOs);
        }


        return responseJson.toString();
    }

    private String prepareResultPlain(String searchKey, List<String> extensionList, Integer totalSize) {
    	JSONObject responseJson = new JSONObject();
    	List<EFWReportVO> requiredList=filterResourcesByExtension(extensionList);


        int size = requiredList.size();
        responseJson.put("reportsCount", size);
        responseJson.put("latestReports", requiredList.subList(0, Math.min(size, Math.min(totalSize, size))));



        return responseJson.toString();
    }

	private List<EFWReportVO> filterResourcesByExtension(List<String> extensionList) {
		
		List<EFWReportVO> requiredList = new ArrayList<>();
		List<Long> resourceTypeIds=new ArrayList<>();
		extensionList.forEach(s->{
			resourceTypeIds.add(ResourceTypeIDMap.getResourceIdByName(s));
		});
		Integer userId = Integer.valueOf(AuthenticationUtils.getUserId());
		HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceOfActiveUser allResourcesIncludingReadOnly = hiResourceServiceDB.findAllResourcesIncludingReadOnly();
         List<HIResourceDTO> resourceDTOList = allResourcesIncludingReadOnly.getResourceDTOList();
         if(resourceDTOList !=null && !resourceDTOList.isEmpty())
             for(HIResourceDTO dto:resourceDTOList)
					iterate(dto,extensionList,requiredList);

		return requiredList;
	}

	private void iterate(HIResourceDTO e,List<String> extensionList,List<EFWReportVO> requiredList) {
		if(!e.getType().equals("folder") && extensionList.contains(e.getExtension())) {
				String url=e.getPath();
				String fileName=e.getName();
				String parent=url.substring(0,url.length()-fileName.length()-1);
				EFWReportVO efwReportVO=new EFWReportVO(url, e.getLastModified(), e.getTitle(), parent, fileName, "//"+parent);
                if(e.getDeleted()==null || !e.getDeleted()) {
                    requiredList.add(efwReportVO);
                }
		}else {

			List<HIResourceDTO> contents = e.getChildren();
            if(contents!=null) {
                contents.forEach(obj->{
                    iterate(obj,extensionList,requiredList);
                });
            }
		}
	}
}

