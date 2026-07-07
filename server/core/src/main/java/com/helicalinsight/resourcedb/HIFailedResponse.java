package com.helicalinsight.resourcedb;

public class HIFailedResponse extends HIResponse {

    public HIFailedResponse(){
        this.setStatus(HIResponse.FAILED_STATUS);
    }
}
