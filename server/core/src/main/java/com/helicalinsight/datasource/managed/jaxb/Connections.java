package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.datasource.calcite.CalciteConnection;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by author on 26-Dec-14.
 *
 * @author Rajasekhar
 */
@XmlRootElement(name = "connections")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class Connections {

    @XmlAttribute
    private String mandatory = "true";

    @XmlElement(name = "jdbcConnection")
    private List<JdbcConnection> jdbcConnection;

    @XmlElement(name = "calciteConnection")
    private List<CalciteConnection> calciteConnection;

    @XmlElement(name = "jndiDataSource")
    private List<JndiDataSource> jndiDataSource;

    @XmlElement(name = "hikariDataSource")
    private List<HikariProperties> hikariProperties;

    @XmlElement(name = "tomcatJdbcDataSource")
    private List<TomcatPoolProperties> tomcatPoolProperties;

    @XmlElement(name = "nosqlDataSource")
    private List<NoSqlProperties> noSqlProperties;

    public Connections() {
    }

    public List<NoSqlProperties> getNoSqlProperties() {
        return noSqlProperties;
    }

    public void setNoSqlProperties(List<NoSqlProperties> noSqlProperties) {
        this.noSqlProperties = noSqlProperties;
    }

    public List<JdbcConnection> getJdbcConnection() {
        return jdbcConnection;
    }

    public void setJdbcConnection(List<JdbcConnection> jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    public List<JndiDataSource> getJndiDataSource() {
        return jndiDataSource;
    }

    public void setJndiDataSource(List<JndiDataSource> jndiDataSource) {
        this.jndiDataSource = jndiDataSource;
    }

    public List<HikariProperties> getHikariProperties() {
        return hikariProperties;
    }

    public void setHikariProperties(List<HikariProperties> hikariProperties) {
        this.hikariProperties = hikariProperties;
    }

    public List<TomcatPoolProperties> getTomcatPoolProperties() {
        return tomcatPoolProperties;
    }

    public void setTomcatPoolProperties(List<TomcatPoolProperties> tomcatPoolProperties) {
        this.tomcatPoolProperties = tomcatPoolProperties;
    }

    public List<CalciteConnection> getCalciteConnection() {
        return calciteConnection;
    }

    public void setCalciteConnection(List<CalciteConnection> calciteConnection) {
        this.calciteConnection = calciteConnection;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connections that = (Connections) o;

        if (calciteConnection != null ? !calciteConnection.equals(that.calciteConnection) : that.calciteConnection !=
                null)
            return false;
        if (hikariProperties != null ? !hikariProperties.equals(that.hikariProperties) : that.hikariProperties != null)
            return false;
        if (jdbcConnection != null ? !jdbcConnection.equals(that.jdbcConnection) : that.jdbcConnection != null)
            return false;
        if (jndiDataSource != null ? !jndiDataSource.equals(that.jndiDataSource) : that.jndiDataSource != null)
            return false;
        if (mandatory != null ? !mandatory.equals(that.mandatory) : that.mandatory != null) return false;
        if (tomcatPoolProperties != null ? !tomcatPoolProperties.equals(that.tomcatPoolProperties) : that
                .tomcatPoolProperties != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mandatory != null ? mandatory.hashCode() : 0;
        result = 31 * result + (jdbcConnection != null ? jdbcConnection.hashCode() : 0);
        result = 31 * result + (calciteConnection != null ? calciteConnection.hashCode() : 0);
        result = 31 * result + (jndiDataSource != null ? jndiDataSource.hashCode() : 0);
        result = 31 * result + (hikariProperties != null ? hikariProperties.hashCode() : 0);
        result = 31 * result + (tomcatPoolProperties != null ? tomcatPoolProperties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Connections{" +
                "mandatory='" + mandatory + '\'' +
                ", jdbcConnection=" + jdbcConnection +
                ", calciteConnection=" + calciteConnection +
                ", jndiDataSource=" + jndiDataSource +
                ", hikariProperties=" + hikariProperties +
                ", tomcatPoolProperties=" + tomcatPoolProperties +
                '}';
    }
}
