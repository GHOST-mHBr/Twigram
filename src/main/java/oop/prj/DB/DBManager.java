package oop.prj.DB;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.Message;



import oop.prj.model.Sendable;

public class DBManager {

    private static final String DB_NAME = "oop_test_db";
    private static final String DB_URL = "jdbc:mysql://localhost/" + DB_NAME;
    private static final String DB_UNAME = "root";
    private static final String DB_PASS = "12021202";

    private static Connection conn = null;
    private static Gson gson = null;
    private static GsonBuilder builder = null;

    static {
        builder = new GsonBuilder();
        builder.registerTypeAdapter(org.joda.time.LocalDateTime.class, JodaLocalDateTimeAdapter.getInstance());
        builder.registerTypeAdapter(LocalDateTime.class, LocalDateTimeAdapter.getInstance());
        builder.registerTypeAdapter(Sendable.class, SendableAdapter.getInstance());
        builder.registerTypeAdapter(Message.class, MessageAdapter.getInstance());
        builder.registerTypeHierarchyAdapter(Sendable.class, SendableAdapter.getInstance());
        gson = builder.create();
    }

    private static int getMaxId(String columnName, String tableName) throws SQLException {
        getConnection();
        Statement st = conn.createStatement();
        ResultSet res = st
                .executeQuery("SELECT max(" + columnName + ")" + " FROM "
                        + tableName + ";");
        int index = 0;
        while (res.next()) {
            index = res.getInt("max(" + columnName + ")");
        }
        return index;
    }

