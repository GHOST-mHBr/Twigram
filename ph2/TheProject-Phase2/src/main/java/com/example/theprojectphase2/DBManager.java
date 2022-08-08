package com.example.theprojectphase2;//package oop.prj.model.DB;

import java.sql.Connection;
import java.sql.Statement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {
    private static final String DB_NAME = "TheProjectDataBase";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String DB_UNAME = "root";
    private static final String DB_PASS = "400100898";

    private static final String COL_OBJECTS_NAME = "Objects";
    private static final String COL_ID_NAME = "id";
    private static Connection conn = null;

    private DBManager() {
        // initialize();
    }

    private DBManager(DBManager other) {
    }

    public static void UpdateQuery(String sql) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

    }

    public static  ResultSet getResultSet(String sql) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    private static final String getSaveQuery(String tableName, String json) {
        return "INSERT INTO `" + tableName + "` (" + COL_OBJECTS_NAME + ") VALUES (\'" + json + "\');";
    }

    private static final String getCreateTableQuery(String tableName) {
        return "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + COL_ID_NAME
                + " INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT , "
                + COL_OBJECTS_NAME + " JSON);";
    }

    public static void save(Saveable s) {

        Connection conn = getConnection();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String json = gson.toJson(s);


        try {
            Statement st = conn.createStatement();
            st.executeUpdate(getCreateTableQuery(s.getTableName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement st = conn.createStatement();
            st.executeUpdate(getSaveQuery(s.getTableName(), json));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getMaxId() {
        Connection conn = getConnection();
        int result = 0;
        try {
            ResultSet resp = getResultSet("SELECT * FROM `groups` WHERE id=(SELECT max(id) FROM `groups`);");
            resp.next();

            result = resp.getInt("id");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getLastId(Saveable s) {
        Connection conn = getConnection();
        int result = 0;
        try {
            Statement stm = conn.createStatement();
            ResultSet resp = stm.executeQuery("SELECT max(id) from " + s.getTableName() + ";");
            while (resp.next()) {
                result = resp.getInt("max(id)");
            }
        } catch (SQLException e) {

        }
        return result;
    }

    public static <T extends Saveable> T getObject(String tableName, int id, Class<T> cl) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(getObjectAsMappedJSONString(id, tableName), cl);
    }

    private static String getObjectAsMappedJSONString(int id, String tableName) {
        Connection conn = getConnection();
        String result = null;

        try {
            Statement st = conn.createStatement();
            ResultSet responseSet = st.executeQuery(
                    "SELECT * FROM " + tableName + " WHERE id=" + String.valueOf(id) + ";");
            if (responseSet.next()) {
                result = responseSet.getString(COL_OBJECTS_NAME);
            }
            st.close();
            responseSet.close();
            // conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Connection getConnection() {
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
}
