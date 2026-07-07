package com.helicalinsight.efw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public record Embed(
		Boolean ssoEnabled, 
		Boolean jwtEnabled
		) {
}