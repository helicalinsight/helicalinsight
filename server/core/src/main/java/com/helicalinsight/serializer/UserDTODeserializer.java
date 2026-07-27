package com.helicalinsight.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.helicalinsight.admin.dto.UserDTO;

public class UserDTODeserializer extends JsonDeserializer<UserDTO> {

	@Override
	public UserDTO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = p.getCodec().readTree(p);
		if (node.isObject()) {
			return p.getCodec().treeToValue(node, UserDTO.class);
		}
		if (node.isTextual() ||  node.isInt()) {
			UserDTO user = new UserDTO();
			user.setId(node.asInt());
			return user;
		}
		return null;
	}
}
