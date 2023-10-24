package trash;

import domain.myclass.ClassInfo;
import repository.ClassRepository;
import domain.myfield.FieldInfo;
import org.apache.commons.lang.StringUtils;
import repository.utilities.JDBCConnector;
import utilities.common.Log;
import repository.Repository;
import repository.RepositoryFactory;
import utilities.common.PropParser;
import repository.utilities.Column;
import repository.utilities.ConditionColumn;
import repository.utilities.SqlCommand;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FieldRepository_backup extends Repository {

    public static final String tableName = "FieldInfo";
    static ClassRepository classRepository;

    static {
        classRepository = RepositoryFactory.getClassRepository();
    }

    public Class getMyClass() {
        return FieldInfo.class;
    }

    public String getTableName() {
        return tableName;
    }


    public  Object retrieveField(String className, String fieldName)  {

        ClassInfo myClass = classRepository.getClassByName(className);

        if (myClass == null) {
            Log.in("No such class");
            return null;
        }
        SqlCommand query = this.getSelectQuery();

        ConditionColumn var1 = null;
        try {
            var1 = new ConditionColumn(new FieldInfo().getClass().getField("classId"));
            var1.setWhere_op(ConditionColumn.op.eq).setValue(myClass.getClassId());
            query.addWhere(var1);
            if (StringUtils.isNotEmpty(fieldName)) {
                ConditionColumn var2 = new ConditionColumn(new FieldInfo().getClass().getField("fieldName"));
                var2.setWhere_op(ConditionColumn.op.eq).setCondition(ConditionColumn.condition.AND).setValue(fieldName);
                query.addWhere(var2);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }


        try {
            PreparedStatement ps = JDBCConnector.getStatementByQuery(query.builder());
            Log.in(query.builder());
            JDBCConnector.prepareQuery(ConditionColumn.toColumn(query.getWhereColumns()), ps);
            ps.execute();
            ResultSet rs = (ResultSet) JDBCConnector.execute(ps);
            List objs = JDBCConnector.getObjects(rs, FieldInfo.class);
            return (List<FieldInfo>)objs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        return (FieldInfo)objs.get(0);

    }

    public FieldInfo getField(String className, String fieldName) {
        Object obj = retrieveField(className, fieldName);
        List<FieldInfo> result = obj instanceof List ? (List) obj : new ArrayList();
        if (result.size() > 0 && result.get(0) instanceof FieldInfo) {
            return result.get(0);
        }
        return null;

    }
    public List<FieldInfo> getFields(String className) {
        Object obj = retrieveField(className, "");
        List<FieldInfo> result = obj instanceof List ? (List) obj : new ArrayList();
        if (result.size() > 0 && result.get(0) instanceof FieldInfo) {
            return result;
        }
        return null;

    }

    public Object updateField(String className, String fieldName, String newFieldName, Boolean isList) {

        try {
            FieldInfo fieldInfo = getField(className, fieldName);
            FieldInfo newFieldInfo = (FieldInfo) fieldInfo.clone();
            if (fieldInfo != null) {
                newFieldInfo.setFieldName(newFieldName);
                newFieldInfo.setIslist(isList);
                return updateField(fieldInfo, newFieldInfo);
            }

        } catch (CloneNotSupportedException e) {
            Log.in("clone not supported:" + fieldName);
        }

        return PropParser.getProp("error")+" update field: "+ fieldName;

    }
    public Object updateField(String className, String fieldName, String newFieldName) {

        try {
            FieldInfo fieldInfo = getField(className, fieldName);
            FieldInfo newFieldInfo = (FieldInfo) fieldInfo.clone();
            if (fieldInfo != null) {
                newFieldInfo.setFieldName(newFieldName);
               return updateField(fieldInfo, newFieldInfo);
            }

        } catch (CloneNotSupportedException e) {
            Log.in("clone not supported:" + fieldName);
        }

        return PropParser.getProp("error")+" update field: "+ fieldName;

    }

    public Object updateField(FieldInfo fieldInfo, FieldInfo newFieldInfo) {

        try {
            SqlCommand cmd = this.getUpdateQuery();
            cmd.addColumn(new Column().setColumnName("field_name").setValue(newFieldInfo.getFieldName()).setColumnDataType(String.class));
            cmd.setWhereColumns(Column.toConditionColumn(JDBCConnector.ObjectToColumns(fieldInfo)));

            Log.in(cmd.builder());
            PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(cmd.builder());
            int i = JDBCConnector.prepareQuery(cmd.getColumns(), preparedStatement);
            JDBCConnector.prepareQuery(i, ConditionColumn.toColumn(cmd.getWhereColumns()), preparedStatement);
            return  JDBCConnector.execute(preparedStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PropParser.getProp("error")+" update field: "+ fieldInfo.getFieldName();
    }

    public Object addFieldToClass(String className, String fieldName, Boolean isList) {
        ClassInfo myClass = classRepository.getClassByName(className);
        if(myClass==null){
            classRepository.insertClass(new ClassInfo(className));
            myClass = classRepository.getClassByName(className);
        }

        FieldInfo myField = new FieldInfo(fieldName);
//        myField.setClassId(myClass.getClassId());
        myField.setIslist(isList);

        try {
            List<Column> columns = JDBCConnector.ObjectToColumns(myField);
            SqlCommand cmd = this.getInsertQuery();
            cmd.setColumns(columns);
            PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(cmd.builder());
            JDBCConnector.prepareQuery(columns, preparedStatement);
            return JDBCConnector.execute(preparedStatement);
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e.toString());
        }

        return "Fail to add field:"+ fieldName +" to class" + className;
    }
}
