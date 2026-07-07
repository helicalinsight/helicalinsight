package com.helicalinsight.scheduling.model;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.io.Serializable;

/**
 * Created by author on 4/20/2020.
 *
 * @author Rajesh
 */
public class CustomIdGenerator extends SequenceStyleGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {

        if (obj instanceof Schedules) {
            Schedules identifiable = (Schedules) obj;
            Serializable id = identifiable.getId();
            if (id != null) {
                return id;
            }
        }
        return (Serializable) super.generate(session, obj);
    }
}
