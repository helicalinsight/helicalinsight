package com.helicalinsight.resourcedb.hcr.adv;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.jasperintegration.bandcontents.HCRCrosstab;
import com.helicalinsight.efw.jasperintegration.bandcontents.HCRTextField;

import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

public class HCRCrosstabHeaderTextFieldUnitTest {

	private HCRCrosstab hcrCrosstab;
	private Method addTextFieldsToCell;
	private JasperDesign jasperDesign;

	@Before
	public void setUp() throws Exception {
		hcrCrosstab = new HCRCrosstab();
		HCRTextField hcrTextField = mock(HCRTextField.class);
		when(hcrTextField.prepareTextField(any(JsonObject.class), any(JasperDesign.class)))
				.thenAnswer(invocation -> new JRDesignTextField());
		Field field = HCRCrosstab.class.getDeclaredField("hcrTextField");
		field.setAccessible(true);
		field.set(hcrCrosstab, hcrTextField);
		addTextFieldsToCell = HCRCrosstab.class.getDeclaredMethod("addTextFieldsToCell",
				JRDesignCellContents.class, JsonObject.class, JasperDesign.class);
		addTextFieldsToCell.setAccessible(true);
		jasperDesign = new JasperDesign();
	}

	@Test
	public void textFieldArray_addsAllElements() throws Exception {
		JRDesignCellContents cell = new JRDesignCellContents();
		JsonObject cellJson = new JsonObject();
		JsonArray textFields = new JsonArray();
		textFields.add(textField("\"a\""));
		textFields.add(textField("\"b\""));
		cellJson.add("textField", textFields);

		addTextFieldsToCell.invoke(hcrCrosstab, cell, cellJson, jasperDesign);

		assertEquals(2, cell.getChildren().size());
	}

	@Test
	public void textFieldObject_addsSingleElement() throws Exception {
		JRDesignCellContents cell = new JRDesignCellContents();
		JsonObject cellJson = new JsonObject();
		cellJson.add("textField", textField("\"legacy\""));

		addTextFieldsToCell.invoke(hcrCrosstab, cell, cellJson, jasperDesign);

		assertEquals(1, cell.getChildren().size());
	}

	@Test
	public void missingOrEmptyTextField_addsNothing() throws Exception {
		JRDesignCellContents missing = new JRDesignCellContents();
		addTextFieldsToCell.invoke(hcrCrosstab, missing, new JsonObject(), jasperDesign);
		assertEquals(0, missing.getChildren().size());

		JRDesignCellContents emptyObject = new JRDesignCellContents();
		JsonObject emptyObjectJson = new JsonObject();
		emptyObjectJson.add("textField", new JsonObject());
		addTextFieldsToCell.invoke(hcrCrosstab, emptyObject, emptyObjectJson, jasperDesign);
		assertEquals(0, emptyObject.getChildren().size());

		JRDesignCellContents emptyArray = new JRDesignCellContents();
		JsonObject emptyArrayJson = new JsonObject();
		emptyArrayJson.add("textField", new JsonArray());
		addTextFieldsToCell.invoke(hcrCrosstab, emptyArray, emptyArrayJson, jasperDesign);
		assertEquals(0, emptyArray.getChildren().size());
	}

	private static JsonObject textField(String expression) {
		JsonObject textField = new JsonObject();
		textField.addProperty("textFieldExpression", expression);
		return textField;
	}
}
