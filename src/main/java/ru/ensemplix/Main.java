package ru.ensemplix;

import ru.ensemplix.decompiler.Decompiler;
import ru.ensemplix.decompiler.DecompilerImpl;
import ru.ensemplix.deobfuscator.Deobfuscator;
import ru.ensemplix.deobfuscator.DeobfuscatorImpl;
import ru.ensemplix.mapping.MappingReader;
import ru.ensemplix.mapping.MappingReaderImpl;
import ru.ensemplix.mod.ModInfo;
import ru.ensemplix.mod.ModInfoReader;
import ru.ensemplix.mod.ModInfoReaderImpl;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static ru.ensemplix.deobfuscator.Deobfuscator.Result;
import static ru.ensemplix.deobfuscator.Deobfuscator.Type;

public class Main {
    private static final MappingReader reader = new MappingReaderImpl();

    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.out.println("Please provide mod for deobfuscation");
            return;
        }

        Path path = Paths.get(args[0]);

        if(Files.notExists(path)) {
            System.out.println("Not found " + path.toString());
            return;
        }

        ModInfoReader infoReader = new ModInfoReaderImpl();
        ModInfo info = infoReader.getInfo(path);

        if(info == null) {
            System.out.println("Not found mod info in " + path.toString());
            return;
        }

        System.out.println("Deobfuscating " + info.getName());

        Map<String, String> mappings = new HashMap<>();
        String mcVersion = info.getMcversion();

        if(mcVersion == null) {
            System.out.println("Mod info has absent minecraft version");
            //return;
            mcVersion = "1.7.10";
        }

        mappings.putAll(mapping("/mappings/" + mcVersion + "/fields.csv"));
        mappings.putAll(mapping("/mappings/" + mcVersion + "/methods.csv"));
        mappings.putAll(mapping("/mappings/" + mcVersion + "/params.csv"));

        if(mappings.isEmpty()) {
            System.out.println("Not found mappings for " + mcVersion + " minecraft");
            return;
        }

        System.out.println("Loaded " + mappings.size() + " mappings for " + mcVersion + " minecraft");
        System.out.println("Renaming fields and methods");


        String name = path.toString();
        Path deobfPath = Paths.get(name.substring(0, name.lastIndexOf(".")) + "-deobf.jar");

        if(Files.notExists(deobfPath)) {
            Files.createFile(deobfPath);
        }

        Deobfuscator deobfuscator = new DeobfuscatorImpl(mappings);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(deobfPath.toFile()));

        try(ZipFile zip = new ZipFile(path.toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if(entry.getName().contains("META-INF")) {
                    continue;
                }

                out.putNextEntry(new ZipEntry(entry.getName()));

                try (InputStream in = zip.getInputStream(entry)) {
                    if (entry.getName().endsWith(".class")) {
                        deobfuscator.read(in);
                        deobfuscator.write(out);
                    } else {
                        byte[] bytes = new byte[1024];
                        int count;

                        while((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                    }
                }
            }
        } finally {
            out.close();
        }

        System.out.println("\rDeobfuscated:");

        for (Map.Entry<Type, Result> entry : deobfuscator.getResults().entrySet()) {
            String type = entry.getKey().toString().toLowerCase();
            Result result = entry.getValue();

            System.out.println("\t" + type + ": " + result.getCount() + "/" + result.getTotal());
        }

        System.out.println("Decompiling:");
        System.out.print("\tLoading...");

        Decompiler decompiler = new DecompilerImpl();
        decompiler.decompile(deobfPath);

        System.out.println("\rFinished");
    }

    public static Map<String, String> mapping(String name) throws Exception {
        URL resource = Main.class.getResource(name);

        if(resource == null) {
            return Collections.emptyMap();
        }

        Path path = Paths.get(resource.toURI());
        return reader.getMapping(path);
    }

}
