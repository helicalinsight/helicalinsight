package com.helicalinsight.export.dto;

import java.util.List;
import java.util.Map;

import com.helicalinsight.datasource.model.DSExtraOption;
import com.helicalinsight.datasource.model.DSTypeHikari;
import com.helicalinsight.datasource.model.DSTypeJndi;
import com.helicalinsight.datasource.model.DSTypeNoSQL;
import com.helicalinsight.datasource.model.DSTypeTomcat;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;

public class DataSourceWrapper {

	private DSTypeTomcat tomcat;
	private DSTypeHikari hikari;
	private DSTypeJndi jndi;
	private DSTypeNoSQL noSql;
	private Map<Integer,List<GlobalConnectionSecurity>> securities;

   private List<DSExtraOption> extraOptions;  // BUG-7548
    
	public DSTypeTomcat getTomcat() {
		return tomcat;
	}

	public DSTypeHikari getHikari() {
		return hikari;
	}

	public DSTypeJndi getJndi() {
		return jndi;
	}

	public DSTypeNoSQL getNoSql() {
		return noSql;
	}

	public void setTomcat(DSTypeTomcat tomcat) {
		this.tomcat = tomcat;
	}

	public void setHikari(DSTypeHikari hikari) {
		this.hikari = hikari;
	}

	public void setJndi(DSTypeJndi jndi) {
		this.jndi = jndi;
	}

	public void setNoSql(DSTypeNoSQL noSql) {
		this.noSql = noSql;
	}

	public Map<Integer, List<GlobalConnectionSecurity>> getSecurities() {
		return securities;
	}

	public void setSecurities(Map<Integer, List<GlobalConnectionSecurity>> securities) {
		this.securities = securities;
	}

	public List<DSExtraOption> getExtraOptions() {
		return extraOptions;
	}

	public void setExtraOptions(List<DSExtraOption> extraOptions) {
		this.extraOptions = extraOptions;
	}



}
