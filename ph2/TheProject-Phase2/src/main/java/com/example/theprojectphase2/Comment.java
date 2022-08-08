package com.example.theprojectphase2;

import java.util.ArrayList;

@DBTable(tableName = "comments")
public class Comment extends Post {

    public static ArrayList<Comment> Comments = new ArrayList<>();

    Comment(String context , User owner){
        super("", context , owner);
    }
    Comment(){}





}
