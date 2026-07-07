package com.helicalinsight.resourcedb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import net.sf.jasperreports.engine.JRDatasetParameter;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignExpression;

public class LazySubDatasetRunTest {

	private static final String LAZY_FACTORY =
			"com.helicalinsight.adhoc.jreport.LazySubDatasetDataSourceFactory";

	@Test
	public void extractReportParameterName_parsesValidExpression() {
		assertEquals("TravelData", HCRUtils.extractReportParameterName("$P{TravelData}"));
		assertEquals("MAIN_DATASET", HCRUtils.extractReportParameterName("$P{MAIN_DATASET}"));
	}

	@Test
	public void extractReportParameterName_returnsNullForInvalidExpression() {
		assertEquals(null, HCRUtils.extractReportParameterName(""));
		assertEquals(null, HCRUtils.extractReportParameterName("$F{mode_of_payment}"));
		assertEquals(null, HCRUtils.extractReportParameterName("TravelData"));
	}

	@Test
	public void extractDatasetRunParameters_returnsParentScopeExpressions() {
		JsonObject dataSetRunJson = new JsonObject();
		dataSetRunJson.addProperty("dataSetName", "TravelData");
		dataSetRunJson.addProperty("dataSetExpression", "$P{TravelData}");

		JsonArray parameters = new JsonArray();
		JsonObject mappedField = new JsonObject();
		mappedField.addProperty("name", "mode_of_payment");
		mappedField.addProperty("expression", "$F{mode_of_payment}");
		parameters.add(mappedField);

		JsonObject mappedVariable = new JsonObject();
		mappedVariable.addProperty("name", "region");
		mappedVariable.addProperty("expression", "$V{regionVar}");
		parameters.add(mappedVariable);

		JsonObject skipped = new JsonObject();
		skipped.addProperty("name", "emptyExpression");
		parameters.add(skipped);

		dataSetRunJson.add("parameters", parameters);

		List<String[]> pairs = HCRUtils.extractDatasetRunParameters(dataSetRunJson);

		assertEquals(2, pairs.size());
		assertEquals("mode_of_payment", pairs.get(0)[0]);
		assertEquals("$F{mode_of_payment}", pairs.get(0)[1]);
		assertEquals("region", pairs.get(1)[0]);
		assertEquals("$V{regionVar}", pairs.get(1)[1]);
	}

	@Test
	public void buildLazyResolveExpression_usesParentScopeExpressionNotSubdatasetParameter() {
		JsonObject dataSetRunJson = buildDataSetRunJson("$P{TravelData}", "$F{mode_of_payment}");

		JRDesignExpression expression = HCRUtils.buildLazyResolveExpression("TravelData",
				HCRUtils.extractDatasetRunParameters(dataSetRunJson));

		String expressionText = expression.getText();
		assertEquals(LAZY_FACTORY + ".resolve($P{TravelData}, \"mode_of_payment\", $F{mode_of_payment})",
				expressionText);
		assertFalse(expressionText.contains("$P{mode_of_payment}"));
	}

	@Test
	public void buildLazyResolveExpression_withoutMappedParameters() {
		JRDesignExpression expression = HCRUtils.buildLazyResolveExpression("TravelData", List.of());

		assertEquals(LAZY_FACTORY + ".resolve($P{TravelData})", expression.getText());
	}

	@Test
	public void configureSubDatasetRun_buildsLazyExpressionAndDatasetParameters() throws Exception {
		JsonObject dataSetRunJson = buildDataSetRunJson("$P{TravelData}", "$F{mode_of_payment}");

		JRDesignDatasetRun datasetRun = HCRUtils.configureSubDatasetRun(dataSetRunJson);

		assertEquals("TravelData", datasetRun.getDatasetName());
		assertEquals(LAZY_FACTORY + ".resolve($P{TravelData}, \"mode_of_payment\", $F{mode_of_payment})",
				datasetRun.getDataSourceExpression().getText());

		JRDatasetParameter[] datasetParameters = datasetRun.getParameters();
		assertEquals(1, datasetParameters.length);
		assertEquals("mode_of_payment", datasetParameters[0].getName());
		assertEquals("$F{mode_of_payment}", datasetParameters[0].getExpression().getText());
	}

	@Test
	public void configureSubDatasetRun_withoutMappedParameters_usesLazyPipelineOnly() {
		JsonObject dataSetRunJson = new JsonObject();
		dataSetRunJson.addProperty("dataSetName", "MainDataset");
		dataSetRunJson.addProperty("dataSetExpression", "$P{MAIN_DATASET}");

		JRDesignDatasetRun datasetRun = HCRUtils.configureSubDatasetRun(dataSetRunJson);

		assertEquals("MainDataset", datasetRun.getDatasetName());
		assertEquals(LAZY_FACTORY + ".resolve($P{MAIN_DATASET})",
				datasetRun.getDataSourceExpression().getText());
		assertEquals(0, datasetRun.getParameters().length);
	}

	@Test(expected = EfwServiceException.class)
	public void configureSubDatasetRun_throwsWhenDataSetExpressionIsInvalid() {
		JsonObject dataSetRunJson = new JsonObject();
		dataSetRunJson.addProperty("dataSetName", "TravelData");
		dataSetRunJson.addProperty("dataSetExpression", "$F{TravelData}");

		HCRUtils.configureSubDatasetRun(dataSetRunJson);
	}

	@Test
	public void configureSubDatasetRun_preservesLegacyReportConnectionExpression() {
		JsonObject dataSetRunJson = new JsonObject();
		dataSetRunJson.addProperty("dataSetName", "Query1");
		dataSetRunJson.addProperty("dataSetExpression", "$P{REPORT_CONNECTION}");

		JRDesignDatasetRun datasetRun = HCRUtils.configureSubDatasetRun(dataSetRunJson);

		assertEquals("Query1", datasetRun.getDatasetName());
		assertTrue(datasetRun.getDataSourceExpression() == null
				|| !datasetRun.getDataSourceExpression().getText().contains("LazySubDatasetDataSourceFactory"));
	}

	private JsonObject buildDataSetRunJson(String dataSetExpression, String mappedExpression) {
		JsonObject dataSetRunJson = new JsonObject();
		dataSetRunJson.addProperty("dataSetName", "TravelData");
		dataSetRunJson.addProperty("dataSetExpression", dataSetExpression);

		JsonArray parameters = new JsonArray();
		JsonObject mappedParameter = new JsonObject();
		mappedParameter.addProperty("name", "mode_of_payment");
		mappedParameter.addProperty("expression", mappedExpression);
		parameters.add(mappedParameter);
		dataSetRunJson.add("parameters", parameters);
		return dataSetRunJson;
	}
}
