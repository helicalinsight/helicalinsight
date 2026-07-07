package com.helicalinsight.adhoc.services;

import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIHcrQueryParameters;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HiHcrQuery;
import com.helicalinsight.datasource.service.EFWDConnectionService;

public abstract class AbstractHCRConnectionSave {
	
	@Autowired
	EFWDConnectionService efwServiceDB;

	private HIHcrConnections hiHcrConnections;
	
	public abstract void saveConnection(JsonObject connectionObj);
	
	public HIHcrConnections addHcrConnection(String type,HIResource hiResource) {
	    hiHcrConnections=new HIHcrConnections();
    	hiHcrConnections.setConnectionType(type);
    	hiHcrConnections.setHiResourceHcr(hiResource);
    	efwServiceDB.addHiHcrConnection(hiHcrConnections);
		return hiHcrConnections;
	}
	
	public void addQueryAndParams(JsonObject dataMap) {
			HiHcrQuery hcrQuery=new HiHcrQuery();
			hcrQuery.setHcrQueryName(dataMap.get("Name").getAsString());
		hcrQuery.setHcrQueryString(dataMap.get("Query").getAsString());
			hcrQuery.setHcrQueryType(dataMap.get("type").getAsString());
			hcrQuery.setHiHcrConnections(getHiHcrConnections());
			efwServiceDB.addHiHcrQuery(hcrQuery);
			if(dataMap.has("Parameters")){
				JsonElement arrObj=dataMap.get("Parameters").getAsJsonObject().get("Parameter");
				if(arrObj.isJsonObject()) {
					JsonObject arr = arrObj.getAsJsonObject();
					if (arr != null)
						saveParameters(arr, hcrQuery);
				}
				if(arrObj.isJsonArray()){
					JsonArray array = arrObj.getAsJsonArray();
					for (int index = 0; index < array.size(); index++) {
						JsonObject jsonObject = array.get(index).getAsJsonObject();
						if (jsonObject != null)
							saveParameters(jsonObject, hcrQuery);
					}
				}
			}

	}
	
	private void saveParameters(JsonObject parameter, HiHcrQuery hcrQuery) {
			HIHcrQueryParameters hiHcrQueryParameters=new HIHcrQueryParameters();
			hiHcrQueryParameters.setHiQuery(hcrQuery);
			hiHcrQueryParameters.setParamDefaultValue(parameter.get("default").getAsString());
			hiHcrQueryParameters.setParameterName(parameter.get("name").getAsString());
			hiHcrQueryParameters.setParameterType(parameter.get("type").getAsString());
			hiHcrQueryParameters.setOpenQuotes(parameter.get("openQuote").isJsonNull()?"":parameter.get("openQuote").getAsString());
			hiHcrQueryParameters.setCloseQuotes(parameter.get("closeQuote").isJsonNull()?"":parameter.get("closeQuote").getAsString());
			efwServiceDB.addHiHcrQueryParams(hiHcrQueryParameters);
	}

	public HIHcrConnections getHiHcrConnections() {
		return hiHcrConnections;
	}
}
