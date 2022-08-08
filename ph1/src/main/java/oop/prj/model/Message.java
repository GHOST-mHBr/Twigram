package oop.prj.model;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.BLUE_TEXT;
import static com.diogonunes.jcolor.Attribute.MAGENTA_TEXT;
import static com.diogonunes.jcolor.Attribute.YELLOW_TEXT;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

import static com.diogonunes.jcolor.Attribute.*;

@DBTable(tableName = "messages")
public class Message extends RawMessage implements Comparable<Message> {

    public enum MessageType {
        NORMAL, REPLY, FROWARD
    }

    @DBField(name = "receiver")
    private Sendable receiver = null;

    @DBField(name = "watches")
    private TreeSet<Integer> watchersIds = new TreeSet<>();

    @DBField(name = "message_type")
    private MessageType messageType = MessageType.NORMAL;

    @DBField(name = "rep_for_id")
    private Integer repForId = -1;

    transient private static ArrayList<Message> allMessages = new ArrayList<>();

    private static boolean fetched = false;

    public Message() {
    }

    public Message(String context, User owner, Sendable receiver)
            throws IllegalArgumentException, IllegalAccessException {
        super(owner, context);
        if (receiver == null) {
            throw new IllegalArgumentException("The receiver is null");
        }
        this.receiver = receiver;
        DBManager.insert(this);
        try {
            receiver.messageReceived(this);
        } catch (IllegalAccessException e) {
            DBManager.deleteRecordIfExist(this);
            receiver.messageRemoved(this);
            throw e;
        }
        owner.getSentMessagesIds().add(id);
        allMessages.add(this);
    }

    public Message(String context, User owner, Sendable receiver, MessageType type, Integer repForId)
            throws IllegalAccessException {
        super(owner, context);
        if (receiver == null) {
            throw new IllegalArgumentException("The receiver is null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The invalid message type");
        }
        if (repForId == null) {
            throw new IllegalArgumentException("bad reply or forward id");
        }
        this.receiver = receiver;
        this.repForId = repForId;
        this.messageType = type;
        DBManager.insert(this);
        try {
            receiver.messageReceived(this);
        } catch (IllegalAccessException e) {
            DBManager.deleteRecordIfExist(this);
            receiver.messageRemoved(this);
            throw e;
        }
        owner.getSentMessagesIds().add(id);
        allMessages.add(this);
    }

    public TreeSet<Integer> getAllWatchersIds() {
        return watchersIds;
    }

    public MessageType getType(){
        return messageType;
    }

    public boolean hasSeen(User user) {
        return watchersIds.contains(user.getID());
    }

    public void seen(User user) {
        watchersIds.add(user.getID());
    }

    public Sendable getReceiver() {
        return receiver;
    }

    public static Message getWithId(Integer id) {
        for (Message msg : allMessages) {
            if (msg.getId().equals(id)) {
                return msg;
            }
        }
        throw new NoSuchElementException("No such a message exists");
    }

    public static boolean messageExists(Integer id){
        try{
            Message msg = getWithId(id);
            return true;
        }catch (NoSuchElementException e){
            return false;
        }
    }

    public static Message getWithId(String id) {
        try {
            Integer id_ = Integer.parseInt(id);
            return getWithId(id_);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("No such a message exists");
        }
    }

    @Override
    public String toString() {
        String res = "";
        var formatter = DateTimeFormatter.ofPattern("hh:mm a\nyyyy LLL dd");
        res += colorize("-----------", CYAN_TEXT()) + "\n";

        switch (messageType) {
            case NORMAL:
                break;
            case FROWARD:
                res = colorize("Forwarded from " + Message.getWithId(repForId).getOwner().getUserName(), BOLD(),
                        ITALIC(),
                        BLUE_TEXT(), WHITE_BACK()) + "\n";
                break;
            case REPLY:
                String context_=colorize("Deleted message",BLUE_TEXT(),ITALIC());
                if(messageExists(repForId)){
                    context_ = Message.getWithId(repForId).getContext();
                }
                res += colorize(getOwner().getUserName() + ":", BLUE_TEXT()) + "\n";
                res += colorize("reply to: ",
                        BOLD(), ITALIC(),
                        BLUE_TEXT(), WHITE_BACK());
                res += "\n";
                res += "    " + String.format("%.25s...", context_);
                res += "\n";
                break;
        }
        res += colorize(getOwner().getUserName() + ":", BLUE_TEXT()) + "\n";
        res += context;
        res += "\n" + colorize(dateTime.format(formatter), MAGENTA_TEXT());
        res += "\n" + colorize("id:" + id, YELLOW_TEXT());
        res += "\n" + colorize("-----------", CYAN_TEXT());
        return res;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Message) {
            Message msg = (Message) other;
            return id == msg.getId();
        }
        return false;
    }

    public static void deleteMessage(Message msg) {
        allMessages.remove(msg);
    }

    public static void fetchData() {
        if (!fetched) {
            allMessages = DBManager.getAllObjects(Message.class);

            fetched = true;
        }
    }

    public static void saveData() {
        for (Message msg : allMessages) {
            DBManager.update(msg);
        }

    }

    public static void loadAllObjects() {
        if (allMessages.size() == 0) {
            allMessages.addAll(DBManager.getAllObjects(Message.class));
        }
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public static void removeMessage(Integer id) {
        allMessages.remove(getWithId(id));
    }

    public static void removeMessage(String id) {
        Message msg = getWithId(id);
        try {
            DBManager.deleteRecordIfExist(msg);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        msg.getOwner().getSentMessagesIds().remove(msg.getId());
        msg.getReceiver().messageRemoved(msg);
        allMessages.remove(getWithId(id));
    }

    public void reply(String replyContext, User replier) throws IllegalAccessException {
        new Message(replyContext, replier, receiver, MessageType.REPLY, id);
    }

    public void forward(Sendable receiver, User forwarder) throws IllegalAccessException {
        new Message(context, forwarder, receiver, MessageType.FROWARD, id);
    }

    @Override
    public int compareTo(Message o) {
        return dateTime.compareTo(o.getDateTime());
    }

}