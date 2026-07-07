package com.helicalinsight.scheduling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.quartz.JobExecutionContext;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.SendMail;

import jakarta.mail.MessagingException;

public class HttpScheduleJobTest {

	@Test
	public void testSessoinStart_a1() throws IOException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		HttpURLConnection connection = mock(HttpURLConnection.class);
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("organization", "organization");

		JsonObject printOptions = new JsonObject();
		JsonObject parametersJSON = new JsonObject();
		String[] theTotalFormats = new String[] { "pdf" };
		String[] totalRecipients = new String[] { "123@gmail.com" };
		Map<String, String> propertiesMap = new HashMap<>();
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(
				ApplicationDefaultUserAndRoleNamesConfigurer.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		CookieStore cookieJar = mock(CookieStore.class);
		InputStream response = mock(InputStream.class);
		Scanner scanner = mock(Scanner.class);
		HttpCookie httpCookie = new HttpCookie("name", "value");
		List<HttpCookie> cookies = new ArrayList<>();
		cookies.add(httpCookie);
		when(instance.getDomain()).thenReturn("hi-ee/hi.html");
		when(namesConfigurer.getRolePhantomName()).thenReturn("phantom");
		try (MockedStatic<EmailUtility> emailUtility = mockStatic(EmailUtility.class)) {

			try (MockedStatic<ApplicationProperties> applicationProperties = mockStatic(ApplicationProperties.class)) {
				try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(
						ApplicationContextAccessor.class)) {
					try (MockedConstruction<SendMail> sendMail = mockConstruction(SendMail.class, (mock, context) -> {
						mock.sendMessage(anyString(), anyString(), any(String[].class), anyString(), anyString(),
								anyString(), anyString(), anyString(), anyString(), anyString(), any(String[].class));

					})) {
						try (MockedConstruction<URL> construction = mockConstruction(URL.class, (mock, context) -> {
							when(mock.openConnection()).thenReturn(connection);
							when(connection.getResponseCode()).thenReturn(200);
							when(connection.getInputStream()).thenReturn(response);
						})) {

							try (MockedConstruction<CookieManager> cookie = mockConstruction(CookieManager.class,
									(mock, context) -> {
										when(mock.getCookieStore()).thenReturn(cookieJar);
										when(cookieJar.getCookies()).thenReturn(cookies);

									})) {
								try (MockedConstruction<Scanner> scn = mockConstruction(Scanner.class,
										(mock, context) -> {

											when(mock.useDelimiter("\\A")).thenReturn(scanner);
											when(scanner.next()).thenReturn("responseBody");

										})) {

									applicationProperties.when(() -> ApplicationProperties.getInstance())
											.thenReturn(instance);
									mockedStatic
											.when(() -> ApplicationContextAccessor
													.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class))
											.thenReturn(namesConfigurer);

									httpScheduleJob.sessionStart(realNames, printOptions, "csvData", "encodedUrl",
											"reportName", "reportSourceType", theTotalFormats, parametersJSON,
											totalRecipients, "subject", propertiesMap, "body");
								}
							}
						}
					}
				}
			}

		}
	}

	@Test
	public void testSessoinStart_a4() throws IOException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		HttpURLConnection connection = mock(HttpURLConnection.class);
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("organization", "organization");

		JsonObject printOptions = new JsonObject();
		JsonObject parametersJSON = new JsonObject();
		String[] theTotalFormats = new String[] { "pdf" };
		String[] totalRecipients = new String[] { "123@gmail.com" };
		Map<String, String> propertiesMap = new HashMap<>();
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(
				ApplicationDefaultUserAndRoleNamesConfigurer.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		CookieStore cookieJar = mock(CookieStore.class);
		InputStream response = mock(InputStream.class);
		Scanner scanner = mock(Scanner.class);
		HttpCookie httpCookie = new HttpCookie("name", "value");
		List<HttpCookie> cookies = new ArrayList<>();
		cookies.add(httpCookie);
		when(instance.getDomain()).thenReturn("hi-ee/hi.html");
		when(namesConfigurer.getRolePhantomName()).thenReturn("phantom");
		try (MockedStatic<EmailUtility> emailUtility = mockStatic(EmailUtility.class)) {

			try (MockedStatic<ApplicationProperties> applicationProperties = mockStatic(ApplicationProperties.class)) {
				try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(
						ApplicationContextAccessor.class)) {
					try (MockedConstruction<SendMail> sendMail = mockConstruction(SendMail.class, (mock, context) -> {
						mock.sendMessage(anyString(), anyString(), any(String[].class), anyString(), anyString(),
								anyString(), anyString(), anyString(), anyString(), anyString(), any(String[].class));

					})) {
						try (MockedConstruction<URL> construction = mockConstruction(URL.class, (mock, context) -> {
							when(mock.openConnection()).thenReturn(connection);
							when(connection.getResponseCode()).thenReturn(200);
							when(connection.getInputStream()).thenReturn(response);
							doThrow(new IOException()).when(response).close();
						})) {

							try (MockedConstruction<CookieManager> cookie = mockConstruction(CookieManager.class,
									(mock, context) -> {
										when(mock.getCookieStore()).thenReturn(cookieJar);
										when(cookieJar.getCookies()).thenReturn(cookies);

									})) {
								try (MockedConstruction<Scanner> scn = mockConstruction(Scanner.class,
										(mock, context) -> {

											when(mock.useDelimiter("\\A")).thenReturn(scanner);
											when(scanner.next()).thenReturn("responseBody");

										})) {

									applicationProperties.when(() -> ApplicationProperties.getInstance())
											.thenReturn(instance);
									mockedStatic
											.when(() -> ApplicationContextAccessor
													.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class))
											.thenReturn(namesConfigurer);

									httpScheduleJob.sessionStart(realNames, printOptions, "csvData", "encodedUrl",
											"reportName", "reportSourceType", theTotalFormats, parametersJSON,
											totalRecipients, "subject", propertiesMap, "body");
								}
							}
						}
					}
				}
			}

		}
	}

	@Test
	public void testSessoinStart_a2() throws IOException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		HttpURLConnection connection = mock(HttpURLConnection.class);
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("organization", "organization");

		JsonObject printOptions = new JsonObject();
		JsonObject parametersJSON = new JsonObject();
		String[] theTotalFormats = new String[] { "pdf" };
		String[] totalRecipients = new String[] { "123@gmail.com" };
		Map<String, String> propertiesMap = new HashMap<>();
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(
				ApplicationDefaultUserAndRoleNamesConfigurer.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		CookieStore cookieJar = mock(CookieStore.class);
		InputStream response = mock(InputStream.class);
		Scanner scanner = mock(Scanner.class);
		HttpCookie httpCookie = new HttpCookie("name", "value");
		List<HttpCookie> cookies = new ArrayList<>();
		cookies.add(httpCookie);
		when(instance.getDomain()).thenReturn("hi-ee/hi.html");
		when(namesConfigurer.getRolePhantomName()).thenReturn("phantom");

		try (MockedStatic<ApplicationProperties> applicationProperties = mockStatic(ApplicationProperties.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {

				try (MockedConstruction<URL> construction = mockConstruction(URL.class, (mock, context) -> {
					when(mock.openConnection()).thenReturn(connection);
					when(connection.getResponseCode()).thenReturn(300);

				})) {

					try (MockedConstruction<CookieManager> cookie = mockConstruction(CookieManager.class,
							(mock, context) -> {
								when(mock.getCookieStore()).thenReturn(cookieJar);
								when(cookieJar.getCookies()).thenReturn(cookies);

							})) {

						applicationProperties.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
						mockedStatic
								.when(() -> ApplicationContextAccessor
										.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class))
								.thenReturn(namesConfigurer);

						httpScheduleJob.sessionStart(realNames, printOptions, "csvData", "encodedUrl", "reportName",
								"reportSourceType", theTotalFormats, parametersJSON, totalRecipients, "subject",
								propertiesMap, "body");

					}
				}
			}

		}
	}

	@Test
	public void testSessoinStart_a3() throws IOException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");

		JsonObject printOptions = new JsonObject();
		JsonObject parametersJSON = new JsonObject();
		String[] theTotalFormats = new String[] { "pdf" };
		String[] totalRecipients = new String[] { "123@gmail.com" };
		Map<String, String> propertiesMap = new HashMap<>();
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(
				ApplicationDefaultUserAndRoleNamesConfigurer.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		when(instance.getDomain()).thenReturn("hi-ee/hi.html");
		when(namesConfigurer.getRolePhantomName()).thenReturn("phantom");

		try (MockedStatic<ApplicationProperties> applicationProperties = mockStatic(ApplicationProperties.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {

				applicationProperties.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
				mockedStatic.when(
						() -> ApplicationContextAccessor.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class))
						.thenReturn(namesConfigurer);

				httpScheduleJob.sessionStart(realNames, printOptions, "csvData", "encodedUrl", "reportName",
						"reportSourceType", theTotalFormats, parametersJSON, totalRecipients, "subject", propertiesMap,
						"body");

			}

		}
	}

	@Test
	public void testEmailSettings_a1() throws MessagingException, IOException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		JsonObject emailSettings = new JsonObject();

		Map<String, String> propertiesMap = new HashMap<>();

		httpScheduleJob.getBodyLine(emailSettings, propertiesMap);

	}

	@Test
	public void testEmailSettings_a2() throws MessagingException, IOException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		JsonObject emailSettings = new JsonObject();
		emailSettings.addProperty("Body", "body");
		Map<String, String> propertiesMap = new HashMap<>();

		httpScheduleJob.getBodyLine(emailSettings, propertiesMap);

	}

	@Test
	public void testGetSubjectLine() {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		JsonObject json = new JsonObject();
		json.addProperty("JobName", "JobName");
		JsonObject emailSettings = new JsonObject();
		emailSettings.addProperty("Subject", "Subject");
		httpScheduleJob.getSubjectLine(json, emailSettings);
	}

	@Test
	public void testGetReportUrl_a1() {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("password", "password");
		realNames.put("organization", "organization");
		httpScheduleJob.getReportUrl("reportEfwFile", "reportDirectory", "baseUrl", "parameters", realNames);
	}

	@Test
	public void testGetReportUrl_a2() {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("password", "password");
		realNames.put("organization", null);
		httpScheduleJob.getReportUrl("reportEfwFile", "reportDirectory", "baseUrl", "parameters", realNames);
	}

	@Test
	public void testUpdateXmlFile() {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		JobExecutionContext context = mock(JobExecutionContext.class);
		XmlOperation xmlOperation = mock(XmlOperation.class);
		JsonObject object = new JsonObject();
		object.addProperty("NoOfExecutions", "1");
		JsonObject json = new JsonObject();
		JsonObject ScheduleOptions = new JsonObject();
		ScheduleOptions.addProperty("endsRadio", "After");
		when(context.getFireTime()).thenReturn(new Date());
		when(context.getNextFireTime()).thenReturn(new Date());
		when(xmlOperation.getParticularObject(anyString(), anyString())).thenReturn(object);
		json.add("ScheduleOptions", ScheduleOptions);
		httpScheduleJob.updateXmlFile(context, json, 10, "path", xmlOperation);
	}

	@Test
	public void testGetCookiesArray_a1() throws IOException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		URLConnection conn = mock(URLConnection.class);
		when(conn.getContent()).thenThrow(new IOException());
		httpScheduleJob.getCookiesArray(conn);
	}

	@Test
	public void testGetCookiesArray_a2() {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		URLConnection conn = mock(URLConnection.class);
		CookieStore cookieJar = mock(CookieStore.class);
		HttpCookie httpCookie = new HttpCookie("name", "value");
		List<HttpCookie> cookies = new ArrayList<>();
		cookies.add(httpCookie);

		try (MockedConstruction<CookieManager> cookie = mockConstruction(CookieManager.class, (mock, context) -> {
			when(mock.getCookieStore()).thenReturn(cookieJar);
			when(cookieJar.getCookies()).thenReturn(cookies);

		})) {
			httpScheduleJob.getCookiesArray(conn);
		}
	}

	@Test
	public void testOptJsonObject_a1() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		Method method = HttpScheduleJob.class.getDeclaredMethod("optJsonObject", JsonObject.class, String.class);
		method.setAccessible(true);
		JsonObject object = new JsonObject();
		JsonObject obj = new JsonObject();
		object.add("key", obj);
		method.invoke(httpScheduleJob, object, "key");
	}

	@Test
	public void testOptJsonObject_a2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		HttpScheduleJob httpScheduleJob = new HttpScheduleJob();
		Method method = HttpScheduleJob.class.getDeclaredMethod("optJsonObject", JsonObject.class, String.class);
		method.setAccessible(true);
		JsonObject object = new JsonObject();
		method.invoke(httpScheduleJob, object, "key");
	}
}
