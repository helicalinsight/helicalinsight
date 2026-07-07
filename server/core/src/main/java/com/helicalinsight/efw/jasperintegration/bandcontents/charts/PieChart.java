package com.helicalinsight.efw.jasperintegration.bandcontents.charts;

import org.springframework.stereotype.Component;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.jasperintegration.bandcontents.charts.dataset.DatasetConfigurator;

import net.sf.jasperreports.charts.design.JRDesignItemLabel;
import net.sf.jasperreports.charts.design.JRDesignPiePlot;
import net.sf.jasperreports.engine.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JRDesignChartDataset;
import net.sf.jasperreports.engine.design.JasperDesign;

@Component("pie")
public class PieChart extends HCRChart {
	
	
	public JRDesignChart buildChart(JsonObject formData, JasperDesign jasperDesign) {
		
		JRDesignChart pieChart = populateChart(formData, jasperDesign);
		JRDesignPiePlot piePlot = (JRDesignPiePlot) pieChart.getPlot();
		
		JsonObject pieChartPlotJson = formData.getAsJsonObject("chartPlot");
		JRDesignItemLabel itemLabel =  (JRDesignItemLabel) piePlot.getItemLabel();		
		piePlot.setItemLabel(configureItemLabels(itemLabel, pieChartPlotJson.getAsJsonObject("itemLabel")));
		JsonObject labels =  GsonUtility.optJsonObject(pieChartPlotJson, "labels");	
		if ( labels != null ) {
			piePlot.setLabelFormat(GsonUtility.optString(labels, "labelFormat"));
			piePlot.setShowLabels(GsonUtility.optBoolean(labels, "showLabels"));
			piePlot.setCircular(GsonUtility.optBoolean(labels, "circular"));
			piePlot.setLegendLabelFormat(GsonUtility.optString(labels, "legendLabelFormat"));
		}
		
		JRDesignChartDataset pieDataset = (JRDesignChartDataset) pieChart.getDataset();
		DatasetConfigurator<JRDesignChartDataset> datasetConfigurator =  datasetConfiguratorResolver.resolve( pieDataset);
		pieDataset = datasetConfigurator.configure(pieDataset,  formData.getAsJsonObject("dataSet"));
		pieChart.setDataset(pieDataset);
		return pieChart;
	 }

}
