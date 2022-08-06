package oop.prj.model;

import java.util.ArrayList;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

@DBTable(tableName = "replies")
public class Reply extends Message {

    @DBField(name = "replied_message")
    Message repliedMsg = null;

    private static ArrayList<Reply> allReplies = new ArrayList<>();

    public Reply(Message repliedMsg, String context, User owner, Sendable receiver) {
        super(context, owner, receiver);
        this.repliedMsg = repliedMsg;
    }

    public Message getRepliedMsg(){
        return repliedMsg;
    }

    public static void saveData(){

    }

    public static void loadAllObjects(){
        allReplies = DBManager.getAllObjects(Reply.class);
    }

}
