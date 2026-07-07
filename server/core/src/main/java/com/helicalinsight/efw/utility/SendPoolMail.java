package com.helicalinsight.efw.utility;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.smtp.pool.SmtpConnectionValidator;
import com.helicalinsight.efw.smtp.pool.SmtpPoolMonitorService;
import com.helicalinsight.efw.smtp.pool.PoolConfigs;
import com.helicalinsight.efw.smtp.pool.SmtpConnectionPool;
import com.helicalinsight.efw.smtp.transport.connection.ClosableSmtpConnection;
import com.helicalinsight.efw.smtp.transport.factory.SmtpConnectionFactory;
import com.helicalinsight.efw.smtp.transport.factory.SmtpConnectionFactoryBuilder;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The class is able to send e-mail messages to a single recipient or multiple
 * recipients with single or multiple attachments.
 *
 * @author Sharad Sinha
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */

@Component
public class SendPoolMail {
    private static final Logger logger = LoggerFactory.getLogger(SendPoolMail.class);
    private PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
    private Map<String, String> propertiesMap = propertiesFileReader.read("Mail", "mailConfiguration.properties");
    private Map<String,Long> durationStat= new HashMap<>();
    private String from;
    private final AtomicBoolean smtpAuthenticated = new AtomicBoolean(true);
    private final AtomicInteger mailSend= new AtomicInteger(0);
    private final AtomicInteger mailFailed= new AtomicInteger(0);
    private final JsonObject settingsJson = JsonUtils.newGetSettingsJson();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    

    public Map<String, Long> getDurationStat() {
        return durationStat;
    }

    public void setDurationStat(Map<String, Long> durationStat) {
        this.durationStat = durationStat;
    }

   
    public Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }

    public int getMailSend() {
        return mailSend.get();
    }

    public int getMailFailed() {
        return mailFailed.get();
    }

    private SmtpConnectionPool smtpConnectionPool;
    private Thread monitorThread;


    public SendPoolMail() {
        logger.debug("Sending mail via pool configs");
        Properties properties = new Properties();

        String hostName = propertiesMap.get("hostName");
        String port = propertiesMap.get("port");
        this.from = propertiesMap.get("from");
        String isAuthenticated = propertiesMap.get("isAuthenticated");
        String isSSLEnabled = propertiesMap.get("isSSLEnabled");
        String user = propertiesMap.get("user");
        String password = propertiesMap.get("password");
        String enabledDebug = propertiesMap.get("debug");
        String sslProtocols = propertiesMap.get("sslProtocols");
        String ttlsEnabled = propertiesMap.getOrDefault("ttlsEnabled","false");
        String ttlsRequired = propertiesMap.getOrDefault("ttlsRequired","false");
        String keepAlive = propertiesMap.get("keepAlive");
        String quitWait = propertiesMap.get("quitWait");
        String mailProtocol = propertiesMap.getOrDefault("mailProtocol", "smtp");

        properties.put("mail.smtp.user", user);
        properties.put("mail.smtp.auth", isAuthenticated);
        properties.put("mail.debug", enabledDebug);
        properties.put("mail.smtp.starttls.enable", ttlsEnabled);
        properties.put("mail.smtp.starttls.required",ttlsRequired);
        properties.put("mail.smtp.host", hostName);
        properties.put("mail.smtp.port", port);
        
        properties.put("mail.smtp.ssl.enable", isSSLEnabled);
        properties.put("mail.smtp.ssl.protocols", sslProtocols);
        
        properties.put("mail.smtp.keepalive", keepAlive);
        properties.put("mail.smtp.quitwait", quitWait);

        Session aSession = Session.getInstance(properties);
        aSession.setDebug(Boolean.valueOf(enabledDebug));
        SmtpConnectionFactory factory = SmtpConnectionFactoryBuilder.newSmtpBuilder()
                .session(aSession)
                .protocol(mailProtocol)
                .host(hostName)
                .port(Integer.valueOf(port))
                .username(user)
                .password(password).build();
        

        GenericObjectPoolConfig<?> config = PoolConfigs.standardConfig(propertiesMap);
        
        SmtpPoolMonitorService poolMonitorService = new SmtpPoolMonitorService();
        
        long monitorPeriod = GsonUtility.optIntValue(settingsJson, "poolMonitorInterval", 60);
        
        poolMonitorService.setMonitoringPeriod(monitorPeriod);
        
        if(!SmtpConnectionValidator.validate(factory,config.clone())) {
        	smtpAuthenticated.set(false);
        	LocalDateTime now = LocalDateTime.now();
            String formattedTime = now.format(FORMATTER);
        	poolMonitorService.setErrorMessage("Mail Authentication failed, please update the credentials and retry. Last checked : " + formattedTime);
        }
        
        if (smtpAuthenticated.get()) {
        	smtpConnectionPool = new SmtpConnectionPool(factory, config);
        	poolMonitorService.setConnectionPool(smtpConnectionPool);
        }
        
        monitorThread = new HIManagedThread(poolMonitorService);
        monitorThread.setName("SmtpConnectionMonitorService");
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public boolean sendMessage(String[] recipients, String[] attachments, String subject, String body) {
        
    	if ( !smtpAuthenticated.get()) {
    		logger.error("Mail Authentication failed, please update the credentials and retry");
    		throw new EfwServiceException("Mail Authentication failed, please update the credentials and retry");
    	}
    	
    	long now = System.currentTimeMillis();
        try (ClosableSmtpConnection transport = smtpConnectionPool.borrowObject()) {

            MimeMessage mimeMessage = new MimeMessage(transport.getSession());
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            for (int index = 0; index < recipients.length; index++) {
                mimeMessageHelper.addTo(recipients[index]);
            }
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);

            mimeMessageHelper.setText(body, true);
            if (attachments != null) {
                for (int index = 0; index < attachments.length; index++) {
                    File attachmentFile = new File(attachments[index]);
                    mimeMessageHelper.addAttachment(attachmentFile.getName(), attachmentFile);
                }
            }
            mimeMessageHelper.setSentDate(new Date());

            transport.sendMessage(mimeMessage);
            long then=System.currentTimeMillis();

            mailSend.getAndIncrement();
            durationStat.put(String.valueOf(mailSend),then-now);
        } catch (Exception e) {
            mailFailed.getAndIncrement();
            e.printStackTrace();
            
            if ( containsAuthenticationException(e)) {
            	smtpAuthenticated.set(false);
            	smtpConnectionPool.close();
            	logger.error("SMTP Authentication failed. Pool shut down to prevent lockout.");
            	throw new EfwServiceException("Mail Authentication failed, please update the credentials and retry");
            }
            else {
            	throw new EfwServiceException("Could not send email");
            }
        }
        return true;
    }


    @PreDestroy
    public void destroy() {
    	if (smtpConnectionPool != null) {
    		smtpConnectionPool.close();
    	}
    }
    
    private boolean containsAuthenticationException(Throwable t) {
        while (t != null) {
            if (t instanceof AuthenticationFailedException) return true;
            t = t.getCause();
        }
        return false;
    }

}
