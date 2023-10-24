package repository;

import com.google.gson.Gson;
import domain.myfield.FieldComparator;
import domain.myfield.FieldInfo;
import domain.myclass.ClassInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import repository.utilities.*;
import utilities.common.Log;
import utilities.common.PropParser;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FieldRepository extends Repository {
    //another table related to field repository is classfield
    public static final String fieldMapTable = "classfields";
    public static final String tableName = "FieldInfo";

    public String getTableName() {
        return tableName;
    }

    public Class getMyClass() {
        return FieldInfo.class;
    }

    ClassRepository classRepository;

    {
        classRepository = RepositoryFactory.getClassRepository();
    }

    public FieldInfo getFieldById(String id) {
        return getFieldById(UUID.fromString(id));
    }

    public FieldInfo getFieldById(UUID id) {
        SqlCommand query = this.getSelectQuery();

        ConditionColumn var1 = null;
        try {
            var1 = new ConditionColumn(new FieldInfo().getClass().getField("fieldId"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        var1.setWhere_op(ConditionColumn.op.eq).setValue(id);
        query.addWhere(var1);
        PreparedStatement ps = JDBCConnector.getStatementByQuery(query.builder());
        Log.in(query.builder());
        JDBCConnector.prepareQuery(ConditionColumn.toColumn(query.getWhereColumns()), ps);
        ResultSet rs = (ResultSet) JDBCConnector.execute(ps);
        List objs = JDBCConnector.getObjects(rs, FieldInfo.class);
        if (CollectionUtils.isNotEmpty(objs))
            return (FieldInfo) objs.get(0);
        else return null;

    }

    public FieldInfo getFieldByName(String name) {
        SqlCommand query = this.getSelectQuery();

        ConditionColumn var1 = null;
        try {
            var1 = new ConditionColumn(new FieldInfo().getClass().getField("fieldName"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        var1.setWhere_op(ConditionColumn.op.eq).setValue(name);
        query.addWhere(var1);
        PreparedStatement ps = JDBCConnector.getStatementByQuery(query.builder());
        Log.in(query.builder());
        JDBCConnector.prepareQuery(ConditionColumn.toColumn(query.getWhereColumns()), ps);
        ResultSet rs = (ResultSet) JDBCConnector.execute(ps);
        List objs = JDBCConnector.getObjects(rs, FieldInfo.class);
        if (CollectionUtils.isNotEmpty(objs)) {
            return (FieldInfo) objs.get(0);
        }
        return null;

    }

    public HashMap toMap(List<FieldInfo> fields) {
        Collections.sort(fields, new FieldComparator());
        UUID parent = null;
        HashMap<String, Object> fieldMap = new HashMap<>();
        HashMap<String, String> childFields = new HashMap<>();

        for (int i = 0; i < fields.size(); ++i) {
            FieldInfo fieldInfo = fields.get(i);

            if (fieldInfo.getParent() != null) {

                if (parent == null || fieldInfo.getParent().equals(parent)) {
                    childFields.put(fieldInfo.getFieldId().toString(), fieldInfo.getFieldName());
                }
                if ((parent != null && !fieldInfo.getParent().equals(parent)) || i == fields.size() - 1) {
                    fieldMap.put(fieldInfo.getParent().toString(), childFields);
                    childFields = new HashMap<>();
                }
                parent = fieldInfo.getParent();
            } else {
                fieldMap.put(fieldInfo.getFieldId().toString(), fieldInfo.getFieldName());
            }

        }
        System.out.println(new Gson().toJson(fieldMap));
        return fieldMap;
    }

    public String toJson(List<FieldInfo> fields) {
        return new Gson().toJson(toMap(fields));
    }

    public HashSet toObject(List<FieldInfo> fields) {
        Collections.sort(fields, new FieldComparator());
        UUID parent = null;
        //HashSet<FieldInfo> +  List<HashSet<FieldInfo>>
        HashSet fieldMap = new HashSet<>();
        List<HashSet<FieldInfo>> childList = new ArrayList<>();
        HashSet childFields = new HashSet<>();

        for (int i = 0; i < fields.size(); ++i) {
            FieldInfo fieldInfo = fields.get(i);

            if (fieldInfo.getParent() != null) {

                if (parent == null || fieldInfo.getParent().equals(parent)) {
                    childFields.add(fieldInfo);
                }
                if ((parent != null && !fieldInfo.getParent().equals(parent)) || i == fields.size() - 1) {
                    childList.add(childFields);
                    childFields = new HashSet<>();
                }
                parent = fieldInfo.getParent();
            } else {
                fieldMap.add(fieldInfo);
            }

        }
        fieldMap.add(childList);
        System.out.println(new Gson().toJson(fieldMap));
        return fieldMap;
    }


    HashMap<String, List> map = new HashMap<>();
    List<Object> temp = new ArrayList();

    public void getFieldMap(List<FieldInfo> fields) {

        for (FieldInfo fieldInfo : fields) {
            if (fieldInfo.isList) {
                UUID parent = fieldInfo.getParent();
                List arr = null;
                if (map.get(parent.toString()) == null) {
                    arr = new ArrayList<>();
                    map.put(parent.toString(), arr);
                } else {
                    arr = map.get(parent.toString());
                }
                if (!temp.contains(arr)) temp.add(arr);
            } else {
                temp.add(fieldInfo.getFieldId().toString());
            }
        }

    }

//    public static HashMap<String,Object> listToMap(List list){
//        HashMap<String,Object> result = new HashMap<>();
//        for(Object obj:list){
//            if(obj instanceof  String){
//                result.put(obj.toString())
//            }
//        }
//    }

    public LinkedHashMap<UUID, Object> getFullFieldsByClass(String className) {
        //Here, object is either string or hashmap<UUID, String>
        LinkedHashMap<UUID, Object> result = new LinkedHashMap();
        List<FieldInfo> fields = getFieldsByClass(className);
        for (FieldInfo fieldInfo : fields) {
            UUID fieldId = fieldInfo.getFieldId();
            if (fieldInfo.isList) {
                LinkedHashMap<UUID,String> childMap = null;
                if (!result.containsKey(fieldId)) {
                    result.put(fieldId, new LinkedHashMap<>());
                }
                childMap = (LinkedHashMap) result.get(fieldId);
                List<FieldInfo> children = getByParent(fieldInfo.getFieldId());
                for (FieldInfo childField : children) {
                    childMap.put(childField.getFieldId(), childField.getFieldName());
                }
            } else {
                result.put(fieldInfo.getFieldId(), fieldInfo.getFieldName());
            }
        }
        return result;
    }

    public List<FieldInfo> getFieldsByClass(String className) {
        ClassInfo classInfo = classRepository.getClassByName(className);
        SqlCommand cmd = new SqlCommand(SqlAction.SELECT, fieldMapTable);

        cmd.addColumn(new Column(UUID.class, "field_id"));
        cmd.setWhereColumns(new ConditionColumn(UUID.class, "class_id").setValue(classInfo.getClassId()));
        PreparedStatement ps = JDBCConnector.getStatementByQuery(cmd.builder());
        JDBCConnector.prepareQuery(ConditionColumn.toColumn(cmd.getWhereColumns()), ps);
        Log.in(cmd.builder());
        ResultSet rs = (ResultSet) JDBCConnector.execute(ps);
        List<UUID> uuids = new ArrayList<>();
        try {
            while (rs.next()) {
                int i = 1;
                Object id = null;
                id = rs.getObject(i);
                if (id instanceof UUID) {
                    uuids.add((UUID) id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List fields = new ArrayList();
        for (UUID id : uuids) {
            FieldInfo f = getFieldById(id);
            if (f != null) {
                fields.add(f);
            }
        }
        return fields;
    }

    Result insertField(FieldInfo fieldInfo) {
        List<Column> columns = JDBCConnector.ObjectToColumns(fieldInfo);
        SqlCommand cmd = this.getInsertQuery();
        cmd.setColumns(columns);
        PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(cmd.builder());
        JDBCConnector.prepareQuery(columns, preparedStatement);
        JDBCConnector.execute(preparedStatement);
        return Result.newResult(ResultCode.SUCCESS);
    }

    public List<FieldInfo> getByParent(UUID uuid) {
        SqlCommand query = new SqlCommand(SqlAction.SELECT, this.getTableName());
        query.addWhere(new ConditionColumn(UUID.class, "parent").setValue(uuid));
        Result result = getObjects(query);
        if (result.ifSuccess()) {
            return (List<FieldInfo>) result.getObject();
        }
        return new ArrayList<>();
    }

    public FieldInfo createField(String fieldName, String parent, Boolean isList) {
        FieldInfo fieldInfo = new FieldInfo(fieldName);
        FieldInfo parentField = getFieldByName(parent);
        if (parentField != null) {
            fieldInfo.setParent(parentField.getFieldId());
        }
        fieldInfo.setIslist(isList);
        insertField(fieldInfo);
        return fieldInfo;
    }

    public FieldInfo createField(String fieldName, String parent) {
        return createField(fieldName, parent, false);
    }

    public Result addFieldToClass(String className, String... fieldnames) {
        Result result = Result.newResult();
        List<FieldInfo> fields = new ArrayList();
        for (String fieldname : fieldnames) {
            FieldInfo fieldInfo = getFieldByName(fieldname);
            if (fieldInfo != null) fields.add(fieldInfo);
            else result.appendMsg(fieldname + PropParser.getProp("NAN"));
        }
        return addFieldToClass(className, fields);
    }

    public Result addFieldToClass(String className, List<FieldInfo> fields, Result result) {

        ClassInfo classInfo = classRepository.getClassByName(className);
        if (classInfo == null) {
            result.appendMsg(className + PropParser.getProp("NAN"));
            result.setResultCode(ResultCode.WARN);
        }
        for (FieldInfo fieldInfo : fields) {
            SqlCommand cmd = new SqlCommand(SqlAction.INSERT, fieldMapTable);
            cmd.addColumn(new Column(UUID.class, "class_id").setValue(classInfo.getClassId()));
            cmd.addColumn(new Column(UUID.class, "field_id").setValue(fieldInfo.getFieldId()));

            Log.in(cmd.builder());
            try {
                PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(cmd.builder());
                int i = JDBCConnector.prepareQuery(cmd.getColumns(), preparedStatement);

                JDBCConnector.execute(preparedStatement);

            } catch (Exception e) {
                e.printStackTrace();
                result.appendMsg(PropParser.getProp("error") + "add fields to class:" + fieldInfo);
                result.setResultCode(ResultCode.WARN);
            }
        }
        if (result.getResultCode() == null) result.setResultCode(ResultCode.SUCCESS);
        result.appendMsg("Fields created.");
        return result;
    }

    public Result addFieldToClass(String className, List<FieldInfo> fields) {
        return addFieldToClass(className, fields, new Result());
    }

    public Result addFieldToClass(String className, FieldInfo fieldInfo) {
        List arr = new ArrayList<>();
        arr.add(fieldInfo);
        return addFieldToClass(className, arr);
    }

    public Result updateField(UUID fieldId, FieldInfo newFieldInfo) {
        try {
            SqlCommand cmd = this.getUpdateQuery();

            cmd.setWhereColumns(new ConditionColumn(UUID.class, "field_id"));

            if (StringUtils.isNotEmpty(newFieldInfo.getFieldName())) {
                cmd.addColumn(new Column().setColumnName("field_name").setValue(newFieldInfo.getFieldName()).setColumnDataType(String.class));
            }
            if (newFieldInfo.isList) {
                cmd.addColumn(new Column().setColumnName("isList").setValue(newFieldInfo.isList).setColumnDataType(Boolean.class));
            }
            if (newFieldInfo.getParent() != null) {
                cmd.addColumn(new Column().setColumnName("parent").setValue(newFieldInfo.getParent()).setColumnDataType(UUID.class));
            }
            Log.in(cmd.builder());
            PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(cmd.builder());
            int i = JDBCConnector.prepareQuery(cmd.getColumns(), preparedStatement);
            JDBCConnector.prepareQuery(i, ConditionColumn.toColumn(cmd.getWhereColumns()), preparedStatement);
            JDBCConnector.execute(preparedStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.newResult(ResultCode.ERROR).setMessage(PropParser.getProp("error") + " update failure field: " + fieldId);

    }
}
