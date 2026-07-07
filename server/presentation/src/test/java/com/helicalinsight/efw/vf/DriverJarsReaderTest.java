// TODO: Configuration issue.
//package com.helicalinsight.efw.vf;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.Map;
//
//import org.junit.Test;
//
//import com.helicalinsight.efw.exceptions.NoJarFileFoundException;
//import com.helicalinsight.efw.utility.DriverJarsReader;
//import com.helicalinsight.efw.utility.JdbcUrlFormatUtility;
//
//import net.sf.json.JSONObject;
//
//public class DriverJarsReaderTest {
//
//	@Test(expected = NoJarFileFoundException.class)
//	public void testgetAvailableJdbcDrivers() {
//		DriverJarsReader driverJarsReader = new DriverJarsReader();
//		File f = new File("hi-repository/System/Drivers");
//		DriverJarsReader.getAvailableJdbcDrivers(f.toString());
//	}
//	@Test
//	public void testGetAvailableJdbcDrivers() {
//		DriverJarsReader driverJarsReader = new DriverJarsReader();
//		File f = new File("/home/helical/Performance/hi/hi-repository/System/Drivers");
//		DriverJarsReader.getAvailableJdbcDrivers(f.toString());
//	}
//	
//	//JdbcUrlFormatUtility
//	@Test
//	public void testgetJsonOfDrivers() {
//		JdbcUrlFormatUtility formatUtility = new JdbcUrlFormatUtility();
//		JdbcUrlFormatUtility.getJsonOfDrivers();
//	}
//	@Test
//	public void testgetRegexString() {
//		JSONObject obj = new JSONObject();
//		obj.put("driverLoadRegexPatterns", "");
//		JdbcUrlFormatUtility.getRegexString(obj);
//	}
//	
//	@Test
//	public void testgetExcludeRegexFromFatList() {
//		JSONObject obj = new JSONObject();
//		obj.put("exculdeRegexFromLoadedClass", "tjkg7");
//		 List<String> fatList = new ArrayList<>();
//		JdbcUrlFormatUtility.getExcludeRegexFromFatList(obj,fatList);
//	}
//	
//	@Test
//	public void testremoveFromList() {
//		String[] array = new String[] {"Welcome","to","Gfg"};
//		List<String> names = new LinkedList<>();
//	       
//        names.add("Welcome");
//        names.add("To");
//        names.add("Gfg");
//		ListIterator<String> iterator = names.listIterator();
//		JdbcUrlFormatUtility.removeFromList(array, iterator);
//	}
//	@Test
//	public void testgetFatListForRegex() {
//		List<String> fatList = new ArrayList<>();
//		String regex = "helical,insight";
//		JdbcUrlFormatUtility.getFatListForRegex(fatList, regex);
//	}
//	@Test
//	public void testgetDialectInformation() {
//		
//		JdbcUrlFormatUtility.getDialectInformation();
//	}
//	@Test
//	public void testgetEnabledTypesForDrill() {
//		JdbcUrlFormatUtility.getEnabledTypesForDrill();
//	}
//	
//	
//}
//
