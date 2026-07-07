package com.helicalinsight.hwf.core;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;

public interface IRequest {
    public JSONObject getRequestValue(HttpServletRequest request, JSONObject hwfInput, JSONArray filterJSON,
                                      JSONObject filterInputJSONObject);

}
