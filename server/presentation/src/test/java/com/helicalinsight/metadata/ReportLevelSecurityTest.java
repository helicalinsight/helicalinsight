package com.helicalinsight.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletContext;
import javax.transaction.Transactional;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReportLevelSecurityTest {


    MockMvc efwMock;
    MockMvc mockMvc;
    
    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
    
    @Autowired
    IntegrationTestUtility testUtility;


    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }
    
    @Before
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
    }

    private static String dbName = "";
	private static String jdbcUrl = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");

			} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join(File.separator, "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
		}
	}
	
    @Autowired
    private EfwServicesController efwServicesController;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;

    @Test
    public void md_a1_create_a_folder_to_save_metadata() throws Exception {
    	testUtility.createFolder("ReportLevelSecurityTest");
    }
    
    @Test
    public void md_a2_create_cache() throws Exception {
    	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(defaultCatalog);
    	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(defaultTable);
    }
    static JSONArray noChangeJoins ;
    static JSONArray tableIds;
    
    
    
 static String firstExpressionId = "";
    
    @Test
    public void md_a3_saveSecurity() throws Exception {
    	
    	String validate = "{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"${user}.name eq 'hiadmin'\"}}";
    	String saveSecurity = "{\"uuid\":true,\"expression\":[{\"expressionName\":\"security_on_dimdate\",\"expressionType\":\"global\",\"accessType\":\"grant\",\"executionType\":\"conditionIf\",\"on\":[\"4ac5d9f68b58bd7c0d179146e46795be_z0fr5\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"filter\":\"\\\"dimdate\\\".\\\"dim_id\\\"=2\",\"action\":\"add\"}]}";
    	testUtility.validateSecurity(validate);
    	String response = testUtility.saveSecurity(saveSecurity);
    	ObjectNode responseObj = JacksonUtility.fromObject(response);
    	firstExpressionId = responseObj.with("response").get("expressionId").asText();
    	
    }
    
    @Test
    public void md_a4_create_metadata() throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+firstExpressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"ReportLevelSecurityTest\",\"fileName\":\"ReportLevelFilter\",\"uniqueId\":true}";
    	testUtility.createMetadata(formData);
    }
    
    @Test
    public void md_a5_fetchData_security_grant_dimdate() throws Exception {
    	String formData = "{\"location\":\"ReportLevelSecurityTest\",\"metadataFileName\":\"ReportLevelFilter.metadata\",\"columns\":[{\"column\":\"HIUSER.dimdate.created_date\",\"alias\":\"created_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"created_date\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
    	String response = testUtility.fetchData(formData);
    	JSONArray arr = JSONObject.fromObject(response).getJSONObject("response").getJSONArray("data");
    	Assert.assertEquals(1, arr.size());
    }
    
    
    @Test
    public void md_a6_create_security() throws Exception {
    	String saveSecurity = "{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_dimdate\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"4ac5d9f68b58bd7c0d179146e46795be_z0fr5\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
    	String response = testUtility.saveSecurity(saveSecurity);
    	ObjectNode responseObj = JacksonUtility.fromObject(response);
    	firstExpressionId = responseObj.with("response").get("expressionId").asText();
    }
    @Test
    public void md_a7_create_metadata() throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+firstExpressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"ReportLevelSecurityTest\",\"fileName\":\"HReportDenyDimdate\",\"uniqueId\":true}";
    	testUtility.createMetadata(formData);
    }
    
    @Test
    public void md_a8_fetchData_security_deny_dimdate() throws Exception {
    	String formData = "{\"location\":\"ReportLevelSecurityTest\",\"metadataFileName\":\"HReportDenyDimdate.metadata\",\"columns\":[{\"column\":\"HIUSER.employee_details.address\",\"alias\":\"address\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.geo_cordinates.latitude\",\"alias\":\"sum_latitude\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.cancellation_reason\",\"alias\":\"cancellation_reason\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.travel_details.destination\",\"alias\":\"destination\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.geo_cordinates.latitude\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_latitude\"}],\"groupBy\":[{\"column\":\"address\",\"custom\":true},{\"column\":\"cancellation_reason\",\"custom\":true},{\"column\":\"destination\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
    	String response = testUtility.fetchData(formData);
    	JSONArray arr = JSONObject.fromObject(response).getJSONObject("response").getJSONArray("data");
    	Assert.assertTrue(arr.size() > 1);
    }
  
}