package repository.utilities;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import utilities.common.Log;

import java.util.ArrayList;
import java.util.List;

import static utilities.common.StringProcessor.autoSpaceStringBuilder;

public class SqlCommand {
    public static String CREATE = "CREATE";
    public static String UPDATE = "UPDATE";
    public static String SELECT = "SELECT";


    public String orderBy;


    public SqlAction action;
    boolean ifDistinct;
    List<Column> columns;
    List<ConditionColumn> whereColumns;
    public String tablename;
    public  short topNumber;

    public String groupBy;

    //default setting
    SqlCommand() {
        super();
        this.ifDistinct = false;
        this.whereColumns = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.topNumber =-1;
    }

    public SqlCommand(SqlAction action, String tablename) {
        this();
        this.action = action;
        this.tablename = tablename;
    }
    public SqlCommand(SqlAction action, String tablename, short topNumber) {
        this();
        this.action = action;
        this.tablename = tablename;
        this.topNumber = topNumber;
    }

    public SqlCommand(SqlAction action, String tablename, List<Column> columns) {
        this();
        this.action = action;
        this.tablename = tablename;
        this.columns = columns;
    }


    public static final String FROM = "FROM";
    public static final String INTO = "INTO";


    public void addWhere(ConditionColumn column) {
        this.whereColumns.add(column);
    }

    public void addWhere(Column column) {
        this.whereColumns.add(column.toConditionColumn());
    }
    public String getWhereClause(){
        if (CollectionUtils.isNotEmpty(whereColumns)) {
            return  autoSpaceStringBuilder(ConditionColumn.WHERE, ConditionColumn.where_clause(whereColumns));
        }
        return "";
    }
    public String builder() {
        StringBuilder clause = new StringBuilder(action.value + " ");
        if(ifDistinct) clause.append("DISTINCT");
        if(getColumns().size()< getWhereColumns().size() && !this.action.equals(SqlAction.SELECT)){
            setColumns(ConditionColumn.toColumn(getWhereColumns()));
        }
        switch (action) {
            //"SELECT VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME=?");
            case SELECT:
                if (CollectionUtils.isNotEmpty(columns)) {
                    if(StringUtils.isNotEmpty(orderBy) && !columns.contains(orderBy)) {
                        clause.append(orderBy+",");
                    }
                    clause.append(autoSpaceStringBuilder(Column.getColumnNames(columns, ","), FROM, tablename));
                } else {
                    clause.append(autoSpaceStringBuilder("*", FROM, tablename));
                }
                if(CollectionUtils.isNotEmpty(this.whereColumns)){
                    clause.append( " "+ autoSpaceStringBuilder(ConditionColumn.WHERE, ConditionColumn.where_clause_CheckNULL(whereColumns)));
                }

                if(topNumber>0){
                    clause.append(" LIMIT "+ topNumber);
                }
                if(orderBy!=null && orderBy.length()>0){
                    clause.append(" ORDER BY "+ orderBy);
                }
                break;
            case UPDATE:
                clause.append(autoSpaceStringBuilder(tablename,"SET"));
                //UPDATE TB01 SET BLADETYPE=?,AIRT1=? WHERE SERIAL=?"
                if (CollectionUtils.isNotEmpty(columns)) {
                    clause.append(Column.getSetColumnNames(columns,"=?,"));
                    clause.append( " "+getWhereClause());
                } else return "";
                break;


            case INSERT:
                //"INSERT INTO survey (id) VALUES(?)";
                clause.append(autoSpaceStringBuilder(INTO, tablename, "("));
                if (CollectionUtils.isEmpty(columns)) {
                    Log.in("Insert error, columns not set.");
                } else {
                    clause.append(autoSpaceStringBuilder(
                            Column.getColumnNames(columns, ",") + ")", "values(") +
                            Column.getQuestionMarks(columns, ",") + ")");
                }


            default:

        }

        Log.in(clause.toString());
        return clause.toString();
    }


    public SqlAction getAction() {
        return action;
    }


    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public List<ConditionColumn> getWhereColumns() {
        return whereColumns;
    }

    public void setWhereColumns(List<ConditionColumn> whereColumns) {
        this.whereColumns = whereColumns;
    }
    public void setWhereColumns(ConditionColumn whereColumn) {
        List whereColumns = new ArrayList<>();
        whereColumns.add(whereColumn);
        this.whereColumns = whereColumns;
    }

    public void setWhereColumns(Column whereColumn) {
        List whereColumns = new ArrayList<>();
        whereColumns.add(whereColumn);
        this.whereColumns = whereColumns;
    }
    public boolean isIfDistinct() {
        return ifDistinct;
    }

    public void setIfDistinct(boolean ifDistinct) {
        this.ifDistinct = ifDistinct;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy,Class ColDataType) {
        this.orderBy = orderBy;
        //should set order add columns??
        this.addColumn(new Column(ColDataType,orderBy));
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
}
