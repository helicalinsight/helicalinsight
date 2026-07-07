package com.helicalinsight.efw.controller;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.MalformedXmlException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParametersNotProvidedException;
import com.helicalinsight.efw.io.SynchronizationOperationHandler;
import com.helicalinsight.efw.utility.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The controller has a mapping to /sync. It does the work of coping the reports
 * of different formats like pdf, png etc. to a destination directory based on
 * the content in the .result file. The destination and source will be in the
 * solution directory.
 * <p/>
 * Created by author on 31-10-2014.
 *
 * @author Rajasekhar
 * @since 1.1
 */

@Controller
@Component
public class SynchronizationController {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizationController.class);

    /**
     * The method takes request parameters sourceArray and destination which are
     * required for the operation to be done. Else
     * RequiredParametersNotProvidedException will be thrown
     *
     * @param request The Http request object
     * @throws RequiredParametersNotProvidedException                  if the parameters are not well
     *                                                                 formed this
     *                                                                 exception is
     *                                                                 thrown
     * @throws com.helicalinsight.efw.exceptions.MalformedXmlException if the xml files that the method
     *                                                                 operates
     *                                                                 on are not well
     *                                                                 formed this exception is thrown
     */
    @RequestMapping(value = "/sync", method = {RequestMethod.POST, RequestMethod.GET})
    public void synchronize(HttpServletRequest request, HttpServletResponse response) throws
            RequiredParametersNotProvidedException, MalformedXmlException, IOException {
        boolean isAjax = ControllerUtils.isAjax(request);

        try {
            String sourceArray = request.getParameter("sourceArray");
            String destination = request.getParameter("destination");

            if (validateRequest(sourceArray, destination, request)) {
                logger.debug("The request parameters are not null");
            }

            String responseMessage;

            SynchronizationOperationHandler operationHandler = new SynchronizationOperationHandler();
            if (operationHandler.handle(sourceArray, destination, request)) {
                responseMessage = "Sync is successful";
            } else {
                throw new OperationFailedException("Failed to perform the operation.");
            }
            String jsonResponse = ResponseUtils.createJsonResponse(responseMessage);
            ControllerUtils.handleSuccess(response, isAjax, jsonResponse);
        } catch (Exception ex) {
            ControllerUtils.handleFailure(response, isAjax, ex);
        }
    }

    /**
     * Validates the parameters of the http request
     *
     * @param sourceArray The request parameter sourceArray
     * @param destination The request parameter destination
     * @param request     The Http request object
     * @return returns true if the parameters are not empty and not null
     * @throws RequiredParametersNotProvidedException
     */
    private boolean validateRequest(String sourceArray, String destination, HttpServletRequest request) throws
            RequiredParametersNotProvidedException {
        if (sourceArray == null || "".equals(sourceArray.trim())) {
            request.setAttribute("message", "The request parameter sourceArray is not valid");
            throw new RequiredParametersNotProvidedException("The request parameter sourceArray " + "is not valid");
        }

        if (destination == null || "".equals(destination.trim())) {
            request.setAttribute("message", "The request parameter destination is not valid");
            throw new RequiredParametersNotProvidedException("The request parameter destination " + "is not valid");
        }
        return true;
    }
}
