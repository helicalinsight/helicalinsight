//package com.helicalinsight.adhoc.report;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import com.helicalinsight.resourcesecurity.jaxb.CanvasElements;
//import com.helicalinsight.resourcesecurity.jaxb.MetadataReference;
//import com.helicalinsight.resourcesecurity.jaxb.Security;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class AdhocReportTest {
//
//	@Mock
//	private MetadataReference metadataReference;
//
//	@Mock
//	private CanvasElements canvasElements;
//
//	@Mock
//	private Security security;
//
//	private AdhocReport report;
//
//	@Before
//	public void setUp() {
//		MockitoAnnotations.openMocks(this);
//		report = new AdhocReport();
//	}
//
//	@Test
//	public void testGettersAndSetters() {
//		report.setReportName("Test Report");
//		assertEquals("Test Report", report.getReportName());
//
//		report.setVisible("true");
//		assertEquals("true", report.getVisible());
//
//		report.setDescription("Test Description");
//		assertEquals("Test Description", report.getDescription());
//
//		report.setMetadataReference(metadataReference);
//		assertEquals(metadataReference, report.getMetadataReference());
//
//		report.setCanvasElements(canvasElements);
//		assertEquals(canvasElements, report.getCanvasElements());
//
//		report.setState("Test State");
//		assertEquals("Test State", report.getState());
//
//		report.setSecurity(security);
//		assertEquals(security, report.getSecurity());
//
//		Security.Share share = new Security.Share();
//		report.setShare(share);
//		assertEquals(share, report.getShare());
//	}
//
//	@Test
//	public void testToString() {
//		report.setReportName("Test Report");
//		report.setVisible("true");
//		report.setDescription("Test Description");
//		report.setMetadataReference(metadataReference);
//		report.setCanvasElements(canvasElements);
//		report.setState("Test State");
//		report.setSecurity(security);
//		Security.Share share = new Security.Share();
//		report.setShare(share);
//
//		String expectedToString = "AdhocReport{" + "reportName='Test Report', " + "visible='true', "
//				+ "description='Test Description', " + "metadataReference=" + metadataReference + ", "
//				+ "canvasElements=" + canvasElements + ", " + "state='Test State', " + "security=" + security + ", "
//				+ "share=" + share + "}";
//		assertEquals(expectedToString, report.toString());
//	}
//}
