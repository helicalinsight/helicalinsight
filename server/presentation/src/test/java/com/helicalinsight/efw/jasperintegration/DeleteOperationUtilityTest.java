//package com.helicalinsight.efw.jasperintegration;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//
//import com.helicalinsight.efw.exceptions.OperationFailedException;
//import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
//import com.helicalinsight.efw.io.delete.DeleteOperationUtility;
//
//public class DeleteOperationUtilityTest {
//
//	@Test
//	public void testSetListOfFileExtensions() {
//		List<String> listOfFileExtensions = new ArrayList<>();
//		listOfFileExtensions.add("java");
//		listOfFileExtensions.add("class");
//		listOfFileExtensions.add("efw");
//		DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
//		deleteOperationUtility.setListOfFileExtensions(listOfFileExtensions);
//	}
//
//	@Test(expected = OperationFailedException.class)
//	public void testTryDeleting() throws UnSupportedRuleImplementationException {
//		DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
//		File file = new File("D:/home/helical/Perf/test.txt");
//		deleteOperationUtility.tryDeleting(file);
//	}
//
//	@Test
//	public void testFindCorrespondingClass() {
//		DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
//		String key = "112";
//		String action = "move";
//		deleteOperationUtility.findCorrespondingClass(key, action);
//	}
//	@Test
//	public void testFindCorrespondingKey() {
//		DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
//		String key = "report";
//		deleteOperationUtility.findCorrespondingKey(key);
//	}
//	@Test
//	public void testFindCorrespondingKey1() {
//		DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
//		String key = "111";
//		deleteOperationUtility.findCorrespondingKey(key);
//	}
//	@Test
//	public void testDeleteFile() throws UnSupportedRuleImplementationException {
//		DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
//		File file = new File("test.txt");
//		deleteOperationUtility.deleteFile(file);
//	}
//	@Test(expected = OperationFailedException.class)
//	public void testDeleteDirectory() throws UnSupportedRuleImplementationException {
//		DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
//		File file = new File("D:/home/helical/Perf/test.txt");
//		deleteOperationUtility.deleteDirectory(file);
//	}
//}
