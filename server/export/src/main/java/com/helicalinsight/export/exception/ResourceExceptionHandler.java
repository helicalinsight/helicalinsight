package com.helicalinsight.export.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The ResourceExceptionHandler class serves as a global exception handler for resource-related exceptions
 * in a Spring MVC application.
 *
 * It uses annotations such as @EnableWebMvc and @RestControllerAdvice to configure and handle exceptions
 * globally for REST controllers.
 */
@EnableWebMvc
@RestControllerAdvice
public class ResourceExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ResourceExceptionHandler.class);
	/**
     * Handles ResourceException and constructs a ResponseEntity containing a JSON response.
     *
     * @param exception 				specific ResourceException that occurred.
     * @return ResponseEntity<String> containing a JSON response with the error details.
     */
	@ExceptionHandler(value = ResourceException.class)
	public ResponseEntity<String> resourceExceptionHandler(ResourceException exception) {
		ObjectNode errorNode = JsonNodeFactory.instance.objectNode();
		ObjectNode response = JsonNodeFactory.instance.objectNode();
		response.put("message", exception.getLocalizedMessage());
		errorNode.put("status", 0);
		errorNode.putPOJO("response", response);
		LOG.error(exception.getMessage());
		return new ResponseEntity<>(errorNode.toString(),HttpStatus.OK);
	}
}

