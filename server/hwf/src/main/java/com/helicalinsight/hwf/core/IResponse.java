package com.helicalinsight.hwf.core;

import net.sf.json.JSONObject;

import jakarta.servlet.http.HttpServletResponse;

public interface IResponse {
    public JSONObject setOutPutResponse(HttpServletResponse response, JSONObject outputTag, String outputFromInputTag);

}
