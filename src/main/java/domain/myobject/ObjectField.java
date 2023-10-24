package domain.myobject;

import domain.myfield.FieldInfo;
import domain.myobject.FieldBased;
import utilities.common.StringProcessor;

import java.util.*;

public class ObjectField extends FieldBased {


    UUID objectfieldId;
    UUID parent;

    HashSet<StringField> fields;

    ObjectType objectType;
    public enum ObjectType{
        String, Number
    }


    public ObjectField(){
        super();
        fields = new HashSet<>();
    }

    public HashSet<StringField> getFields() {
        return fields;
    }
    public String getFieldsAsString() {

        return "{" + StringProcessor.concanate(fields,",") +"}";
    }

   public StringField getFieldByFieldId(UUID matchingFieldID){
        //#opt, make fieldID and stringfield into map
        for(StringField stringField:fields){
            if(stringField.getFieldId().equals(matchingFieldID)){
                return stringField;
            }
        }
        return null;
   }
    public HashMap<String, String> toHashMap( ){

        HashMap<String, String> result = new HashMap<>();
        for(StringField s: getFields()){
            result.put(s.getStringfieldId().toString(), s.getValue());
        }
        return result;
    }

    public void setFields(HashSet<StringField> fields) {
        this.fields = fields;
    }

    public void addFields(List<StringField> fields) {
        for(StringField field: fields){
            addField(field);
        }
    }

    public void addField(StringField field) {
        this.fields.add(field);
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public UUID getFieldId() {
        return fieldId;
    }

    public UUID getObjectfieldId() {
        return objectfieldId;
    }

    public void setObjectfieldId(UUID objectId) {
        this.objectfieldId = objectId;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }
}
