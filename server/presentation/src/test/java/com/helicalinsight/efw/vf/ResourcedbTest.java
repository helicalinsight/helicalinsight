// TODO: Configuration issue.
//package com.helicalinsight.efw.vf;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.helicalinsight.admin.model.HIResource;
//import com.helicalinsight.resourcedb.CubeResourceRenameHandler;
//import com.helicalinsight.resourcedb.EFWCEResourceRenameHandler;
//import com.helicalinsight.resourcedb.EFWDDResourceRenameHandler;
//import com.helicalinsight.resourcedb.EFWResourceRenameHandler;
//import com.helicalinsight.resourcedb.EFWSRResourceRenameHandler;
//import com.helicalinsight.resourcedb.ExportResourceAction;
//import com.helicalinsight.resourcedb.HCRReportResourceRenameHandler;
//import com.helicalinsight.resourcedb.HReportResourceRenameHandler;
//import com.helicalinsight.resourcedb.InstantResourceRenameHandler;
//import com.helicalinsight.resourcedb.JsonToCollectionUtil;
//import com.helicalinsight.resourcedb.MetadataDumpDTO;
//import com.helicalinsight.resourcedb.MoveResourceAction;
//import com.helicalinsight.resourcedb.NewFolderHandlerDB1;
//import com.helicalinsight.resourcedb.RenameActionPayload;
//import com.helicalinsight.resourcedb.ReportResourceRenameHandler;
//import com.helicalinsight.resourcedb.ResultResourceRenameHandler;
//
//import net.sf.json.JSONObject;
//
//public class ResourcedbTest {
//
//	@Test
//	public void testJsonToCollectionUtil() throws JsonProcessingException {
//		JsonToCollectionUtil util = new JsonToCollectionUtil();
//		JSONObject obj = new JSONObject();
//		JsonToCollectionUtil.getMapObject(obj.toString());
//	}
//
//	@Test
//	public void testExportResourceAction() {
//		ExportResourceAction action = new ExportResourceAction();
//		action.performAction();
//	}
//
//	@Test
//	public void testNewFolderHandlerDB1() {
//		NewFolderHandlerDB1 action = new NewFolderHandlerDB1();
//		action.handle();
//	}
//
//	@Test
//	public void testMoveResourceAction() {
//		MoveResourceAction action = new MoveResourceAction();
//		action.performAction();
//	}
//
//	@Test
//	public void testRenameActionPayload() {
//		RenameActionPayload action = new RenameActionPayload();
//		action.setAction("action");
//		Assert.assertEquals("action", action.getAction());
//		List<List<String>> sourceArray = new ArrayList<>();
//		action.setSourceArray(sourceArray);
//		Assert.assertTrue(action.getSourceArray().isEmpty());
//	}
//
//	@Test
//	public void testCubeResourceRenameHandler() {
//		CubeResourceRenameHandler handler = new CubeResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//
//	@Test
//	public void testInstantResourceRenameHandler() {
//		InstantResourceRenameHandler handler = new InstantResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//
//	@Test
//	public void testHCRReportResourceRenameHandler() {
//		HCRReportResourceRenameHandler handler = new HCRReportResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//
//	@Test
//	public void testHReportResourceRenameHandler() {
//		HReportResourceRenameHandler handler = new HReportResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//
//	@Test
//	public void testEFWCEResourceRenameHandler() {
//		EFWCEResourceRenameHandler handler = new EFWCEResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//
//	@Test
//	public void testEFWSRResourceRenameHandler() {
//		EFWSRResourceRenameHandler handler = new EFWSRResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//
//	@Test
//	public void testResultResourceRenameHandler() {
//		ResultResourceRenameHandler handler = new ResultResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//
//	@Test
//	public void testEFWDDResourceRenameHandler() {
//		EFWDDResourceRenameHandler handler = new EFWDDResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//
//	@Test
//	public void testEFWResourceRenameHandler() {
//		EFWResourceRenameHandler handler = new EFWResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//	
//	@Test
//	public void testReportResourceRenameHandler() {
//		ReportResourceRenameHandler handler = new ReportResourceRenameHandler();
//		HIResource hiResource = new HIResource();
//		handler.setHiResource(hiResource);
//		handler.setIsPublic("temp");
//		handler.setIsVisible("visible");
//		handler.setMessage("message");
//		handler.setResourceId(12);
//		handler.setResourceType("type");
//		handler.setUpdatedName("name");
//		handler.performAction();
//	}
//	@Test
//	public void testMetadataDumpDTO() {
//		MetadataDumpDTO dto = new MetadataDumpDTO();
//		dto.setDumpType("temp");
//		dto.setId(123l);
//		dto.setLastUpdatedTime(new Date());
//		dto.setName("temp");
//		dto.setPath("path");
//		dto.setStatus("status");
//		dto.setTitle("title");
//		
//		Assert.assertEquals("temp", dto.getDumpType());
//		Assert.assertEquals("temp", dto.getName());
//		Assert.assertEquals("path", dto.getPath());
//		Assert.assertEquals("status", dto.getStatus());
//		Assert.assertEquals("title", dto.getTitle());
//		Assert.assertNotNull(dto.getLastUpdatedTime());
//		Assert.assertNotNull(dto.getId());
//		
//		dto.toString();
//	}
//	
//}
