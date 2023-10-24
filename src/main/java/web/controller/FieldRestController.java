package web.controller;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import domain.myfield.FieldInfo;
import repository.FieldRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import repository.RepositoryFactory;
import repository.utilities.Result;
import repository.utilities.ResultCode;
import trash.DataTypeConverter;
import utilities.common.PropParser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static utilities.common.JsonFormatter.isJson;
import static web.controller.RestServiceFactory.getParameterByIdx;

public class FieldRestController extends RestController {
    static FieldRepository fieldRepository;

    static {
        fieldRepository = RepositoryFactory.getFieldRepository();
    }

    //get method
    //api/field/{className}
    public static Result getFields(HttpExchange exchange) {
        String var1 = getParameterByIdx(exchange.getRequestURI(), first);
        List<FieldInfo> fieldInfo = null;
        if (StringUtils.isNotEmpty(var1)) {
            fieldInfo = fieldRepository.getFieldsByClass(var1);
        }
        if (CollectionUtils.isNotEmpty(fieldInfo)) {
            return Result.newResult(ResultCode.SUCCESS).setObject(fieldInfo);
        } else return Result.newResult(ResultCode.ERROR).setMessage(PropParser.getProp("NAN"));
    }

    //post method
    //api/field/
    public static Result updateField(JsonObject jsonRequest) {
        for (String fieldId : jsonRequest.keySet()) {
            FieldInfo fieldInfo = fieldRepository.getFieldById(fieldId);
            JsonObject fields_json = (JsonObject) jsonRequest.get(fieldId);
            if (fields_json instanceof JsonObject) {
                JsonObject fields = (JsonObject) fields_json;
                for (String fieldName : fields.keySet()) {
                    try {
                        Field field =fieldInfo.getClass().getField(fieldName);
                        Class fieldDataType = field.getType();
                        String getBykey = fields.get(fieldName).toString();
                        if(!isJson(getBykey)){
                            Object value = DataTypeConverter.convert(getBykey,fieldDataType);
                            Method setMethod = fieldInfo.getClass().getMethod("set" + StringUtils.capitalize(fieldName),fieldDataType);

                            if(field!= null && setMethod!=null){
                                setMethod.invoke(fieldInfo, value);
                                return Result.newResult(ResultCode.SUCCESS).setMessage(PropParser.getProp("success")+" update field").setObject(fieldInfo);
                            }
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//                        throw new RuntimeException(e);
                        e.printStackTrace();
                        return Result.newResult(ResultCode.ERROR).setMessage(PropParser.getProp("error") + " updates");
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }return Result.newResult(ResultCode.ERROR).setMessage(PropParser.getProp("error") + " updates; format error.");

    }

    public static String toJson(List<FieldInfo> objects) {
        StringBuilder jsonStr = new StringBuilder("{");

        for (int countObj = 0; countObj < objects.size(); ++countObj) {
            FieldInfo o = objects.get(countObj);
            jsonStr.append(toJson(o));
            if (countObj < objects.size() - 1) {
                jsonStr.append(",");
            }
        }
        jsonStr.append("}");
        return jsonStr.toString();
    }

    public static String toJson(FieldInfo FieldInfo) {
        StringBuilder jsonStr = new StringBuilder("");

        Field[] fields = FieldInfo.getClass().getDeclaredFields();
        for (int countField = 0; countField < fields.length; ++countField) {
            Field field = fields[countField];
            field.setAccessible(true); // You might want to set modifier to public first.
            Object value = "";
            try {
                value = field.get(FieldInfo);
                if (value != null) {
                    if (field.getName().toLowerCase().contains("id")) {
                        jsonStr.append("\"" + value + "\":");
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        return jsonStr.toString();
    }
}
