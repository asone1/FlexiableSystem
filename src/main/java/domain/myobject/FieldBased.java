package domain.myobject;

import domain.DDObject;
import domain.myfield.FieldInfo;
import repository.RepositoryFactory;

import java.util.UUID;

public class FieldBased extends DDObject {
    FieldInfo fieldInfo;
    UUID fieldId;
    public void setFieldId(UUID fieldId) {
        this.fieldId = fieldId;
        if(RepositoryFactory.getFieldRepository()!= null){
            FieldInfo fieldInfo1 = RepositoryFactory.getFieldRepository().getFieldById(fieldId);
            if(fieldInfo1 != null){
                setFieldInfo(fieldInfo1);
            }
        }
    }
    public void setFieldInfo(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
    }
}
