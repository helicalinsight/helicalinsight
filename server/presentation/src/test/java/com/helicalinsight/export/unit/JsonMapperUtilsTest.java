package com.helicalinsight.export.unit;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ResourceDependencySorter;

public class JsonMapperUtilsTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testMapToDTOFromString() {
		JsonMapperUtils utils = new JsonMapperUtils();
		ImportRequest request = utils.mapToDTO("{\"onConflict\":\"update\"}", ImportRequest.class);
		Assert.assertNotNull(request);
		Assert.assertEquals("update", request.getOnConflict());
	}

	@Test
	public void ut_a2_testMapToDTOReturnsNullOnInvalidJson() {
		JsonMapperUtils utils = new JsonMapperUtils();
		ImportRequest request = utils.mapToDTO("invalid-json", ImportRequest.class);
		Assert.assertNull(request);
	}

	@Test
	public void ut_a3_testAsListWithSingleNode() {
		JsonMapperUtils utils = new JsonMapperUtils();
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("key", "value");
		List<JsonNode> list = utils.asList(node);
		Assert.assertEquals(1, list.size());
	}

	@Test
	public void ut_a4_testAsListWithArrayNode() {
		JsonMapperUtils utils = new JsonMapperUtils();
		ArrayNode array = JsonNodeFactory.instance.arrayNode();
		array.add("a");
		array.add("b");
		List<JsonNode> list = utils.asList(array);
		Assert.assertEquals(2, list.size());
	}

	@Test
	public void ut_a4b_testMapToArrayFromFile() throws Exception {
		JsonMapperUtils utils = new JsonMapperUtils();
		File file = File.createTempFile("array", ".json");
		try (FileWriter writer = new FileWriter(file)) {
			writer.write("[\"a\",\"b\"]");
		}
		try (FileInputStream fis = new FileInputStream(file)) {
			ArrayNode array = utils.mapToArray(fis);
			Assert.assertNotNull(array);
			Assert.assertEquals(2, array.size());
		}
		file.delete();
	}

	@Test
	public void ut_a5_testMapToDTOFromFileInputStream() throws Exception {
		JsonMapperUtils utils = new JsonMapperUtils();
		File file = File.createTempFile("dto", ".json");
		try (FileWriter writer = new FileWriter(file)) {
			writer.write("{\"onConflict\":\"skip\"}");
		}
		try (FileInputStream fis = new FileInputStream(file)) {
			ImportRequest request = utils.mapToDTO(fis, ImportRequest.class);
			Assert.assertNotNull(request);
			Assert.assertEquals("skip", request.getOnConflict());
		}
		file.delete();
	}

	@Test
	public void ut_a6_testMapToArrayReturnsNullOnError() throws Exception {
		JsonMapperUtils utils = new JsonMapperUtils();
		File file = File.createTempFile("bad", ".json");
		try (FileInputStream fis = new FileInputStream(file)) {
			Assert.assertNull(utils.mapToArray(fis));
		}
		file.delete();
	}

	@Test
	public void ut_a7_testMapToDTOFromStreamReturnsNullOnError() throws Exception {
		JsonMapperUtils utils = new JsonMapperUtils();
		File file = File.createTempFile("bad-dto", ".json");
		try (FileInputStream fis = new FileInputStream(file)) {
			Assert.assertNull(utils.mapToDTO(fis, ImportRequest.class));
		}
		file.delete();
	}

	@Test
	public void ut_a8_testAsListFromFile() throws Exception {
		JsonMapperUtils utils = new JsonMapperUtils();
		File file = File.createTempFile("list", ".json");
		try (FileWriter writer = new FileWriter(file)) {
			writer.write("[\"x\",\"y\"]");
		}
		List<String> result = utils.asList(file, new TypeReference<List<String>>() {});
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		file.delete();
	}

	@Test
	public void ut_a9_testAsListFromFileReturnsNullOnError() throws Exception {
		JsonMapperUtils utils = new JsonMapperUtils();
		File file = File.createTempFile("bad-list", ".json");
		List<String> result = utils.asList(file, new TypeReference<List<String>>() {});
		Assert.assertNull(result);
		file.delete();
	}

}


