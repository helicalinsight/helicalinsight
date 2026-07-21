package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.instant.ai.service.IAiRecommendAnalystService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.helicalinsight.instant.ai.util.PatternExtractor.extractFromPattern;

public class AiRecommendAnalystServiceImpl implements IAiRecommendAnalystService {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public void execute(String model, String domain, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        model = InstantBIUtils.getEncodedElseNormal(model);
        domain = InstantBIUtils.getEncodedElseNormal(domain);
        JsonObject modelJson = GsonUtility.parseString(model, JsonObject.class);

        JsonObject js = new JsonObject();
        String topN = request.getParameter("topN");
        if (topN == null || topN.isEmpty()) {
            topN = "10";
        }
        js.add("model", modelJson);
        js.addProperty("domain", domain);
        js.addProperty("topN", Integer.valueOf(topN));

        InstantBIUtils.addSessionContext(request, js);

        String questions = InstantBIServiceFactory.getHttpService().callHttp("/topNQuestion", js);
        JsonObject responseObj = new JsonObject();
        String[] split = questions.split("\\n");
        List<String> list = Arrays.asList(split);
        List<String> newSt = new ArrayList<>();
        for (String s : list) {
            String ss = extractFromPattern(s, "^\\d+\\.\\s*(.*?):");
            newSt.add(ss);
        }
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(newSt).getAsJsonArray();

        responseObj.add("questions", jsonArray);

        try {
            InstantBIUtils.sendResponse(response, isAjax, responseObj);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }
}
