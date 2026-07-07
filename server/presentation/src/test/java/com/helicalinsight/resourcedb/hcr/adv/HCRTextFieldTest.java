package com.helicalinsight.resourcedb.hcr.adv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRTextFieldTest {
	
	
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	private FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility integrationTestUtility;
	

	
	@Autowired
	private ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Before
	@Transactional
	public void setup() {
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}
	
	@Test
	public void hcrTextField_a1_addAllPaddings() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"details\":[{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[{\"textFieldExpression\":\"\\\"Text\\\"\",\"X\":150,\"Y\":0,\"shapeId\":\"node-4f306f7c-b06a-4ee7-9f2f-25ea3fca4569\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true,\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"rightLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"bottomLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"topLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"}}},\"padding\":{\"bottomPadding\":2,\"topPadding\":2,\"leftPadding\":2,\"rightPadding\":2}}],\"image\":[],\"lines\":[],\"break\":[]}]},\"type\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		
		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
			assertNotNull("JRXML input stream should not be null",jrxmlStream);
			JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);	
			assertNotNull("JasperDesign must be loaded", jasperDesign);
			JRDesignSection detailBand = (JRDesignSection) jasperDesign.getDetailSection();
			detailBand.getBandsList().get(0).getChildren().forEach(child -> {
				if ( child instanceof JRDesignTextField textField) {
					JRLineBox lineBox =  textField.getLineBox();
					int bottomPadding =  lineBox.getBottomPadding();
					int topPadding =  lineBox.getTopPadding();
					int leftPadding =  lineBox.getLeftPadding();
					int rightPadding =  lineBox.getRightPadding();
					assertEquals(2, bottomPadding);
					assertEquals(2, topPadding);
					assertEquals(2, leftPadding);
					assertEquals(2, rightPadding);
					
				}
			});
			
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}
	
	@Test
	public void hcrTextField_a2_addSinglePadding() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"details\":[{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[{\"textFieldExpression\":\"\\\"Text\\\"\",\"X\":150,\"Y\":0,\"shapeId\":\"node-4f306f7c-b06a-4ee7-9f2f-25ea3fca4569\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true,\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"rightLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"bottomLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"topLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"}}},\"padding\":{\"padding\":2}}],\"image\":[],\"lines\":[],\"break\":[]}]},\"type\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		
		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
			assertNotNull("JRXML input stream should not be null",jrxmlStream);
			JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);	
			assertNotNull("JasperDesign must be loaded", jasperDesign);
			JRDesignSection detailBand = (JRDesignSection) jasperDesign.getDetailSection();
			detailBand.getBandsList().get(0).getChildren().forEach(child -> {
				if ( child instanceof JRDesignTextField textField) {
					JRLineBox lineBox =  textField.getLineBox();
					int allPadding =  lineBox.getPadding();
					assertEquals(2, allPadding);
					assertEquals(2,  lineBox.getTopPadding().intValue());
					assertEquals(2, lineBox.getBottomPadding().intValue());
					assertEquals(2, lineBox.getRightPadding().intValue());
					assertEquals(2, lineBox.getLeftPadding().intValue());
				}
			});
			
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}
	
	@Test
	public void hcrTextField_a3_addSinglePaddingWithoutBorder() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"details\":[{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[{\"textFieldExpression\":\"\\\"Text\\\"\",\"X\":150,\"Y\":0,\"shapeId\":\"node-4f306f7c-b06a-4ee7-9f2f-25ea3fca4569\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true,\"padding\":{\"padding\":2}}],\"image\":[],\"lines\":[],\"break\":[]}]},\"type\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		
		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
			assertNotNull("JRXML input stream should not be null",jrxmlStream);
			JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);	
			assertNotNull("JasperDesign must be loaded", jasperDesign);
			JRDesignSection detailBand = (JRDesignSection) jasperDesign.getDetailSection();
			detailBand.getBandsList().get(0).getChildren().forEach(child -> {
				if ( child instanceof JRDesignTextField textField) {
					JRLineBox lineBox =  textField.getLineBox();
					int allPadding =  lineBox.getPadding();
					assertEquals(2, allPadding);
					assertEquals(0f,lineBox.getTopPen().getLineWidth().floatValue(),0.1);
					assertEquals(0f,lineBox.getBottomPen().getLineWidth().floatValue(),0.1);
					assertEquals(0f,lineBox.getLeftPen().getLineWidth().floatValue(),0.1);
					assertEquals(0f,lineBox.getRightPen().getLineWidth().floatValue(),0.1);

				}
			});
			
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}
	
	@Test
	public void hcrTextField_a4_noPadding() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"details\":[{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[{\"textFieldExpression\":\"\\\"Text\\\"\",\"X\":150,\"Y\":0,\"shapeId\":\"node-4f306f7c-b06a-4ee7-9f2f-25ea3fca4569\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true,\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"rightLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"bottomLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"topLine\":{\"lineWidth\":2,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"}}}}],\"image\":[],\"lines\":[],\"break\":[]}]},\"type\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		
		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
			assertNotNull("JRXML input stream should not be null",jrxmlStream);
			JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);	
			assertNotNull("JasperDesign must be loaded", jasperDesign);
			JRDesignSection detailBand = (JRDesignSection) jasperDesign.getDetailSection();
			detailBand.getBandsList().get(0).getChildren().forEach(child -> {
				if ( child instanceof JRDesignTextField textField) {
					JRLineBox lineBox =  textField.getLineBox();
					int bottomPadding =  lineBox.getBottomPadding();
					int topPadding =  lineBox.getTopPadding();
					int leftPadding =  lineBox.getLeftPadding();
					int rightPadding =  lineBox.getRightPadding();
					assertEquals(0, bottomPadding);
					assertEquals(0, topPadding);
					assertEquals(0, leftPadding);
					assertEquals(0, rightPadding);
				}
			});
			
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}
}
