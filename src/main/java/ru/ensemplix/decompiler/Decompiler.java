package ru.ensemplix.decompiler;

import java.nio.file.Path;

public interface Decompiler {

    void decompile(Path path);

}
