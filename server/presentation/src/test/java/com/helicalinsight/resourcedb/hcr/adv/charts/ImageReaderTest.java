package com.helicalinsight.resourcedb.hcr.adv.charts;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import com.helicalinsight.efw.externalresources.ImageReader;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import jakarta.servlet.http.HttpServletResponse;


public class ImageReaderTest {
	
	
	@Test
	public void  readImage_should_read_image() {
		String filePath = "src/test/resources/test/echo-sport.jpg";
		HttpServletResponse response = new MockHttpServletResponse();
		ImageReader imageReader = new ImageReader();
		File imageFile = new File(filePath);
		imageReader.setFile(imageFile);	
		imageReader.setResponse(response);
		imageReader.getFileType();
	 	Assert.assertEquals("image/jpeg", response.getContentType());
	}
	
	@Test
	public void  readWrongImage_should_throw_exception() {
		String filePath = "src/test/resources/test/echo-sport_invalid.jpg";
		HttpServletResponse response = new MockHttpServletResponse();
		ImageReader imageReader = new ImageReader();
		File imageFile = new File(filePath);
		imageReader.setFile(imageFile);	
		imageReader.setResponse(response);
		imageReader.getFileType();
	 	Assert.assertEquals("image/jpeg", response.getContentType());
	 	Assert.assertNull(imageReader.getFileType());
	} 
	
	
	@Test
    public void testGetContentTypeWithValidExtensions() throws Exception {
		
		Map<String,String> extensionContentTypeMap = new HashMap<>();
		extensionContentTypeMap.put("svg", "image/svg+xml");
		extensionContentTypeMap.put("png", "image/png");
		extensionContentTypeMap.put("jpg", "image/jpeg");
		extensionContentTypeMap.put("jpeg", "image/jpeg");
		extensionContentTypeMap.put("gif", "image/gif");
		extensionContentTypeMap.put("bmp", "image/bmp");
		extensionContentTypeMap.put("webp", "image/webp");
		extensionContentTypeMap.put("ico", "image/x-icon");
		extensionContentTypeMap.put("tiff", "image/tiff");
		extensionContentTypeMap.put("tif", "image/tiff");
		extensionContentTypeMap.put("wrongExtension", "application/octet-stream");
		
		extensionContentTypeMap.entrySet().stream().forEach(entry -> {
				String extension = entry.getKey();
				String expected = entry.getValue();
				String actual = ApplicationUtilities.getContentType(extension);
				Assert.assertEquals(expected, actual);
		});
    }

}
