package com.helicalinsight.efw.jasperintegration.bandcontents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;

import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabBucket;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabCell;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabDataset;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabMeasure;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.HorizontalPosition;
import net.sf.jasperreports.engine.type.RunDirectionEnum;




@Component
public class HCRCrosstab implements IHCRBandContents {


	private static final String CROSS_TAB = "crosstab";
	
	@Autowired
	private HCRTextField hcrTextField;
	
	@Autowired
	private HCRImage hcrImage;	

	private static final Logger logger = LoggerFactory.getLogger(HCRCrosstab.class);

	@Override
	public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
		addCrossTabIfAny(band, bandJson, jasperDesign);
	}

	private void addCrossTabIfAny(JRDesignBand jrDesignBand, JsonObject bandJson, JasperDesign jasperDesign) {
		if (bandJson.has(CROSS_TAB)) {
			JsonArray crossTabArray = GsonUtility.optJsonArray(bandJson, CROSS_TAB);
			if(crossTabArray == null ) return ;
			for (int index = 0; index < crossTabArray.size(); index++) {
				JsonObject eachCrossTabJson = crossTabArray.get(index).getAsJsonObject();
				JRDesignCrosstab crosstab = prepareCrossTab(eachCrossTabJson, jasperDesign);
				jrDesignBand.addElement(crosstab);
			}
		}
	}

	private JRDesignCrosstab prepareCrossTab(JsonObject json, JasperDesign jasperDesign) {

		JRDesignCrosstab crosstab = new JRDesignCrosstab();
		
		JsonObject datasetRunJson = json.getAsJsonObject("dataSetRun");
		JRDesignDatasetRun datasetRun = HCRUtils.configureSubDatasetRun(datasetRunJson);
		JRDesignCrosstabDataset crosstabDataSet = new JRDesignCrosstabDataset();
		crosstabDataSet.setDatasetRun(datasetRun);
		crosstab.setDataset(crosstabDataSet);

		addRowGroups(crosstab, json, jasperDesign);
		addColumnGroups(crosstab, json, jasperDesign);
		setMeasures(crosstab, json);

		JsonArray cells = json.getAsJsonArray("crosstabCells");
		for (int index = 0; index < cells.size(); index++) {
			JsonObject cellJson = cells.get(index).getAsJsonObject();
			JRDesignCrosstabCell cell = new JRDesignCrosstabCell();
			cell.setWidth(cellJson.get("width").getAsInt());
			cell.setHeight(cellJson.get("height").getAsInt());

			if (cellJson.has("rowTotalGroup"))
				cell.setRowTotalGroup(cellJson.get("rowTotalGroup").getAsString());
			if (cellJson.has("columnTotalGroup"))
				cell.setColumnTotalGroup(cellJson.get("columnTotalGroup").getAsString());
			
			JRDesignCellContents designCell = new JRDesignCellContents();
			HCRUtils.applyStyleReference(cellJson, designCell::setStyleNameReference);
			
			if (cellJson.has("textField")) {
				
				for(JsonElement textFieldElement : GsonUtility.optJsonArray(cellJson, "textField")) {
					JsonObject textFieldJson = textFieldElement.getAsJsonObject();
					if ( textFieldJson != null && !textFieldJson.keySet().isEmpty()) {
						JRDesignTextField textField = hcrTextField.prepareTextField(textFieldJson,jasperDesign);
						designCell.addElement(textField);
					}
				}
			}
			
			addImages(jasperDesign, designCell, cellJson);

			cell.setContents(designCell);

			try {
				crosstab.addCell(cell);
			} catch (JRException ex) {
				throw new EfwServiceException("Error occurred while preparing " + index + "th crosstab cell. Root cause : " +  ex.getMessage());
			}
		}
		
		 
		
		 HCRUtils.configureComponentElementProperties(crosstab, json);
		 
		 // Crosstab specific 
		 
		 JsonObject componentElementProperties = json.getAsJsonObject("componentElementProperties");
	        
	     RunDirectionEnum runDirectionEnum = RunDirectionEnum.valueOf(GsonUtility.optStringValue(componentElementProperties, "runDirection", "LTR"));
		 crosstab.setRunDirection(runDirectionEnum);
		 crosstab.setRepeatColumnHeaders(GsonUtility.optBooleanValue(componentElementProperties, "repeatColumnHeaders", false));
		 crosstab.setRepeatRowHeaders(GsonUtility.optBooleanValue(componentElementProperties, "repeatRowHeaders", false));
		 crosstab.setPrintRepeatedValues(GsonUtility.optBooleanValue(componentElementProperties, "printRepeatedValues", false));
		 crosstab.setColumnBreakOffset(GsonUtility.optIntValue(componentElementProperties, "columnBreakOffset", 0));
		 HorizontalPosition horizontalPosition = HorizontalPosition.valueOf(GsonUtility.optStringValue(componentElementProperties, "horizontalPosition", "LEFT"));
		 crosstab.setHorizontalPosition(horizontalPosition);
		 crosstab.setIgnoreWidth(GsonUtility.optBooleanValue(componentElementProperties,"ignoreWidth", false));
		 return crosstab;
	}

	private void addRowGroups(JRDesignCrosstab crosstab, JsonObject json, JasperDesign jasperDesign) {

		logger.debug("Adding row group to crosstab.");

		JsonArray rowGroups = json.getAsJsonArray("rowGroups");
		for (int i = 0; i < rowGroups.size(); i++) {
			JsonObject rowGroupJson = rowGroups.get(i).getAsJsonObject();
			JRDesignCrosstabRowGroup rowGroup = new JRDesignCrosstabRowGroup();
			rowGroup.setName(rowGroupJson.get("name").getAsString());
			rowGroup.setWidth(rowGroupJson.get("width").getAsInt());

			String totalPosition = rowGroupJson.get("totalPosition").getAsString();
			rowGroup.setTotalPosition(CrosstabTotalPositionEnum.valueOf(totalPosition));

			JRDesignCrosstabBucket bucket = prepareBucket(rowGroupJson.getAsJsonObject("bucket"));
			rowGroup.setBucket(bucket);

			JRDesignCellContents headerCell = new JRDesignCellContents();
			JsonObject rowHeaderJson = rowGroupJson.getAsJsonObject("crosstabRowHeader");
			JsonObject rowHeaderTextField = rowHeaderJson.getAsJsonObject("textField");
			if(rowHeaderTextField != null && !rowHeaderTextField.keySet().isEmpty()) {
				headerCell.addElement(hcrTextField.prepareTextField(rowHeaderTextField, jasperDesign));
			}
			addImages(jasperDesign, headerCell, rowHeaderJson);
			HCRUtils.applyStyleReference(rowHeaderJson, headerCell::setStyleNameReference);
			rowGroup.setHeader(headerCell);

			if (rowGroupJson.has("crosstabTotalRowHeader")) {
				JRDesignCellContents totalHeader = new JRDesignCellContents();
				JsonObject totalHeaderJson = rowGroupJson.getAsJsonObject("crosstabTotalRowHeader");
				
				JsonObject tableHeaderText = 	totalHeaderJson.getAsJsonObject("textField");
				if(tableHeaderText != null && !tableHeaderText.keySet().isEmpty()) {
					totalHeader.addElement(hcrTextField.prepareTextField(tableHeaderText, jasperDesign));
				}
				addImages(jasperDesign, totalHeader, totalHeaderJson);
				HCRUtils.applyStyleReference(totalHeaderJson, totalHeader::setStyleNameReference);
				rowGroup.setTotalHeader(totalHeader);
			}
			
			try {
				crosstab.addRowGroup(rowGroup);
			} catch (JRException ex) {
				String message = "Error occurred while adding the row group [name = " + rowGroupJson.get("name").getAsString() + " ] " ;
				logger.error(message  + " .Root cause : ",ex);
				throw new EfwServiceException(message);
			}
		}

	}
	

	private void addColumnGroups(JRDesignCrosstab crosstab, JsonObject json, JasperDesign jasperDesign) {
		JsonArray colGroups = json.getAsJsonArray("columnGroups");
		for (int i = 0; i < colGroups.size(); i++) {
			JsonObject colGroupJson = colGroups.get(i).getAsJsonObject();
			JRDesignCrosstabColumnGroup colGroup = new JRDesignCrosstabColumnGroup();
			colGroup.setName(colGroupJson.get("name").getAsString());
			colGroup.setHeight(colGroupJson.get("height").getAsInt());

			String totalPosition = colGroupJson.get("totalPosition").getAsString();
			colGroup.setTotalPosition(CrosstabTotalPositionEnum.valueOf(totalPosition));

			JRDesignCrosstabBucket bucket = prepareBucket(colGroupJson.getAsJsonObject("bucket"));
			colGroup.setBucket(bucket);

			// Column header
			JRDesignCellContents colHeaderCell = new JRDesignCellContents();
			JsonObject colHeaderJson = colGroupJson.getAsJsonObject("crosstabColumnHeader");
			JsonObject columnHeaderTextField = colHeaderJson.getAsJsonObject("textField");
			if(columnHeaderTextField != null && !columnHeaderTextField.keySet().isEmpty()) {
				colHeaderCell.addElement(hcrTextField.prepareTextField(columnHeaderTextField, jasperDesign));
			}
			addImages(jasperDesign, colHeaderCell, colHeaderJson);
			HCRUtils.applyStyleReference(colHeaderJson, colHeaderCell::setStyleNameReference);
			colGroup.setHeader(colHeaderCell);
			
			if (colGroupJson.has("crosstabTotalColumnHeader")) {
				JRDesignCellContents totalHeader = new JRDesignCellContents();
				JsonObject totalHeaderJson = colGroupJson.getAsJsonObject("crosstabTotalColumnHeader");
				JsonObject totalHeaderTextField = totalHeaderJson.getAsJsonObject("textField");
				if(totalHeaderTextField != null && !totalHeaderTextField.keySet().isEmpty()) {
					totalHeader.addElement(hcrTextField.prepareTextField(totalHeaderTextField, jasperDesign));
				}
				addImages(jasperDesign, totalHeader, totalHeaderJson);
				HCRUtils.applyStyleReference(totalHeaderJson, totalHeader::setStyleNameReference);
				colGroup.setTotalHeader(totalHeader);
			}

			try {
				crosstab.addColumnGroup(colGroup);
			} catch (JRException e) {
				String message = "Error occurred while adding the column group [name = " + colGroupJson.get("name").getAsString() + " ] " ;
				logger.error(message  + " .Root cause : {}",e);
				throw new EfwServiceException(message);
			}
		}
	}

	private JRDesignCrosstabBucket prepareBucket(JsonObject bucketJson) {
		JRDesignCrosstabBucket bucket = new JRDesignCrosstabBucket();
		bucket.setExpression(new JRDesignExpression(bucketJson.get("expression").getAsString()));
		bucket.setValueClassName(bucketJson.get("className").getAsString());
		return bucket;
	}

	private void setMeasures(JRDesignCrosstab crosstab, JsonObject json) {
		JsonArray measures = json.getAsJsonArray("measures");
		for (int index = 0; index < measures.size(); index++) {
			JsonObject measureJson = measures.get(index).getAsJsonObject();
			JRDesignCrosstabMeasure measure = new JRDesignCrosstabMeasure();
			measure.setName(measureJson.get("name").getAsString());
			measure.setValueClassName(measureJson.get("className").getAsString());
			measure.setValueExpression(new JRDesignExpression(measureJson.get("measureExpression").getAsString()));
			measure.setCalculation(CalculationEnum.valueOf(measureJson.get("calculation").getAsString().toUpperCase()));
			try {
				crosstab.addMeasure(measure);
			} catch (JRException ex) {
				String message = "Error occurred while adding the measure  [name = " + measureJson.get("name").getAsString() + " ] " ;
				logger.error(message  + " .Root cause : ",ex);
				throw new EfwServiceException(message);
			}
		}
	}
	
	
	public void addImages(JasperDesign jasperDesign , JRDesignCellContents designCell,  JsonObject cellJson) {
		if (cellJson.has("image")) {
			JsonArray images = GsonUtility.optJsonArray(cellJson, "image");
			for (JsonElement element : images) {
				JsonObject imageJson = element.getAsJsonObject();
				JRDesignImage jrDesignImage = hcrImage.prepareImage(jasperDesign, imageJson);
				designCell.addElement(jrDesignImage);
			}
		}
	}

}
