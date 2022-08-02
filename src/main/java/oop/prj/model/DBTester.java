package oop.prj.model;

import java.time.LocalDateTime;
import java.util.ArrayList;


import oop.prj.DB.DBAutoIncrement;
import oop.prj.DB.DBField;
import oop.prj.DB.DBPrimaryKey;
import oop.prj.DB.DBTable;

// @Deprecated
@DBTable(tableName = "tester")
public class DBTester {

    @DBPrimaryKey
    @DBAutoIncrement
    @DBField(name = "Id")
    private Integer id = 0;

    @DBField(name = "Name")
    private String name = "";

    @DBField(name = "Age")
    private Integer age = 10;

    @DBField(name = "Followers")
    private ArrayList<BusinessUser> followers = new ArrayList<>();

    @DBField(name = "date")
    private LocalDateTime time = null;

    @DBField(name = "date1")
    private java.time.LocalDateTime time1 = null;

    public DBTester(String name, Integer age) {
        this.name = name;
        this.age = age;
        time = LocalDateTime.now();
        // time1 = java.time.LocalDateTime.now();
    }

    public DBTester() {
        time = LocalDateTime.now();
        time1 = java.time.LocalDateTime.now();
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

}
