package com.helicalinsight.adhoc.jreport;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.JsonObject;
import com.helicalinsight.stream.StreamSession;

import net.sf.jasperreports.engine.JasperPrint;


/**
 * This Interface is intended to be implemented by classes that handle the creation of HCR reports using 
 * JasperReports.
 * Created by author on 12/5/2019.
 * @author Rajesh
 */
public interface IHCRGenerator {
 
	/**
     * Generates an HCR report based on the provided form data. This method handles caching, refreshing cache if required,
     * and processes the report generation.
     * 
     * @param formData 		 JSON object containing form data for report generation
     * @return the JSON object containing the generated report details
     */
    JsonObject generateHCReport(JsonObject formData);
    
    default void generateHCReportStreaming(JsonObject formData, StreamSession session) {
    	// NOOP
    }
    
    
    /**
     * Generates a JasperPrint object based on the provided form data.
     * 
     * @param formData 		 JSON object containing form data for report generation
     * @return the generated JasperPrint object
     */
    JasperPrint generateHCRPrint(JsonObject formData);
    /**
     * Provides a response using the JasperPrint object and number of records found in cache.
     * 
     * @param formData           JSON object containing form data for report generation
     * @param jasperPrint        JasperPrint object
     * @param noOfRecords        number of records found in cache
     * @return the JSON object containing the response
     */
    JsonObject getResponseUsingPrint(JsonObject formData, JasperPrint jasperPrint, Integer noOfRecords);
}
