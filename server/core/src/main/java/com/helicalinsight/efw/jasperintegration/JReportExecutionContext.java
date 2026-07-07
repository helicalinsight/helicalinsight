package com.helicalinsight.efw.jasperintegration;

import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperPrint;

public class JReportExecutionContext {
	
	private final JasperPrint jasperPrint;
	private final JRVirtualizer virtualizer;
	private final AtomicInteger pageCounter;
	private final String format;
	private final String swapDir;
	
	public JReportExecutionContext(JasperPrint jasperPrint, JRVirtualizer virtualizer, AtomicInteger pageCounter, String format, String swapDir) {
		this.jasperPrint = jasperPrint;
		this.virtualizer = virtualizer;
		this.pageCounter = pageCounter;
		this.format = format;
		this.swapDir = swapDir;
	}
	
	public JasperPrint getJasperPrint() {
		return this.jasperPrint;
	}
	
	public void cleanup() {
		if ( virtualizer != null ) {
			virtualizer.cleanup();
		}
	}
	
	public int getTotalPages() {
		return pageCounter.get();
	}
	
	public String getFormat() {
		return format;
	}
	
	public final String getSwapDir() {
		return swapDir;
	}
}
