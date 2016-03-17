package ru.ensemplix.mapping;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MappingReaderImplTest {

    @Test
    public void testReader() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/mapping.csv").toURI());
        MappingReader reader = new MappingReaderImpl();

        Map<String, String> mapping = reader.getMapping(path);
        
        assertEquals("isAggressive", mapping.get("field_104003_g"));
        assertEquals("isPotionDurationMax", mapping.get("field_100013_f"));
        assertEquals("isGamemodeForced", mapping.get("field_104057_T"));
        assertEquals("theMainMenu", mapping.get("field_104058_d"));
    }

}
