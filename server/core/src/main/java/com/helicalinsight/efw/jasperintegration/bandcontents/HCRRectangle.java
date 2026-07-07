package com.helicalinsight.efw.jasperintegration.bandcontents;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.springframework.stereotype.Component;

/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
@Component
public class HCRRectangle implements IHCRBandContents {
    @Override
    public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
        addRectangleIfAny(band, bandJson);
    }

    private void addRectangleIfAny(JRDesignBand band, JsonObject bandJson) {
        if (bandJson.has("rectangle")) {
            JsonArray rectangleArray = GsonUtility.optJsonArray(bandJson,"rectangle");
            if (rectangleArray != null) {
                for (int index = 0; index < rectangleArray.size(); index++) {
                    JsonObject eachRectangleObject = rectangleArray.get(index).getAsJsonObject();
                    JRDesignRectangle jrDesignRectangle = prepareRectangle(eachRectangleObject);
                    band.addElement(jrDesignRectangle);
                }
            }
        }
    }

    private JRDesignRectangle prepareRectangle(JsonObject eachRectangleJson) {
        JRDesignRectangle rectangle = new JRDesignRectangle();
        setElementPositionType(eachRectangleJson, rectangle);
        rectangle.setRadius(new Integer(23));
        
        JsonObject customSettings = GsonUtility.optJsonObject(eachRectangleJson, "customSettings");
        HCRUtils.applyCustomProperties(rectangle, customSettings);
        
        return rectangle;
    }
}
