// TODO: Configuration issue
//package com.helicalinsight.export;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.mockito.InjectMocks;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//
//import com.helicalinsight.export.dto.ImportRequest;
//import com.helicalinsight.export.dto.ResourceExportRequest;
//import com.helicalinsight.export.dto.ResourceOptions;
//import com.helicalinsight.export.dto.validation.ExportRequestValidator;
//import com.helicalinsight.export.dto.validation.ImportRequestValidator;
//import com.helicalinsight.export.exception.RequestValidationException;
//import com.helicalinsight.export.exception.ResourceExportException;
//import com.helicalinsight.export.utils.ResourceFileUtils;
//import com.helicalinsight.resourcedb.HIResourceDTO;
//
//import jakarta.servlet.http.HttpServletResponse;
//
//@RunWith(MockitoJUnitRunner.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class ExportResourceUnitTest {
//
//    @InjectMocks
//    private ExportResourceManager exportResourceManager;
//    
//    @InjectMocks
//    private ImportRequestValidator importRequestValidator;
//    
//    @InjectMocks
//    private ExportRequestValidator exportRequestValidator;
//
//    @Mock
//    private ResourceFileUtils fileUtils;
//
//    @Test(expected = ResourceExportException.class)
//    public void a1_testWriteWithFailedSchemaCreation() throws Exception {
//        List<HIResourceDTO> resources = new ArrayList<>();
//        HIResourceDTO parent = new HIResourceDTO();
//        parent.setName("MockResource");
//        resources.add(parent);
//        String dir = "your/mock/dir";
//        ResourceOptions options = new ResourceOptions();
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        when(fileUtils.createSchema(dir)).thenReturn(false);
//        exportResourceManager.write(resources, dir, options, response);
//        verify(fileUtils).createSchema(dir);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void a2_nullDtoShouldThrowValidationException() {
//    	exportRequestValidator.validate(null);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void a3_nullDtoDirShouldThrowValidationException() {
//    	ResourceExportRequest request = new ResourceExportRequest();
//    	request.setDir(null);
//    	request.setFile("");
//    	request.setOptions(new ResourceOptions());
//    	exportRequestValidator.validate(request);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void a4_emptyDtoDirShouldThrowValidationException() {
//    	ResourceExportRequest request = new ResourceExportRequest();
//    	request.setDir("");
//    	request.setFile("");
//    	request.setOptions(new ResourceOptions());
//    	exportRequestValidator.validate(request);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void a5_dtoNotNullDirLengthLessThan3CharsShouldThrowValidationException() {
//    	ResourceExportRequest request = new ResourceExportRequest();
//    	request.setDir("hi");
//    	request.setFile("in");
//    	request.setOptions(new ResourceOptions());
//    	exportRequestValidator.validate(request);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void a6_dtoNotNullDirLengthMoreThan60CharsShouldThrowValidationException() {
//    	ResourceExportRequest request = new ResourceExportRequest();
//    	String name = "hi".repeat(61);
//    	request.setDir(name);
//    	request.setFile(name);
//    	request.setOptions(new ResourceOptions());
//    	exportRequestValidator.validate(request);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void a7_dtoDirWithExtensionShouldThrowValidationException() {
//    	ResourceExportRequest request = new ResourceExportRequest();
//    	String name = "hi.metadata";
//    	request.setDir(name);
//    	request.setFile(name);
//    	request.setOptions(new ResourceOptions());
//    	exportRequestValidator.validate(request);
//    }
//    @Test
//    public void a8_dtoDirWithValidLenfth() {
//    	ResourceExportRequest request = new ResourceExportRequest();
//    	String name = "test";
//    	request.setDir(name);
//    	request.setFile(name);
//    	request.setOptions(new ResourceOptions());
//    	exportRequestValidator.validate(request);
//    }
//    
//    // import Request Validation test
//    @Test(expected = RequestValidationException.class)
//    public void  a9_multiPartNullShouldThrowValidationException() {
//    	importRequestValidator.validateMultipartFile(null,"");
//    }
//    @Test(expected = RequestValidationException.class)
//    public void  b1_multiPartEmptyShouldThrowValidationException() {
//   	 MockMultipartFile file = new MockMultipartFile("file", "hello.zip", MediaType.TEXT_PLAIN_VALUE, "".getBytes());
//    	importRequestValidator.validateMultipartFile(file,"");
//    }
//    
//    @Test
//    public void  b2_validMultiPart() {
//   	 MockMultipartFile file = new MockMultipartFile("file", "hello.zip", MediaType.TEXT_PLAIN_VALUE, "Hello Java".getBytes());
//    	importRequestValidator.validateMultipartFile(file,"");
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void  b3_inValidMultiPartExtension() {
//   	 MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello Java".getBytes());
//    	importRequestValidator.validateMultipartFile(file,"");
//    }
//    
//    
//    
//    
//    @Test(expected = RequestValidationException.class)
//    public void  b4_dtoNullShouldThrowValidationException() {
//    	 MockMultipartFile file = new MockMultipartFile("file", "hello.zip", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
//    	 importRequestValidator.validate(file, null);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void  b5_nullConflictShouldThrowValidationException() {
//    	 MockMultipartFile file = new MockMultipartFile("file", "hello.zip", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
//    	 ImportRequest request = new ImportRequest();
//    	 request.setOnConflict(null);
//    	 request.setOptions(new ResourceOptions());
//    	 request.setUpload(true);
//    	 importRequestValidator.validate(file, request);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void  b6_emptyConflictShouldThrowValidationException() {
//    	 MockMultipartFile file = new MockMultipartFile("file", "hello.zip", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
//    	 ImportRequest request = new ImportRequest();
//    	 request.setOnConflict("");
//    	 request.setOptions(new ResourceOptions());
//    	 request.setUpload(true);
//    	 importRequestValidator.validate(file, request);
//    }
//    
//    @Test
//    public void  b7_validConflict() {
//    	 MockMultipartFile file = new MockMultipartFile("file", "hello.zip", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
//    	 ImportRequest request = new ImportRequest();
//    	 request.setOnConflict("update");
//    	 request.setOptions(new ResourceOptions());
//    	 request.setUpload(true);
//    	 importRequestValidator.validate(file, request);
//    }
//    
//    @Test(expected = RequestValidationException.class)
//    public void  b8_validConflictShouldThrowValidationException() {
//    	 MockMultipartFile file = new MockMultipartFile("file", "hello.zip", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
//    	 ImportRequest request = new ImportRequest();
//    	 request.setOnConflict("insert");
//    	 request.setOptions(new ResourceOptions());
//    	 request.setUpload(true);
//    	 importRequestValidator.validate(file, request);
//    }
//}
