package oop.prj.model;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import oop.prj.DB.DBAutoIncrement;
import oop.prj.DB.DBField;
import oop.prj.DB.DBPrimaryKey;

public abstract class RawMessage {

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
    
    @Expose(serialize = false, deserialize = false)
    RawUser owner = null;

    // transient static ArrayList<RawMessage> allMessages = new ArrayList<>();

    protected RawMessage() {
    }

    protected RawMessage(RawUser owner, String context) {
        if (owner == null || context == null || context.replaceAll(" ", "").equals("")) {
            throw new IllegalArgumentException("Bad input for post information!\nPlease try again");
        }
        // ownerId=owner.getID();
        this.owner = owner;
        this.context = context;
        dateTime = LocalDateTime.now();
    }

    public RawUser getOwner() {
        return owner;
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

    public void setId(Integer id){
        this.id = id;
    }
}
