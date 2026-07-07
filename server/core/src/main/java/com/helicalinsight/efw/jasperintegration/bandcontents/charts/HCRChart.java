package com.helicalinsight.efw.jasperintegration.bandcontents.charts;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import com.helicalinsight.efw.jasperintegration.bandcontents.charts.dataset.DatasetConfiguratorResolver;

import net.sf.jasperreports.charts.design.JRDesignItemLabel;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.charts.util.JRAxisFormat;
import net.sf.jasperreports.engine.JRChartPlot;
import net.sf.jasperreports.engine.JRChartPlot.JRSeriesColor;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.base.JRBaseChartPlot;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignHyperlinkParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;


@Component
public  class HCRChart implements IHCRBandContents  {

	
	private static final String PROPERTY_FORE_COLOR = "foreColor";
	private static final String PROPERTY_BACK_COLOR = "backColor";
	
	private static final String PROPERTY_COLOR = "color";
	private static final String PROPERTY_FONT = "font";
	private static final String PROPERTY_EXPRESSION  = "expression";
	protected static final String PROPERTY_DEFAULT_LABEL_ROTATION = "90.0";
	
	private static final String DEFAULT_EVALUATION_TIME = "Report";
	private static final String DEFAULT_LEGEND_POSITION = "Bottom";
	private static final String DEFAULT_TITLE_POSITION = "Top";
	private static final String DEFAULT_FOREGROUND_APLHA = "1.0";
	private static final String DEFAULT_BACKGROUND_ALPHA = "1.0";
	private static final String DEFAULT_PLOT_ORIENTATION = "Vertical";
	
	
	
	@Autowired
	protected DatasetConfiguratorResolver datasetConfiguratorResolver;
	
	public JRDesignChart buildChart(JsonObject formData, JasperDesign jasperDesign) {
		 throw new EfwServiceException("Not implemented") ;
	 }
	
	
	
