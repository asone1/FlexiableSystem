package web.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import domain.myclass.ClassInfo;
import domain.myobject.MyObjFormatter;
import repository.ClassRepository;
import domain.myfield.FieldInfo;
import repository.FieldRepository;
import domain.myobject.StringField;
import repository.RepositoryFactory;
import repository.StringfieldRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import repository.utilities.Result;
import repository.utilities.ResultCode;
import utilities.common.JsonFormatter;

import java.lang.reflect.Field;
import java.util.List;

import static web.controller.RestServiceFactory.getParameterByIdx;

public class StringfieldController extends RestController {
    static StringfieldRepository stringfieldRepository;
    static FieldRepository fieldRepository;
static ClassRepository classRepository;

    static {
        stringfieldRepository = RepositoryFactory.getStringfieldRepository();
        fieldRepository = RepositoryFactory.getFieldRepository();
        classRepository = RepositoryFactory.getClassRepository();
    }

    //get method
    //api/myfield/{className}/{parent}
    public static String getMyFields(HttpExchange exchange) {
        String className = getParameterByIdx(exchange.getRequestURI(), first);
        String parent = getParameterByIdx(exchange.getRequestURI(), second);

        if (StringUtils.isNotEmpty(parent)) {
           return JsonFormatter.toJson(MyObjFormatter.convert(className,stringfieldRepository.getDetails(parent)));
        } else {
            return  stringfieldRepository.getFieldsByClass(className);
        }

    }

    //post method
    //api/myfield/{className}/{fieldName}/{field value}/{parent}
    public static Object insertMyField(HttpExchange exchange) {
        String var1 = getParameterByIdx(exchange.getRequestURI(), first);
        String var2 = getParameterByIdx(exchange.getRequestURI(), second);
        String var3 = getParameterByIdx(exchange.getRequestURI(), third);
        String var4 = getParameterByIdx(exchange.getRequestURI(), fourth);
        if (var4 == null)
            return stringfieldRepository.addFieldToClass(var1, var2, null, var3);
        else
            return stringfieldRepository.addFieldToClass(var1, var2, var3, var4);
    }

    public static String ID = "id";

    //
    public static String insertAnObject(String tableName,JsonObject fields){

        StringBuilder result = new StringBuilder();
            if (fields.has(ID)) {
                String id = fields.get(ID).getAsString();
                if (StringUtils.isEmpty(id)) {
                    return "no parent field in the request.";
                }
                StringField parent = stringfieldRepository.getParentField(id);

                if (parent == null) {
                    //how to distinhuish a field key e.g. order number??
                    ClassInfo myClass = classRepository.getClassByName(tableName);
                    FieldInfo keyField = fieldRepository.getFieldById(myClass.getKeyField());
                    stringfieldRepository.addField(keyField,null,fields.get(ID).getAsString());
                    result.append("added:"+ keyField.getFieldName()+":"+fields.get(ID).getAsString());
                }

                for (String field_id : fields.keySet()) {
                    if (!field_id.equalsIgnoreCase(ID)) {
                        String value = fields.get(field_id).getAsString();
                        FieldInfo fieldInfo = fieldRepository.getFieldById(field_id);
                        stringfieldRepository.addField(fieldInfo, parent, value);
                        result.append("added:"+ fieldInfo.getFieldName()+":"+value);
                    }
                }
            }
        return result.toString();
    }

