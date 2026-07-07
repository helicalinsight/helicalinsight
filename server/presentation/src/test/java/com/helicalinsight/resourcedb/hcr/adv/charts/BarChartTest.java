package com.helicalinsight.resourcedb.hcr.adv.charts;

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

import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;
import com.lowagie.text.pdf.PdfReader;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChartDataset;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarChartTest {

	private MockMvc efwMock;

	@Autowired
	private FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility integrationTestUtility;


	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Before
	@Transactional
	public void setup() {
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	static String tempEfwdUUID = ""; 

	@Test
	public void hcr_barchart_a1_save_datasource_state() throws Exception {
		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"1\",\"type\":\"global.jdbc\",\"connDetails\":{\"globalId\":\"1\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"1\",\"type\":\"sql\",\"query\":\"select \\\"destination\\\" , sum(\\\"travel_cost\\\") as \\\"sum_travel_cost\\\" from \\\"HIUSER\\\".\\\"travel_details\\\"\\r\\n group by \\\"destination\\\" order by \\\"sum_travel_cost\\\" desc fetch first 20 rows only \",\"parameters\":[]}}]}}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "saveReportState");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		Assert.assertEquals(200, result.getResponse().getStatus());
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		tempEfwdUUID = jsonObject.getJSONObject("response").getString("temp_uuid");
		Assert.assertNotEquals(tempEfwdUUID, "");
	}

	@Test
		public void hcr_barchart_a2_adv_generateChart() throws Exception {
			String formData = "{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":1,\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":1500,\"pageWidth\":2500,\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":900,\"break\":[],\"crosstab\":[],\"table\":[],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[],\"chart\":[{\"chartType\":\"Bar\",\"styleNameReference\":\"TABLE_CT\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":700,\"width\":1200,\"stretchType\":\"NoStretch\",\"positionType\":\"FixRelativeToTop\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"hyperlink\":{\"anchroNameExpression\":\"\",\"bookmarkLevelExpression\":\"\",\"bookmarkLevel\":0,\"linkTarget\":\"Self\",\"linkType\":\"ReportExecution\",\"hyperlinkReferenceExpression\":\"\",\"hyperlinkAnchorExpression\":\"\",\"hyperlinkPageExpression\":\"\",\"hyperlinkWhenExpression\":\"\",\"hyperlinkTooltipExpression\":\"\",\"parameters\":[]},\"chart\":{\"renderType\":\"image\",\"theme\":\"default\",\"evaluationTime\":\"Report\",\"chartTitle\":{\"expression\":\"\\\"Destination Travel Cost Chart\\\"\",\"color\":\"#000000\",\"font\":{\"size\":16,\"isBold\":true,\"isItalic\":false,\"isUnderline\":true,\"isStrikeThrough\":false},\"position\":\"Top\"},\"chartSubtitle\":{\"expression\":\"\\\"Calculates total cost by destination\\\"\",\"color\":\"#000000\",\"font\":{\"size\":10,\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false}},\"chartLegend\":{\"showLegend\":true,\"position\":\"Bottom\",\"foreColor\":\"#000000\",\"backColor\":\"#FCFCFC\",\"font\":{\"size\":18,\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false}}},\"dataSet\":{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\"},\"categorySeries\":{\"seriesExpression\":\"$F{destination}\",\"categoryExpression\":\"\\\"\\\"\",\"valueExpression\":\"$F{sum_travel_cost}\",\"labelExpression\":\"\"}},\"chartPlot\":{\"plot\":{\"backColor\":\"#FFFFFF\",\"backgroundAlpha\":\"1.0\",\"foregroundAlpha\":\"1.0\",\"labelRotation\":\"90.0\",\"orientation\":\"Vertical\",\"seriesColors\":[{\"seriesOrder\":\"0\",\"color\":\"#1f77b4\"},{\"seriesOrder\":\"1\",\"color\":\"#ff7f0e\"},{\"seriesOrder\":\"2\",\"color\":\"#2ca02c\"},{\"seriesOrder\":\"3\",\"color\":\"#d62728\"},{\"seriesOrder\":\"4\",\"color\":\"#9467bd\"},{\"seriesOrder\":\"5\",\"color\":\"#8c564b\"},{\"seriesOrder\":\"6\",\"color\":\"#e377c2\"},{\"seriesOrder\":\"7\",\"color\":\"#7f7f7f\"},{\"seriesOrder\":\"8\",\"color\":\"#bcbd22\"},{\"seriesOrder\":\"9\",\"color\":\"#17becf\"}]},\"itemLabel\":{\"color\":\"#000000\",\"backgroundColor\":\"#0D0202\",\"font\":{\"fontName\":\"Agency FB\",\"size\":10,\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false}},\"labelRotation\":\"90.0\",\"categoryAxisLabelExpression\":\"\",\"categoryAxisFormat\":{\"axisFormat\":{\"labelColor\":\"#050505\",\"tickLabelColor\":\"#050505\",\"tickLabelMask\":\"\",\"verticalTickLabels\":false,\"axisLineColor\":\"#000000\",\"labelFont\":{\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false},\"tickLabelFont\":{\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false}}},\"valueAxisLabelExpression\":\"\",\"valueAxisFormat\":{\"axisFormat\":{\"labelColor\":\"#050505\",\"tickLabelColor\":\"#050505\",\"tickLabelMask\":\"\",\"verticalTickLabels\":false,\"axisLineColor\":\"#000000\",\"labelFont\":{\"size\":10,\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false},\"tickLabelFont\":{\"size\":10,\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false}}}}}]},\"variables\":[],\"dataSets\":[{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"fields\":[{\"clazz\":\"java.lang.Integer\",\"name\":\"sum_travel_cost\"},{\"clazz\":\"java.lang.String\",\"name\":\"destination\"}],\"parameters\":[],\"variables\":[]}]},\"format\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"pdf\"}";
			String resp = integrationTestUtility.generateHCRReport(formData);
			JSONObject jsonObject = JSONObject.fromObject(resp);
			int status = jsonObject.getInt("status");
			Assert.assertEquals(1, status);
			JSONObject reportPageInfo = jsonObject.getJSONObject("response").getJSONObject("reportPageInfo");
			Assert.assertTrue(reportPageInfo.getInt("totalPageCount") >= 1);
			Assert.assertEquals("File successfully exported", jsonObject.getJSONObject("response").getString("response"));
			String uuid = jsonObject.getJSONObject("response").getJSONObject("jrxmlData").getString("uuid");
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
					assertEquals(1500, jasperDesign.getPageHeight());
					
					Map<String,JRDataset> dataSetMap =  jasperDesign.getDatasetMap();
					assertTrue(!dataSetMap.isEmpty());
					assertTrue(dataSetMap.containsKey("MainDataset"));
					
					JRDataset mainDataSet =  dataSetMap.get("MainDataset");
					assertTrue(!mainDataSet.getQuery().getText().isEmpty());
					
					Set<String> expectedFields =  Set.of("destination","sum_travel_cost");
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
					JRDesignChart chart =  (JRDesignChart) firstChild;
					assertNotNull("barchart object should not be null", chart);
					JRChartDataset dataset =  chart.getDataset();
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
					assertTrue(dsExpr.toString().contains("MAIN_DATASET"));
					
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
	public void hcr_barchart_a3_adv_generateBarChartWithDefaults() throws Exception {
		String formData = "{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":1,\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":1500,\"pageWidth\":2500,\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":900,\"break\":[],\"crosstab\":[],\"table\":[],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[],\"chart\":[{\"chartType\":\"Bar\",\"chart\":{\"chartTitle\":{\"expression\":\"\\\"Destination Travel Cost Chart\\\"\"},\"chartSubtitle\":{\"expression\":\"\\\"Calculates total cost by destination\\\"\"}},\"dataSet\":{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\"},\"categorySeries\":{\"seriesExpression\":\"$F{destination}\",\"categoryExpression\":\"\\\"\\\"\",\"valueExpression\":\"$F{sum_travel_cost}\",\"labelExpression\":\"\"}},\"chartPlot\":{\"categoryAxisLabelExpression\":\"\",\"categoryAxisFormat\":{\"axisFormat\":{\"labelColor\":\"#050505\",\"tickLabelColor\":\"#050505\",\"tickLabelMask\":\"\",\"verticalTickLabels\":false,\"axisLineColor\":\"#000000\",\"labelFont\":{\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false},\"tickLabelFont\":{\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false}}},\"valueAxisLabelExpression\":\"\",\"valueAxisFormat\":{\"axisFormat\":{\"labelColor\":\"#050505\",\"tickLabelColor\":\"#050505\",\"tickLabelMask\":\"\",\"verticalTickLabels\":false,\"axisLineColor\":\"#000000\",\"labelFont\":{\"size\":10,\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false},\"tickLabelFont\":{\"size\":10,\"isBold\":false,\"isItalic\":false,\"isUnderline\":false,\"isStrikeThrough\":false}}}}}]},\"variables\":[],\"dataSets\":[{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"fields\":[{\"clazz\":\"java.lang.Integer\",\"name\":\"sum_travel_cost\"},{\"clazz\":\"java.lang.String\",\"name\":\"destination\"}],\"parameters\":[],\"variables\":[]}]},\"format\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"pdf\"}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		int status = jsonObject.getInt("status");
		Assert.assertEquals(1, status);
		JSONObject reportPageInfo = jsonObject.getJSONObject("response").getJSONObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.getInt("totalPageCount") >= 1);
		Assert.assertEquals("File successfully exported", jsonObject.getJSONObject("response").getString("response"));
		String uuid = jsonObject.getJSONObject("response").getJSONObject("jrxmlData").getString("uuid");
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
				assertEquals(1500, jasperDesign.getPageHeight());
				
				Map<String,JRDataset> dataSetMap =  jasperDesign.getDatasetMap();
				assertTrue(!dataSetMap.isEmpty());
				assertTrue(dataSetMap.containsKey("MainDataset"));
				
				JRDataset mainDataSet =  dataSetMap.get("MainDataset");
				assertTrue(!mainDataSet.getQuery().getText().isEmpty());
				
				Set<String> expectedFields =  Set.of("destination","sum_travel_cost");
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
				JRDesignChart chart =  (JRDesignChart) firstChild;
				assertNotNull("barchart object should not be null", chart);
				JRChartDataset dataset =  chart.getDataset();
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
				assertTrue(dsExpr.toString().contains("MAIN_DATASET"));
				
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
}
