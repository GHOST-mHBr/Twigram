package oop.prj.model;

import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

@DBTable(tableName = "messages")
public class Message extends RawMessage {

    @DBField(name = "receiver")
    private Sendable receiver = null;

    transient private static ArrayList<Message> allMessages = new ArrayList<>();

    private static boolean fetched = false;

    public Message() {
    }

    public Message(String context, RawUser owner, Sendable receiver) {
        super(owner, context);
        if (receiver == null) {
            throw new IllegalArgumentException("The receiver is null");
        }
        this.receiver = receiver;
        allMessages.add(this);
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

    @Override
    public String toString() {
        String res = "\n--------\n";
        res += dateTime.format(DateTimeFormatter.ISO_DATE) + "\n";
        res += dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
        res += "\nfrom ";
        res += owner.getUserName();
        res += " to ";
        if (receiver instanceof Group) {
            Group grR = (Group) receiver;
            res += "group: " + grR.getGroupId();
        } else if (receiver instanceof RawUser) {
            res += "You";
        }
        res += "\n--------\n";
        res += context;
        res += "\n--------\n";

        return res;
    }

    public static void fetchData() {
        allMessages.forEach(e -> {
            RawUser user = RawUser.getWithId(e.ownerId);
            if (user == null) {
                throw new IllegalStateException();
            }
            e.owner = user;
        });
        
        if (!fetched) {
            try {
                allMessages = DBManager.getAllObjects(Message.class);
                fetched = true;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void saveData() {
        for (Message msg : allMessages) {
            msg.ownerId = msg.owner.getID();
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

}