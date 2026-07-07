package com.helicalinsight.efw.utility;

import com.helicalinsight.admin.customauth.DataSourceEncrypt;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author Somen
 *         Created  on 11/14/2017
 */
public class HISparkContext {

    private static JSONObject noSqlFile;

    static {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        String noSqlFilePath = JsonUtils.noSqlConfigPath();
        noSqlFile = processor.getJSONObject(noSqlFilePath, false);
    	String password1=noSqlFile.getString("password");
    	String password2=noSqlFile.getString("metaStorePassword");
    	if(password1!=null && !password1.equals(""))
			noSqlFile.put("password",DataSourceEncrypt.decrypt(password1));
		if(password2!=null && !password2.equals(""))
			noSqlFile.put("metaStorePassword",DataSourceEncrypt.decrypt(password2));
    }


    public static String getUrl() {
        return noSqlFile.getString("url");
    }

    public static String getMaster() {
        return noSqlFile.getString("master");
    }

    public static Boolean isEnabledWareHouse() {
        return noSqlFile.getBoolean("enableWarehouse");
    }

    public static String getAppName() {
        return noSqlFile.getString("appName");
    }

    public static String getMode() {
        return noSqlFile.getString("mode");
    }

    public static String getHost() {
        return noSqlFile.getString("host");
    }

    public static String getRestApiPort() {
        return noSqlFile.getString("restApiPort");
    }

    public static String getTemplateDirectory() {
        String templatePath = noSqlFile.optString("templatePath");
        String templateDirectory;
        if (templatePath != null) {
            templateDirectory = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File.separator + templatePath;
        } else {
            templateDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        }
        return templateDirectory;
    }

    public static String getDriverClass() {
        return noSqlFile.getString("driverClass");
    }


    public static String getSplitCharacter() {
        return noSqlFile.getString("splitCharacter");
    }

    public static String getInMemoryPort() {

        return noSqlFile.getString("inMemoryPort");
    }

    public static String getExecutorMemory() {

        return noSqlFile.getString("executorMemory");
    }


    public static String getOtherUrl() {
        return noSqlFile.getString("otherUrlPart");
    }

    public static String getSparkHome() {
        String sparkHome = noSqlFile.getString("sparkHome");
        if(StringUtils.isEmpty(sparkHome) || sparkHome.trim().equals("[]")){
             sparkHome = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "SparkHome";
        }
        return sparkHome;
    }

    public static String getMasterPort() {
        String masterPort = noSqlFile.getString("masterPort");
        String accuratePort = getAccuratePort(masterPort);
        AppStatistics.setPORT_MASTER(accuratePort);
        return accuratePort;
    }

    public static String getMasterWebUiPort() {
        String masterPort = noSqlFile.getString("masterWebUiPort");
        String accuratePort = getAccuratePort(masterPort);
        AppStatistics.setWEB_UI_PORT_MASTER(accuratePort);
        return accuratePort;
    }

    public static String getWorkerWebUiPort() {
        String masterPort = noSqlFile.getString("workerWebUiport");
        String accuratePort = getAccuratePort(masterPort);
        AppStatistics.setWEB_UI_PORT_WORKER(accuratePort);
        return accuratePort;
    }

    public static String getSparkWebUiPort() {
        String masterPort = noSqlFile.getString("applicationUiPort");
        String accuratePort = getAccuratePort(masterPort);
        AppStatistics.setWEB_UI_PORT_SPARK(accuratePort);
        return accuratePort;
    }

    private static String getAccuratePort(String masterPort) {
        while (true) {
            boolean portAvailable = ApplicationUtilities.isPortAvailable(masterPort);
            if (portAvailable) {
                return masterPort;
            } else {
                Integer integer = Integer.valueOf(masterPort);
                int newPort = integer + 1;
                masterPort = String.valueOf(newPort);
            }
        }
    }

    public static String getUsername() {
       return noSqlFile.getString("username");
    }

    public static String getPassword() {
       return noSqlFile.getString("password");
    }

    public static String getDatasourceUrl() {
        return HISparkContext.getUrl() + HISparkContext.getHost() + ":" + HISparkContext.getInMemoryPort() + HISparkContext.getOtherUrl().trim();
    }

    public static String getHadoopHome() {
        String hadoopHome = noSqlFile.getString("hadoopHome");
        if(StringUtils.isEmpty(hadoopHome)|| hadoopHome.trim().equals("[]")) {
         hadoopHome= ApplicationProperties.getInstance().getSystemDirectory() + File.separator+"HadoopHome";
        }
        return hadoopHome;
    }

    public static String getExecutorInstances() {
        return noSqlFile.optString("executorInstances");
    }

    public static long getMaxCore() {
        return noSqlFile.optInt("maxCore");
    }

    public static long getExecutorCores() {
        return noSqlFile.optInt("executorCore");
    }
    public static long getSampleSize() {
        return noSqlFile.optInt("sampleDataDumpSize",1000);
    }
    public static String getWareHousePath() {
        String wareHousePath = noSqlFile.getString("wareHousePath");
        if (StringUtils.isEmpty(wareHousePath.trim())) {
            wareHousePath = ApplicationProperties.getInstance().getSystemDirectory() + "/" + "hidw";
        }
        return wareHousePath;
    }

    public static String getMetaStoreJdbcUrl() {
        String metaStoreJdbcUrl = noSqlFile.getString("metaStoreJdbcUrl");
        if (StringUtils.isEmpty(metaStoreJdbcUrl.trim())) {
            String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
            solutionDirectory = solutionDirectory.replaceAll("hi-repository", "");
            metaStoreJdbcUrl = "jdbc:derby:" + solutionDirectory + "db" + File.separator + "hi_metastore;create=true";
        }
        return metaStoreJdbcUrl;
    }

    public static Boolean getExecuteAtStart() {
        return noSqlFile.getBoolean("executeAtStart");
    }

    public static String getMetaStoreUserName() {
        return noSqlFile.getString("metaStoreUserName");
    }

    public static String getMetaStorePassword() {
        return noSqlFile.getString("metaStorePassword");
    }

    public static String getMetaStoreDriver() {
        return noSqlFile.getString("metaStoreDriver");
    }
}
