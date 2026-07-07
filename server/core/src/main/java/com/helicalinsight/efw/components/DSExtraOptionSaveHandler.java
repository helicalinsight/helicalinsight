package com.helicalinsight.efw.components;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.model.DSExtraOption;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.utility.JsonUtils;
import static java.lang.Integer.parseInt;

@Component
public class DSExtraOptionSaveHandler {
	
	@Autowired
	private GlobalConnectionService globalConnectionService;
	
	public  void save(String globalIdStr , JsonObject extraOptions) {
    	
		Integer globalId = parseInt(globalIdStr);
		globalConnectionService.deleteExtraOptionByGloablId(globalId);
		GlobalConnections connection =  globalConnectionService.findGlobalConnectionById(globalId);
		JsonUtils.getKeys(extraOptions).stream()
				.filter(extraOptionKey ->  StringUtils.isNotBlank(GsonUtility.optString(extraOptions, extraOptionKey)))
				.map(extraOptionKey -> new DSExtraOption(connection,extraOptionKey,GsonUtility.optString(extraOptions, extraOptionKey)))
				.forEach(globalConnectionService::saveExtraOption);		
    }
}
