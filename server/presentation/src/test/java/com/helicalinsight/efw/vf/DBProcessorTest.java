package com.helicalinsight.efw.vf;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.helicalinsight.efw.smtp.transport.factory.SmtpConnectionFactory;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.DBProcessor;

public class DBProcessorTest {

	@Test
	public void testgetIdByPath() {
		DBProcessor processor = new DBProcessor();
		String dir= "path";
		List<HIResourceDTO> dtoList = new ArrayList<>();
		HIResourceDTO dto1 = new HIResourceDTO();
		HIResourceDTO dto2 = new HIResourceDTO();
		dto1.setPath("path");
		dtoList.add(dto1);
		processor.getIdByPath(dir, dtoList);
	}
	@Test
	public void testGetIdByPath() {
		DBProcessor processor = new DBProcessor();
		String dir= "path";
		List<HIResourceDTO> dtoList = new ArrayList<>();
		HIResourceDTO dto1 = new HIResourceDTO();
		dto1.setPath("path@");
		List<HIResourceDTO> list = new ArrayList<>();
		HIResourceDTO dto2 = new HIResourceDTO();
		dto2.setPath("path");
		list.add(dto2);
		dto1.setChildren(list);
		dtoList.add(dto1);
		
		processor.getIdByPath(dir, dtoList);
	}
	@Test
	public void testfindIdFromResource() {
		DBProcessor processor = new DBProcessor();
		List<HIResourceDTO> dtoList = new ArrayList<>();
		HIResourceDTO dto = new HIResourceDTO();
		dto.setPath("path");
		dtoList.add(dto);
		processor.findIdFromResource(dtoList, "path");	
		
	}
	
	@Test
	public void testfindChildIdFromResource() {
		DBProcessor processor = new DBProcessor();
		List<HIResourceDTO> dtoList = new ArrayList<>();
		HIResourceDTO dto = new HIResourceDTO();
		dto.setChildren(dtoList);
		HIResourceDTO dto1 = new HIResourceDTO();
		dtoList.add(dto1);
		processor.findChildIdFromResource(dto);	
		
	}
	
	@Test
	public void testcountResource() {
		DBProcessor processor = new DBProcessor();
		List<HIResourceDTO> dtoList = new ArrayList<>();
		HIResourceDTO dto = new HIResourceDTO();
		dto.setPath("url");
		dtoList.add(dto);
		processor.countResource("url", dtoList);	
		
	}
	
	@Test
	public void testflattenHelperFolder() {
		DBProcessor processor = new DBProcessor();
		List<HIResourceDTO> dtoList = new ArrayList<>();
		HIResourceDTO dto = new HIResourceDTO();
		dto.setPath("url");
		dtoList.add(dto);
		List<HIResourceDTO> nestedList=new ArrayList<>();
		HIResourceDTO dto1 = new HIResourceDTO();
		List<HIResourceDTO> list=new ArrayList<>();
		list.add(dto);
		dto1.setDescription("des");
		dto1.setInherit("inherit");
		dto1.setChildren(list);
		nestedList.add(dto1);
		DBProcessor.flattenHelperFolder(nestedList, dtoList);	
		
	}
	@Test
	public void testFlattenHelperFolder() {
		DBProcessor processor = new DBProcessor();
		List<HIResourceDTO> dtoList = new ArrayList<>();
		HIResourceDTO dto = new HIResourceDTO();
		dto.setPath("url");
		dtoList.add(dto);
		List<HIResourceDTO> nestedList=new ArrayList<>();
		HIResourceDTO dto1 = new HIResourceDTO();
		List<HIResourceDTO> list=new ArrayList<>();
	
		dto1.setChildren(list);
		nestedList.add(dto1);
		DBProcessor.flattenHelperFolder(nestedList, dtoList);	
		
	}

	@Test
	public void test() throws Exception {
		SmtpConnectionFactory connectionFactory = new SmtpConnectionFactory(null, null, null, false);
	    connectionFactory.getDefaultListeners();
	    connectionFactory.isInvalidateConnectionOnException();
	    connectionFactory.getSession();
	    connectionFactory.getTransportFactory();
	    connectionFactory.getConnectionStrategy();

	    
	}
	
	
}
