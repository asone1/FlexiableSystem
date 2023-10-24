package repository;

import domain.myobject.MyObject;
import domain.myobject.ObjectField;
import domain.myobject.StringField;
import domain.myfield.FieldInfo;
import repository.utilities.ConditionColumn;
import repository.utilities.Result;
import repository.utilities.SqlCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ObjectfieldRepository extends Repository {

    public static final String tableName = "objectfield";
    ClassRepository classRepository;
    FieldRepository fieldRepository;
//    StringfieldRepository stringfieldRepository;

    {
        classRepository = RepositoryFactory.getClassRepository();
        fieldRepository = RepositoryFactory.getFieldRepository();
//        stringfieldRepository = RepositoryFactory.getStringfieldRepository();
    }

    public Class getMyClass() {
        return ObjectField.class;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ObjectField> getByParent(UUID idObject) {
        SqlCommand query = this.getSelectQuery();
        ConditionColumn var1 = new ConditionColumn(UUID.class, "parent");
        var1.setWhere_op(ConditionColumn.op.eq).setValue(idObject);
        query.addWhere(var1);
        Result result =  getObjects(query);
        if(result.ifSuccess()){
            List<ObjectField> objects = ( List<ObjectField> )result.getObject();
//            List<StringField> allChildren = new ArrayList<>();
            for (ObjectField childObject : objects) {
                Result result1 = RepositoryFactory.getStringfieldRepository().getByParent(childObject.getObjectfieldId());
                if (result1.ifSuccess()) {
//                    allChildren.addAll((List) result1.getObject());
                    childObject.addFields((List<StringField>) result1.getObject());
                }
            }
            return objects;
        }
       return new ArrayList<>();
    }

    //    public List<MyObject> getResultByParentField(FieldInfo fieldInfo, UUID parent, MyObject childObject) {
    public void getResultByParentField(FieldInfo fieldInfo, UUID parent, MyObject childObject) {
//        List<MyObject> objectFields = new ArrayList();
        if (fieldInfo != null) {
            SqlCommand query = this.getSelectQuery();
            ConditionColumn var1 = new ConditionColumn(UUID.class, "field_id");
            var1.setWhere_op(ConditionColumn.op.eq).setValue(fieldInfo.getFieldId());
            var1.setCondition(ConditionColumn.condition.AND);
            query.addWhere(var1);
            ConditionColumn var2 = new ConditionColumn(UUID.class, "parent");
            var2.setWhere_op(ConditionColumn.op.eq).setValue(parent);
            var2.setCondition(ConditionColumn.condition.AND);
            query.addWhere(var2);
            Result result = getObjects(query);
            if (result.ifSuccess()) {
                List<ObjectField> objects = (List<ObjectField>) result.getObject();
                //remove this one or remove the one at addfield
//                MyObject childObject = new MyObject();
                for (ObjectField childObj : objects) {
                    childObject.setClassName(childObj.getObjectfieldId().toString());
                    Result result_c = RepositoryFactory.getStringfieldRepository().getByParent(childObj.getObjectfieldId());
                    if (result_c.ifSuccess()) {
                        childObject.addField((List<StringField>) result_c.getObject(), childObj.getObjectfieldId());
                    }
                }
//                objectFields.add(childObject);

            }

        }
//        return objectFields;
    }


}
