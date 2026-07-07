package com.helicalinsight.scheduling.model;

import com.helicalinsight.datasource.model.GlobalConnections;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.io.Serializable;

/**
 * Created by Helical on 6/3/2021.
 */
public class CustomDataSourceIdGenerator extends SequenceStyleGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {

        if (obj instanceof GlobalConnections) {
            GlobalConnections identifiable = (GlobalConnections) obj;
            Serializable id = identifiable.getGlobalId();
            if (id != null) {
                return id;
            }
        }
        return (Serializable) super.generate(session, obj);
    }

}
