package my.simple.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SimpleHttpServer {

   private  static int number =1;

    // http://localhost:8000/
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new GetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("The server is running");
    }

    static class GetHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();

            Map<String, String> parms = SimpleHttpServer.queryToMap(query);

            System.out.println("\n------------------------------------------------------");
            System.out.println("Запрос "+number+":\n" +query);
            number++;
            System.out.println("\nПараметры:");

            int numberParam=1;
            for (String key : parms.keySet()) {
                System.out.println("+++++++  "+numberParam+"  +++++++");
                System.out.println("key: "+key + "\nvalue: " + parms.get(key));
                System.out.println();
                numberParam++;
            }
            numberParam=1;

            System.out.println("------------------------------------------------------");

            StringBuilder response = new StringBuilder();
            response.append("<html><body>");
            response.append("<p>" + query + "</p>");
            for (String key : parms.keySet()) {
                response.append("<p>" + key + " " + parms.get(key) + "</p>");
            }
            response.append("</body></html>");


            SimpleHttpServer.writeResponse(httpExchange, response.toString());
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    /**
     * returns the url parameters in a map
     *
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

}