package com.helicalinsight.datasource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JRField;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CustomJRResultSetDataSource extends JRResultSetDataSource {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_TIME_FORMAT_MILIS = "yyyy-MM-dd HH:mm:ss.SS";
    private static final String DATE_TIME_FORMAT_MILIS_3 = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
    private static final SimpleDateFormat dateTimeFormatMillis = new SimpleDateFormat(DATE_TIME_FORMAT_MILIS);
    private static final SimpleDateFormat dateTimeFormatMillis3 = new SimpleDateFormat(DATE_TIME_FORMAT_MILIS_3);

    public CustomJRResultSetDataSource(ResultSet resultSet) {
        super(resultSet);
    }

    @Override
    protected Object readDate(Integer columnIndex, JRField field) throws SQLException {
        // Get the value as a string
        String value = getResultSet().getString(columnIndex);

        // Check if the value is a valid date or date-time string
        if (value != null && !value.isEmpty()) {
            try {
                // Try parsing as date-time first
                if (value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    Date timestamp = new Date(dateTimeFormat.parse(value).getTime());
                    return timestamp; // Return as Timestamp
                } else if (value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{2}")) {
                    Date date = new Date(dateTimeFormatMillis.parse(value).getTime());
                    return date; // Return as Date
                }else if (value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}")) {
                    Date date = new Date(dateTimeFormatMillis3.parse(value).getTime());
                    return date; // Return as Date
                }else if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    Date date = new Date(dateFormat.parse(value).getTime());
                    return date; // Return as Date
                }
            } catch (ParseException e) {
                throw new SQLException("Failed to parse date: " + value, e);
            }
        }

        // Call the superclass method for other cases
        return super.readDate(columnIndex, field);
    }

}