package com.helicalinsight.export.unit;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.mockito.Mockito;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.efw.utility.ResourceTypeIDMap;

/**
 * Resets shared static state after each test so unit tests do not pollute
 * integration tests running in the same JVM.
 */
public abstract class ExportUnitTestBase {

	@After
	public void resetSharedStaticState() throws Exception {
		resetResourceTypeIDMap();
		Mockito.framework().clearInlineMocks();
	}

	protected void setField(Object target, String fieldName, Object value) throws Exception {
		Field field = findField(target.getClass(), fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	protected void populateResourceType(long typeId, String extension, String name) throws Exception {
		ResourceType resourceType = new ResourceType();
		resourceType.setResourceTypeId(typeId);
		resourceType.setExtension(extension);
		resourceType.setName(name);
		putMapValue(ResourceTypeIDMap.class, "resourceTypeMap", typeId, resourceType);
		putMapValue(ResourceTypeIDMap.class, "resourceIdExtension", typeId, extension);
		putMapValue(ResourceTypeIDMap.class, "resourceIdNameMap", typeId, name);
		putMapValue(ResourceTypeIDMap.class, "resourceNameIdMap", name, typeId);
	}

	private static void resetResourceTypeIDMap() throws Exception {
		clearMapField(ResourceTypeIDMap.class, "resourceTypeMap");
		clearMapField(ResourceTypeIDMap.class, "resourceIdExtension");
		clearMapField(ResourceTypeIDMap.class, "resourceIdNameMap");
		clearMapField(ResourceTypeIDMap.class, "resourceNameIdMap");
	}

	@SuppressWarnings("unchecked")
	private static void clearMapField(Class<?> clazz, String fieldName) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		((Map<?, ?>) field.get(null)).clear();
	}

	@SuppressWarnings("unchecked")
	private static void putMapValue(Class<?> clazz, String fieldName, Object key, Object value) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		Map<Object, Object> map = (Map<Object, Object>) field.get(null);
		if (map.isEmpty() && !fieldName.equals("resourceNameIdMap")) {
			field.set(null, new HashMap<>());
			map = (Map<Object, Object>) field.get(null);
		}
		map.put(key, value);
	}

	private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
		Class<?> current = type;
		while (current != null) {
			try {
				return current.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ignored) {
				current = current.getSuperclass();
			}
		}
		throw new NoSuchFieldException(fieldName);
	}

}
