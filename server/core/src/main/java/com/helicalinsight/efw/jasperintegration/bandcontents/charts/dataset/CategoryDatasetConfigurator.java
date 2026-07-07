package com.helicalinsight.efw.jasperintegration.bandcontents.charts.dataset;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.HCRUtils;

import net.sf.jasperreports.charts.design.JRDesignCategoryDataset;
import net.sf.jasperreports.charts.design.JRDesignCategorySeries;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignExpression;

@Component
public class CategoryDatasetConfigurator implements DatasetConfigurator<JRDesignCategoryDataset> {

	@Override
	public JRDesignCategoryDataset configure(JRDesignCategoryDataset categoryDataset, JsonObject datasetJson) {
		
		JsonObject categorySeries =  datasetJson.getAsJsonObject("categorySeries");
		JRDesignCategorySeries series = new JRDesignCategorySeries();
		
		JRDesignExpression categoryExpression = HCRUtils.getExpressionFromJson(categorySeries, "categoryExpression");
		series.setCategoryExpression(categoryExpression);
		

		JRDesignExpression labelExpression = HCRUtils.getExpressionFromJson(categorySeries, "labelExpression");
		series.setLabelExpression(labelExpression);

		
		JRDesignExpression seriesExpression = HCRUtils.getExpressionFromJson(categorySeries, "seriesExpression");
		series.setSeriesExpression(seriesExpression);
		
		JRDesignExpression valueExpression = HCRUtils.getExpressionFromJson(categorySeries, "valueExpression");
		series.setValueExpression(valueExpression);
		
		categoryDataset.addCategorySeries(series);
		
		JsonObject datasetRunJson = datasetJson.getAsJsonObject("dataSetRun");

		JRDesignDatasetRun datasetRun = configureDatasetRun(datasetRunJson);
		
		categoryDataset.setDatasetRun(datasetRun);
		
		return categoryDataset;
	}
	
	
	@Override
	public Class<JRDesignCategoryDataset> getDatasetType() {
		return JRDesignCategoryDataset.class;
	}

}
