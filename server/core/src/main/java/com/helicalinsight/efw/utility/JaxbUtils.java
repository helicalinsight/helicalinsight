package com.helicalinsight.efw.utility;

import com.google.gson.Gson;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
public class JaxbUtils {
    static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    public static <T> void marshal(T pojo, File location) {
        JAXBContext jaxbContext;
        JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
        try {
            jaxbContext = jaxbContexts.getContextForClass(pojo.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            String charsetName = ControllerUtils.defaultCharSet();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charsetName);
            CharacterEscapeHandler characterEscapeHandler = new CharacterEscapeHandler() {
                @Override
                public void escape(@NotNull char[] array, int i, int j, boolean flag,
                                   @NotNull Writer writer) throws IOException {
                    writer.write(array, i, j);
                }
            };
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), characterEscapeHandler);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(location), charsetName);
            marshaller.marshal(pojo, writer);
            writer.flush();
            writer.close();
        } catch (Exception exception) {
            throw new ConfigurationException("Could not write the xml file in the " +
                    "location " + location.toString().replace(applicationProperties.getSolutionDirectory(), ""),exception);
        }
    }


    public static <T> void marshal1(T pojo, File location) {
        JAXBContext jaxbContext;
        JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
        try {
            jaxbContext = jaxbContexts.getContextForClass(pojo.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            String charsetName = ControllerUtils.defaultCharSet();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charsetName);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(location), charsetName);
            marshaller.marshal(pojo, writer);
            writer.flush();
            writer.close();
        } catch (Exception exception) {
            throw new ConfigurationException("Could not write the xml file in the " +
                    "location " + location.toString().replace(applicationProperties.getSolutionDirectory(), ""),exception);
        }
    }


    @Nullable
    public static <T> T jsonStringToObject(Class<T> clazz, @Nullable String fileAsJson) {
        if (fileAsJson == null) {
            return null;
        }
        return new Gson().fromJson(fileAsJson, clazz);
    }

    public static <T> String getFileAsGson(Class<T> clazz, @Nullable File resource) {
        if (resource == null) {
            throw new IllegalArgumentException("File can't be null");
        }
        T t = unMarshal(clazz, resource);
        return new Gson().toJson(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> T unMarshal(Class<T> clazz, File location) {
        JAXBContext jaxbContext;
        JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
        try {
            jaxbContext = jaxbContexts.getContextForClass(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            //noinspection unchecked
            return (T) unmarshaller.unmarshal(location);
        } catch (JAXBException ex) {
            throw new ConfigurationException("Could not convert the xml to java object for the location " + location,
                    ex);
        }
    }
}
