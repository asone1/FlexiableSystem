package utilities.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropParser {
    public static Properties properties = new Properties();
    static{
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String msgConfigPath = rootPath + "message.properties";
        String catalogConfigPath = rootPath + "catalog";

        Properties msgProp = new Properties();

        try {
            msgProp.load(new FileInputStream(msgConfigPath));
            properties.putAll(msgProp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getProp(String key){
        return properties.getProperty(key);
    }
}
