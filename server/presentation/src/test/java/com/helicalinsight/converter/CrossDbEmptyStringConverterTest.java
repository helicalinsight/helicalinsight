package com.helicalinsight.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.utils.DialectSupport;
import com.helicalinsight.efw.ApplicationProperties;

public class CrossDbEmptyStringConverterTest {

	private static final String BLANK_MARKER = ApplicationProperties.getInstance().getBlankValue();

	private CrossDbEmptyStringConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new CrossDbEmptyStringConverter();
	}

	@Test
	public void convertToDatabaseColumn_null_returnsNull() {
		assertNull(converter.convertToDatabaseColumn(null));
	}

	@Test
	public void convertToDatabaseColumn_whitespace_returnsBlankMarker() {
		assertEquals("", converter.convertToDatabaseColumn(""));
		assertEquals("\t\n", converter.convertToDatabaseColumn("\t\n"));
	}
	
	@Test
	public void convertToDatabaseColumn_whitespace_returnsWhiteSpace() {
		try (MockedStatic<DialectSupport> dialect = mockStatic(DialectSupport.class)) {
			dialect.when(DialectSupport::shouldConvert).thenReturn(false);
			assertEquals("", converter.convertToDatabaseColumn(""));
			assertEquals("", converter.convertToEntityAttribute(""));
		}
	}
	
	@Test
	public void convertToDatabaseColumn_null_returnsNullWithDialectSupport() {
		try (MockedStatic<DialectSupport> dialect = mockStatic(DialectSupport.class)) {
			dialect.when(DialectSupport::shouldConvert).thenReturn(true);
			assertEquals(null, converter.convertToDatabaseColumn(null));
			assertEquals(null, converter.convertToEntityAttribute(null));
		}
	}
	
	
	@Test
	public void convertToDatabaseColumn_whitespace_returnsBlankMarkerWithSupportedDatabse() {
		try (MockedStatic<DialectSupport> dialect = mockStatic(DialectSupport.class)) {
			dialect.when(DialectSupport::shouldConvert).thenReturn(true);
			assertEquals(BLANK_MARKER, converter.convertToDatabaseColumn(""));
			assertEquals("", converter.convertToEntityAttribute(BLANK_MARKER));
			assertEquals("value", converter.convertToDatabaseColumn("value"));
			assertEquals("value", converter.convertToEntityAttribute("value"));
		}
	}

	@Test
	public void convertToDatabaseColumn_nonBlank_returnsSameValue() {
		assertEquals("hello", converter.convertToDatabaseColumn("hello"));
		assertEquals(" hello ", converter.convertToDatabaseColumn(" hello "));
	}

	@Test
	public void convertToEntityAttribute_whenShouldConvert_null_returnsNull() {
		try (MockedStatic<DialectSupport> dialect = mockStatic(DialectSupport.class)) {
			dialect.when(DialectSupport::shouldConvert).thenReturn(true);

			assertNull(converter.convertToEntityAttribute(null));
		}
	}

	@Test
	public void convertToEntityAttribute_whenShouldConvert_blankMarker_returnsEmptyString() {
		try (MockedStatic<DialectSupport> dialect = mockStatic(DialectSupport.class)) {
			dialect.when(DialectSupport::shouldConvert).thenReturn(true);
			assertEquals("", converter.convertToEntityAttribute(BLANK_MARKER));
		}
	}

	@Test
	public void convertToEntityAttribute_whenShouldConvert_otherValue_returnsSame() {
		try (MockedStatic<DialectSupport> dialect = mockStatic(DialectSupport.class)) {
			dialect.when(DialectSupport::shouldConvert).thenReturn(true);
			assertEquals("hello", converter.convertToEntityAttribute("hello"));
			assertEquals("", converter.convertToEntityAttribute(""));
		}
	}

	@Test
	public void convertToEntityAttribute_whenShouldNotConvert_returnsValueUnchanged() {
		try (MockedStatic<DialectSupport> dialect = mockStatic(DialectSupport.class)) {
			dialect.when(DialectSupport::shouldConvert).thenReturn(false);
			assertEquals(BLANK_MARKER, converter.convertToEntityAttribute(BLANK_MARKER));
			assertEquals("hello", converter.convertToEntityAttribute("hello"));
			assertNull(converter.convertToEntityAttribute(null));
		}
	}
}