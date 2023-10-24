package web.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang.StringUtils;
import utilities.common.IOformatter;

import static utilities.common.IOformatter.*;
import static utilities.common.JsonFormatter.*;

import java.io.*;

import static web.controller.ClassRestController.getClassObj;
import static web.controller.RestServiceFactory.getParameterByIdx;
import static web.controller.FieldRestController.*;
import static web.controller.StringfieldController.*;

public class HttpRequestHandler implements HttpHandler {

    public void handle(HttpExchange exchange) throws IOException {
        String responseText = "";
        String tableName = getParameterByIdx(exchange.getRequestURI(), 2).toLowerCase();
        String requestMethod = exchange.getRequestMethod();
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        responseHeaders.add("Access-Control-Allow-Headers", "*");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Credentials-Header", "*");

//        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
//            responseHeaders.add("Access-Control-Allow-Methods", "GET, OPTIONS");
//            responseHeaders.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
//            exchange.sendResponseHeaders(204, -1);
//            return;
//        }

        Headers requestHeaders = exchange.getRequestHeaders();
//        requestHeaders.add("Access-Control-Allow-Origin", "*");
//        requestHeaders.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
//        requestHeaders.add("Access-Control-Allow-Headers", "*");
//        requestHeaders.add("Access-Control-Allow-Credentials", "true");
//        requestHeaders.add("Access-Control-Allow-Credentials-Header", "*");
//        exchange.getRequestHeaders().forEach((key, value) -> System.out.println(key + " " + value));
//        System.out.println(inputToString(exchange.getRequestBody()));
        System.out.println(exchange.getRequestURI());
        System.out.println(tableName);
        System.out.println(requestMethod);

        System.out.println(requestHeaders);
//        if (requestHeaders.containsKey("Access-Control-Allow-Headers") && requestHeaders.get("Access-Control-Allow-Headers").contains("Content-Type,application/json")) {
//        System.out.println(IOUtils.toByteArray(exchange.getRequestBody()).length);

        String jsonReq = IOformatter.inputToString(exchange.getRequestBody(),false);
        System.out.println(jsonReq);
        if(requestMethod.equalsIgnoreCase("POST") && jsonReq.length()==0){
            responseText = "preflight payload with post request";
            System.out.println("no payload with post request");

        }else{
            /*
            Rest api design:
            1. If Json request:
                1.1 POST -- Create
                1.2 PUT -- Update
            2. If not:
                2.1 GET
                    2.1.1 myfields/order
                    2.1.2 myfields/order/23-0001
                2.2 PUT -- Update
             */
            if (jsonReq.length() > 0) {
                System.out.println(inputToString(exchange.getRequestBody(),false));
                switch (requestMethod.toUpperCase()) {
                    case "POST":
                        switch (tableName) {
                            case "myfield":
                                responseText = insertAnObject(tableName, StringToJson(jsonReq));
                                break;
                            default:
                                responseText = "payload should use post request;";
                        }

                }
            } else {
                switch (requestMethod.toUpperCase()) {
                    case "GET":
                        switch (tableName) {
                            case "class":
                                responseText = getClassObj(exchange);
                                break;
                            case "field":
                                responseText = getFields(exchange).getMessage();
                                break;
                            case "myfield":
                                responseText = getMyFields(exchange);
                                break;

                            default:
                                responseText = "get request default";
                                break;
                        }
                        break;

//                    case "POST":
//                        switch (tableName) {
//                            case "class":
//                                responseText = updateClass(exchange).toString();
//                                break;
//                            case "field":
//                                responseText = updateField(exchange).toString();
//                                break;
//                            case "myfield":
//                                responseText = updateMyField(exchange).toString();
//                                break;
//                            default:responseText = "post request default";
//                                break;
//                        }
//                        break;
                    case "PUT":
                        switch (tableName) {
                            case "myfield":
                                responseText = insertMyField(exchange).toString();
                            default:
                                responseText = "put request default";
                                break;
                        }
                        break;
                    default:
                        responseText = "no payload default";
//                        exchange.sendResponseHeaders(405, -1);
//                        exchange.close();
                        break;

                }
            }
        }



        if (StringUtils.isNotEmpty(responseText)) {
            exchange.sendResponseHeaders(200, responseText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(responseText.getBytes());
            output.close();
        } else {
            exchange.sendResponseHeaders(200, "no result".getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(responseText.getBytes());
            output.close();
        }


    }


}
