package trash;

import web.controller.RestServiceFactory;

public class RestService {
    public void startService(){

        RestServiceFactory.createContext("GET","api/order/getAllField");
    }
}
