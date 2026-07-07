package com.helicalinsight.hwf.core;

import net.sf.json.JSONObject;

public interface ICondition extends IJobProcess {
    boolean evaluateCondition(JSONObject inputs);
}