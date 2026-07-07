package com.helicalinsight.adhoc.metadata;

import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * This component handles the saving of metadata security related details.
 * @author Somen
 * Created on 8/4/2016.
 */
public class MetadataSecuritySaveHandler implements IComponent {
    private final static String VALIDATOR = "com.helicalinsight.adhoc.metadata.MetadataSecurityAccessValidator";
    private final static String HANDLER = "com.helicalinsight.adhoc.metadata.MetadataSecurityAccessHandler";
    /**
     * Method helps to save metadata security related details.
     * @param jsonFormData          string containing formData , metadata related details
     */
    @Override
    public String executeComponent(String jsonFormData) {
        IComponent component;/* = FactoryMethodWrapper.getTypedInstance(VALIDATOR, IComponent.class);
        if (component == null) {
            throw new RuntimeException("Couldn't get Instance of MetadataSecurityAccessValidator.");
        }

        component.executeComponent(jsonFormData);
*/
        component = FactoryMethodWrapper.getTypedInstance(HANDLER, IComponent.class);
        if (component == null) {
            throw new RuntimeException("Couldn't get Instance of MetadataSecurityAccessHandler.");
        }
        return component.executeComponent(jsonFormData);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
