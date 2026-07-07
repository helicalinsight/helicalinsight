package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;

import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.filter.PreAuthenticationFilter;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.externalauth.rest.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RestControllerTest {

	//@Test
	public void testGetLoginAuthentication_a1() throws IOException {
		RestController controller = new RestController();
		ModelMap model = mock(ModelMap.class);	
		HttpServletRequest request   = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		when(request.getMethod()).thenReturn("get");
		controller.getLoginAuthentication("username", "password", model, request, response);
	}
	
	@Test
	public void testGetLoginAuthentication_a2() throws IOException {
		RestController controller = new RestController();
		ModelMap model = mock(ModelMap.class);	
		HttpServletRequest request   = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		SecurityContext context  = mock(SecurityContext.class);
		Authentication authentication = mock(Authentication.class);
		HttpSession session = mock(HttpSession.class);
		 
		when(request.getMethod()).thenReturn("set");
		when(request.getParameter("username")).thenReturn("username");
		when(request.getParameter("password")).thenReturn("password");
		when(request.getSession()).thenReturn(session);
		when(session.getId()).thenReturn("12");
		
		
		try(MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)){
			mockedStatic.when(() -> SecurityContextHolder.getContext()).thenReturn(context);
			when(context.getAuthentication()).thenReturn(authentication);
			controller.getLoginAuthentication("username", "password", model, request, response);
		}
		
	}
	@Test
	public void testFindUserByNameAndOrgNull() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		RestController controller = new RestController();
		UserDao userDao = mock(UserDao.class);
		User user = mock(User.class);
		when(userDao.findUserByNameNorgNull(anyString(),anyBoolean())).thenReturn(user);
		Field field = RestController.class.getDeclaredField("userDao");
		field.setAccessible(true);
		field.set(controller, userDao);
		
		controller.findUserByNameAndOrgNull("username");
	}
	@Test
	public void testRestLogout() {
		RestController controller = new RestController();
		HttpServletRequest request   = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		controller.restLogout(request, response);
	}
}
