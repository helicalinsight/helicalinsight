package com.helicalinsight.efw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ServiceContextBuilder;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.EfwdReaderUtility;
import com.helicalinsight.efw.components.GlobalDSReaderUtility;
import com.helicalinsight.efw.components.GlobalXmlReaderUtility;
import com.helicalinsight.efw.context.ServiceRequestContext;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.controllerutils.StatusValidator;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.DirectoryLoaderProxy;
import com.helicalinsight.efw.serviceframework.ServiceManager;
import com.helicalinsight.efw.utility.JdbcUrlFormatUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.SettingXmlUtility;
import com.helicalinsight.parallelprocessor.TaskExecutorService;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import com.helicalinsight.stream.IStreamRegistry;
import com.helicalinsight.stream.InMemoryStreamRegistry;
import com.helicalinsight.stream.StreamManager;
import com.helicalinsight.stream.StreamRegistry;
import com.helicalinsight.stream.StreamSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

/**
 * Created by author on 24-Jan-15.
 *
 * @author Rajasekhar
 * @author Muqtar
 * @author Somen
 */
@Controller
public class EfwServicesController {

    private static final Logger logger = LoggerFactory.getLogger(EfwServicesController.class);
    @Autowired
    private StatusValidator statusValidator;


    @Autowired
    private HIResourceServiceDB hiResourceServiceDB;
    
    @Autowired
    private TaskExecutorService taskExecutorService;

    @Autowired
    private ServiceContextBuilder serviceContextBuilder;
    
    @Autowired
    private ServiceManager serviceManager;
    
