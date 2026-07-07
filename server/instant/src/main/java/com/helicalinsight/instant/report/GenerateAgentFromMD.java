package com.helicalinsight.instant.report;

import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.adhoc.report.ReportOpenHelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * InstantReportReaderComponent is responsible for reading an instant report from the file system
 * and returning its content as JSON.
 * <p>
 * It implements the IComponent interface.
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class GenerateAgentFromMD implements IComponent {
    /**
     * Executes the component to read an agent.
     *
     * @param jsonFormData JSON string containing form data with directory and file name.
     * @return A string representing the content of the agent report in JSON format.
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
        userInput.addProperty("sessionCookie", resolveSessionCookie());
        userInput.addProperty("username", userDetails.getUsername());
        js.add("input", userInput);
        return callHttp("/getSemanticData", js);
    }

    private static String resolveSessionCookie() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new EfwServiceException("Session cookie not found.");
        }
        HttpServletRequest request = attributes.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName()) && StringUtils.isNotBlank(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        throw new EfwServiceException("Session cookie not found.");
    }

    private String callHttp(String endpoint, JsonObject body) {
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://pyflask:8000/";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new EfwServiceException("problem while loading the call " + endpoint);
        }

        return response.body();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
