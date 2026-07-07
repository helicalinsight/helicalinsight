package com.helicalinsight.hwf.component;

import com.helicalinsight.hwf.core.IJobProcess;
import com.helicalinsight.hwf.core.api.ExecutionStatus;
import com.helicalinsight.hwf.util.ComponentUtils;
import net.sf.json.JSONObject;

public class TestHWFComponent implements IJobProcess {


    @Override
    public JSONObject jobProcess(JSONObject input, JSONObject processJson) {
        return print(input);
    }

    public JSONObject print(JSONObject value) {
        return value;
    }

    @Override
    public JSONObject executionStatus() {
        return ComponentUtils.setExecutionStatus(ExecutionStatus.SUCCESS);
    }


}
