package ru.ensemplix.mod;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ModInfoReaderImplTest {

    private final ModInfoReader reader = new ModInfoReaderImpl();

    @Test
    public void testReader() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/modinfo.jar").toURI());
        ModInfo info = reader.getInfo(path);

        assertNotNull(info);
        assertEquals("IndustrialCraft 2", info.getName());
        assertEquals("2.2.811-experimental", info.getVersion());
        assertEquals("1.7.10", info.getMcversion());
    }

}
