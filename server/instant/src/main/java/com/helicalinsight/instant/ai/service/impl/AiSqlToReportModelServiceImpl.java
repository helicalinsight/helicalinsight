package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.instant.ai.payload.IInstantBIPayload;
import com.helicalinsight.instant.ai.payload.SqlToReportModelPayload;
import com.helicalinsight.instant.ai.service.IInstantBIService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Proxies InstantBI SQL → report_model conversion to the Python {@code /sql-to-report-model}
 * service. VizModel is derived downstream from SQL dimensions and measures.
 */
@Service(InstantBIServiceFactory.SQL_TO_REPORT_MODEL_SERVICE)
public class AiSqlToReportModelServiceImpl implements IInstantBIService {

    private static final Logger logger = LoggerFactory.getLogger(AiSqlToReportModelServiceImpl.class);

    static final String DOWNSTREAM_ENDPOINT = "/sql-to-report-model";

    @Override
    public void execute(IInstantBIPayload instantBIPayload, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SqlToReportModelPayload payload = (SqlToReportModelPayload) instantBIPayload;
        try {
            Callable<JsonObject> bodyPreparer = () -> {
                if (StringUtils.isAnyBlank(payload.getSql(), payload.getLocation(), payload.getMetadataFileName())) {
                    throw new EfwServiceException("sql, location, and metadataFileName are required");
                }
                JsonObject js = new JsonObject();
                JsonObject userInput = new JsonObject();

                InstantBIUtils.addSessionContext(request, userInput);
                userInput.addProperty("sql", InstantBIUtils.getEncodedElseNormal(payload.getSql()));
                userInput.addProperty("location", payload.getLocation());
                userInput.addProperty("metadataFileName", payload.getMetadataFileName());

                js.add("input", userInput);
                return js;
            };
            String botResponse = InstantBIServiceFactory.getHttpService().executeCancellableCall(
                    request, bodyPreparer, DOWNSTREAM_ENDPOINT);

            JsonObject responseObject = InstantBIUtils.prepareSqlToReportModelResponse(botResponse);
            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", responseObject);

            InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
        } catch (EfwServiceException exception) {
            if (InstantBIUtils.isAbortException(exception)) {
                logger.info("SQL-to-report-model request aborted for requestId={}",
                        InstantBIUtils.resolveRequestId(request));
                return;
            }
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        }
    }
}
