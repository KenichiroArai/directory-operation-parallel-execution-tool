package com.example.directorytool.runner;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.directorytool.model.OperationMode;
import com.example.directorytool.service.DirectoryService;

@Component
public class CommandLineAppRunner implements CommandLineRunner {

    private final DirectoryService directoryService;

    public CommandLineAppRunner(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: java -jar directory-tool.jar <src> <dest> <mode>");
            System.out.println("Modes: COPY, MOVE");
            return;
        }

        String src = args[0];
        String dest = args[1];
        String modeStr = args[2].toUpperCase();

        try {
            OperationMode mode = OperationMode.valueOf(modeStr);
            directoryService.processDirectory(src, dest, mode);
            System.out.println("Operation completed successfully");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid mode: " + modeStr);
            System.out.println("Valid modes are: COPY, MOVE");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
