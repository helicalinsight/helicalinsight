package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.IScheduleService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteScheduleFromScheduleListTest {

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
	@Autowired
	SchedulesService scheduleService;
	@Test
	public void sch_a1_create_a_folder() throws Exception {
		testutility.createFolder("DeleteScheduleFromScheduleListTest");
	}
	@Test
	public void sch_a2_create_cache() throws Exception {
		String expandSchema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testutility.expand(expandSchema);
		String expandTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testutility.expand(expandTable);

	}


	@Test
	public void sch_a3_create_metadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"bpvl1\",\"dbId\":\"bpvl1\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"DeleteScheduleFromScheduleListTest\",\"metadataReload\":true}";
		String response = testutility.createMetadata(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("metadata").getJSONObject("dataSource");


	}

	@Test
	public void sch_a4_report_fetchData() throws Exception {
		String fetchData = "{\"location\":\"DeleteScheduleFromScheduleListTest\",\"metadataFileName\":\"Metadata_1.metadata\",\"columns\":[{\"column\":{\"name\":\"HIUSER.dimdate.dim_id\",\"id\":\"101\"},\"alias\":\"sum_dim_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"},{\"column\":{\"name\":\"HIUSER.employee_details.employee_name\",\"id\":\"112\"},\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":{\"name\":\"HIUSER.dimdate.dim_id\",\"id\":\"101\"},\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_dim_id\"}],\"groupBy\":[{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0);

		JSONObject obj1 = object.getJSONObject("1");
		String name1 = obj1.getString("name");
		Assert.assertEquals("sum_dim_id", name1);

		JSONObject obj2 = object.getJSONObject("2");
		String name2 = obj2.getString("name");
		Assert.assertEquals("employee_name", name2);

	}

	@Test
	public void sch_a5_report_saveReport() throws Exception {
		String saveReport = "{\"isHrReport\":true,\"columns\":[{\"column\":\"dimdate.dim_id\",\"columnID\":\"101\",\"label\":\"sum_dim_id\",\"id\":\"9b2a847c-8467-4e95-803a-ee74b228ccac\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.employee_name\",\"columnID\":\"112\",\"label\":\"employee_name\",\"id\":\"e9605244-e5f6-4acc-a012-4769ab3ab142\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false}],\"state\":{\"metadataLoading\":false,\"hreportLoading\":false,\"fields\":[{\"column\":\"dimdate.dim_id\",\"columnID\":\"101\",\"label\":\"sum_dim_id\",\"id\":\"9b2a847c-8467-4e95-803a-ee74b228ccac\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.employee_name\",\"columnID\":\"112\",\"label\":\"employee_name\",\"id\":\"e9605244-e5f6-4acc-a012-4769ab3ab142\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"7098fdea-f548-4187-9ec3-27b7f2c5056a\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_dim_id\",\"id\":\"9b2a847c-8467-4e95-803a-ee74b228ccac\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"7098fdea-f548-4187-9ec3-27b7f2c5056a\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"stylesId\":\"hi-report-7810df07\",\"savedStyles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"geoJsonData\":{},\"isAborted\":false,\"referenceLineList\":[{\"display\":\"All\",\"id\":\"9b3d4f7e-445c-4bd3-9ea1-5ee5c81a0451\",\"referenceType\":\"Line\",\"value\":\"\",\"enabled\":false,\"isStatic\":true},{\"display\":\"sum_dim_id\",\"id\":\"9b2a847c-8467-4e95-803a-ee74b228ccac\",\"referenceType\":\"Line\",\"value\":\"\",\"enabled\":false,\"isStatic\":true}],\"customChart\":{\"selected\":false,\"drawer\":false,\"code\":\"\",\"applied\":false},\"tableRecordsPerPage\":10,\"measures\":{\"enable\":false,\"fields\":[{\"column\":\"frontend_custom_measure_name\",\"alias\":\"Measure_Name\",\"genre\":\"custom-formula\",\"custom_frontend_field\":true,\"type\":{\"dataType\":\"text\"}},{\"column\":\"frontend_custom_measure_value\",\"alias\":\"Measure_Value\",\"genre\":\"custom-formula\",\"custom_frontend_field\":true,\"type\":{\"dataType\":\"numeric\"}}]},\"database\":\"HIUSER\",\"appliedDbfs\":{}},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"DeleteScheduleFromScheduleListTest\",\"metadataFileName\":\"Metadata_1.metadata\"},\"reportName\":\"Report_1\",\"location\":\"DeleteScheduleFromScheduleListTest\"}";
		testutility.saveReport(saveReport);
	}
	/*	@Test
        public void sch_a6_schedule_report() throws Exception {
            sch_a4_report_fetchData();

            MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
            Map<String, String> map = new HashMap<>();
            map.put("command", "add");
            map.put("reportDirectory", "DeleteScheduleFromScheduleListTest");
            map.put("reportFile", "Report_1.hr");
            map.put("reportName", "Report_1 2407031641");
            map.put("location", "DeleteScheduleFromScheduleListTest");
            map.put("EmailSettings", "{\"Formats\":[\"csv\"],\"Recipients\":[\"helical@gmail.com\"],\"Zip\":false,\"Subject\":\"scheduleReport\",\"Body\":\"<p>scheduling report</p>\"}");
            map.put("isActive", "true");
            map.put("reportParameters", "{\"mode\":\"dashboard\",\"navigatorUserAgent\":\"print\"}");
            map.put("adhocFormData","{\"location\":\"DeleteScheduleFromScheduleListTest\",\"metadataFileName\":\"Metadata_1.metadata\",\"columns\":[{\"column\":{\"name\":\"HIUSER.dimdate.dim_id\",\"id\":\"101\"},\"alias\":\"sum_dim_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"},{\"column\":{\"name\":\"HIUSER.employee_details.employee_name\",\"id\":\"112\"},\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":{\"name\":\"HIUSER.dimdate.dim_id\",\"id\":\"101\"},\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_dim_id\"}],\"groupBy\":[{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"18fed006-2630-4968-a0b2-c732ec1d4e44\",\"isAdhoc\":true,\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
            map.put("ScheduleOptions", "{\"DaysofWeek\":[\"Thursday\"],\"Frequency\":\"Weekly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":1,\"StartDate\":\"2050-07-03\",\"EndDate\":\"2050-07-03\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"16:57:35\",\"ScheduledEndTime\":\"16:58:35\"}");
            RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

            MvcResult result =  efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                    .jsonPath("$.response.message").value("Successfully scheduled the report")).andReturn();
            JSONObject responseObj = (JSONObject) JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response");



        }

        static String jobId = "";
        @Test
        public void sch_a7_schedule_list() throws Exception {
            MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
            Map<String, String> map = new HashMap<>();
            map.put("type","monitor");
            map.put("serviceType","scheduling");
            map.put("service","schedule");
            map.put("formData","{\"action\":\"list\"}");
            RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

            MvcResult result =  efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
            JSONObject responseObj = (JSONObject) JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response");
            jobId = responseObj.getJSONArray("scheduledList").getJSONObject(0).getString("jobId");

        }

        @Test
        public void sch_a8_schedule_expire() throws Exception {
            MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
            Map<String, String> map = new HashMap<>();
            map.put("type","monitor");
            map.put("serviceType","scheduling");
            map.put("service","schedule");
            map.put("formData","{\"action\":\"delete\",\"jobId\":\""+jobId+"\"}");
            RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

            MvcResult result =  efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
            JSONObject responseObj = (JSONObject) JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response");
            assertEquals("The job deleted from memory successfully",responseObj.getString("message"));
            Schedules schedule = scheduleService.getSchedule(Long.valueOf(jobId));
            assertEquals(false,schedule.getIsActive());
        }

        @Test
        public void sch_a9_schedule_delete() throws Exception {
            MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
            Map<String, String> map = new HashMap<>();
            map.put("type","monitor");
            map.put("serviceType","scheduling");
            map.put("service","schedule");
            map.put("formData","{\"action\":\"delete\",\"jobId\":\""+jobId+"\"}");
            RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

            MvcResult result =  efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
            JSONObject responseObj = (JSONObject) JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response");
            assertEquals("The schedule is deleted successfully",responseObj.getString("message"));
            Schedules schedule = scheduleService.getSchedule(Long.valueOf(jobId));
            assertEquals(null,schedule);
        }*/
	@Test
	public void sch_b1_schedule_delete() throws Exception {
		testutility.deleteResource("DeleteScheduleFromScheduleListTest");
	}
}
