package trash;


import java.sql.*;
import java.util.Map;

import org.apache.log4j.Logger;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

public class Querybuilder{

    static ResultSet resObj;
    static Statement stmtObj;
    static Connection connObj;

    static DbSchema schemaObj;
    static DbSpec specficationObj;

    static DbTable table_name;


    public final static Logger logger = Logger.getLogger(Querybuilder.class);
     class ColumnInfo{
        String type;
        Integer length;
        String colName;

       public String getType() {
           return type;
       }

       public void setType(String type) {
           this.type = type;
       }

       public Integer getLength() {
           return length;
       }

       public void setLength(Integer length) {
           this.length = length;
       }

       public String getColName() {
           return colName;
       }

       public void setColName(String colName) {
           this.colName = colName;
       }
   }

    // Helper Method #1 :: This Method Is Used To Create A Connection With The Database
    public static void connectDb() {
        try {
            Class.forName("org.postgresql.Driver");
            connObj = DriverManager.getConnection("jdbc:postgresql://localhost/Kabalah", "postgres", "12345678");
            logger.info("\n=======Database Connection Open=======\n");

            stmtObj = connObj.createStatement();
            logger.info("\n=======Statement Object Created=======\n");

            loadSQLBuilderSchema();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
    }

    // Helper Method #2 :: This Method Is Used To Create Or Load The Default Schema For The SQLBuilder
    private static void loadSQLBuilderSchema() {
        specficationObj = new DbSpec();
        schemaObj = specficationObj.addDefaultSchema();
    }

    // Helper Method #3 :: This Method To Used To Close The Connection With The Database
    public static void disconnectDb() {
        try {
            stmtObj.close();
            connObj.close();
            logger.info("\n=======Database Connection Closed=======\n");
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static void displayAllRecords(String TableName) {
        SelectQuery query = new SelectQuery();
        System.out.println(query.toString());
        query.addAllTableColumns(new DbTable(schemaObj,TableName));
        try {
            stmtObj.execute(query.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void insertDataInTable(String tableName, Map<DbColumn,String> Cols) {
        String insertTableQuery="";
        logger.info("\n=======Inserting Record In The '" + tableName + "'=======\n");
        try {
            InsertQuery query = new InsertQuery(tableName);
            for(Map.Entry<DbColumn,String> map :Cols.entrySet()){
                DbColumn columnInfo =map.getKey();
                query.addColumn(
                        new DbColumn(schemaObj.findTable(tableName),columnInfo.getName(),columnInfo.getTypeNameSQL()),map.getValue());
            }

            logger.info("\nGenerated Sql Query?= "+ insertTableQuery + "\n");
            stmtObj.execute(insertTableQuery.toString());
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("\n=======Record Sucessfully Inserted  In The '" + tableName + "'=======\n");
    }

    /*
    // SQLQueryBuilder #1 :: This Method Is Used To Perform The Create Operation In The Database
    public static void createDbTable(String TABLE_NAME, ColumnInfo ... COLS) {
        logger.info("\n=======Creating '" +TABLE_NAME + "' In The Database=======\n");
        try {
            // Specifying Table Name
            DbTable table = schemaObj.addTable(TABLE_NAME);

            // Specifying Column Names For The Table
            addColumns(table, COLS);
            String createTableQuery = new CreateTableQuery(table, true).validate().toString();
            logger.info("\nGenerated Sql Query?= "+ createTableQuery + "\n");
            stmtObj.execute(createTableQuery);
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("\n=======The '" + TABLE_NAME + "' Successfully Created In The Database=======\n");
    }



    // SQLQueryBuilder #2 :: This Method Is Used To Perform The Insert Operation In The Database
    public static void insertDataInTable(String tableName, Map<ColumnInfo,String> Cols) {
        String insertTableQuery;
        logger.info("\n=======Inserting Record In The '" + tableName + "'=======\n");
        try {
            InsertQuery query = new InsertQuery(table_name);
            query.addColumn(new DbColumn(tableName,))
            insertTableQuery = new InsertQuery(table_name).t;
            logger.info("\nGenerated Sql Query?= "+ insertTableQuery + "\n");
            stmtObj.execute(insertTableQuery);
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("\n=======Record Sucessfully Inserted  In The '" + TABLE_NAME + "'=======\n");
    }

    // SQLQueryBuilder #3 :: This Method Is Used To Display All Records From The Database
    public static void displayRecords() {
        String displayRecordsQuery;
        logger.info("\n=======Displaying All Records From The '" + TABLE_NAME + "'=======\n");
        try {
            displayRecordsQuery = new SelectQuery().addColumns(column_1).addColumns(column_2).addColumns(column_3).validate().toString();
            logger.info("\nGenerated Sql Query?= "+ displayRecordsQuery + "\n");

            resObj = stmtObj.executeQuery(displayRecordsQuery);
            if(!resObj.next()) {
                logger.info("\n=======No Records Are Present In The '" + TABLE_NAME + "'=======\n");
            } else {
                do {
                    logger.info("\nId?= " + resObj.getString(COLUMN_ONE) + ", Name?= " + resObj.getString(COLUMN_TWO) + ", Salary?= " + resObj.getString(COLUMN_THREE) + "\n");
                } while (resObj.next());
                logger.info("\n=======All Records Displayed From The '" + TABLE_NAME + "'=======\n");
            }
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
    }

    // SQLQueryBuilder #4 :: This Method Is Used To Display A Specific Record From The Database
    public static void displaySelectiveRecord(int emp_id) {
        String selectiveRecordQuery;
        logger.info("\n=======Displaying Specific Record From The '" + TABLE_NAME + "'=======\n");
        try {
            selectiveRecordQuery = new SelectQuery().addColumns(column_1).addColumns(column_2).addColumns(column_3).addCondition(BinaryCondition.equalTo(column_1, emp_id)).validate().toString();
            logger.info("\nGenerated Sql Query?= "+ selectiveRecordQuery + "\n");

            resObj = stmtObj.executeQuery(selectiveRecordQuery);
            if(!resObj.next()) {
                logger.info("\n=======No Record Is Present In The '" + TABLE_NAME + "'=======\n");
            } else {
                do {
                    logger.info("\nId?= " + resObj.getString(COLUMN_ONE) + ", Name?= " + resObj.getString(COLUMN_TWO) + ", Salary?= " + resObj.getString(COLUMN_THREE) + "\n");
                } while (resObj.next());
            }
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("\n=======Specific Record Displayed From The '" + TABLE_NAME + "'=======\n");
    }

    // SQLQueryBuilder #5 :: This Method Is Used To Update A Record In The Database
    public static void updateRecord(int update_record_id) {
        String updateRecord, editorName = "Java Code Geek";
        logger.info("\n=======Updating Record In The '" + TABLE_NAME + "'=======\n");
        try {
            updateRecord = new UpdateQuery(table_name).addSetClause(column_2, editorName).addCondition(BinaryCondition.equalTo(column_1, update_record_id)).validate().toString();
            logger.info("\nGenerated Sql Query?= "+ updateRecord + "\n");
            stmtObj.execute(updateRecord);
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("\n=======Record Updated In The '" + TABLE_NAME + "' =======\n");
    }

    // SQLQueryBuilder #6 :: This Method Is Used To Delete A Specific Record From The Table
    public static void deleteSelectiveRecord(int delete_record_id) {
        String deleteSelectiveRecordQuery;
        logger.info("\n=======Deleting Specific Record From The '" + TABLE_NAME + "'=======\n");
        try {
            deleteSelectiveRecordQuery = new DeleteQuery(table_name).addCondition(BinaryCondition.equalTo(column_1, delete_record_id)).validate().toString();
            logger.info("\nGenerated Sql Query?= "+ deleteSelectiveRecordQuery + "\n");
            stmtObj.execute(deleteSelectiveRecordQuery);
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("\n=======Selective Specific Deleted From The '" + TABLE_NAME + "'=======\n");
    }

    // SQLQueryBuilder #7 :: This Method Is Used To Delete All Records From The Table
    public static void deleteRecords() {
        String deleteRecordsQuery;
        logger.info("\n=======Deleting All Records From The '" + TABLE_NAME + "'=======\n");
        try {
            deleteRecordsQuery = new DeleteQuery(table_name).validate().toString();
            logger.info("\nGenerated Sql Query?= "+ deleteRecordsQuery + "\n");
            stmtObj.execute(deleteRecordsQuery);
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("\n=======All Records Deleted From The '" + TABLE_NAME + "'=======\n");
    }

    // SQLQueryBuilder #8 :: This Method Is Used To Drop A Table From The Database
    @SuppressWarnings("static-access")
    public static void dropTableFromDb() {
        String dropTableQuery;
        logger.info("\n=======Dropping '" + TABLE_NAME + "' From The Database=======\n");
        try {
            dropTableQuery = new DropQuery(DropQuery.Type.TABLE, table_name).dropTable(table_name).validate().toString();
            logger.info("\nGenerated Sql Query?= "+ dropTableQuery + "\n");
            stmtObj.execute(dropTableQuery);
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("\n======='" + TABLE_NAME + "' Is Dropped From The Database=======\n");
    }
    8/
     */
}
