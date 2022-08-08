package oop.prj.model;

import java.time.LocalDateTime;

import oop.prj.DB.DBAutoIncrement;
import oop.prj.DB.DBField;
import oop.prj.DB.DBPrimaryKey;

public abstract class RawMessage{

    @DBPrimaryKey
    @DBAutoIncrement
    @DBField(name = "Id")
    protected Integer id = 0;

    @DBField(name = "context")
    String context = null;

    @DBField(name = "date_time")
    LocalDateTime dateTime = null;

    @DBField(name = "owner_id")
    Integer ownerId = null;

    protected RawMessage() {
    }

    protected RawMessage(User owner, String context) {
        if (owner == null || context == null || context.replaceAll(" ", "").equals("")) {
            throw new IllegalArgumentException("Bad input for post information!\nPlease try again");
        }
        // id = DBManager.getLastId(Message.class) + 1;
        ownerId = owner.getID();
        this.context = context;
        dateTime = LocalDateTime.now();
    }

    public User getOwner() {
        return User.getWithId(ownerId);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getContext() {
        return context;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setContext(String newC){
        context = newC;
    }
}
