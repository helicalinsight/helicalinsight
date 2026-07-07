package com.helicalinsight.adhoc.jreport;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HelicalJasperReportRunnerTest {

	@Test
	public void ut_a1_test_cancel() {
		JsonObject formData = new JsonObject();
		JsonObject response = new JsonObject();
		HelicalJasperReportRunner helicalJasperReportRunner = new HelicalJasperReportRunner(formData, response, null);
		helicalJasperReportRunner.cancel();
	}
	
	@Test
	public void ut_a2_test_run() {
		JsonObject formData = new JsonObject();
		JsonObject response = new JsonObject();
		HelicalJasperReportRunner helicalJasperReportRunner = new HelicalJasperReportRunner(formData, response, null);
		IHCRGenerator generator = mock(IHCRGenerator.class);
		JsonObject responseFromJasper = new JsonObject();
		responseFromJasper.addProperty("key", "value");
		when(generator.generateHCReport(formData)).thenReturn(responseFromJasper);
		
		try(MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)){
			mockedStatic.when(()-> JsonUtils.getHCRGeneratorType()).thenReturn("sample");
			try(MockedStatic<ApplicationContextAccessor> mockedStatic1 = mockStatic(ApplicationContextAccessor.class)){
				mockedStatic1.when(()-> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
				helicalJasperReportRunner.run();
			}
		}
		
		
	}
	
}
