package repository;

import org.apache.commons.collections.CollectionUtils;
import repository.utilities.Result;
import repository.utilities.ResultCode;
import utilities.common.Log;
import repository.utilities.ConditionColumn;
import repository.utilities.JDBCConnector;
import repository.utilities.SqlAction;
import repository.utilities.SqlCommand;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public abstract class Repository{


    public  abstract Class getMyClass();

    public abstract String getTableName();

    public SqlCommand getSelectQuery(){
        return new SqlCommand(SqlAction.SELECT,getTableName());
    }

    public   SqlCommand getSelectTopQuery(short topNumber){
        return new SqlCommand(SqlAction.SELECT,getTableName(),topNumber);
    }
    public   SqlCommand getInsertQuery(){return new SqlCommand(SqlAction.INSERT,getTableName());}

    public   SqlCommand getUpdateQuery(){return new SqlCommand(SqlAction.UPDATE,getTableName());}

      Result getObjects(SqlCommand query){
        try {
            PreparedStatement ps = JDBCConnector.getStatementByQuery(query.builder());
            Log.in(query.builder());
            JDBCConnector.prepareQuery(ConditionColumn.toColumn(query.getWhereColumns()), ps);
            ps.execute();
            ResultSet rs = (ResultSet) JDBCConnector.execute(ps);

            List objs = JDBCConnector.getObjects(rs, getMyClass());
            if (CollectionUtils.isNotEmpty(objs)) {
                return Result.newResult(ResultCode.SUCCESS)
                        .setObject(objs);
            } else return  Result.newResult(ResultCode.WARN)
                    .setMessage("No objects");
        } catch (SQLException e) {
            Log.in(e.toString());
           return Result.newResult(ResultCode.ERROR).setMessage(e.toString());
        }
    }

    public Object getByID(UUID id){
        ConditionColumn var1 = new ConditionColumn(UUID.class, getTableName()+"_id");
        var1.setWhere_op(ConditionColumn.op.eq).setValue(id);
        var1.setCondition(ConditionColumn.condition.AND);
        SqlCommand cmd = this.getSelectQuery();
        cmd.setWhereColumns(var1);
        return   getObject(cmd);
    }

    public Object getObject (SqlCommand query){
        query.topNumber = 1;
        Result result = getObjects(query);
        if(result.ifSuccess()){
            return ((List) result.getObject()).get(0);
        }
        else return null;
    }
}
