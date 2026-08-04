package com.helicalinsight.export.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.dto.OrganizationDTO;
import com.helicalinsight.admin.dto.ProfileDTO;
import com.helicalinsight.admin.dto.RoleDTO;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.exception.ZipResourceException;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ResourceFileUtils;
import com.helicalinsight.export.utils.ResourceShareUtils;
import com.helicalinsight.export.utils.ZipUtils;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

import net.lingala.zip4j.exception.ZipException;

public class ResourceFileUtilsTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testGetAllBytes() throws Exception {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		File file = File.createTempFile("bytes", ".txt");
		try (FileWriter writer = new FileWriter(file)) {
			writer.write("hello");
		}
		byte[] bytes = fileUtils.getAllBytes(file);
		Assert.assertNotNull(bytes);
		Assert.assertTrue(bytes.length > 0);
		file.delete();
	}

	@Test
	public void ut_a2_testGetAllBytesReturnsNullOnError() {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		Assert.assertNull(fileUtils.getAllBytes(new File("/nonexistent/path/file.txt")));
	}

	@Test
	public void ut_a3_testCreateSchema() {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		String dirName = "schema-test-" + System.nanoTime();
		Boolean result = fileUtils.createSchema(dirName);
		Assert.assertTrue(result);
		fileUtils.cleanDir(dirName);
	}

	@Test(expected = ResourceExportException.class)
	public void ut_a4_testCleanDirThrows() throws Exception {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		String dirName = "clean-throw-" + System.nanoTime();
		File dir = new File(TempDirectoryCleaner.getTempDirectory(), dirName);
		dir.mkdirs();
		File leaf = new File(dir, "leaf");
		leaf.createNewFile();
		fileUtils.cleanDir(dirName + File.separator + "leaf");
	}

	@Test(expected = ZipResourceException.class)
	public void ut_a5_testArchiveThrows() throws Exception {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		ZipUtils zipUtils = mock(ZipUtils.class);
		Field field = ResourceFileUtils.class.getDeclaredField("zipUtils");
		field.setAccessible(true);
		field.set(fileUtils, zipUtils);
		doThrow(new ZipException("fail")).when(zipUtils).archive(anyString(), anyString());
		fileUtils.archive("dir");
	}

	@Test(expected = ZipResourceException.class)
	public void ut_a6_testExtractThrows() throws Exception {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		ZipUtils zipUtils = mock(ZipUtils.class);
		Field field = ResourceFileUtils.class.getDeclaredField("zipUtils");
		field.setAccessible(true);
		field.set(fileUtils, zipUtils);
		doThrow(new ZipException("fail")).when(zipUtils).extract(anyString(), anyString());
		fileUtils.extract("zip");
	}

	@Test
	public void ut_a7_testReadFileWithShareOption() throws Exception {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		JsonMapperUtils jsonMapperUtils = mock(JsonMapperUtils.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Field f1 = ResourceFileUtils.class.getDeclaredField("jsonMapperUtils");
		f1.setAccessible(true);
		f1.set(fileUtils, jsonMapperUtils);
		Field f2 = ResourceFileUtils.class.getDeclaredField("shareHandler");
		f2.setAccessible(true);
		f2.set(fileUtils, shareHandler);

		ImportRequest importRequest = new ImportRequest();
		ResourceOptions options = new ResourceOptions();
		options.setShare(true);
		importRequest.setOptions(options);
		fileUtils.setRequest(importRequest);

		File file = File.createTempFile("read", ".json");
		try (FileWriter writer = new FileWriter(file)) {
			writer.write("{\"createdBy\":{\"username\":\"u\"}}");
		}
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		ObjectNode createdBy = JsonNodeFactory.instance.objectNode();
		createdBy.put("username", "u");
		node.set("createdBy", createdBy);
		when(jsonMapperUtils.mapToDTO(any(FileInputStream.class), eq(ObjectNode.class))).thenReturn(node);
		when(jsonMapperUtils.mapToDTO(anyString(), eq(Manifest.class))).thenReturn(new Manifest());
		when(shareHandler.saveOwner(any())).thenReturn(42);

		Manifest manifest = fileUtils.readFile(file.getAbsolutePath(), Manifest.class);
		Assert.assertNotNull(manifest);
		file.delete();
	}

	@Test
	public void ut_a8_testReadFileWithoutShareOption() throws Exception {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		JsonMapperUtils jsonMapperUtils = mock(JsonMapperUtils.class);
		Field f1 = ResourceFileUtils.class.getDeclaredField("jsonMapperUtils");
		f1.setAccessible(true);
		f1.set(fileUtils, jsonMapperUtils);

		ImportRequest importRequest = new ImportRequest();
		ResourceOptions options = new ResourceOptions();
		options.setShare(false);
		importRequest.setOptions(options);
		fileUtils.setRequest(importRequest);

		File file = File.createTempFile("read2", ".json");
		try (FileWriter writer = new FileWriter(file)) {
			writer.write("{\"createdBy\":{\"username\":\"u\"}}");
		}
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		ObjectNode createdBy = JsonNodeFactory.instance.objectNode();
		createdBy.put("username", "u");
		node.set("createdBy", createdBy);
		when(jsonMapperUtils.mapToDTO(anyString(), any())).thenReturn(new Manifest());

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);
			when(jsonMapperUtils.mapToDTO(any(FileInputStream.class), eq(ObjectNode.class))).thenReturn(node);
			when(jsonMapperUtils.mapToDTO(anyString(), eq(Manifest.class))).thenReturn(new Manifest());
			Manifest result = fileUtils.readFile(file.getAbsolutePath(), Manifest.class);
			Assert.assertNotNull(result);
		}
		file.delete();
	}

	@Test(expected = ResourceImportException.class)
	public void ut_a9_testReadFileThrowsOnMissingFile() {
		ResourceFileUtils fileUtils = new ResourceFileUtils();
		fileUtils.readFile(new File("/nonexistent/file.json"), Manifest.class);
	}

}