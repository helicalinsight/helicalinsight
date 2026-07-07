package com.helicalinsight.efw.exceptions;

import org.junit.Assert;
import org.junit.Test;

public class EfwExceptionTest {
	@Test
	public void testImplementationNotFound() {
		ImplementationNotFound found = new ImplementationNotFound("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testDuplicateEfwdException() {
		DuplicateEfwdException found = new DuplicateEfwdException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testDatabaseConnectionFailedException() {
		DatabaseConnectionFailedException found = new DatabaseConnectionFailedException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testClassNotConfiguredException() {
		ClassNotConfiguredException found = new ClassNotConfiguredException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testTaskTimeoutException() {
		TaskTimeoutException found = new TaskTimeoutException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testDuplicateChartIdException() {
		DuplicateChartIdException found = new DuplicateChartIdException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	
	@Test
	public void testDataSourceConnectionNotFoundException() {
		DataSourceConnectionNotFoundException found = new DataSourceConnectionNotFoundException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testExportException() {
		ExportException found = new ExportException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testTaskInterruptedException() {
		TaskInterruptedException found = new TaskInterruptedException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testDuplicateDatasourceConnectionException() {
		DuplicateDatasourceConnectionException found = new DuplicateDatasourceConnectionException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	@Test
	public void testRequiredParametersNotProvidedException() {
		RequiredParametersNotProvidedException found = new RequiredParametersNotProvidedException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	
	@Test
	public void testApplicationException() {
		ApplicationException found = new ApplicationException("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
	}
	
	@Test
	public void testMalformedJsonException () {
		MalformedJsonException  found = new MalformedJsonException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		MalformedJsonException  found1 = new MalformedJsonException ("message",throwable);
	}
	@Test
	public void testXmlConfigurationException () {
		XmlConfigurationException  found = new XmlConfigurationException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		XmlConfigurationException  found1 = new XmlConfigurationException ("message",throwable);
	}
	@Test
	public void testEfwException () {
		EfwException  found = new EfwException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		EfwException  found1 = new EfwException ("message",throwable);
	}	
	@Test
	public void testJdbcConnectionException() {
		JdbcConnectionException  found = new JdbcConnectionException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		JdbcConnectionException  found1 = new JdbcConnectionException ("message",throwable);
		JdbcConnectionException  found2 = new JdbcConnectionException (throwable);
	}	
	@Test
	public void testIncompleteFormDataException() {
		IncompleteFormDataException  found = new IncompleteFormDataException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		IncompleteFormDataException  found1 = new IncompleteFormDataException ("message",throwable);
		IncompleteFormDataException  found2 = new IncompleteFormDataException (throwable);
	}
	@Test
	public void testHCRException() {
		HCRException  found = new HCRException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		Exception exception  = new Exception();
		HCRException  found1 = new HCRException("message",exception);
		HCRException  found2 = new HCRException (throwable);
	}
	@Test
	public void testUnSupportedRuleImplementationException() {
		UnSupportedRuleImplementationException  found = new UnSupportedRuleImplementationException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		Exception exception  = new Exception();
		UnSupportedRuleImplementationException  found1 = new UnSupportedRuleImplementationException("message",exception);
		UnSupportedRuleImplementationException  found2 = new UnSupportedRuleImplementationException (throwable);
	}	
	@Test
	public void testImproperXMLConfigurationException() {
		ImproperXMLConfigurationException  found = new ImproperXMLConfigurationException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		Exception exception  = new Exception();
		ImproperXMLConfigurationException  found1 = new ImproperXMLConfigurationException("message",exception);
		ImproperXMLConfigurationException  found2 = new ImproperXMLConfigurationException (throwable);
	}
	@Test
	public void testEfwServiceException() {
		EfwServiceException  found = new EfwServiceException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		Exception exception  = new Exception();
		EfwServiceException  found1 = new EfwServiceException("message",exception);
		EfwServiceException  found2 = new EfwServiceException	 (throwable);
	}	
	@Test
	public void testRequiredParameterIsNullException() {
		RequiredParameterIsNullException  found = new RequiredParameterIsNullException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		Exception exception  = new Exception();
		RequiredParameterIsNullException  found1 = new RequiredParameterIsNullException("message",exception);
		RequiredParameterIsNullException  found2 = new RequiredParameterIsNullException	 (throwable);
	}	
	
	@Test
	public void testRuntimeIOException() {
		RuntimeIOException  found = new RuntimeIOException ("message");
		String message = found.getMessage();
		Assert.assertEquals("message", message);
		Throwable throwable = new Throwable();
		
		RuntimeIOException  found1 = new RuntimeIOException("message",throwable);
	
	}	
	@Test
	public void testOperationFailedException() {
		
		Throwable throwable = new Throwable();
		
		OperationFailedException  found1 = new OperationFailedException(throwable);
	
	}	
	@Test
	public void testEfwdServiceException() {
		
		Exception exception  = new Exception();
		
		OperationFailedException  found1 = new OperationFailedException(exception);
	
	}	
	@Test
	public void testConfigurationException() {
		
		Throwable throwable = new Throwable();
		
		ConfigurationException  found1 = new ConfigurationException(throwable);
	
	}	
}
