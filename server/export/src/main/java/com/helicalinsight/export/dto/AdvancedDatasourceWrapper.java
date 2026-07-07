package com.helicalinsight.export.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.helicalinsight.admin.dto.EFWDConnGroovyDTO;
import com.helicalinsight.admin.dto.EFWDConnSqlJDBCDTO;
import com.helicalinsight.admin.dto.HIEfwdConnSecurityDTO;

public class AdvancedDatasourceWrapper {

	private List<EFWDConnSqlJDBCDTO> jdbc = new ArrayList<>();
	private List<EFWDConnGroovyDTO> groovy = new ArrayList<>();
	private Map<Integer, List<HIEfwdConnSecurityDTO>> securities =  new HashMap<>();
	public List<EFWDConnSqlJDBCDTO> getJdbc() {
		return jdbc;
	}
	public List<EFWDConnGroovyDTO> getGroovy() {
		return groovy;
	}
	public Map<Integer, List<HIEfwdConnSecurityDTO>> getSecurities() {
		return securities;
	}
	public void setJdbc(List<EFWDConnSqlJDBCDTO> jdbc) {
		this.jdbc = jdbc;
	}
	public void setGroovy(List<EFWDConnGroovyDTO> groovy) {
		this.groovy = groovy;
	}
	public void setSecurities(Map<Integer, List<HIEfwdConnSecurityDTO>> securities) {
		this.securities = securities;
	}
	
}
