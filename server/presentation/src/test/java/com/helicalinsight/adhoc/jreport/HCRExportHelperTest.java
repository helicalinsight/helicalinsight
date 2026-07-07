package com.helicalinsight.adhoc.jreport;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.exceptions.HCRException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvMetadataExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.JsonExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRExportHelperTest {

	@Test
	public void ut_a1_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		exportHelper.exportInBytes("HTML", 10, 10,true);
		
	}
	
	@Test
	public void ut_a2_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		exportHelper.exportInBytes("HTML", 10, 10,true);
		
	}
	
	
	@Test(expected = EfwException.class)
	public void ut_a3_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		exportHelper.exportInBytes("HTML", 8, 10,true);
		
	}
	@Test
	public void ut_a4_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		try(MockedConstruction<JRCsvExporter> mockedConstruction = mockConstruction(JRCsvExporter.class, (mock,context)->{	
		})){
			exportHelper.exportInBytes("CSV", 10, 10,true);
		}
		
	}
	
	@Test
	public void ut_a5_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		try(MockedConstruction<JRXmlExporter> mockedConstruction = mockConstruction(JRXmlExporter.class, (mock,context)->{	
		})){
			exportHelper.exportInBytes("XML", 10, 10,true);
		}
		
	}
	
	@Test
	public void ut_a6_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		try(MockedConstruction<JRXlsxExporter> mockedConstruction = mockConstruction(JRXlsxExporter.class, (mock,context)->{	
		})){
			exportHelper.exportInBytes("XLSX", 10, 10,true);
		}
		
	}
	
	@Test
	public void ut_a7_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		try(MockedConstruction<JRPdfExporter> mockedConstruction = mockConstruction(JRPdfExporter.class, (mock,context)->{	
		})){
			exportHelper.exportInBytes("PDF", 10, 10,true);
		}
		
	}
	
	@Test(expected = EfwException.class)
	public void ut_a8_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		try(MockedConstruction<ByteArrayOutputStream> construction = mockConstruction(ByteArrayOutputStream.class,(mock, context)->{
			doAnswer(invocation -> {
			    throw new IOException("Mocked exception");
			}).when(mock).close();
		})){
			try(MockedConstruction<JRPdfExporter> mockedConstruction = mockConstruction(JRPdfExporter.class, (mock,context)->{	
				doAnswer(invocation -> {
				    throw new JRRuntimeException("Mocked exception");
				}).when(mock).exportReport();
			})){
				
				exportHelper.exportInBytes("PDF", 10, 10,true);
			}
		}
		
		
	}
	
	@Test(expected = JRException.class)
	public void ut_a9_test_exportInBytes() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		try(MockedConstruction<JRPdfExporter> mockedConstruction = mockConstruction(JRPdfExporter.class, (mock,context)->{	
		})){
			exportHelper.exportInBytes("TEST", 10, 10,true);
		}
		
	}
	
	
	@Test
	public void ut_b1_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		boolean exportIntoFiles = exportHelper.exportIntoFiles("html", 10, jrxmlData);
		assertTrue(exportIntoFiles);
	}
	
	@Test
	public void ut_b2_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		boolean exportIntoFiles = exportHelper.exportIntoFiles("html", 10, jrxmlData);
		assertTrue(exportIntoFiles);
	}
	
	@Test
	public void ut_b3_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		boolean exportIntoFiles = exportHelper.exportIntoFiles("csv", 10, jrxmlData);
		assertTrue(exportIntoFiles);
	}
	
	@Test
	public void ut_b4_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		boolean exportIntoFiles = exportHelper.exportIntoFiles("xml", 10, jrxmlData);
		assertTrue(exportIntoFiles);
	}
	
	
	@Test
	public void ut_b5_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JsonObject designerProperties = new JsonObject();
		newFormData.add("designerProperties", designerProperties);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JRXlsxExporter> mockedConstruction = mockConstruction(JRXlsxExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("XLSX", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}	
		
	}
	
	@Test
	public void ut_b6_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JRPdfExporter> mockedConstruction = mockConstruction(JRPdfExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("PDF", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
		
	}
	
	@Test
	public void ut_b7_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JRRtfExporter> mockedConstruction = mockConstruction(JRRtfExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("RTF", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	@Test
	public void ut_b8_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JRDocxExporter> mockedConstruction = mockConstruction(JRDocxExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("DOCX", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	@Test
	public void ut_b9_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JROdtExporter> mockedConstruction = mockConstruction(JROdtExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("ODT", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	@Test
	public void ut_c1_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JROdsExporter> mockedConstruction = mockConstruction(JROdsExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("ODS", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_c2_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JRPptxExporter> mockedConstruction = mockConstruction(JRPptxExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("PPTX", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_c3_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JsonObject designerProperties = new JsonObject();
		designerProperties.addProperty("pageWidth", 10);
		designerProperties.addProperty("pageHeight", 10);
		newFormData.add("designerProperties", designerProperties);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JRTextExporter> mockedConstruction = mockConstruction(JRTextExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("TXT", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_c4_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JRXlsExporter> mockedConstruction = mockConstruction(JRXlsExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("XLS", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_c5_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JRCsvMetadataExporter> mockedConstruction = mockConstruction(JRCsvMetadataExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("CSVMETADATA", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_c6_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JsonExporter> mockedConstruction = mockConstruction(JsonExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("JSONMETADATA", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_c7_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		newFormData.addProperty("isMail", true);
		newFormData.addProperty("emailExportName", "emailExportName");
		
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JsonExporter> mockedConstruction = mockConstruction(JsonExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("JSONMETADATA", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	@Test
	public void ut_c8_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		PrintPageFormat pageFormat = mock(PrintPageFormat.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		when(jasperPrint.getPageFormat(anyInt())).thenReturn(pageFormat);
		when(pageFormat.getPageHeight()).thenReturn(10);
		when(pageFormat.getPageHeight()).thenReturn(10);
		
		try(MockedConstruction<JRGraphics2DExporter> mockedConstruction = mockConstruction(JRGraphics2DExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("PNG", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_c9_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		PrintPageFormat pageFormat = mock(PrintPageFormat.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		when(jasperPrint.getPageFormat(anyInt())).thenReturn(pageFormat);
		when(pageFormat.getPageHeight()).thenReturn(10);
		doAnswer(invocation -> {
		    throw new IOException("Mocked exception");
		}).when(pageFormat).getPageHeight();
		
		try(MockedConstruction<JRGraphics2DExporter> mockedConstruction = mockConstruction(JRGraphics2DExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("PNG", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_d1_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		PrintPageFormat pageFormat = mock(PrintPageFormat.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		when(jasperPrint.getPageFormat(anyInt())).thenReturn(pageFormat);
		when(pageFormat.getPageHeight()).thenReturn(10);
		when(pageFormat.getPageHeight()).thenReturn(10);
		try(MockedConstruction<JRGraphics2DExporter> mockedConstruction = mockConstruction(JRGraphics2DExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("JPEG", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test
	public void ut_d2_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		PrintPageFormat pageFormat = mock(PrintPageFormat.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		when(jasperPrint.getPageFormat(anyInt())).thenReturn(pageFormat);
		when(pageFormat.getPageHeight()).thenReturn(10);
		when(pageFormat.getPageHeight()).thenReturn(10);
		try(MockedConstruction<JRGraphics2DExporter> mockedConstruction = mockConstruction(JRGraphics2DExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("JPG", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test(expected = HCRException.class)
	public void ut_d3_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		PrintPageFormat pageFormat = mock(PrintPageFormat.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		when(jasperPrint.getPageFormat(anyInt())).thenReturn(pageFormat);
		when(pageFormat.getPageHeight()).thenReturn(10);
		when(pageFormat.getPageHeight()).thenReturn(10);
		try(MockedConstruction<JRGraphics2DExporter> mockedConstruction = mockConstruction(JRGraphics2DExporter.class, (mock,context)->{	
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("test", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	
	@Test(expected =HCRException.class )
	public void ut_d4_test_exportIntoFiles() throws JRException {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		newFormData.addProperty("isMail", true);
		newFormData.addProperty("emailExportName", "emailExportName");
		
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		try(MockedConstruction<JsonExporter> mockedConstruction = mockConstruction(JsonExporter.class, (mock,context)->{
			doAnswer(invocation -> {
			    throw new JRRuntimeException("Mocked exception");
			}).when(mock).exportReport();
		})){
			boolean exportIntoFiles = exportHelper.exportIntoFiles("JSONMETADATA", 10, jrxmlData);
			assertTrue(exportIntoFiles);
		}
		
	}
	@Test
	public void ut_d5_test_handleMultiExport() {
		JsonObject newFormData= new JsonObject();
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		newFormData.add("saveDetails", saveDetails);
		
		JasperPrint jasperPrint = mock(JasperPrint.class);
		String uuid = "uuid";
		HCRExportHelper exportHelper = new HCRExportHelper(newFormData,jasperPrint,uuid);
		JsonObject jrxmlData = new JsonObject();
		JsonArray format = new JsonArray();
		format.add("html");
		exportHelper.handleMultiExport(format, 10, jrxmlData);
		
	}
}
