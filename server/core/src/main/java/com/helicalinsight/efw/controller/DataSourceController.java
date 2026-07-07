package com.helicalinsight.efw.controller;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.EfwdReaderUtility;
import com.helicalinsight.efw.components.GlobalDSReaderUtility;
import com.helicalinsight.efw.components.GlobalXmlReaderUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.controllerutils.StatusValidator;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 26-July-21.
 *
 * @author Somen
 */
@Controller
public class DataSourceController {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    @Autowired
    private StatusValidator statusValidator;

    private static final String ALL_CONNECTIONS = "all";

    @RequestMapping(value = "/listDataSources", 
    		method = {RequestMethod.GET, RequestMethod.POST},
    		produces = {"application/json"}
    		)
    

    public ResponseEntity<?> listDataSources(@RequestParam("classifier") String classifier, @RequestParam(required = false,name = "driver") String driver,
    		HttpServletRequest request,HttpServletResponse response) throws IOException {

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }

        if ("".equals(classifier) || classifier.trim().length() == 0) {
            throw new RequiredParameterIsNullException("The parameter classifier is empty.");
        }

        boolean isAjax = ControllerUtils.isAjax(request);
        boolean onlyEfwds = "efwd".equalsIgnoreCase(classifier);
        boolean onlyGlobal = "global".equalsIgnoreCase(classifier);
        boolean both = ALL_CONNECTIONS.equalsIgnoreCase(classifier);
        String type = null;
        try {
			if (onlyEfwds) {
				type = request.getParameter("type");

				if (StringUtils.isBlank(type)) {
					throw new RequiredParameterIsNullException("The parameter type is empty.");
				}

			}
			if (!(both || onlyGlobal || onlyEfwds)) {
				throw new IllegalArgumentException(
						"The parameter classifier should be either " + "'efwd' or 'global' or " + "'all'.");
			}

            List<Map<String, Object>> dataSources = new ArrayList();
            List<ObjectNode> allEfwdConnections = new ArrayList<>();
			if (both || onlyEfwds) {
				JsonArray extensions = new JsonArray();
				extensions.add("efwd");
				EfwdReaderUtility efwdReaderUtility = new EfwdReaderUtility(extensions);
				if (ALL_CONNECTIONS.equalsIgnoreCase(classifier)) {
					efwdReaderUtility.addDataSources(allEfwdConnections,classifier,DataSourceSecurityUtility.READ);
				}else{
					efwdReaderUtility.addDataSources(allEfwdConnections,type,DataSourceSecurityUtility.READ);
                }

				for (ObjectNode object : allEfwdConnections) {
					Map<String, Object> myMap = new ObjectMapper().convertValue(object, Map.class);
					dataSources.add(myMap);
				}

			}

            if (both || onlyGlobal) {

                Boolean dsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
                GlobalDSReaderUtility globalDSReaderUtility = ApplicationContextAccessor.getBean(GlobalDSReaderUtility.class);
                if (!dsTypeStorageDatabase) {
                    GlobalXmlReaderUtility globalXmlReaderUtility = ApplicationContextAccessor.getBean
                            (GlobalXmlReaderUtility.class);
                    globalXmlReaderUtility.addDataSources(dataSources, DataSourceSecurityUtility.READ);
                } else {
                    String vendorName=request.getParameter("vendorName");
                    globalDSReaderUtility.addDataSources(dataSources, DataSourceSecurityUtility.READ,driver,vendorName);
                }

            }

            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.putPOJO("dataSources",dataSources);
            return ResponseEntity.ok()
                    .body(objectNode);
        } catch (Exception exception) {
            exception.printStackTrace();
            ControllerUtils.handleFailure(response, isAjax, exception);
        }

        return null;


    }

}
