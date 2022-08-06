package oop.prj.model;

import java.util.ArrayList;
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

    public Group(String name, String groupId, User admin) throws IllegalArgumentException {
        setGroupId(groupId);
        setGroupName(name);
        setAdmin(admin);
        id = DBManager.getLastId(Group.class) + 1;
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

    public void removeUser(User user) {
        if (usersIds.contains(user.getID())) {
            usersIds.remove(user.getID());
        }
    }

    public void ban(User user, User banner) {
        if (banner.equals(admin)) {
            bannedIds.add(user.getID());
        }
    }

    public void setGroupId(String groupId) throws IllegalArgumentException {
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

    public void setGroupName(String name) throws IllegalArgumentException {
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
        res += colorize("     end of members     ", WHITE_TEXT(), MAGENTA_BACK());
        App.prLn(res);
    }

    public void printChat() {
        for (int i = messages.size() - 1; i > 0; i--) {
            var msg = Message.getWithId(messages.get(i));
            App.prLn(msg.toString());
            App.prLn(colorize("              ", GREEN_BACK()));
        }
        App.prLn(Message.getWithId(messages.get(0)).toString());
    }

    @Override
    public void messageReceived(Message msg) {
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
}
