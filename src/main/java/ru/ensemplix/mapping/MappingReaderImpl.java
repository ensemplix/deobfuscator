package ru.ensemplix.mapping;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MappingReaderImpl implements MappingReader {

    @Override
    public Map<String, String> getMapping(Path path) throws IOException {
        Map<String, String> mapping = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            boolean ignoreFirstLine = true;

            for(CSVRecord record : records) {
                if(ignoreFirstLine) {
                    ignoreFirstLine = false;
                    continue;
                }

                String from = record.get(0);
                String to = record.get(1);
                mapping.put(from, to);
            }
        }

        return mapping;
    }

}
