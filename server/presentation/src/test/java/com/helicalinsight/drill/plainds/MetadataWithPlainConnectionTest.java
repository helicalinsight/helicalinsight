// package com.helicalinsight.drill.plainds;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.InputStream;
// import java.io.OutputStream;
// import java.lang.reflect.Constructor;
// import java.lang.reflect.Method;
// import java.util.HashMap;
// import java.util.Map;

// import jakarta.servlet.ServletContext;
// import javax.transaction.Transactional;

// import org.apache.commons.fileupload.FileItem;
// import org.apache.commons.fileupload.disk.DiskFileItem;
// import org.apache.commons.io.IOUtils;
// import org.junit.Before;
// import org.junit.FixMethodOrder;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.MethodSorters;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.web.FilterChainProxy;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
// import org.springframework.test.context.web.WebAppConfiguration;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.RequestBuilder;
// import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.web.context.WebApplicationContext;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.ObjectWriter;
// import com.fasterxml.jackson.databind.node.ObjectNode;
// import com.helicalinsight.adhoc.FileSystemOperationsController;
// import com.helicalinsight.efw.controller.EfwServicesController;
// import com.helicalinsight.efw.utility.TempDirectoryCleaner;
// import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
// import com.helicalinsight.test.utility.IntegrationTestUtility;
// import com.helicalinsight.test.utility.TestUtility;

// import net.sf.json.JSONArray;
// import net.sf.json.JSONObject;

// @WebAppConfiguration
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
// 		"classpath:spring-security.xml" })
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
// public class MetadataWithPlainConnectionTest {

// 	MockMvc efwMock;
// 	MockMvc mockMvc;

// 	@Autowired
// 	private FilterChainProxy filterChainProxy;

// 	@Autowired
// 	private WebApplicationContext context;

// 	@Autowired
// 	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

// 	@Autowired
// 	IntegrationTestUtility testUtility;

// 	@Autowired
// 	private EfwServicesController efwServicesController;

// 	@Autowired
// 	private FileSystemOperationsController fileSystemOperationsController;

// 	@Bean
// 	public FileSystemOperationsController fileSystemOperationsController() {
// 		return new FileSystemOperationsController();
// 	}

// 	@Before
// 	@Transactional
// 	public void setup() {
// 		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
// 				.build();
// 		ServletContext servletContext = context.getServletContext();
// 		servletContext.setAttribute("filterStatus", "ok");
// 		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
// 				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
// 		loadRepository();
// 	}
	
	
// 	public void loadRepository() {

// 		try {
// 			Constructor<?> constructor = Class.forName("com.helicalinsight.efw.framework.RepositoryLoader")
// 					.getDeclaredConstructor();
// 			constructor.setAccessible(true);
// 			Object b = constructor.newInstance();
// 			Method loadMethod = b.getClass().getMethod("load");
// 			loadMethod.setAccessible(true);
// 			loadMethod.invoke(b);

// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 	}


	
// 	static String fileName = "dataset.json";
// 	@Test
// 	public void drill_a1_upload_file() throws Exception{
// 		// download dataset  to temp folder
// 		String tempPath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
// 		String filePath = tempPath+"/"+fileName;
// //		URL website = new URL("https://api.exchangerate-api.com/v4/latest/USD");
// //		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
// //		FileOutputStream fos = new FileOutputStream(filePath);
// //		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
// //		fos.close();
// 		// upload file
		
