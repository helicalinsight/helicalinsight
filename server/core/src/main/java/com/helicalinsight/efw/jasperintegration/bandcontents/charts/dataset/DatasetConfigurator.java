package com.helicalinsight.efw.jasperintegration.bandcontents.charts.dataset;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.HCRUtils;

import net.sf.jasperreports.engine.design.JRDesignChartDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;

public interface DatasetConfigurator<T extends JRDesignChartDataset> {
	
	T configure(T dataset, JsonObject datasetJson);
	
	Class<T> getDatasetType();
	
	default JRDesignDatasetRun configureDatasetRun(JsonObject datasetRunJson) {
		return HCRUtils.configureSubDatasetRun(datasetRunJson);
	}
}
