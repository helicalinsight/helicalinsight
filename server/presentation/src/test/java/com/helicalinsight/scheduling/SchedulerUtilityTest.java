package com.helicalinsight.scheduling;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

public class SchedulerUtilityTest {

	
	@Test
	public void testGetInstance_a1() throws SchedulerException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SchedulerUtility schedulerUtility = new SchedulerUtility();
		SchedulerFactory schedulerFactory = mock(SchedulerFactory.class);
		Field field = SchedulerUtility.class.getDeclaredField("schedulerFactory");
		field.setAccessible(true);
		field.set(schedulerUtility, schedulerFactory);
		when(schedulerFactory.getScheduler()).thenThrow(new SchedulerException());
		SchedulerUtility.getInstance();
	}
	@Test
	public void testGetInstance_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SchedulerUtility schedulerUtility = new SchedulerUtility();
		SchedulerFactory schedulerFactory = mock(SchedulerFactory.class);
		Field field = SchedulerUtility.class.getDeclaredField("schedulerFactory");
		field.setAccessible(true);
		field.set(schedulerUtility, schedulerFactory);
		SchedulerUtility.getInstance();
	}
}