	@Override
	public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
		addChartIfAny(band, bandJson, jasperDesign);
	}
	
	private void addChartIfAny(JRDesignBand jrDesignBand, JsonObject bandJson, JasperDesign jasperDesign) {
		if (bandJson.has("chart")) {
			JsonArray chartArray = GsonUtility.optJsonArray(bandJson, "chart");

			if(chartArray == null ) return ;

			for (JsonElement element : chartArray) {
				JsonObject eachChartJson = element.getAsJsonObject();
				String chartType = eachChartJson.get("chartType").getAsString().toLowerCase();
				HCRChart chartBean = (HCRChart) ApplicationContextAccessor.getBean(chartType);
				JRDesignChart chart = chartBean.buildChart(eachChartJson, jasperDesign);
				jrDesignBand.addElement(chart);
			}
		}
	}
	
	
	protected JRDesignChart populateChart(JsonObject formData, JasperDesign jasperDesign) {
		byte chartType = HCRUtils.getChartTypeByName(formData.get("chartType").getAsString());
		JRDesignChart chart = new JRDesignChart(null, chartType);
		configureChartProperties(chart, formData, jasperDesign);
		return chart;
	}
	
	
	
	private  void configureChartProperties(JRDesignChart chart, JsonObject formData, JasperDesign jasperDesign) {
		
		HCRUtils.configureComponentElementProperties(chart, formData);
		configureHyperlink(chart, formData);
	
		JsonObject chartPlot = formData.getAsJsonObject("chartPlot");
		configurePlot(chart, chartPlot.getAsJsonObject("plot"));
		HCRUtils.applyStyleReference(formData, chart::setStyleNameReference);
		
		JsonObject chartJson = formData.getAsJsonObject("chart");

		chart.setRenderType(GsonUtility.optString(chartJson, "renderType"));
		chart.setTheme(GsonUtility.optString(chartJson, "theme"));

		String evaliationTime = GsonUtility.optStringValue(chartJson, "evaluationTime", DEFAULT_EVALUATION_TIME);
		EvaluationTimeEnum evaluationTime = EvaluationTimeEnum.getByName(evaliationTime);
		chart.setEvaluationTime(evaluationTime);
		evaluationGroup(chartJson, jasperDesign, chart, evaliationTime);
		
		configureTitle(chart, chartJson);
		configureSubTitle(chart, chartJson);
		configureChartLegend(chart, chartJson);
		
	}
	
	
	
	private  void  configurePlot(JRDesignChart chart, JsonObject chartPlotJson)  {
		JRChartPlot plot =  chart.getPlot();
		
		Color backColor =  HCRUtils.getColorFromJson(chartPlotJson, PROPERTY_BACK_COLOR);
		plot.setBackcolor(backColor);
		
		plot.setBackgroundAlpha(Float.valueOf(GsonUtility.optStringValue(chartPlotJson, "backgroundAlpha", DEFAULT_BACKGROUND_ALPHA)));
		plot.setForegroundAlpha(Float.valueOf(GsonUtility.optStringValue(chartPlotJson, "foregroundAlpha", DEFAULT_FOREGROUND_APLHA)));
		plot.setOrientation(PlotOrientationEnum.getByName(GsonUtility.optStringValue(chartPlotJson, "orientation", DEFAULT_PLOT_ORIENTATION)));
		plot.setSeriesColors(configureSeriesColors(chartPlotJson));
		
	}
	
	
	private Set<JRSeriesColor> configureSeriesColors(JsonObject chartPlotJson) {
		
		JsonArray colorSeriesJsonArray =  GsonUtility.optJsonArray(chartPlotJson, "seriesColors");
		
		if(colorSeriesJsonArray == null || colorSeriesJsonArray.isEmpty()) {
			return Set.of();
		}
		
		Set<JRSeriesColor> colorSeriesSet = new HashSet<JRSeriesColor>();
		for(JsonElement colorElement : colorSeriesJsonArray ) {
			JsonObject  colorJsonObject =  colorElement.getAsJsonObject();
			int seriesOrder =  colorJsonObject.get("seriesOrder").getAsInt();
			Color color = HCRUtils.getColorFromJson(colorJsonObject, PROPERTY_COLOR);
			JRSeriesColor seriesColor = new JRBaseChartPlot.JRBaseSeriesColor(seriesOrder, color);
			colorSeriesSet.add(seriesColor);
		}
		return colorSeriesSet;
	}
	
	
	
	public  JRAxisFormat configureAxisFormat(JsonObject axisFormat) {
		
		JRAxisFormat jrAxisFormat = new JRAxisFormat();
		jrAxisFormat.setLabelFont(HCRUtils.getFontFromJson(axisFormat, "labelFont"));
		jrAxisFormat.setTickLabelFont(HCRUtils.getFontFromJson(axisFormat, "tickLabelFont"));
		jrAxisFormat.setLabelColor(HCRUtils.getColorFromJson(axisFormat, "labelColor"));
		jrAxisFormat.setLineColor(HCRUtils.getColorFromJson(axisFormat, "axisLineColor"));
		jrAxisFormat.setTickLabelColor(HCRUtils.getColorFromJson(axisFormat, "tickLabelColor"));
		jrAxisFormat.setTickLabelMask(GsonUtility.optStringValue(axisFormat, "tickLabelMask", ""));
		jrAxisFormat.setVerticalTickLabel(GsonUtility.optBooleanValue(axisFormat, "verticalTickLabels", false));
		
		return jrAxisFormat;
	}
	
	
	protected JRDesignItemLabel  configureItemLabels(JRDesignItemLabel itemLabel, JsonObject itemLabelJson) {
		itemLabel.setFont(HCRUtils.getFontFromJson(itemLabelJson, PROPERTY_FONT));
		itemLabel.setBackgroundColor(HCRUtils.getColorFromJson(itemLabelJson, "backgroundColor"));
		itemLabel.setColor(HCRUtils.getColorFromJson(itemLabelJson, PROPERTY_COLOR));
		return itemLabel;
	}
	
	
	private  void configureHyperlink(JRDesignChart chart, JsonObject formData) {
		
		JsonObject hyperlinkJson = GsonUtility.optJsonObject(formData ,  "hyperlink");
		
		if (hyperlinkJson == null ) return ;
		
		JRDesignExpression anchorNameExpression = HCRUtils.getExpressionFromJson(hyperlinkJson, "anchorNameExpression");
		chart.setAnchorNameExpression(anchorNameExpression);

		JRDesignExpression bookmarkLevelExpression = HCRUtils.getExpressionFromJson(hyperlinkJson, "bookmarkLevelExpression");
		chart.setBookmarkLevelExpression(bookmarkLevelExpression);
		
		chart.setBookmarkLevel(GsonUtility.optIntValue(hyperlinkJson, "bookmarkLevel", 0));
		chart.setLinkTarget(GsonUtility.optString(hyperlinkJson, "linkTarget"));
		chart.setLinkType(GsonUtility.optString(hyperlinkJson, "linkType"));
		
	
		JRDesignExpression hyperlinkReferenceExpression = HCRUtils.getExpressionFromJson(hyperlinkJson, "hyperlinkReferenceExpression");
		chart.setHyperlinkReferenceExpression(hyperlinkReferenceExpression);
		
		JRDesignExpression hyperlinkAnchorExpression = HCRUtils.getExpressionFromJson(hyperlinkJson, "hyperlinkAnchorExpression");
		chart.setHyperlinkAnchorExpression(hyperlinkAnchorExpression);
		
		JRDesignExpression hyperlinkPageExpression = HCRUtils.getExpressionFromJson(hyperlinkJson, "hyperlinkPageExpression");
		chart.setHyperlinkPageExpression(hyperlinkPageExpression);
		
		JRDesignExpression hyperlinkWhenExpression = HCRUtils.getExpressionFromJson(hyperlinkJson, "hyperlinkWhenExpression");
		chart.setHyperlinkWhenExpression(hyperlinkWhenExpression);
		
		JRDesignExpression hyperlinkTooltipExpression = HCRUtils.getExpressionFromJson(hyperlinkJson, "hyperlinkTooltipExpression");
		chart.setHyperlinkTooltipExpression(hyperlinkTooltipExpression);

		
		JsonArray parameterArray  = GsonUtility.optJsonArray(hyperlinkJson, "parameters");
		
		if(parameterArray == null || parameterArray.isEmpty()) return ;
		
		List<JRHyperlinkParameter>  paramList = new ArrayList<>();
		for(JsonElement parameterElement  : parameterArray ) {
			JsonObject parameterJson = parameterElement.getAsJsonObject();
			JRDesignHyperlinkParameter parameter = new JRDesignHyperlinkParameter();
			parameter.setName(parameterJson.get("name").getAsString());
			JRDesignExpression expression = new JRDesignExpression();
			expression.setText(parameterJson.get(PROPERTY_EXPRESSION).getAsString());
			parameter.setValueExpression(expression);
			paramList.add(parameter);
		}
		chart.getHyperlinkParametersList().addAll(paramList);
	}
	
	private void configureTitle(JRDesignChart chart,  JsonObject chartJson) {
		JsonObject chartTitleJson = GsonUtility.optJsonObject(chartJson ,  "chartTitle");
		if (chartTitleJson == null ) return ;
		chart.setTitleExpression(HCRUtils.getExpressionFromJson(chartTitleJson,PROPERTY_EXPRESSION));
		chart.setTitleColor(HCRUtils.getColorFromJson(chartTitleJson, PROPERTY_COLOR));
		chart.setTitleFont(HCRUtils.getFontFromJson(chartTitleJson, PROPERTY_FONT));
		chart.setTitlePosition(EdgeEnum.getByName(GsonUtility.optStringValue(chartJson, "position", DEFAULT_TITLE_POSITION)));
	}
	
	
	private void configureSubTitle(JRDesignChart chart,  JsonObject chartJson) {
		JsonObject chartSubTitleJson = GsonUtility.optJsonObject(chartJson ,  "chartSubtitle");
		if (chartSubTitleJson == null ) return ;
		chart.setSubtitleExpression(HCRUtils.getExpressionFromJson(chartSubTitleJson,PROPERTY_EXPRESSION));
		chart.setSubtitleColor(HCRUtils.getColorFromJson(chartSubTitleJson, PROPERTY_COLOR));
		chart.setSubtitleFont(HCRUtils.getFontFromJson(chartSubTitleJson, PROPERTY_FONT));
	}
	
	private void configureChartLegend(JRDesignChart chart,  JsonObject chartJson) {
		JsonObject chartLegendJson = GsonUtility.optJsonObject(chartJson ,  "chartLegend");
		if (chartLegendJson == null ) return ;
		chart.setLegendFont(HCRUtils.getFontFromJson(chartLegendJson, PROPERTY_FONT));
		chart.setShowLegend(GsonUtility.optBooleanValue(chartLegendJson, "showLegend", true));
		chart.setLegendColor(HCRUtils.getColorFromJson(chartLegendJson, PROPERTY_FORE_COLOR));
		chart.setLegendBackgroundColor(HCRUtils.getColorFromJson(chartLegendJson,PROPERTY_BACK_COLOR));
		chart.setLegendPosition(EdgeEnum.getByName(GsonUtility.optStringValue(chartLegendJson, "position", DEFAULT_LEGEND_POSITION)));
	}
	
}
