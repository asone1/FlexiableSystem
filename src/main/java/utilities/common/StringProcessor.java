package utilities.common;

import domain.myobject.ObjectField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StringProcessor {
    public static String autoSpaceStringBuilder(String... strs) {
        StringBuilder result = new StringBuilder();
        for (String s : strs) {
            result.append(s).append(" ");
        }
        return result.toString();
    }
    public static String appendQuote(String originalString){
        return "'"+ originalString+"'";
    }


    public static String concanate(HashSet strings, String separator){
       List result = new ArrayList();
        for (Object o : strings)
            result.add(o);
        return concanate(result,separator);
    }

    public static String concanate(List<Object> strings, String separator){
        StringBuilder result = new StringBuilder();
        int i =1;
        for(Object s:strings){
            result.append(s.toString());
            if(i++ != strings.size()) result.append(separator);
        }
        return result.toString();
    }
    //remove single/double quotes and convert list into string with delimitor
    public static String listToString(List<String> strings, String delimitor){
        StringBuilder result = new StringBuilder();
        if(delimitor.contains("'")||delimitor.contains("\"")){
            String quote = delimitor.contains("'")?"'":"\"";
            String separator = delimitor.replaceAll(quote,"");
            for(String s: strings){
                result.append(quote).append(s).append(quote).append(separator);
            }
        }else{
            for(String s: strings){
                result.append(s).append(delimitor);
            }
        }

        return result.deleteCharAt(result.length()-1).toString();

    }
}
