package com.helicalinsight.admin.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.exception.LicenseViolationException;

@EnableWebMvc
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = LicenseViolationException.class)
	public ResponseEntity<String> resourceExceptionHandler(LicenseViolationException exception) {
		JsonObject errorNode = new JsonObject();
		JsonObject response = new JsonObject();
		response.addProperty("message", exception.getLocalizedMessage());
		errorNode.addProperty("status", 0);
		errorNode.add("response", response);
		return new ResponseEntity<>(errorNode.toString(),HttpStatus.OK);
	}
}
