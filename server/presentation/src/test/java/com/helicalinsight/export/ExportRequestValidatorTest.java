package com.helicalinsight.export;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.export.dto.ResourceExportRequest;
import com.helicalinsight.export.dto.validation.ExportRequestValidator;
import com.helicalinsight.export.exception.RequestValidationException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExportRequestValidatorTest {

	@Test(expected = RequestValidationException.class)
	public void ut_a1_testValidate() {
		ExportRequestValidator exportRequestValidator = new ExportRequestValidator();
		ResourceExportRequest dto = null;
		exportRequestValidator.validate(dto);
	}
	
	//@Test(expected = RequestValidationException.class)
	public void ut_a2_testValidate() {
		ExportRequestValidator exportRequestValidator = new ExportRequestValidator();
		ResourceExportRequest dto = mock(ResourceExportRequest.class);
		exportRequestValidator.validate(dto);
	}
	
	//@Test(expected = RequestValidationException.class)
	public void ut_a3_testValidate() {
		ExportRequestValidator exportRequestValidator = new ExportRequestValidator();
		ResourceExportRequest dto = mock(ResourceExportRequest.class);
		when(dto.getDir()).thenReturn("");
		exportRequestValidator.validate(dto);
	}
	
	//@Test(expected = RequestValidationException.class)
	public void ut_a4_testValidate() {
		ExportRequestValidator exportRequestValidator = new ExportRequestValidator();
		ResourceExportRequest dto = mock(ResourceExportRequest.class);
		when(dto.getDir()).thenReturn("dir.zip");
		exportRequestValidator.validate(dto);
	}
	//@Test(expected = RequestValidationException.class)
	public void ut_a5_testValidate() {
		ExportRequestValidator exportRequestValidator = new ExportRequestValidator();
		ResourceExportRequest dto = mock(ResourceExportRequest.class);
		when(dto.getDir()).thenReturn("di");
		exportRequestValidator.validate(dto);
	}
	
	//@Test(expected = RequestValidationException.class)
	public void ut_a6_testValidate() {
		ExportRequestValidator exportRequestValidator = new ExportRequestValidator();
		ResourceExportRequest dto = mock(ResourceExportRequest.class);
		String directory = "abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxya\r\n"
				+ "";
		when(dto.getDir()).thenReturn(directory);
		exportRequestValidator.validate(dto);
	}
	
	//@Test
	public void ut_a7_testValidate() {
		ExportRequestValidator exportRequestValidator = new ExportRequestValidator();
		ResourceExportRequest dto = mock(ResourceExportRequest.class);
		String directory = "directory";
		when(dto.getDir()).thenReturn(directory);
		exportRequestValidator.validate(dto);
	}
}