    public static String insertAnObject(String tableName,JsonArray arr){

        return "";
    }
    //post
    //api/myfield/new
    public static String insertAnObject(String tableName, JsonElement obj) {
        Result result = null;
        if (obj.isJsonArray()) {
            JsonArray arr = obj.getAsJsonArray();
            for (JsonElement ele : arr) {
                if (ele.isJsonObject()) {
                    JsonObject fields = ele.getAsJsonObject();
                    String id = fields.get(ID).getAsString();
                    if (fields.has(ID)) {

                        if (StringUtils.isEmpty(id)) {
                            return "no parent field in the request.";
                        }
                        StringField parent = stringfieldRepository.getParentField(id);

                        if (parent == null) {
                            //how to distinhuish a field key e.g. order number??
                           ClassInfo myClass = classRepository.getClassByName(tableName);
                           FieldInfo keyField = fieldRepository.getFieldById(myClass.getKeyField());
                            result = stringfieldRepository.addField(keyField,null,id);
                        }

                        if(result.getResultCode().equals(ResultCode.SUCCESS)){
                            parent = (StringField) result.getObject();
                        }
                        if (parent != null) {
                            for (String field_id : fields.keySet()) {
                                if (!field_id.equalsIgnoreCase(ID)) {
                                    String value = fields.get(field_id).getAsString();
                                    FieldInfo fieldInfo = fieldRepository.getFieldById(field_id);
                                    stringfieldRepository.addField(fieldInfo, parent, value);
                                }
                            }
                        }

                    }
                }

            }
        }
        if (obj.isJsonObject()) {
            JsonObject fields = obj.getAsJsonObject();

            if (fields.has(ID)) {
                String id = fields.get(ID).getAsString();
                if (StringUtils.isEmpty(id)) {
                    return "no parent field in the request.";
                }
                StringField parent = stringfieldRepository.getParentField(id);

                if (parent == null) {
                    //how to distinhuish a field key e.g. order number??
                    ClassInfo myClass = classRepository.getClassByName(tableName);
                    FieldInfo keyField = fieldRepository.getFieldById(myClass.getKeyField());
                    stringfieldRepository.addField(keyField,null,fields.get(ID).getAsString());
                }

                for (String field_id : fields.keySet()) {
                    if (!field_id.equalsIgnoreCase(ID)) {
                        String value = fields.get(field_id).getAsString();
                        FieldInfo fieldInfo = fieldRepository.getFieldById(field_id);
                        stringfieldRepository.addField(fieldInfo, parent, value);
                    }
                }
            }
        }

        return null;
    }

    //api/myfield/{field name}/{parent name}/{stringfield new value}
    //api/myfield/{stringfield id}/{stringfield new value}/
    public static Object updateMyField(HttpExchange exchange) {

        String firstp = getParameterByIdx(exchange.getRequestURI(), first);
        String secondp = getParameterByIdx(exchange.getRequestURI(), second);
        String thirdp = getParameterByIdx(exchange.getRequestURI(), third);
        StringField stringField = null;
        String newValue = "";
        if (thirdp != null) {
            stringField = stringfieldRepository.getField(firstp);
            newValue = thirdp;
        } else {
            stringField = stringfieldRepository.getParentField(secondp);
            newValue = secondp;
        }

        if (stringField != null) {
            stringfieldRepository.updateField(stringField.getStringfieldId().toString(), newValue);
        } else if (stringField == null) {
            return "no such field exist for my fieldid: " + stringField.getStringfieldId();
        }

        return "end of updateMyField";
    }


    public static String toJson(List<StringField> objects) {
        StringBuilder jsonStr = new StringBuilder("[");

        for (int countObj = 0; countObj < objects.size(); ++countObj) {
            StringField o = objects.get(countObj);
            jsonStr.append("{").append(toJson(o));
            jsonStr.append(",\"id\":\"" + o.getStringfieldId() + "\"").append("}");
            if (countObj < objects.size() - 1) {
                jsonStr.append(",");
            }
        }
        jsonStr.append("]");
        return jsonStr.toString();
    }

    public static String toJson(StringField stringField) {
        StringBuilder jsonStr = new StringBuilder("");
        if (stringField != null) {
            Field[] fields = stringField.getClass().getDeclaredFields();
            for (int countField = 0; countField < fields.length; ++countField) {
                Field field = fields[countField];
                field.setAccessible(true); // You might want to set modifier to public first.
                Object value = "";
                try {
                    value = field.get(stringField);
                    if (value != null) {
                        switch (field.getName()) {
                            case "fieldId":
                                FieldInfo fieldInfo = fieldRepository.getFieldById(value.toString());
                                jsonStr.append("\"" + fieldInfo.getFieldName() + "\":");
                                break;
                            case "value":
                                jsonStr.append("\"" + value + "\"");
                                break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        return jsonStr.toString();
    }
}

