package com.helicalinsight.datasource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * The `HCRJsonDataSource` class is a implementation of the {@link JRDataSource} interface designed to work with JasperReports. 
 * It serves as a data source for generating reports from a JSON array.
 * Created by author on 12/4/2019.
 * @author Rajesh
 */
public class HCRJsonDataSource implements JRDataSource {
	private final String date = "java.sql.Date";
	private final String dateTime = "java.sql.Timestamp";
	private final String time = "java.sql.Time";
	private final String bigDecimal = "java.math.BigDecimal";
	private final String floatValue = "java.lang.Float";
	private final String byteValue = "java.lang.Byte";
	private final String shortValue = "java.lang.Short";
	private final String doubleValue = "java.lang.Double";
	private final String integerValue = "java.lang.Integer";
	private final String longValue = "java.lang.Long";
	private final String stringValue  = "java.lang.String";

	private JsonArray data;
	private int index = -1;

	
	public HCRJsonDataSource(JsonArray data) {
		this.data = data;
	}


	/**
	 * next()
	 * Moves to the next record in the data source.
	 * @return True if there is a next record, false otherwise.
	 * @throws JRException If an exception occurs while moving to the next record.
	 */
	@Override
	public boolean next() throws JRException {
		index++;
		return index < data.size();
	}
	/**
	 * getFieldValue(JRField jrField)
	 * Gets the value of the specified field .
	 * @param jrField 			provides field name and class name.
	 * @return value of the field .
	 * @throws JRException If an exception occurs while retrieving the field value.
	 */
	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		JsonObject item = data.get(index).getAsJsonObject();
		String fieldName = jrField.getName();
		String valueClassName = jrField.getValueClassName();
		Object value = item.get(fieldName);
		value = prepareValue(valueClassName, value);
		return value;
	}
	/**
	 * prepareValue(String valueClassName, Object value)
	 * Prepares the field value based on its class type.
	 *
	 * @param valueClassName 		class name of the field.
	 * @param value          		field value.
	 * @return The prepared field value.
	 */
	public Object prepareValue(String valueClassName, Object value) {
 
		String  valueAsString =  value == null ? null : value.toString();
		valueAsString =  valueAsString.replaceAll("^\"|\"$", ""); 

		if (ApplicationProperties.getInstance().getNullValue().equals(valueAsString)) {
			return null;

		}
		if (value != null && valueClassName.equals(value.getClass().getName())) {
			return value;
		}
		

		try {
			switch (valueClassName) {
			case date:
				if (valueAsString.length() > 10)
					valueAsString = valueAsString.substring(0, 10);
				return Date.valueOf(valueAsString);

			case dateTime:
				return Timestamp.valueOf(valueAsString);

			case time:
				return Time.valueOf(valueAsString);

			case floatValue:
				return Float.valueOf(valueAsString);

			case bigDecimal:
				return BigDecimal.valueOf((Integer) value);

			case byteValue:
				return Byte.valueOf(valueAsString);

			case shortValue:
				return Short.valueOf(valueAsString);

			case doubleValue:
				return Double.valueOf(valueAsString);

			case integerValue:
				return Integer.valueOf(valueAsString);
			case longValue:
				return Long.valueOf(valueAsString);
			case stringValue:
				 return valueAsString;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return value;
	}

	@Override
	public String toString() {
		return "HCRJsonDataSource{" + "data=" + data + ", index=" + index + '}';
	}
}
