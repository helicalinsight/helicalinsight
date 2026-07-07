package com.helicalinsight.admin.utils;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public class UUIDGenerator {

	@NotNull
	public static String getUuid() {
		// Generate the random uuid
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
