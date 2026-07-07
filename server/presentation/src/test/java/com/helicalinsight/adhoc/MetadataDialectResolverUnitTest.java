package com.helicalinsight.adhoc;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;

import com.google.gson.JsonObject;

public class MetadataDialectResolverUnitTest {

	private static final Map<String, String> DIALECT_SQL_MAP = Map.ofEntries(
			Map.entry("org.hibernate.dialect.MySQL8Dialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.OracleDialect",
					"select * from (select * from (select * from travel_details) foo) where rownum<=10"),
			Map.entry("org.hibernate.dialect.DB2zDialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.H2Dialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.Oracle12cDialect",
					"select * from (select * from (select * from travel_details) foo) where rownum<=10"),
			Map.entry("org.hibernate.dialect.MariaDB106Dialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.MariaDB103Dialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.DB2Dialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.SQLServer2012Dialect",
					"select * from (select * from travel_details) foo order by @@version offset 0 rows fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.SQLServer2016Dialect",
					"select * from (select * from travel_details) foo order by @@version offset 0 rows fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.DB2400V7R3Dialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.PostgreSQLDialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.MySQLDialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.DerbyDialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.MariaDBDialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.DB2iDialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.CockroachDialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.HSQLDialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.HANACloudColumnStoreDialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.MySQL57Dialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.SpannerDialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.SybaseASEDialect",
					"select top 10  * from (select * from travel_details) foo"),
			Map.entry("org.hibernate.dialect.SQLServer2008Dialect",
					"select top(10) * from (select * from travel_details) foo"),
			Map.entry("org.hibernate.dialect.PostgresPlusDialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.SQLServerDialect",
					"select top(10) * from (select * from travel_details) foo"),
			Map.entry("org.hibernate.dialect.DB2400Dialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.HANARowStoreDialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.HANAColumnStoreDialect",
					"select * from (select * from travel_details) foo limit 10"),
			Map.entry("org.hibernate.dialect.PostgreSQL10Dialect",
					"select * from (select * from travel_details) foo fetch first 10 rows only"),
			Map.entry("org.hibernate.dialect.TiDBDialect",
					"select * from (select * from travel_details) foo limit 10"));

	@Test
	public void testLimit() {

		JsonObject formData = new JsonObject();

		String packageName = "org.hibernate.dialect";

		String path = packageName.replace('.', '/');

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resource = classLoader.getResource(path);

		String resourcePath = resource.getPath();

		resourcePath = resourcePath.replace("file:/", "").replace("!/org/hibernate/dialect", "");

		Set<String> classNames = new HashSet<>();

		try (ZipInputStream zip = new ZipInputStream(new FileInputStream(resourcePath))) {
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
					String className = entry.getName().replace('/', '.');
					classNames.add(className.substring(0, className.length() - ".class".length()));
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();

		}

		formData.addProperty("query", "select * from travel_details");
		String limit = "10";
		for (String className : classNames) {
			if (className.endsWith("Dialect") && !className.equalsIgnoreCase("org.hibernate.dialect.Dialect")
					&& !className.contains("Abstract")) {
				try {
					String query = ViewLabelsRetrievalComponent.query(formData, className, limit);
					query = query.trim();
					
					System.out.println(className + "\t"  + query);
					
					assertEquals(DIALECT_SQL_MAP.get(className), query);
				} catch (UnsupportedOperationException unSupportedOperationException) {
				}
			}
		}
	}

}
