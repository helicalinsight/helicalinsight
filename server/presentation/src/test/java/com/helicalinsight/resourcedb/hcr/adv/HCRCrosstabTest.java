package com.helicalinsight.resourcedb.hcr.adv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;
import com.lowagie.text.pdf.PdfReader;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.JRCrosstabDataset;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRCrosstabTest {

	private  MockMvc efwMock;

	@Autowired
	private FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility integrationTestUtility;

	@Autowired
	private CacheService cacheService;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Before
	@Transactional
	public void setup() {
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	static String tempEfwdUUID = "";

	@Test
	public void hcr_crosstab_a1_save_datasource_state() throws Exception {
		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"1\",\"type\":\"global.jdbc\",\"connDetails\":{\"globalId\":\"1\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"1\",\"type\":\"sql\",\"query\":\"select * from \\\"HIUSER\\\".\\\"travel_details\\\"\",\"parameters\":[]}}]}}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "saveReportState");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		Assert.assertEquals(200, result.getResponse().getStatus());
		JsonObject jsonObject = GsonUtility.parseString(result.getResponse().getContentAsString(), JsonObject.class);
		tempEfwdUUID = jsonObject.getAsJsonObject("response").get("temp_uuid").getAsString();
		Assert.assertNotEquals(tempEfwdUUID, "");
	}


	@Test
	public void hcr_crosstab_a2_adv_generateCrosstab() throws Exception {
		String formData = "{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":1,\"dataSets\":[{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"dataSetExpression\":\"$P{MAIN_DATA}\",\"fields\":[{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_id\"},{\"clazz\":\"java.sql.Timestamp\",\"name\":\"travel_date\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_type\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_medium\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"source_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"source\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"destination_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"destination\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_cost\"},{\"clazz\":\"java.lang.String\",\"name\":\"mode_of_payment\"},{\"clazz\":\"java.lang.String\",\"name\":\"booking_platform\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travelled_by\"}],\"isMainDataset\":false,\"name\":\"MainDataset\",\"parameters\":[]}],\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":1000,\"pageWidth\":2500,\"parameters\":[{\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"name\":\"MAIN_DATA\"}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":950,\"break\":[],\"crosstab\":[{\"columnGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{mode_of_payment}\"},\"crosstabColumnHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{mode_of_payment}\",\"textHeight\":60,\"textWidth\":200}},\"crosstabTotalColumnHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total mode_of_payment\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}},\"height\":80,\"name\":\"mode_of_payment\",\"totalPosition\":\"END\"}],\"componentElementProperties\":{\"X\":0,\"Y\":0,\"backColor\":\"#ffffff\",\"foreColor\":\"#000000\",\"height\":950,\"mode\":\"Transparent\",\"stretchType\":\"NoStretch\",\"width\":2400,\"runDirection\":\"LTR\",\"repeatColumnHeaders\":false,\"repeatRowHeaders\":false,\"printRepeatedValues\":false,\"columnBreakOffset\":0,\"horizontalPosition\":\"LEFT\",\"ignoreWidth\":false},\"crosstabCells\":[{\"height\":80,\"styleNameReference\":\"TABLE_TD\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textHeight\":60,\"textWidth\":200}],\"width\":250},{\"columnTotalGroup\":\"mode_of_payment\",\"height\":80,\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}],\"width\":250},{\"height\":80,\"rowTotalGroup\":\"travel_medium\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}],\"width\":250}],\"dataSetRun\":{\"dataSetExpression\":\"$P{MAIN_DATA}\",\"dataSetName\":\"MainDataset\"},\"measures\":[{\"calculation\":\"Sum\",\"className\":\"java.lang.Integer\",\"measureExpression\":\"$F{travel_cost}\",\"name\":\"travel_cost_MEASURE\"}],\"rowGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{travel_medium}\"},\"crosstabRowHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{travel_medium}\",\"textHeight\":60,\"textWidth\":200}},\"crosstabTotalRowHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total travel_medium\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}},\"name\":\"travel_medium\",\"totalPosition\":\"END\",\"width\":500}]}],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[]},\"variables\":[]},\"format\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"pdf\"}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp, JsonObject.class);
		int status = GsonUtility.optInt(jsonObject, "status");
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() >= 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());
		// verify jrxml file

		CompletableFuture<Void>  xmlTask = CompletableFuture.runAsync(() -> {
			try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
				assertNotNull("JRXML input stream should not be null",jrxmlStream);
				JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);
				assertNotNull("JasperDesign must be loaded", jasperDesign);
				assertEquals("Untitled 1", jasperDesign.getName());
				assertEquals(2500, jasperDesign.getPageWidth());
				assertEquals(1000, jasperDesign.getPageHeight());

				Map<String,JRDataset> dataSetMap =  jasperDesign.getDatasetMap();
				assertTrue(!dataSetMap.isEmpty());
				assertTrue(dataSetMap.containsKey("MainDataset"));

				JRDataset mainDataSet =  dataSetMap.get("MainDataset");
				assertEquals("select * from \"HIUSER\".\"travel_details\"", mainDataSet.getQuery().getText());

				Set<String> expectedFields =  Set.of("travel_id","travel_date","travel_type","travel_medium","source_id","source","destination_id","destination","travel_cost","mode_of_payment","booking_platform","travelled_by");
				Set<String> actualFields =  Arrays.stream(mainDataSet.getFields())
						.map(it -> it.getName())
						.collect(Collectors.toSet());

				assertTrue(expectedFields.containsAll(actualFields));

				JRBand summaryBand =  jasperDesign.getSummary();;

				assertNotNull("Summary band should not be null",summaryBand);
				assertTrue("Summary height must be more than 500",summaryBand.getHeight() >= 500);

				List<JRChild>  children =  summaryBand.getChildren();
				assertTrue(children.size() == 1);
				JRChild firstChild  =  children.get(0);
				assertNotNull("First child of Summary band should not be null", firstChild);
				JRDesignCrosstab crosstab =  (JRDesignCrosstab) firstChild;
				assertNotNull("crosstab object should not be null", crosstab);
				JRCrosstabDataset dataset =  crosstab.getDataset();
				JRDatasetRun datasetRun =  dataset.getDatasetRun();
				assertEquals("MainDataset", datasetRun.getDatasetName());
				JRExpressionChunk[]  chunks =  datasetRun.getDataSourceExpression().getChunks();
				assertTrue(chunks.length > 0);
				JRExpressionChunk firstChunk = chunks[0];
				StringBuilder dsExpr = new StringBuilder();
				for (JRExpressionChunk chunk : chunks) {
					dsExpr.append(chunk.getText());
				}
				assertTrue(dsExpr.toString().contains("LazySubDatasetDataSourceFactory.resolve"));
				assertTrue(dsExpr.toString().contains("MAIN_DATA"));

			} catch (IOException | JRException e) {
				e.printStackTrace();
			}
		});

		CompletableFuture<Void> pdfTask = CompletableFuture.runAsync(() -> {
			try (InputStream pdfFileStream = Files.newInputStream(Path.of(filePath + ".pdf"));
				 PdfReader pdfReader = new PdfReader(pdfFileStream)) {

				assertNotNull("Pdf file stream should not be null",pdfFileStream);
				assertNotNull("pdfReader should not be null", pdfReader);
				assertTrue(pdfReader.getNumberOfPages() >= 1 );
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		});

		CompletableFuture.allOf(xmlTask, pdfTask).join();

	}


	@Test
	public void hcr_crosstab_a3_adv_saveCrosstab() throws Exception {
		integrationTestUtility.createFolder("HCR_CROSSTAB");
		String formData = "{\"state\":{\"dsPanes\":[{\"key\":\"query\",\"dataSourcePane\":\"Query\",\"menu\":[{\"id\":1,\"name\":\"Query1\",\"config\":\"select  * from \\\"HIUSER\\\".\\\"travel_details\\\"\",\"connectionDetails\":{\"id\":\"1\",\"baseType\":\"global.jdbc\",\"dataSourceType\":\"Managed DataSource\",\"name\":\"SampleTravelDataDerby\",\"type\":\"dynamicDataSource\"},\"executeQueryData\":{\"data\":[{\"travel_id\":1,\"travel_date\":\"2015-09-04 08:22:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":8,\"source\":\"Bangalore\",\"destination_id\":16,\"destination\":\"Chennai\",\"travel_cost\":1350,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":29},{\"travel_id\":2,\"travel_date\":\"2015-09-25 10:22:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":57,\"source\":\"New Delhi\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":1200,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":3,\"travel_date\":\"2015-09-25 14:13:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":57,\"source\":\"New Delhi\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":1200,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":4,\"travel_date\":\"2015-10-20 11:33:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Cab\",\"source_id\":16,\"source\":\"Chennai\",\"destination_id\":19,\"destination\":\"Coimbatore\",\"travel_cost\":800,\"mode_of_payment\":\"Cash\",\"booking_platform\":\"Agent\",\"travelled_by\":11},{\"travel_id\":5,\"travel_date\":\"2015-08-12 13:19:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Misc\",\"source_id\":65,\"source\":\"Pune\",\"destination_id\":29,\"destination\":\"Hyderabad\",\"travel_cost\":1200,\"mode_of_payment\":\"Cash\",\"booking_platform\":\"Agent\",\"travelled_by\":14},{\"travel_id\":6,\"travel_date\":\"2015-10-09 13:18:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Train\",\"source_id\":55,\"source\":\"Nagpur\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":2400,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":23},{\"travel_id\":7,\"travel_date\":\"2015-05-21 10:23:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Flight\",\"source_id\":11,\"source\":\"Bhubaneshwar\",\"destination_id\":33,\"destination\":\"Jammu\",\"travel_cost\":10000,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":8,\"travel_date\":\"2015-07-08 13:29:00.0\",\"travel_type\":\"International\",\"travel_medium\":\"Flight\",\"source_id\":16,\"source\":\"Chennai\",\"destination_id\":63,\"destination\":\"Philippines\",\"travel_cost\":42000,\"mode_of_payment\":\"Net Banking\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":45},{\"travel_id\":9,\"travel_date\":\"2015-11-10 13:29:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Cab\",\"source_id\":8,\"source\":\"Bangalore\",\"destination_id\":54,\"destination\":\"Mysore\",\"travel_cost\":800,\"mode_of_payment\":\"Net Banking\",\"booking_platform\":\"Website\",\"travelled_by\":41},{\"travel_id\":10,\"travel_date\":\"2015-12-24 10:23:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Misc\",\"source_id\":15,\"source\":\"Chandigarh\",\"destination_id\":25,\"destination\":\"Gurgaon\",\"travel_cost\":1200,\"mode_of_payment\":\"Cheque\",\"booking_platform\":\"Agent\",\"travelled_by\":33}],\"field\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"},{\"name\":\"travelled_by\",\"clazz\":\"java.lang.Integer\"}]},\"parameterList\":[],\"temp_uuid\":\""+tempEfwdUUID+"\"}]},{\"key\":\"parameter\",\"dataSourcePane\":\"Parameter\",\"menu\":[]}],\"canvasProperties\":{\"margin\":{},\"layout\":{\"name\":\"A4\",\"orientation\":\"Portrait\",\"size\":{\"width\":595,\"height\":842}},\"pageProperties\":{\"columnCount\":1},\"calculations\":{\"selectCalculation\":\"\",\"options\":[],\"keyValuePairs\":{\"id\":\"39a13b5e-4ca5-45ca-b559-5730ddc3fbef\"}},\"previewParameters\":{\"showParameters\":true},\"groupProperties\":{\"selectGroup\":\"\",\"options\":[]},\"pageStyles\":{\"selectStyles\":\"\",\"options\":[],\"keyValuePairs\":{\"borders\":{},\"padding\":{},\"lineStyles\":{}}}},\"groupCount\":0,\"groupsOrder\":[],\"selectedQueryId\":1},\"diagram\":{\"nodes\":[{\"name\":\"mode_of_payment\",\"width\":100,\"height\":30,\"label\":\"$F{mode_of_payment}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.String\",\"id\":\"node-97bbfcab-aa9f-4be9-b060-315883567f1b\",\"x\":60,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null},{\"name\":\"travel_medium\",\"width\":100,\"height\":30,\"label\":\"$F{travel_medium}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.String\",\"id\":\"node-7c808e29-c783-43b3-9c63-46581f7eddca\",\"x\":201.6864,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null}]},\"name\":\"hcr_crosstab\",\"dir\":\"HCR_CROSSTAB\",\"previewFormData\":{\"format\":\"html\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"designerProperties\":{\"groups\":[],\"fields\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"},{\"name\":\"travelled_by\",\"clazz\":\"java.lang.Integer\"}],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATA\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"designerStyle\":[],\"pageWidth\":2500,\"pageHeight\":950,\"orientation\":\"Portrait\",\"columnCount\":1,\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATA}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}],\"query\":\"select * from \\\"HIUSER\\\".\\\"travel_details\\\"\",\"summary\":{\"bandHeight\":\"950\",\"crosstab\":[{\"columnGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{mode_of_payment}\"},\"crosstabColumnHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{mode_of_payment}\",\"textHeight\":\"60\",\"textWidth\":\"200\"}},\"crosstabTotalColumnHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total mode_of_payment\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}},\"height\":\"60\",\"name\":\"mode_of_payment\",\"totalPosition\":\"END\"}],\"componentElementProperties\":{\"X\":\"0\",\"Y\":\"0\",\"backColor\":\"#ffffff\",\"foreColor\":\"#000000\",\"height\":\"950\",\"mode\":\"Transparent\",\"stretchType\":\"NoStretch\",\"width\":\"2400\"},\"crosstabCells\":[{\"height\":\"60\",\"styleNameReference\":\"TABLE_TD\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textHeight\":\"60\",\"textWidth\":\"200\"}],\"width\":\"150\"},{\"columnTotalGroup\":\"mode_of_payment\",\"height\":\"80\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}],\"width\":\"150\"},{\"height\":\"60\",\"rowTotalGroup\":\"travel_medium\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}],\"width\":\"300\"}],\"dataSetRun\":{\"dataSetExpression\":\"$P{MAIN_DATA}\",\"dataSetName\":\"MainDataset\"},\"measures\":[{\"calculation\":\"Sum\",\"className\":\"java.lang.Integer\",\"measureExpression\":\"$F{travel_cost}\",\"name\":\"travel_cost_MEASURE\"}],\"rowGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{travel_medium}\"},\"crosstabRowHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{travel_medium}\",\"textHeight\":\"60\",\"textWidth\":\"200\"}},\"crosstabTotalRowHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total travel_medium\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}},\"name\":\"travel_medium\",\"totalPosition\":\"END\",\"width\":\"350\"}]}],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[]}},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"generateXML\":false},\"saveUUID\":\"6e4d7e24-8b80-4c29-a89a-4f3a821b177a\"}";
		String response = integrationTestUtility.saveHCRReportState(formData);
		List<Cache> cacheList =  cacheService.findAllReportedCaches(tempEfwdUUID);
		JsonObject rawNode = GsonUtility.parseString(response, JsonObject.class);
		JsonObject responseNode = rawNode.getAsJsonObject("response");
		Assert.assertEquals("hcr_crosstab.hcr", responseNode.get("uuid").getAsString());
		Assert.assertEquals("Report saved successfully", responseNode.get("message").getAsString());
		if( CacheUtils.isCacheEnabled()) {
			Assert.assertTrue(cacheList.size() >= 1);
		}
	}

	//@Test
	public void hcr_crosstab_a4_adv_generateCrosstabWithDefaults() throws Exception {


		String formData = "{\"connectionDetails\":{},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":1,\"dataSets\":[{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"dataSetExpression\":\"$P{MAIN_DATA}\",\"fields\":[{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_id\"},{\"clazz\":\"java.sql.Timestamp\",\"name\":\"travel_date\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_type\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_medium\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"source_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"source\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"destination_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"destination\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_cost\"},{\"clazz\":\"java.lang.String\",\"name\":\"mode_of_payment\"},{\"clazz\":\"java.lang.String\",\"name\":\"booking_platform\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travelled_by\"}],\"isMainDataset\":false,\"name\":\"MainDataset\",\"parameters\":[]}],\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":1000,\"pageWidth\":2500,\"parameters\":[{\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"name\":\"MAIN_DATA\"}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":950,\"break\":[],\"crosstab\":[{\"componentElementProperties\":{},\"columnGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{mode_of_payment}\"},\"crosstabColumnHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{mode_of_payment}\"}},\"crosstabTotalColumnHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total mode_of_payment\\\"\",\"textForecolor\":\"#000000\"}},\"name\":\"mode_of_payment\",\"totalPosition\":\"END\"}],\"crosstabCells\":[{\"styleNameReference\":\"TABLE_TD\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\"}]},{\"columnTotalGroup\":\"mode_of_payment\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\"}]},{\"rowTotalGroup\":\"travel_medium\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\"}]}],\"dataSetRun\":{\"dataSetExpression\":\"$P{MAIN_DATA}\",\"dataSetName\":\"MainDataset\"},\"measures\":[{\"calculation\":\"Sum\",\"className\":\"java.lang.Integer\",\"measureExpression\":\"$F{travel_cost}\",\"name\":\"travel_cost_MEASURE\"}],\"rowGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{travel_medium}\"},\"crosstabRowHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{travel_medium}\"}},\"crosstabTotalRowHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total travel_medium\\\"\",\"textForecolor\":\"#000000\"}},\"name\":\"travel_medium\"}]}],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[]},\"variables\":[]},\"format\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"pdf\"}";

		String resp = integrationTestUtility.generateHCRReport(formData);



		JsonObject jsonObject = GsonUtility.parseString(resp, JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() >= 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());
		// verify jrxml file

		CompletableFuture<Void>  xmlTask = CompletableFuture.runAsync(() -> {
			try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
				assertNotNull("JRXML input stream should not be null",jrxmlStream);
				JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);
				assertNotNull("JasperDesign must be loaded", jasperDesign);
				assertEquals("Untitled 1", jasperDesign.getName());
				assertEquals(2500, jasperDesign.getPageWidth());
				assertEquals(1000, jasperDesign.getPageHeight());

				Map<String,JRDataset> dataSetMap =  jasperDesign.getDatasetMap();
				assertTrue(!dataSetMap.isEmpty());
				assertTrue(dataSetMap.containsKey("MainDataset"));

				JRDataset mainDataSet =  dataSetMap.get("MainDataset");
				assertEquals("select * from \"HIUSER\".\"travel_details\"", mainDataSet.getQuery().getText());

				Set<String> expectedFields =  Set.of("travel_id","travel_date","travel_type","travel_medium","source_id","source","destination_id","destination","travel_cost","mode_of_payment","booking_platform","travelled_by");
				Set<String> actualFields =  Arrays.stream(mainDataSet.getFields())
						.map(it -> it.getName())
						.collect(Collectors.toSet());

				assertTrue(expectedFields.containsAll(actualFields));

				JRBand summaryBand =  jasperDesign.getSummary();;

				assertNotNull("Summary band should not be null",summaryBand);
				assertTrue("Summary height must be more than 500",summaryBand.getHeight() >= 500);

				List<JRChild>  children =  summaryBand.getChildren();
				assertTrue(children.size() == 1);
				JRChild firstChild  =  children.get(0);
				assertNotNull("First child of Summary band should not be null", firstChild);
				JRDesignCrosstab crosstab =  (JRDesignCrosstab) firstChild;
				assertNotNull("crosstab object should not be null", crosstab);
				JRCrosstabDataset dataset =  crosstab.getDataset();
				JRDatasetRun datasetRun =  dataset.getDatasetRun();
				assertEquals("MainDataset", datasetRun.getDatasetName());
				JRExpressionChunk[]  chunks =  datasetRun.getDataSourceExpression().getChunks();
				assertTrue(chunks.length > 0);
				JRExpressionChunk firstChunk = chunks[0];
				StringBuilder dsExpr = new StringBuilder();
				for (JRExpressionChunk chunk : chunks) {
					dsExpr.append(chunk.getText());
				}
				assertTrue(dsExpr.toString().contains("LazySubDatasetDataSourceFactory.resolve"));
				assertTrue(dsExpr.toString().contains("MAIN_DATA"));

			} catch (IOException | JRException e) {
				e.printStackTrace();
			}
		});

		CompletableFuture<Void> pdfTask = CompletableFuture.runAsync(() -> {
			try (InputStream pdfFileStream = Files.newInputStream(Path.of(filePath + ".pdf"));
				 PdfReader pdfReader = new PdfReader(pdfFileStream)) {

				assertNotNull("Pdf file stream should not be null",pdfFileStream);
				assertNotNull("pdfReader should not be null", pdfReader);
				assertTrue(pdfReader.getNumberOfPages() >= 1 );
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		});

		CompletableFuture.allOf(xmlTask, pdfTask).join();
	}

	//@Test
	public void hcr_crosstab_a5_adv_emptyCrosstabTest() throws Exception {
		String formData = "{\"format\":\"html\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"dataSets\":[{\"name\":\"Query1\",\"isMainDataset\":false,\"dataSetExpression\":\"$P{REPORT_CONNECTION}\",\"connectionDetails\":{},\"fields\":[],\"parameters\":[]}],\"summary\":{\"bandHeight\":75,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[],\"crosstab\":[{\"dataSetRun\":{\"dataSetName\":\"Query1\",\"dataSetExpression\":\"$P{REPORT_CONNECTION}\"},\"printRepeatedValues\":true,\"componentElementProperties\":{\"X\":120.18639999999999,\"Y\":0,\"width\":250,\"height\":75}}],\"chart\":[]}},\"type\":\"html\",\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertEquals(1,reportPageInfo.get("totalPageCount").getAsInt());
	}

	@Test
	public void hcr_crosstab_a6_adv_addBorderAndPaddingForCrosstab() throws Exception {
		String formData2= "{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":1,\"dataSets\":[{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"dataSetExpression\":\"$P{MAIN_DATA}\",\"fields\":[{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_id\"},{\"clazz\":\"java.sql.Timestamp\",\"name\":\"travel_date\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_type\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_medium\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"source_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"source\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"destination_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"destination\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_cost\"},{\"clazz\":\"java.lang.String\",\"name\":\"mode_of_payment\"},{\"clazz\":\"java.lang.String\",\"name\":\"booking_platform\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travelled_by\"}],\"isMainDataset\":false,\"name\":\"MainDataset\",\"parameters\":[]}],\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":1000,\"pageWidth\":2500,\"parameters\":[{\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"name\":\"MAIN_DATA\"}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":950,\"break\":[],\"crosstab\":[{\"columnGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{mode_of_payment}\"},\"crosstabColumnHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{mode_of_payment}\",\"textHeight\":60,\"textWidth\":200}},\"crosstabTotalColumnHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total mode_of_payment\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}},\"height\":80,\"name\":\"mode_of_payment\",\"totalPosition\":\"END\"}],\"componentElementProperties\":{\"X\":0,\"Y\":0,\"backColor\":\"#ffffff\",\"foreColor\":\"#000000\",\"height\":950,\"mode\":\"Transparent\",\"stretchType\":\"NoStretch\",\"width\":2400,\"runDirection\":\"LTR\",\"repeatColumnHeaders\":false,\"repeatRowHeaders\":false,\"printRepeatedValues\":false,\"columnBreakOffset\":0,\"horizontalPosition\":\"LEFT\",\"ignoreWidth\":false,\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"}}},\"padding\":{\"bottomPadding\":1,\"topPadding\":1,\"leftPadding\":1,\"rightPadding\":1}},\"crosstabCells\":[{\"height\":80,\"styleNameReference\":\"TABLE_TD\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textHeight\":60,\"textWidth\":200}],\"width\":250},{\"columnTotalGroup\":\"mode_of_payment\",\"height\":80,\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}],\"width\":250},{\"height\":80,\"rowTotalGroup\":\"travel_medium\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}],\"width\":250}],\"dataSetRun\":{\"dataSetExpression\":\"$P{MAIN_DATA}\",\"dataSetName\":\"MainDataset\"},\"measures\":[{\"calculation\":\"Sum\",\"className\":\"java.lang.Integer\",\"measureExpression\":\"$F{travel_cost}\",\"name\":\"travel_cost_MEASURE\"}],\"rowGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{travel_medium}\"},\"crosstabRowHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{travel_medium}\",\"textHeight\":60,\"textWidth\":200}},\"crosstabTotalRowHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total travel_medium\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}},\"name\":\"travel_medium\",\"totalPosition\":\"END\",\"width\":500}]}],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[]},\"variables\":[]},\"format\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"pdf\"}";
		String resp = integrationTestUtility.generateHCRReport(formData2);
		JsonObject jsonObject = GsonUtility.parseString(resp, JsonObject.class);
		int status = GsonUtility.optInt(jsonObject, "status");
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() >= 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());

		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
			assertNotNull("JRXML input stream should not be null",jrxmlStream);
			JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);
			assertNotNull("JasperDesign must be loaded", jasperDesign);
			jasperDesign.getSummary().getChildren().forEach(child -> {
				if ( child instanceof JRCrosstab crosstab) {
					JRLineBox lineBox =  crosstab.getLineBox();
					// border
					assertEquals(1f,lineBox.getTopPen().getLineWidth().floatValue(),0.1);
					assertEquals(1f,lineBox.getBottomPen().getLineWidth().floatValue(),0.1);
					assertEquals(1f,lineBox.getLeftPen().getLineWidth().floatValue(),0.1);
					assertEquals(1f,lineBox.getRightPen().getLineWidth().floatValue(),0.1);

					//padding
					assertEquals(1,lineBox.getTopPadding().intValue());
					assertEquals(1,lineBox.getBottomPadding().intValue());
					assertEquals(1,lineBox.getRightPadding().intValue());
					assertEquals(1,lineBox.getLeftPadding().intValue());

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Test
	public void hcr_crosstab_a7_adv_paddingInsideBorderBackWardCompatibility() throws Exception {
		String formData2= "{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":1,\"dataSets\":[{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"dataSetExpression\":\"$P{MAIN_DATA}\",\"fields\":[{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_id\"},{\"clazz\":\"java.sql.Timestamp\",\"name\":\"travel_date\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_type\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_medium\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"source_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"source\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"destination_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"destination\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_cost\"},{\"clazz\":\"java.lang.String\",\"name\":\"mode_of_payment\"},{\"clazz\":\"java.lang.String\",\"name\":\"booking_platform\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travelled_by\"}],\"isMainDataset\":false,\"name\":\"MainDataset\",\"parameters\":[]}],\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":1000,\"pageWidth\":2500,\"parameters\":[{\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"name\":\"MAIN_DATA\"}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":950,\"break\":[],\"crosstab\":[{\"columnGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{mode_of_payment}\"},\"crosstabColumnHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{mode_of_payment}\",\"textHeight\":60,\"textWidth\":200}},\"crosstabTotalColumnHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total mode_of_payment\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}},\"height\":80,\"name\":\"mode_of_payment\",\"totalPosition\":\"END\"}],\"componentElementProperties\":{\"X\":0,\"Y\":0,\"backColor\":\"#ffffff\",\"foreColor\":\"#000000\",\"height\":950,\"mode\":\"Transparent\",\"stretchType\":\"NoStretch\",\"width\":2400,\"runDirection\":\"LTR\",\"repeatColumnHeaders\":false,\"repeatRowHeaders\":false,\"printRepeatedValues\":false,\"columnBreakOffset\":0,\"horizontalPosition\":\"LEFT\",\"ignoreWidth\":false,\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"}},\"padding\":{\"bottomPadding\":1,\"topPadding\":1,\"leftPadding\":1,\"rightPadding\":1}}},\"crosstabCells\":[{\"height\":80,\"styleNameReference\":\"TABLE_TD\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textHeight\":60,\"textWidth\":200}],\"width\":250},{\"columnTotalGroup\":\"mode_of_payment\",\"height\":80,\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}],\"width\":250},{\"height\":80,\"rowTotalGroup\":\"travel_medium\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}],\"width\":250}],\"dataSetRun\":{\"dataSetExpression\":\"$P{MAIN_DATA}\",\"dataSetName\":\"MainDataset\"},\"measures\":[{\"calculation\":\"Sum\",\"className\":\"java.lang.Integer\",\"measureExpression\":\"$F{travel_cost}\",\"name\":\"travel_cost_MEASURE\"}],\"rowGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{travel_medium}\"},\"crosstabRowHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{travel_medium}\",\"textHeight\":60,\"textWidth\":200}},\"crosstabTotalRowHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total travel_medium\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":60,\"textWidth\":200}},\"name\":\"travel_medium\",\"totalPosition\":\"END\",\"width\":500}]}],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[]},\"variables\":[]},\"format\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"pdf\"}";
		String resp = integrationTestUtility.generateHCRReport(formData2);
		JsonObject jsonObject = GsonUtility.parseString(resp, JsonObject.class);
		int status = GsonUtility.optInt(jsonObject, "status");
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() >= 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());

		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
			assertNotNull("JRXML input stream should not be null",jrxmlStream);
			JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);
			assertNotNull("JasperDesign must be loaded", jasperDesign);
			jasperDesign.getSummary().getChildren().forEach(child -> {
				if ( child instanceof JRCrosstab crosstab) {
					JRLineBox lineBox =  crosstab.getLineBox();
					// border
					assertEquals(1f,lineBox.getTopPen().getLineWidth().floatValue(),0.1);
					assertEquals(1f,lineBox.getBottomPen().getLineWidth().floatValue(),0.1);
					assertEquals(1f,lineBox.getLeftPen().getLineWidth().floatValue(),0.1);
					assertEquals(1f,lineBox.getRightPen().getLineWidth().floatValue(),0.1);

					//padding
					assertEquals(1,lineBox.getTopPadding().intValue());
					assertEquals(1,lineBox.getBottomPadding().intValue());
					assertEquals(1,lineBox.getRightPadding().intValue());
					assertEquals(1,lineBox.getLeftPadding().intValue());

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
