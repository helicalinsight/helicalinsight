package com.helicalinsight.adhoc.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SqlQueryMetadataValidatorTest {

    @Test
    public void ut_a1_buildAllowedTablesColumns_returnsEmptyForNullMetadata() {
        Map<String, Set<String>> allowed = SqlQueryMetadataValidator.buildAllowedTablesColumns(null);
        assertNotNull(allowed);
        assertTrue(allowed.isEmpty());
    }

    @Test
    public void ut_a2_buildAllowedTablesColumns_returnsEmptyWhenTablesMissing() {
        JsonObject metadata = new JsonObject();
        metadata.addProperty("name", "test-db");

        Map<String, Set<String>> allowed = SqlQueryMetadataValidator.buildAllowedTablesColumns(metadata);
        assertTrue(allowed.isEmpty());
    }

    @Test
    public void ut_a3_buildAllowedTablesColumns_registersTableNameKeyAndAlias() {
        JsonObject metadata = sampleMetadata();

        Map<String, Set<String>> allowed = SqlQueryMetadataValidator.buildAllowedTablesColumns(metadata);

        assertTrue(allowed.containsKey("employee_details"));
        assertTrue(allowed.containsKey("emp"));
        assertEquals(allowed.get("employee_details"), allowed.get("emp"));
        assertTrue(allowed.get("employee_details").contains("id"));
        assertTrue(allowed.get("employee_details").contains("name"));
        assertTrue(allowed.get("departments").contains("dept_name"));
    }

    @Test
    public void ut_a4_buildAllowedTablesColumns_usesEntryKeyWhenNameMissing() {
        JsonObject columns = new JsonObject();
        columns.add("col_a", new JsonObject());

        JsonObject tableJson = new JsonObject();
        tableJson.add("columns", columns);

        JsonObject tables = new JsonObject();
        tables.add("fallback_table", tableJson);

        JsonObject metadata = new JsonObject();
        metadata.add("tables", tables);

        Map<String, Set<String>> allowed = SqlQueryMetadataValidator.buildAllowedTablesColumns(metadata);

        assertTrue(allowed.containsKey("fallback_table"));
        assertTrue(allowed.get("fallback_table").contains("col_a"));
    }

    @Test
    public void ut_a5_validateQueryAgainstMetadata_noOpForBlankOrEmptyScope() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(null, allowed);
        SqlQueryMetadataValidator.validateQueryAgainstMetadata("", allowed);
        SqlQueryMetadataValidator.validateQueryAgainstMetadata("   ", allowed);
        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT employee_details.id FROM employee_details", null);
        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT employee_details.id FROM employee_details", Collections.emptyMap());
    }

    @Test
    public void ut_a6_validateQueryAgainstMetadata_noOpForUnparseableSql() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata("SELECT FROM", allowed);
    }

    @Test(expected = SecurityException.class)
    public void ut_a7_validateQueryAgainstMetadata_rejectsNonSelectStatements() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "INSERT INTO employee_details (id) VALUES (1)", allowed);
    }

    @Test
    public void ut_a8_validateQueryAgainstMetadata_allowsValidSelect() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT employee_details.id, employee_details.name FROM employee_details", allowed);
    }

    @Test(expected = SecurityException.class)
    public void ut_a9_validateQueryAgainstMetadata_rejectsUnknownTable() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT secret_table.id FROM secret_table", allowed);
    }

    @Test(expected = SecurityException.class)
    public void ut_a10_validateQueryAgainstMetadata_rejectsUnknownColumn() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT employee_details.salary FROM employee_details", allowed);
    }

   
    @Test
    public void ut_a12_validateQueryAgainstMetadata_resolvesTableAlias() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT emp.id, emp.name FROM employee_details emp", allowed);
    }

    @Test
    public void ut_a13_validateQueryAgainstMetadata_allowsJoinAcrossAllowedTables() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT employee_details.id, departments.dept_name "
                        + "FROM employee_details "
                        + "INNER JOIN departments ON employee_details.department_id = departments.id",
                allowed);
    }

    @Test(expected = SecurityException.class)
    public void ut_a14_validateQueryAgainstMetadata_rejectsJoinWithUnknownTable() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT employee_details.id FROM employee_details "
                        + "INNER JOIN secret_table ON employee_details.id = secret_table.id",
                allowed);
    }

    @Test
    public void ut_a15_validateQueryAgainstMetadata_allowsSchemaQualifiedTableName() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT dbo.employee_details.id FROM dbo.employee_details", allowed);
    }

    @Test
    public void ut_a16_validateQueryAgainstMetadata_allowsUnionOfValidSelects() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT employee_details.id FROM employee_details "
                        + "UNION SELECT departments.id FROM departments",
                allowed);
    }

    @Test
    public void ut_a17_validateQueryAgainstMetadata_allowsUnqualifiedColumnWhenUniqueAcrossTables() {
        Map<String, Set<String>> allowed = sampleAllowedTablesColumns();

        SqlQueryMetadataValidator.validateQueryAgainstMetadata(
                "SELECT name FROM employee_details", allowed);
    }

    @Test
    public void ut_a18_buildAllowedTablesColumns_skipsNonObjectTableEntries() {
        JsonObject tables = new JsonObject();
        tables.addProperty("invalid_table", "not-an-object");

        JsonObject metadata = new JsonObject();
        metadata.add("tables", tables);

        Map<String, Set<String>> allowed = SqlQueryMetadataValidator.buildAllowedTablesColumns(metadata);

        assertFalse(allowed.containsKey("invalid_table"));
    }

    private static JsonObject sampleMetadata() {
        JsonObject employeeColumns = new JsonObject();
        employeeColumns.add("id", new JsonObject());
        employeeColumns.add("name", new JsonObject());
        employeeColumns.add("department_id", new JsonObject());

        JsonObject employeeTable = new JsonObject();
        employeeTable.addProperty("name", "employee_details");
        employeeTable.addProperty("alias", "emp");
        employeeTable.add("columns", employeeColumns);

        JsonObject departmentColumns = new JsonObject();
        departmentColumns.add("id", new JsonObject());
        departmentColumns.add("dept_name", new JsonObject());

        JsonObject departmentTable = new JsonObject();
        departmentTable.addProperty("name", "departments");
        departmentTable.add("columns", departmentColumns);

        JsonObject tables = new JsonObject();
        tables.add("employee_details", employeeTable);
        tables.add("departments", departmentTable);

        JsonObject metadata = new JsonObject();
        metadata.add("tables", tables);
        return metadata;
    }

    private static Map<String, Set<String>> sampleAllowedTablesColumns() {
        return SqlQueryMetadataValidator.buildAllowedTablesColumns(sampleMetadata());
    }
}