    @Autowired
    private StreamRegistry<SseEmitter> streamRegistry;
    
    
    public static final Map<String, JsonObject> gsonStore = new ConcurrentHashMap<String, JsonObject>(1000, 0.9f,
            1) {
        private static final long serialVersionUID = 1L;

        public boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
            return size() > 1000;
        }
    };
    
    
    
    @RequestMapping(value="/services", params = "stream=true" ,  method = {RequestMethod.GET, RequestMethod.POST},
    		produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamService(@RequestParam("type") String type, @RequestParam("serviceType") String serviceType,
            @RequestParam("service") String service,  HttpServletRequest request,HttpServletResponse response) throws IOException {

        ServiceRequestContext serviceContext  = serviceContextBuilder.buildContext(type, serviceType, service, request, gsonStore);
        String requestId  = serviceContext.getRequestId();
        
        IStreamRegistry registry = new InMemoryStreamRegistry();
        StreamManager streamManager = new StreamManager(registry);
        SseEmitter emitter = new SseEmitter();
        StreamSession streamSession =  streamManager.createSseStream(requestId, emitter);
        taskExecutorService.submit(() -> serviceManager.streamResult(type, serviceType, service, serviceContext.getFormJson(), serviceContext.getWorkerClass(), streamSession), requestId);
        return emitter;
    }
    
    
    @RequestMapping(value = "/services", params = "!stream", method = {RequestMethod.GET, RequestMethod.POST}, 
    		produces = {"application/json"})
    public ResponseEntity<?> service(@RequestParam("type") String type, @RequestParam("serviceType") String serviceType,
                                     @RequestParam("service") String service, HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }

        ServiceRequestContext serviceContext  = serviceContextBuilder.buildContext(type, serviceType, service, request, gsonStore);
        String requestId = serviceContext.getRequestId();
        String workerClass = serviceContext.getWorkerClass();
        JsonObject formJson = serviceContext.getFormJson();
        
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
                logger.debug("Registering service request : {}", requestId);
                
                @SuppressWarnings("unchecked")
				Future<Object> future = (Future<Object>) taskExecutorService.submit(() -> serviceManager.getResult(type, serviceType, service, formJson ,workerClass),	requestId);
                Object result = future.get();
                
                if (result instanceof JsonObject) {
                    ControllerUtils.handleSuccess(response, isAjax, result.toString());
                } else {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_TYPE,isAjax ? "application/json; charset=UTF-8" : "text/html; charset=UTF-8");
                    return ResponseEntity.ok().headers(headers).body(result.toString());
                }
        }
        catch (CancellationException e) {
        	throw new EfwServiceException("Request cancelled");
		}
        catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        } 
        finally {
            serviceContext.cleanup(gsonStore);
        }
        return null;
    }

    @RequestMapping(value = "/createDataSource", method = {RequestMethod.POST, RequestMethod.GET})
    public void createDataSource(HttpServletRequest request, HttpServletResponse response) throws IOException {

//        if (this.statusValidator.isStatusNotOkay()) {
//            throw new EfwServiceException("Unexpected error occured!");
//        }

        Map<String, Object> model = new HashMap<>();

        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            JsonObject dataSourcesList = SettingXmlUtility.getDataSourcesJson(true);

            JdbcUrlFormatUtility urlFormatUtility = ApplicationContextAccessor.getBean(JdbcUrlFormatUtility.class);

            JSONObject jsonOfDrivers = urlFormatUtility.getJsonOfDrivers();
            model.put("driversList", jsonOfDrivers.getJSONArray("drivers"));
            model.put("dataSourceTypes", dataSourcesList.getAsJsonArray("dataSources"));

            JsonObject result = new JsonObject();
            GsonUtility.accumulateAll(result, model);

            ControllerUtils.handleSuccess(response, isAjax, result.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/d/getResources", method = {RequestMethod.POST, RequestMethod.GET})
    public void getDirectories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Received a request to get the resources information from the Solution " + "directory.");
        }

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }

        long now = System.currentTimeMillis();
        long later;
        String extensions = request.getParameter("extensions");
        List listOfKeys = null;
        if (extensions != null) {
            if ("[]".equals(extensions) || "".equals(extensions) || extensions.trim().length() == 0) {
                throw new IllegalArgumentException("When provided, the parameter 'extensions'  " + "should not be " +
                        "empty.");
            }
            JsonArray jsonArray;
            try {
                jsonArray = JsonParser.parseString(extensions).getAsJsonArray();
            } catch (Exception e) {
                throw new EfwServiceException("The parameter extensions is expected to be of " + "type json array.");
            }
            for (Object element : jsonArray) {
                listOfKeys.add(element);
            }
        }

        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            String resources = new DirectoryLoaderProxy(listOfKeys).getResources(true);
            JsonArray resourcesJsonArray = JsonParser.parseString(resources).getAsJsonArray();
            ControllerUtils.replaceFilePath(resourcesJsonArray);
            resources = resourcesJsonArray.toString();
            later = System.currentTimeMillis();

            if (logger.isDebugEnabled()) {
                logger.debug("It took nearly " + ((later - now) / 1000) + " seconds to complete " +
                        "the IO operations to get the resources information.");
            }
            ControllerUtils.handleSuccess(response, isAjax, resources);
        } catch (Exception exception) {
            later = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("There was some problem. Could not read the resources. It took " +
                        "nearly " + ((later - now) / 1000) + " seconds to complete the IO " +
                        "operations to get the resources information.");
            }
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }


    @RequestMapping(value = "/getResources", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity getListOfDirectories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Received a request to get the resources information from the Solution " + "directory.");
        }

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }

        long now = System.currentTimeMillis();
        long later;
        String extensions = request.getParameter("extensions");
        List<String> listOfKeys = new ArrayList();
        if (extensions != null) {
            if ("[]".equals(extensions) || "".equals(extensions) || extensions.trim().length() == 0) {
                throw new IllegalArgumentException("When provided, the parameter 'extensions'  " + "should not be " +
                        "empty.");
            }
            JsonArray jsonArray;
            try {
                jsonArray = JsonParser.parseString(extensions).getAsJsonArray();
            } catch (Exception e) {
                throw new EfwServiceException("The parameter extensions is expected to be of " + "type json array.");
            }
            for (Object element : jsonArray) {
                String elementString = ((JsonPrimitive) element).getAsString();
                listOfKeys.add(elementString);
            }
            if (!listOfKeys.contains("folder")) listOfKeys.add("folder");

            HIResourceOfActiveUser allResources = hiResourceServiceDB.getAllResourcesWithExtensions(listOfKeys);
            List<HIResourceDTO> resourceDTOList = allResources.getResourceDTOList();

            return ResponseEntity.ok().body(resourceDTOList);
        }


        return null;
    }


    @Deprecated
    @RequestMapping(value = "/d/listDataSources", method = {RequestMethod.GET, RequestMethod.POST})
    public void listDataSources(@RequestParam("classifier") String classifier,
                                HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }

        if ("".equals(classifier) || classifier.trim().length() == 0) {
            throw new RequiredParameterIsNullException("The parameter classifier is empty.");
        }

        boolean isAjax = ControllerUtils.isAjax(request);
        boolean onlyEfwds = false;
        boolean onlyGlobal = false;
        boolean both = false;
        String type = null;
        try {
            if ("efwd".equalsIgnoreCase(classifier)) {
                onlyEfwds = true;
                type = request.getParameter("type");
                if (type != null) {
                    if ("".equals(type) || type.trim().length() == 0) {
                        throw new RequiredParameterIsNullException("The parameter type is empty.");
                    }
                }
            } else if ("global".equalsIgnoreCase(classifier)) {
                onlyGlobal = true;
            } else if ("all".equalsIgnoreCase(classifier)) {
                both = true;
            } else {
                throw new IllegalArgumentException("The parameter classifier should be either " +
                        "'efwd' or 'global' or " + "'all'.");
            }

            JsonObject connections;
            connections = new JsonObject();
            List dataSources = null;

            if (both || onlyEfwds) {
                JsonArray extensions = new JsonArray();
                extensions.add("efwd");
                EfwdReaderUtility efwdReaderUtility = new EfwdReaderUtility(extensions);
                List<ObjectNode> allEfwdConnections = efwdReaderUtility.getAllEfwdConnections(type);
                ObjectNode jsonObject = allEfwdConnections.get(0);
                Map m = (Map) jsonObject;

                dataSources = allEfwdConnections;
            }

            if (both || onlyGlobal) {
                if (dataSources == null) {
                    dataSources = new ArrayList<>();
                }
                Boolean dsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
                GlobalDSReaderUtility globalDSReaderUtility = ApplicationContextAccessor.getBean(GlobalDSReaderUtility.class);
                if (!dsTypeStorageDatabase) {
                    GlobalXmlReaderUtility globalXmlReaderUtility = ApplicationContextAccessor.getBean
                            (GlobalXmlReaderUtility.class);
                    globalXmlReaderUtility.addDataSources(dataSources, DataSourceSecurityUtility.READ);
                } else {
                    globalDSReaderUtility.addDataSources(dataSources, DataSourceSecurityUtility.READ, null, null);
                }

            }

            GsonUtility.accumulate(connections, "dataSources", dataSources);
            ControllerUtils.handleSuccess(response, isAjax, connections.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    @RequestMapping(value = "/adhoc", method = RequestMethod.GET)
    public String adhocPage() {
        return "adhoc";
    }

    @RequestMapping(value = "/designer-edit", method = RequestMethod.GET)
    public String designerPage() {
        return "designer-edit";
    }

    @RequestMapping(value = "/designer", method = RequestMethod.GET)
    public String designerEditPage() {
        return "designer";
    }

    @RequestMapping(value = "/adhoc/{page}", method = {RequestMethod.GET, RequestMethod.POST})
    public String tilesView(@PathVariable("page") String page) {
        return page;
    }

    @RequestMapping(value = "/visualizeAdhoc", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView visualizeAdhoc(HttpServletRequest request) {

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }
        //JSONObject cachedContent = JSONObject.fromObject(request.getAttribute("cachedContent"));
        JsonObject cachedContent = new Gson().fromJson(request.getAttribute("cachedContent").toString(), JsonObject.class);
        String formData = request.getParameter("formData");

        ModelAndView adhocView = new ModelAndView();
        if ((cachedContent == null || cachedContent.entrySet().isEmpty())) {
            if (formData != null) {
                String type = "adhoc";
                String service = "fetchData";
                String serviceType = "report";
                try {
                    formData = new String(Base64.decodeBase64(formData), ControllerUtils.defaultCharSet());

                } catch (IOException ex) {
                    logger.error("Exception occurred", ex);
                }
                JsonObject formJson = new Gson().fromJson(formData.substring(0, formData.lastIndexOf("}") + 1), JsonObject.class);
                //JSONObject formJson = JSONObject.fromObject(formData.substring(0, formData.lastIndexOf("}") + 1));
                ServiceManager serviceManager = ApplicationContextAccessor.getBean(ServiceManager.class);

                String serviceClass = serviceManager.pickTheWorkerClass(type, serviceType, service);
                String requestId = request.getParameter("requestId");
                try {
                    JsonObject jsonObject = (JsonObject) serviceManager.getResult(type, serviceType, service, formJson,
                            serviceClass);
                    String result = jsonObject.toString();
                    JsonObject serviceResult = new Gson().fromJson(result, JsonObject.class);
                    //JSONObject serviceResult = JSONObject.fromObject(result);
                    if (serviceResult.get("status").getAsString().equalsIgnoreCase("0")) {
                        adhocView.addObject("errorMessage", serviceResult.getAsJsonObject("response").get
                                ("message").getAsString());
                    } else {
                        JsonObject jsonResult = ControllerUtils.getDataFromResponse(serviceResult);
                        if (jsonResult != null) {
                            adhocView.addObject("result", jsonResult);
                        } else {
                            adhocView.addObject("result", "{}");
                        }
                    }
                } catch (Exception exception) {
                    logger.error("Error occurred", exception);
                    adhocView.addObject("result", "{}");
                }
            } else {
                adhocView.addObject("result", "{}");
            }
        } else {
            JsonObject jsonResult = ControllerUtils.getDataFromResponse(cachedContent);
            //jsonResult.accumulate("lastModified", cachedContent.optString("lastModified"));
            jsonResult.addProperty("lastModified", GsonUtility.optString(cachedContent, "lastModified"));
            adhocView.addObject("result", jsonResult.toString());
            request.removeAttribute("cachedContent");
        }
        ControllerUtils.addUrlParameters(request, adhocView);
        adhocView.setViewName("visualizeAdhoc");
        return adhocView;
    }


    @RequestMapping(value = "/cancelRequest", method = {RequestMethod.POST, RequestMethod.GET})
    public void cancelRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }


        boolean isAjax = ControllerUtils.isAjax(request);
        JsonObject jsonObject = new JsonObject();
        try {
            JsonObject outerJSON = new JsonObject();
            outerJSON.addProperty("status", 1);
            String requestId = request.getParameter("requestId");

            try {
               taskExecutorService.cancelTaskByRequestId(requestId);
            } catch (InstantiationError ie) {
                logger.error("Thread interrrupted");
            }
            jsonObject.addProperty("message", "Request cancelled successfully");
            outerJSON.add("response", jsonObject);

            ControllerUtils.handleSuccess(response, isAjax, outerJSON.toString());
        } catch (Exception exception) {

            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }


}
