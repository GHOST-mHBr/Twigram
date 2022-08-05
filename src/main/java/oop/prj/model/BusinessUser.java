package oop.prj.model;

import java.util.ArrayList;

import oop.prj.DB.DBField;
import oop.prj.DB.DBTable;

@DBTable(tableName = "business_user")
@Deprecated
public class BusinessUser extends User {

    @DBField(name = "ads")
    ArrayList<Integer> postsIds = new ArrayList<>();

    ArrayList<AdPost> posts = new ArrayList<>();

    @DBField(name = "watches")

    private static ArrayList<BusinessUser> allBusinessUsers = new ArrayList<>();


    public BusinessUser() {
    }




    @Override
    public Class<? extends Sendable> getReceiverClass(){
        return BusinessUser.class;
    }
}
