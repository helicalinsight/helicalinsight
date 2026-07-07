package com.helicalinsight.adhoc;

import java.io.File;
import java.io.FileInputStream;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.controller.DataSourceController;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataErrorLogs7825Test {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;

    @Autowired
    private DataSourceController dataSourceController;

    @Autowired
    private EfwServicesController efwServicesController;

    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

    @Autowired
    private HIResourceDBDAO serviceDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EFWDConnectionService dsService;

    private MockMvc efwMock;
    private MockMvc mockMvc;
    private MockMvc dsMock;

    private static String TESTURL = "";
    private static String jdbcUrl = "";
    private static String firstDSId = "";

    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) {
            TESTURL = "/home/helical/Performance/HITest";
            jdbcUrl = "jdbc:derby:/home/helical/Performance/HITest/hiee";
        } else if (os.contains("windows")) {
            TESTURL = "C:\\home\\helical\\Performance\\HITest";
            jdbcUrl = "jdbc:derby:/C:/home/helical/Performance/HITest/hiee";
        }
    }

    @Before
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController)
                .addFilters(filterChainProxy).build();
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
        this.dsMock = MockMvcBuilders.standaloneSetup(this.dataSourceController)
                .addFilter(filterChainProxy).build();

        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
    }

    @Test
    public void a1_create_a_folder_to_save_datasource() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "DatasourceTest");
        map.put("sourceArray", new ArrayList<>().toString());

        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(requestBuilder, map);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
                        .value("A new folder is created successfully"));
    }
    
    static String filename;
    @Test
    public void a2_upload_csv() throws Exception {
    	    	File file=new File("/home/helical/Performance/induatry.csv");
		if(!file.exists()){
			file=new File("c:/home/helical/Performance/industry.csv");
		}
    	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/importFile").requestAttr("file", file);

    	Map<String,String> map = new HashMap<>();
    	map.put("type", "flatfile");
    	map.put("destination","");
    	map.put("dataFile", "true");
    	map.put("driverType", "flatfile");
    	try {
    	
    	MockMultipartFile multiPartFile = new MockMultipartFile("file",file.getName(), MediaType.TEXT_PLAIN_VALUE, new FileInputStream(file));
    	
    	 IntegrationTestUtility bean = ApplicationContextAccessor.getBean(IntegrationTestUtility.class);
 		String generateAuthToken = bean.generateAuthToken("hiadmin", "hiadmin", null);
 		String token_1 = "Bearer "+generateAuthToken;
    	
        // Create a RequestBuilder using MockMvcRequestBuilders
        RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/importFile")
                .file(multiPartFile)
                .param("type", "flatfile")
                .param("destination","")
                .param("dataFile", "true")
                .param("driverType", "flatfile").header("Authorization", token_1);
        
       
		
        
    	MvcResult response= this.efwMock.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        		JSONObject jsonObject = JSONObject.fromObject(response.getResponse().getContentAsString());
        		int status = jsonObject.getInt("status");
        		JSONObject responseObject = jsonObject.getJSONObject("response");
        		filename = responseObject.getString("fileName");
        		System.out.println("filename: "+filename);
        		}
    	catch(Exception e) {
			e.printStackTrace();
		}
    }
   

    @Test
    public void a3_createDatasource() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "core");
        map.put("serviceType", "dataSource");
        map.put("service", "write");
     // Define the correct path for Industry.csv
        map.put("formData", "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"com.helical.FlatFileDriver\",\"name\":\"industry_csv\",\"userName\":\"\",\"password\":\"\",\"jdbcUrl\":\"jdbc:flatfile:industry.csv\",\"vendorName\":\"Flatfile csv\","
        		+ "\"fileName\":\""+filename
        		+ "\",\"extraOptions\":{\"config\":{},\"dataFile\":\"industry.csv\"}}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(requestBuilder, map);
        MvcResult result = this.efwMock.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
                        .value("A new Tomcat data source is created successfully."))
                .andReturn();

        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        JSONObject responseObject = jsonObject.getJSONObject("response");
        firstDSId = responseObject.getString("dataSourceId");
    }

    
    
    /*@Test
	public void a4_create_metadata() throws Exception {
    	MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData","{\"id\":\""+firstDSId
				+ "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		
		 MvcResult result = this.efwMock.perform(builder)
		            .andExpect(MockMvcResultMatchers.status().isOk())
		            .andReturn();

		    String responseContent = result.getResponse().getContentAsString();
		    System.out.println("Response content: " + responseContent);
		
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
		}
    @Test
	public void a5_expand_schema() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData","{\"id\":\""+firstDSId
				+ "\",\"type\":\"dynamicDataSource\",\"vendorName\":\"Flatfile csv\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"main\"}],\"catalog\":\"memory\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
    
    @Test
   	public void a6_expand_table() throws Exception {
   		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
   		Map<String, String> map = new HashMap<>();
   		map.put("type", "adhoc");
   		map.put("serviceType", "metadata");
   		map.put("service", "fetchColumns");
   		map.put("formData","{\"dataSource\":{\"id\":\""+firstDSId
   				+ "\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"memory\",\"schema\":\"main\",\"connId\":\"ov2l2\",\"dbId\":\"ov2l2\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"cf78-fd58-89qa-eyeq-vi/lull-riig-c2zp-cquw-w2/fppi-f1u8-zm1z-sh5s-hp/d71r-dv20-cdzr-97go-0u\",\"driverType\":\"Flatfile csv\",\"database\":\"memory.main\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"memory\",\"schema\":\"main\",\"table\":\"industry\"},\"refresh\":true}");
   		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
   		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
   				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
   	}*/
    
    
	}
    
    
    

