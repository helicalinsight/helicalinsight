package com.helicalinsight.converter;

import com.helicalinsight.admin.utils.CrossDbStringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CrossDbEmptyStringConverter implements AttributeConverter<String, String> {

	@Override
	public String convertToDatabaseColumn(String value) {
		return CrossDbStringUtils.toDatabaseValue(value);
	}

	@Override
	public String convertToEntityAttribute(String value) {
		return CrossDbStringUtils.toEntityValue(value);
	}
}