package com.helicalinsight.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.helicalinsight.admin.model.User;

public class UserDeserializer extends JsonDeserializer<User> {

	@Override
	public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = p.getCodec().readTree(p);
		if (node.isObject()) {
			return p.getCodec().treeToValue(node, User.class);
		}
		if (node.isTextual() ||  node.isInt()) {
			User user = new User();
			user.setId(node.asInt());
			return user;
		}
		return null;
	}
}
