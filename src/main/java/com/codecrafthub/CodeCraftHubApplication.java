package com.codecrafthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the CodeCraftHub application.
 *
 * Run this class to start the embedded web server on http://localhost:8080
 *
 * From the command line:
 *   mvn spring-boot:run
 *
 * Or run this class directly from your IDE.
 */
@SpringBootApplication
public class CodeCraftHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeCraftHubApplication.class, args);
    }
}
