package com.helicalinsight.export;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.validation.ImportRequestValidator;
import com.helicalinsight.export.exception.RequestValidationException;

public class ImportRequestValidatorTest {

	@Test(expected = RequestValidationException.class)
	public void ut_a1_testValidate() {
		ImportRequestValidator importRequestValidator = new ImportRequestValidator();
		
		MultipartFile multipartFile = mock(MultipartFile.class);
		ImportRequest dto = null;
		importRequestValidator.validate(multipartFile, dto);
		
	}
	@Test(expected = RequestValidationException.class)
	public void ut_a2_testValidate() {
		ImportRequestValidator importRequestValidator = new ImportRequestValidator();
		
		MultipartFile multipartFile = mock(MultipartFile.class);
		ImportRequest dto = mock(ImportRequest.class);
		importRequestValidator.validate(multipartFile, dto);
		
	}
	@Test(expected = RequestValidationException.class)
	public void ut_a3_testValidate() {
		ImportRequestValidator importRequestValidator = new ImportRequestValidator();
		
		MultipartFile multipartFile = null;
		ImportRequest dto = mock(ImportRequest.class);
		importRequestValidator.validate(multipartFile, dto);
		
	}
	
	@Test(expected = RequestValidationException.class)
	public void ut_a4_testValidate() {
		ImportRequestValidator importRequestValidator = new ImportRequestValidator();
		
		MultipartFile multipartFile = mock(MultipartFile.class);
		ImportRequest dto = mock(ImportRequest.class);
		when(dto.getKey()).thenReturn("key");
		importRequestValidator.validate(multipartFile, dto);
		
	}
	
	@Test
	public void ut_a5_testValidate() {
		ImportRequestValidator importRequestValidator = new ImportRequestValidator();
		
		MultipartFile multipartFile = mock(MultipartFile.class);
		ImportRequest dto = mock(ImportRequest.class);
		when(dto.getKey()).thenReturn("key");
		when(dto.getOnConflict()).thenReturn("skip");
		importRequestValidator.validate(multipartFile, dto);
		
	}
	
	@Test(expected = RequestValidationException.class)
	public void ut_a6_testValidate() {
		ImportRequestValidator importRequestValidator = new ImportRequestValidator();
		
		MultipartFile multipartFile = mock(MultipartFile.class);
		ImportRequest dto = mock(ImportRequest.class);
		when(dto.getKey()).thenReturn("key");
		when(dto.getOnConflict()).thenReturn("create");
		importRequestValidator.validate(multipartFile, dto);
		
	}
}