// 		String dataSet = "{\"provider\":\"https://www.exchangerate-api.com\",\"WARNING_UPGRADE_TO_V6\":\"https://www.exchangerate-api.com/docs/free\",\"terms\":\"https://www.exchangerate-api.com/terms\",\"base\":\"USD\",\"date\":\"2023-03-20\",\"time_last_updated\":1679270402,\"rates\":{\"USD\":1,\"AED\":3.67,\"AFN\":87.71,\"ALL\":107.67,\"AMD\":388.46,\"ANG\":1.79,\"AOA\":511.7,\"ARS\":203.4,\"AUD\":1.49,\"AWG\":1.79,\"AZN\":1.71,\"BAM\":1.83,\"BBD\":2,\"BDT\":107.03,\"BGN\":1.83,\"BHD\":0.376,\"BIF\":2070.6,\"BMD\":1,\"BND\":1.34,\"BOB\":6.92,\"BRL\":5.27,\"BSD\":1,\"BTN\":82.71,\"BWP\":13.3,\"BYN\":2.56,\"BZD\":2,\"CAD\":1.37,\"CDF\":2043.83,\"CHF\":0.927,\"CLP\":826.16,\"CNY\":6.89,\"COP\":4852.57,\"CRC\":546.59,\"CUP\":24,\"CVE\":103.36,\"CZK\":22.48,\"DJF\":177.72,\"DKK\":6.99,\"DOP\":54.88,\"DZD\":136.34,\"EGP\":30.83,\"ERN\":15,\"ETB\":53.84,\"EUR\":0.937,\"FJD\":2.21,\"FKP\":0.822,\"FOK\":6.99,\"GBP\":0.822,\"GEL\":2.58,\"GGP\":0.822,\"GHS\":12.45,\"GIP\":0.822,\"GMD\":62.97,\"GNF\":8524.08,\"GTQ\":7.81,\"GYD\":211.02,\"HKD\":7.85,\"HNL\":24.65,\"HRK\":7.06,\"HTG\":154.29,\"HUF\":372.52,\"IDR\":15380.11,\"ILS\":3.67,\"IMP\":0.822,\"INR\":82.71,\"IQD\":1460.03,\"IRR\":42142.71,\"ISK\":140.48,\"JEP\":0.822,\"JMD\":152.26,\"JOD\":0.709,\"JPY\":132.32,\"KES\":130.14,\"KGS\":87.49,\"KHR\":4035.86,\"KID\":1.49,\"KMF\":461.14,\"KRW\":1306.59,\"KWD\":0.307,\"KYD\":0.833,\"KZT\":459.1,\"LAK\":17001,\"LBP\":15000,\"LKR\":337,\"LRD\":161.79,\"LSL\":18.34,\"LYD\":4.82,\"MAD\":10.38,\"MDL\":18.66,\"MGA\":4315.56,\"MKD\":58.01,\"MMK\":2101.06,\"MNT\":3525.46,\"MOP\":8.09,\"MRU\":34.45,\"MUR\":46.29,\"MVR\":15.42,\"MWK\":1045.1,\"MXN\":18.87,\"MYR\":4.49,\"MZN\":64.16,\"NAD\":18.34,\"NGN\":460.7,\"NIO\":36.68,\"NOK\":10.69,\"NPR\":132.34,\"NZD\":1.59,\"OMR\":0.384,\"PAB\":1,\"PEN\":3.8,\"PGK\":3.53,\"PHP\":54.64,\"PKR\":282.18,\"PLN\":4.4,\"PYG\":7159.88,\"QAR\":3.64,\"RON\":4.62,\"RSD\":110,\"RUB\":76.86,\"RWF\":1104.48,\"SAR\":3.75,\"SBD\":8.49,\"SCR\":13.57,\"SDG\":506.2,\"SEK\":10.49,\"SGD\":1.34,\"SHP\":0.822,\"SLE\":21.4,\"SLL\":21398.23,\"SOS\":568.84,\"SRD\":35.02,\"SSP\":797.21,\"STN\":22.96,\"SYP\":2518.49,\"SZL\":18.34,\"THB\":34.14,\"TJS\":10.92,\"TMT\":3.5,\"TND\":3.11,\"TOP\":2.36,\"TRY\":19.02,\"TTD\":6.79,\"TVD\":1.49,\"TWD\":30.53,\"TZS\":2342.33,\"UAH\":36.89,\"UGX\":3740.93,\"UYU\":39.51,\"UZS\":11367.45,\"VES\":24.2,\"VND\":23577.97,\"VUV\":119.68,\"WST\":2.73,\"XAF\":614.86,\"XCD\":2.7,\"XDR\":0.748,\"XOF\":614.86,\"XPF\":111.85,\"YER\":250.59,\"ZAR\":18.34,\"ZMW\":20.57,\"ZWL\":915.07}}";
// 		ObjectMapper mapper = new ObjectMapper();
// 		ObjectNode node = mapper.readValue(dataSet, ObjectNode.class);
// 		ObjectWriter writer =  mapper.writer();
// 		writer.writeValue(new File(filePath), node);
		
