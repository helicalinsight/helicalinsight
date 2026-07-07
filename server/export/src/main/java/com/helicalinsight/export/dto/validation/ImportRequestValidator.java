package com.helicalinsight.export.dto.validation;

import java.util.HashSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.exception.RequestValidationException;
/**
 * Validates the import request DTO and associated multipart file.
 * @Component Indicates that this class is a Spring component.
 */
@Component
public class ImportRequestValidator {
    /**
     * Validates the import request DTO and associated multipart file.
     *
     * @param multipartFile 	multipart file to be validated.
     * @param dto               import request DTO to be validated.
     * @throws RequestValidationException If the validation fails.
     */
	public void validate(MultipartFile multipartFile, ImportRequest dto) {
		
		if (dto == null) {
			throw new RequestValidationException("Please provide a valid PayLoad");
		}
		
		validateMultipartFile(multipartFile,dto.getKey());
		
		if (dto.getOnConflict() == null || dto.getOnConflict().isEmpty()) {
			throw new RequestValidationException("Please provide conflict mode.");
		}
		HashSet<String> allowedValues = new HashSet<>();
		allowedValues.add("skip");
		allowedValues.add("update");
		if (!allowedValues.contains(dto.getOnConflict())) {
			throw new RequestValidationException("Invalid conflict mode selected.");
		}
		

	}

	public void validateMultipartFile(MultipartFile file, String key) {
		if(StringUtils.isBlank(key)) {
			if ( file == null || file.isEmpty()) {
				throw new RequestValidationException("File should not be empty");
			} 
			String extension = FilenameUtils.getExtension(file.getOriginalFilename());
			if (!"zip".equalsIgnoreCase(extension)) {
				throw new RequestValidationException("Invalid file format , please select a zip file.");
			}
		}
		
	}

}
