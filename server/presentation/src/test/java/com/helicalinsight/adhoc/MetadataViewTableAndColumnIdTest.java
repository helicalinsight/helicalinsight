package com.helicalinsight.adhoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.efw.utility.SplitterUtils;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataViewTableAndColumnIdTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

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

	@Autowired
	private IntegrationTestUtility testUtility;

	@Test
	public void md_a1_init() throws Exception {
		testUtility.createFolder("MetadataViewTableColumnTest");
		testUtility.expand(
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		testUtility.expand(
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
	}

	static String viewId1;
	static String viewId2;

	static JSONObject response1 = null;
	static JSONObject response2 = null;
	static JSONObject response3 = null;

	@Test
	public void md_a2_create_view() throws Exception {
		String view = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from  \\\"HIUSER\\\".\\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"fiscal_year\",\"type\":\"date\",\"checked\":true},{\"name\":\"modified_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"date_key\",\"type\":\"text\",\"checked\":true},{\"name\":\"day_number\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_name\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_label\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_date\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_time\",\"type\":\"text\",\"checked\":true},{\"name\":\"rating\",\"type\":\"text\",\"checked\":true}]}";
		response1 = JSONObject.fromObject(testUtility.saveView(view)).getJSONObject("response");
		viewId1 = response1.getString("viewId");
	}

	@Test
	public void md_a3_save_second_time_view() throws Exception {
		String view = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from  \\\"HIUSER\\\".\\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"fiscal_year\",\"type\":\"date\",\"checked\":true},{\"name\":\"modified_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"date_key\",\"type\":\"text\",\"checked\":true},{\"name\":\"day_number\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_name\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_label\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_date\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_time\",\"type\":\"text\",\"checked\":true},{\"name\":\"rating\",\"type\":\"text\",\"checked\":true}],\"viewId\":\""
				+ viewId1 + "\"}";
		response2 = JSONObject.fromObject(testUtility.saveView(view)).getJSONObject("response");
		viewId2 = response2.getString("viewId");
	}

	@Test
	public void md_a4_compare_responses() throws Exception {

		String view1Columns = response1.getJSONObject("tables").getJSONObject("View 1").getJSONObject("columns")
				.toString();

		String view2Columns = response2.getJSONObject("tables").getJSONObject("View 1").getJSONObject("columns")
				.toString();
		assertEquals(SplitterUtils.prepareServiceId(view1Columns), SplitterUtils.prepareServiceId(view2Columns));
	}
	
	
	@Test
	public void md_a5_save_same_view_different_query() throws Exception {
		String view = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select dim_id from  \\\"HIUSER\\\".\\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"fiscal_year\",\"type\":\"date\",\"checked\":true},{\"name\":\"modified_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"date_key\",\"type\":\"text\",\"checked\":true},{\"name\":\"day_number\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_name\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_label\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_date\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_time\",\"type\":\"text\",\"checked\":true},{\"name\":\"rating\",\"type\":\"text\",\"checked\":true}],\"viewId\":\""
				+ viewId1 + "\"}";
		response3 = JSONObject.fromObject(testUtility.saveView(view)).getJSONObject("response");
		
		String view2Columns = response2.getJSONObject("tables").getJSONObject("View 1").getJSONObject("columns")
				.toString();
		String view3Columns = response3.getJSONObject("tables").getJSONObject("View 1").getJSONObject("columns")
				.toString();
		assertNotEquals(SplitterUtils.prepareServiceId(view3Columns), SplitterUtils.prepareServiceId(view2Columns));
	}
	
	

}
