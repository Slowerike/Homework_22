package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.*;

public class HistoryHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path filePath = Paths.get("src/main/resources/history.txt");
        final Path ERROR_404_PATH = Paths.get("src/main/resources/404.html");
        final Path ERROR_500_PATH = Paths.get("src/main/resources/500.html");
        StringBuilder result = new StringBuilder();
        if (Files.exists(filePath)) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }

            String response = result.toString();
            if (!response.isEmpty()) {
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(UTF_8));
                }
            } else {
                // Файл пустой, отправляем ошибку 404
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                String errorResponse = Files.readString(ERROR_404_PATH);
                exchange.sendResponseHeaders(404, errorResponse.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorResponse.getBytes(UTF_8));
                }
            }
        } else {
            // Файл не существует, отправляем ошибку 500
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            String errorResponse = Files.readString(ERROR_500_PATH);
            exchange.sendResponseHeaders(500, errorResponse.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errorResponse.getBytes(UTF_8));
            }
        }
    }
}
