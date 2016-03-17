package ru.ensemplix.mapping;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MappingReaderImpl implements MappingReader {

    @Override
    public Map<String, String> getMapping(Path path) throws IOException {
        Map<String, String> mapping = new HashMap<>();

        try (FileReader reader = new FileReader(path.toFile())) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);

            for(CSVRecord record : records) {
                String from = record.get(0);
                String to = record.get(1);

                if(from.startsWith("func") || from.startsWith("field")) {
                    mapping.put(from, to);
                }
            }
        }

        return mapping;
    }

}
