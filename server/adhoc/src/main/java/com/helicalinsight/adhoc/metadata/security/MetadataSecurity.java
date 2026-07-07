package com.helicalinsight.adhoc.metadata.security;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Somen
 *         Created on 9/9/2015.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "access")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class MetadataSecurity {

    @XmlElement(name = "expression")
    private List<SecurityExpression> expressions;

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MetadataSecurity)) return false;

        MetadataSecurity that = (MetadataSecurity) other;

        if (expressions != null ? !expressions.equals(that.expressions) : that.expressions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return expressions != null ? expressions.hashCode() : 0;
    }

    public List<SecurityExpression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<SecurityExpression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public String toString() {
        return "MetadataSecurity{" +
                "expressions=" + expressions +
                '}';
    }
}
