package trash;

public class DataTypeConverter {
    public static Object convert(String s,Class datatype){
        if (Boolean.class.equals(datatype)) {
            return Boolean.parseBoolean(s);
        }
        return s;
    }
}
