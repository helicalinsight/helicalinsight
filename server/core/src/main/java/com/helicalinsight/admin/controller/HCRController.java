package com.helicalinsight.admin.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import  com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.jasperintegration.HCRConfigDTO;

@RestController
public class HCRController {
	
	@GetMapping(value = "/hcrConfig", produces = MediaType.APPLICATION_JSON_VALUE,  
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HCRConfigDTO>  provideHcrConfigurations() {
		List<String> dependencies = HCRUtils.findAllDependencies();
		Path sampleScriptPath = Paths.get(ApplicationProperties.getInstance().getSystemDirectory(),"Admin","Static","HCRScripts","sample.js");
		String scriptAsString = HCRUtils.getFileAsString(sampleScriptPath);
		return  ResponseEntity.ok(new HCRConfigDTO(dependencies, scriptAsString));
	}
}
