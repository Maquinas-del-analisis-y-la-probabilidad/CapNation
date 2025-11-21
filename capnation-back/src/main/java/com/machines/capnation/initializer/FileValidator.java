package com.machines.capnation.initializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileValidator implements EnvironmentPostProcessor {
    private final String homePath = System.getProperty("user.home");

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        validateFile(environment.getProperty("caps.file"));
        validateFile(environment.getProperty("capIndex.file"));
        validateFile(environment.getProperty("brandIndex.file"));
    }

    private void validateFile(String pathStr) {
        if (pathStr == null) {
            throw new IllegalStateException("file environment variable not configured in application.properties");
        }

        Path path = Paths.get(pathStr);
        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't create file or directory ", e);
        }
    }
}
