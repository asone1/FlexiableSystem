package utilities.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class IOformatter {

    public static String inputToString(InputStream inputStream,boolean ifNewLine) {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try{

            for (int length; (length =  inputStream.read(buffer)) != -1; ) {
                    result.write(buffer, 0, length);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            try {
                String textual = result.toString("UTF-8");
                if(!ifNewLine) {
                textual = textual.replaceAll("\r","").replaceAll("\n","").replaceAll("    ","");
                }

//                System.out.println(textual);
                return  textual;
            } catch (UnsupportedEncodingException e) {
                Log.in("Server received payload, and could not convert to UTF8");
            }

        }catch (IOException ioException){
            Log.in("Server received payload, and there is err when reading input");
        }
        return "";
    }
}
