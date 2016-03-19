package ru.ensemplix.decompiler;

import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static ru.ensemplix.decompiler.DecompilerImpl.DECOMPILER_RESULT_FOLDER;

public class DecompilerImplTest {

    @Test
    public void testDecompiler() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/dummy.jar").toURI());

        Decompiler decompiler = new DecompilerImpl();
        decompiler.decompile(path);

        String dummyFile = "/dummy/src/main/java/ru/ensemplix/Dummy.java";

        String match = readContents(getClass().getResourceAsStream("/Dummy.dec"));
        String match2 = readContents(new FileInputStream(DECOMPILER_RESULT_FOLDER + dummyFile));

        assertEquals(match, match2);
    }

    public String readContents(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            String line = reader.readLine();

            while(line != null){
                builder.append(line);
                line = reader.readLine();
            }
        }

        return builder.toString();
    }

}
