package com.helicalinsight.adhoc.report;

/**
 * The ReportFileNotFoundException class represents an exception that is thrown when a specified
 * report file is not found.
 * Created by Rajasekhar on 27-04-2015.
 * @author Rajasekhar
 */
public class ReportFileNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -697849305502823745L;

    public ReportFileNotFoundException(String message) {
        super(message);
    }
}
