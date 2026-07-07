package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.jreport.IHCRGenerator;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The HCRFileDownload class is responsible for handling the download of HCR reports in various formats.
 * It implements the IDownload interface and provides a method to download HCR reports in a specific format.
 * 
 * Created by author on 12/10/2019.
 * @author Rajesh
 */
@Component
@Scope("prototype")
public class HCRFileDownload implements IDownload {
   /* @Autowired
    private HcrPrint hcrPrint;*/

	/**
     * Downloads report in a specific format.
     *
     * @param jsonData  contains ResultSet.
     * @param formData  object containing details or report name, format, download type etc .
     * @return An object representing the response from the Jasper report generator.
     */
    @Override
    public Object downloadFormat(Object jsonData, JsonObject formData) {
        IHCRGenerator generator = (IHCRGenerator) ApplicationContextAccessor.getBean(JsonUtils.getHCRGeneratorType());
        JsonObject responseFromJasper = generator.generateHCReport(formData);

        return responseFromJasper;
    }
}
