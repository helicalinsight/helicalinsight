package com.helicalinsight.efw.jasperintegration.bandcontents;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.*;
import org.springframework.stereotype.Component;

import java.awt.*;

/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
@Component
public class HCRLine implements IHCRBandContents {
    @Override
    public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
        addLineIfAny(band, bandJson);
    }

    private void addLineIfAny(JRDesignBand band, JsonObject bandJson) {
        if (bandJson.has("lines")) {
            JsonArray linesArray = bandJson.getAsJsonArray("lines");
            for (int index = 0; index < linesArray.size(); index++) {
                JsonObject eachLineObject = linesArray.get(index).getAsJsonObject();
                JRDesignLine designLine = prepareLine(eachLineObject);

                band.addElement(designLine);
            }
        }
    }

    private JRDesignLine prepareLine(JsonObject eachLineJson) {
        JRDesignLine designLine = new JRDesignLine();
        if (eachLineJson.has("fill"))
            designLine.setFill(FillEnum.SOLID);
        if (eachLineJson.has("lineDirection"))
            designLine.setDirection(LineDirectionEnum.getByName(eachLineJson.get("lineDirection").getAsString()));
        if (eachLineJson.has("lineStyle")) {
            JRPen linePen = designLine.getLinePen();
            if (eachLineJson.has("penLineWidth"))
                linePen.setLineWidth(Float.valueOf(eachLineJson.get("penLineWidth").getAsString()));
            linePen.setLineStyle(LineStyleEnum.getByName(eachLineJson.get("lineStyle").getAsString()));
        }
        designLine.setPrintRepeatedValues(GsonUtility.optBooleanValue(eachLineJson,"printRepeatedValues", true));
        HCRUtils.applyStyleReference(eachLineJson, designLine::setStyleNameReference);
        designLine.setWidth(eachLineJson.get("lineWidth").getAsInt());
        designLine.setHeight(eachLineJson.get("lineHeight").getAsInt());
        if (eachLineJson.has("mode"))
            designLine.setMode(ModeEnum.getByName(eachLineJson.get("mode").getAsString()));
        if (eachLineJson.has("lineForecolor"))
            designLine.setForecolor(Color.decode(eachLineJson.get("lineForecolor").getAsString()));
        if (eachLineJson.has("lineBackcolor"))
            designLine.setBackcolor(Color.decode(eachLineJson.get("lineBackcolor").getAsString()));
        if (eachLineJson.has("linePositionType")) {
            String linePositionType = eachLineJson.get("linePositionType").getAsString();
            designLine.setPositionType(PositionTypeEnum.getByName(linePositionType));
        }

        designLine.setX(eachLineJson.get("X").getAsInt());
        designLine.setY(eachLineJson.get("Y").getAsInt());
        if(eachLineJson.has("com.jaspersoft.studio.unit.height")) {
            designLine.getPropertiesMap().setProperty("com.jaspersoft.studio.unit.height", eachLineJson.get("com.jaspersoft.studio.unit.height").getAsString());
        }else{
            designLine.getPropertiesMap().setProperty("com.jaspersoft.studio.unit.height", "px");
        }
        
        JsonObject customSettings = GsonUtility.optJsonObject(eachLineJson, "customSettings");
        HCRUtils.applyCustomProperties(designLine, customSettings);
        
        
        return designLine;
    }
}
