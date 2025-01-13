package com.example.directorytool.service;

import com.example.directorytool.model.OperationMode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DirectoryService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void processDirectory(String srcPath, String destPath, OperationMode mode) throws IOException {
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(destPath);

        if (!Files.exists(source)) {
            throw new IOException("Source directory does not exist");
        }

        if (!Files.isDirectory(source)) {
            throw new IOException("Source path is not a directory");
        }

        if (Files.exists(destination) && !Files.isDirectory(destination)) {
            throw new IOException("Destination path exists but is not a directory");
        }

        if (!Files.exists(destination)) {
            Files.createDirectories(destination);
        }

        try (var stream = Files.walk(source)) {
            stream.forEach(path -> executorService.submit(() -> {
                try {
                    Path relativePath = source.relativize(path);
                    Path targetPath = destination.resolve(relativePath);

                    if (Files.isDirectory(path)) {
                        if (!Files.exists(targetPath)) {
                            Files.createDirectories(targetPath);
                        }
                    } else {
                        if (mode == OperationMode.COPY) {
                            Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        } else if (mode == OperationMode.MOVE) {
                            Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to process file: " + path, e);
                }
            }));
        }
    }
}
