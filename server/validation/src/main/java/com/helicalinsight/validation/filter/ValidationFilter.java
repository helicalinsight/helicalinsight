package com.helicalinsight.validation.filter;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.FilterUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.validation.IValidation;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * ValidationFilter class validate the request(urls), formData, xml file
 * and provides validation mesages. which implements Filter .
 * {@code @Component("validationFilter")} creates bean with name "validationFilter"
 */
@Component("validationFilter")
public class ValidationFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(ValidationFilter.class);

	private  boolean allowValidation = false;
	private  String configurationsDirectory;
	private  JsonObject validationXmlJson;
	private  JsonArray serviceParameters;
	private  JsonArray urlExcludePatterns;
	


	public ValidationFilter() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// NOP
	}
	/**
	 * initialize()
	 * It initializes various configuration parameters and loads validation data from the specified file and xml file .
	 * {@code  @PostConstruct} it executes after the object has been initialized.
	 */
	@PostConstruct
	public void initialize() {
		configurationsDirectory = FilterUtils.getValidationConfigurationFilesDirectory();
		logger.info(configurationsDirectory + " is updated");
		IProcessor processor = ResourceProcessorFactory.getIProcessor();
		validationXmlJson = processor.getJsonObject(FilterUtils.getValidationFilePath(), false);
		serviceParameters = validationXmlJson.getAsJsonObject("mapping").getAsJsonArray("url").get(13).getAsJsonObject().getAsJsonArray("serviceParameters");
		urlExcludePatterns = FilterUtils.newGetExcludeUlrPattern();
		if (validationXmlJson != null) {
			if (validationXmlJson.has("validationEnabled")) {
				allowValidation = validationXmlJson.get("validationEnabled").getAsBoolean();
				logger.info("Validation is set to " + allowValidation);
			}
		}
	}
	/**
	 * doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	 * @param request				provides validation url 
	 * @param response				sets the content type to text/html
	 * @param chain					The filter chain takes the request and response objects as parameters,
     *                              its purpose is to process these objects as they flow through the chain of filters
	 * @throws IOException 		    If an I/O error occurs.
     * @throws ServletException     If a servlet exception occurs.
     */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String requestedUrl = null;
		try {
			if (allowValidation) {
				HttpServletRequest httpRequest = ((HttpServletRequest) request);
				HttpServletResponse httpResponse = ((HttpServletResponse) response);
				requestedUrl = httpRequest.getServletPath();
				logger.info("The validation url is " + requestedUrl);
				// adding html to requestedUrl if not contains html
				if (!requestedUrl.toLowerCase().endsWith(".html")) {
					requestedUrl = requestedUrl + ".html";
				}

				/******** EXCLUDE THE PATTERN FOR .js, .css,.jpeg,.png etc ********************/
				if (FilterUtils.newIsExcludePattern(urlExcludePatterns, requestedUrl)) {
					chain.doFilter(request, response);
					return;
				}

				/******** EXCLUDE THE PATTERN FOR .js, .css,.jpeg,.png etc ********************/

				/******** MAP THE PATTERN FOR URL PATTERN *************************************/
				JsonObject mapping = GsonUtility.optJsonObject(validationXmlJson, "mapping");
				String defaultValidationClass = mapping.get("class").getAsString();
				JsonArray urlPatterns = mapping.getAsJsonArray("url");
				for (int i = 0; i < urlPatterns.size(); i++) {
					JsonObject jsonObject = urlPatterns.get(i).getAsJsonObject();
					String urlPattern = jsonObject.get("pattern")!=null?jsonObject.get("pattern").getAsString():"";
					String validationClass = GsonUtility.optString(jsonObject, "class");
					if (requestedUrl.contains(urlPattern)) {
						boolean isAjax = ControllerUtils.isAjax(httpRequest);
						if (validationClass == null || validationClass.trim().length() == 0) {
							validationClass = defaultValidationClass;
						}
						IValidation iValidation = FactoryMethodWrapper.getTypedInstance(validationClass, IValidation.class);
						
						JsonObject requestConfig = jsonObject.deepCopy();
						requestConfig.addProperty("definitionFolder", configurationsDirectory);

						JsonObject httpRequestJson = JsonUtils.newHttpRequestToFormData(httpRequest);
						if (("/services.html".equalsIgnoreCase(urlPattern))) {
							httpRequestJson = JsonUtils.newHttpRequestWithServiceAndFormData(httpRequest);
							if(!requestConfig.has("serviceParameters")) {
								requestConfig.add("serviceParameters", serviceParameters.deepCopy());
							}
						}
						if (httpRequestJson != null) {
							JsonObject httpRequestJsonCopy = httpRequestJson.deepCopy();
							if (iValidation != null && iValidation.isValid(httpRequestJson, requestConfig)) {
								chain.doFilter(request, response);
								logger.info("Returning true from validation");
								return;
							} else {
								logger.error("Validation failed for  service : {}", httpRequestJsonCopy);
								JsonObject model = new JsonObject();
								model.addProperty("status", 0);

								JsonObject result = new JsonObject();
								result.addProperty("message",
										getValidationMessage(requestConfig.getAsJsonObject("message")));
								model.add("response", result);
								if (isAjax) {
									ControllerUtils.handleSuccess(httpResponse, true, model.toString());
								} else {
									request.setAttribute("message", result.get("message").getAsString());
									logger.info("returning the error page");
									request.getRequestDispatcher("WEB-INF/jsp/errorPages/errorPage.jsp")
											.forward(request, response);
								}

								return;
							}
						}
						break;
					}
				}
			}
		} catch (Exception exception) {
			if (exception instanceof JsonSyntaxException)
				logger.error("Error occurred for " + requestedUrl);
			else
				logger.error("Error occurred ", exception);
			if (exception instanceof EfwServiceException) {
				throw new EfwServiceException(exception.getMessage());
			}
		}
		chain.doFilter(request, response);
	}
	
	public void destroy() {
	}
	/**
	 * getValidationMessage(JsonObject jsonObject)
	 * @param jsonObject        validation details and validation messages
	 * @return validation messages in string format
	 */
	public String getValidationMessage(JsonObject jsonObject) {
		StringBuilder message = new StringBuilder();

		List<String> validationKeys = JsonUtils.getKeys(jsonObject);
		for (String jsonKey : validationKeys) {
			Object sampleObject = jsonObject.get(jsonKey);

			if (sampleObject instanceof JsonObject) {
				JsonObject sampleJson = (JsonObject) sampleObject;
				message.append("\n").append(getValidationMessage(sampleJson));
			} else if (sampleObject instanceof JsonArray) {
				JsonArray sampleArray = (JsonArray) sampleObject;
				for (int index = 0; index < sampleArray.size(); index++) {
					message.append(sampleArray.get(index).getAsString()).append("\n");
				}
			} else if (sampleObject instanceof JsonPrimitive) {  // sampleObject instanceof String changed to JsonPrimative
				message.append(sampleObject.toString()).append("\n");
			}
		}
		return message.toString();
	}
}