    public static void insert(Object object) {
        createTableIfNotExist(object.getClass());
        if (object.getClass().isAnnotationPresent(DBTable.class)) {
            String tableName = "";
            for (Annotation anno : object.getClass().getAnnotations()) {
                if (anno.annotationType().equals(DBTable.class)) {
                    DBTable table = (DBTable) anno;
                    tableName = table.tableName();
                    break;
                }
            }
            String query = "INSERT INTO " + tableName + " ";
            String sub_query_record_fields = "";
            String sub_query_record_values = "";

            for (Field f : getAllFields(object.getClass())) {
                f.setAccessible(true);
                if (f.isAnnotationPresent(DBField.class)) {
                    DBField anno = f.getAnnotation(DBField.class);
                    sub_query_record_fields += anno.name() + ",";
                    if (f.isAnnotationPresent(DBAutoIncrement.class)) {
                        try {
                            f.set(object, getMaxId(f.getAnnotation(DBField.class).name(), tableName) + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Object r = f.get(object);

                        if (needJson(r)) {
                            // String str = gson.toJson(f.getType().cast(r));
                            sub_query_record_values += "\'" + gson.toJson(f.getType().cast(r)) + "\'";
                        } else {
                            sub_query_record_values += "\'" + r + "\'";
                        }
                        sub_query_record_values += ",";

                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            sub_query_record_fields = sub_query_record_fields.replaceAll(",$", "");
            sub_query_record_fields = "(" + sub_query_record_fields + ")";
            sub_query_record_values = sub_query_record_values.replaceAll(",$", "");
            sub_query_record_values = "(" + sub_query_record_values + ")";
            query += sub_query_record_fields + " VALUES " + sub_query_record_values + ";";
            createTableIfNotExist(object.getClass());
            doUpdateQuery(query);

        } else {
            throw new IllegalArgumentException(
                    "The class " + object.getClass().getSimpleName()
                            + " most annotate DBTable and marks its fields with DBField Annotation\nFATAL ERROR\nexit program...\n");
        }
    }

    public static boolean exists(Object obj) {
        try {
            if (obj.getClass().isAnnotationPresent(DBTable.class)) {
                String tableName = obj.getClass().getAnnotation(DBTable.class).tableName();
                for (Field f : getAllFields(obj.getClass())) {
                    f.setAccessible(true);
                    if (f.isAnnotationPresent(DBPrimaryKey.class)) {
                        Integer value = (Integer) f.get(obj);
                        var colName = f.getAnnotation(DBField.class).name();
                        String query = "SELECT 1 FROM " + tableName + " WHERE " + colName + "=" + value;
                        getConnection();
                        Statement st = conn.createStatement();
                        var result = st.executeQuery(query);
                        if (result.next()) {
                            return true;
                        }
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static int doUpdateQuery(String query) {
        getConnection();
        try {
            Statement st = conn.createStatement();
            return st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static <E> ArrayList<E> doSelectQuery(String query, Class<E> sample)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        getConnection();
        ArrayList<E> finalResult = new ArrayList<>();
        try {
            Statement st = conn.createStatement();
            ResultSet result = st.executeQuery(query);
            while (result.next()) {
                E e = sample.getDeclaredConstructor().newInstance();
                for (Field field : getAllFields(sample)) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(DBField.class)) {
                        DBField mf = field.getAnnotation(DBField.class);
                        try {
                            Object fieldValue = result.getObject(mf.name());
                            switch (field.getType().getSimpleName()) {
                                case "Integer":
                                    field.set(e, (Integer) fieldValue);
                                    break;
                                case "Double":
                                    field.set(e, (Double) fieldValue);
                                    break;
                                case "Float":
                                    field.set(e, (Float) fieldValue);
                                    break;
                                case "String":
                                    field.set(e, (String) fieldValue);
                                    break;
                                case "ArrayList": {
                                    Class<?> type = (Class<?>) ((ParameterizedType) field.getGenericType())
                                            .getActualTypeArguments()[0];
                                    Type t = TypeToken.getParameterized(ArrayList.class, type)
                                            .getType();
                                    Object res = gson.fromJson((String) fieldValue, t);
                                    res = field.getType().cast(res);
                                    field.set(e, res);
                                    break;
                                }
                                case "TreeSet": {
                                    Class<?> type = (Class<?>) ((ParameterizedType) field.getGenericType())
                                            .getActualTypeArguments()[0];
                                    Type t = TypeToken.getParameterized(TreeSet.class, type)
                                            .getType();
                                    Object res = gson.fromJson((String) fieldValue, t);
                                    res = field.getType().cast(res);
                                    field.set(e, res);
                                    break;
                                }
                                case "HashSet": {
                                    Class<?> type = (Class<?>) ((ParameterizedType) field.getGenericType())
                                            .getActualTypeArguments()[0];
                                    Type t = TypeToken.getParameterized(HashSet.class, type)
                                            .getType();
                                    Object res = gson.fromJson((String) fieldValue, t);
                                    res = field.getType().cast(res);
                                    field.set(e, res);
                                    break;
                                }
                                default: {
                                    field.set(e, gson.fromJson((String) fieldValue, field.getType()));
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                finalResult.add(e);
            }
            return finalResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean needJson(Object obj) {
        return !(obj instanceof Integer || obj instanceof Double || obj instanceof Float || obj instanceof String);
    }

    private static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(DB_URL, DB_UNAME, DB_PASS);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("\n##FATAL ERROR:\nerror with database!\nclosing program...\n");
                System.exit(-1);
            }
        }
        return conn;
    }

    public static void createTableIfNotExist(Class<?> class_) {
        if (class_.isAnnotationPresent(DBTable.class)) {
            String tableName = "";
            String fieldsNames = "";
            for (Annotation anno : class_.getAnnotations()) {
                if (anno.annotationType().equals(DBTable.class)) {
                    DBTable annoTable = (DBTable) anno;
                    tableName = annoTable.tableName();
                }
            }
            for (Field f : getAllFields(class_)) {
                if (f.isAnnotationPresent(DBField.class)) {
                    DBField DBfield = f.getAnnotation(DBField.class);
                    fieldsNames += DBfield.name() + " ";
                    switch (f.getType().getSimpleName()) {
                        case "Double", "Float":
                            fieldsNames += "DOUBLE";
                            break;
                        case "Integer":
                            fieldsNames += "INT";
                            break;
                        case "String":
                            fieldsNames += "LONGTEXT";
                            break;
                        default:
                            fieldsNames += "JSON";
                            break;
                    }
                    if (f.isAnnotationPresent(DBAutoIncrement.class)) {
                        fieldsNames += " AUTO_INCREMENT";
                    }
                    if (f.isAnnotationPresent(DBPrimaryKey.class)) {
                        fieldsNames += " PRIMARY KEY";
                    }
                    fieldsNames += ",";
                }
            }
            fieldsNames = fieldsNames.replaceAll(",$", "");
            fieldsNames = "(" + fieldsNames + ")";
            String finalQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " " + fieldsNames + ";";
            doUpdateQuery(finalQuery);
        }
    }

    public static void update(Object obj) {
        if (obj.getClass().isAnnotationPresent(DBTable.class)) {
            DBTable tableAnno = obj.getClass().getAnnotation(DBTable.class);
            String query = "UPDATE " + tableAnno.tableName() + " SET ";
            String primaryKeyColumnName = "";
            Integer primaryKeyColumnValue = 0;
            try {
                for (Field f : getAllFields(obj.getClass())) {
                    f.setAccessible(true);
                    if (f.isAnnotationPresent(DBField.class)) {
                        if (f.isAnnotationPresent(DBPrimaryKey.class)) {
                            primaryKeyColumnName = f.getAnnotation(DBField.class).name();
                            primaryKeyColumnValue = (Integer) f.get(obj);
                        }
                        query += f.getAnnotation(DBField.class).name();
                        query += "=";
                        Object val = f.get(obj);
                        switch (val.getClass().getSimpleName()) {
                            case "Integer":
                            case "String":
                            case "Double":
                            case "Float":
                                query += "\'" + val + "\'";
                                break;
                            default:
                                query += "\'" + gson.toJson(f.get(obj)) + "\'";
                                break;
                        }
                        query += ",";
                    }
                }
                query = query.replaceAll(",$", "");
                query += " WHERE " + primaryKeyColumnName + "=" + primaryKeyColumnValue + ";";
                doUpdateQuery(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<Field> getAllFields(Class<?> inputClass) {
        ArrayList<Field> result = new ArrayList<>();

        result.addAll(Arrays.asList(inputClass.getDeclaredFields()));
        Class<?> temp = inputClass.getSuperclass();

        while (temp != null) {
            result.addAll(Arrays.asList(temp.getDeclaredFields()));
            temp = temp.getSuperclass();
        }

        return result;
    }

    public static int deleteRecordIfExist(Object obj) throws IllegalArgumentException, IllegalAccessException {
        if (obj.getClass().isAnnotationPresent(DBTable.class)) {
            String tableName = obj.getClass().getAnnotation(DBTable.class).tableName();
            String query = "DELETE FROM " + tableName + " WHERE ";
            for (Field f : getAllFields(obj.getClass())) {
                f.setAccessible(true);
                if (f.isAnnotationPresent(DBPrimaryKey.class)) {
                    query += f.getAnnotation(DBField.class).name();
                    query += "=";
                    query += f.get(obj);
                    break;
                }
            }
            if (!query.contains("=")) {
                throw new InputMismatchException("the class has no primary key!");
            }
            query += ";";
            return (doUpdateQuery(query));
        } else {
            throw new InputMismatchException("the class does not Annotate DBTable!");
        }
    }

    public static int getLastId(Class<?> inputClass) {
        if (inputClass.isAnnotationPresent(DBTable.class)) {
            int result = 0;
            int primaryKeys = 0;
            Field founded = null;
            String tableName = inputClass.getAnnotation(DBTable.class).tableName();
            for (Field f : getAllFields(inputClass)) {
                if (f.isAnnotationPresent(DBPrimaryKey.class)) {
                    primaryKeys += 1;
                    if (primaryKeys > 1) {
                        throw new InputMismatchException("The class has more than one primary key");
                    }
                    founded = f;
                }
            }
            if (primaryKeys == 0)
                throw new InputMismatchException("The class has not any primary key");
            try {
                result = getMaxId(founded.getAnnotation(DBField.class).name(), tableName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            throw new InputMismatchException("The class does not annotate DBTable");
        }
    }

    public static <E> ArrayList<E> getAllObjects(Class<E> class_) {
        if (class_.isAnnotationPresent(DBTable.class)) {
            try {
                return doSelectQuery("SELECT * FROM " + class_.getAnnotation(DBTable.class).tableName(), class_);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        } else {
            throw new InputMismatchException("The class does not annotate DBTable");
        }
        return null;
    }

}