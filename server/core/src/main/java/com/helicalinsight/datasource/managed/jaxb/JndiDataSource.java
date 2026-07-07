package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 26-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class JndiDataSource {

	@XmlAttribute
	private int id;

	@XmlAttribute
	private String name;

	@XmlAttribute
	private String type = GlobalJdbcType.STATIC_DATASOURCE;

	@XmlAttribute
	private String baseType = GlobalJdbcType.TYPE;

	@XmlElement
	private String visible;

	@XmlElement
	private String dataSourceProvider;

	@XmlElement
	private String lookUpName;

	@XmlElement
	private String driverClassName;

	@XmlElement
	private Security security;

	@XmlElement
	private Security.Share share;

	@XmlElement
	private String databaseDialect;

	@XmlElement
	private String databaseName;

	public JndiDataSource() {
	}

	public JndiDataSource(int id, String name, String baseType, String dataSourceProvider, String lookUpName,
			String databaseDialect, String databaseName) {
		this.id = id;
		this.name = name;
		this.baseType = baseType;
		this.dataSourceProvider = dataSourceProvider;
		this.lookUpName = lookUpName;
		this.databaseDialect = databaseDialect;
		this.databaseName = databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseName() {

		return databaseName;
	}

	public String getDatabaseDialect() {
		return databaseDialect;
	}

	public void setDatabaseDialect(String databaseDialect) {
		this.databaseDialect = databaseDialect;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBaseType() {
		return baseType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getDataSourceProvider() {
		return dataSourceProvider;
	}

	public void setDataSourceProvider(String dataSourceProvider) {
		this.dataSourceProvider = dataSourceProvider;
	}

	public String getLookUpName() {
		return lookUpName;
	}

	public void setLookUpName(String lookUpName) {
		this.lookUpName = lookUpName;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public Security.Share getShare() {
		return share;
	}

	public void setShare(Security.Share share) {
		this.share = share;
	}

	@Override
	public String toString() {
		return "JndiDataSource{" + "id=" + id + ", name='" + name + '\'' + ", type='" + type + '\'' + ", baseType='"
				+ baseType + '\'' + ", visible='" + visible + '\'' + ", dataSourceProvider='" + dataSourceProvider
				+ '\'' + ", lookUpName='" + lookUpName + '\'' + ", driverClassName='" + driverClassName + '\''
				+ ", security=" + security + ", share=" + share + ", databaseDialect='" + databaseDialect + '\''
				+ ", databaseName='" + databaseName + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		JndiDataSource that = (JndiDataSource) o;

		if (id != that.id)
			return false;
		if (!baseType.equals(that.baseType))
			return false;
		if (!dataSourceProvider.equals(that.dataSourceProvider))
			return false;
		if (!databaseDialect.equals(that.databaseDialect))
			return false;
		if (!databaseName.equals(that.databaseName))
			return false;
		if (!driverClassName.equals(that.driverClassName))
			return false;
		if (!lookUpName.equals(that.lookUpName))
			return false;
		if (!name.equals(that.name))
			return false;
		if (!security.equals(that.security))
			return false;
		if (!share.equals(that.share))
			return false;
		if (!type.equals(that.type))
			return false;
		if (!visible.equals(that.visible))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + name.hashCode();
		result = 31 * result + type.hashCode();
		result = 31 * result + baseType.hashCode();
		// result = 31 * result + visible.hashCode();
		result = 31 * result + dataSourceProvider.hashCode();
		result = 31 * result + lookUpName.hashCode();
		// result = 31 * result + driverClassName.hashCode();
		// result = 31 * result + security.hashCode();
		// result = 31 * result + share.hashCode();
		result = 31 * result + databaseDialect.hashCode();
		result = 31 * result + databaseName.hashCode();
		return result;
	}

}
