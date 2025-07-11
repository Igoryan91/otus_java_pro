package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.util.List;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private final String fileName;
    private final ObjectMapper mapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        mapper = JsonMapper.builder().build();
    }

    @Override
    public List<Measurement> load() {
        try {
            var jsonReader = getClass().getClassLoader().getResourceAsStream(fileName);
            var measurements = mapper.readValue(jsonReader, Measurement[].class);
            return List.of(measurements);
        } catch (IOException e) {
            throw new FileProcessException("Error when reading a file", e);
        }
    }
}
