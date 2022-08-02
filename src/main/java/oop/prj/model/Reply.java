package oop.prj.model;

import oop.prj.DB.DBField;

public class Reply extends Message {

    @DBField(name = "replied_message")
    Message repliedMsg = null;

    public Reply(Message repliedMsg, String context, BusinessUser owner, BusinessUser receiver) {
        super(context, owner, receiver);
        this.repliedMsg = repliedMsg;
    }

    public Message getRepliedMsg(){
        return repliedMsg;
    }

}
