package domain.myobject;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import repository.*;
import domain.myfield.FieldInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MyObject {
    static ClassRepository classRepository;
    static FieldRepository fieldRepository;
    static StringfieldRepository stringfieldRepository;
    static ObjectfieldRepository objectfieldRepository;

    static {
        classRepository = RepositoryFactory.getClassRepository();
        fieldRepository = RepositoryFactory.getFieldRepository();
        stringfieldRepository = RepositoryFactory.getStringfieldRepository();
        objectfieldRepository = RepositoryFactory.getObjectfieldRepository();
    }

    String className;

    public MyObject() {
        super();
        fields = new HashMap<>();
    }

    public MyObject(String keyField) {
        this();
        this.className = keyField;
    }

    //either StringField or List<MyObject>(= List<StringField>)
    //stringfieldid : string value
    //stringfieldid : HashMap of {stringfieldid : string value}
    public HashMap<String, Object> fields;
    public void addField(String id, List<MyObject> children) {
        fields.put(id,children);
    }

    public void addField2(StringField stringField, UUID objectId) {


            if (objectId == null) {
                fields.put(stringField.getStringfieldId().toString(),stringField);
            } else {
                String id = objectId.toString();
                if(fields.get(id)==null){
//                    fields.put(id,new HashMap<String,StringField>());
                    fields.put(id,new MyObject(objectId.toString()));
                }
//                HashMap childs = (HashMap<String,StringField>)fields.get(id);
//                childs.put(stringField.getStringfieldId().toString(),stringField);
                MyObject child = (MyObject) fields.get(id);
                child.addField2(stringField,null);
            }
    }
    public void addField(StringField stringField, String className) {
        UUID parent = stringField.getFieldInfo().getParent();
        if (parent == null) {
            List field = fields.get(className) == null ? new ArrayList<>() : (List) fields.get(className);
            field.add(stringField);
            fields.put(className, field);
        } else {
            MyObject nestedObj = fields.get(parent.toString()) == null ? new MyObject() : (MyObject) fields.get(parent.toString());
            List field =null;
            if(nestedObj.fields.get(parent.toString()) == null){
                field = new ArrayList();
                nestedObj.fields.put(parent.toString(),field);
                fields.put(parent.toString(),nestedObj);

            }else{
                field = (List) nestedObj.fields.get(parent.toString());
            }
            field.add(stringField);
        }
    }


    public void addField(List<StringField> stringFields, UUID className) {
        for (StringField stringField : stringFields) {
            addField2(stringField,  className);
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public static String getQuote(String value){
        return "\"" +value + "\"" ;
    }
    public String toString() {
        StringBuilder result = new StringBuilder();
        HashMap<String, StringBuilder> childs = new HashMap();
        for (String key : fields.keySet()) {
           if(fields.get(key) instanceof  StringField){
               result.append(fields.get(key));
           }
           else if(fields.get(key) instanceof  HashMap){
                HashMap<String, StringField> childMap = ((HashMap<String, StringField>) fields.get(key));
                StringBuilder childResult = null;
                int c_obj =1;
                for (String objectId : childMap.keySet()) {
                    StringField childObj = childMap.get(objectId);
//                    String fieldId = childObj.getFieldInfo().getFieldId().toString();
                    ObjectField parentObj = (ObjectField)objectfieldRepository.getByID(UUID.fromString(key));
                    FieldInfo fieldInfo = parentObj.getFieldInfo();
                    if(fieldInfo != null){
                        String fieldId = fieldInfo.getFieldId().toString();
                        if(childs.get(fieldId)== null){
                            childs.put(fieldId,new StringBuilder());
                        }
                        childResult =childs.get(fieldId);
//                        if()
                        childResult.append(getQuote(childObj.getStringfieldId().toString())+":"+getQuote(childObj.getValue()));
                        if(c_obj ++ != childMap.size()){
                            childResult.append(",");
                        }
                    }

                }
                childResult.append("},");
            }

        }
        int c_key = 1;
        for(String key: childs.keySet()){
           if(c_key ==1) result.append(getQuote(key)+":[");
           result.append(childs.get(key));
            if(c_key !=childs.keySet().size()) result.append(",");
            if(c_key++ ==childs.keySet().size()) result.append("]");
        }
//        if(result.charAt(result.length()-1) == ','){
//            result.deleteCharAt(result.length()-1);
//        }

        return result.toString();
    }
    public static String toMyJson(List<MyObject> objs){
        StringBuilder s = new StringBuilder("[");
        int i =1;
        for(MyObject obj:objs){
            if(obj.toString().length()>2){
                s.append("{"+obj+"},");
            }

        }
        if(s.charAt(s.length()-1) == ','){
            s.deleteCharAt(s.length()-1);
        }
        return  s.append("]").toString();
    }

    public String toJson() {
        Gson json = new GsonBuilder().create();
//        if (CollectionUtils.isNotEmpty(this.fields)) {
//            return json.toJson(this.fields);
//        }
        return "";
    }
}
