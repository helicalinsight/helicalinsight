package com.helicalinsight.adhoc;

public class MetadataDriverReferences {
    private String dialect;
    private String driverClass;
	private String driverClassReference;
    private Integer globalConnectionId;
    private Integer efwId;
    private String connectionType;
    
    public MetadataDriverReferences() {
    	
    }
    
    public MetadataDriverReferences(String dialect, String driverClass, String driverClassReference,
			Integer globalConnectionId, Integer efwId) {
		this.dialect = dialect;
		this.driverClass = driverClass;
		this.driverClassReference = driverClassReference;
		this.globalConnectionId = globalConnectionId;
		this.efwId = efwId;
	}
    
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public Integer getEfwId() {
		return efwId;
	}
	public void setEfwId(Integer efwId) {
		this.efwId = efwId;
	}
    
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		if(dialect.equals("org.hibernate.dialect.DerbyTenSevenDialect")){
			dialect="org.hibernate.dialect.DerbyDialect";
		}
		this.dialect = dialect;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String getDriverClassReference() {
		return driverClassReference;
	}
	public void setDriverClassReference(String driverClassReference) {
		this.driverClassReference = driverClassReference;
	}
	public Integer getGlobalConnectionId() {
		return globalConnectionId;
	}
	public void setGlobalConnectionId(Integer globalConnectionId) {
		this.globalConnectionId = globalConnectionId;
	}
}
