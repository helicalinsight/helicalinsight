package com.helicalinsight.resourcedb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Set;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;

public class HCRUtilTest {

	private static String defaultFormData = "{\"format\":\"html\",\"page\":\"0\",\"generateXML\":false,\"connectionDetails\":{},\"designerProperties\":{\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnWidth\":0,\"columnSpacing\":0,\"leftMargin\":20,\"rightMargin\":20,\"topMargin\":20,\"bottomMargin\":20,\"titleNewPage\":false,\"floatColumnFooter\":false,\"summaryNewPage\":false,\"designerStyle\":[{\"backColor\":\"#f8f9fa\",\"foreColor\":\"#ffffff\",\"name\":\"TABLE_CT\",\"mode\":\"Opaque\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000000\"}}}},{\"name\":\"TABLE_TH\",\"mode\":\"Opaque\",\"backColor\":\"#F0F8FF\",\"foreColor\":\"#000000\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"}}}},{\"name\":\"TABLE_CH\",\"mode\":\"Opaque\",\"backColor\":\"#BFE1FF\",\"foreColor\":\"#000000\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"}}}},{\"name\":\"TABLE_CF\",\"mode\":\"Opaque\",\"backColor\":\"#BFE1FF\",\"foreColor\":\"#000000\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"}}}},{\"name\":\"TABLE_TD\",\"mode\":\"Opaque\",\"backColor\":\"#FFFFFF\",\"foreColor\":\"#000000\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"}}}},{\"name\":\"TABLE_TF\",\"mode\":\"Opaque\",\"backColor\":\"#F0F8FF\",\"foreColor\":\"#000000\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"}}}}],\"title\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]},\"pageHeader\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]},\"details\":[{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]}],\"groups\":[{\"groupHeader\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]},\"groupFooter\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]}}],\"pageFooter\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]},\"summary\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]},\"noData\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]},\"lastPageFooter\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]},\"parameters\":[],\"fields\":[],\"variables\":[],\"columnHeader\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]},\"columnFooter\":{\"bandHeight\":0,\"isImageAttached\":\"false\",\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"table\":[],\"crosstab\":[],\"chart\":[],\"subReport\":[],\"customVisualization\":[]}}}";

	@Test
	public void testJsonMerge() {

		JsonObject defaultJson = new JsonObject();

		JsonObject object = new JsonObject();
		object.addProperty("object_prop", "objectValue");
		object.addProperty("object_prop2", "objectValue2");
		object.addProperty("name", "random_name");

		JsonArray array = new JsonArray();
		array.add(object);

		defaultJson.addProperty("rootProperty", "someValue");
		defaultJson.add("object", object);
		defaultJson.add("array", array);

		JsonObject userObject = new JsonObject();
		userObject.addProperty("object_prop", "userValue");

		JsonObject userObject2 = new JsonObject();
		userObject2.addProperty("object_prop", "randomeName");
		userObject2.addProperty("object_prop2", "randomValue");

		JsonArray userArray = new JsonArray();
		userArray.add(userObject2);

		JsonObject userJson = new JsonObject();
		userJson.add("object", userObject);
		userJson.add("array", userArray);

		HCRUtils.copyDefault(defaultJson, userJson);

		Assert.assertTrue(userJson.has("rootProperty"));
		Assert.assertTrue(userJson.has("object"));
		Assert.assertTrue(userJson.get("object").isJsonObject());
		Assert.assertTrue(!userJson.get("object").getAsJsonObject().entrySet().isEmpty());
		Assert.assertTrue(userJson.get("object").getAsJsonObject().entrySet().size() == 3);

		Assert.assertTrue(userJson.has("array"));
		Assert.assertTrue(userJson.get("array").isJsonArray());
		Assert.assertTrue("Array size must not be 0", userJson.get("array").getAsJsonArray().size() == 1);
		Assert.assertTrue(userJson.get("array").getAsJsonArray().get(0).getAsJsonObject().entrySet().size() == 3);

	}

	@Test
	public void testGroupHeaderBandDefaults() {

		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"designerStyles\":[],\"parameters\":[{\"name\":\"Query1\",\"className\":\"net.sf.jasperreports.engine.JRDataSource\",\"connectionDetails\":{\"temp_uuid\":\"_temp_c81d2937-19d7-4ca3-b0ba-2980f8604d83\",\"map_id\":1}}],\"groups\":[{\"name\":\"group_travel_type\",\"groupNumber\":1,\"expression\":\"$F{travel_type}\",\"groupHeader\":{\"bandHeight\":190,\"textField\":[],\"lines\":[],\"image\":[],\"break\":[],\"splitType\":\"Stretch\",\"table\":[{\"dataSetRun\":{\"dataSetName\":\"Query1\",\"dataSetExpression\":\"$P{Query1}\"},\"columns\":[{\"tableHeader\":{\"enabled\":true},\"tableFooter\":{\"enabled\":true},\"columnHeaderOfTable\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"travel_id\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-a9918344-7ce1-4455-a7fa-ca3169afcae1\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":true},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_id}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-989d7ff2-7fa8-4732-8a4f-078e6fedc516\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"width\":100},{\"tableHeader\":{\"enabled\":true},\"tableFooter\":{\"enabled\":true},\"columnHeaderOfTable\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"travel_date\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-41a1d413-9be7-4082-938b-e88b3d854fd2\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":true},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_date}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-c9dde574-d4bb-435e-a192-5f5f255b0ffc\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"width\":100},{\"tableHeader\":{\"enabled\":true},\"tableFooter\":{\"enabled\":true},\"columnHeaderOfTable\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"travel_type\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-6948ea6c-7976-4990-a175-6b866a49c209\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":true},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_type}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-807e5622-162c-4112-a091-5f83411d006a\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"width\":100}],\"componentElementProperties\":{\"X\":80,\"Y\":0,\"width\":300,\"height\":125,\"printRepeatedValues\":true,\"mode\":\"Transparent\",\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"rightLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"bottomLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"topLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"}}},\"padding\":{\"bottomPadding\":1,\"topPadding\":1,\"leftPadding\":1,\"rightPadding\":1}}}]},\"groupFooter\":{\"bandHeight\":0,\"textField\":[],\"lines\":[],\"image\":[],\"break\":[],\"splitType\":\"Stretch\"}}],\"fields\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"},{\"name\":\"travelled_by\",\"clazz\":\"java.lang.Integer\"}],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"applyCustomSettings\":true,\"customSettings\":{\"export.xls.exclude.origin.keep.first.band.1\":\"pageHeader\",\"export.xls.exclude.origin.band.1\":\"title\",\"export.xls.exclude.origin.band.2\":\"pageFooter\",\"export.xls.exclude.origin.keep.first.band.2\":\"columnHeader\",\"export.xls.exclude.origin.keep.first.band.3\":\"columnHeader\",\"export.xls.remove.empty.space.between.rows\":\"true\",\"export.xls.remove.empty.space.between.columns\":\"true\",\"export.xlsx.exclude.origin.keep.first.band.1\":\"pageHeader\",\"export.xlsx.exclude.origin.band.1\":\"title\",\"export.xlsx.exclude.origin.band.2\":\"pageFooter\",\"export.xlsx.exclude.origin.keep.first.band.2\":\"columnHeader\",\"export.xlsx.exclude.origin.keep.first.band.3\":\"columnHeader\",\"export.xlsx.remove.empty.space.between.rows\":\"true\",\"export.xlsx.remove.empty.space.between.columns\":\"true\"},\"dataSets\":[{\"name\":\"Query1\",\"isMainDataset\":false,\"dataSetExpression\":\"$P{Query1}\",\"connectionDetails\":{\"temp_uuid\":\"_temp_c81d2937-19d7-4ca3-b0ba-2980f8604d83\",\"map_id\":1},\"parameters\":[]}]},\"type\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		JsonObject formJson = JsonParser.parseString(formData).getAsJsonObject();
		JsonObject defaultJson = JsonParser.parseString(defaultFormData).getAsJsonObject();
		HCRUtils.copyDefault(defaultJson, formJson);
		JsonArray groups = formJson.getAsJsonObject("designerProperties").getAsJsonArray("groups");
		assertTrue(!groups.isEmpty());
		JsonObject groupObject = (JsonObject) groups.get(0);
		JsonObject groupHeader = GsonUtility.optJsonObject(groupObject, "groupHeader");
		JsonObject tableObject = (JsonObject) GsonUtility.optJsonArray(groupHeader, "table").get(0);
		JsonObject componentElementProperties = GsonUtility.optJsonObject(tableObject, "componentElementProperties");
		assertTrue("stretchType should present", componentElementProperties.has("stretchType"));
		assertTrue("positionType should present", componentElementProperties.has("positionType"));
	}

	@Test
	public void testDetailsBandTableDefaults() {

		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\"_temp_c41c3088-f794-4e89-9ab5-1269bb532345\",\"map_id\":1},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\"_temp_c41c3088-f794-4e89-9ab5-1269bb532345\",\"map_id\":1}}],\"variables\":[],\"pageWidth\":1400,\"pageHeight\":1000,\"orientation\":\"Portrait\",\"columnCount\":1,\"details\":[{\"bandHeight\":500,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"crosstab\":[],\"chart\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\"},\"positionType\":\"FixRelativeTop\",\"tableWidth\":500,\"tableHeight\":250,\"mode\":\"Transparent\",\"printWhenDetailOverflows\":false,\"printInFirstWholeBand\":false,\"dataSourceExpression\":\"\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"columns\":[{\"width\":300,\"tableHeader\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"dim_id\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"height\":60,\"rowSpan\":1,\"textField\": [{\"textFieldExpression\":\"$F{dim_id}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"date_key\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{date_key}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"day_number\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{day_number}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"fiscal_year\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{fiscal_year}\",\"textWidth\":200,\"textHeight\":50}]}}]}]}],\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\"_temp_c41c3088-f794-4e89-9ab5-1269bb532345\",\"map_id\":1},\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}],\"query\":\"select * from \\\"HIUSER\\\".\\\"dimdate\\\"\"},\"type\":\"pdf\",\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":true,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		JsonObject formJson = JsonParser.parseString(formData).getAsJsonObject();

		JsonObject defaultJson = JsonParser.parseString(defaultFormData).getAsJsonObject();
		HCRUtils.copyDefault(defaultJson, formJson);

		JsonArray detailsBand = formJson.getAsJsonObject("designerProperties").getAsJsonArray("details");

		assertTrue(!detailsBand.isEmpty());
		assertEquals(1, detailsBand.size());

		JsonObject firstDetailBandObj = detailsBand.get(0).getAsJsonObject();

		JsonObject firstTable = firstDetailBandObj.getAsJsonArray("table").get(0).getAsJsonObject();

		JsonObject firstColumn = firstTable.getAsJsonArray("columns").get(0).getAsJsonObject();

		Set<String> keys = firstColumn.keySet();

		for (String key : keys) {
			JsonElement element = firstColumn.get(key);
			if (element.isJsonObject()) {
				Set<String> objKeys = element.getAsJsonObject().keySet();
				assertTrue(objKeys.contains("styleNameReference"));
				assertTrue(objKeys.contains("enabled"));
				break;
			}
		}
	}

	@Test
	public void testStyleArrayDefaults() {
		String formData = "{\"format\":\"html\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[{\"name\":\"Query1\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\"_temp_be4f7e87-075c-4c91-bb75-d438521ae7d3\",\"map_id\":1}}],\"variables\":[],\"designerStyle\":[{\"name\":\"NEW_STYLE\",\"textFontSize\":16,\"bold\":true,\"border\":{\"padding\":{\"bottomPadding\":1,\"topPadding\":1,\"leftPadding\":1,\"rightPadding\":1},\"line\":{\"leftLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"Solid\"},\"rightLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"Solid\"},\"bottomLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"Solid\"},\"topLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"Solid\"}}}}],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"dataSets\":[{\"name\":\"Query1\",\"isMainDataset\":false,\"dataSetExpression\":\"$P{Query1}\",\"connectionDetails\":{\"temp_uuid\":\"_temp_be4f7e87-075c-4c91-bb75-d438521ae7d3\",\"map_id\":1},\"fields\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"},{\"name\":\"travelled_by\",\"clazz\":\"java.lang.Integer\"}],\"parameters\":[]}],\"summary\":{\"bandHeight\":240,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"Query1\",\"dataSetExpression\":\"$P{Query1}\"},\"columns\":[{\"tableHeader\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false},\"columnHeaderOfTable\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"travel_date\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-a1240e71-667f-49d9-b6ae-302df7385f3c\",\"textHeight\":25,\"textWidth\":100,\"fontName\":\"questrialregular\",\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":false},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_date}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-4c354519-e1d9-4dbf-a67a-713bec173a9d\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"width\":100},{\"tableHeader\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false},\"columnHeaderOfTable\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"travel_type\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-20299ab8-92de-4eee-bbb6-a44e36be7786\",\"textHeight\":25,\"textWidth\":100,\"fontName\":\"questrialregular\",\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":false},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_type}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-e62b94d8-46fb-4415-89ff-077846461dc1\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"width\":100},{\"tableHeader\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false},\"columnHeaderOfTable\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"travel_medium\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-fb7c3dfc-a349-44f7-be75-90a1c1a6b612\",\"textHeight\":25,\"textWidth\":100,\"fontName\":\"questrialregular\",\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":false},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_medium}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-37c933fa-6347-40b0-bf01-05983a8a60a3\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"width\":100}],\"printRepeatedValues\":true,\"componentElementProperties\":{\"X\":20,\"Y\":0,\"width\":510,\"height\":240}}],\"crosstab\":[],\"chart\":[]}},\"type\":\"html\",\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		JsonObject formJson = JsonParser.parseString(formData).getAsJsonObject();
		JsonObject defaultJson = JsonParser.parseString(defaultFormData).getAsJsonObject();
		HCRUtils.copyDefault(defaultJson, formJson);
		JsonArray designerStyle = formJson.getAsJsonObject("designerProperties").getAsJsonArray("designerStyle");
		assertTrue(designerStyle.size() > 1);
	}

	@Test
	public void shouldNotInvokeSetterWhenStyleNameReferenceIsAbsent() {
		JsonObject json = new JsonObject();

		@SuppressWarnings("unchecked")
		Consumer<String> setter = mock(Consumer.class);

		HCRUtils.applyStyleReference(json, setter);

		verifyNoInteractions(setter);
	}

	@Test
	public void shouldNotInvokeSetterWhenStyleNameReferenceIsBlank() {
		JsonObject json = new JsonObject();
		json.addProperty("styleNameReference", "");

		@SuppressWarnings("unchecked")
		Consumer<String> setter = mock(Consumer.class);

		HCRUtils.applyStyleReference(json, setter);

		verifyNoInteractions(setter);
	}

	@Test
	public void shouldNotInvokeSetterWhenStyleNameReferenceIsNull() {
		JsonObject json = new JsonObject();
		json.add("styleNameReference", JsonNull.INSTANCE);

		@SuppressWarnings("unchecked")
		Consumer<String> setter = mock(Consumer.class);

		HCRUtils.applyStyleReference(json, setter);

		verifyNoInteractions(setter);
	}

	@Test
	public void shouldInvokeSetterWhenStyleNameReferenceHasValue() {
		JsonObject json = new JsonObject();
		json.addProperty("styleNameReference", "myStyle");

		@SuppressWarnings("unchecked")
		Consumer<String> setter = mock(Consumer.class);

		HCRUtils.applyStyleReference(json, setter);

		verify(setter).accept("myStyle");
		verifyNoMoreInteractions(setter);
	}
}
