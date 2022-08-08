package oop.prj.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

import oop.prj.App;
import oop.prj.DB.DBAutoIncrement;
import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBPrimaryKey;
import oop.prj.DB.DBTable;

import static com.diogonunes.jcolor.Ansi.*;
import static com.diogonunes.jcolor.Attribute.*;

@DBTable(tableName = "groups_")
public class Group implements Sendable {
    private String type = "Group";
    @DBField(name = "members")
    ArrayList<Integer> usersIds = new ArrayList<>();
    @DBField(name = "banned_ids")
    ArrayList<Integer> bannedIds = new ArrayList<>();
    @DBField(name = "messages")
    ArrayList<Integer> messages = new ArrayList<>();

    @Expose(serialize = false, deserialize = false)
    static ArrayList<Group> allGroups = new ArrayList<>();

    @DBField(name = "Id")
    @DBPrimaryKey
    @DBAutoIncrement
    Integer id = null;

    @DBField(name = "name")
    String name = null;

    @DBField(name = "admin")
    User admin = null;

    @DBField(name = "groupId")
    String groupId = null;

    public Group() {
    }

    public Group(String name, String groupId, User admin) throws IllegalArgumentException, IllegalAccessException {
        setAdmin(admin);
        setGroupId(groupId, admin);
        setGroupName(name, admin);
        DBManager.insert(this);
        usersIds.add(admin.getID());
        allGroups.add(this);
    }

    public String getGroupId() {
        return groupId;
    }

    public void addUser(User user, User adder) throws IllegalAccessException {
        if (adder == null) {
            throw new NullPointerException("Please login first");
        }
        if (!adder.equals(admin)) {
            throw new IllegalAccessException("You are not admin");
        }
        if (bannedIds.contains(user.getID())) {
            throw new IllegalAccessException(user.getID() + " is banned");
        }
        usersIds.add(user.getID());
    }

