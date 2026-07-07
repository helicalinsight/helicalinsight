package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.utils.FileDeleteUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SimpleMetadataDeleteHandlerTest {

	@Test
	public void ut_a1_test_deleteMetadata() {
		SimpleMetadataDeleteHandler deleteHandler = new SimpleMetadataDeleteHandler();
		JsonArray metadataFileName = new JsonArray();
		metadataFileName.add("metaList");
		metadataFileName.add("metaFilePath");
		JsonObject responseJson = new JsonObject();
		JsonArray reportFileName = new JsonArray();
		String deleteMetadata = deleteHandler.deleteMetadata(metadataFileName, responseJson, reportFileName);
		String response = JsonParser.parseString(deleteMetadata).getAsJsonObject().get("message").getAsString();
		assertEquals("Metadata cannot be deleted",response);
	}
	
	@Test
	public void ut_a2_test_deleteMetadata() {
		SimpleMetadataDeleteHandler deleteHandler = new SimpleMetadataDeleteHandler();
		JsonArray metadataFileName = new JsonArray();
		metadataFileName.add("metaList");
		metadataFileName.add("metaFilePath");
		JsonObject responseJson = new JsonObject();
		JsonArray reportFileName = new JsonArray();
		
		List<String> deletedMetadataList = new ArrayList<>();
		deletedMetadataList.add("deletedMetadataList");
		try(MockedStatic<FileDeleteUtils> mockedStatic = mockStatic(FileDeleteUtils.class)){
			mockedStatic.when(()-> FileDeleteUtils.deleteRequestedMetadata(any())).thenReturn(deletedMetadataList);
			String deleteMetadata = deleteHandler.deleteMetadata(metadataFileName, responseJson, reportFileName);
			String response = JsonParser.parseString(deleteMetadata).getAsJsonObject().get("message").getAsString();
			assertEquals("Metadata deleted successfully",response);
			
		}
	}
}
