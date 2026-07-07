package com.helicalinsight.efw.jasperintegration.bandcontents.charts.dataset;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.HCRUtils;

import net.sf.jasperreports.charts.design.JRDesignPieDataset;
import net.sf.jasperreports.charts.design.JRDesignPieSeries;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignExpression;

@Component
public class PieDatasetConfigurator implements DatasetConfigurator<JRDesignPieDataset> {

	
	@Override
	public JRDesignPieDataset configure(JRDesignPieDataset dataset, JsonObject datasetJson) {
		
		JRDesignExpression keyExpression = HCRUtils.getExpressionFromJson(datasetJson, "keyExpression");
		JRDesignExpression valueExpression = HCRUtils.getExpressionFromJson(datasetJson, "valueExpression");
		JRDesignExpression labelExpression  = HCRUtils.getExpressionFromJson(datasetJson, "labelExpression");
		
		
		JRDesignPieSeries jrDesignPieSeries = new JRDesignPieSeries();
		jrDesignPieSeries.setKeyExpression(keyExpression);
		jrDesignPieSeries.setLabelExpression(labelExpression);
		jrDesignPieSeries.setValueExpression(valueExpression);
		
		dataset.getSeriesList().add(jrDesignPieSeries);
		
		JsonObject datasetRunJson = datasetJson.getAsJsonObject("dataSetRun");
		JRDesignDatasetRun datasetRun = configureDatasetRun(datasetRunJson);
		dataset.setDatasetRun(datasetRun);
		
		return dataset;
	}

	@Override
	public Class<JRDesignPieDataset> getDatasetType() {
		return JRDesignPieDataset.class;
	}

}
