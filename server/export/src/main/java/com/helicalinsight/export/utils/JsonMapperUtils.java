package com.helicalinsight.export.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
/**
 * Utility class for mapping JSON data to objects and handling JSON arrays.
 */
@Component
public class JsonMapperUtils {

	private static final Logger LOG = LoggerFactory.getLogger(JsonMapperUtils.class);
	
	private static final JsonFactory factory = JsonFactory.builder()
            .streamReadConstraints(
                    StreamReadConstraints.builder()
                            .maxStringLength(50_000_000)
                            .build())
            .build();
	
	private static final ObjectMapper mapper = new ObjectMapper(factory);
    /**
     * Method deserialize JSON content and returns to the specified class.
     *
     * @param data  	JSON data to be mapped.
     * @param clazz 	class of the DTO.
     * @param <T>   	type of the DTO.
     * @return {@code ObjectMapper} object.
     */
	public <T> T mapToDTO(String data, Class<T> clazz) {

		try {
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(data, clazz);
		} catch (JsonProcessingException e) {
			LOG.error("Error occurred during mapping to DTO :: {}", e.getMessage());
			return null;
		}

	}
	/**
     * Maps a JSON array from a file to an ArrayNode.
     * purpose of this code is to read a JSON data from a file (represented by stream) and create a Jackson ArrayNode object .
     *
     * @param stream 		FileInputStream of the file containing the JSON data.
     * @return The mapped ArrayNode.
     */
	public ArrayNode mapToArray(FileInputStream stream) {
		try {
			ObjectMapper mapper = new ObjectMapper(factory);
			return mapper.readValue(stream, ArrayNode.class);
		} catch (IOException e) {
			LOG.error("Error occurred during mapping to Array :: {}", e.getMessage());
			return null;
		}

	}
	 /**
     * Maps JSON data from a file to an object of the specified class.
     *
     * @param stream 		FileInputStream of the file containing the JSON data.
     * @param clazz  		class of the object.
     * @param <T>    		type of the object.
     * @return The mapped object.
     */
	public <T> T mapToDTO(FileInputStream stream, Class<T> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper(factory);
			return mapper.readValue(stream, clazz);
		} catch (IOException e) {
			LOG.error("Error occurred during mapping to Object :: {}", e.getMessage());
			return null;
		}
	}
	/**
     * Converts a {@code JsonNode} to a List of JsonNode. If the input node is not an array, a List with a single element is returned.
     *
     * @param node 		JsonNode to be converted.
     * @return The List of JsonNode.
     */
	public List<JsonNode> asList(JsonNode node) {
		List<JsonNode> list = new ArrayList<>();
		if (!node.isArray()) {
			list.add(node);
		} else {
			for (JsonNode listNode : node) {
				list.add(listNode);
			}
		}
		return list;
	}
	/**
     * Maps JSON data from a file to a List using a TypeReference.
     *
     * @param file    		file containing the JSON data.
     * @param typeRef 		TypeReference specifying the type of the List.
     * @param <T>     		type of the List.
     * @return The mapped List.
     */
	public <T> T asList(File file, TypeReference<T> typeRef) {
		try {
			return new ObjectMapper().readValue(file, typeRef);
		} catch (IOException e) {
			LOG.error("Error occurred during mapping to List :: {}", e.getMessage());
			return null;
		}
	}

}
