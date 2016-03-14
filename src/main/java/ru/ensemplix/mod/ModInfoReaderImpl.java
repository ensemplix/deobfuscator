package ru.ensemplix.mod;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModInfoReaderImpl implements ModInfoReader {

    public static final String MOD_INFO_FILE = "mcmod.info";

    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new Gson();

    @Override
    public ModInfo getInfo(Path path) throws IOException {
        try(JarFile jar = new JarFile(path.toFile())) {
            JarEntry modInfo = jar.getJarEntry(MOD_INFO_FILE);

            if(modInfo != null) {
                InputStreamReader reader = new InputStreamReader(jar.getInputStream(modInfo));
                JsonElement element = parser.parse(reader).getAsJsonArray().get(0);
                return gson.fromJson(element, ModInfo.class);
            }
        }

        return null;
    }

}
