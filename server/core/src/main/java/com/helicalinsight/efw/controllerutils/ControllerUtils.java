package com.helicalinsight.efw.controllerutils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ServerSideExportComponent;
import com.helicalinsight.parallelprocessor.cache.ApplicationCacheManager;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by author on 01-Jan-15.
 *
 * @author Rajasekhar
 */
@Component
public class ControllerUtils {

	private static final Logger logger = LoggerFactory.getLogger(ControllerUtils.class);
	private final static Map<String, String> propertyMap = ConfigurationFileReader
			.mapFromClasspathPropertiesFile("reports.properties");

	public static Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	public static void addUrlParameters(HttpServletRequest request, ModelAndView modelAndView) {
		JsonObject urlParameters = new JsonObject();
		GsonUtility.accumulateAll(urlParameters, parameters(request));
		modelAndView.addObject("urlParameters", urlParameters);
	}

	public static JsonObject addAndGetUrlParameters(HttpServletRequest request, ModelAndView modelAndView) {
		JsonObject urlParameters = new JsonObject();
		GsonUtility.accumulateAll(urlParameters, parameters(request));
		modelAndView.addObject("urlParameters", urlParameters);
		return urlParameters;
	}

	/**
	 * newGetCookieArray using gson
	 * 
	 * @param HttpServletRequest request
	 * @return JsonArray
	 */
	public static JsonArray newGetCookieArray(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		JsonArray cookiesArray = new JsonArray();
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(cookie.getName(), cookie.getValue());
			cookiesArray.add(jsonObject);
		}
		return cookiesArray;
	}

	public static boolean isAjax(@NotNull HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	/**
	 * Checks whether the parameters are null or empty strings. If an empty or null
	 * parameter is found then RequiredParameterIsNullException is thrown with the
	 * the message including the faulty parameter.
	 *
	 * @param parameters A map of request parameters and their values
	 */
	public static void checkForNullsAndEmptyParameters(Map<String, String> parameters) {
		if (parameters == null) {
			throw new IllegalArgumentException("The map is null");
		}
		Set<Map.Entry<String, String>> entries = parameters.entrySet();

		for (Map.Entry<String, String> entry : entries) {
			String value = entry.getValue();
			if ((value == null) || ("".equals(value)) || (value.trim().length() < 0)) {
				throw new RequiredParameterIsNullException(
						String.format("The parameter %s is null or empty. " + "Invalid request.", entry.getKey()));
			}
		}
	}

//	public static List<String> getMessageChain(Exception ex) {
//		List<String> messageArray = new ArrayList<>();
//		Throwable throwable = ex.getCause();
//		while (throwable != null) {
//			String message = throwable.getMessage();
//			messageArray.add(message);
//			throwable = throwable.getCause();
//		}
//		return messageArray;
//	}
	
	public static List<String> getMessageChain(Throwable ex) {
	    List<String> messageChain = new ArrayList<>();
	    Throwable current = ex;

	    while (current != null) {
	        String msg = current.getMessage();
	        messageChain.add(msg != null ? msg : current.getClass().getSimpleName());
	        current = current.getCause();
	    }

	    return messageChain;
	}


	public static void handleFailure(@NotNull HttpServletResponse response, boolean isAjax, Exception exception)
			throws IOException {
		if (isAjax) {
			String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
			if (StringUtils.isBlank(rootCauseMessage) || rootCauseMessage.equalsIgnoreCase("NullPointerException: ")) {
				rootCauseMessage = "Unknown error occurred , Please check logs for more information.";
			}
			JsonObject theResponse = statusZeroJson(rootCauseMessage);
			logger.error("There was a problem in serving the request. The cause is " + rootCauseMessage, exception);
			handleAjaxRuntimeException(response, theResponse.toString());
		} else {
			// Now ErrorInterceptorFilter and Tomcat deal with the request
			throw new RuntimeException("Unable to process the request. Something went terribly " + "wrong.", exception);
		}
	}

	public static void handleFailureMessage(@NotNull HttpServletResponse response, boolean isAjax, Exception exception)
			throws IOException {
		if (isAjax) {
			String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
			JsonObject theResponse = statusZeroJson(rootCauseMessage);
			logger.error("There was a problem in serving the request. The cause is " + rootCauseMessage, exception);
			handleAjaxRuntimeException(response, theResponse.toString());
		} else {
			// Now ErrorInterceptorFilter and Tomcat deal with the request
			throw new RuntimeException("Unable to process the request. Something went terribly " + "wrong.", exception);
		}
	}

	public static JsonObject statusZeroJson(String rootCauseMessage) {
		JsonObject theResponse;
		theResponse = new JsonObject();

		GsonUtility.accumulateInt(theResponse, "status", 0);

		JsonObject messageJson = new JsonObject();
		GsonUtility.accumulate(messageJson, "message", rootCauseMessage);
		GsonUtility.accumulate(theResponse, "response", messageJson);
		return theResponse;
	}

	private static void handleAjaxRuntimeException(@NotNull HttpServletResponse response, @NotNull String message)
			throws IOException {
		// Commented as 500 status code is not required
		// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		setContentType(response);
		response.getWriter().write(message);
		response.flushBuffer();
	}

	public static void handleFailure(@NotNull HttpServletResponse response, String message) throws IOException {
		ObjectNode node = JacksonUtility.emptyNode();
		node.put("status", 0);
		node.put("message", message);
		handleAjaxRuntimeException(response, node.toString());
	}

	public static void setContentType(HttpServletResponse response) {
		response.setContentType(defaultContentType());
		response.setCharacterEncoding(ApplicationUtilities.getEncoding());
	}

	public static void handleSuccess(@NotNull HttpServletResponse response, boolean isAjax, String executionResult)
			throws IOException {
		if (isAjax) {
			handleAjaxSuccess(response, executionResult);
		} else {
			handleNormalRequestSuccess(response, executionResult);
		}
	}

	private static void handleAjaxSuccess(@NotNull HttpServletResponse response, String json) throws IOException {
		setContentType(response);
		write(response, json);
	}

	private static void handleNormalRequestSuccess(@NotNull HttpServletResponse response, String json)
			throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding(ApplicationUtilities.getEncoding());
		write(response, json);
	}

	private static void write(@NotNull HttpServletResponse response, String json) throws IOException {
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}

	/**
	 * Saves the file being downloaded in a specific location as efwresult file
	 *
	 * @param request        Web request
	 * @param resultNameTag  Value of the resultNameTag
	 * @param fileToDownload The file being downloaded and is being saved.
	 */
	// Extracted from two controller methods to modularize on 21-07-2015. Earlier
	// code duplication
	// is reduced.
	public static void saveFile(HttpServletRequest request, String resultNameTag, File fileToDownload) {
		String enableSaveResult = ApplicationProperties.getInstance().getEnableSavedResult();
		if ("TRUE".equalsIgnoreCase(enableSaveResult)) {
			String fileReportPath = request.getParameter("filename");
			String requestParameter = request.getParameter("reportNameParam");
			String reportNameParam = (requestParameter == null) ? fileReportPath : requestParameter;
			String dirReportPath = request.getParameter("dir");
			String reportType = request.getParameter("reportType");
			String resultDirectory = request.getParameter("resultDirectory");

			ServerSideExportComponent serverSideExportComponent = new ServerSideExportComponent(reportNameParam,
					reportType, resultNameTag, resultDirectory, fileToDownload, dirReportPath, fileReportPath);

			boolean validate = serverSideExportComponent.validateRequestParameters();

			if (validate) {
				serverSideExportComponent.copyReportFromTemp();
				serverSideExportComponent.saveEfwResultFile();
			}
		} else {
			logger.info("The JSONObject corresponding to setting.xml does not have "
					+ "enableSavedResult tag. Please set it enableSavedResult to true to save " + "downloaded report.");
		}
	}

	@SuppressWarnings("unused")
	public static JsonArray loadResource(String location, Boolean recursive) {
		BaseLoader baseLoader = new BaseLoader(ApplicationProperties.getInstance());
		String result;
		JsonArray resultArray = new JsonArray();
		try {
			result = baseLoader.loadResources(location, recursive);
			if (result != null) {
				return JsonParser.parseString(result).getAsJsonArray();
			}
		} catch (Exception ignore) {
			logger.error("Error occurred ", ignore);
		}
		return resultArray;
	}

	public static void addCookie(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String cookieName,
			String cookieValue) {
		String cookiePath = httpRequest.getContextPath();
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setPath(cookiePath);
		httpResponse.addCookie(cookie);
	}

	public static void accessDenied(HttpServletRequest request, HttpServletResponse response, String message) {
		JsonObject model = new JsonObject();
		model.addProperty("status", 0);
		JsonObject data = new JsonObject();
		data.addProperty("message", message);
		try {
			model.add("response", data);
			if (isAjax(request)) {
				handleSuccess(response, true, model.toString());
			} else {
//                request.getRequestDispatcher("WEB-INF/jsp/errorPages/accessdenied.jsp").forward(request, response);
				handleFailure(response, message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String defaultContentType() {
		return propertyMap.get("default-content-type");
	}

	public static String defaultCharSet() {
		return propertyMap.get("default-charset");
	}

	@SuppressWarnings("rawtypes")
	@NotNull
	public static Map<String, String> parameters(@NotNull HttpServletRequest request) {
		Enumeration enumeration = request.getParameterNames();
		JsonObject settingsJson = JsonUtils.newGetSettingsJson();
		String excludeUrlParameter = GsonUtility.optString(settingsJson, "excludeUrlParameter");
		List<String> excludeList = new ArrayList<>();
		if (StringUtils.isNotBlank(excludeUrlParameter)) {
			excludeList = Arrays.asList(excludeUrlParameter.split(","));
		}
		Map<String, String> parameters = new HashMap<>();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			if (!excludeList.contains(name)) {
				String[] value = request.getParameterValues(name);

				if (value.length > 1) {
					String multiSelection = "[";
					for (int count = 0; count < value.length; count++) {
						String separator = (count == 0 ? "" : ",");
						multiSelection = multiSelection.trim() + separator + "\"" + value[count] + "\"";
					}
					parameters.put(name, multiSelection + "]");
				} else {
					parameters.put(name, value[0]);
				}
			}
		}
		return parameters;
	}

	/**
	 * using gson validate(JsonObject formData)
	 * 
	 * @param formData
	 */
	public static void validate(JsonObject formData) {
		JsonObject formDataJson = new Gson().fromJson(formData, JsonObject.class);
		String jdbcUrl;
		String driverName;
		String name;
		try {
			jdbcUrl = formDataJson.get("jdbcUrl").getAsString();
			driverName = formDataJson.get("driverName").getAsString();
			name = formDataJson.get("name").getAsString();
		} catch (Exception e) {
			throw new IncompleteFormDataException("The form data json is incomplete and lacks "
					+ "required parameters. " + ExceptionUtils.getRootCauseMessage(e));
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("jdbcUrl", jdbcUrl);
		parameters.put("driverName", driverName);
		parameters.put("name", name);
		checkForNullsAndEmptyParameters(parameters);
	}

	/**
	 * using gson getDataFromResponse(JsonObject jsonServiceResult)
	 * 
	 * @param jsonServiceResult
	 * @return
	 */
	public static JsonObject getDataFromResponse(JsonObject jsonServiceResult) {
		if (jsonServiceResult != null) {
			JsonObject response = GsonUtility.optJsonObject(jsonServiceResult, "response");
			if (response != null && !response.entrySet().isEmpty()) {
				return response;
			}
		}
		return new JsonObject();
	}

	// created duplicate method for gson
	/**
	 * concatenateParameters using gson
	 * 
	 * @param JsonObject reportParameters
	 * @return String
	 */
	public static String concatenateParameters(JsonObject reportParameters) {
		reportParameters.remove("csvdata");
		List<String> listOfKeys = JsonUtils.listKeys(reportParameters );

		String parameter = "";

		for (String keyName : listOfKeys) {
			Object keyValue = reportParameters.get(keyName);

			if (reportParameters.get(keyName).isJsonArray()) {
				JsonArray array = reportParameters.get(keyName).getAsJsonArray();
				for (Object parameterValue : array) {
					parameter = parameter + keyName + "=" + replaceQuotesBenginingEnding(parameterValue) + "&";
				}
			} else {
				parameter = parameter + keyName + "=" + replaceQuotesBenginingEnding(keyValue) + "&";
			}
		}
		return parameter;
	}
	private static String replaceQuotesBenginingEnding(Object values){
		String value=values!=null? values.toString():"";
		if(value.startsWith("\"") && value.endsWith("\"")){
			value = value.substring(1, value.length()-1);
		}
		return value;
	}

	/**
	 * replaceFilePath using gson
	 * 
	 * @param resourceArray
	 */
	public static void replaceFilePath(JsonArray resourceArray) {

		for (JsonElement jsonObject : resourceArray) {
			JsonObject resourceJson = jsonObject.getAsJsonObject();
			if (resourceJson.has("children")) {
				JsonArray children = resourceJson.getAsJsonArray("children");
				if (!children.isEmpty()) {
					replaceFilePath(children);
				}
			} else {
				String type = resourceJson.get("type").getAsString();
				String path = resourceJson.get("path").getAsString();
				if ("file".equalsIgnoreCase(type)) {
					resourceJson.addProperty("path", path.replaceAll("\\\\", "/"));
					resourceJson.remove("absolutepath");

				}
			}

		}
	}

	public static String checkCache(String resource, Boolean recursive, boolean update)
			throws UnSupportedRuleImplementationException {
		ApplicationCacheManager cacheManager = ApplicationContextAccessor.getBean(ApplicationCacheManager.class);
		String searchResource;
		ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
		BaseLoader baseLoader = new BaseLoader(applicationProperties);

		if (recursive == null) {
			recursive = applicationProperties.isRecursiveResourceLoad();
		}
		if (resource == null) {
			resource = "";
			searchResource = "__solutionDirectory__";
		} else {
			searchResource = resource;
		}
		String resourceString;

		Object rawCache = cacheManager.getRawCache(searchResource);
		if (rawCache != null && !update) {
			resourceString = (String) rawCache;
		} else {
			resourceString = baseLoader.loadResources(resource, recursive);
			cacheManager.putRawCache(searchResource, resourceString);
		}
		return resourceString;
	}

	/**
	 * Returns the json of the tags for which visibility is true in the setting.xml
	 *
	 * @param jsonOfExtensions The Extensions tag of the setting.xml
	 * @return The json of the tags for which visibility is true
	 */

	/**
	 * getJSONOfVisibleExtensionTags(JsonObject jsonOfExtensions) using gson
	 * 
	 * @param jsonOfExtensions
	 * @return JsonObject
	 */
	public static JsonObject getJSONOfVisibleExtensionTags(JsonObject jsonOfExtensions) {
		Iterator<?> iterator = jsonOfExtensions.keySet().iterator();
		JsonObject visibleExtensionsJSON = new JsonObject();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			try {
				// Check whether the visible attribute is provided
				// or not. If not, control moves to the catch block as there
				// will be an exception
				if (jsonOfExtensions.get(key).isJsonObject()) {
					JsonObject jsonObject = jsonOfExtensions.getAsJsonObject(key);
					if (jsonObject != null) {
						try {
							if ("true".equalsIgnoreCase(jsonObject.get("visible").getAsString())) {
								if (logger.isDebugEnabled()) {
									logger.debug("The key " + key + " is set to be visible in the repository.");
								}
								visibleExtensionsJSON.add(key, jsonObject);
							}
						} catch (JsonSyntaxException ex) {
							if (logger.isDebugEnabled()) {
								logger.debug("The key " + key + " is set not to be visible in the " + "repository.");
							}
						}
					}
				}
			} catch (JsonSyntaxException ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("The key " + key + " is not a json object.");
				}
			}
		}
		return visibleExtensionsJSON;
	}

	public static JsonArray prepareLevel(JsonArray result, boolean isRoot) {

		if (isRoot) {
			for (int index = 0; index < result.size(); index++) {
				JsonObject eachJsonObject = result.get(index).getAsJsonObject();
				if (eachJsonObject.has("children")) {
					eachJsonObject.add("children", new JsonArray());

				}
			}
		} else {
			JsonObject jsonObject = result.get(0).getAsJsonObject();
			boolean children = jsonObject.has("children");
			if (children) {
				JsonArray children1 = jsonObject.getAsJsonArray("children");
				for (int index = 0; index < children1.size(); index++) {
					JsonObject eachJsonObject = children1.get(index).getAsJsonObject();
					if (eachJsonObject.has("children")) {
						eachJsonObject.add("children", new JsonArray());
					}
				}
			}

		}
		return result;
	}

}