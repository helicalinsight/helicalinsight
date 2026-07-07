package com.helicalinsight.hwf.core;

import net.sf.json.JSONObject;

public interface IIterator extends IJobProcess {
    JSONObject evaluateIteration(JSONObject steps); //returns the json from array/condition

    boolean hasNext();

    int getCountState();
}

