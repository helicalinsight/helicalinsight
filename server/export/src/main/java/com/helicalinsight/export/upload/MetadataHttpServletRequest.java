package com.helicalinsight.export.upload;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Request facade for async import handoff — exposes upload metadata parameters
 * and stores request attributes (handlers set message / response on the request).
 */
final class MetadataHttpServletRequest {

	private MetadataHttpServletRequest() {
	}

	static HttpServletRequest from(Map<String, String> params) {
		Map<String, String> map = params != null ? new HashMap<>(params) : new HashMap<>();
		map.putIfAbsent("X-Requested-With", "XMLHttpRequest");
		Map<String, Object> attributes = new ConcurrentHashMap<>();
		return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
				new Class<?>[] { HttpServletRequest.class }, (proxy, method, args) -> {
					String name = method.getName();
					if ("getParameter".equals(name) && args != null && args.length == 1) {
						return map.get(args[0]);
					}
					if ("getParameterValues".equals(name) && args != null && args.length == 1) {
						String v = map.get(args[0]);
						return v == null ? null : new String[] { v };
					}
					if ("getParameterMap".equals(name)) {
						Map<String, String[]> out = new HashMap<>();
						for (Map.Entry<String, String> e : map.entrySet()) {
							out.put(e.getKey(), new String[] { e.getValue() });
						}
						return Collections.unmodifiableMap(out);
					}
					if ("getParameterNames".equals(name)) {
						Set<String> names = new HashSet<>(map.keySet());
						return Collections.enumeration(names);
					}
					if ("getHeader".equals(name) && args != null && args.length == 1) {
						if ("X-Requested-With".equalsIgnoreCase(String.valueOf(args[0]))) {
							return "XMLHttpRequest";
						}
						return null;
					}
					if ("getMethod".equals(name)) {
						return "POST";
					}
					if ("getRequestURI".equals(name) || "getServletPath".equals(name)) {
						return "/importFile";
					}
					if ("getAttribute".equals(name) && args != null && args.length == 1) {
						return attributes.get(String.valueOf(args[0]));
					}
					if ("setAttribute".equals(name) && args != null && args.length == 2) {
						String key = String.valueOf(args[0]);
						if (args[1] == null) {
							attributes.remove(key);
						} else {
							attributes.put(key, args[1]);
						}
						return null;
					}
					if ("removeAttribute".equals(name) && args != null && args.length == 1) {
						attributes.remove(String.valueOf(args[0]));
						return null;
					}
					if ("getAttributeNames".equals(name)) {
						return Collections.enumeration(attributes.keySet());
					}
					Class<?> rt = method.getReturnType();
					if (rt == boolean.class) {
						return false;
					}
					if (rt == int.class) {
						return 0;
					}
					if (rt == long.class) {
						return 0L;
					}
					if (Enumeration.class.isAssignableFrom(rt)) {
						return Collections.emptyEnumeration();
					}
					return null;
				});
	}
}