// 		File file=new File(filePath);
//     	FileItem fileResource = new DiskFileItem("file", "multipart/form-data", true, "dataset.json",Integer.MAX_VALUE,  file);
    	
//     	InputStream input =  new FileInputStream(file);
//     	OutputStream os = fileResource.getOutputStream();
//     	IOUtils.copy(input, os);
    	
//     	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//                 .post("/importFile").requestAttr("file", fileResource);
//     	Map<String,String> map = new HashMap<>();
//     	map.put("type", "csv");
//     	map.put("destination", "");
//         RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
// 		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
// 				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
// 				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Upload of "+fileName+" is successful"))
// 				.andExpect(MockMvcResultMatchers.jsonPath("$.response.fileName").value(fileName));
		
// 	}
	
// 	@Test
// 	public void drill_a2_enableDrill() throws Exception {

//     	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//     	Map<String,String> map = new HashMap<>();
//     	map.put("type", "core");
//     	map.put("serviceType", "dataSource");
//     	map.put("service", "drillConfig");
//     	map.put("serviceType", "dataSource");
//     	map.put("formData", "{\"enabled\":true}");
//         RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
// 		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
// 	}
	
// 	static String dataSourceId = "";
// 	@Test
// 	public void drill_a3_create_datasource()  throws Exception{
// 		testUtility.createFolder("DrillPlainDatasource");
// 		String formData = "{\"classifier\":\"efwd\",\"driverName\":\"com.helicalinsight.json\",\"name\":\"JsonDs\",\"userName\":\"\",\"password\":\"\",\"jdbcUrl\":\""+fileName+"\",\"directory\":\"DrillPlainDatasource\",\"type\":\"sql.jdbc\"}";
// 		String response = testUtility.createPlainDatasource(formData);
// 		dataSourceId = JSONObject.fromObject(response).getJSONObject("response").getString("dataSourceId");
// 	}

// 	static String tableId = "";
// 	@Test
// 	public void drill_a4_create_cache()  throws Exception{
// 		testUtility.expand("{\"id\":"+dataSourceId+",\"type\":\"sql.jdbc\",\"dir\":\"DrillPlainDatasource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
// 		String tables = testUtility.expand("{\"dir\":\"DrillPlainDatasource\",\"driverName\":\"com.helicalinsight.json\",\"type\":\"sql.jdbc\",\"id\":"+dataSourceId+",\"userName\":\"\",\"password\":\"\",\"jdbcUrl\":\""+fileName+"\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"dfs.hidw\"}],\"catalog\":\"DRILL\"}]}}");
// 		JSONArray catalogs = JSONObject.fromObject(tables).getJSONObject("response").getJSONObject("metadata").getJSONArray("catalogs");
// 		JSONArray tablesArr = ((JSONObject)(( JSONObject) catalogs.get(0)).getJSONArray("schemas").get(0)).getJSONArray("tables");
// 		tableId = ((JSONObject)tablesArr.get(0)).getString("id");
// 	}
	
	
// 	@Test
// 	public void drill_a5_create_metadata()  throws Exception {
// 		String formData = "{\"database\":\"DRILL.dfs.hidw\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\""+dataSourceId+"\",\"type\":\"sql.jdbc\",\"baseType\":\"sql.jdbc\",\"catalog\":\"DRILL\",\"schema\":\"dfs.hidw\",\"dir\":\"DrillPlainDatasource\",\"connId\":\"jcus8\",\"dbId\":\"jcus8\",\"datasourceName\":\"JsonDs\",\"database\":\"DRILL.dfs.hidw\",\"databaseType\":\"Json\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\""+tableId+"\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"DrillPlainDatasource\",\"metadataReload\":true}";
// 		testUtility.createMetadata(formData);
// 	}
	
// 	@Test
// 	public void drill_z_clean() throws Exception {
// 		testUtility.deleteResource("DrillPlainDatasource");
// 	}

// }
