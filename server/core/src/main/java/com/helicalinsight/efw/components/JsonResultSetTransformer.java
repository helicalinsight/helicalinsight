package com.helicalinsight.efw.components;

import org.hibernate.transform.ResultTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonResultSetTransformer implements ResultTransformer {
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Object> jsonItem = new HashMap<>();
        for (int index = 0; index < tuple.length; index++) {
            jsonItem.put(aliases[index], tuple[index]);
        }
        return jsonItem;
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }
}
