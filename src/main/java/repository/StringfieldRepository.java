package repository;

import domain.myobject.MyObjFormatter;
import domain.myobject.ObjectField;
import domain.myobject.StringField;
import domain.myclass.ClassInfo;
import domain.myfield.FieldInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import domain.myobject.MyObject;
import repository.utilities.Result;
import repository.utilities.ResultCode;
import repository.utilities.*;
import utilities.common.IDGenerator;
import utilities.common.JsonFormatter;
import utilities.common.Log;
import utilities.common.PropParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class StringfieldRepository extends Repository {

    public static final String tableName = "stringfield";
    ClassRepository classRepository;
    FieldRepository fieldRepository;
//    ObjectfieldRepository objectfieldRepository;

    {
        classRepository = RepositoryFactory.getClassRepository();
        fieldRepository = RepositoryFactory.getFieldRepository();
//        objectfieldRepository = RepositoryFactory.getObjectfieldRepository();
    }

    public Class getMyClass() {
        return StringField.class;
    }

    public String getTableName() {
        return tableName;
    }


    public Result addField(FieldInfo field, StringField parent, String value) {

        StringField stringField = null;
        if (parent != null) {
            stringField = new StringField(field.getFieldId(), parent.getStringfieldId(), value);
        } else {
            stringField = new StringField(field.getFieldId(), value);
        }

        List<Column> columns = JDBCConnector.ObjectToColumns(stringField);
        SqlCommand cmd = this.getInsertQuery();
        cmd.setColumns(columns);
        return getObjects(cmd);
    }


    public Object addFieldToClass(String className, String fieldName, String parent, String value) {

        StringField parentField = null;
        if (StringUtils.isEmpty(parent)) {
            if (getParentField(value) != null) {
                return "Parent field should be unique. value: " + value + " existed.";
            }
        } else {
            parentField = getParentField(parent);
        }

        ClassInfo myClass = classRepository.getClassByName(className);
        FieldInfo fieldInfo = fieldRepository.getFieldByName(fieldName);
        if (fieldInfo != null) {
            if (!fieldInfo.isList) {
                if (parentField == null) {
                    return "parent field cannot be list.";
                } else {
                    List<StringField> temp = getFieldByNameParent(fieldInfo, parent);
                    if (CollectionUtils.isNotEmpty(temp)) {
                        return "string field :" + fieldInfo.getFieldName() + " existed for object: " + parentField.getValue();
                    } else {
                        addField(fieldInfo, parentField, value);
                    }
                }

            } else {
                addField(fieldInfo, parentField, value);
            }

            return "add field: " + fieldName + ",whose value is: " + value + " to class: " + className;
        } else {
            return "field " + fieldName + "under class " + className + "does not exist.";
        }


    }

    public StringField getKeyFields(SqlCommand query, String objectID) {
        ConditionColumn var1 = new ConditionColumn(String.class, "value");
        var1.setWhere_op(ConditionColumn.op.eq).setValue(objectID);
        var1.setCondition(ConditionColumn.condition.AND);
        query.addWhere(var1);
        ConditionColumn var2 = new ConditionColumn(UUID.class, "parent");
        var2.setWhere_op(ConditionColumn.op.eq).setValue(null);
        var2.setCondition(ConditionColumn.condition.AND);
        query.addWhere(var2);

        return (StringField) getObject(query);
    }

    public StringField getFieldsByParent(String parentName) {

        SqlCommand query = this.getSelectQuery();
        return getKeyFields(query, parentName);

    }

    public List<StringField> getFieldsByParent(String className, String parentName) {

        List<FieldInfo> fieldInfos = fieldRepository.getFieldsByClass(className);
        List<StringField> result = new ArrayList<>();
        for (FieldInfo fieldInfo : fieldInfos) {
            result.addAll(getFieldByNameParent(fieldInfo, parentName));
        }

        return result;

    }

    public String getFieldsByClass(String className) {
        List<StringField> keyFields = getAllKeys(className);
        List result = new ArrayList();
        for (StringField keyField: keyFields){
            HashMap myfields =MyObjFormatter.convert(className,getDetails(keyField.getValue()));
            if(myfields.size()>0) {
                result.add(myfields);
            }
        }
        return JsonFormatter.toJson(result);

    }

    public StringField getKeyField(String parentName) {

        SqlCommand query = this.getSelectTopQuery((short) 1);
        return getKeyFields(query, parentName);

    }

    public Result getByParent(UUID parentId) {
        if(parentId != null){
            SqlCommand query = this.getSelectQuery();
            ConditionColumn var2 = new ConditionColumn(UUID.class, "parent");
            var2.setWhere_op(ConditionColumn.op.eq).setValue(parentId);
            query.addWhere(var2);
            return getObjects(query);
        }
        return Result.newResult(ResultCode.WARN).setMessage("no object");

    }

    @Override
    public Result getObjects(SqlCommand query) {
        Result result = super.getObjects(query);
        if (result.ifSuccess()) {
            setField((List<StringField>) result.getObject());
        }
        return result;
    }

    public Result getObjects(SqlCommand query, FieldInfo fieldInfo) {
        Result result = super.getObjects(query);
        if (result.ifSuccess()) {
            for (StringField stringField : (List<StringField>) result.getObject()) {
                stringField.setFieldInfo(fieldInfo);
            }
        }
        return result;
    }

    public void setField(List<StringField> stringFields) {
        for (StringField stringField : stringFields) {
            stringField.setFieldInfo(fieldRepository.getFieldById(stringField.getFieldId()));
        }
    }

    public Result getResultByParentField(FieldInfo fieldInfo, UUID parent) {
        SqlCommand query = this.getSelectQuery();
        if (fieldInfo != null) {
            ConditionColumn var1 = new ConditionColumn(UUID.class, "field_id");
            var1.setWhere_op(ConditionColumn.op.eq).setValue(fieldInfo.getFieldId());
            var1.setCondition(ConditionColumn.condition.AND);
            query.addWhere(var1);
            ConditionColumn var2 = new ConditionColumn(UUID.class, "parent");
            var2.setWhere_op(ConditionColumn.op.eq).setValue(parent);
            var2.setCondition(ConditionColumn.condition.AND);
            query.addWhere(var2);
        }
        return getObjects(query, fieldInfo);
    }

    public ResultSet getResultByParentField(FieldInfo fieldInfo, String parentName) {
        StringField stringField = getParentField(parentName);
        return getResultByParentField(fieldInfo, stringField.getParent().toString());
    }

    public List<StringField> getFieldByNameParent(FieldInfo fieldInfo, String parentName) {
        ResultSet rs = getResultByParentField(fieldInfo, parentName);
        List objs = JDBCConnector.getObjects(rs, StringField.class);
        if (objs != null) {
            return (List<StringField>) objs;
        } else return null;
    }

    public StringField getParentField(String parentName) {
        if (parentName == null) return null;
        else {
            SqlCommand query = this.getSelectQuery();
            ConditionColumn var1 = new ConditionColumn(String.class, "value");
            var1.setWhere_op(ConditionColumn.op.eq).setValue(parentName);
            var1.setCondition(ConditionColumn.condition.AND);
            query.addWhere(var1);
            ConditionColumn var2 = new ConditionColumn(UUID.class, "parent");
            var2.setWhere_op(ConditionColumn.op.eq).setValue(null);
            var2.setCondition(ConditionColumn.condition.AND);
            query.addWhere(var2);

            try {
                PreparedStatement ps = JDBCConnector.getStatementByQuery(query.builder());
                Log.in(query.builder());
                JDBCConnector.prepareQuery(ConditionColumn.toColumn(query.getWhereColumns()), ps);
                ps.execute();
                ResultSet rs = (ResultSet) JDBCConnector.execute(ps);
//            JDBCConnector.printResultSet(rs);
                List objs = JDBCConnector.getObjects(rs, StringField.class);
                if (objs != null && objs.size() > 0) {
                    return (StringField) objs.get(0);
                } else return null;
            } catch (SQLException e) {
                Log.in(e.toString());
                e.printStackTrace();
            }
            return null;
        }
    }

    public List<StringField> getFieldsByKeys(UUID keyfieldId) {
        SqlCommand cmd = new SqlCommand(SqlAction.SELECT, this.getTableName());
        cmd.addWhere(new ConditionColumn(UUID.class, "field_id").setValue(keyfieldId));
        Result result = getObjects(cmd);
        if (result.ifSuccess()) {
            return (List<StringField>) result.getObject();
        }
        return new ArrayList<>();

    }


    public Result getKeyFields(String className) {
        ClassInfo classInfo = classRepository.getClassByName(className);
        if (classInfo != null) {
            UUID keyfieldId = classInfo.getKeyField();
            return Result.newResult(ResultCode.SUCCESS).setObject(getFieldsByKeys(keyfieldId));
        }
        return Result.newResult(ResultCode.ERROR).setMessage("class " + className + PropParser.getProp("NAN"));
    }

    public void setMyField(FieldInfo field, UUID parent, MyObject object) {
        Result result = getResultByParentField((FieldInfo) field, parent);
        if (result.getResultCode().equals(ResultCode.SUCCESS)) {
            for (StringField fieldInObject : (List<StringField>) result.getObject()) {
                fieldInObject.setFieldInfo((FieldInfo) field);
            }
            if (result != null) {
//                object.addField((List<StringField>) result.getObject());
            }
        }
    }



    public List<StringField> getAllKeys(String className) {
        Result result = getKeyFields(className);
        if (result.getResultCode().equals(ResultCode.SUCCESS)) {
            return (List<StringField>) result.getObject();

        }
        return new ArrayList<>();
    }


    public List<StringField> getOverview(String className) {

        List<StringField> result = new ArrayList<>();
        for (StringField idField : getAllKeys(className)) {
            result.addAll(getAnObject(idField.getStringfieldId()));
        }
        return result;
    }

    public List<StringField> getAnObject(UUID StringfieldID) {
        Result getFields = getByParent(StringfieldID);
        if (getFields.ifSuccess()) {
            return (List<StringField>) getFields.getObject();
        } else return new ArrayList<>();
    }

    //e.g. 23-0001
    public HashMap<UUID, Object> getDetails(UUID objectID) {
        //return either fieldid :  stringfield
        // or fieldid : [objectfield ...]
        //or object id : [ stringfieldID :  stringfield  ...]
        HashMap result = new HashMap<UUID, Object>();
        for (StringField field : getAnObject(objectID)) {
            result.put(field.getFieldInfo().getFieldId(), field);
        }
        for (ObjectField objField : RepositoryFactory.getObjectfieldRepository().getByParent(objectID)) {
            UUID field = objField.getFieldInfo().getFieldId();
            if (!result.containsKey(field)) {
                result.put(field, new ArrayList<>());
            }
            ((ArrayList<ObjectField>) result.get(field)).add(objField);
        }

        return result;
    }

    public HashMap<UUID, Object> getDetails(String objectID) {
        UUID id = null;
        if (!IDGenerator.isUUID(objectID)) {
            StringField field = getFieldsByParent(objectID);
            if (field != null) {
                return getDetails(field.getStringfieldId());
            }
        }
        return null;
    }


    public Object updateField(String id, String newValue) {

        SqlCommand cmd = this.getUpdateQuery();
        cmd.addColumn(new Column().setColumnName("value").setValue(newValue));
        cmd.addWhere(new ConditionColumn(UUID.class, "field_id"));

        PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(cmd.builder());
        int i = JDBCConnector.prepareQuery(cmd.getColumns(), preparedStatement);
        JDBCConnector.prepareQuery(i, ConditionColumn.toColumn(cmd.getWhereColumns()), preparedStatement);
        return JDBCConnector.execute(preparedStatement);


    }

    public StringField getField(String id) {

        SqlCommand cmd = this.getSelectQuery();
        ConditionColumn conditionColumn = new ConditionColumn(UUID.class, "field_id");
        conditionColumn.setValue(UUID.fromString(id));
        cmd.addWhere(conditionColumn);

        PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(cmd.builder());
        int i = JDBCConnector.prepareQuery(cmd.getColumns(), preparedStatement);
        JDBCConnector.prepareQuery(i, ConditionColumn.toColumn(cmd.getWhereColumns()), preparedStatement);
        JDBCConnector.execute(preparedStatement);
        ResultSet rs = (ResultSet) JDBCConnector.execute(preparedStatement);
        List objs = JDBCConnector.getObjects(rs, StringField.class);
        return (StringField) objs.get(0);

    }


}
