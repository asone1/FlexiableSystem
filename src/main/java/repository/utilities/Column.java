package repository.utilities;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Column {
    String columnName;
    Class ColumnDataType;

    Object value;

    public Column(){
        super();
    }
    public Column(Class columnDataType, String columnName){
        this.ColumnDataType = columnDataType;
        this.columnName = columnName;
    }

    public Column(Field field){
        this.ColumnDataType = field.getType();
        this.columnName =  CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, field.getName());
    }
    public String getColumnName() {
        return columnName;
    }

    public Column setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public Class getColumnDataType() {
        return ColumnDataType;
    }

    public Column setColumnDataType(Class columnDataType) {
        ColumnDataType = columnDataType;return  this;
    }

    public Object getValue() {
        return value;
    }

    public Column setValue(Object value) {
        if(getColumnName().toLowerCase().contains("id")&&value instanceof String){
            this.value = UUID.fromString((String) value);
        }
        else{
            this.value = value;
        }

        return this;
    }

    public static String getColumnNames(List<Column>columns,String delimitor){
        StringBuilder result = new StringBuilder();
        for(int i=0; i< columns.size();++i){
            result.append(columns.get(i).getColumnName());
                    if(i<columns.size()-1) result.append(delimitor);
        }
        return result.toString();
    }
    public static String getSetColumnNames(List<Column>columns,String delimitor){
        StringBuilder result = new StringBuilder();
        if(columns.size()==1){
            result.append(columns.get(0).getColumnName()).append(delimitor.replaceAll(",",""));
        }else {
            for(int i=0; i< columns.size();++i){
                result.append(columns.get(i).getColumnName()+ " ");
                if(i==columns.size()-1) result.append(delimitor.replaceAll(",",""));
                else result.append(delimitor);

            }
        }

        return result.toString();
    }
    public static String getQuestionMarks(List<Column>columns,String delimitor){
        StringBuilder result = new StringBuilder();
        for(int i=0; i< columns.size();++i){
            result.append("?");
            if(i<columns.size()-1) result.append(delimitor);
        }
        return result.toString();
    }


    public ConditionColumn toConditionColumn(){
        return (ConditionColumn) new ConditionColumn(this.getColumnDataType(),this.getColumnName()).setValue(this.getValue());
    }
    public static List<ConditionColumn> toConditionColumn(List<Column> columns){
        List<ConditionColumn> result = new ArrayList<>();
        for(Column column:columns){
            result.add(column.toConditionColumn());
        }
        return result;
    }


}
