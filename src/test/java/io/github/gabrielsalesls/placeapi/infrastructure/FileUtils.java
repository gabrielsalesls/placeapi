package io.github.gabrielsalesls.placeapi.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FileUtils {

    ObjectMapper objectMapper = new ObjectMapper();

    public String loadFileContents(String filename) {
        try {
            var fileContent = new ClassPathResource(filename).getInputStream();
            return new String(fileContent.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