    public void addUsers(ArrayList<User> users, User adder) {
        if (adder.equals(admin)) {
            usersIds.addAll(users.stream().map(e -> e.getID()).filter(e -> !bannedIds.contains(e))
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
    }

    public void removeUser(Integer userId) {
        if (usersIds.contains(userId)) {
            usersIds.remove(userId);
        }
    }

    public void removeUser(User user, User remover) throws IllegalAccessException {
        if (remover == null) {
            throw new NullPointerException("Please login first");
        }
        if (!remover.equals(admin)) {
            throw new IllegalAccessException("You are not admin");
        }
        if (usersIds.contains(user.getID())) {
            usersIds.remove(user.getID());
        }
    }

    public void ban(User user, User banner) throws IllegalAccessException {
        if (banner == null) {
            App.prLn("Please login first");
        }
        if (banner.equals(admin)) {
            if (!bannedIds.contains(user.getID()))
                bannedIds.add(user.getID());
        } else {
            throw new IllegalAccessException("You are not admin!");
        }
    }

    public void ban(String idStr, User banner) throws IllegalAccessException {
        try {
            Integer id = User.getUser(idStr).getID();
            if (banner == null) {
                throw new IllegalAccessException("Please login first");
            }
            if (banner.equals(admin)) {
                if (!bannedIds.contains(id))
                    bannedIds.add(id);
            } else {
                throw new IllegalAccessException("You are not admin!");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Bad input for id");
        }

    }

    public void setGroupId(String groupId, User changer) throws IllegalArgumentException, IllegalAccessException {
        if (changer == null) {
            throw new IllegalArgumentException("Please login first");
        }
        if (!changer.equals(admin)) {
            throw new IllegalAccessException("You are not admin");
        }
        if (groupId == null) {
            throw new IllegalArgumentException("Bad group id");
        }
        for (Group g : allGroups) {
            if (g.getGroupId().equals(groupId)) {
                throw new IllegalArgumentException();
            }
        }
        this.groupId = groupId;
    }

    public void setGroupName(String name, User changer) throws IllegalArgumentException, IllegalAccessException {
        if (changer == null) {
            throw new IllegalArgumentException("Please login first");
        }
        if (!changer.equals(admin)) {
            throw new IllegalAccessException("You are not admin");
        }
        if (name == null || name.replaceAll(" +", "").equals("")) {
            throw new IllegalArgumentException("The name is empty");
        }
        this.name = name;
    }

    private void setAdmin(User admin) throws IllegalArgumentException {
        if (admin == null) {
            throw new IllegalArgumentException("Bad admin!\nPlease login");
        }
        this.admin = admin;
    }

    public void printInfo() {
        String res = colorize("       group info       ", BLUE_TEXT(), MAGENTA_BACK(), BOLD());
        res += "\n|name:" + name + "\n";
        res += "|id:" + groupId + "\n";
        res += "|admin:" + admin.getUserName() + "\n";
        if (usersIds.size() > 0)
            res += "\n members:\n";
        int i = 1;
        for (var m : usersIds.stream().map(e -> User.getWithId(e)).collect(Collectors.toList())) {
            res += "  " + colorize("" + i++ + ". ", RED_TEXT()) + m.getUserName() + "\n";
        }
        res += colorize("     end of members     ", WHITE_TEXT(), GREEN_BACK());
        i = 1;
        if (bannedIds.size() > 0) {
            res += "\n Banned users:\n";
            for (var m : bannedIds.stream().map(e -> User.getWithId(e)).collect(Collectors.toList())) {
                res += "  " + colorize("" + i++ + ". ", RED_TEXT()) + m.getUserName() + "\n";
            }
            res += colorize("  end of banned users   ", WHITE_TEXT(), RED_BACK());
        }
        App.prLn(res);
    }

    public List<Message> getAllMessages() {
        return messages.stream().map(e -> Message.getWithId(e)).collect(Collectors.toList());
    }

    public void printChat(User user) {
        if (messages.size() == 0) {
            App.prLn(colorize("No message yet!", ITALIC()));
            return;
        }
        if (user != null) {
            for (var msg : getAllMessages()) {
                msg.seen(user);
            }
        }
        for (int i = 0; i < messages.size() - 1; i++) {
            App.prLn(Message.getWithId(messages.get(i)).toString());
            App.prLn(colorize("              ", GREEN_BACK()));
        }
        App.prLn(Message.getWithId(messages.get(messages.size() - 1)).toString());
    }

    @Override
    public void messageReceived(Message msg) throws IllegalAccessException {
        if (!usersIds.contains(msg.getOwnerId())) {
            throw new IllegalAccessException("You are not a member of this group");
        }
        if (bannedIds.contains(msg.getOwnerId()))
            throw new IllegalAccessException("You are banned!");
        messages.add(msg.getId());
    }

    @Override
    public Integer getReceiverId() {
        return id;
    }

    @Override
    public Class<? extends Sendable> getReceiverClass() {
        return Group.class;
    }

    public static Group getWithId(Integer id) {
        for (var g : allGroups) {
            if (g.id == id) {
                return g;
            }
        }
        throw new NoSuchElementException("No Such a Group exits");
    }

    public static Group getWithId(String id) {
        for (var g : allGroups) {
            try {
                var id_ = Integer.parseInt(id);
                if (g.id == id_) {
                    return g;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("bad id");
            }
        }
        throw new NoSuchElementException("No Such a Group exits");
    }

    public static Group getGroup(String groupId) {
        for (var g : allGroups) {
            if (g.getGroupId().equals(groupId)) {
                return g;
            }
        }
        throw new NoSuchElementException("No such a group exists");
    }

    public static void saveData() {
        for (var g : allGroups) {
            if (DBManager.exists(g)) {
                DBManager.update(g);
            } else {
                DBManager.insert(g);
            }
        }
    }

    public static void loadAllObjects() {
        allGroups = DBManager.getAllObjects(Group.class);
    }

    public void unban(String idStr, User banner) throws IllegalAccessException {

        try {
            Integer id = User.getUser(idStr).getID();
            if (banner == null) {
                App.prLn("Please login first");
                return;
            }
            if (banner.equals(admin)) {
                if (bannedIds.contains(id))
                    bannedIds.remove(id);
                else
                    throw new IllegalAccessException(idStr + " is not banned");
            } else {
                throw new IllegalAccessException("You are not admin!");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Bad input for id");
        }
    }

    @Override
    public void messageRemoved(Message msg) {
        messages.remove(msg.getId());
    }
}
