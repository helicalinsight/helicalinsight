//// TODO: Configuration issue
//
//import java.io.File;
//
//import org.junit.Test;
//
//import com.helicalinsight.datasource.DatabaseConnectionFactory;
//import com.helicalinsight.efw.components.DataSourceSecurityUtility;
//import com.helicalinsight.efw.exceptions.EfwException;
//import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
//import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
//import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//public class DataSourceSecurityUtilityTest {
//
//
//	
////	@Test
////	public  void testcheckInstanceOfJsonArray() {
////	
////		JSONArray arr = new JSONArray();
////		arr.add(1);
////		arr.add(2);
////		
////		JSONArray checkInstanceOfJsonArray = DataSourceSecurityUtility.checkInstanceOfJsonArray(arr);
////		System.out.println(checkInstanceOfJsonArray);
////	}
//	
//	@Test(expected = Exception.class)
//	public void testThrowException() {
//		DataSourceSecurityUtility.throwException();
//	}
//	@Test(expected = Exception.class)
//	public void theThrowResourceNotFoundException() {
//		DataSourceSecurityUtility.throwResourceNotFoundException();
//	}
//	
//	@Test
//	public void testisGlobalAccessible() {
//		
//		DataSourceSecurityUtility.isGlobalAccessible("","READ");
//	}
//	//@Test
//	public void testgetPermissionLevel() {
//		
//
//		String str = "READ_WRITE";
//		DataSourceSecurityUtility.getPermissionLevel(str);
//	}
//	@Test
//	public void testValidateGlobalDataSourceAccessForWriteOperation() {
//		
//		DataSourceSecurityUtility.validateGlobalDataSourceAccessForWriteOperation("","edit");
//	}
//	
//	@Test(expected =EfwException.class)
//	public void testValidateGlobalDataSourceAccessForDeleteOperation() {
//		
//		DataSourceSecurityUtility.validateGlobalDataSourceAccessForDeleteOperation("1","delete");
//	}
//
//	
//	@Test(expected = Exception.class)
//	public void testvalidateGlobalDS() {
//		JSONObject obj = new JSONObject();
//		obj.put("@id", "");
//		obj.put("access", "edit");
//		boolean validateDataSource = DataSourceSecurityUtility.validateGlobalDS("",obj,"edit");
//		System.out.println(validateDataSource);
//	}
//	@Test(expected = Exception.class)
//	public void testcheckEfwdPermission() {
//		File file = new File("EFWD.txt");
//		DataSourceSecurityUtility.checkEfwdPermission("",file,"edit");
//	}
//	
//}
