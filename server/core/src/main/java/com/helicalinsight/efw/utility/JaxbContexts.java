package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.exceptions.EfwServiceException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by author on 03-08-2015.
 *
 * @author Rajasekhar
 */
public enum JaxbContexts {

    JAXB_CONTEXTS;

    private static final ConcurrentHashMap<String, JAXBContext> CONTEXTS_MAP = new ConcurrentHashMap<>(16, 0.9f, 1);

    public static JaxbContexts getJaxbContexts() {
        return JAXB_CONTEXTS;
    }

    public <T> JAXBContext getContextForClass(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("The type parameter can't be null.");
        }
        String typeName = type.getName();
        JAXBContext jaxbContext = CONTEXTS_MAP.get(typeName);
        if (jaxbContext != null) {
            return jaxbContext;
        } else {
            try {
                jaxbContext = JAXBContext.newInstance(type);
                if (jaxbContext == null) {
                    throw new EfwServiceException("Fatal error. Couldn't obtain the jaxb context.");
                }
                synchronized (this) {
                    CONTEXTS_MAP.put(typeName, jaxbContext);
                }
                return jaxbContext;
            } catch (JAXBException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
