package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileSerializer implements Serializer {
    private final String fileName;
    private final ObjectMapper mapper;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
        mapper = JsonMapper.builder().build();
    }

    @Override
    public void serialize(Map<String, Double> data) {
        try {
            var jsonString = mapper.writeValueAsString(data);
            var file = new File(fileName);
            var json = mapper.readTree(jsonString);
            mapper.writeValue(file, json); // тут можно было сразу передать мапу, но в задании преобразование в джейсон)
        } catch (IOException e) {
            throw new FileProcessException("Error while saving a file", e);
        }
    }
}
