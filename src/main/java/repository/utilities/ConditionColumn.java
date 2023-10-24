package repository.utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static utilities.common.StringProcessor.autoSpaceStringBuilder;

public class ConditionColumn extends Column {
    public static final String WHERE = "WHERE";
    public op where_op;
    public static condition condition;

    public ConditionColumn() {
        super();
        condition = condition.AND;
        where_op = op.eq;
    }

    public ConditionColumn(Field field) {
        super(field);
        condition = condition.AND;
        where_op = op.eq;
    }

    public ConditionColumn(Class columnDataType, String columnName) {
        super(columnDataType, columnName);
        condition = condition.AND;
        where_op = op.eq;
    }


    public ConditionColumn.condition getCondition() {
        return condition;
    }

    public ConditionColumn setCondition(ConditionColumn.condition condition) {
        this.condition = condition;
        return this;
    }

    public op getWhere_op() {
        return where_op;
    }

    public ConditionColumn setWhere_op(op where_op) {
        this.where_op = where_op;
        return this;
    }

    public enum condition {
        AND("AND"), OR("OR");
        String value;

        condition(String s) {
            value = s;
        }
    }

    public enum op {
        eq("="), like("LIKE"), isNotNull("is not null");

        String value;

        op(String s) {
            value = s;
        }
    }

    public static String where_clause(ConditionColumn whereColumn) {
        return autoSpaceStringBuilder(WHERE, whereColumn.getColumnName(), whereColumn.where_op.value);
    }

    public static String where_clause_CheckNULL(List<ConditionColumn> conditionColumns) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < conditionColumns.size(); ++i) {
            ConditionColumn column = conditionColumns.get(i);
            if (i > 0 && i < conditionColumns.size()) {
                result.append(column.getCondition() + " ");
            }

            if(column.getValue()==null){
                result.append(autoSpaceStringBuilder(column.columnName, "is null"));
            }else{
                result.append(autoSpaceStringBuilder(column.columnName, column.where_op.value, "?"));
            }
        }
        return result.toString();
    }

    public static String where_clause(List<ConditionColumn> conditionColumns) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < conditionColumns.size(); ++i) {
            ConditionColumn column = conditionColumns.get(i);
            if (i > 0 && i < conditionColumns.size()) {
                result.append(column.getCondition() + " ");
            }
            result.append(autoSpaceStringBuilder(column.columnName, column.where_op.value, "?"));
        }
        return result.toString();
    }
    public static List<Column> toColumn(List<ConditionColumn> whereCols){
        List<Column> columns =new ArrayList<>();
        for(ConditionColumn conditionColumn:whereCols){
            columns.add(conditionColumn);
        }
        return columns;
    }
}
