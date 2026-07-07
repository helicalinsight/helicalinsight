package com.helicalinsight.efw.jasperintegration.bandcontents.charts;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.jasperintegration.bandcontents.charts.dataset.DatasetConfigurator;

import net.sf.jasperreports.charts.design.JRDesignBarPlot;
import net.sf.jasperreports.charts.design.JRDesignItemLabel;
import net.sf.jasperreports.charts.util.JRAxisFormat;
import net.sf.jasperreports.engine.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JRDesignChartDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;

@Component("bar")
public class BarChart  extends HCRChart  {
	
	@Override
	public JRDesignChart buildChart(JsonObject formData, JasperDesign jasperDesign) {
		
		JRDesignChart barChart = populateChart(formData, jasperDesign);
		
		JRDesignBarPlot barPlot = (JRDesignBarPlot) barChart.getPlot();
		
		JsonObject barChartPlotJson = formData.getAsJsonObject("chartPlot");
		
		
		barPlot.setShowLabels(GsonUtility.optBooleanValue(barChartPlotJson, "showLabels",false));
		barPlot.setShowTickLabels(GsonUtility.optBooleanValue(barChartPlotJson, "showTickLabels",true));
		barPlot.setShowTickMarks(GsonUtility.optBooleanValue(barChartPlotJson, "showTickMarks",true));
		
		setAxisFormatIfPresent(barChartPlotJson, "categoryAxisFormat", true, barPlot);
		setAxisFormatIfPresent(barChartPlotJson, "valueAxisFormat", false, barPlot);
		
		JRDesignItemLabel itemLabel =  (JRDesignItemLabel) barPlot.getItemLabel();
		barPlot.setItemLabel(configureItemLabels(itemLabel, barChartPlotJson.getAsJsonObject("itemLabel")));
		
		JRDesignChartDataset dataset =  (JRDesignChartDataset) barChart.getDataset();
		DatasetConfigurator<JRDesignChartDataset> datasetConfigurator =  datasetConfiguratorResolver.resolve( dataset);
		dataset = datasetConfigurator.configure(dataset,  formData.getAsJsonObject("dataSet"));
		barChart.setDataset(dataset);
		return barChart;
	}
		
	private void setAxisFormatIfPresent(JsonObject plotJson, String key, boolean isCategory, JRDesignBarPlot barPlot) {
		if (plotJson.has(key)) {
			JsonObject axisFormatJson = plotJson.getAsJsonObject(key).getAsJsonObject("axisFormat");
			JRAxisFormat axisFormat = configureAxisFormat(axisFormatJson);
			
			if (isCategory) {
				
				barPlot.setCategoryAxisTickLabelRotation(Double.valueOf(GsonUtility.optStringValue(axisFormatJson, "tickLabelRotation", PROPERTY_DEFAULT_LABEL_ROTATION)));
				
				JRDesignExpression categoryAxisLabelExpression =  HCRUtils.getExpressionFromJson(axisFormatJson, "categoryAxisLabelExpression");
				barPlot.setCategoryAxisLabelExpression(categoryAxisLabelExpression);
				
				JRDesignExpression domainAxisMaxValueExpression =  HCRUtils.getExpressionFromJson(axisFormatJson, "domainAxisMaxValueExpression");
				barPlot.setDomainAxisMaxValueExpression(domainAxisMaxValueExpression);
				
				JRDesignExpression domainAxisMinValueExpression =  HCRUtils.getExpressionFromJson(axisFormatJson, "domainAxisMinValueExpression");
				barPlot.setDomainAxisMinValueExpression(domainAxisMinValueExpression);
				
				barPlot.setCategoryAxisFormat(axisFormat);
			} else {
				
				JRDesignExpression valueAxisLabelExpression =  HCRUtils.getExpressionFromJson(axisFormatJson, "valueAxisLabelExpression");
				barPlot.setValueAxisLabelExpression(valueAxisLabelExpression);
				
				JRDesignExpression rangeAxisMaxValueExpression =  HCRUtils.getExpressionFromJson(axisFormatJson, "rangeAxisMaxValueExpression");
				barPlot.setRangeAxisMaxValueExpression(rangeAxisMaxValueExpression);
				
				JRDesignExpression rangeAxisMinValueExpression =  HCRUtils.getExpressionFromJson(axisFormatJson, "rangeAxisMinValueExpression");
				barPlot.setRangeAxisMinValueExpression(rangeAxisMinValueExpression);
				
				barPlot.setValueAxisFormat(axisFormat);
			}
		}
	}

}
