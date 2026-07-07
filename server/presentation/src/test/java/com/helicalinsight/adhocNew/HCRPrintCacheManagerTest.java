//package com.helicalinsight.adhocNew;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockConstruction;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.when;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//import org.mockito.MockedConstruction;
//import org.mockito.MockedStatic;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.helicalinsight.adhoc.cachemanager.HCRPrintCacheManager;
//import com.helicalinsight.adhoc.jreport.IHCRGenerator;
//import com.helicalinsight.cache.CacheUtils;
//import com.helicalinsight.cache.model.Cache;
//import com.helicalinsight.cache.service.CacheService;
//import com.helicalinsight.datasource.EnhancedQueryExecutor;
//import com.helicalinsight.efw.components.DataSourceSecurityUtility;
//import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
//import com.helicalinsight.efw.utility.JsonUtils;
//
//import net.sf.jasperreports.engine.JRPrintPage;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.base.JRBasePrintPage;
//
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class HCRPrintCacheManagerTest {
//
//	@Test
//	public void ut_a1_test_setRequestData() {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//
//		JsonObject formData = new JsonObject();
//		formData.addProperty("noOfRecords", 1);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				String requestData = cacheManager.getRequestData();
//				assertEquals(formData.toString(), requestData);
//
//			}
//		}
//	}
//
//	@Test
//	public void ut_a2_test_readFileContent() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		String filePath = "/home/helical/Perf/example.txt";
//		File file = new File(filePath);
//		file.createNewFile();
//		try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))){
//		 // Write the first serialized object to the file
//		JRPrintPage object1 = new JRBasePrintPage(); // Replace YourObject with the class of objects you want to serialize
//        objectOutputStream.writeObject(object1);
//		} catch (IOException e) {
//            System.err.println("An error occurred while writing to the file: " + e.getMessage());
//            e.printStackTrace();
//        }
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				
//				cacheManager.readFileContent(filePath, false);
//				
//			}
//		}finally {
//			file.delete();
//		}
//
//
//	}
//
//	@Test
//	public void ut_a3_test_readFileContent() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		String filePath = "/home/helical/Perf/example.txt";
//		File file = new File(filePath);
//		file.createNewFile();
//		try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))){
//		JasperPrint object1 = new JasperPrint(); 
//        objectOutputStream.writeObject(object1);
//		} catch (IOException e) {
//            System.err.println("An error occurred while writing to the file: " + e.getMessage());
//            e.printStackTrace();
//        }
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				
//				cacheManager.readFileContent(filePath, true);
//				
//			}
//		}finally {
//			file.delete();
//		}
//
//	}
//
//	@Test(expected = ClassCastException.class)
//	public void ut_a4_test_readFileContent() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		String filePath = "/home/helical/Perf/example.txt";
//		File file = new File(filePath);
//		file.createNewFile();
//		
//		try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))){
//			
//			JasperPrint object1 = new JasperPrint(); 
//            objectOutputStream.writeObject(object1);
//            	JRPrintPage object2 = new JRBasePrintPage(); // Replace YourObject with the class of objects you want to serialize
//                objectOutputStream.writeObject(object2);
//           
//		} catch (IOException e) {
//            System.err.println("An error occurred while writing to the file: " + e.getMessage());
//            e.printStackTrace();
//        }
//		
//		JsonObject formData = new JsonObject();
//		formData.addProperty("noOfRecords", 1);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//					mockedStatic3.when(()-> CacheUtils.getCacheExtension()).thenReturn("cache");
//			
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				cacheManager.setRequestedPage(1);
//				cacheManager.readFileContent(filePath);
//				}
//			}
//		}finally {
//			file.delete();
//		}
//
//	}
//	
//	@Test(expected = ClassCastException.class)
//	public void ut_a5_test_readFileContent() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		String filePath = "/home/helical/Perf/example.txt";
//		File file = new File(filePath);
//		file.createNewFile();
//		
//		try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))){
//			JasperPrint object1 = new JasperPrint(); 
//            objectOutputStream.writeObject(object1);
//            
//			JRPrintPage object2 = new JRBasePrintPage();
//            objectOutputStream.writeObject(object2);
//            	
//		} catch (IOException e) {
//            System.err.println("An error occurred while writing to the file: " + e.getMessage());
//            e.printStackTrace();
//        }
//		
//		JsonObject formData = new JsonObject();
//		formData.addProperty("noOfRecords", 1);
//		formData.addProperty("isExport", true);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//					mockedStatic3.when(()-> CacheUtils.getCacheExtension()).thenReturn("cache");
//			
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				cacheManager.setRequestedPage(1);
//				cacheManager.readFileContent(filePath);
//				}
//			}
//		}finally {
//			file.delete();
//		}
//
//	}
//
//	@Test
//	public void ut_a6_test_saveToDisk() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		Cache requestCache = mock(Cache.class);
//		CacheService cacheService = mock(CacheService.class);
//		JasperPrint originalJasperPrint = new JasperPrint();
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//					mockedStatic3.when(()-> CacheUtils.getCacheExtension()).thenReturn("cache");
//			
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				
//				
//				cacheManager.saveToDisk(requestCache, null,originalJasperPrint );
//				}
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_a7_test_saveToDisk() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		Cache requestCache = mock(Cache.class);
//		CacheService cacheService = mock(CacheService.class);
//		
//		JRPrintPage jrPrintPage = mock(JRPrintPage.class);
//		
//		JasperPrint originalJasperPrint = new JasperPrint();
//		originalJasperPrint.addPage(jrPrintPage);
//		originalJasperPrint.addPage(jrPrintPage);
//		
//		
//		JsonObject formData = new JsonObject();
//		formData.addProperty("noOfRecords", 1);
//		formData.addProperty("isExport", true);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//					mockedStatic3.when(()-> CacheUtils.getCacheExtension()).thenReturn("cache");
//			
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				cacheManager.saveToDisk(requestCache, null,originalJasperPrint );
//				}
//			}
//		}
//
//	}
//	
//	@Test
//	public void ut_a8_test_saveAllCacheInDisk() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				JRPrintPage jrPrintPage = mock(JRPrintPage.class);
//				List<JRPrintPage> pages = Arrays.asList(jrPrintPage);
//				JasperPrint object1 = new JasperPrint(); 
//				Method method = HCRPrintCacheManager.class.getDeclaredMethod("saveAllCacheInDisk", String.class, String.class, String.class, JasperPrint.class, List.class);
//				method.setAccessible(true);
//				method.invoke(cacheManager, "directory", "cacheUUID", "cacheDirectory", object1, pages);
//				}
//			}
//		}
//
//	}
//	
//	
//	@Test
//	public void ut_a8_test_serveCachedContent() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JRPrintPage jrPrintPage = mock(JRPrintPage.class);
//		
//		JasperPrint originalJasperPrint = new JasperPrint();
//		originalJasperPrint.addPage(jrPrintPage);
//		originalJasperPrint.addPage(jrPrintPage);
//		
//		
//		JsonObject formData = new JsonObject();
//		formData.addProperty("noOfRecords", 1);
//		formData.addProperty("isExport", true);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//					mockedStatic3.when(()-> CacheUtils.getCacheExtension()).thenReturn("cache");
//			
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				boolean serveCachedContent = cacheManager.serveCachedContent(null, null,originalJasperPrint );
//				assertTrue(serveCachedContent);
//				}
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_a9_test_serveCachedContent() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JRPrintPage jrPrintPage = mock(JRPrintPage.class);
//		
//		JasperPrint originalJasperPrint = new JasperPrint();
//		originalJasperPrint.addPage(jrPrintPage);
//		originalJasperPrint.addPage(jrPrintPage);
//		
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", true);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//					mockedStatic3.when(()-> CacheUtils.getCacheExtension()).thenReturn("cache");
//			
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				boolean serveCachedContent = cacheManager.serveCachedContent(null, null,originalJasperPrint );
//				assertTrue(serveCachedContent);
//				}
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_b1_test_serveCachedContent() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JRPrintPage jrPrintPage = mock(JRPrintPage.class);
//		
//		JasperPrint originalJasperPrint = new JasperPrint();
//		originalJasperPrint.addPage(jrPrintPage);
//		originalJasperPrint.addPage(jrPrintPage);
//		
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//					mockedStatic3.when(()-> CacheUtils.getCacheExtension()).thenReturn("cache");
//			
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				boolean serveCachedContent = cacheManager.serveCachedContent(null, null,originalJasperPrint );
//				assertTrue(serveCachedContent);
//				}
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_b2_test_serveCachedContent() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JasperPrint originalJasperPrint = new JasperPrint();
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<CacheUtils> mockedStatic3 = mockStatic(CacheUtils.class)){
//					mockedStatic3.when(()-> CacheUtils.getCacheExtension()).thenReturn("cache");
//			
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				boolean serveCachedContent = cacheManager.serveCachedContent(null, null,originalJasperPrint );
//				assertFalse(serveCachedContent);
//				}
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_b3_test_getDataFromDatabase() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JasperPrint originalJasperPrint = new JasperPrint();
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		when(generator.generateHCRPrint(any(JsonObject.class))).thenReturn(originalJasperPrint);
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				JasperPrint dataFromDatabase = cacheManager.getDataFromDatabase("query");
//				assertEquals(originalJasperPrint,dataFromDatabase);
//				
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_b4_test_getQuery() throws IOException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JasperPrint originalJasperPrint = new JasperPrint();
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		when(generator.generateHCRPrint(any(JsonObject.class))).thenReturn(originalJasperPrint);
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				String query = cacheManager.getQuery("query");
//				assertEquals(query, "JUST_TO_IGNORE");
//				
//			}
//		}
//
//	}
//
//	
//	@Test
//	public void ut_b5_test_getQuery() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JasperPrint originalJasperPrint = new JasperPrint();
//		
//		EnhancedQueryExecutor queryExecutor = mock(EnhancedQueryExecutor.class);
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		connectionDetails.addProperty("key", "value");
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		when(queryExecutor.getQuery()).thenReturn("query");
//		when(generator.generateHCRPrint(any(JsonObject.class))).thenReturn(originalJasperPrint);
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				
//				Field field = HCRPrintCacheManager.class.getDeclaredField("queryExecutor");
//				field.setAccessible(true);
//				field.set(cacheManager, queryExecutor);
//				
//				cacheManager.setRequestData(formData.toString());
//				String query = cacheManager.getQuery("query");
//				assertEquals(query, "query");
//				
//			}
//		}
//
//	}
//	
//	@Test
//	public void ut_b6_test_getConnectionType() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JsonObject efwdFileAsJson = null;
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				
//				Field field = HCRPrintCacheManager.class.getDeclaredField("efwdFileAsJson");
//				field.setAccessible(true);
//				field.set(cacheManager, efwdFileAsJson);
//				
//				String type = cacheManager.getConnectionType(1l);
//				assertEquals(type, "JUST_TO_IGNORE");
//				
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_b7_test_getConnectionType() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JsonObject efwdFileAsJson = new JsonObject();
//		JsonArray dataSources = new JsonArray();
//		JsonObject connection = new JsonObject();
//		connection.addProperty("id", 12);
//		JsonObject connection2 = new JsonObject();
//		connection2.addProperty("id", 1);
//		connection2.addProperty("type", "connType");
//		dataSources.add(connection);
//		dataSources.add(connection2);
//		efwdFileAsJson.add("DataSources", dataSources);
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				
//				Field field = HCRPrintCacheManager.class.getDeclaredField("efwdFileAsJson");
//				field.setAccessible(true);
//				field.set(cacheManager, efwdFileAsJson);
//				
//				String type = cacheManager.getConnectionType(1l);
//				assertEquals(type, "connType");
//				
//			}
//		}
//
//	}
//
//
//	@Test
//	public void ut_b8_test_getMapId() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		Integer mapId = null;
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				
//				Field field = HCRPrintCacheManager.class.getDeclaredField("mapId");
//				field.setAccessible(true);
//				field.set(cacheManager, mapId);
//				
//				Integer id = cacheManager.getMapId();
//				Integer mId = -401;
//				assertEquals(id, mId);
//				
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_b9_test_getMapId() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		Integer mapId = 123;
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				
//				Field field = HCRPrintCacheManager.class.getDeclaredField("mapId");
//				field.setAccessible(true);
//				field.set(cacheManager, mapId);
//				
//				Integer id = cacheManager.getMapId();
//				Integer mId = 123;
//				assertEquals(id, mId);
//				
//			}
//		}
//
//	}
//
//	
//	@Test
//	public void ut_c1_test_getConnectionId() {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		connectionDetails.addProperty("key", "value");
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				Long id = cacheManager.getConnectionId();
//				Long mId = -404l;
//				assertEquals(id, mId);
//				
//			}
//		}
//
//	}
//
//	
//	@Test
//	public void ut_c2_test_getConnectionId() {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		connectionDetails.addProperty("dir", "dir");
//		connectionDetails.addProperty("map_id", "111");
//		JsonObject efwd = new JsonObject();
//		
//		connectionDetails.add("efwd", null);
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		JsonObject efwdFileAsJson = new JsonObject();
//		JsonArray DataMaps = new JsonArray();
//		JsonObject dataMapTag = new JsonObject();
//		dataMapTag.addProperty("id", 12);
//		JsonObject dataMapTag2 = new JsonObject();
//		dataMapTag2.addProperty("id", 111);
//		dataMapTag2.addProperty("connection", 123);
//		DataMaps.add(dataMapTag);
//		DataMaps.add(dataMapTag2);
//		efwdFileAsJson.add("DataMaps", DataMaps);
//		efwdFileAsJson.addProperty("_efwdFileName_", "_efwdFileName_");
//		
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,(mock,context)->{
//					when(mock.newReadEFWD(any(), any(), any())).thenReturn(efwdFileAsJson);
//				})){
//					try(MockedStatic<DataSourceSecurityUtility> mockedStatic3 = mockStatic(DataSourceSecurityUtility.class)){
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				Long id = cacheManager.getConnectionId();
//				Long mId = 123l;
//				assertEquals(id, mId);
//					}
//				}
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_c3_test_getConnectionId() {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		connectionDetails.addProperty("dir", "dir");
//		connectionDetails.addProperty("map_id", "111");
//		
//		JsonObject efwdFileAsJson = new JsonObject();
//		JsonArray dataMapsJSONArray = new JsonArray();
//		
//		JsonObject dataMap = new JsonObject();
//		
//		JsonObject dataMapTag = new JsonObject();
//		dataMapTag.addProperty("id", 12);
//		JsonObject dataMapTag2 = new JsonObject();
//		dataMapTag2.addProperty("id", 111);
//		dataMapTag2.addProperty("connection", 123);
//		
//		dataMap.add("dataMap", dataMapTag);
//		dataMap.add("dataMap", dataMapTag2);
//		
//		dataMapsJSONArray.add(dataMap);
//		
//		efwdFileAsJson.add("dataMaps", dataMapsJSONArray);
//		connectionDetails.add("efwd", efwdFileAsJson);
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,(mock,context)->{
//					when(mock.newReadEFWD(any(), any(), any())).thenReturn(efwdFileAsJson);
//				})){
//					try(MockedStatic<DataSourceSecurityUtility> mockedStatic3 = mockStatic(DataSourceSecurityUtility.class)){
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				Long id = cacheManager.getConnectionId();
//				Long mId = 123l;
//				assertEquals(id, mId);
//					}
//				}
//			}
//		}
//
//	}
//	
//	
//	@Test
//	public void ut_c4_test_getConnectionId() {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		
//		
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<DataSourceSecurityUtility> mockedStatic3 = mockStatic(DataSourceSecurityUtility.class)){
//				
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				Long id = cacheManager.getConnectionId();
//				Long mId = -404l;
//				assertEquals(id, mId);
//					
//				}
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_c5_test_getConnectionFilePath() {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		JsonObject formData = new JsonObject();
//		
//		formData.addProperty("isExport", false);
//		JsonObject connectionDetails = new JsonObject();
//		
//		formData.add("connectionDetails", connectionDetails);
//		JsonObject designerProperties = new JsonObject();
//		formData.add("designerProperties", designerProperties);
//		JsonObject saveDetails = new JsonObject();
//		saveDetails.addProperty("dir", "dir");
//		saveDetails.addProperty("uuid", "uuid");	
//		formData.add("saveDetails", saveDetails);
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<DataSourceSecurityUtility> mockedStatic3 = mockStatic(DataSourceSecurityUtility.class)){
//					mockedStatic2.when(() -> JsonUtils.getHCRExtension()).thenReturn("hcr");
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				cacheManager.setRequestData(formData.toString());
//				
//				String connectionFilePath = cacheManager.getConnectionFilePath();
//				assertEquals("dir\\uuid.hcr",connectionFilePath);
//				
//				String directory = cacheManager.getDirectory();
//				assertEquals("dir", directory);
//					
//				}
//			}
//		}
//
//	}
//
//	@Test
//	public void ut_c6_test_setterAndGetter() {
//		IHCRGenerator generator = mock(IHCRGenerator.class);
//		
//		
//		try (MockedStatic<JsonUtils> mockedStatic2 = mockStatic(JsonUtils.class)) {
//			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//				try(MockedStatic<DataSourceSecurityUtility> mockedStatic3 = mockStatic(DataSourceSecurityUtility.class)){
//					
//				mockedStatic2.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("dummy");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
//				
//				HCRPrintCacheManager cacheManager = new HCRPrintCacheManager();
//				Integer records = 10;
//				cacheManager.setNoOfRecords(records);
//				Integer noOfRecords = cacheManager.getNoOfRecords();
//				assertEquals(records, noOfRecords);
//				Integer pages = 10;
//				cacheManager.setRequestedPage(pages);
//			    Integer requestedPage = cacheManager.getRequestedPage();
//				assertEquals(pages, requestedPage);
//				JasperPrint result = cacheManager.getResult();	
//				assertNull(result);
//				}
//			}
//		}
//
//	}
//
//}
