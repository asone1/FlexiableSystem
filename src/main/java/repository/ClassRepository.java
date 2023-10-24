package repository;

import domain.myclass.ClassInfo;
import org.apache.commons.lang.StringUtils;
import repository.utilities.JDBCConnector;
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

public class ClassRepository extends Repository {

    public static final String tableName = "ClassInfo";

    public String getTableName() {
        return tableName;
    }

    public Class getMyClass() {
        return ClassInfo.class;
    }
    public Object insertClass(ClassInfo myClass) {

        try {
            List<Column> columns = JDBCConnector.ObjectToColumns(myClass);
            SqlCommand cmd = this.getInsertQuery();
            cmd.setColumns(columns);
            PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(cmd.builder());
            JDBCConnector.prepareQuery(columns, preparedStatement);
            return JDBCConnector.execute(preparedStatement);

        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e.toString());
        }
        return PropParser.getProp("error") + "create " + this.getClass().getName();
    }

    public Object updateClassByName(String name, String value, String keyField) {
        ClassInfo classInfo = getClassByName(name);
        if (classInfo != null) {
            updateClassById(classInfo.getClassId(), value,keyField);
        }
        return name + " " + PropParser.getProp("NAN")+ "in "+ this.getClass().getName();
    }

    public ClassInfo updateClassById(UUID id, String value, String keyField) {
        SqlCommand query = this.getUpdateQuery();
        ConditionColumn whereColumn = new ConditionColumn(UUID.class, "class_id");
        whereColumn.setWhere_op(ConditionColumn.op.eq);
        whereColumn.setValue(id);
        query.addWhere(whereColumn);

        Column column = new Column(String.class, "class_name");
        column.setValue(value);
        query.addColumn(column);
        if(StringUtils.isNotEmpty(keyField)){
            Column column2 = new Column(String.class, "key_field");
            column.setValue(keyField);
            query.addColumn(column2);
        }

        List<Column> where = new ArrayList<Column>();
        where.addAll(query.getWhereColumns());

        return (ClassInfo)getObject(query);
//        try {
//
//            PreparedStatement preparedStatement = JDBCConnector.getStatementByQuery(query.builder());
//            int i = JDBCConnector.prepareQuery(query.columns, preparedStatement);
//            JDBCConnector.prepareQuery(i, where, preparedStatement);
//            System.out.println(query.builder());
//            return preparedStatement.execute();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return "Error in update";

    }

    public ClassInfo getClassByName(String name) {
        name = name.toLowerCase();
        SqlCommand query = this.getSelectTopQuery((short)1);
        ConditionColumn whereColumn = new ConditionColumn(String.class, "class_name");
        whereColumn.setWhere_op(ConditionColumn.op.eq);
        whereColumn.setValue(name);
        query.addWhere(whereColumn);

        PreparedStatement ps = JDBCConnector.getStatementByQuery(query.builder());
        System.out.println(query.builder());
        JDBCConnector.prepareQuery(whereColumn, ps);
//            ps.execute();
//            ResultSet rs = (ResultSet) JDBCConnector.executeQuery(query);
        ResultSet rs = (ResultSet) JDBCConnector.execute(ps);
        List objs = JDBCConnector.getObjects(rs, ClassInfo.class);
        if (objs.size() > 0) {
            if (objs.get(0) instanceof ClassInfo) {
                return (ClassInfo) objs.get(0);
            }
        }
        return null;

    }

    public ClassInfo getClassById(String id) {

        SqlCommand query = this.getSelectQuery();
        ConditionColumn whereColumn = new ConditionColumn(String.class, "class_name");
        whereColumn.setWhere_op(ConditionColumn.op.eq);
        whereColumn.setValue(id);
        query.addWhere(whereColumn);

        PreparedStatement ps = JDBCConnector.getStatementByQuery(query.builder());
        JDBCConnector.prepareQuery(whereColumn, ps);
        ResultSet rs = (ResultSet) JDBCConnector.execute(ps);
        List objs = JDBCConnector.getObjects(rs, ClassInfo.class);
        if (objs.size() > 0) {
            if (objs.get(0) instanceof ClassInfo) {
                return (ClassInfo) objs.get(0);
            }
        }
        return null;

    }

    public List<ClassInfo> getAllClass() {
        SqlCommand query = this.getSelectQuery();
        PreparedStatement ps = JDBCConnector.getStatementByQuery(query.builder());
//            ps.execute();
//            ResultSet rs = (ResultSet) JDBCConnector.executeQuery(query);
        ResultSet rs = (ResultSet) JDBCConnector.execute(ps);
        List objs = JDBCConnector.getObjects(rs, ClassInfo.class);
        return objs;

    }

}
