package com.helicalinsight.adhoc.genericsql;

/**
 * Created by author on 11-09-2015.
 *
 * @author Rajasekhar
 */
final class DerivedTable {

    private final String name;

    private final String aliasName;

    private final String query;

    private final String type;

    @Override
    public String toString() {
        return "DerivedTable{" +
                "name='" + name + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", query='" + query + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DerivedTable that = (DerivedTable) o;

        if (aliasName != null ? !aliasName.equals(that.aliasName) : that.aliasName != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (aliasName != null ? aliasName.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public DerivedTable(String name, String aliasName, String query, String type) {
        this.name = name;
        this.aliasName = aliasName;
        this.query = query;
        this.type=type;
    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        return query;
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getType() {
        return type;
    }

}
