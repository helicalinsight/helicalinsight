package com.helicalinsight.adhoc.jreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.SplitterUtils;

import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;

public class JasperPrintCacheKeyGeneratorTest {

	private JasperDesign buildDesign(String mainQueryText, String subDatasetName, String subQueryText) throws Exception {
		JasperDesign design = new JasperDesign();
		design.setName("testDesign");

		JRDesignQuery mainQuery = new JRDesignQuery();
		mainQuery.setText(mainQueryText);
		design.setQuery(mainQuery);

		if (subDatasetName != null) {
			JRDesignDataset subDataset = new JRDesignDataset(false);
			subDataset.setName(subDatasetName);
			JRDesignQuery subQuery = new JRDesignQuery();
			subQuery.setText(subQueryText);
			subDataset.setQuery(subQuery);
			design.addDataset(subDataset);
		}
		return design;
	}

	@Test
	public void testGenerate_usesMainDataKeyForMainDataset() throws Exception {
		JasperDesign design = buildDesign("SELECT * FROM employees", null, null);
		JsonObject formData = new JsonObject();
		formData.addProperty("mainDataKey", "result-set-cache-key");

		String keyWithMainDataKey = JasperPrintCacheKeyGenerator.generate(design, formData);

		JRDesignQuery mainQuery = (JRDesignQuery) design.getMainDataset().getQuery();
		mainQuery.setText("SELECT * FROM completely_different_table");
		String keyAfterQueryChange = JasperPrintCacheKeyGenerator.generate(design, formData);

		assertEquals(keyWithMainDataKey, keyAfterQueryChange);
	}

	@Test
	public void testGenerate_differentMainDataKeyProducesDifferentKey() throws Exception {
		JasperDesign design = buildDesign("SELECT 1", null, null);

		JsonObject formData1 = new JsonObject();
		formData1.addProperty("mainDataKey", "cache-key-a");
		String keyA = JasperPrintCacheKeyGenerator.generate(design, formData1);

		JsonObject formData2 = new JsonObject();
		formData2.addProperty("mainDataKey", "cache-key-b");
		String keyB = JasperPrintCacheKeyGenerator.generate(design, formData2);

		assertNotEquals(keyA, keyB);
	}

	@Test
	public void testGenerate_subDatasetUsesQueryText() throws Exception {
		JasperDesign design = buildDesign("SELECT 1", "subDS", "SELECT id FROM orders");
		JsonObject formData = new JsonObject();
		formData.addProperty("mainDataKey", "main-cache-key");

		String keyOriginal = JasperPrintCacheKeyGenerator.generate(design, formData);

		JRDesignDataset subDataset = (JRDesignDataset) design.getDatasets()[0];
		((JRDesignQuery) subDataset.getQuery()).setText("SELECT id FROM customers");
		String keyAfterSubQueryChange = JasperPrintCacheKeyGenerator.generate(design, formData);

		assertNotEquals(keyOriginal, keyAfterSubQueryChange);
	}

}
