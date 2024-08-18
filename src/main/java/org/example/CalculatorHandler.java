package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;


public class CalculatorHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            Double num1 = Double.parseDouble(params.getOrDefault("num1", "0"));
            Double num2 = Double.parseDouble(params.getOrDefault("num2", "0"));
            String operation = params.getOrDefault("operation", "+");
            Double resultOperation = chooseOperation(num1, num2, operation);
            String response = "Результат операции :%,.2f".formatted(resultOperation);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            writeResultInFile(num1, num2, operation, resultOperation);
            outputStream.close();
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            String[] parts = query.split("&");
            for (String part : parts) {
                String[] keyValue = part.split("=");
                if (keyValue.length > 1) {
                    result.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return result;
    }

    private Double chooseOperation(Double num1, Double num2, String operation) {
        double result;
        switch (operation) {
            case "+" -> result = num1 + num2;
            case "-" -> result = num1 - num2;
            case "*" -> result = num1 * num2;
            case "/" -> {
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    result = Double.NaN;
                }
            }
            default -> result = Double.NaN;
        }
        return result;
    }

    private void writeResultInFile(Double num1, Double num2, String operation, Double result) {
        String resultString = "%,.2f%s%,.2f=%,.2f".formatted(num1, operation, num2, result);
        Path path = Paths.get("src/main/resources/history.txt");//src/main/resources/history.txt
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bufferedWriter.write(resultString + '\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
