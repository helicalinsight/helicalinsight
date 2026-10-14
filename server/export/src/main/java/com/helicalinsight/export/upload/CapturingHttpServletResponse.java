package com.helicalinsight.export.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Captures body written by import handlers (ControllerUtils.getWriter).
 */
final class CapturingHttpServletResponse extends HttpServletResponseWrapper {

	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private PrintWriter writer;
	private String contentType;
	private String characterEncoding = StandardCharsets.UTF_8.name();
	private int status = SC_OK;

	CapturingHttpServletResponse() {
		super(noopResponse());
	}

	private static HttpServletResponse noopResponse() {
		return (HttpServletResponse) Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(),
				new Class<?>[] { HttpServletResponse.class }, (proxy, method, args) -> {
					Class<?> rt = method.getReturnType();
					if (rt == void.class) {
						return null;
					}
					if (rt == boolean.class) {
						return false;
					}
					if (rt == int.class) {
						return 0;
					}
					if (rt == long.class) {
						return 0L;
					}
					if (Collection.class.isAssignableFrom(rt)) {
						return Collections.emptyList();
					}
					return null;
				});
	}

	@Override
	public void setStatus(int sc) {
		this.status = sc;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setContentType(String type) {
		this.contentType = type;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		this.characterEncoding = charset;
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public PrintWriter getWriter() {
		if (writer == null) {
			writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8), true);
		}
		return writer;
	}

	@Override
	public ServletOutputStream getOutputStream() {
		return new ServletOutputStream() {
			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
			}

			@Override
			public void write(int b) {
				buffer.write(b);
			}
		};
	}

	@Override
	public void flushBuffer() {
		if (writer != null) {
			writer.flush();
		}
	}

	@Override
	public void addCookie(Cookie cookie) {
	}

	@Override
	public void setHeader(String name, String value) {
	}

	@Override
	public void addHeader(String name, String value) {
	}

	@Override
	public Locale getLocale() {
		return Locale.getDefault();
	}

	String getBody() {
		flushBuffer();
		return buffer.toString(StandardCharsets.UTF_8);
	}
}
