package com.helicalinsight.efw.io;

import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Component;

import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.services.FontImportService;

import jakarta.servlet.http.HttpServletRequest;

@Component("fontUploadHandler")
public class FontUploadHandler implements IUpload {
	
	private final FontImportService fontImportService;
	
	public FontUploadHandler(FontImportService fontImportService) {
		this.fontImportService = fontImportService;
	}
	
	@Override
	public boolean processMultipartItem(HttpServletRequest request, FileItem fileObject, String destination, String extensionOfFileTypeToBeImported) {
		try {
			fontImportService.importFont(fileObject, request.getParameter("zipFilePassword"));
			return true;
		}
		catch (Exception e) {
			throw new EfwServiceException("Could not import " + fileObject.getName() + " due to : " +  e.getMessage());
		}
	}

}
