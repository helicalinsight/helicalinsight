package com.helicalinsight.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CheckExcelCellFormatTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;
	
	@Autowired
	IntegrationTestUtility testutility;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	
	
	//bugId= 7292
	@Test
	public void exp_a1_create_a_folder() throws Exception {
		testutility.createFolder("CheckExcelFormat");
	}
	@Test
	public void exp_a2_create_cache() throws Exception {
		String expandSchema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testutility.expand(expandSchema);
		String expandTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testutility.expand(expandTable);
		
	}
	
	static String columnId="";
	@Test
	public void exp_a3_create_metadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"8f6zv\",\"dbId\":\"8f6zv\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"MetadataSave\",\"location\":\"CheckExcelFormat\",\"metadataReload\":true}";
		String response = testutility.createMetadata(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		columnId = jsonObject.getJSONObject("response").getJSONObject("metadata").getJSONObject("tables")
				.getJSONObject("dimdate")
				.getJSONObject("columns")
				.getJSONObject("fiscal_year")
				.getString("id");
		
	}
	
	@Test
	public void exp_a4_report_fetchData() throws Exception {
		String fetchData = "{\"location\":\"CheckExcelFormat\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":{\"name\":\"HIUSER.dimdate.fiscal_year\",\"id\":\""+columnId+"\"},\"alias\":\"fiscal_year\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"fiscal_year\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0);

		JSONObject obj1 = object.getJSONObject("1");
		String name1 = obj1.getString("name");
		String type = obj1.getString("type");
		Assert.assertEquals("fiscal_year", name1);
		Assert.assertEquals("date", type);
		
	}

	@Test
	public void exp_a5_export_excel() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/downloadReport.html");
		Map<String, String> map = new HashMap<>();
		map.put("data", "{\"location\":\"CheckExcelFormat\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":{\"name\":\"HIUSER.dimdate.fiscal_year\",\"id\":\""+columnId+"\"},\"alias\":\"fiscal_year\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"fiscal_year\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"7062670a-0c66-41c3-9c8f-b884d59ac321\",\"isAdhoc\":true,\"type\":\"xls\",\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());

	}
	@Test
	public void exp_a5a_export_excelx() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/downloadReport.html");
		Map<String, String> map = new HashMap<>();
		map.put("data", "{\"location\":\"CheckExcelFormat\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":{\"name\":\"HIUSER.dimdate.fiscal_year\",\"id\":\""+columnId+"\"},\"alias\":\"fiscal_year\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"fiscal_year\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"7062670a-0c66-41c3-9c8f-b884d59ac321\",\"isAdhoc\":true,\"type\":\"xlsx\",\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());

	}
	//it will not show exact date that we are getting in excel
	//date it shows in the format of java.util.Date : "Tue Jan 29 00:00:00 IST 2013"
//	@Test
	public void exp_a6_check_excel_cell_format() throws Exception {
		File tempDirectoryPath = TempDirectoryCleaner.getTempDirectory();
		try(FileOutputStream fos = new FileOutputStream(tempDirectoryPath+File.separator+"temp.xlsx")) {
			Files.copy(Path.of("src","test","resources","test","sample_travel_data.xlsx"), fos);
		}
		File excelFile = getLatestCreatedFileFromDir(tempDirectoryPath);
		FileInputStream fis = new FileInputStream(excelFile);
	    Workbook workbook = new XSSFWorkbook(fis);
	    Sheet sheet = workbook.getSheetAt(1);
	    Row row1 = sheet.getRow(0);
        Cell cell1 = row1.getCell(1);
        String columnName = cell1.getStringCellValue();
        assertEquals("fiscal_year", columnName);
        
        Row row2 = sheet.getRow(1);
        Cell cell2 = row2.getCell(1);
        assertTrue(DateUtil.isCellDateFormatted(cell2));
      
        if (DateUtil.isCellDateFormatted(cell2)) {
            Date actualDate = cell2.getDateCellValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String actualDateStr = sdf.format(actualDate);
            String expectedDateStr = "2013-01-01";

            // Verify the date value
            assertEquals(expectedDateStr, actualDateStr);
        } else {
            throw new Exception("cell is not in date format");
        }
        workbook.close();
        fis.close();
	}
	

	private File getLatestCreatedFileFromDir(File dir) {
		
        File[] files = dir.listFiles((d, name) -> name.endsWith(".xlsx"));
        if (files == null || files.length == 0) {
            throw new IllegalStateException("No Excel files found in the directory");
        }

        // Sort files by creation time, descending
        Arrays.sort(files, Comparator.comparingLong(this::getFileCreationTime).reversed());

        // Return the most recently created file
        return files[0];
	}
	
	private long getFileCreationTime(File file) {
        try {
            Path filePath = file.toPath();
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            return attrs.creationTime().toMillis();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file creation time for " + file.getName(), e);
        }
    }
}
