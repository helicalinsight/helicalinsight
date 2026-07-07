package com.helicalinsight.efw.jasperintegration.bandcontents;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.springframework.stereotype.Component;

/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
@Component
public class HCRStaticText implements IHCRBandContents {
    @Override
    public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
        addStaticTextIfAny(band, bandJson);
    }

    private void addStaticTextIfAny(JRDesignBand band, JsonObject bandJson) {
        if (bandJson.has("staticText")) {
            JsonArray staticTextArray = bandJson.getAsJsonArray("staticText");
            for (int index = 0; index < staticTextArray.size(); index++) {
                JsonObject eachStaticTextJson = staticTextArray.get(index).getAsJsonObject();
                JRDesignStaticText staticText = prepareStaticText(eachStaticTextJson);
                band.addElement(staticText);
            }
        }
    }

    public JRDesignStaticText prepareStaticText(JsonObject eachStaticTextJson) {
        JRDesignStaticText staticTextField = new JRDesignStaticText();
        staticTextField.setText(eachStaticTextJson.get("staticTextValue").getAsString());
        setElementPositionType(eachStaticTextJson, staticTextField);
        prepareCommonTextProperties(eachStaticTextJson, staticTextField);
        return staticTextField;
    }
}
