package com.helicalinsight.export.unit;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResourceDataWriterTest extends ExportUnitTestBase {

	private static final String TEMPDIR = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();

	@Test
	public void ut_a1_testWriteSimplePath() throws Exception {
		ResourceDataWriter writer = new ResourceDataWriter();
		HIResourceDTO resource = new HIResourceDTO();
		resource.setPath("datasource.json");
		String dir = "export-" + System.nanoTime();
		Map<String, String> payload = new HashMap<>();
		payload.put("name", "test-datasource");

		writer.write(payload, dir, resource, ResourceSuffix.DATASOURCE);

		File output = new File(TEMPDIR, String.join(File.separator, dir, "resources", "datasource.json_datasource"));
		Assert.assertTrue(output.exists());
		String content = Files.readString(output.toPath(), StandardCharsets.UTF_8);
		Assert.assertTrue(content.contains("test-datasource"));
		output.delete();
		new File(TEMPDIR, dir).delete();
	}

	@Test
	public void ut_a2_testWriteScheduleResultSuffixRemovesExtension() throws Exception {
		ResourceDataWriter writer = new ResourceDataWriter();
		HIResourceDTO resource = new HIResourceDTO();
		resource.setPath("schedule/report.efwsr");
		String dir = "export-" + System.nanoTime();
		Map<String, String> payload = new HashMap<>();
		payload.put("status", "ok");

		writer.write(payload, dir, resource, ResourceSuffix.SCHEDULE_RESULT);

		File output = new File(TEMPDIR, String.join(File.separator, dir, "resources", "schedule/report.efwsr"));
		Assert.assertTrue(output.exists());
		output.delete();
		new File(TEMPDIR, String.join(File.separator, dir, "resources", "schedule")).delete();
		new File(TEMPDIR, dir).delete();
	}

	@Test
	public void ut_a3_testWriteNestedPathWithManifestVersion() throws Exception {
		ApplicationProperties properties = ApplicationProperties.getInstance();
		String originalVersion = properties.getManifestVersion();
		properties.setManifestVersion("1");
		try {
			ResourceDataWriter writer = new ResourceDataWriter();
			HIResourceDTO resource = new HIResourceDTO();
			resource.setPath("nested/folder/datasource.json");
			String dir = "export-" + System.nanoTime();
			Map<String, String> payload = new HashMap<>();
			payload.put("key", "value");

			writer.write(payload, dir, resource, ResourceSuffix.DATASOURCE_EFWD);

			File output = new File(TEMPDIR,
					String.join(File.separator, dir, "resources", "nested", "folder", "datasource.json_efwd_datasource"));
			Assert.assertTrue(output.exists());
			output.delete();
			new File(TEMPDIR, String.join(File.separator, dir, "resources", "nested", "folder")).delete();
			new File(TEMPDIR, String.join(File.separator, dir, "resources", "nested")).delete();
			new File(TEMPDIR, dir).delete();
		} finally {
			properties.setManifestVersion(originalVersion);
		}
	}

	@Test(expected = ResourceExportException.class)
	public void ut_a4_testWriteInvalidObjectThrows() {
		ResourceDataWriter writer = new ResourceDataWriter();
		HIResourceDTO resource = new HIResourceDTO();
		resource.setPath("bad/path.json");
		writer.write(new Object() {
			@SuppressWarnings("unused")
			public String getValue() {
				throw new RuntimeException("serialization failed");
			}
		}, "invalid-export-" + System.nanoTime(), resource, ResourceSuffix.DATASOURCE);
	}
}
