package com.helicalinsight.resourcedb;

import java.util.HashMap;
import java.util.Map;

public abstract class HIResponse {
    public static Integer FAILED_STATUS = 0;
    public static Integer SUCCESS_STATUS = 1;
    private Integer status = 1;
    private Map<String, Object> response;

    public HIResponse(){
        response = new HashMap<>();
    }

    public void setMessage(String message) {
        this.response.put("message", message);
    }

    public void setData(HIResourceDTO hiResourceDTO){
        this.response.put("data",hiResourceDTO);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(String key,Object value) {
        this.response.put(key,value);
    }
}
