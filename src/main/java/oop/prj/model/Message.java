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
public class Message extends RawMessage {

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

    public Message(String context, User owner, Sendable receiver) {
        super(owner, context);
        if (receiver == null) {
            throw new IllegalArgumentException("The receiver is null");
        }
        id = DBManager.getLastId(Message.class) + 1;
        this.receiver = receiver;
        allMessages.add(this);
    }

    public Message(String context, User owner, Sendable receiver, MessageType type, Integer repForId) {
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

        id = DBManager.getLastId(Message.class) + 1;
        this.repForId = repForId;
        this.receiver = receiver;
        this.messageType = type;
        allMessages.add(this);
    }

    public TreeSet<Integer> getAllWatchersIds() {
        return watchersIds;
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
        switch (messageType) {
            case NORMAL:
                break;
            case FROWARD:
                return forwardToString();
            case REPLY:
                return replyToString();
        }
        var formatter = DateTimeFormatter.ofPattern("hh:mm a\nyyyy LLL dd");
        String res = "";
        res += colorize(getOwner().getUserName() + ":", BLUE_TEXT()) + "\n";
        res += context;
        res += "\n";
        res += "\n" + colorize(dateTime.format(formatter), MAGENTA_TEXT());
        res += "\n" + colorize("id:" + id, YELLOW_TEXT());
        res += "\n";

        return res;
    }

    private String replyToString() {
        var formatter = DateTimeFormatter.ofPattern("hh:mm a\nyyyy LLL dd");
        Message replied = Message.getWithId(repForId);
        String context_ = replied.context;
        String res = "";
        res += colorize(getOwner().getUserName() + ":", BLUE_TEXT()) + "\n";
        res += colorize("reply to: ",
                BOLD(), ITALIC(),
                BLUE_TEXT(), WHITE_BACK());
        res += "\n";
        res += "    " + (context_.length() > 5 ? String.format("%.5s...", context_) : context_);
        res += "\n";
        res += context;
        res += "\n";
        res += "\n" + colorize(dateTime.format(formatter), MAGENTA_TEXT());
        res += "\n" + colorize("id:" + id, YELLOW_TEXT());
        res += "\n";

        return res;
    }

    private String forwardToString() {
        var formatter = DateTimeFormatter.ofPattern("hh:mm a\nyyyy LLL dd");
        String res = colorize("Forwarded from" + Message.getWithId(repForId).getOwner().getUserName(), BOLD(), ITALIC(),
                BLUE_TEXT(), WHITE_BACK());

        res += colorize(getOwner().getUserName() + ":", BLUE_TEXT()) + "\n";
        res += context;
        res += "\n";
        res += "\n" + colorize(dateTime.format(formatter), MAGENTA_TEXT());
        res += "\n" + colorize("id:" + id, YELLOW_TEXT());
        res += "\n";

        return res;
    }

    public static void fetchData() {
        if (!fetched) {
            allMessages = DBManager.getAllObjects(Message.class);

            fetched = true;
        }
    }

    public static void saveData() {
        for (Message msg : allMessages) {
            // msg.ownerId = msg.owner.getID();
            try {
                if (DBManager.exists(msg)) {
                    DBManager.update(msg);
                } else {
                    DBManager.insert(msg);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
        allMessages.remove(getWithId(id));
    }

}