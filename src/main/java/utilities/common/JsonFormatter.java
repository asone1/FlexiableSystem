package utilities.common;

import com.google.gson.*;

import java.io.InputStream;

import static utilities.common.IOformatter.*;
public class JsonFormatter {
    static Gson gson;
    static {
        gson = new Gson();
    }
    public static String toJson(Object o){
        return gson.toJson(o);
    }

    public static JsonElement StringToJson(String input){
        try {
            JsonElement obj;
            if(input.charAt(0) =='['){
                obj = JsonParser.parseString(input).getAsJsonArray();
            }else{
                obj = JsonParser.parseString(input).getAsJsonObject();
            }
            return obj;
        }catch (Exception e){
            Log.in("Error in parsing json.");
            Log.in(e.toString());
        }
        return  null;
    }
    public static JsonElement InputToJson(InputStream inputStream){
//        System.out.println(inputToString(inputStream));
      return StringToJson(inputToString(inputStream,true));
    }


    public static String getQuote(String value){
        return "\"" +value + "\"" ;
    }

    public static boolean isJson(String input) {
        try {
            JsonParser.parseString(input);
        } catch (Exception ex) {

                return false;

        }
        return true;
    }

}
