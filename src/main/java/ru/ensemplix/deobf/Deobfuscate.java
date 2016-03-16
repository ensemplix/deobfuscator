package ru.ensemplix.deobf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Deobfuscate {

    void read(InputStream in) throws IOException;

    void write(OutputStream out) throws IOException;

}
