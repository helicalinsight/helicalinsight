package com.helicalinsight.export.dto.validation;

import org.springframework.stereotype.Component;

import com.helicalinsight.export.dto.ResourceExportRequest;
import com.helicalinsight.export.exception.RequestValidationException;
/**
 * Validates the directories.
 * @Component Indicates that this class is a Spring component.
 */
@Component
public class ExportRequestValidator {
	/**
     * Validates the directory.
     *
     * @param dto 		 DTO for validation.
     * @throws RequestValidationException If the validation fails.
     */
	public void validate(ResourceExportRequest dto) {

		if (dto == null) {
			throw new RequestValidationException("Please provide a valid PayLoad");
		}
		/**
		if (dto.getDir() == null || dto.getDir().isEmpty()) {
			throw new RequestValidationException("Directory should not be blank");
		} 
		if (!FileNameUtils.getExtension(dto.getDir()).isEmpty()) {
			throw new RequestValidationException("Invalid path provided.");
		}
		if (dto.getDir().length() < 3 || dto.getDir().length() > 60) {
			throw new RequestValidationException("Directory should be between 3 and 60 chars.");
		}
		**/
	}

}
