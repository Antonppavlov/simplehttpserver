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

    private static int number = 1;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // creates a default executor
        server.createContext("/", new DefaultHttp());

        server.createContext("/default", new GetHandler("/default"));
        server.createContext("/installationTracking", new GetHandler("/installationTracking"));

        server.setExecutor(null);
        server.start();
        System.out.println("The server is running");
    }

    static class GetHandler implements HttpHandler {

       private final String path;
        public GetHandler(String path) {
            this.path=path;
        }

        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();

            Map<String, String> parms = SimpleHttpServer.queryToMap(query);

            System.out.println("\n--------------------------"+path+"----------------------------");
            System.out.println("Запрос " + number + ":\n" + query);
            number++;
            System.out.println("\nПараметры:");

            int numberParam = 1;

            for (String key : parms.keySet()) {
                System.out.println("+++++++  " + numberParam + "  +++++++");
                System.out.println("key: " + key + "\nvalue: " + parms.get(key));
                System.out.println();
                numberParam++;
            }
            numberParam = 1;


            StringBuilder response = new StringBuilder();
            response.append("<html><body>");
            response.append("<h1> Query:</h1>");
            response.append("<p>" + query + "</p>");
            response.append("<h1> Params:</h1>");
            for (String key : parms.keySet()) {
                response.append("<p> " + key +"</p>");
                response.append("<p> " + parms.get(key) + "</p>");
                response.append("<p> ------------------------------------" +"</p>");
            }
            response.append("</body></html>");


            SimpleHttpServer.writeResponse(httpExchange, response.toString());
        }
    }


    static class DefaultHttp implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {

            StringBuilder response = new StringBuilder();
            response.append("<html><body>");

            response.append("<p><a href='score://?uid=12345'>" + "IOS score://?uid=12345" + "</a></p>");

            response.append("<p><a href='admitad://?uid=12345'>" + "Android admitad://?uid=12345" + "</a></p>");
            response.append("<p><a href='javatestapp://?uid=12345'>" + "Android javatestapp://?uid=12345" + "</a></p>");
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

