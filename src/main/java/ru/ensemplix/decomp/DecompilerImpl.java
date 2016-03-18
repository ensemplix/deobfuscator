package ru.ensemplix.decomp;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

public class DecompilerImpl implements Decompiler {

    public static final String DECOMPILER_RESULT_FOLDER = "deobf";

    private final Fernflower fernflower;

    public DecompilerImpl() {
        this(IFernflowerPreferences.getDefaults());
    }

    public DecompilerImpl(Map<String, Object> options) {
        this.fernflower = new Fernflower(new BytecodeProvider(), new ResultSaver(), options, new ResultLogger());
    }

    @Override
    public void decompile(Path path) {
        fernflower.getStructContext().addSpace(path.toFile(), true);

        try {
            fernflower.decompileContext();
        } finally {
            fernflower.clearContext();
        }
    }

    public class BytecodeProvider implements IBytecodeProvider {

        @Override
        public byte[] getBytecode(String file, String cls) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try(ZipFile zip = new ZipFile(file)) {
                try(InputStream in = zip.getInputStream(zip.getEntry(cls))) {
                    while(in.available() > 0) {
                        out.write(in.read());
                    }
                }

                return out.toByteArray();
            } finally {
                out.close();
            }
        }

    }

    public class ResultSaver implements IResultSaver {

        private Path resources;
        private Path src;
        private Path dir;

        @Override
        public void saveFolder(String path) {
            dir = Paths.get(DECOMPILER_RESULT_FOLDER);
        }

        @Override
        public void copyFile(String source, String path, String entry) {

        }

        @Override
        public void saveClassFile(String path, String qualified, String entry, String content, int[] mapping) {

        }

        @Override
        public void createArchive(String path, String name, Manifest manifest) {
            dir = dir.resolve(name.substring(0, name.length() - 4) + "/src/main");
            resources = dir.resolve("resources");
            src = dir.resolve("java");

            try {
                Files.createDirectories(resources);
                Files.createDirectories(src);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void saveDirEntry(String path, String name, String entry) {

        }

        @Override
        public void copyEntry(String source, String path, String name, String entry) {
            if(entry.contains("META-INF")) {
                return;
            }

            try(ZipFile zip = new ZipFile(source)) {
                Path dest = resources.resolve(entry);
                Files.createDirectories(dest.getParent());

                FileOutputStream out = new FileOutputStream(dest.toFile());

                try(InputStream in = zip.getInputStream(zip.getEntry(entry))) {
                    while(in.available() > 0) {
                        out.write(in.read());
                    }
                } finally {
                    out.close();
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void saveClassEntry(String path, String name, String qualified, String entry, String content) {
            try {
                Path dest = src.resolve(entry);
                Files.createDirectories(dest.getParent());

                try (FileWriter writer = new FileWriter(dest.toFile())) {
                    writer.write(content);
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void closeArchive(String path, String name) {

        }

    }

    public class ResultLogger extends IFernflowerLogger {

        @Override
        public void writeMessage(String message, Severity severity) {

        }

        @Override
        public void writeMessage(String message, Throwable throwable) {

        }

    }

}
