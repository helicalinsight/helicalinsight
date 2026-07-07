package com.helicalinsight.scheduling;

public interface ICronExpresssionOperation {

    public String convertDateIntoCronExpression(com.google.gson.JsonObject jsonData);
    
}
