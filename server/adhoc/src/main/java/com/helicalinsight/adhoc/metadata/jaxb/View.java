package com.helicalinsight.adhoc.metadata.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * Created by author on 09-09-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "view")
@XmlAccessorType(XmlAccessType.FIELD)
public class View {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String alias;

    @XmlAttribute
    private Boolean hasStoredProcedure;


    @XmlElement(name = "paramValues")
    private List<KeyValue> keyValues;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        View view = (View) o;

        if (alias != null ? !alias.equals(view.alias) : view.alias != null) return false;
        if (hasStoredProcedure != null ? !hasStoredProcedure.equals(view.hasStoredProcedure) : view.hasStoredProcedure != null)
            return false;
        if (id != null ? !id.equals(view.id) : view.id != null) return false;
        if (keyValues != null ? !keyValues.equals(view.keyValues) : view.keyValues != null) return false;
        if (name != null ? !name.equals(view.name) : view.name != null) return false;
        if (query != null ? !query.equals(view.query) : view.query != null) return false;
        if (table != null ? !table.equals(view.table) : view.table != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (hasStoredProcedure != null ? hasStoredProcedure.hashCode() : 0);
        result = 31 * result + (keyValues != null ? keyValues.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (table != null ? table.hashCode() : 0);
        return result;
    }

    public List<KeyValue> getKeyValues() {

        return keyValues;
    }

    public void setKeyValues(List<KeyValue> keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public String toString() {
        return "View{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", hasStoredProcedure=" + hasStoredProcedure +
                ", query=" + query +
                ", table=" + table +
                '}';
    }


    public Boolean getHasStoredProcedure() {
        return hasStoredProcedure;
    }

    public void setHasStoredProcedure(Boolean hasStoredProcedure) {
        this.hasStoredProcedure = hasStoredProcedure;
    }

    @XmlElement
    private Query query;

    @XmlElement
    private Table table;

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Component
    @Scope("prototype")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Query {

        @XmlValue
        @XmlJavaTypeAdapter(AdapterCDATA.class)
        private String query;

        @XmlAttribute
        private String type;

        public String getType() {
            if (StringUtils.isEmpty(type)) {
                return "conditionIf";
            }
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getQuery() {
            //please do not change this getter as it customized to return query
          /*  if (this.type != null) {
                ISecureMetadata securityClass = MetadataSecurityObjectFactory.getSecurityClass(this.type);
                if (securityClass != null) {
                    query = securityClass.getFilters(query);
                }
            }*/
            return getUnprocessedQuery();
        }
        public String getUnprocessedQuery(){
            return query.replaceAll("\nfrom\n", "\n from \n").replaceAll("\nwhere\n", "\n where \n").replaceAll("\ngroup by\n", "\n group by \n").replaceAll("\nhaving\n", "\n having \n").replaceAll("\norder by\n", "\n order by \n");
        }

        public void setQuery(String query) {
            this.query = query;
        }

        @Override
        public String toString() {
            return "Query{" +
                    "query='" + query + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            Query object = (Query) other;

            return !(this.query != null ? !this.query.equals(object.query) : object.query != null);

        }

        @Override
        public int hashCode() {
            return query != null ? query.hashCode() : 0;
        }
    }
}

