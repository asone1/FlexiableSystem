package repository.utilities;

public enum ResultCode {

        SUCCESS("SUCCESS"), ERROR("ERROR"), WARN("WARN");
        String value;
        ResultCode(String s) {
            value = s;
        }

}
