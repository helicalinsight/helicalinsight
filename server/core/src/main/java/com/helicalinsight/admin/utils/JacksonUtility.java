package com.helicalinsight.admin.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonUtility {

	private static final Logger logger = LoggerFactory.getLogger(JacksonUtility.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();

	public static ObjectNode fromObject(String jsonString) {
		try {
			return mapper.readValue(jsonString, ObjectNode.class);
		} catch (Exception e) {
			logger.error("Error occurred while mapping string to objectnode : {}",e.getMessage());
			return null;
		}

	}
	public static ObjectNode emptyNode() {
		return JsonNodeFactory.instance.objectNode();
	}
	public static ArrayNode arrayNode() {
		return JsonNodeFactory.instance.arrayNode();
	}
	public static ObjectNode mapToJson(Object object) {
		try {
			String jsonString = mapper.writer().writeValueAsString(object);
			return fromObject(jsonString);
		} catch (Exception e) {
			logger.error("Error occurred while mapping object to json : {}",e.getMessage());
			logger.error("Error occurred while mapping object to json : {}",e);


			e.printStackTrace();
			return emptyNode();
		}
	}
	
	public static Map<String,String>  mapToMap(String json) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		Object o = jsonObject.get("extraOptions");
		JSONObject extraOptions=null;
		if(o instanceof String){
			extraOptions=JSONObject.fromObject(o);
		}
		Map<String,String> map = new HashMap<>();
		if(o instanceof JSONObject || extraOptions!=null){
		JSONObject obj=	extraOptions!=null?extraOptions:((JSONObject)o);
			Set set = obj.keySet();
			for(Object oj:set) {
				map.put((String)oj, obj.getString((String)oj));
			}
		}
		return map;
	}
 }
