package repository.utilities;

import com.google.common.base.CaseFormat;
import utilities.common.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class JDBCConnector {
    //  public static String

    static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                startConnect();
            } catch (Exception e) {
                Log.in("No connection, Please check the DB connection config!");
                throw new RuntimeException(e);
            }
        }
        return connection;
    }



    public static Connection startConnect() throws Exception {
        String url
                = "jdbc:postgresql://localhost/Kabalah"; // table details
        String username = "postgres"; //
        String password = "12345678";
        Class.forName(
                "org.postgresql.Driver"); // Driver name
        connection = DriverManager.getConnection(
                url, username, password);
        System.out.println(
                "Connection Established successfully");
        connection = JDBCConnector.connection;
        return connection;
    }

    public static void stopConnect() throws Exception {
        if (connection != null)
            connection.close(); // close connection
        System.out.println("Connection Closed....");
    }

    public static final List StringTypes = Arrays.asList(new String[]{"text", "name", "varchar"});

    public static Boolean ifStringType(String type) {
        if (StringTypes.contains(type)) return true;
        else return false;

    }

    public static void loopColumn(ResultSet resultSet) throws SQLException {
        int i = 1;
        ResultSetMetaData resultSetmd = resultSet.getMetaData();

        while (i <= resultSetmd.getColumnCount()) {
            if (ifStringType(resultSetmd.getColumnTypeName(i))) {
                System.out.print(resultSet.getString(i) + " ");
            }
            ++i;
        }
        System.out.println("");
    }

    public static void loopColumnH(ResultSet resultSet) throws SQLException {
        int i = 1;
        ResultSetMetaData resultSetmd = resultSet.getMetaData();
        while (i <= resultSetmd.getColumnCount()) {
            String name = resultSetmd.getColumnName(i);
            System.out.print(name + " ");
        }
        ++i;
        System.out.println("");
    }


    public static List<Column> getColumnNames(ResultSet resultSet) throws SQLException {
        List<Column> columns = new ArrayList<>();
        ResultSetMetaData resultSetmd = resultSet.getMetaData();
        int i = 1;
        while (i <= resultSetmd.getColumnCount()) {
            resultSetmd = resultSet.getMetaData();
            String columnName = resultSetmd.getColumnName(i);
            String columnTypeName = resultSetmd.getColumnTypeName(i);
            Column col = null;
            if(ifStringType(columnTypeName)){
                col = new Column(String.class,columnName);
            }else if(columnTypeName.equalsIgnoreCase("uuid")){
                col = new Column(UUID.class,columnName);
            }else if(columnTypeName.equalsIgnoreCase("bool")){
                col = new Column(Boolean.class,columnName);
            }
            else {
                try {
                    col = new Column(Class.forName(columnTypeName),columnName);
                } catch (ClassNotFoundException e) {
                    Log.in("no class for "+columnTypeName);
                    e.printStackTrace();
                }
            }

            columns.add(col);
            ++i;
        }
        return columns;
    }


    public static List<Column> ObjectToColumns(Object object) {
        Method[] methods = object.getClass().getMethods();
        List<Column> cols = new ArrayList<>();

        for (Method method : methods) {
            if (method.getName().contains("get")
                    && !method.getName().equalsIgnoreCase("getClass")) {
                Column column = new Column();
                Object value = null;
                try {
                    value = method.invoke(object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                String columnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName().replaceAll("get", ""));
                column.setColumnName(columnName);
                column.setValue(value);
                if (method.getName().toLowerCase().contains("id")) {
                    column.setColumnDataType(UUID.class);
                } else {
                    column.setColumnDataType(method.getReturnType());
                }
                cols.add(column);
            }
        }
        return cols;
    }


    public static Object setObject(Object newObj, Class columnDataType, Class classType, String columnName, Object value) {

        try {
            //getDeclaredMethod cannot get parent method
            Method setMethod = classType.getMethod(
                    "set" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, columnName), columnDataType);
            setMethod.invoke(newObj, value);
        } catch (NoSuchMethodException e) {
            Log.in("no set method for" + classType);
            Log.in(e.toString());
        } catch (InvocationTargetException e) {
            Log.in("InvocationTargetException:" + classType);
            Log.in(e.toString());
        } catch (IllegalAccessException e) {
            Log.in("more than one argument for setMethod:" + classType);
            Log.in(e.toString());
        }
        return newObj;
    }

    public static Object getObjectByClass(Class classType) {
        try {
            return classType.newInstance();
        } catch (InstantiationException e) {
            Log.in("cannot create object for type:" + classType);
            Log.in(e.toString());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static List<Object> getObjects(ResultSet resultSet, Class classType) {


        List<Object> newObjs = new ArrayList<>();

        try {
            List<Column> columns = getColumnNames(resultSet);
//            resultSet.first();
            while (resultSet.next()) {
                int i = 1;
                Object newObj = getObjectByClass(classType);
                for (Column column : columns) {
                    if (column.getColumnDataType() == String.class) {
                        String result = resultSet.getString(i);
                        setObject(newObj,
                                column.getColumnDataType(), classType, column.getColumnName(), result);
                    }
                    else if(column.getColumnDataType() == UUID.class){
                        Object uuid = resultSet.getObject(i);
                        setObject(newObj,
                                column.getColumnDataType(), classType, column.getColumnName(),uuid instanceof String? UUID.fromString((String) uuid):uuid);
                    }else if(column.getColumnDataType() == Boolean.class){
                        Boolean result = resultSet.getBoolean(i);
                        setObject(newObj,
                                column.getColumnDataType(), classType, column.getColumnName(), result);
                    }

                    ++i;
                }
                if (newObj != null) {
                    newObjs.add(newObj);
                }
            }

        } catch (SQLException e) {
            Log.in("no meta data for result set");
            Log.in(e.toString());
//            throw new RuntimeException(e);
        }
        return newObjs;
    }

    public static void printResultSet(ResultSet resultSet) {
        try {
//            loopColumnH(resultSet);
            while (resultSet.next()) {
                loopColumn(resultSet);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }

    public static Object execute(PreparedStatement prep) {
        try {
            if (prep.toString().contains("SELECT")) {
               return prep.executeQuery();
            } else {
                return prep.executeUpdate();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public static boolean setParameterByColumn(Column column,int i, PreparedStatement pstmt){
        try {

            if(column.getValue() == null && (!pstmt.toString().contains("SELECT"))){
                pstmt.setNull(i,Types.NULL);
            }
            else if (column.getColumnDataType().equals(String.class)) {
                if(column.getValue()!=null){
                  pstmt.setString(i, (String) column.getValue());
                }
            } else if (column.getColumnDataType().equals(UUID.class)) {
                UUID uuid =  (UUID) column.getValue();
                if(column.getValue()!=null){
                    pstmt.setObject(i, uuid);
                }else{
                    return false;
                }
            } else if (column.getColumnDataType().equals(Integer.class)) {
                if(column.getValue()!=null){
                    pstmt.setInt(i, ((Integer) column.getValue()));
                }

            } else if (column.getColumnDataType().equals(BigDecimal.class)) {
                if(column.getValue()!=null){
                    pstmt.setBigDecimal(i, ((BigDecimal) column.getValue()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static PreparedStatement getStatementByQuery(String query){
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static int prepareQuery( List<Column> columns,PreparedStatement pstmt) {

        return prepareQuery(1,columns,pstmt);
    }
    public static int prepareQuery( int i, List<Column> columns,PreparedStatement pstmt) {

        for (Column column : columns) {
            if(column.getValue()==null){
                setParameterByColumn(column,i,pstmt);
            }else{
                setParameterByColumn(column,i++,pstmt);
            }

        }
        return i;
    }


    public static int prepareQuery(Column column,PreparedStatement pstmt) {
        List<Column> columns = new ArrayList<>();
        columns.add(column);
        return prepareQuery(columns,pstmt);
    }
}