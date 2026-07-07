package com.helicalinsight.hwf.core;


import net.sf.json.JSONObject;

public interface IJobProcess {
    JSONObject jobProcess(JSONObject input, JSONObject jobProcess);

    JSONObject executionStatus();
}
