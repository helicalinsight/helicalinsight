package com.helicalinsight.resourcedb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.ArrayList;
import java.util.List;

public class ConvertJsonUtility {

    public List<String> jsonToList(String sourceArray){
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> sourceList = new ArrayList<>();
        try {
            sourceList = objectMapper.readValue(sourceArray, TypeFactory.defaultInstance().constructCollectionType(List.class,
                    String.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return sourceList;
    }
}
