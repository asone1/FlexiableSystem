package domain.myobject;

import domain.myfield.FieldInfo;
import repository.Repository;
import repository.RepositoryFactory;

import java.util.*;

public class MyObjFormatter {

    //remove escaping******
    //pass string fields -- as an object (HashMap<UUID, Object>
    //covert it to HashMap string
    public static LinkedHashMap convert(String className, HashMap<UUID, Object> myObject){

        //stringfieldID : value
        //or fieldId: [ {stringfield: value, ...}, {...} ]
        LinkedHashMap result = new LinkedHashMap();

        //field map
        HashMap<UUID,Object> map = RepositoryFactory.getFieldRepository().getFullFieldsByClass(className);
        for(UUID fieldId: map.keySet()){
            Object value = map.get(fieldId);
            if(value instanceof String && myObject.containsKey(fieldId) && myObject.get(fieldId) instanceof StringField){
                StringField myField = (StringField) myObject.get(fieldId);
                result.put(myField.getStringfieldId(),myField.getValue());
            }else if(value instanceof HashMap){
                HashMap<UUID,String> childFieldMap = (HashMap) value;
                if(myObject.containsKey(fieldId)){
                    List<ObjectField> objects = (List<ObjectField>) myObject.get(fieldId);
                    List<LinkedHashMap> children = new ArrayList<>();
                    for(ObjectField objectField:objects){
                        LinkedHashMap childResult = new LinkedHashMap<>();
                        for(UUID fieldID:childFieldMap.keySet()){
                            StringField childrenOfObj =objectField.getFieldByFieldId(fieldID);
                            if(childrenOfObj !=null ) {
                                childResult.put(childrenOfObj.getStringfieldId(), childrenOfObj.getValue());
                            }
                        }
                       if(childResult.size()>0) {
                           children.add(childResult);
                       }
                    }
                   if(children.size()>0) {
                       result.put(fieldId, children);
                   }
                }

            }
        }
        return result;
    }


}
