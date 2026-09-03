package com.helicalinsight.scheduling;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SendMail;
import com.helicalinsight.efw.utility.SendPoolMail;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sends system schedule execution results by email.
 * Recipients come from the schedule JSON {@code email.to} field, falling back to
 * {@code systemadmin} in System/Mail/mailConfiguration.properties.
 */
public final class SystemScheduleMailHelper {

    private static final Logger logger = LoggerFactory.getLogger(SystemScheduleMailHelper.class);

    public static final String MAIL_SYSTEMADMIN_KEY = "systemadmin";

    private SystemScheduleMailHelper() {
    }

    @SuppressWarnings("unchecked")
    public static void sendResultEmail(Map<?, ?> schedule, Object result) {
        if (schedule == null || !isEmailEnabled(schedule)) {
            return;
        }
        try {
            String[] recipients = resolveRecipients(schedule);
            if (recipients.length == 0) {
                logger.warn("System schedule email skipped: no recipients configured (email.to or mailConfiguration.systemadmin)");
                return;
            }
            String scheduleId = String.valueOf(schedule.get("id"));
            String subject = "System Schedule: " + scheduleId;
            String body = buildBody(scheduleId, result);

            PropertiesFileReader reader = new PropertiesFileReader();
            Map<String, String> mailProps = reader.read("Mail", "mailConfiguration.properties");
            if (mailProps == null) {
                logger.error("mailConfiguration.properties could not be loaded");
                return;
            }

            try {
                SendPoolMail poolMail = ApplicationContextAccessor.getBean(SendPoolMail.class);
                poolMail.sendMessage(recipients, null, subject, body);
            } catch (Exception poolEx) {
                logger.warn("SendPoolMail unavailable, falling back to SendMail", poolEx);
                SendMail mailClient = new SendMail();
                mailClient.sendMessage(
                        mailProps.get("hostName"),
                        mailProps.get("port"),
                        recipients,
                        mailProps.get("from"),
                        mailProps.get("isAuthenticated"),
                        mailProps.get("isSSLEnabled"),
                        mailProps.get("user"),
                        mailProps.get("password"),
                        subject,
                        body
                );
            }
            logger.info("System schedule {} result email sent to {}", scheduleId, String.join(",", recipients));
        } catch (Exception ex) {
            logger.error("Failed to send system schedule result email", ex);
        }
    }

    static boolean isEmailEnabled(Map<?, ?> schedule) {
        Object emailNode = schedule.get("email");
        if (emailNode == null) {
            return false;
        }
        if (emailNode instanceof Boolean bool) {
            return bool;
        }
        if (emailNode instanceof Map<?, ?> emailMap) {
            Object enabled = emailMap.get("enabled");
            if (enabled == null) {
                return true;
            }
            if (enabled instanceof Boolean bool) {
                return bool;
            }
            return "true".equalsIgnoreCase(String.valueOf(enabled));
        }
        return "true".equalsIgnoreCase(String.valueOf(emailNode));
    }

    static String[] resolveRecipients(Map<?, ?> schedule) {
        List<String> recipients = new ArrayList<>();
        Object emailNode = schedule.get("email");
        if (emailNode instanceof Map<?, ?> emailMap) {
            Object to = emailMap.get("to");
            if (to instanceof List<?> list) {
                for (Object item : list) {
                    if (item != null && StringUtils.isNotBlank(String.valueOf(item))) {
                        recipients.add(String.valueOf(item).trim());
                    }
                }
            } else if (to != null && StringUtils.isNotBlank(String.valueOf(to))) {
                for (String part : String.valueOf(to).split("[,;]")) {
                    if (StringUtils.isNotBlank(part)) {
                        recipients.add(part.trim());
                    }
                }
            }
        }
        if (recipients.isEmpty()) {
            String systemAdmin = readSystemAdminEmail();
            if (StringUtils.isNotBlank(systemAdmin)) {
                recipients.add(systemAdmin.trim());
            }
        }
        return recipients.toArray(new String[0]);
    }

    static String readSystemAdminEmail() {
        PropertiesFileReader reader = new PropertiesFileReader();
        Map<String, String> mailProps = reader.read("Mail", "mailConfiguration.properties");
        if (mailProps == null) {
            return null;
        }
        return mailProps.get(MAIL_SYSTEMADMIN_KEY);
    }

    static String buildBody(String scheduleId, Object result) {
        String resultText = result == null ? "No response returned." : String.valueOf(result);
        return "<html><body>"
                + "<p>System schedule <b>" + escapeHtml(scheduleId) + "</b> completed.</p>"
                + "<p><b>Response:</b></p>"
                + "<pre>" + escapeHtml(resultText) + "</pre>"
                + "</body></html>";
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
