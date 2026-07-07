package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.RuntimeIOException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * The DatabaseCubeUpdateHandler class is responsible for updating cube metadata
 * based on the provided JSON form data. It implements the {@link IComponent}
 * interface and provides methods to execute the component and determine its
 * thread safety for caching purposes.
 * 
 * @author Somen
 */
@SuppressWarnings("unused")
public class DatabaseCubeUpdateHandler implements IComponent {

	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}

	/**
	 * Executes the component logic to update cube metadata based on the provided
	 * JSON form data. This method first validates the length of the cube filename
	 * within the specified range. it retrieves the cube handler bean and uses it to
	 * handle the cube, obtaining the process details.
	 * 
	 * 
	 * @param jsonFormData JSON data containing the form parameters such as fileName
	 * @return a JSON string representing the response message
	 */
	@SuppressWarnings("ConstantConditions")
	@Override
	public String executeComponent(String jsonFormData) {

		JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
		if (formJson.has("fileName")) {
			String fileName = formJson.get("fileName").getAsString();
			if (fileName.length() < 3 ) {
				throw new RuntimeIOException("Cube filename should be between 3 and 60 characters.");
			}
		}

		CubeHandler cubeHandler = (CubeHandler) ApplicationContextAccessor.getBean("cubeHandler");
		List<ProcessDetails> processDetails = cubeHandler.handleCube(formJson);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("message", "Cube saved successfully");
		JsonArray dataObj = new JsonArray();
		JsonObject dataObItem = new JsonObject();
		JsonArray cubes = new JsonArray();

		HICubeDAOService hiCubeDAOService = ApplicationContextAccessor.getBean(HICubeDAOService.class);
		for (ProcessDetails item : processDetails) {

			String object = hiCubeDAOService.getCubeAsJsonObj(Integer.valueOf(item.getCubeId())).toString();
			JsonObject cubeAsJsonObj = JsonParser.parseString(object).getAsJsonObject();
			cubes.addAll(cubeAsJsonObj.getAsJsonArray("cubes"));
		}
		jsonObject.add("cubes", cubes);
		GsonUtility.accumulateAll(dataObItem, formJson.getAsJsonObject("data"));
		dataObj.add(dataObItem);
		jsonObject.add("data", dataObj);
		jsonObject.add("metadata", formJson.getAsJsonObject("metadata"));

		return jsonObject.toString();
	}

}