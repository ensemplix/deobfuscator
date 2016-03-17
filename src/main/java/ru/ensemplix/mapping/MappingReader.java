package ru.ensemplix.mapping;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface MappingReader {

    Map<String, String> getMapping(Path path) throws IOException;

}
