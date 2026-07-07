package com.helicalinsight.efw.jasperintegration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.SplitTypeEnum;

/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
public class BandFactory {
    private JsonObject bandJson;
    private JasperDesign jasperDesign;

    public BandFactory(JsonObject bandJson, JasperDesign jasperDesign) {
        this.bandJson = bandJson;
        this.jasperDesign = jasperDesign;
    }

    public JRDesignBand getBand() {

        JRDesignBand band = new JRDesignBand();
        band.setHeight(GsonUtility.optIntValue(bandJson,"bandHeight", 1));
        if (bandJson.has("splitType"))
            band.setSplitType(SplitTypeEnum.getByName(bandJson.get("splitType").getAsString()));

        JsonArray jasperBandContent = JsonUtils.getHCRBandContent();
        for (int index = 0; index < jasperBandContent.size(); index++) {
            JsonObject eachBandJson = jasperBandContent.get(index).getAsJsonObject();
            String beanName = eachBandJson.get("bean").getAsString();
            IHCRBandContents bean = (IHCRBandContents) ApplicationContextAccessor.getBean(beanName);
            bean.processContent(band, bandJson, jasperDesign);

        }
        return band;
    }

}
