package com.helicalinsight.resourcedb;

import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlainJDBCMetadtaTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	@Autowired
	HIResourceServiceDB serviceDb;
	@Autowired
	HIMetadataResourceServiceDB mdServiceDb;
	
	@Autowired
	private IntegrationTestUtility testUtility;

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
	private EfwServicesController efwServicesController;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String jdbcUrl = "";
	private static String firstJdbcId = "";
	private static String dbName = "";

	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "hi","db","SampleTravelData");
			dbName = String.join("/", "/home", "helical", "Performance", "hi","db","SampleTravelData");
		} else if (os.toLowerCase().contains("windows")) {
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "hi","db","SampleTravelData");
			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi","db","SampleTravelData");
		}
	}

	@Test
	public void md_a1_create_a_folder_to_save_metadata() throws Exception {
		testUtility.clearRecycleBin();
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "MetadataWithPlainJDBC");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}

	@Test
	public void md_a2_test_plain_jdbc_DataSourceConnection() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		String formData = "{\"classifier\":\"efwd\",\"name\":\"PlainJdbcTest\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""
				+ jdbcUrl + "\",\"database\":\"" + dbName
				+ "\",\"directory\":\"MetadataWithPlainJDBC\",\"type\":\"sql.jdbc\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful"));
	}

	@Test
	public void md_a3_create_plain_jdbc_Datasource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		String formData = "{\"classifier\":\"efwd\",\"name\":\"PlainJdbcTest\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""
				+ jdbcUrl + "\",\"database\":\"" + dbName
				+ "\",\"directory\":\"MetadataWithPlainJDBC\",\"type\":\"sql.jdbc\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists())
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());

		JSONObject responseObject = jsonObject.getJSONObject("response");
		firstJdbcId = responseObject.getString("dataSourceId");

	}

	@Test
	public void md_a4_expand_catalog_schema() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData", "{\"id\":\"" + firstJdbcId
				+ "\",\"type\":\"sql.jdbc\",\"dir\":\"MetadataWithPlainJDBC\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_a5_expand_table() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData", "{\"id\":\"" + firstJdbcId
				+ "\",\"type\":\"sql.jdbc\",\"dir\":\"MetadataWithPlainJDBC\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_a6_createMetadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc\",\"baseType\":\"sql.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"MetadataWithPlainJDBC\",\"connId\":\"yfj7o\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataWithPlainJDBC\",\"metadataReload\":true}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		String message = responseObject.getString("message");
		Assert.assertNotNull(responseObject.getJSONArray("data"));
		Assert.assertEquals(1, status);
		Assert.assertEquals("Successfully saved metadata file", message);
	}
	
	@Test
	public void md_a7_share_Metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		String formData = "{\"share\":{\"user\":[{\"id\":2,\"permission\":2}]},\"type\":\"file\",\"dir\":\"MetadataWithPlainJDBC\",\"file\":\"SaveSelectAll.metadata\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The selected file privileges are updated successfully."));

	}
	@Test
	public void md_a8_fetchData() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "fetchData");
		String formData = "{\"location\":\"MetadataWithPlainJDBC\",\"metadataFileName\":\"SaveSelectAll.metadata\",\"columns\":[{\"column\":\"HIUSER.dimdate.created_date\",\"alias\":\"created_date\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.dimdate.created_time\",\"alias\":\"created_time\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"created_date\",\"custom\":true},{\"column\":\"created_time\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder)
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
		
	}
	@Test
	public void md_a9_createReport() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "saveReport");
		String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"dimdate.created_date\",\"label\":\"created_date\",\"id\":\"398fbe1b-47df-418f-8a7b-c36e30109701\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"created_date\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"created_date\"},{\"column\":\"dimdate.created_time\",\"label\":\"created_time\",\"id\":\"ed27677f-f3dc-4c41-86d8-13dbfd672369\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"created_time\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"created_time\"}],\"state\":{\"fields\":[{\"column\":\"dimdate.created_date\",\"label\":\"created_date\",\"id\":\"398fbe1b-47df-418f-8a7b-c36e30109701\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"created_date\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"created_date\"},{\"column\":\"dimdate.created_time\",\"label\":\"created_time\",\"id\":\"ed27677f-f3dc-4c41-86d8-13dbfd672369\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"created_time\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"created_time\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"191867ff-c6c5-4cec-ae69-52b5964cfdb3\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"191867ff-c6c5-4cec-ae69-52b5964cfdb3\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fecth\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"field\":\"\",\"apply\":[],\"formatDatatype\":\"\",\"thousandSperator\":false,\"decimalPlace\":2,\"prefix\":\"\",\"suffix\":\"\",\"displayUnits\":\"None\",\"percentage\":false,\"numberCustom\":\"\",\"day\":\"dayNumber\",\"week\":\"none\",\"month\":\"monthNum\",\"quarter\":\"none\",\"year\":\"4digit\",\"dateSeperator\":\"-\",\"dateCustom\":\"\",\"hour\":\"12hr\",\"minute\":\"mintuesNumber\",\"second\":\"secondsNumber\",\"milliSecond\":\"milliSecondsNumber\",\"timeSeperator\":\":\",\"timeCustom\":\"\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"metadata\":{\"location\":\"MetadataWithPlainJDBC\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"samplereport\",\"location\":\"MetadataWithPlainJDBC\"}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder)
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	} 
	
	@Test
	public void md_b1_plain_jdbc_cascade_delete() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"efwd\",\"id\":\""+firstJdbcId+"\",\"type\":\"cascade\",\"directory\":\"MetadataWithPlainJDBC\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The data source has been deleted successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.id").value(firstJdbcId));
		testUtility.clearRecycleBin();
			
	}
	
	@Test
    public void md_b2_verifyDelete() throws Exception {
       HIResource resource =  serviceDb.getResourceByUrl("MetadataWithPlainJDBC/SaveSelectAll.metadata");
       HIResource hresource =  serviceDb.getResourceByUrl("MetadataWithPlainJDBC/samplereport.hr");
       assertNull(resource);
       assertNull(hresource);
    }
	@Test
    public void md_b3_deleteFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[\"MetadataWithPlainJDBC\"]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "delete");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Delete operation is successful"));
        testUtility.clearRecycleBin();
    }
}
