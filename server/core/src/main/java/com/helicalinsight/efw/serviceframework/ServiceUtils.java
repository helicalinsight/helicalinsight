package com.helicalinsight.efw.serviceframework;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.service.ComponentFactory;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.stream.StreamSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;


/**
 * Created by author on 24-02-2015.
 *
 * @author Rajasekhar
 */
public class ServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

    //Requires classifier
    public static String executeService(String type, String serviceType, String service, String formData) {
        JsonObject formDataJson = JsonParser.parseString(formData).getAsJsonObject();

        String classifier;
        try {
            classifier = formDataJson.get("classifier").getAsString();
        } catch (Exception ex) {
            throw new RequiredParameterIsNullException("Required parameter classifier not found.");
        }

        String componentJson = componentJson(type, serviceType, service, classifier);

        String componentClass = componentClass(componentJson);
        return executeService(formDataJson, componentJson, componentClass);
    }

    /**
     * Returns the json of the component from the xml
     *
     * @param type        The feature type
     * @param serviceType The service type
     * @param service     The actual service
     * @param classifier  Differentiates components configured. Can be null
     * @return The json of the component
     */
    @Nullable
    public static String componentJson(String type, String serviceType, String service, String classifier) {
        ComponentsXmlReader componentsXmlReader = ApplicationContextAccessor.getBean(ComponentsXmlReader.class);
        String componentJson = componentsXmlReader.findComponentNode(type, serviceType, service, classifier);

        if (componentJson == null || "".equals(componentJson) || "".equals(componentJson.trim())) {
            throw new ConfigurationException("The component is not defined for the service " + service);
        }
        return componentJson;
    }

    public static String componentClass(String componentJson) {
        String componentClass;
        try {
            componentClass = JsonParser.parseString(componentJson).getAsJsonObject().get("@class").getAsString();
        } catch (Exception ex) {
            throw new EfwServiceException("The component doesn't exist. Check configuration.");
        }
        return componentClass;
    }

    //Does not require classifier
    public static String executeService(@NotNull JsonObject formDataJson, String componentJson,
                                        @Nullable String componentClass) {
        if (componentClass == null || "".equals(componentClass) || "".equals(componentClass.trim())) {
            throw new EfwServiceException("The component doesn't exist. Check configuration.");
        }

        JsonObject model;
        model = new JsonObject();
        try {
            IComponent iComponent = ComponentFactory.getComponentInstance(componentClass);
            if (iComponent != null) {
                String result = getComponentResult(formDataJson, componentJson, iComponent);

                emptyResult(model, result);
            } else {
                throw new ConfigurationException("Could not produce the component object. Check " + "the " +
                        "configuration files.");
            }
        } catch (Exception exception) {
            whenException(model, exception);
        }
        return model.toString();
    }
    
    
	public static void streamService(@NotNull JsonObject formDataJson, String componentJson,
			@Nullable String componentClass, StreamSession session) {
		if (componentClass == null || "".equals(componentClass) || "".equals(componentClass.trim())) {
			throw new EfwServiceException("The component doesn't exist. Check configuration.");
		}

		JsonObject model;
		model = new JsonObject();
		try {
			IComponent iComponent = ComponentFactory.getComponentInstance(componentClass);
			if (iComponent != null) {
				logger.debug("Found component class : {}", componentClass);
				streamComponentResult(formDataJson, componentJson, iComponent, session);

			} else {
				throw new ConfigurationException(
						"Could not produce the component object. Check " + "the " + "configuration files.");
			}
		} catch (Exception exception) {
			whenException(model, exception);
		}
	}

    private static void whenException(JsonObject model, Exception exception) {
        //exception.printStackTrace();
        model.addProperty("status", 0);
        logger.error("An exception has taken place. The stackTrace is ", exception);
        JsonObject response = new JsonObject();
        String message = prepareExceptionMessage(exception);
        response.addProperty("message", "Error: " + message);
        response.addProperty("className", exception.getClass().getSimpleName());
        model.add("response", response);
    }


    private static void emptyResult(JsonObject model, String result) {
        String emptySelection = "No Columns Selected";
        if (emptySelection.equals(result)) {
            model.addProperty("status", 0);
            JsonObject message = new JsonObject();
            message.addProperty("message", emptySelection);
            model.add("response", message);
        } else {
            model.addProperty("status", 1);
            JsonElement element = new Gson().fromJson(result, JsonElement.class);
            JsonObject jsonObject = element.getAsJsonObject();
            model.add("response", jsonObject);
        }
    }

    private static Object executeServiceLogic(@NotNull JsonObject formDataJson, String componentJson,
                                        @Nullable String componentClass) {
        if (componentClass == null || "".equals(componentClass) || "".equals(componentClass.trim())) {
            throw new EfwServiceException("The component doesn't exist. Check configuration.");
        }

        JsonObject model= new JsonObject();
        try {
            IComponent iComponent = ComponentFactory.getComponentInstance(componentClass);
            if (iComponent != null) {
                formDataJson.add("componentJson", JsonParser.parseString(componentJson).getAsJsonObject());
               Object componentResult= iComponent.componentLogic(formDataJson.toString());
               if(componentResult!=null){
                   if(componentResult instanceof JsonObject){
                       model.addProperty("status", 1);
                       JsonObject jsonObject  = (JsonObject)componentResult;
                       if(jsonObject.has("columnResult")){
                           emptyResult(model, jsonObject.get("columnResult").getAsString());
                       }
                       model.add("response", jsonObject);
                       return  model;
                   }
               }else {
                   String result = getComponentResult(formDataJson, componentJson, iComponent);

                   emptyResult(model, result);
               }
            } else {
                throw new ConfigurationException("Could not produce the component object. Check " + "the " +
                        "configuration files.");
            }
        } catch (Exception exception) {
             //exception.printStackTrace();
            whenException(model, exception);
        }
        return model.toString();
    }




    private static String getComponentResult(JsonObject formDataJson, String componentJson, IComponent iComponent) {
        GsonUtility.accumulate(formDataJson,"componentJson", componentJson);
        return iComponent.executeComponent(formDataJson.toString());
    }
    
    private static void streamComponentResult(JsonObject formDataJson, String componentJson, IComponent iComponent, StreamSession session) {
        GsonUtility.accumulate(formDataJson,"componentJson", componentJson);
         iComponent.streamResponse(formDataJson.toString(), session);
    }

    public static void stream(String type, String serviceType, String service, String formData, StreamSession session) {
        JsonObject formDataJson =	JsonParser.parseString(formData).getAsJsonObject();

        String componentJson = componentJson(type, serviceType, service);

        String componentClass = componentClass(componentJson);
        streamService(formDataJson, componentJson, componentClass, session);
    }

    public static String execute(String type, String serviceType, String service, String formData) {
        JsonObject formDataJson =	JsonParser.parseString(formData).getAsJsonObject();

        String componentJson = componentJson(type, serviceType, service);

        String componentClass = componentClass(componentJson);
        return executeService(formDataJson, componentJson, componentClass);
    }

    public static Object executeComponent(String type, String serviceType, String service, String formData) {

        JsonObject formDataId = JsonParser.parseString(formData).getAsJsonObject();
        JsonObject formDataJson = formDataId;
        if(formDataId.has("formDataId")){
            formDataJson= EfwServicesController.gsonStore.get(formDataId.get("formDataId").getAsString());;
        }

        String componentJson = componentJson(type, serviceType, service);

        String componentClass = componentClass(componentJson);
        return executeServiceLogic(formDataJson, componentJson, componentClass);
    }

    @Nullable
    public static String componentJson(String type, String serviceType, String service) {
        ComponentsXmlReader componentsXmlReader = ApplicationContextAccessor.getBean(ComponentsXmlReader.class);
        String componentJson = componentsXmlReader.findComponentNode(type, serviceType, service, null);

        if (componentJson == null || "".equals(componentJson) || "".equals(componentJson.trim())) {
            throw new ConfigurationException("The component is not defined for the service " + service);
        }
        return componentJson;
    }


    
    private static String prepareExceptionMessage(Exception exception) {
    	String message = ExceptionUtils.getRootCauseMessage(exception);
    	if( exception instanceof SQLException || exception instanceof HibernateException ) {
    		message = "Error occurred during query execution , Please check logs for more information.";
    	}
    	if ( exception instanceof JsonIOException) {
			if( message.indexOf("[") != -1 && message.indexOf("]") != -1) {
				message = message.substring(message.indexOf("[")+2,message.indexOf("]")-1);
				message = " Required parameter " +message+ " not found.";
			}
    	}
    	return message;
    }
}