package com.helicalinsight.datasource.managed;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public final class ResultSetCacheTypeConversion {

	private static final String STRING_TARGET = "java.lang.String";

	private static volatile Map<String, String> writeMap = Collections.emptyMap();

	private ResultSetCacheTypeConversion() {
	}

	public static void configure(Map<String, String> nonSerializableToSerializable) {
		if (nonSerializableToSerializable == null || nonSerializableToSerializable.isEmpty()) {
			writeMap = Collections.emptyMap();
		} else {
			writeMap = Collections.unmodifiableMap(nonSerializableToSerializable);
		}
	}

	static Map<String, String> writeConversions() {
		return writeMap;
	}

	public static Object convertForWrite(Object value) {
		if (value == null) {
			return null;
		}
		Map<String, String> map = writeConversions();
		if (map.isEmpty()) {
			return value;
		}
		Class<?> clazz = value.getClass();
		while (clazz != null && clazz != Object.class) {
			String target = map.get(clazz.getName());
			if (target != null) {
				if (STRING_TARGET.equals(target)) {
					return value.toString();
				}
				return value;
			}
			clazz = clazz.getSuperclass();
		}
		return value;
	}

	public static ResultSet wrapForWrite(ResultSet rs) {
		return (ResultSet) Proxy.newProxyInstance(ResultSet.class.getClassLoader(), new Class<?>[] { ResultSet.class },
				(_, method, args) -> {
					try {
						if ("getObject".equals(method.getName()) && args != null && args.length >= 1) {
							Object raw;
							if (args[0] instanceof Integer) {
								raw = rs.getObject((Integer) args[0]);
							} else if (args[0] instanceof String) {
								raw = rs.getObject((String) args[0]);
							} else {
								return method.invoke(rs, args);
							}
							return convertForWrite(raw);
						}
						return method.invoke(rs, args);
					} catch (InvocationTargetException e) {
						Throwable cause = e.getCause();
						if (cause instanceof Exception) {
							throw (Exception) cause;
						}
						throw e;
					}
				});
	}

	public static ResultSet batchLimited(ResultSet rs, int batchSize) {
		AtomicInteger remaining = new AtomicInteger(batchSize);
		return (ResultSet) Proxy.newProxyInstance(ResultSet.class.getClassLoader(), new Class<?>[] { ResultSet.class },
				(_, method, args) -> {
					try {
						if ("next".equals(method.getName())) {
							if (remaining.get() <= 0) {
								return false;
							}
							boolean hasNext = rs.next();
							if (hasNext) {
								remaining.decrementAndGet();
							}
							return hasNext;
						}
						if ("close".equals(method.getName())) {
							return null;
						}
						return method.invoke(rs, args);
					} catch (InvocationTargetException e) {
						Throwable cause = e.getCause();
						if (cause instanceof Exception) {
							throw (Exception) cause;
						}
						throw e;
					}
				});
	}
}
