package oop.prj.model;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.google.gson.annotations.Expose;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

@DBTable(tableName = "comments")
public class Comment extends Post {

    @DBField(name = "commented_post")
    Integer commentedPostId = null;

    @Expose(serialize = false, deserialize = false)
    transient static ArrayList<Comment> allComments = new ArrayList<>();

    public Comment() {
        super(null, null);
        id = DBManager.getLastId(Comment.class);
    }

    public Comment(String context, User owner, Post post) {
        super(context, owner);
        commentedPostId = post.getId();
        id = DBManager.getLastId(Comment.class);
        allComments.add(this);
    }

    public static Comment getWithId(Integer id){
        for(var c : allComments){
            if(c.getId().equals(id)){
                return c;
            }
        }
        return null;
    }

    public static Comment getWithId(String id){
        try{
            Integer id_ = Integer.parseInt(id);
            return getWithId(id_);
        }catch (NumberFormatException e){
            throw new NoSuchElementException("bad id for comment!");
        }
    }

    public static void fetchData() {
        if (allComments.size() == 0) {
            allComments = DBManager.getAllObjects(Comment.class);
        }
    }

    public static void saveData() {
        for (var c : allComments) {
            if (DBManager.exists(c)) {
                DBManager.update(c);
            } else {
                DBManager.insert(c);
            }
        }
    }

}
