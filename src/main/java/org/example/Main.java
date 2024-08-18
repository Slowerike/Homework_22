package org.example;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new HelloWorld());
        Path fileToDelete = Paths.get("src/main/resources/history.txt");
        server.createContext("/calculate", new CalculatorHandler());
        server.createContext("/history", new HistoryHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Servet started on port 8080");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (Files.exists(fileToDelete)) {
                try {
                    Files.delete(fileToDelete);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }
}