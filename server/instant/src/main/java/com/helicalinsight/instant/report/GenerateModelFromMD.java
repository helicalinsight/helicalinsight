package com.helicalinsight.instant.report;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * InstantReportReaderComponent is responsible for reading an instant report from the file system
 * and returning its content as JSON.
 * <p>
 * It implements the IComponent interface.
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class GenerateModelFromMD implements IComponent {
    /**
     * Executes the component to read an model.
     *
     * @param jsonFormData JSON string containing form data with directory and file name.
     * @return A string representing the content of the model report in JSON format.
     * @throws IllegalArgumentException If the specified file doesn't exist.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String directory = formJson.get("dir").getAsString();
        String fileName = formJson.get("file").getAsString();
        Principal userDetails = AuthenticationUtils.getUserDetails();
        JsonObject js = new JsonObject();
        JsonObject userInput = new JsonObject();
        userInput.addProperty("location", directory);
        userInput.addProperty("fileName", fileName);
        InstantBIUtils.addSessionContext(currentRequest(), userInput);
        userInput.addProperty("username", userDetails.getLoggedInUser().getUsername());
        js.add("input", userInput);
        return InstantBIServiceFactory.getHttpService().callHttp("/getSemanticData", js);
    }

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new EfwServiceException("Session cookie not found.");
        }
        return attributes.getRequest();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
