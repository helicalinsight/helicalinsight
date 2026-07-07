package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.helicalinsight.externalauth.filter.DestroyRequest;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class DestroyRequestTest {

	@Test
	public void testRequestDestroyed_a1() {
		DestroyRequest destroyRequest = new DestroyRequest();
		ServletRequestEvent event = mock(ServletRequestEvent.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(event.getServletRequest()).thenReturn(request);
		destroyRequest.requestDestroyed(event);
	}
	
	@Test
	public void testRequestDestroyed_a2() {
		DestroyRequest destroyRequest = new DestroyRequest();
		ServletRequestEvent event = mock(ServletRequestEvent.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getParameter(anyString())).thenReturn("token");
		when(request.getHeader(anyString())).thenReturn("tokenHeader");
		when(request.getSession(false)).thenReturn(session);
		when(event.getServletRequest()).thenReturn(request);
		destroyRequest.requestDestroyed(event);
	}
	
	@Test
	public void testRequestDestroyed_a3() {
		DestroyRequest destroyRequest = new DestroyRequest();
		ServletRequestEvent event = mock(ServletRequestEvent.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getHeader(anyString())).thenReturn("tokenHeader");
		when(request.getSession(false)).thenReturn(null);
		when(event.getServletRequest()).thenReturn(request);
		destroyRequest.requestDestroyed(event);
	}
	@Test
	public void testRequestInitialized() {
		DestroyRequest destroyRequest = new DestroyRequest();
		ServletRequestEvent event = mock(ServletRequestEvent.class);
		destroyRequest.requestInitialized(event);
	}
}
