package domain.myfield;


import domain.DDObject;
import utilities.common.IDGenerator;

import java.util.UUID;

public class FieldInfo extends DDObject {
    public UUID fieldId;
    public String fieldName;
    public Boolean isList;
    public UUID parent;
    public FieldInfo(){
        super();
    }
    public FieldInfo(String name){
        this.fieldId = IDGenerator.get();
        this.fieldName = name;
        this.isList = false;
    }

    public Boolean isList() {
        return isList;
    }

    public void setIslist(Boolean list) {
        isList = list;
    }

    public UUID getFieldId() {
        return fieldId;
    }

    public void setFieldId(UUID fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
//        fieldName = fieldName.toLowerCase();
        this.fieldName = fieldName;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }


}



