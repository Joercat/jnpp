import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Server {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8003), 0);
        
        server.createContext("/", new RootHandler());
        server.createContext("/health", new HealthHandler());
        server.createContext("/calculate", new CalculateHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("Java server running on port 8003");
    }
    
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\":\"Java Server is running!\",\"endpoints\":[\"/health\",\"/calculate\"],\"server\":\"Java\"}";
            sendResponse(exchange, response, 200);
        }
    }
    
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String response = "{\"status\":\"OK\",\"server\":\"Java\",\"timestamp\":\"" + timestamp + "\"}";
            sendResponse(exchange, response, 200);
        }
    }
    
    static class CalculateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                String response = "{\"error\":\"Method not allowed\"}";
                sendResponse(exchange, response, 405);
                return;
            }
            
            try {
                // Read request body
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    body.append(line);
                }
                
                // Parse JSON manually (simple approach)
                String json = body.toString();
                Map<String, Double> numbers = parseNumbers(json);
                
                if (numbers == null) {
                    String response = "{\"error\":\"Invalid JSON or missing numbers\"}";
                    sendResponse(exchange, response, 400);
                    return;
                }
                
                double num1 = numbers.get("num1");
                double num2 = numbers.get("num2");
                
                // Perform calculations
                double addition = num1 + num2;
                double subtraction = num1 - num2;
                double multiplication = num1 * num2;
                double division = num2 != 0 ? num1 / num2 : Double.NaN;
                double power = Math.pow(num1, num2);
                double average = (num1 + num2) / 2;
                
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                
                String response = String.format(
                    "{\"num1\":%.2f,\"num2\":%.2f,\"addition\":%.2f,\"subtraction\":%.2f,\"multiplication\":%.2f,\"division\":%s,\"power\":%.2f,\"average\":%.2f,\"server\":\"Java\",\"timestamp\":\"%s\"}",
                    num1, num2, addition, subtraction, multiplication, 
                    Double.isNaN(division) ? "\"Cannot divide by zero\"" : String.format("%.2f", division),
                    power, average, timestamp
                );
                
                sendResponse(exchange, response, 200);
                
            } catch (Exception e) {
                String response = "{\"error\":\"" + e.getMessage() + "\"}";
                sendResponse(exchange, response, 500);
            }
        }
        
        private Map<String, Double> parseNumbers(String json) {
            try {
                Map<String, Double> numbers = new HashMap<>();
                
                // Simple JSON parsing for num1 and num2
                String[] parts = json.replace("{", "").replace("}", "").split(",");
                for (String part : parts) {
                    String[] keyValue = part.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replace("\"", "");
                        double value = Double.parseDouble(keyValue[1].trim());
                        numbers.put(key, value);
                    }
                }
                
                if (numbers.containsKey("num1") && numbers.containsKey("num2")) {
                    return numbers;
                }
                
            } catch (Exception e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
            }
            return null;
        }
    }
    
    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
