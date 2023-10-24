package domain.myobject;

import domain.myfield.FieldInfo;
import utilities.common.IDGenerator;

import java.util.UUID;
import static utilities.common.JsonFormatter.*;
public class StringField extends FieldBased {

    UUID stringfieldId;

    UUID parent;
    String value;

    public StringField(){
        super();
    }
    public StringField(UUID fieldId,String value){
        this.stringfieldId = IDGenerator.get();
        this.fieldId = fieldId;
        this.value = value;
    }

    public StringField(UUID fieldId,UUID parent,String value){
        this.stringfieldId = IDGenerator.get();
        this.fieldId = fieldId;
        this.parent = parent;
        this.value = value;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }

    public UUID getFieldId() {
        return fieldId;
    }



    public UUID getStringfieldId() {
        return stringfieldId;
    }

    public void setStringfieldId(UUID stringfieldId) {
        this.stringfieldId = stringfieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }



    @Override
    public String toString()
    {
        return getQuote(stringfieldId.toString()) + ": "+ getQuote(value);
    }


//    {
//        return stringfieldId.toString() + ": "+ value;
//    }

    public String toJsonString(){
        return getQuote(stringfieldId.toString()) + ": "+ getQuote(value);
    }
}
