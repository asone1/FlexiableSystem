package trash;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.lang.reflect.Field;
import java.util.List;

public class CustomJson {
    static Gson gson;
    static {

    }

    public static String toJson(Object o){
        return gson.toJson(o);
    }

}
