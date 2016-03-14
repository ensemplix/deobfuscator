package ru.ensemplix.mod;

import java.io.IOException;
import java.nio.file.Path;

public interface ModInfoReader {

    ModInfo getInfo(Path path) throws IOException;

}
