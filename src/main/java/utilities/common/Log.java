package utilities.common;

public class Log {
    public static void in(String s){
        System.out.println(s);
    }
    public static void in(Object s){
        System.out.println(s.toString());
    }
}
