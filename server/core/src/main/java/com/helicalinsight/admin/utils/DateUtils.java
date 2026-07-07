package com.helicalinsight.admin.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
	
	public static String convertDateToString(LocalDate date, String format) {
		if (date == null)
			return "";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return date.format(formatter);
	}
}
