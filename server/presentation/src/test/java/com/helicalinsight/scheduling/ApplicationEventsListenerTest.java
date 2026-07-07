package com.helicalinsight.scheduling;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import jakarta.servlet.ServletContextEvent;

public class ApplicationEventsListenerTest {

	@Test
	public void testContextDestroyed() {
		ServletContextEvent contextEvent = mock(ServletContextEvent.class);
		ApplicationEventsListener applicationEventsListener = new ApplicationEventsListener();
		applicationEventsListener.contextDestroyed(contextEvent);
	}
	
	@Test
	public void testContextDestroyed_Exception() throws SchedulerException {
		ApplicationEventsListener applicationEventsListener = new ApplicationEventsListener();
		ServletContextEvent contextEvent = mock(ServletContextEvent.class);
		Scheduler scheduler = spy(Scheduler.class);
		try(MockedStatic<SchedulerUtility> mockedStatic = mockStatic(SchedulerUtility.class)){
			mockedStatic.when(() -> SchedulerUtility.getInstance()).thenReturn(scheduler);
			doThrow(new SchedulerException()).when(scheduler).shutdown(anyBoolean());
			applicationEventsListener.contextDestroyed(contextEvent);
		}
		
	}
	@Test
	public void test() {
		ApplicationEventsListener applicationEventsListener = new ApplicationEventsListener();
		applicationEventsListener.sessionCreated(null);
		applicationEventsListener.sessionDestroyed(null);
		applicationEventsListener.attributeAdded(null);
		applicationEventsListener.attributeRemoved(null);
		applicationEventsListener.attributeReplaced(null);
		applicationEventsListener.contextInitialized(null);
	}
	
}
