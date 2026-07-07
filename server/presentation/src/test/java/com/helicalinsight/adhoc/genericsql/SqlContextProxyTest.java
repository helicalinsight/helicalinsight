package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SqlContextProxyTest {


	@Mock
	private SqlQueryContext context;

	private SqlContextProxy proxy;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		proxy = new SqlContextProxy(context);
	}

	@Test
	public void testQuotes() {
		String column = "columnName";
		when(context.quotes(column)).thenReturn("`columnName`");
		assertEquals("`columnName`", proxy.quotes(column));
	}

	@Test
	public void testGetDerivedTableColumn() {
	    when(context.getDerivedTableColumns()).thenReturn(List.of("column1", "column2"));
	    assertEquals(List.of("column1", "column2"), proxy.getDerivedTableColumn());
	}

	@Test
	public void testDatabaseFunction() {
		JsonObject json = new JsonObject();
		when(context.databaseFunction(json)).thenReturn("functionResult");
		assertEquals("functionResult", proxy.databaseFunction(json));
	}
	
	@Test
    public void testGetDatabaseName() {
        when(context.getDatabaseName()).thenReturn("dbName");
        assertEquals("dbName", proxy.getDatabaseName());
    }

    @Test
    public void testGetDerivedTables() {
        List<DerivedTable> derivedTables = List.of(new DerivedTable("name", "alias", "query", "type"));
        when(context.getDerivedTables()).thenReturn(derivedTables);
        assertEquals(derivedTables, proxy.getDerivedTables());
    }
    
    @Test
    public void testGetFormData() {
        JsonObject formData = new JsonObject();
        when(context.getFormData()).thenReturn(formData);
        assertEquals(formData, proxy.getFormData());
    }

    @Test
    public void testGetColumnsMap() {
        Map<String, String> columnsMap = Map.of("column1", "type1", "column2", "type2");
        when(context.getColumnsMap()).thenReturn(columnsMap);
        assertEquals(columnsMap, proxy.getColumnsMap());
    }

    @Test
    public void testGetDerivedTableColumns() {
        List<String> derivedTableColumns = List.of("column1", "column2");
        when(context.getDerivedTableColumns()).thenReturn(derivedTableColumns);
        assertEquals(derivedTableColumns, proxy.getDerivedTableColumns());
    }

    @Test
    public void testIsApplyAggregation() {
        when(context.isApplyAggregation()).thenReturn(true);
        assertEquals(true, proxy.isApplyAggregation());
    }

    @Test
    public void testGetTableName() {
        String fullName = "schema.table";
        when(context.getTableName(fullName)).thenReturn("table");
        assertEquals("table", proxy.getTableName(fullName));
    }

    @Test
    public void testIsDistinctResults() {
        when(context.isDistinctResults()).thenReturn(true);
        assertEquals(true, proxy.isDistinctResults());
    }
    
    @Test
    public void testGetDerivedTableNames() {
        List<String> derivedTableNames = List.of("table1", "table2");
        when(context.getDerivedTableNames()).thenReturn(derivedTableNames);
        assertEquals(derivedTableNames, proxy.getDerivedTableNames());
    }

    @Test
    public void testGetRequestedTables() {
        List<String> requestedTables = List.of("table1", "table2");
        when(context.getRequestedTables()).thenReturn(requestedTables);
        assertEquals(requestedTables, proxy.getRequestedTables());
    }

    @Test
    public void testGetGraphNodes() {
        List<String> graphNodes = List.of("node1", "node2");
        when(context.getGraphNodes()).thenReturn(graphNodes);
        assertEquals(graphNodes, proxy.getGraphNodes());
    }

    @Test
    public void testGetTables() {
        List<String> tables = List.of("table1", "table2");
        when(context.getTables()).thenReturn(tables);
        assertEquals(tables, proxy.getTables());
    }

    @Test
    public void testGetStandardSqlFunction() {
        SqlFunction function = mock(SqlFunction.class);
        when(context.getStandardSqlFunction()).thenReturn(function);
        assertEquals(function, proxy.getStandardSqlFunction());
    }

    @Test
    public void testGetHibernateDialect() {
        Dialect dialect = mock(Dialect.class);
        when(context.getHibernateDialect()).thenReturn(dialect);
        assertEquals(dialect, proxy.getHibernateDialect());
    }

    @Test
    public void testGetOpenQuote() {
        when(context.getOpenQuote()).thenReturn("`");
        assertEquals("`", proxy.getOpenQuote());
    }

    @Test
    public void testSetOpenQuote() {
        String openQuote = "";
        proxy.setOpenQuote(openQuote);
        assertEquals(null, context.getOpenQuote());
    }

    @Test
    public void testGetCloseQuote() {
        when(context.getCloseQuote()).thenReturn("`");
        assertEquals("`", proxy.getCloseQuote());
    }
    @Test
    public void testGetNameAndFullNameMap() {
        Map<String, String> nameAndFullNameMap = Map.of("name1", "fullName1", "name2", "fullName2");
        when(context.getNameAndFullNameMap()).thenReturn(nameAndFullNameMap);
        assertEquals(nameAndFullNameMap, proxy.getNameAndFullNameMap());
    }

    @Test
    public void testGetListOfRelationships() {
        List<Relationship> relationships = List.of(new Relationship(), new Relationship());
        when(context.getListOfRelationships()).thenReturn(relationships);
        assertEquals(relationships, proxy.getListOfRelationships());
    }

    @Test
    public void testIsApplyWhere() {
        when(context.isApplyWhere()).thenReturn(true);
        assertEquals(true, proxy.isApplyWhere());
    }

    @Test
    public void testIsApplyGroupBy() {
        when(context.isApplyGroupBy()).thenReturn(true);
        assertEquals(true, proxy.isApplyGroupBy());
    }

    @Test
    public void testIsApplyOrderBy() {
        when(context.isApplyOrderBy()).thenReturn(true);
        assertEquals(true, proxy.isApplyOrderBy());
    }

    @Test
    public void testIsApplyHaving() {
        when(context.isApplyHaving()).thenReturn(true);
        assertEquals(true, proxy.isApplyHaving());
    }

    @Test
    public void testGetPreConstructedFilters() {
        PreConstructedFilters filters = mock(PreConstructedFilters.class);
        when(context.getPreConstructedFilters()).thenReturn(filters);
        assertEquals(filters, proxy.getPreConstructedFilters());
    }

    @Test
    public void testIsLimitRequested() {
        when(context.isLimitRequested()).thenReturn(true);
        assertEquals(true, proxy.isLimitRequested());
    }

    @Test
    public void testGetDriverClassName() {
        String driverClassName = "com.mysql.jdbc.Driver";
        when(context.getDriverClassName()).thenReturn(driverClassName);
        assertEquals(driverClassName, proxy.getDriverClassName());
    }

    @Test
    public void testGetQueryOffset() {
        String queryOffset = "10";
        when(context.getQueryOffset()).thenReturn(queryOffset);
        assertEquals(queryOffset, proxy.getQueryOffset());
    }

    @Test
    public void testGetQueryLimit() {
        String queryLimit = "100";
        when(context.getQueryLimit()).thenReturn(queryLimit);
        assertEquals(queryLimit, proxy.getQueryLimit());
    }

    @Test
    public void testGetReferenceFile() {
        String referenceFile = "path/to/reference/file";
        when(context.getReferenceFile()).thenReturn(referenceFile);
        assertEquals(referenceFile, proxy.getReferenceFile());
    }

    @Test
    public void testGetMetadata() {
        Metadata metadata = new Metadata();
        when(context.getMetadata()).thenReturn(metadata);
        assertEquals(metadata, proxy.getMetadata());
    }
}
