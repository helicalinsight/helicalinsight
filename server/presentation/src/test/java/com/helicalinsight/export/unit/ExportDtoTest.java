package com.helicalinsight.export.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.export.dto.AdvancedDatasourceWrapper;
import com.helicalinsight.export.dto.Conflict;
import com.helicalinsight.export.dto.DataSourceWrapper;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExportRequest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;

public class ExportDtoTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testManifestGettersSetters() {
		Manifest manifest = new Manifest();
		List<String> paths = new ArrayList<>(Arrays.asList("path1"));
		Map<String, String> shares = new HashMap<>();
		shares.put("k", "v");
		Map<String, String> schedules = new HashMap<>();
		schedules.put("s", "v");
		Map<String, String> dataSources = new HashMap<>();
		dataSources.put("d", "v");
		Map<String, List<String>> dependencies = new HashMap<>();
		dependencies.put("dep", Arrays.asList("a"));
		Map<String, List<String>> images = new HashMap<>();
		images.put("img", Arrays.asList("i"));
		ResourceOptions options = new ResourceOptions();

		manifest.setVersion("2");
		manifest.setResourcePaths(paths);
		manifest.setShares(shares);
		manifest.setSchedules(schedules);
		manifest.setDataSources(dataSources);
		manifest.setDependencies(dependencies);
		manifest.setImages(images);
		manifest.setOptions(options);

		Assert.assertEquals("2", manifest.getVersion());
		Assert.assertEquals(paths, manifest.getResourcePaths());
		Assert.assertEquals(shares, manifest.getShares());
		Assert.assertEquals(schedules, manifest.getSchedules());
		Assert.assertEquals(dataSources, manifest.getDataSources());
		Assert.assertEquals(dependencies, manifest.getDependencies());
		Assert.assertEquals(images, manifest.getImages());
		Assert.assertEquals(options, manifest.getOptions());
	}

	@Test
	public void ut_a2_testManifestDefaultVersion() {
		Manifest manifest = new Manifest();
		Assert.assertEquals("0", manifest.getVersion());
	}

	@Test
	public void ut_a3_testImportResponse() {
		ImportResponse response = new ImportResponse();
		response.setMessage("done");
		response.setInsertCount(1);
		response.setUpdateCount(2);
		response.setSkipCount(3);
		List<String> updates = Arrays.asList("u");
		List<String> skips = Arrays.asList("s");
		List<String> inserts = Arrays.asList("i");
		response.setUpdates(updates);
		response.setSkips(skips);
		response.setInserts(inserts);

		Assert.assertEquals("done", response.getMessage());
		Assert.assertEquals(1, response.getInsertCount());
		Assert.assertEquals(2, response.getUpdateCount());
		Assert.assertEquals(3, response.getSkipCount());
		Assert.assertEquals(updates, response.getUpdates());
		Assert.assertEquals(skips, response.getSkips());
		Assert.assertEquals(inserts, response.getInserts());
		Assert.assertTrue(response.toString().contains("done"));
	}

	@Test
	public void ut_a4_testImportRequest() {
		ImportRequest request = new ImportRequest();
		ResourceOptions options = new ResourceOptions();
		request.setOnConflict(Conflict.UPDATE);
		request.setUpload(true);
		request.setOptions(options);
		request.setDestination("dest");
		request.setKey("key");

		Assert.assertEquals(Conflict.UPDATE, request.getOnConflict());
		Assert.assertTrue(request.getUpload());
		Assert.assertEquals(options, request.getOptions());
		Assert.assertEquals("dest", request.getDestination());
		Assert.assertEquals("key", request.getKey());
	}

	@Test
	public void ut_a5_testResourceExportRequest() {
		ResourceExportRequest request = new ResourceExportRequest();
		ResourceOptions options = new ResourceOptions();
		request.setDir("dir");
		request.setFile("file");
		request.setOptions(options);

		Assert.assertEquals("dir", request.getDir());
		Assert.assertEquals("file", request.getFile());
		Assert.assertEquals(options, request.getOptions());
	}

	@Test
	public void ut_a6_testResourceOptions() {
		ResourceOptions options = new ResourceOptions();
		options.setSchedules(true);
		options.setDataSource(true);
		options.setShare(false);

		Assert.assertTrue(options.getSchedules());
		Assert.assertTrue(options.getDataSource());
		Assert.assertFalse(options.getShare());
		Assert.assertTrue(options.toString().contains("schedules"));
	}

	@Test
	public void ut_a7_testResourceExtension() {
		Assert.assertEquals(".efwfolder", ResourceExtension.FOLDER.getValue());
		Assert.assertEquals(".metadata", ResourceExtension.METADATA.getValue());
		Assert.assertEquals(".hr", ResourceExtension.HREPORT.getValue());
		Assert.assertEquals(".efwdd", ResourceExtension.EFWDD.getValue());
		Assert.assertEquals(".efw", ResourceExtension.EFW.getValue());
		Assert.assertEquals(".zip", ResourceExtension.ZIP.getValue());
	}

	@Test
	public void ut_a8_testConflictConstants() {
		Assert.assertEquals("update", Conflict.UPDATE);
		Assert.assertEquals("skip", Conflict.SKIP);
	}

	@Test
	public void ut_a9_testDataSourceWrapper() {
		DataSourceWrapper wrapper = new DataSourceWrapper();
		Map<Integer, List<com.helicalinsight.datasource.model.GlobalConnectionSecurity>> securities = new HashMap<>();
		wrapper.setSecurities(securities);
		wrapper.setExtraOptions(new ArrayList<>());

		Assert.assertEquals(securities, wrapper.getSecurities());
		Assert.assertNotNull(wrapper.getExtraOptions());
		Assert.assertNull(wrapper.getTomcat());
		Assert.assertNull(wrapper.getHikari());
		Assert.assertNull(wrapper.getJndi());
		Assert.assertNull(wrapper.getNoSql());
	}

	@Test
	public void ut_b1_testAdvancedDatasourceWrapper() {
		AdvancedDatasourceWrapper wrapper = new AdvancedDatasourceWrapper();
		wrapper.setJdbc(new ArrayList<>());
		wrapper.setGroovy(new ArrayList<>());
		wrapper.setSecurities(new HashMap<>());

		Assert.assertNotNull(wrapper.getJdbc());
		Assert.assertNotNull(wrapper.getGroovy());
		Assert.assertNotNull(wrapper.getSecurities());
	}

}
