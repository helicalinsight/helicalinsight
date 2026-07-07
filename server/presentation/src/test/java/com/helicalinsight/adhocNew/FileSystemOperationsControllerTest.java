package com.helicalinsight.adhocNew;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import  org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.context.ApplicationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.adhoc.RenameOperationHandler;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.io.ExportOperationHandler;
import com.helicalinsight.efw.io.FileArchive;
import com.helicalinsight.efw.io.IUpload;
import com.helicalinsight.efw.io.MoveToOperationHandler;
import com.helicalinsight.efw.io.NewFolderHandler;
import com.helicalinsight.efw.io.UploadClassFactory;
import com.helicalinsight.efw.io.delete.DeleteOperationHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileSystemOperationsControllerTest {

	@Test
	public void ut_a1_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		
		NewFolderHandler newFolderHandler = mock(NewFolderHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		
		when(applicationContext.getBean("newFolderHandler")).thenReturn(newFolderHandler);
		when(request.getParameter("folderName")).thenReturn("");
		
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "newFolder", request, response);
			
		}
	}
	
	@Test
	public void ut_a2_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		
		NewFolderHandler newFolderHandler = mock(NewFolderHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		
		when(applicationContext.getBean("newFolderHandler")).thenReturn(newFolderHandler);
		when(request.getParameter("folderName")).thenReturn(null);
		
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "newFolder", request, response);
			
		}
	}
	
	@Test
	public void ut_a3_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		
		NewFolderHandler newFolderHandler = mock(NewFolderHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		
		when(applicationContext.getBean("newFolderHandler")).thenReturn(newFolderHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "newFolder", request, response);
			
		}
	}
	
	@Test
	public void ut_a4_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		NewFolderHandler newFolderHandler = mock(NewFolderHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		String handle[] = {"handle"};
		when(applicationContext.getBean("newFolderHandler")).thenReturn(newFolderHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(newFolderHandler.handle(anyString(), anyString())).thenReturn(handle);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "newFolder", request, response);
			
		}
	}
	
	@Test
	public void ut_a5_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		RenameOperationHandler renameOperationHandler = mock(RenameOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("renameOperationHandler")).thenReturn(renameOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(renameOperationHandler.handle(anyString())).thenReturn(true);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "rename", request, response);
			
		}
	}
	
	@Test
	public void ut_a6_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		RenameOperationHandler renameOperationHandler = mock(RenameOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("renameOperationHandler")).thenReturn(renameOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(renameOperationHandler.handle(anyString())).thenReturn(false);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "rename", request, response);
			
		}
	}
	@Test
	public void ut_a7_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		MoveToOperationHandler moveToOperationHandler = mock(MoveToOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("moveToOperationHandler")).thenReturn(moveToOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(request.getParameter("destination")).thenReturn("destination");
		when(moveToOperationHandler.handle(anyString(),anyString())).thenReturn(true);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "move", request, response);
			
		}
	}
	
	@Test
	public void ut_a8_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		MoveToOperationHandler moveToOperationHandler = mock(MoveToOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("moveToOperationHandler")).thenReturn(moveToOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(request.getParameter("destination")).thenReturn("destination");
		when(moveToOperationHandler.handle(anyString(),anyString())).thenReturn(false);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "move", request, response);
			
		}
	}
	
	@Test
	public void ut_a9_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		MoveToOperationHandler moveToOperationHandler = mock(MoveToOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("moveToOperationHandler")).thenReturn(moveToOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(request.getParameter("destination")).thenReturn(null);
		when(moveToOperationHandler.handle(anyString(),anyString())).thenReturn(false);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "move", request, response);
			
		}
	}
	
	@Test
	public void ut_b1_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		DeleteOperationHandler deleteOperationHandler = mock(DeleteOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("deleteOperationHandler")).thenReturn(deleteOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(deleteOperationHandler.handle(anyString())).thenReturn(true);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "delete", request, response);
			
		}
	}
	
	@Test
	public void ut_b2_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		DeleteOperationHandler deleteOperationHandler = mock(DeleteOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("deleteOperationHandler")).thenReturn(deleteOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		
		when(deleteOperationHandler.handle(anyString())).thenReturn(false);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "delete", request, response);
			
		}
	}
	
	@Test
	public void ut_b3_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		DeleteOperationHandler deleteOperationHandler = mock(DeleteOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("deleteOperationHandler")).thenReturn(deleteOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		
		when(deleteOperationHandler.handle(anyString())).thenReturn(false);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "delete", request, response);
			
		}
	}
	
	@Test
	public void ut_b4_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
	
		ExportOperationHandler exportOperationHandler = mock(ExportOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		when(applicationContext.getBean("exportOperationHandler")).thenReturn(exportOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		
		//when(exportOperationHandler.validateSource(anyString())).thenReturn(null);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "export", request, response);
			
		}
	}
	
	@Test
	public void ut_b5_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("arr");
		ExportOperationHandler exportOperationHandler = mock(ExportOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		controller.setApplicationContext(applicationContext);
		
		File file = new File("path");
		when(applicationContext.getBean("exportOperationHandler")).thenReturn(exportOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		
		when(exportOperationHandler.validateSource(anyString())).thenReturn(null);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "export", request, response);
			
		}
	}
	
	@Test
	public void ut_b6_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("arr");
		ExportOperationHandler exportOperationHandler = mock(ExportOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		Principal principal = mock(Principal.class);
		controller.setApplicationContext(applicationContext);
		
		File file = new File("path");
		List<File> listOfFilesToBeZipped = Arrays.asList(file);
		when(applicationContext.getBean("exportOperationHandler")).thenReturn(exportOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(exportOperationHandler.validateSource(anyString())).thenReturn(listOfFilesToBeZipped);
		when(principal.getUsername()).thenReturn("userName");
		try(MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)){
			try(MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)){
				mockedStatic2.when(()-> AuthenticationUtils.getUserDetails()).thenReturn(principal);
		
			controller.executeFileSystemOperations(sourceArray.toString(), "export", request, response);
			}
		}
	}
	
	@Test
	public void ut_b7_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("arr");
		ExportOperationHandler exportOperationHandler = mock(ExportOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		Principal principal = mock(Principal.class);
		controller.setApplicationContext(applicationContext);
	
		File file = new File("path");
		List<File> listOfFilesToBeZipped = Arrays.asList(file);
		when(applicationContext.getBean("exportOperationHandler")).thenReturn(exportOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(exportOperationHandler.validateSource(anyString())).thenReturn(listOfFilesToBeZipped);
		when(principal.getUsername()).thenReturn("userName");
		
		
		try(MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)){
			try(MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)){
				try(MockedConstruction<FileArchive> construction = mockConstruction(FileArchive.class,(mock,context)->{
					when(mock.getExtensionOfZipFile()).thenReturn("txt");
					when(mock.archive(any(File.class), any(File[].class))).thenReturn(true);
				})){
					mockedStatic2.when(()-> AuthenticationUtils.getUserDetails()).thenReturn(principal);
					controller.executeFileSystemOperations(sourceArray.toString(), "export", request, response);
				}
				
			}
		}
	}
	
	@Test
	public void ut_b8_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("arr");
		ExportOperationHandler exportOperationHandler = mock(ExportOperationHandler.class);
		ApplicationContext applicationContext =  mock(ApplicationContext.class);
		Principal principal = mock(Principal.class);
		controller.setApplicationContext(applicationContext);
	
		File file = new File("path");
		List<File> listOfFilesToBeZipped = Arrays.asList(file);
		when(applicationContext.getBean("exportOperationHandler")).thenReturn(exportOperationHandler);
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(exportOperationHandler.validateSource(anyString())).thenReturn(listOfFilesToBeZipped);
		when(principal.getUsername()).thenReturn("userName");
		
		
		try(MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)){
			try(MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)){
				try(MockedConstruction<FileArchive> construction = mockConstruction(FileArchive.class,(mock,context)->{
					when(mock.getExtensionOfZipFile()).thenReturn(null);
					when(mock.archive(any(File.class), any(File[].class))).thenReturn(true);
				})){
					mockedStatic2.when(()-> AuthenticationUtils.getUserDetails()).thenReturn(principal);
					controller.executeFileSystemOperations(sourceArray.toString(), "export", request, response);
				}
				
			}
		}
	}
	
	
	@Test
	public void ut_b9_test_executeFileSystemOperations() throws UnSupportedRuleImplementationException, IOException  {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(request.getParameter("destination")).thenReturn("destination");
			try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeFileSystemOperations(sourceArray.toString(), "Not", request, response);
			
		}
	}
	
	@Test
	public void ut_c1_test_executeImportOperation() throws IOException  {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		MultipartFile fileItem = mock(MultipartFile.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		when(request.getParameter("folderName")).thenReturn("folderName");
		when(request.getParameter("destination")).thenReturn("destination");
			try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeImportOperation(fileItem, "destination", request, response);
			
		}
	}
	@Test
	public void ut_c2_test_executeImportOperation() throws IOException  {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonArray sourceArray = new JsonArray();
		MultipartFile fileItem = mock(MultipartFile.class);
		sourceArray.add("array");
		FileUploadException exception = mock(FileUploadException.class);
		when(request.getAttribute("file")).thenReturn(exception);
		//when(request.getParameter("destination")).thenReturn("destination");
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeImportOperation(fileItem, "destination", request, response);
			
		} 
	}
	
	@Test
	public void ut_c3_test_executeImportOperations() throws IOException  {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		MultipartFile fileItem = mock(MultipartFile.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		when(request.getAttribute("file")).thenReturn(new JsonObject());
		//when(request.getParameter("destination")).thenReturn("destination");
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			controller.executeImportOperation( fileItem,"destination", request, response);
			
		}
	}
	
	@Test
	public void ut_c4_test_executeImportOperation() throws IOException  {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IUpload importOperationHandler = mock(IUpload.class);
		MultipartFile fileItem = mock(MultipartFile.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		when(request.getAttribute("file")).thenReturn(fileItem);
		when(request.getParameter("type")).thenReturn("type");
		when(importOperationHandler.processMultipartItem(any(HttpServletRequest.class), any(FileItem.class), anyString(), anyString())).thenReturn(false);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			try(MockedStatic<UploadClassFactory> mockedStatic2 = mockStatic(UploadClassFactory.class)){
				mockedStatic.when(()-> ControllerUtils.statusZeroJson(anyString())).thenReturn(new JsonObject());
				mockedStatic2.when(()-> UploadClassFactory.getIUploadClass(anyString())).thenReturn(importOperationHandler);
				controller.executeImportOperation(fileItem, "destination", request, response);
			}
			
			
		}
	}
	
	@Test
	public void ut_c5_test_executeImportOperation() throws IOException  {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IUpload importOperationHandler = mock(IUpload.class);
		MultipartFile fileItem = mock(MultipartFile.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		when(request.getAttribute("file")).thenReturn(fileItem);
		when(request.getParameter("type")).thenReturn("type");
		when(request.getAttribute("message")).thenReturn("message");
		when(importOperationHandler.processMultipartItem(any(HttpServletRequest.class), any(FileItem.class), anyString(), anyString())).thenReturn(true);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			try(MockedStatic<UploadClassFactory> mockedStatic2 = mockStatic(UploadClassFactory.class)){
				mockedStatic.when(()-> ControllerUtils.statusZeroJson(anyString())).thenReturn(new JsonObject());
				mockedStatic2.when(()-> UploadClassFactory.getIUploadClass(anyString())).thenReturn(importOperationHandler);
				controller.executeImportOperation( fileItem,"destination", request, response);
			}
			
			
		}
	}
	
	@Test
	public void ut_c6_test_executeImportOperation() throws IOException  {
		FileSystemOperationsController controller = new FileSystemOperationsController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IUpload importOperationHandler = mock(IUpload.class);
		MultipartFile fileItem = mock(MultipartFile.class);
		JsonArray sourceArray = new JsonArray();
		sourceArray.add("array");
		when(request.getAttribute("file")).thenReturn(fileItem);
		when(request.getParameter("type")).thenReturn("type");
		JsonObject object = new JsonObject();
		object.addProperty("message", "message");
		when(request.getAttribute("message")).thenReturn(new JsonObject());
		when(importOperationHandler.processMultipartItem(any(HttpServletRequest.class), any(FileItem.class), anyString(), anyString())).thenReturn(true);
		try(MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)){
			try(MockedStatic<UploadClassFactory> mockedStatic2 = mockStatic(UploadClassFactory.class)){
				mockedStatic.when(()-> ControllerUtils.statusZeroJson(anyString())).thenReturn(new JsonObject());
				mockedStatic2.when(()-> UploadClassFactory.getIUploadClass(anyString())).thenReturn(importOperationHandler);
				controller.executeImportOperation( fileItem, "destination", request, response);
			}
			
			
		}
	}
}
