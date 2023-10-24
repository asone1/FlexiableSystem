package web.controller;

import com.sun.net.httpserver.HttpServer;
import domain.myclass.ClassInfo;
import repository.RepositoryFactory;
import utilities.common.JsonFormatter;
import web.server.HttpRequestHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class RestServiceFactory {
    static HttpServer server;


    static final int portNumber = 8081;

    static {
        try {
            server = HttpServer.create(new InetSocketAddress(portNumber), 0);
            server.createContext("/api", new HttpRequestHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public HttpServer getServer() {
        return server;
    }

    static public String getParameterByIdx(URI url, int index) {
        String query[] = url.toString().split("/");
//        System.out.println( url.toString());
        if (query.length > index) {
//            System.out.println(  query[index]);
            return query[index].replace("%20"," ");
        }
        return null;
    }

    static public String getApiRequest(String url) {
        String port = String.valueOf(portNumber);
        int index = url.indexOf(port);

        if (index > 0 && (index + port.length() - 1) < url.length()) {
            return url.substring(index + port.length());
        }
        return "";
    }

    static public void createContext(String requestMethod, String restPath) {
        server.createContext(restPath, (exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");


            String responseText = "";
            if (requestMethod.equals(exchange.getRequestMethod())) {

                String className = getParameterByIdx(exchange.getRequestURI(), 1);
                ClassInfo myClass = RepositoryFactory.getClassRepository().getClassByName(className);

                responseText = JsonFormatter.toJson(myClass);

                exchange.sendResponseHeaders(200, responseText.getBytes().length);

                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));

    }

    static public void start() {
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static public void stop() {
        server.stop(0);
    }
}
