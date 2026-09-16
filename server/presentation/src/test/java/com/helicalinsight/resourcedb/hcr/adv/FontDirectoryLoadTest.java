package com.helicalinsight.resourcedb.hcr.adv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.services.FontService;
import com.lowagie.text.FontFactory;

public class FontDirectoryLoadTest {

    @Test
    public void testLoadFontDirectories_registersAllDirs() throws Exception {
        File fontsDir = Files.createTempDirectory("hi-fonts-test").toFile();
        fontsDir.deleteOnExit();

        ApplicationProperties properties = mock(ApplicationProperties.class);
        when(properties.getFontsPath()).thenReturn(fontsDir.getAbsolutePath());

        Field hashField = FontService.class.getDeclaredField("registeredDirectoryHash");
        hashField.setAccessible(true);
        Object previousHash = hashField.get(null);
        hashField.set(null, null);

        try (
            MockedStatic<ApplicationProperties> appPropsMock = mockStatic(ApplicationProperties.class);
            MockedStatic<FontFactory> fontFactoryMock = mockStatic(FontFactory.class)
        ) {
            appPropsMock.when(ApplicationProperties::getInstance).thenReturn(properties);

            HCRUtils.loadFontDirectories();

            fontFactoryMock.verify(() ->
                    FontFactory.registerDirectory(fontsDir.getAbsolutePath(), true));
        } finally {
            hashField.set(null, previousHash);
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
