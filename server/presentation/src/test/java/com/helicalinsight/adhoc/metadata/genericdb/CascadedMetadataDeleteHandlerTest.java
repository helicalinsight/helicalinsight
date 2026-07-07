package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CascadedMetadataDeleteHandlerTest {

	@Test
	public void ut_a1_test_deleteMetadata() {
		CascadedMetadataDeleteHandler cascadedMetadataDeleteHandler = new CascadedMetadataDeleteHandler();
		JsonArray metadataFileName = new JsonArray();
		metadataFileName.add("");
		metadataFileName.add("metaFile");
		JsonObject responseJson = new JsonObject();
		JsonArray reportFileName = new JsonArray();
		reportFileName.add("");
		reportFileName.add("reportFile");
		String deleteMetadata = cascadedMetadataDeleteHandler.deleteMetadata(metadataFileName, responseJson,
				reportFileName);
		String response = JsonParser.parseString(deleteMetadata).getAsJsonObject().get("message").getAsString();
		assertEquals("Metadata and related files deleted successfully.", response);
	}

	@Test
	public void ut_a2_test_deleteMetadata() {
		CascadedMetadataDeleteHandler cascadedMetadataDeleteHandler = new CascadedMetadataDeleteHandler();
		JsonArray metadataFileName = new JsonArray();
		JsonObject responseJson = new JsonObject();
		JsonArray reportFileName = new JsonArray();
		reportFileName.add("");
		reportFileName.add("reportFile");
		String deleteMetadata = cascadedMetadataDeleteHandler.deleteMetadata(metadataFileName, responseJson,
				reportFileName);
		String response = JsonParser.parseString(deleteMetadata).getAsJsonObject().get("message").getAsString();
		assertEquals("Metadata and related files can't be deleted.", response);

	}
}
