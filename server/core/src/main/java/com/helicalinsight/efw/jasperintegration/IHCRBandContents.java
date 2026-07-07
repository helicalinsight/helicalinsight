package com.helicalinsight.efw.jasperintegration;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.base.JRBaseElement;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import java.awt.*;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;


/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
public interface IHCRBandContents {
	
	public static final String DATASET_RUN = "dataSetRun";

    void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign);

    default void setElementPositionType(JsonObject dataElement, JRDesignElement designElement) {
        if (dataElement.has("positionType"))
            designElement.setPositionType(PositionTypeEnum.getByName(dataElement.get("positionType").getAsString()));
    }

    default void prepareCommonTextProperties(JsonObject eachTextJson, JRDesignTextElement textField) {
        if (eachTextJson.has("horizontalTextAlign")) {
            textField.setHorizontalTextAlign(HorizontalTextAlignEnum.getByName(eachTextJson.get("horizontalTextAlign").getAsString()));
        }
        if (eachTextJson.has("verticalTextAlign")) {
            textField.setVerticalTextAlign(VerticalTextAlignEnum.getByName(eachTextJson.get("verticalTextAlign").getAsString()));
        }
        if (eachTextJson.has("propertyExpression")) {
            JRDesignPropertyExpression propertyExpression = new JRDesignPropertyExpression();
            JRDesignExpression designExpression = new JRDesignExpression();
            designExpression.setText(eachTextJson.get("propertyExpression").getAsString());
            propertyExpression.setValueExpression(designExpression);
            textField.addPropertyExpression(propertyExpression);
        }
        prepareRotation(eachTextJson, textField);
        textField.setPrintInFirstWholeBand(GsonUtility.optBooleanValue(eachTextJson,"printInFirstWholeBand", false));
        if (eachTextJson.has("printWhenExpression")) {
            JRDesignExpression designExpression = new JRDesignExpression();
            designExpression.setText(GsonUtility.optString(eachTextJson,"printWhenExpression"));
            textField.setPrintWhenExpression(designExpression);
        }
        textField.setPrintWhenDetailOverflows(GsonUtility.optBooleanValue(eachTextJson,"printWhenDetailOverflows", false));
      /*  JRDesignGroup group = new JRDesignGroup();
        group.setName("");
        textField.setPrintWhenGroupChanges();*/
       
        JsonObject borderJson = GsonUtility.optJsonObject(eachTextJson, HCRUtils.PROPERTY_BORDER);
        HCRUtils.movePaddingFromBorder(borderJson, eachTextJson);
        HCRUtils.prepareBorder((JRBoxContainer)textField, borderJson); 
        HCRUtils.preparePadding((JRBoxContainer)textField, GsonUtility.optJsonObject(eachTextJson, HCRUtils.PROPERTY_PADDING));
        
        prepareStretchType(eachTextJson, textField);
        textField.setX(GsonUtility.optIntValue(eachTextJson,"X", 0));
        textField.setY(GsonUtility.optIntValue(eachTextJson,"Y", 10));
        textField.setWidth(GsonUtility.optIntValue(eachTextJson,"textWidth", 50));
        textField.setHeight(GsonUtility.optIntValue(eachTextJson,"textHeight", 30));
		if (eachTextJson.has("fontName")) {
			String fontName = GsonUtility.optString(eachTextJson, "fontName");
			textField.setFontName(fontName);

			if (HCRUtils.fontExists(fontName)) {
				textField.setPdfFontName(fontName);
			}
		}
		
		HCRUtils.applyStyleReference(eachTextJson, textField::setStyleNameReference);
        
		if (eachTextJson.has("bold"))
            textField.setBold(eachTextJson.get("bold").getAsBoolean() ? Boolean.TRUE : Boolean.FALSE);
        if (eachTextJson.has("mode"))
            textField.setMode(ModeEnum.getByName(eachTextJson.get("mode").getAsString()));
        if (eachTextJson.has("strikeThrough"))
            textField.setStrikeThrough(eachTextJson.get("strikeThrough").getAsBoolean() ? Boolean.TRUE : Boolean.FALSE);
        if (eachTextJson.has("underline"))
            textField.setUnderline(eachTextJson.get("underline").getAsBoolean() ? Boolean.TRUE : Boolean.FALSE);
        if (eachTextJson.has("italic"))
            textField.setItalic(eachTextJson.get("italic").getAsBoolean() ? Boolean.TRUE : Boolean.FALSE);
        if (eachTextJson.has("textBackcolor"))
            textField.setBackcolor(Color.decode(eachTextJson.get("textBackcolor").getAsString()));
        if (eachTextJson.has("textForecolor"))
            textField.setForecolor(Color.decode(eachTextJson.get("textForecolor").getAsString()));
        textField.setFontSize(eachTextJson.has("textFontSize") ? (float) eachTextJson.get("textFontSize").getAsInt() : 20f);
        if (eachTextJson.has("markUp"))
            textField.setMarkup(eachTextJson.get("markUp").getAsString());
        if (eachTextJson.has("printRepeatedValues"))
            textField.setPrintRepeatedValues(eachTextJson.get("printRepeatedValues").getAsBoolean());
        if (eachTextJson.has("removeLineWhenBlank"))
            textField.setRemoveLineWhenBlank(eachTextJson.get("removeLineWhenBlank").getAsBoolean());
        if (eachTextJson.has("key"))
            textField.setKey(eachTextJson.get("key").getAsString());
        if (eachTextJson.has("paragraph") && !eachTextJson.getAsJsonObject("paragraph").entrySet().isEmpty()) {
            JsonObject paragraphJson = eachTextJson.getAsJsonObject("paragraph");
            JRParagraph jrParagraph = textField.getParagraph();
            if (paragraphJson.has("lineSpacing"))
                jrParagraph.setLineSpacing(LineSpacingEnum.getByName(paragraphJson.get("lineSpacing").getAsString()));
            if (paragraphJson.has("firstLineIndent"))
                jrParagraph.setFirstLineIndent(paragraphJson.get("firstLineIndent").getAsInt());
            if (paragraphJson.has("leftIndent"))
                jrParagraph.setLeftIndent(paragraphJson.get("leftIndent").getAsInt());
            if (paragraphJson.has("lineSpacingSize"))
                jrParagraph.setLineSpacingSize((float) paragraphJson.get("lineSpacingSize").getAsInt());
            if (paragraphJson.has("rightIndent"))
                jrParagraph.setRightIndent(paragraphJson.get("rightIndent").getAsInt());
            if (paragraphJson.has("spacingAfter"))
                jrParagraph.setSpacingAfter(paragraphJson.get("spacingAfter").getAsInt());
            if (paragraphJson.has("spacingBefore"))
                jrParagraph.setSpacingBefore(paragraphJson.get("spacingBefore").getAsInt());
            if (paragraphJson.has("tabStopWidth"))
                jrParagraph.setTabStopWidth(paragraphJson.get("tabStopWidth").getAsInt());
            if (paragraphJson.has("tabStopAlignment") && paragraphJson.has("tabStopPosition")) {
               /* TabStop tabStop = new TabStop();
                tabStop.setAlignment(TabStopAlignEnum.getByName(paragraphJson.get("tabStopAlignment").getAsString()));
                tabStop.setPosition(paragraphJson.get("tabStopPosition").getAsInt());
                jrParagraph.addTabStop(tabStop);*/
            }
        }

    }

    default void prepareRotation(JsonObject json, JRDesignTextElement element) {
        if (json.has("rotationType")) {
            element.setRotation(RotationEnum.getByName(json.get("rotationType").getAsString()));
        }
    }

    default void prepareStretchType(JsonObject componentElementProperties, JRBaseElement componentElement) {
        if (componentElementProperties.has("stretchType")) {
            componentElement.setStretchType(StretchTypeEnum.getByName(componentElementProperties.get("stretchType").getAsString()));
        }

    }


    default void prepareGroupChanges(JasperDesign jasperDesign, JsonObject eachJson, JRDesignElement designElement) {
        Map<String, JRGroup> groupsMap = jasperDesign.getGroupsMap();
        if (eachJson.has("printWhenGroupChanges")) {
            designElement.setPrintWhenGroupChanges(groupsMap.get(eachJson.get("printWhenGroupChanges").getAsString()));
        }
    }
    
    default void evaluationGroup(JsonObject componentJson, JasperDesign jasperDesign, JRDesignElement element, String evaluationTime) {
        
    	if ("Group".equals(evaluationTime)) {
            Map<String, JRGroup> groupsMap = jasperDesign.getGroupsMap();
            
            String evaluationGroupName = GsonUtility.optStringValue(componentJson, "evaluationGroupName", "");
            
            if ( StringUtils.isBlank(evaluationGroupName)) return ;
            
            JRGroup group =  groupsMap.get(evaluationGroupName);
            if ( element instanceof JRDesignTextField textField) {
            	textField.setEvaluationGroup(group);
            }
            if (element instanceof JRDesignImage image) {
            	image.setEvaluationGroup(group);
            }
            if(element instanceof JRDesignChart chart) {
            	chart.setEvaluationGroup(group);
            }
        }
    }

}
