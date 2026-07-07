package com.helicalinsight.adhoc.jreport;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.fill.JRBaseFiller;

public class PrintContext {

	private Map<String, Object> parameters;
	private JRBaseFiller filler;
	private JRDataSource dataSource;
	private JRVirtualizer virtualizer;
	private String swapDir;

	public PrintContext(JRBaseFiller filler, Map<String, Object> parameters, JRDataSource dataSource, JRVirtualizer virtualizer, String swapDir) {
		this.filler = filler;
		this.parameters = parameters;
		this.dataSource = dataSource;
		this.virtualizer = virtualizer;
		this.swapDir = swapDir;
	}

	public JRBaseFiller getFiller() {
		return filler;
	}

	public JRDataSource getDataSource() {
		return dataSource;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public JRVirtualizer getVirtualizer() {
		return virtualizer;
	}
	
	public final String getSwapDir() {
		return swapDir;
	}
}
