package utilities.common;

import java.util.UUID;

public class IDGenerator {
    public static UUID get(){
        return UUID.randomUUID();
    }

    public static boolean isUUID (String uuid){
        try{
            UUID.fromString(uuid);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
