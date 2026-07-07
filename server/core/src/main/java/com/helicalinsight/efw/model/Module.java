package com.helicalinsight.efw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public record Module (
	 String name,
	 Boolean enabled,
	 Integer reportCount
) {}
