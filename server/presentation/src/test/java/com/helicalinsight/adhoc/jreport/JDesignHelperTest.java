package com.helicalinsight.adhoc.jreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.efw.jasperintegration.BandFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JasperDesign;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDesignHelperTest {

	@Test
	public void ut_a1_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		JsonArray groups = new JsonArray();
		JsonObject eachGroupJson = new JsonObject();
		JsonObject countVariable = new JsonObject();
		countVariable.addProperty("name", "countName");
		countVariable.addProperty("incrementType", "Group");
		countVariable.addProperty("incrementGroup", "incrementGroup");
		countVariable.addProperty("incrementFactoryClass", "incrementFactoryClass");
		countVariable.addProperty("className", "className");
		countVariable.addProperty("calculation", "calculation");
		countVariable.addProperty("resetType", "Group");
		countVariable.addProperty("resetGroup", "resetGroup");
		countVariable.addProperty("expression", "expression");
		countVariable.addProperty("expressionType", "expressionType");
		countVariable.addProperty("isSystemDefined", true);
		countVariable.addProperty("initialValueExpression", "initialValueExpression");

		eachGroupJson.add("countVariable", countVariable);
		groups.add(eachGroupJson);
		designerPropertiesJson.add("groups", groups);
		JsonArray designerStyle = new JsonArray();
		JsonObject eachStyleJson = new JsonObject();
		eachStyleJson.addProperty("isItalic", true);
		eachStyleJson.addProperty("isUnderline", true);
		eachStyleJson.addProperty("strikeThrough", true);
		eachStyleJson.addProperty("isBold", true);
		eachStyleJson.addProperty("foreColor", "#FF0000");
		eachStyleJson.addProperty("fontSize", 12.34f);
		eachStyleJson.addProperty("fontName", "fontName");
		eachStyleJson.addProperty("name", "name");
		eachStyleJson.addProperty("mode", "mode");
		eachStyleJson.addProperty("backColor", "#00FFFF");
		eachStyleJson.addProperty("rotationType", "rotationType");
		eachStyleJson.addProperty("horizontalTextAlign", "horizontalTextAlign");
		eachStyleJson.addProperty("pattern", "pattern");
		eachStyleJson.addProperty("parentStyleNameReference", "parentStyleNameReference");
		JsonObject borderJson = new JsonObject();
		borderJson.addProperty("padding", "padding");
		JsonObject line = new JsonObject();
		JsonObject leftLine = new JsonObject();
		leftLine.addProperty("lineWidth", 50.50f);
		leftLine.addProperty("lineColor", "#000000");
		leftLine.addProperty("lineStyle", "lineStyle");
		line.add("leftLine", leftLine);
		line.add("rightLine", leftLine);
		line.add("bottomLine", leftLine);
		line.add("topLine", leftLine);
		line.add("pen", leftLine);
		borderJson.add("line", line);
		eachStyleJson.add("border", borderJson);

		JsonObject lineStyle = new JsonObject();
		lineStyle.addProperty("penLineWidth", 25);
		lineStyle.addProperty("lineStyle", "lineStyle");
		eachStyleJson.add("lineStyle", lineStyle);
		JsonObject conditionalStyleJson = new JsonObject();
		conditionalStyleJson.addProperty("pattern", "pattern");
		conditionalStyleJson.addProperty("horizontalTextAlign", "horizontalTextAlign");
		conditionalStyleJson.addProperty("backcolor", "#FFFF00");
		conditionalStyleJson.addProperty("rotationType", "rotationType");
		conditionalStyleJson.addProperty("expression", "expression");
		eachStyleJson.add("conditionalStyle", conditionalStyleJson);
		designerStyle.add(eachStyleJson);
		designerPropertiesJson.add("designerStyle", designerStyle);
		formData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);
		JasperDesign createDesigner = designHelper.createDesigner();
		assertNotNull(createDesigner);
	}

	@Test
	public void ut_a2_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		JsonArray groups = new JsonArray();
		JsonObject eachGroupJson = new JsonObject();
		JsonObject countVariable = new JsonObject();
		countVariable.addProperty("name", "countName");
		countVariable.addProperty("incrementType", "Group");
		countVariable.addProperty("incrementGroup", "incrementGroup");
		countVariable.addProperty("incrementFactoryClass", "incrementFactoryClass");
		countVariable.addProperty("className", "className");
		countVariable.addProperty("calculation", "calculation");
		countVariable.addProperty("resetType", "Group");
		countVariable.addProperty("resetGroup", "resetGroup");
		countVariable.addProperty("expression", "expression");
		countVariable.addProperty("expressionType", "expressionType");
		countVariable.addProperty("isSystemDefined", true);
		countVariable.addProperty("initialValueExpression", "initialValueExpression");

		eachGroupJson.add("countVariable", countVariable);
		groups.add(eachGroupJson);
		designerPropertiesJson.add("groups", groups);
		JsonArray designerStyle = new JsonArray();
		JsonObject eachStyleJson = new JsonObject();
		eachStyleJson.addProperty("isItalic", false);
		eachStyleJson.addProperty("isUnderline", false);
		eachStyleJson.addProperty("strikeThrough", false);
		eachStyleJson.addProperty("isBold", false);
		eachStyleJson.addProperty("foreColor", "#FF0000");
		eachStyleJson.addProperty("fontSize", 12.34f);
		eachStyleJson.addProperty("fontName", "fontName");
		eachStyleJson.addProperty("name", "name");
		eachStyleJson.addProperty("mode", "mode");
		eachStyleJson.addProperty("backColor", "#00FFFF");
		eachStyleJson.addProperty("rotationType", "rotationType");
		eachStyleJson.addProperty("horizontalTextAlign", "horizontalTextAlign");
		eachStyleJson.addProperty("pattern", "pattern");
		eachStyleJson.addProperty("parentStyleNameReference", "parentStyleNameReference");
		JsonObject borderJson = new JsonObject();
		borderJson.addProperty("padding", "padding");
		JsonObject line = new JsonObject();
		JsonObject leftLine = new JsonObject();
		leftLine.addProperty("lineWidth", 50.50f);
		leftLine.addProperty("lineColor", "#000000");
		leftLine.addProperty("lineStyle", "lineStyle");
		line.add("leftLine", leftLine);
		line.add("rightLine", leftLine);
		line.add("bottomLine", leftLine);
		line.add("topLine", leftLine);
		line.add("pen", leftLine);
		borderJson.add("line", line);
		eachStyleJson.add("border", borderJson);

		JsonObject lineStyle = new JsonObject();
		lineStyle.addProperty("penLineWidth", 25);
		lineStyle.addProperty("lineStyle", "lineStyle");
		eachStyleJson.add("lineStyle", lineStyle);
		JsonObject conditionalStyleJson = new JsonObject();
		conditionalStyleJson.addProperty("pattern", "pattern");
		conditionalStyleJson.addProperty("horizontalTextAlign", "horizontalTextAlign");
		conditionalStyleJson.addProperty("backcolor", "#FFFF00");
		conditionalStyleJson.addProperty("rotationType", "rotationType");
		conditionalStyleJson.addProperty("expression", "expression");
		eachStyleJson.add("conditionalStyle", conditionalStyleJson);
		designerStyle.add(eachStyleJson);
		designerPropertiesJson.add("designerStyle", designerStyle);
		formData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);
		JasperDesign createDesigner = designHelper.createDesigner();
		assertNotNull(createDesigner);
	}

	@Test
	public void ut_a3_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		JsonArray groups = new JsonArray();
		JsonObject eachGroupJson = new JsonObject();
		JsonObject countVariable = new JsonObject();
		countVariable.addProperty("name", "countName");
		countVariable.addProperty("incrementType", "Group");
		countVariable.addProperty("incrementGroup", "incrementGroup");
		countVariable.addProperty("incrementFactoryClass", "incrementFactoryClass");
		countVariable.addProperty("className", "className");
		countVariable.addProperty("calculation", "calculation");
		countVariable.addProperty("resetType", "Group");
		countVariable.addProperty("resetGroup", "resetGroup");
		countVariable.addProperty("expression", "expression");
		countVariable.addProperty("expressionType", "expressionType");
		countVariable.addProperty("isSystemDefined", true);
		countVariable.addProperty("initialValueExpression", "initialValueExpression");

		eachGroupJson.add("countVariable", countVariable);
		eachGroupJson.addProperty("expression", "expression");
		eachGroupJson.addProperty("name", "name");
		eachGroupJson.addProperty("footerPosition", "footerPosition");
		eachGroupJson.addProperty("keepTogether", true);
		eachGroupJson.addProperty("minDetailsToStartFromTop", 50);
		eachGroupJson.addProperty("preventOrphanFooter", true);
		eachGroupJson.addProperty("minHeightToStartNewPage", 50);
		eachGroupJson.addProperty("reprintHeaderOnEachColumn", true);
		eachGroupJson.addProperty("reprintHeaderOnEachPage", true);
		eachGroupJson.addProperty("resetPageNumber", true);
		eachGroupJson.addProperty("startNewColumn", true);
		eachGroupJson.addProperty("startNewPage", true);
		JsonObject groupHeader = new JsonObject();
		groupHeader.addProperty("key", "value");
		eachGroupJson.add("groupHeader", groupHeader);
		JsonObject groupFooter = new JsonObject();
		JsonObject groupFooterBandBreak = new JsonObject();
		groupFooterBandBreak.addProperty("printWhenExpression", "printWhenExpression");
		groupFooter.add("groupFooterBandBreak", groupFooterBandBreak);
		eachGroupJson.add("groupFooter", groupFooter);
		groups.add(eachGroupJson);
		designerPropertiesJson.add("groups", groups);

		formData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);

		JRDesignBand jrDesignBand = mock(JRDesignBand.class);
		try (MockedConstruction<BandFactory> construction = mockConstruction(BandFactory.class, (mock, context) -> {
			when(mock.getBand()).thenReturn(jrDesignBand);
		})) {
			JasperDesign createDesigner = designHelper.createDesigner();
			assertNotNull(createDesigner);
		}
	}

	@Test
	public void ut_a4_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);

		JsonObject titleJson = new JsonObject();

		titleJson.addProperty("key", "value");
		designerPropertiesJson.add("title", titleJson);
		designerPropertiesJson.addProperty("applyCustomSettings", true);
		JsonObject customSettings = new JsonObject();
		customSettings.addProperty("key", "value");
		designerPropertiesJson.add("customSettings", customSettings);
		designerPropertiesJson.addProperty("reportScriptletClassName", "reportScriptletClassName");
		designerPropertiesJson.addProperty("printOrder", "printOrder");
		designerPropertiesJson.addProperty("language", "language");
		JsonArray importsArray = new JsonArray();
		importsArray.add("temp");
		designerPropertiesJson.add("imports", importsArray);
		designerPropertiesJson.addProperty("temp", "temp");
		designerPropertiesJson.addProperty("whenNoDataType", "whenNoDataType");
		designerPropertiesJson.addProperty("ignorePagination", true);
		formData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);
		JRDesignBand jrDesignBand = mock(JRDesignBand.class);
		try (MockedConstruction<BandFactory> construction = mockConstruction(BandFactory.class, (mock, context) -> {
			when(mock.getBand()).thenReturn(jrDesignBand);
		})) {
			JasperDesign createDesigner = designHelper.createDesigner();
			assertNotNull(createDesigner);
		}
	}

	@Test
	public void ut_a5_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		JsonArray dataSets = new JsonArray();
		JsonObject eachJson2 = new JsonObject();
		JsonObject efwdJson = new JsonObject();
		eachJson2.add("connectionDetails", efwdJson);
		eachJson2.addProperty("isMainDataset", true);
		eachJson2.addProperty("name", "name");
		eachJson2.add("fields", fields);
		JsonArray parametersJson = new JsonArray();
		eachJson2.add("parameters", parametersJson);
		JsonArray variables = new JsonArray();
		JsonObject eachVariableJson = new JsonObject();
		eachVariableJson.addProperty("name", "name");
		eachVariableJson.addProperty("incrementType", "Group");
		eachVariableJson.addProperty("incrementGroup", "incrementGroup");
		eachVariableJson.addProperty("incrementFactoryClass", "incrementFactoryClass");
		eachVariableJson.addProperty("className", "className");
		eachVariableJson.addProperty("calculation", "calculation");
		eachVariableJson.addProperty("resetType", "Group");
		eachVariableJson.addProperty("resetGroup", "resetGroup");
		eachVariableJson.addProperty("expression", "expression");
		eachVariableJson.addProperty("expressionType", "expressionType");
		eachVariableJson.addProperty("isSystemDefined", true);
		eachVariableJson.addProperty("initialValueExpression", "initialValueExpression");

		variables.add(eachVariableJson);

		eachJson2.add("variables", variables);
		dataSets.add(eachJson2);
		designerPropertiesJson.add("dataSets", dataSets);

		formData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);
		JsonObject dataMapTagContent = new JsonObject();
		dataMapTagContent.addProperty("Parameters", "Parameters");
		;
		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getDataMapTagContent()).thenReturn(dataMapTagContent);
					when(mock.getUnProcessedQuery()).thenReturn("${query}");
				})) {
			JasperDesign createDesigner = designHelper.createDesigner();
			assertNotNull(createDesigner);
		}
	}

	@Test
	public void ut_a6_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		JsonArray dataSets = new JsonArray();
		JsonObject eachJson2 = new JsonObject();
		JsonObject efwdJson = new JsonObject();
		eachJson2.add("connectionDetails", efwdJson);
		eachJson2.addProperty("isMainDataset", true);
		eachJson2.addProperty("name", "name");
		eachJson2.add("fields", fields);
		JsonArray parametersJson = new JsonArray();
		JsonObject eachParameterJson = new JsonObject();
		eachParameterJson.addProperty("name", "name");
		eachParameterJson.addProperty("className", "className");
		eachParameterJson.addProperty("isPrompt", true);
		eachParameterJson.addProperty("evaluationTime", "evaluationTime");
		eachParameterJson.addProperty("defaultExpression", "defaultExpression");

		parametersJson.add(eachParameterJson);
		eachJson2.add("parameters", parametersJson);
		JsonArray variables = new JsonArray();
		JsonObject eachVariableJson = new JsonObject();
		eachVariableJson.addProperty("name", "name");
		eachVariableJson.addProperty("incrementType", "Group");
		eachVariableJson.addProperty("incrementGroup", "incrementGroup");
		eachVariableJson.addProperty("incrementFactoryClass", "incrementFactoryClass");
		eachVariableJson.addProperty("className", "className");
		eachVariableJson.addProperty("calculation", "calculation");
		eachVariableJson.addProperty("resetType", "Group");
		eachVariableJson.addProperty("resetGroup", "resetGroup");
		eachVariableJson.addProperty("expression", "expression");
		eachVariableJson.addProperty("expressionType", "expressionType");
		eachVariableJson.addProperty("isSystemDefined", true);
		eachVariableJson.addProperty("initialValueExpression", "initialValueExpression");

		variables.add(eachVariableJson);
		eachJson2.add("variables", variables);
		dataSets.add(eachJson2);
		designerPropertiesJson.add("dataSets", dataSets);

		formData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);
		JsonObject dataMapTagContent = new JsonObject();
		dataMapTagContent.addProperty("Parameters", "Parameters");
		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getDataMapTagContent()).thenReturn(dataMapTagContent);
					when(mock.getUnProcessedQuery()).thenReturn("${query}");
				})) {
			JasperDesign createDesigner = designHelper.createDesigner();
			assertNotNull(createDesigner);
		}
	}

	@Test
	public void ut_a7_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		JsonArray dataSets = new JsonArray();
		JsonObject eachJson2 = new JsonObject();
		JsonObject efwdJson = new JsonObject();
		eachJson2.add("connectionDetails", efwdJson);
		eachJson2.addProperty("isMainDataset", true);
		eachJson2.addProperty("name", "name");
		eachJson2.add("fields", fields);
		JsonArray parametersJson = new JsonArray();
		JsonObject eachParameterJson = new JsonObject();
		eachParameterJson.addProperty("name", "name");
		eachParameterJson.addProperty("className", "className");
		eachParameterJson.addProperty("isPrompt", true);
		eachParameterJson.addProperty("evaluationTime", "evaluationTime");
		eachParameterJson.addProperty("defaultExpression", "defaultExpression");
		eachParameterJson.addProperty("expressionType", "simpleText");

		parametersJson.add(eachParameterJson);
		eachJson2.add("parameters", parametersJson);
		JsonArray variables = new JsonArray();
		JsonObject eachVariableJson = new JsonObject();
		eachVariableJson.addProperty("name", "name");
		eachVariableJson.addProperty("incrementType", "Group");
		eachVariableJson.addProperty("incrementGroup", "incrementGroup");
		eachVariableJson.addProperty("incrementFactoryClass", "incrementFactoryClass");
		eachVariableJson.addProperty("className", "className");
		eachVariableJson.addProperty("calculation", "calculation");
		eachVariableJson.addProperty("resetType", "Group");
		eachVariableJson.addProperty("resetGroup", "resetGroup");
		eachVariableJson.addProperty("expression", "expression");
		eachVariableJson.addProperty("expressionType", "expressionType");
		eachVariableJson.addProperty("isSystemDefined", true);
		eachVariableJson.addProperty("initialValueExpression", "initialValueExpression");

		variables.add(eachVariableJson);
		eachJson2.add("variables", variables);
		dataSets.add(eachJson2);
		designerPropertiesJson.add("dataSets", dataSets);

		formData.add("designerProperties", designerPropertiesJson);
		formData.addProperty("generateXML", true);
		JDesignHelper designHelper = new JDesignHelper(formData);
		JsonObject dataMapTagContent = new JsonObject();
		dataMapTagContent.addProperty("Parameters", "Parameters");
		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getDataMapTagContent()).thenReturn(dataMapTagContent);
					when(mock.getUnProcessedQuery()).thenReturn("${query}");
				})) {
			JasperDesign createDesigner = designHelper.createDesigner();
			assertNotNull(createDesigner);
		}
	}

	@Test
	public void ut_a8_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		JsonObject pageHeader = new JsonObject();
		pageHeader.addProperty("key", "value");
		designerPropertiesJson.add("pageHeader", pageHeader);
		JsonObject breakObect = new JsonObject();
		breakObect.addProperty("printWhenExpression", "printWhenExpression");;
		designerPropertiesJson.add("pageHeaderBandBreak", breakObect);
		JsonObject columnHeader = new JsonObject();
		columnHeader.addProperty("key", "value");
		designerPropertiesJson.add("columnHeader", columnHeader);
		JsonArray details = new JsonArray();
		JsonObject eachDetailJson = new JsonObject();
		details.add(eachDetailJson);
		designerPropertiesJson.add("details", details);
		designerPropertiesJson.add("detailBandBreak", breakObect);
		JsonArray variablesArray = new JsonArray();
		JsonObject eachVariableJson = new JsonObject();
		eachVariableJson.addProperty("name", "name");
		eachVariableJson.addProperty("incrementType", "Group");
		eachVariableJson.addProperty("incrementGroup", "incrementGroup");
		eachVariableJson.addProperty("incrementFactoryClass", "incrementFactoryClass");
		eachVariableJson.addProperty("className", "className");
		eachVariableJson.addProperty("calculation", "calculation");
		eachVariableJson.addProperty("resetType", "Group");
		eachVariableJson.addProperty("resetGroup", "resetGroup");
		eachVariableJson.addProperty("expression", "expression");
		eachVariableJson.addProperty("expressionType", "expressionType");
		eachVariableJson.addProperty("isSystemDefined", true);
		eachVariableJson.addProperty("initialValueExpression", "initialValueExpression");
		variablesArray.add(eachVariableJson);
		designerPropertiesJson.add("variables", variablesArray);
		JsonObject columnFooter = new JsonObject();
		columnFooter.addProperty("key", "value");
		designerPropertiesJson.add("columnFooter",columnFooter);
		JsonObject pageFooter = new JsonObject();
		pageFooter.addProperty("key", "value");
		designerPropertiesJson.add("pageFooter", pageFooter);
		designerPropertiesJson.add("pageFooterBandBreak", breakObect);
		JsonObject lastPageFooter = new JsonObject();
		lastPageFooter.addProperty("key", "value");;
		designerPropertiesJson.add("lastPageFooter", lastPageFooter);
		JsonObject summary = new JsonObject();
		summary.addProperty("key", "value");
		designerPropertiesJson.add("summary", summary);
		designerPropertiesJson.add("summaryBandBreak", breakObect);
		JsonObject noData = new JsonObject();
		noData.addProperty("key", "value");
		designerPropertiesJson.add("noData", noData);
		JsonArray parametersJson = new JsonArray();
		designerPropertiesJson.add("parameters", parametersJson);
		formData.add("designerProperties", designerPropertiesJson);
		formData.addProperty("generateXML", true);
		JDesignHelper designHelper = new JDesignHelper(formData);
		
		JRDesignBand jrDesignBand = mock(JRDesignBand.class);
		try (MockedConstruction<BandFactory> construction = mockConstruction(BandFactory.class, (mock, context) -> {
			when(mock.getBand()).thenReturn(jrDesignBand);
		})) {
			JasperDesign createDesigner = designHelper.createDesigner();
			assertNotNull(createDesigner);
		}
	}
	
	
	@Test
	public void ut_a9_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		
		JsonArray parametersJson = new JsonArray();
		JsonObject eachParameterJson = new JsonObject();
		eachParameterJson.addProperty("name", "name");
		eachParameterJson.addProperty("className", "className");
		eachParameterJson.addProperty("isPrompt", true);
		eachParameterJson.addProperty("evaluationTime", "evaluationTime");
		eachParameterJson.addProperty("defaultExpression", "defaultExpression");
		eachParameterJson.addProperty("expressionType", "simpleText");
		parametersJson.add(eachParameterJson);
		designerPropertiesJson.add("parameters", parametersJson);
		formData.add("designerProperties", designerPropertiesJson);
		formData.addProperty("generateXML", true);
		JDesignHelper designHelper = new JDesignHelper(formData);
		
		JRDesignBand jrDesignBand = mock(JRDesignBand.class);
		try (MockedConstruction<BandFactory> construction = mockConstruction(BandFactory.class, (mock, context) -> {
			when(mock.getBand()).thenReturn(jrDesignBand);
		})) {
			JasperDesign createDesigner = designHelper.createDesigner();
			assertNotNull(createDesigner);
		}
	}
	
	@Test
	public void ut_b1_test_createDesigner() throws JRException {
		JsonObject formData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray fields = new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("clazz", "clazz");
		eachJson.addProperty("description", "description");
		fields.add(eachJson);
		designerPropertiesJson.add("fields", fields);
		
		JsonArray parametersJson = new JsonArray();
		JsonObject eachParameterJson = new JsonObject();
		eachParameterJson.addProperty("name", "name");
		eachParameterJson.addProperty("className", "className");
		eachParameterJson.addProperty("isPrompt", true);
		eachParameterJson.addProperty("evaluationTime", "evaluationTime");
		eachParameterJson.addProperty("defaultExpression", "defaultExpression");
		eachParameterJson.addProperty("expressionType", "simpleText");
		parametersJson.add(eachParameterJson);
		designerPropertiesJson.add("parameters", parametersJson);
		formData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);
		
		JRDesignBand jrDesignBand = mock(JRDesignBand.class);
		try (MockedConstruction<BandFactory> construction = mockConstruction(BandFactory.class, (mock, context) -> {
			when(mock.getBand()).thenReturn(jrDesignBand);
		})) {
			JasperDesign createDesigner = designHelper.createDesigner();
			assertNotNull(createDesigner);
		}
	}
	@Test
	public void ut_b2_test_prepareParameterForReportExecution() {
		JsonObject formData = new JsonObject();
		JsonObject jsonFormData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray parametersJsonArray =new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("value", "value");
		parametersJsonArray.add(eachJson);
		designerPropertiesJson.add("parameters", parametersJsonArray);
		jsonFormData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);
		JsonObject prepareParameterForReportExecution = designHelper.prepareParameterForReportExecution(jsonFormData);
		assertTrue(prepareParameterForReportExecution.has("parameters"));
	}
	
	@Test
	public void ut_b3_test_prepareParameterForReportExecution() {
		JsonObject formData = new JsonObject();
		JsonObject jsonFormData = new JsonObject();
		JsonObject designerPropertiesJson = new JsonObject();
		JsonArray parametersJsonArray =new JsonArray();
		JsonObject eachJson = new JsonObject();
		eachJson.addProperty("name", "name");
		eachJson.addProperty("className", "__CONNECTION__");
		eachJson.add("connectionDetails", new JsonObject());
		
		parametersJsonArray.add(eachJson);
		designerPropertiesJson.add("parameters", parametersJsonArray);
		jsonFormData.add("designerProperties", designerPropertiesJson);
		JDesignHelper designHelper = new JDesignHelper(formData);
		JsonObject prepareParameterForReportExecution = designHelper.prepareParameterForReportExecution(jsonFormData);
		assertTrue(prepareParameterForReportExecution.has("parameters"));
	}
	@Test
	public void ut_b4_test_getEquivalentClass() {
		JsonObject formData = new JsonObject();
		JDesignHelper designHelper = new JDesignHelper(formData);
		Class<?> equivalentClass = designHelper.getEquivalentClass("text");
		assertEquals(String.class, equivalentClass);
		Class<?> equivalentClass1 = designHelper.getEquivalentClass("numeric");
		assertEquals(Integer.class, equivalentClass1);
		Class<?> equivalentClass11 = designHelper.getEquivalentClass("boolean");
		assertEquals(Boolean.class, equivalentClass11);
		Class<?> equivalentClass111 = designHelper.getEquivalentClass("date");
		assertEquals(java.sql.Date.class, equivalentClass111);
		Class<?> equivalentClass1111 = designHelper.getEquivalentClass("dateTime");
		assertEquals(java.sql.Timestamp.class, equivalentClass1111);
		Class<?> equivalentClass11111 = designHelper.getEquivalentClass("__CONNECTION__");
		assertEquals(java.sql.Connection.class, equivalentClass11111);
		Class<?> equivalentClass2 = designHelper.getEquivalentClass("test");
		assertEquals(String.class, equivalentClass2);
	}
	
	
}
