package com.helicalinsight.resourcedb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToCollectionUtil {

    public static List<String> getList(String json) {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<String> sourceList = null;
        try {
            CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, String.class);
            sourceList = objectMapper.readValue(json, collectionType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sourceList;
    }


    public static List<List<String>> getMapList(String json) {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<List<String>> sourceList = null;
        try {
             sourceList = objectMapper.readValue(json, new TypeReference<List<List<String>>>(){});

        } catch (Exception e) {
            //if the string is not in json format
            e.printStackTrace();
        }


        if(sourceList.size()>0){
            return sourceList;
        }else{
            return null;
        }

    }

    public static Map<String,Object> getMapObject(String resourceContent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};

        HashMap<String,Object> resourceMap = objectMapper.readValue(resourceContent, typeRef);
        return resourceMap;
    }
 }
