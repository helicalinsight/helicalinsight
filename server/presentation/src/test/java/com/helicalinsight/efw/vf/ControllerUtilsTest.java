package com.helicalinsight.efw.vf;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.controllerutils.ServletContextFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ControllerUtilsTest {

	@Test
	public void testgetMessageChain() {
		Exception ex = new Exception();
		ControllerUtils.getMessageChain(ex);

	}

	@Test
	public void teststatusZeroJson() {

		JsonObject statusZeroJson = ControllerUtils.statusZeroJson("message");

	}

	@Test
	public void testLoadResources() {
		ControllerUtils.loadResource("desktop", true);
	}

	@Test
	public void testdefaultContentType() {
		ControllerUtils.defaultContentType();
	}

	@Test
	public void testdefaultCharSet() {
		ControllerUtils.defaultCharSet();
	}

	@Test
	public void testvalidate() {
		JsonObject obj = new JsonObject();
		obj.addProperty("jdbcUrl", "helical.com");
		obj.addProperty("driverName", "mysql");
		obj.addProperty("name", "name");

		ControllerUtils.validate(obj);
	}

	@Test
	public void testgetDataFromResponse() {
		JsonObject jsonServiceResult = new JsonObject();
		jsonServiceResult.addProperty("response", "message");
		ControllerUtils.getDataFromResponse(jsonServiceResult);
	}

	@Test
	public void testconcatenateParameters() {
		JsonObject reportParameters = new JsonObject();
		reportParameters.addProperty("keyName", "data");
		reportParameters.addProperty("csvdata", "data");
		ControllerUtils.concatenateParameters(reportParameters);
	}

	@Test
	public void testreplaceFilePath() {
		JsonArray resourceArray = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "type");
		obj.addProperty("path", "path");
		JsonArray arr = new JsonArray();

		obj.add("children", arr);
		resourceArray.add(obj);

		ControllerUtils.replaceFilePath(resourceArray);
	}

	@Test
	public void testReplaceFilePath() {
		JsonArray resourceArray = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "type");
		obj.addProperty("path", "path");

		resourceArray.add(obj);

		ControllerUtils.replaceFilePath(resourceArray);
	}

	//@Test
	public void testgetJSONOfVisibleExtensionTags() {
		JsonObject obj = new JsonObject();
		obj.addProperty("visible", "visible");
		JsonObject key = new JsonObject();
		obj.add("key", key);
		ControllerUtils.getJSONOfVisibleExtensionTags(obj);
	}

	@Test
	public void testprepareLevel() {
		JsonObject index = new JsonObject();
		index.addProperty("children", "children");
		JsonArray array = new JsonArray();
		array.add(index);
		ControllerUtils.prepareLevel(array, true);
		ControllerUtils.prepareLevel(array, false);

	}

	@Test
	public void testServletContextFactory() throws Exception {
		ServletContextFactory factory = new ServletContextFactory();
		factory.getObject();
	}

}
