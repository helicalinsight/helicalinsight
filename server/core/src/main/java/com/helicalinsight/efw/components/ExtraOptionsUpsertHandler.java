package com.helicalinsight.efw.components;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GsonUtility;


@Component
public class ExtraOptionsUpsertHandler {
	
	private final DSExtraOptionSaveHandler extraOptionSaveHandler;
	
	public ExtraOptionsUpsertHandler(DSExtraOptionSaveHandler extraOptionSaveHandler ) {
		this.extraOptionSaveHandler = extraOptionSaveHandler;
	}

	public void upsert(JsonObject formDataJsonObject , String globalId ) {

		JsonObject extraOptions = GsonUtility.optJsonObject(formDataJsonObject, "extraOptions");
		if (extraOptions != null && !extraOptions.keySet().isEmpty()) {
			extraOptionSaveHandler.save(globalId, extraOptions);
			if (extraOptions.has("dataFile")) {
				String dataFileName =  GsonUtility.optString(extraOptions, "dataFile");
				String fileName = GsonUtility.optString(formDataJsonObject, "fileName");
				DataSourceUtils.moveFile(fileName, dataFileName, globalId);
			}
		}
	}

}
