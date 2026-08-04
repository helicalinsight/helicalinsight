package com.helicalinsight.export.unit;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.helicalinsight.export.exception.ManifestException;
import com.helicalinsight.export.exception.RequestValidationException;
import com.helicalinsight.export.exception.ResourceExceptionHandler;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.exception.ZipResourceException;

public class ExportExceptionTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testResourceExportException() {
		ResourceExportException ex = new ResourceExportException("export error");
		Assert.assertEquals("export error", ex.getMessage());
		Assert.assertEquals("export error", ex.getLocalizedMessage());
	}

	@Test
	public void ut_a2_testResourceImportException() {
		ResourceImportException ex = new ResourceImportException("import error");
		Assert.assertEquals("import error", ex.getMessage());
	}

	@Test
	public void ut_a3_testZipResourceException() {
		ZipResourceException ex = new ZipResourceException("zip error");
		Assert.assertEquals("zip error", ex.getMessage());
	}

	@Test
	public void ut_a4_testManifestException() {
		ManifestException ex = new ManifestException("manifest error");
		Assert.assertEquals("manifest error", ex.getMessage());
	}

	@Test
	public void ut_a5_testRequestValidationException() {
		RequestValidationException ex = new RequestValidationException("validation error");
		Assert.assertEquals("validation error", ex.getMessage());
	}

	@Test
	public void ut_a6_testResourceExceptionHandler() {
		ResourceExceptionHandler handler = new ResourceExceptionHandler();
		ResourceExportException ex = new ResourceExportException("handled error");
		ResponseEntity<String> response = handler.resourceExceptionHandler(ex);

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertTrue(response.getBody().contains("handled error"));
		Assert.assertTrue(response.getBody().contains("\"status\":0"));
	}

}
