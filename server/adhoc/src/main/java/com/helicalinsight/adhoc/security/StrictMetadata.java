package com.helicalinsight.adhoc.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.AbstractSecurityRules;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * Implements strict metadata security rules for adhoc queries.
 * This class provides methods to check for SQL injection and validate adhoc query metadata.
 * Created by Helical on 3/9/2021.
 */
@Deprecated
public class StrictMetadata extends AbstractSecurityRules {

	private Map<String, String> propsMap = ApplicationUtilities.getDefaultsMap();

	/**
     * Checks for SQL injection in the provided form data.
     *
     * @param formData 		 form data containing data filters, catalog , schema.
     * @param key      		 key to extract form data from the JSON object.
     * @param object   		 metadata object.
     * @return validated form data JSON object.
     * @throws Exception     If SQL injection or invalid filter values are detected.
     */
	@Override
	public JsonObject checkSqlInjection(JsonObject formData, String key, Object object) throws Exception {
		DocumentContext jsonDocumentContext = JsonPath.parse(formData,
				Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
		Object formDataObject = jsonDocumentContext.read(key);
		boolean checkedCatalog = false;
		String allow_complex_json_queries = propsMap.get("strict-metadata-object");

		Metadata metadata = null;
		if (object instanceof Metadata) {
			metadata = (Metadata) object;
		}
		if (formData != null && metadata != null) {
			if (formDataObject instanceof String) {
				Pattern sqlPattern = Pattern.compile(propsMap.get("sqlpattern"));
				Matcher m = sqlPattern.matcher(formDataObject.toString());
				boolean matchFound = m.find();
				if (matchFound) {
					throw new Exception("Invalid Value");
				}
			} else if (formDataObject instanceof JsonArray) {
				List<Table> tablesList = new ArrayList<>();
				JsonArray jsonArrayColumns = new Gson().fromJson(new Gson().toJson(formDataObject),
						JsonArray.class);
				if (key.contains("$.filters")) {
					try {
						validateFilters(jsonArrayColumns);
					} catch (Exception e) {
						throw new Exception("Invalid Filter Value");
					}
				} else {
					// TODO need to refactor
					List<String> userColumnList = userRequestedColumns(jsonArrayColumns);
					for (String column : userColumnList) {
						SqlUtils sqlUtils = new SqlUtils();
						Map<String, Set<String>> sqlMap = sqlUtils.getSqlColumns(column, allow_complex_json_queries);

						// complex queries
						if (sqlMap.containsKey("complexQuery")) {
							try {
								Set<String> complexQuery = checkComplexData(metadata, sqlMap.get("complexQuery"),
										allow_complex_json_queries);
								if (complexQuery != null) {
									return formData;
								}
							} catch (SecurityException e) {
								throw new SecurityException(e.getMessage());
							}
						}

						if (sqlMap.containsKey("catalog")) {
							Set<String> catalogSet = sqlMap.get("catalog");
							if (catalogSet.size() == 0 && (!StringUtils.isEmpty(metadata.getDatabase().getCatalog())
									&& StringUtils.isEmpty(metadata.getDatabase().getSchema()))) {
								catalogSet = sqlMap.get("schema");
								checkedCatalog = true;
							}
							Database db = checkCatalog(metadata, catalogSet);
							if (sqlMap.containsKey("schema")) {
								Set<String> schema = sqlMap.get("schema");
								if (!StringUtils.isEmpty(db.getSchema())) {
									if (schema.size() > 0) {
										String schemaName = checkSchema(db, schema);
										tablesList = getTablesFromSchema(db, schemaName);
										if (null == tablesList) {
											throw new SecurityException(
													"The Schema you are trying to access is not present in metadata");
										}
									}
									// Fix for {schemaname}.{columnname}
									else if (!StringUtils.isEmpty(db.getSchema()) && schema.size() == 0) {
										Set<String> table = sqlMap.get("tables");
										String schemaName = checkSchema(db, table);
										boolean isSchema = false;
										boolean isColumn = false;
										boolean isTable = false;
										if (null != schemaName) {
											isSchema = true;
											if (isSchema) {
												tablesList = getTablesFromSchema(db, null);
												Set<String> columnList = sqlMap.get("column");
												for (Table tab : tablesList) {
													if (columnList.contains(tab.getName())) {
														isTable = true;
													}
													if (!isTable) {
														List<Column> column1 = tab.getColumns().getColumn();
														for (Column col : column1) {
															if (columnList.contains(col.getName())) {
																isColumn = true;
															}
														}
													}
												}
												if (isSchema == true && isColumn == true) {
													throw new SecurityException(
															"Please provide fully qualified name e.g {tablename}.{columnName}.Schema and catalog are optional");
												} else if (isSchema = true && isTable == true && isColumn == false) {
													throw new SecurityException(
															"Please provide fully qualified name e.g {tablename}.{columnName}.Schema and catalog are optional");
												}
											} else {
												tablesList = getTablesFromSchema(db, null);
											}
										} else {
											tablesList = getTablesFromSchema(db, null);
										}
									} else {
										tablesList = getTablesFromSchema(db, null);
									}
								} else {
									if (!checkedCatalog) {
										String schemaName = checkSchema(db, schema);
										tablesList = getTablesFromSchema(db, schemaName);
									} else {
										tablesList = getTablesFromSchema(db, null);
									}
								}
								if (tablesList.size() > 0) {
									Set<String> tables1 = sqlMap.get("tables");
									if (tables1.size() == 0) {
										tables1 = sqlMap.get("column");
										Columns columns = getColumnsFromTable(tablesList, tables1);
										if (columns == null) {
											throw new SecurityException(
													"Please provide fully qualified name e.g {tablename}.{columnName}.Schema and catalog are optional");
										} else if (columns.getColumn().size() > 0) {
											throw new SecurityException(
													"Please provide fully qualified name e.g {tablename}.{columnName}.Schema and catalog are optional");
										}
									} else {
										Columns columns = getColumnsFromTable(tablesList, tables1);
										if (null != columns) {
											List<Column> column1 = columns.getColumn();
											Set<String> columnSet = sqlMap.get("column");
											for (String col : columnSet) {
												if (!containsName(column1, col, allow_complex_json_queries)) {
													throw new SecurityException(
															"The column you are trying to access is out of scope or not found in the metadata");
												}
											}
										} else {
											throw new SecurityException(
													"The Table you are trying to access is out of scope or not found in the metadata");
										}
									}
								} else {
									throw new SecurityException(
											"The data you are trying to access is out of scope or not found in the metadata");
								}
							}
						}
					}
				}
			}
		}

		return formData;

	}
	/**
	 * Checks if the provided schema name exists in the database metadata.
	 *
	 * @param db     		 database object containing metadata information.
	 * @param schema 		 A set of schema names to be checked for existence.
	 * @return name of the schema if found in metadata, otherwise null.
	 * @throws SecurityException If the schema is not found in the metadata.
	 */
	private String checkSchema(Database db, Set<String> schema) {
		for (String s : schema) {
			if (containsTableName(db.getTables().getTableList(), s)) {
				return null;
			} else if (!schema.contains(db.getSchema())) {
				throw new SecurityException("The Schema you are trying to access is not present in metadata");
			}
		}
		return db.getSchema();
	}

	@Override
	public boolean isThreadSafeToCache() {
		return false;
	}

	/**
	 * Validates the filter expressions provided in a JSON array format.
	 *
	 * @param jsonArray 			 JSON array containing filter expressions to be validated.
	 * @return {@code Null} if all filter expressions are valid, otherwise an error message.
	 * @throws Exception If any filter expression is invalid.
	 */
	private String validateFilters(JsonArray jsonArray) throws Exception {
		for (JsonElement element : jsonArray) {
			if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
				Statement stmt = null;
				try {
					stmt = CCJSqlParserUtil.parse(element.getAsString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (stmt != null) {
					throw new Exception("invalid filter value");
				}
			}
		}
		return null;
	}

	/**
	 * Extracts column names from a JSON array containing user-requested columns.
	 *
	 * @param columns 		 JSON array containing user-requested columns.
	 * @return A list containing the names of columns requested by the user.
	 */
	private List<String> userRequestedColumns(JsonArray columns) {
		List<String> userInput = new ArrayList<>();
		for (JsonElement object : columns) {
			JsonObject json = object.getAsJsonObject();
			String columnName = json.get("column").getAsString();
			String fqdn = "";
			userInput.add(columnName.trim());
		}
		return userInput;
	}
	/**
	 * Retrieves the columns associated with a specified table from the metadata.
	 *
	 * @param metadataTable 		 list of tables from which to retrieve the columns.
	 * @param tables         		 set of table names for which to retrieve columns.
	 * @return The columns associated with the specified table.
	 */
	private Columns getColumnsFromTable(List<Table> metadataTable, Set<String> tables) {
		for (Table table : metadataTable) {
			for (String t : tables) {
				t = StringUtils.strip(t, "\"").trim();
				if (t.equals(table.getName())) {
					return table.getColumns();
				} else if (t.equals(table.getAliasName())) {
					return table.getColumns();
				} else if (!StringUtils.isEmpty(table.getOriginalName())) {
					if (t.equals(table.getOriginalName())) {
						return table.getColumns();
					}
				}
			}
		}
		return null;
	}
	/**
	 * Retrieves the tables from the specified schema in the metadata.
	 *
	 * @param db          	 database metadata containing the schema and tables.
	 * @param schemaName  	 name of the schema from which to retrieve tables.
	 * @return The list of tables from the specified schema.
	 */
	private List<Table> getTablesFromSchema(Database db, String schemaName) {
		String metadataSchemaName = db.getSchema();
		if (null == schemaName) {
			return db.getTables().getTableList();
		} else if (metadataSchemaName.equals(schemaName)) {
			return db.getTables().getTableList();
		}
		return null;
	}
	/**
	 * Checks if the provided catalog matches the catalog defined in the metadata.
	 * If the catalog is not present in the metadata or does not match, a SecurityException is thrown.
	 *
	 * @param metadata  		 metadata containing database information.
	 * @param catalog   		 catalog to be checked against the metadata.
	 * @return database metadata after validating the catalog.
	 * @throws SecurityException If the provided catalog is not present in the metadata or does not match.
	 */
	private Database checkCatalog(Metadata metadata, Set<String> catalog) {
		String catalogName = metadata.getDatabase().getCatalog();
		if (!StringUtils.isEmpty(catalogName)) {
			if (catalog.size() > 0) {
				if (!catalog.contains(catalogName)) {
					throw new SecurityException("The Catalogue you are trying to access is not present in metadata");
				}
			}

		} else if (StringUtils.isEmpty(catalogName) && catalog.size() > 0) {
			throw new SecurityException("The Catalogue you are trying to access is not present in metadata");
		}
		return metadata.getDatabase();
	}
	/**
	 * Checks if a column with a specific name exists within a list of Column objects.
	 *
	 * @param list  An unmodifiable List containing Column objects.
	 * @param name  The name of the column to search for.
	 * @param allow_complex_queries (unused) - Might be used for future functionality with complex queries.
	 * @return     {@code true} if a column with the provided name exists, {@code false} otherwise.
	 */
	public boolean containsName(final List<Column> list, final String name, String allow_complex_queries) {
		boolean present = list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
		return present;
	}
	/**
	 * Checks if a column with a specific alias name exists within a list of Column objects.
	 *
	 * @param list  An unmodifiable List containing Column objects.
	 * @param name  The name of the column alias to search for.
	 * @return  {@code true} if a column with the provided alias name exists, {@code false} otherwise.
	 */
	public boolean containsNameAlias(final List<Column> list, final String name) {
		boolean present = list.stream().filter(o -> {
			if (!o.getAliasName().isEmpty()) {
				if (o.getAliasName().equals(name)) {
					return true;
				}
			}
			return false;
		}).findFirst().isPresent();
		return present;
	}
	/**
	 * Checks if a column with a specific original name exists within a list of Column objects.
	 * (Assuming a typo in the original code, meant to be `getOriginalName` not `getAliasName`)
	 *
	 * @param list  An unmodifiable List containing Column objects.
	 * @param name  The name of the original column to search for.
	 * @return   {@code true} if a column with the provided original name exists, {@code false} otherwise.
	 */
	public boolean containsNameOriginal(final List<Column> list, final String name) {
		boolean present = list.stream().filter(o -> {
			if (!o.getOriginalName().isEmpty()) {
				if (o.getAliasName().equals(name)) {
					return true;
				}
			}
			return false;
		}).findFirst().isPresent();
		return present;
	}
	/**
	 * Checks if a table with a specific name exists within a list of Table objects.
	 *
	 * @param list  An unmodifiable List containing Table objects.
	 * @param name  The name of the table to search for.
	 * @return     true if a table with the provided name exists, false otherwise.
	 */
	public boolean containsTableName(final List<Table> list, final String name) {
		boolean present = list.stream().filter(o -> {
			if (!o.getName().isEmpty()) {
				if (o.getName().equals(name)) {
					return true;
				}
			}
			return false;
		}).findFirst().isPresent();
		return present;
	}
	/**
	 * Checks if a complex query involving tables and columns is allowed based on metadata and configuration.
	 * (Assuming `allowComplexJson` is a configuration parameter for enabling complex query checks)
	 *
	 * @param metadata     		 metadata object containing schema, catalog, tables, and columns.
	 * @param complexQuery 		 set of strings representing elements (potentially including schema, catalog, table, and column) 
	 *                     		 involved in the complex query.
	 * @param allowComplexJson   A configuration parameter indicating if complex query checks are enabled.
	 * @return The original `complexQuery` set if checks pass, throws SecurityException otherwise.
	 * @throws SecurityException If the complex query violates security restrictions based on metadata or configuration.
	 */
	private Set<String> checkComplexData(Metadata metadata, Set<String> complexQuery, String allowComplexJson) {
		String schemaName = metadata.getDatabase().getSchema();
		String catalogName = metadata.getDatabase().getCatalog();
		List<Table> tableList = metadata.getDatabase().getTables().getTableList();
		Set<String> validElements = new HashSet<>();
		boolean skipCheck = false;
		Table table = null;
		String[] sqlMapSet = complexQuery.toArray(new String[complexQuery.size()]);
		table = containsTable(tableList, complexQuery);
		if (table == null) {
			throw new SecurityException(
					"The Table you are trying to access is out of scope or not found in the metadata");
		}
		try {
			checkForMetadata(table, metadata, sqlMapSet);
		} catch (Exception e) {
			throw new SecurityException(e.getMessage());
		}

		if (table != null) {
			List<Column> column = table.getColumns().getColumn();
			Column column1 = containsColumn(column, complexQuery);
			if (null == column1) {
				throw new SecurityException(
						"The Column you are trying to access is out of scope or not found in the metadata");
			}
		}

		return complexQuery;
	}
	/**
	 * Checks if a table object exists in the provided metadata and validates its schema and catalog (if specified).
	 *
	 * @param table    		 table object to be validated.
	 * @param metadata 		 metadata object containing schema and catalog information.
	 * @param sqlMapSet 	 array of strings potentially containing table name, schema, and catalog.
	 * @return  {@code true} if the table exists with matching schema and catalog (if provided), {@code false} otherwise.
	 * @throws SecurityException If schema or catalog validation fails.
	 */
	private boolean checkForMetadata(Table table, Metadata metadata, String[] sqlMapSet) {
		boolean isTable = false;
		String isSchema = "";
		String isCatalog = "";
		String name = table.getName();
		if (name.equals(sqlMapSet[0])) {
			isTable = true;
		}
		if (isTable == false) {
			if (metadata.getDatabase().getCatalog().equals(sqlMapSet[0])) {
				isCatalog = "true";
			}
			if (StringUtils.isEmpty(isCatalog)) {
				if (metadata.getDatabase().getSchema().equals(sqlMapSet[0])) {
					isSchema = "true";
				}
			}
			if (StringUtils.isEmpty(isSchema)) {
				name = table.getName();
				if (name.equals(sqlMapSet[1])) {
					isTable = true;
				}
				if (isTable == false) {
					if (metadata.getDatabase().getSchema().equals(sqlMapSet[1])) {
						isSchema = "true";
					} else {
						throw new SecurityException("The schema you are trying to access is not found in metadata");
					}
				}
				if (StringUtils.isEmpty(isCatalog) && StringUtils.isNotEmpty(isSchema)) {
					throw new SecurityException("The catalog you are trying to access is not found in metadata");
				} else if (StringUtils.isEmpty(isCatalog) && isTable == true && StringUtils.isEmpty(isSchema)) {
					throw new SecurityException(
							"The catalog or the schema you are trying to access is not found in metadata");
				}
			}
		} else {
			if (sqlMapSet.length > 0) {
				if (sqlMapSet.length == 2) {
					if (isSchema.equals("true") || isCatalog.equals("true")) {
						throw new SecurityException(
								"Please provide fully qualified name e.g {tablename}.{columnName}.Schema and catalog are optional");
					}
				} else if (sqlMapSet.length == 1) {
					throw new SecurityException(
							"Please provide fully qualified name e.g {tablename}.{columnName}.Schema and catalog are optional");
				}
			}
		}

		return false;
	}
	/**
	 * Finds a table from a list that matches any element name (case-insensitive) from a set.
	 *
	 * @param list     List containing Table objects.
	 * @param elements A set of element names (strings) to compare against table names.
	 * @return        The first matching Table object found, or null if no match is found.
	 */
	public Table containsTable(final List<Table> list, final Set<String> elements) {
		final String[] result = { "" };
		Table table = list.stream().filter(o -> {
			if (!o.getName().isEmpty()) {
				result[0] = elements.stream().filter(s -> {
					if (o.getName().equalsIgnoreCase(s)) {
						return true;
					}
					return false;
				}).findAny().orElse(null);
			}
			if (StringUtils.isNotEmpty(result[0])) {
				return true;
			}
			return false;
		}).findAny().orElse(null);
		return table;
	}
	/**
	 * Finds a column from a list that matches any element name (case-insensitive) from a set.
	 *
	 * @param list     		List containing Column objects.
	 * @param elements 		A set of element names (strings) to compare against column names.
	 * @return first matching Column object found, or {@code null} if no match is found.
	 */
	public Column containsColumn(final List<Column> list, final Set<String> elements) {
		final String[] result = { "" };
		Column column = list.stream().filter(o -> {
			if (!o.getName().isEmpty()) {
				result[0] = elements.stream().filter(s -> {
					if (o.getName().equalsIgnoreCase(s)) {
						return true;
					}
					return false;
				}).findAny().orElse(null);
			}
			if (StringUtils.isNotEmpty(result[0])) {
				return true;
			}
			return false;
		}).findAny().orElse(null);
		return column;
	}
	/**
	 * Checks if a schema name exists within a set of elements.
	 *
	 * @param schemaName 		 schema name to search for.
	 * @param elemList 		     set of elements (strings) that might contain schema names.
	 * @return {@code true} if the schema name is found in the element list, {@code false} otherwise.
	 */
	public static boolean containsSchemaAndCatalog(String schemaName, Set<String> elemList) {
		String s1 = elemList.stream().filter(s -> {
			if (StringUtils.isNotEmpty(schemaName)) {
				if (elemList.contains(schemaName)) {
					return true;
				}
			}
			return false;
		}).findAny().orElse(null);
		if (StringUtils.isNotEmpty(s1)) {
			return true;
		}
		return false;
	}

	/*
	 * public static boolean containsCatalog(String catalogName,Set<String>
	 * elemList){ String s1 = elemList.stream().filter(s -> { if
	 * (StringUtils.isNotEmpty(catalogName)) { if (elemList.equals(catalogName)) {
	 * return true; } } return false; }).findAny().orElse(null);
	 * if(StringUtils.isNotEmpty(s1)){ return true; } return false; }
	 */
}