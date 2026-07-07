package com.helicalinsight.adhoc;

/*import java.util.ArrayList;
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

import com.helicalinsight.admin.dao.HIResourceEfwddResourceDao;
import com.helicalinsight.admin.model.HIResourceEfwddResource;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MetadataDynamicViewGenerate8025Test {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	@Autowired
	HIResourceEfwddResourceDao hiResourceEfwddResourceDao;
	
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

	
	static String dbid = null;
	@Test
	public void a1_create_datasource_connection() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		map.put("formData","{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData8025\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\"C:\\\\home\\\\helical\\\\Performance\\\\hi\\\\db\\\\SampleTravelData\",\"jdbcUrl\":\"jdbc:derby:C:\\\\home\\\\helical\\\\Performance\\\\hi\\\\db\\\\SampleTravelData\"}");
		 RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
	        MvcResult result = this.efwMock.perform(builder)
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
	                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
	                        .value("A new Tomcat data source is created successfully."))
	                .andReturn();

	        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
	        JSONObject responseObject = jsonObject.getJSONObject("response");
	        dbid = responseObject.getString("dataSourceId");
	}
	
	
	
	@Test
	public void a2_create_associated_meta_data() throws Exception {
	    MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
	    Map<String, String> map = new HashMap<>();
	    map.put("type", "adhoc");
	    map.put("serviceType", "metadata");
	    map.put("service", "metadataWorkflow");
		map.put("formData", "{\"id\":\"" + dbid + "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
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
	public void a3_create_dynamic_query_view() throws Exception {
	    MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
	    Map<String, String> map = new HashMap<>();
	    map.put("type", "adhoc");
	    map.put("serviceType", "metadata");
	    map.put("service", "saveView");
		map.put("formData", "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"" + dbid + "\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"groovy\",\"query\":\"if (check(\\\"${filter}.label\\\" ,\\\"travel_date\\\")) {\\n  traveldateFilter = findFilterByLabel(\\\"travel_date\\\")\\n  traveldateFilterValue = traveldateFilter.value\\n  traveldateFilter_1 = findFilterByLabel(\\\"travel_date_1\\\")\\n  traveldateFilterValue_1 = traveldateFilter_1.value\\n  return \\\"select * from \\\\\\\"travel_details\\\\\\\" T1 where \\\\\\\"travel_date\\\\\\\" between \\\"+ traveldateFilterValue +\\\" and \\\"+traveldateFilterValue_1;\\n} else {\\n  return \\\"select * from \\\\\\\"travel_details\\\\\\\"\\\"\\n}\\n\",\"viewName\":\"View8025_1\",\"labels\":[{\"name\":\"travel_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"travel_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"travel_type\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_medium\",\"type\":\"text\",\"checked\":true},{\"name\":\"source_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"source\",\"type\":\"text\",\"checked\":true},{\"name\":\"destination_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"destination\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_cost\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"mode_of_payment\",\"type\":\"text\",\"checked\":true},{\"name\":\"booking_platform\",\"type\":\"text\",\"checked\":true},{\"name\":\"travelled_by\",\"type\":\"numeric\",\"checked\":true}],\"hasStoredProcedure\":false,\"validate\":true,\"processedQuery\":\"select * from (select * from \\\"travel_details\\\") foo FETCH FIRST 10 ROWS ONLY\"}");
	    RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
	    this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
	static String location;

	@Test
	public void a4_save_meta_data() throws Exception {
	    MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
	    Map<String, String> map = new HashMap<>();
	    map.put("type", "adhoc");
	    map.put("serviceType", "metadata");
	    map.put("service", "update");
	    map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"id\":\"55a5cfab25247c48f9776bf9bd457a3c_1745830891478\",\"action\":\"noChange\"},{\"id\":\"aa85f3fbafd188679f5b9da8797d9ec9_1745830891492\",\"action\":\"noChange\"},{\"id\":\"1f3619f6ae1549d8a8d89c7b7466af22_1745830891493\",\"action\":\"noChange\"},{\"id\":\"45ee06374e9d68c4a841d57c1be69f22_1745830920998\",\"action\":\"noChange\"},{\"id\":\"c113fa06a79370db6feb443c0023f531_1745830920998\",\"action\":\"noChange\"}],\"dataSource\":{\"id\":\"" + dbid + "\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"zf42k\",\"dbId\":\"zf42k\",\"datasourceName\":\"SampleTravelData8025\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[\"b21d1425-ee4c-477a-b050-f3f6a58342f8\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_8025\",\"location\":\"8025\",\"metadataReload\":true}");
	    RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
	    MvcResult result = this.efwMock.perform(builder)
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andReturn();

	    String responseBody = result.getResponse().getContentAsString();
	    JSONObject jsonObject = JSONObject.fromObject(responseBody);
	    JSONObject formData = jsonObject.getJSONObject("formData");
	    location = formData.getString("location"); // Extract location value
	    System.out.println("Extracted location: " + location);
	}

	@Test
	public void a5_create_report_with_filter() throws Exception {
	    MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
	    Map<String, String> map = new HashMap<>();
	    map.put("type", "adhoc");
	    map.put("serviceType", "report");
	    map.put("service", "fetchData");
	    map.put("formData", "{\"location\":\"" + location + "\",\"metadataFileName\":\"Metadata_8025.metadata\",\"columns\":[{\"column\":{\"name\":\"HIUSER.View8025_1.travel_date\",\"id\":\"40\"},\"alias\":\"travel_date\",\"floatingType\":\"discrete\"},{\"column\":{\"name\":\"HIUSER.View8025_1.booking_platform\",\"id\":\"49\"},\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"travel_date\",\"custom\":true},{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"2015-01-01 16:59:00.0\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.sql.Timestamp\",\"customCondition\":\">=\",\"encloseInQuotes\":true,\"alias\":\"travel_date\",\"label\":\"travel_date\",\"column\":{\"name\":\"HIUSER.View8025_1.travel_date\",\"id\":\"40\"},\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"2015-02-15 10:38:00.0\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.sql.Timestamp\",\"customCondition\":\"<=\",\"encloseInQuotes\":true,\"alias\":\"travel_date_1\",\"label\":\"travel_date_1\",\"column\":{\"name\":\"HIUSER.View8025_1.travel_date\",\"id\":\"40\"},\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
		
	

	
	
	

	

}
*/