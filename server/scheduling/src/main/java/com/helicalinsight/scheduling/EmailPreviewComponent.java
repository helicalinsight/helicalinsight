package com.helicalinsight.scheduling;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import com.helicalinsight.scheduling.utils.TemplateReplacer;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import net.sf.json.JSONObject;

import java.util.Map;

public class EmailPreviewComponent implements IComponent {


    @Override
    public String executeComponent(String formData) {

        JSONObject response = new JSONObject();
        JSONObject jsonFormData = JSONObject.fromObject(formData);
        String body = jsonFormData.getString("body");
        String recipients = jsonFormData.getString("recipients");
        String subject = jsonFormData.getString("subject");
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();

        Map<String, String> propertiesMap = propertiesFileReader.read("Mail", "mailConfiguration.properties");

        JSONObject reportParameters = jsonFormData.optJSONObject("reportParameters");
        if (reportParameters == null) {
            reportParameters = new JSONObject();
        }
        String reportName = jsonFormData.optString("reportName");
        int lastSpaceIndex = reportName.lastIndexOf(" ");
        if (lastSpaceIndex != -1) {
            reportName = reportName.substring(0, lastSpaceIndex);
        }


        if(StringUtils.isBlank(subject)){
            subject=propertiesMap.get("subject") + reportName;
        }
        if(StringUtils.isBlank(body)){
            body = propertiesMap.get("body");

        }
        reportParameters.put("reportName", reportName);
        jsonFormData.put("reportName", reportName);
        String dir = jsonFormData.optString("dir");
        if(StringUtils.isBlank(dir)){
            dir= jsonFormData.optString("reportDirectory");
        }
        reportParameters.put("reportDir", dir);
        jsonFormData.put("reportDir", dir);
		reportParameters.put("reportFileName",jsonFormData.optString("reportFile"));
        jsonFormData.put("reportFileName",jsonFormData.optString("reportFile"));
		reportParameters.put("reportNameWithExtension",jsonFormData.optString("reportFile"));
        jsonFormData.put("reportNameWithExtension",jsonFormData.optString("reportFile"));
		reportParameters.put("reportPath", dir +"/"+jsonFormData.getString("reportName"));
        jsonFormData.put("reportPath", dir +"/"+jsonFormData.getString("reportName"));
		reportParameters.put("reportUrl", dir +"/"+jsonFormData.getString("reportFile"));
        jsonFormData.put("reportUrl", dir +"/"+jsonFormData.getString("reportFile"));


        body = TemplateReplacer.replaceEmailComponents(jsonFormData.toString(), body,false);
        body = TemplateReplacer.replaceEmailComponents(reportParameters.toString(), body,true);


        recipients = TemplateReplacer.replaceEmailComponents(jsonFormData.toString(), recipients,true);
        recipients = recipients.replaceAll("'", "");


        subject = TemplateReplacer.replaceEmailComponents(jsonFormData.toString(), subject,false);
        subject = TemplateReplacer.replaceEmailComponents(reportParameters.toString(), subject,true);

        response.put("subject", subject);
        response.put("recipients", recipients);
        response.put("body", body);
        return response.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

}
