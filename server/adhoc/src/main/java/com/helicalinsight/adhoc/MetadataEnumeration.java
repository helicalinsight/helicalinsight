package com.helicalinsight.adhoc;

/**
 * 
 * This is enum is used to provide the mapId based on the requested constant.
 *
 */
public enum MetadataEnumeration {

	CATALOG(1), SCHEMA(2), TABLES(3), COLUMN(4),SCHEMA_TREE(5);
	private final int data;

	private MetadataEnumeration(int data) {

		this.data = data;
	}

	public int getData() {
		return this.data;
	}

}
