package repository.utilities;

import org.apache.commons.lang.StringUtils;

public class Result {
    String message;
    ResultCode resultCode;
    Object object;

    public static Result newResult(){
        return new Result();
    }
    public static Result newResult(ResultCode resultCode){
        return new Result().setResultCode(resultCode);
    }


    public boolean ifSuccess(){
        return this.getResultCode().equals(ResultCode.SUCCESS);
    }
    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message; return this;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public Result setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public Object getObject() {
        return object;
    }

    public Result setObject(Object object) {
        this.object = object; return this;
    }

    public void appendMsg(String msg){
        if(StringUtils.isEmpty(getMessage())){
            setMessage("");
        }
        String origin = getMessage();
        setMessage(new StringBuilder(getMessage()).append(msg+"\n").toString());
    }
    @Override
    public String toString(){
        return getResultCode() + ": "+ getMessage();
    }
}
