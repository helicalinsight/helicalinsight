package com.helicalinsight.efw.jasperintegration.bandcontents;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.ExpressionTypeEnum;

import org.springframework.stereotype.Component;


/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
@Component
public class HCRTextField implements IHCRBandContents {
    @Override
    public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
        addTextFieldIfAny(band, bandJson, jasperDesign);
    }

    private void addTextFieldIfAny(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
        if (bandJson.has("textField")) {
            JsonArray textFieldArray = bandJson.getAsJsonArray("textField");
            for (int index = 0; index < textFieldArray.size(); index++) {
                JsonObject eachTextField = textFieldArray.get(index).getAsJsonObject();
                JRDesignTextField textField = prepareTextField(eachTextField, jasperDesign);
                band.addElement(textField);
            }
        }
    }

    public JRDesignTextField prepareTextField(JsonObject eachTextField, JasperDesign jasperDesign) {
        JRDesignTextField textField = new JRDesignTextField();
        if (eachTextField.has("evaluationTime")) {
            String evaluationTime = eachTextField.get("evaluationTime").getAsString();
            textField.setEvaluationTime(EvaluationTimeEnum.getByName(evaluationTime));
            evaluationGroup(eachTextField, jasperDesign, textField, evaluationTime);
        }
        textField.setStretchWithOverflow(GsonUtility.optBooleanValue(eachTextField,"stretchWithOverFlow", false));
        textField.setBlankWhenNull(GsonUtility.optBooleanValue(eachTextField,"blankWhenNull", false));
        if (eachTextField.has("pattern"))
            textField.setPattern(eachTextField.get("pattern").getAsString());
        if (eachTextField.has("patternExpression")) {
            JRDesignExpression expression = new JRDesignExpression();
            expression.setText(eachTextField.get("patternExpression").getAsString());
            textField.setPatternExpression(expression);
        }
        prepareGroupChanges(jasperDesign, eachTextField, textField);
        setElementPositionType(eachTextField, textField);
        prepareCommonTextProperties(eachTextField, textField);
        JRDesignExpression expression = new JRDesignExpression();
        expression.setType(ExpressionTypeEnum.getByName(GsonUtility.optStringValue(eachTextField,"expressionType", "default")));
        expression.setText(eachTextField.get("textFieldExpression").getAsString());
        textField.setExpression(expression);
        
        JsonObject customSettings = GsonUtility.optJsonObject(eachTextField, "customSettings");
        HCRUtils.applyCustomProperties(textField, customSettings);
        
        return textField;
    }
}
