package com.helicalinsight.efw.jasperintegration;

import java.util.List;

public record  HCRConfigDTO(
		List<String> dependencies,
		String codeSchema
		
		) {}
