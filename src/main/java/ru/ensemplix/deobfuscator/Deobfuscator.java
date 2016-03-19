package ru.ensemplix.deobfuscator;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface Deobfuscator {

    void read(InputStream in) throws IOException;

    void write(OutputStream out) throws IOException;

    Map<Type, Result> getResults();

    class Result {

        @Getter
        protected int count;

        @Getter
        protected int total;

    }

    enum Type {
        FIELD, METHOD, PARAM
    }

}
