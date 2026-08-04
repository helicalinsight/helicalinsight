package com.helicalinsight.export.unit;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ManifestException;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceFileUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;

public class ManifestUtilsTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testInsertPathAndDependency() {
		ManifestUtils utils = new ManifestUtils();
		Manifest manifest = new Manifest();
		utils.insertPath("path1", manifest);
		utils.insertDependency("key", "dep1,dep2", manifest);

		Assert.assertTrue(manifest.getResourcePaths().contains("path1"));
		Assert.assertTrue(manifest.getDependencies().containsKey("key"));
		Assert.assertEquals(2, manifest.getDependencies().get("key").size());
	}

	@Test
	public void ut_a2_testInsertDependencyAddsFolderExtension() {
		ManifestUtils utils = new ManifestUtils();
		Manifest manifest = new Manifest();
		utils.insertDependency("key", "folder", manifest);
		Assert.assertTrue(manifest.getDependencies().get("key").get(0).endsWith(".efwfolder"));
	}

	@Test
	public void ut_a3_testInsertShareAndDatasource() {
		ManifestUtils utils = new ManifestUtils();
		Manifest manifest = new Manifest();
		manifest.setVersion("1");
		HIResourceDTO resource = new HIResourceDTO();
		resource.setPath("folder/report");
		resource.setName("report");

		utils.insertShare(resource, manifest);
		utils.insertDatasource(resource, manifest);
		utils.insertDatasourceEFWD(resource, manifest);
		utils.insertSchedules(resource, manifest);

		Assert.assertTrue(manifest.getShares().containsKey("folder/report"));
		Assert.assertTrue(manifest.getDataSources().containsKey("folder/report"));
		Assert.assertTrue(manifest.getDataSources().containsKey("folder/reportefwd"));
		Assert.assertTrue(manifest.getSchedules().containsKey("folder/report"));
	}

	@Test
	public void ut_a4_testInsertShareVersionZero() {
		ManifestUtils utils = new ManifestUtils();
		Manifest manifest = new Manifest();
		HIResourceDTO resource = new HIResourceDTO();
		resource.setPath("folder/report");
		resource.setName("report");
		utils.insertShare(resource, manifest);
		Assert.assertTrue(manifest.getShares().get("folder/report").contains("report"));
	}

	@Test
	public void ut_a5_testGettersAndExists() {
		ManifestUtils utils = new ManifestUtils();
		Manifest manifest = new Manifest();
		manifest.getShares().put("k", "v");
		manifest.getDataSources().put("d", "ds");
		manifest.getDependencies().put("dep", Arrays.asList("a"));
		manifest.getResourcePaths().add("path");

		Assert.assertEquals("v", utils.getShare("k", manifest));
		Assert.assertEquals("ds", utils.getDatasource("d", manifest));
		Assert.assertEquals(Arrays.asList("a"), utils.getDependency("dep", manifest));
		Assert.assertTrue(utils.dependencyExists("dep", manifest));
		Assert.assertFalse(utils.dependencyExists("missing", manifest));
		Assert.assertTrue(utils.pathExists("path", manifest));
		Assert.assertFalse(utils.pathExists("missing", manifest));
	}

	@Test
	public void ut_a6_testCompareOptionsShare() {
		ManifestUtils utils = new ManifestUtils();
		Manifest manifest = new Manifest();
		ResourceOptions mOptions = new ResourceOptions();
		mOptions.setShare(true);
		manifest.setOptions(mOptions);
		ResourceOptions rOptions = new ResourceOptions();
		rOptions.setShare(true);
		Assert.assertTrue(utils.compareOptions(rOptions, manifest, "share"));
	}

	@Test
	public void ut_a7_testCompareOptionsDatasource() {
		ManifestUtils utils = new ManifestUtils();
		Manifest manifest = new Manifest();
		ResourceOptions mOptions = new ResourceOptions();
		mOptions.setDataSource(true);
		manifest.setOptions(mOptions);
		ResourceOptions rOptions = new ResourceOptions();
		rOptions.setDataSource(true);
		Assert.assertTrue(utils.compareOptions(rOptions, manifest, "datasource"));
	}

	@Test
	public void ut_a8_testCompareOptionsReturnsFalse() {
		ManifestUtils utils = new ManifestUtils();
		Manifest manifest = new Manifest();
		ResourceOptions rOptions = new ResourceOptions();
		Assert.assertFalse(utils.compareOptions(rOptions, manifest, "share"));
		Assert.assertFalse(utils.compareOptions(rOptions, manifest, "datasource"));
		Assert.assertFalse(utils.compareOptions(rOptions, manifest, "unknown"));
	}

	@Test
	public void ut_a9_testReadManifest() throws Exception {
		ManifestUtils utils = new ManifestUtils();
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Field field = ManifestUtils.class.getDeclaredField("fileUtils");
		field.setAccessible(true);
		field.set(utils, fileUtils);
		Manifest manifest = new Manifest();
		when(fileUtils.readFile("dir/Manifest.json", Manifest.class)).thenReturn(manifest);
		Assert.assertEquals(manifest, fileUtils.readFile("dir/Manifest.json",Manifest.class));
	}

	@Test(expected = ManifestException.class)
	public void ut_b1_testWriteManifestThrows() {
		ManifestUtils utils = new ManifestUtils();
		utils.writeManifest(new Manifest(), "/invalid/path/that/does/not/exist/sub");
	}
}
