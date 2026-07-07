package com.helicalinsight.adhoc.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.datasource.managed.jaxb.EFWD;
import com.helicalinsight.efw.utility.JaxbContexts;

public class EFWDWriterUtilsTest {

	@Test
	public void ut_a1_test_createEfwd() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EFWDWriterUtils efwdWriterUtils = new EFWDWriterUtils("driver", "url", "user", "pass");
		File efwdFile = mock(File.class);
		Method method = EFWDWriterUtils.class.getDeclaredMethod("createEfwd", File.class);
		method.setAccessible(true);
		method.invoke(efwdWriterUtils, efwdFile);
	}
	
	@Test
	public void ut_a2_test_createEfwd() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JAXBException {
		EFWDWriterUtils efwdWriterUtils = new EFWDWriterUtils("driver", "url", "user", "pass");
		File efwdFile = mock(File.class);
		Method method = EFWDWriterUtils.class.getDeclaredMethod("createEfwd", File.class);
		method.setAccessible(true);
		JaxbContexts jaxbContexts = mock(JaxbContexts.class);
		JAXBContext jaxbContext = mock(JAXBContext.class);
		Unmarshaller unmarshaller = mock(Unmarshaller.class);
		EFWD efwd =mock(EFWD.class);
		
		when(jaxbContexts.getContextForClass(EFWD.class)).thenReturn(jaxbContext);
		when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
		when(unmarshaller.unmarshal(any(File.class))).thenReturn(efwd);
		
		try(MockedStatic<JaxbContexts> mockedStatic = mockStatic(JaxbContexts.class)){
			mockedStatic.when(()-> JaxbContexts.getJaxbContexts()).thenReturn(jaxbContexts);
			method.invoke(efwdWriterUtils, efwdFile);
		}
		
	}
}

