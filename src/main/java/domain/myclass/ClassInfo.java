package domain.myclass;


import domain.DDObject;
import utilities.common.IDGenerator;

import java.util.UUID;

public class ClassInfo extends DDObject {
    UUID classId;
    String className;
    UUID keyField;

    public ClassInfo(){
        super();
    }
    public ClassInfo(String name){
        this.classId = IDGenerator.get();
        this.className = name.toLowerCase();
    }

    public UUID getKeyField() {
        return keyField;
    }

    public void setKeyField(UUID keyField) {
        this.keyField = keyField;
    }
    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className.toLowerCase();
    }
}



