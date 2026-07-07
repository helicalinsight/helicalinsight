package com.helicalinsight.export;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedConstruction;

import com.google.gson.JsonIOException;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.vf.ChartService;

public class CSVUtilityTest {

	@Test
	public void ut_a1_testGetCSVData_JsonException() throws ApplicationException {
		CSVUtility csvUtility = new CSVUtility();
		try(MockedConstruction<ChartService> mockChartService = mockConstruction(ChartService.class,(mock, context) -> {
			when(mock.getData()).thenThrow(new JsonIOException("Check Excption"));
		})){
			String csvData = csvUtility.getCSVData("data");
			Assert.assertNull(csvData);
		}
		
	}
	
	@Test
	public void ut_a2_testGetCSVData_ApplicationException() throws ApplicationException {
		CSVUtility csvUtility = new CSVUtility();
		try(MockedConstruction<ChartService> mockChartService = mockConstruction(ChartService.class,(mock, context) -> {
			when(mock.getData()).thenThrow(new ApplicationException("Check Excption"));
		})){
			String csvData = csvUtility.getCSVData("data");
			Assert.assertNull(csvData);
		}
		
	}
}
