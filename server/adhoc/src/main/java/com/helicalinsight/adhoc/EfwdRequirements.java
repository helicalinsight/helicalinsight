package com.helicalinsight.adhoc;
/**
 * 
 * This enum is used to provide *.efwd files folder
 *
 */
public enum EfwdRequirements {

	DB_CONFIG_DIRECTORY("DbConfig"), ADMIN_DIRECTORY("Admin");

	private final String data;

	private EfwdRequirements(String data) {

		this.data = data;
	}

	public String getData() {
		return this.data;
	}

}
