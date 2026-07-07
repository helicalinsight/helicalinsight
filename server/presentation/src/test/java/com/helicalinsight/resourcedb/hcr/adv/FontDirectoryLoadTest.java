package com.helicalinsight.resourcedb.hcr.adv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.datasource.HCRUtils;
import com.lowagie.text.FontFactory;

public class FontDirectoryLoadTest {
	
    @Test
    public void testLoadFontDirectories_registersAllDirs() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        try (
            MockedStatic<FontFactory> fontFactoryMock = mockStatic(FontFactory.class);
        ) {
            
        	 Field initField = HCRUtils.class.getDeclaredField("fontDirHash");
             initField.setAccessible(true);
             Object value = initField.get(null);
             
             if ( value instanceof String hash) {
            	 if ( StringUtils.isBlank(hash)) {
            		 HCRUtils.loadFontDirectories();
            		 fontFactoryMock.verify(() -> FontFactory.registerDirectory("/usr/local/share/fonts", true));
            		 fontFactoryMock.verify(() -> FontFactory.registerDirectory("/usr/share/fonts", true));
            	 }
             }
        }
    }

    
    @Test
    public void testFontExists_returnsTrueWhenRegistered() {
        try (MockedStatic<FontFactory> fontFactoryMock = mockStatic(FontFactory.class)) {
            fontFactoryMock.when(() -> FontFactory.isRegistered("arial"))
                           .thenReturn(true);

            boolean exists = HCRUtils.fontExists("Arial");

            assertTrue(exists);
            fontFactoryMock.verify(() -> FontFactory.isRegistered("arial"));
        }
    }

    @Test
    public void testFontExists_returnsFalseWhenNotRegistered() {
        try (MockedStatic<FontFactory> fontFactoryMock = mockStatic(FontFactory.class)) {
            fontFactoryMock.when(() -> FontFactory.isRegistered("roboto"))
                           .thenReturn(false);

            boolean exists = HCRUtils.fontExists("Roboto");

            assertFalse(exists);
            fontFactoryMock.verify(() -> FontFactory.isRegistered("roboto"));
        }
    }

}
