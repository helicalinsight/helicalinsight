package com.helicalinsight.adhoc.utils;

import com.helicalinsight.efwce.DashboardIOHandler;
import com.helicalinsight.datasource.managed.jaxb.EFWD;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JsonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Utility class for writing EFWD files.
 * This class provides methods to create EFWD files and handle EFWD-related operations.
 *
 * @author Rajesh
 * @since 2019-04-09
 */
public class EFWDWriterUtils {
    private final String driver;
    private final String url;
    private final String user;
    private final String pass;
    private int maxId;

    public EFWDWriterUtils(String driver, String url, String user, String pass) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    private void createEfwd(File efwdFile) {

            synchronized (DashboardIOHandler.class) {
                try {
                    String connectionsXmlFile = JsonUtils.getGlobalConnectionsPath();
                    JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();




                    JAXBContext jaxbContext = jaxbContexts.getContextForClass(EFWD.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    File xml = new File(connectionsXmlFile);
                    EFWD efwd = (EFWD) unmarshaller.unmarshal(xml);


                } catch (JAXBException jax) {
                    jax.printStackTrace();
                }

            }
        }
    }
