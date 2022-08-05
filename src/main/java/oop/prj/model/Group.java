package oop.prj.model;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import oop.prj.DB.DBAutoIncrement;
import oop.prj.DB.DBField;
import oop.prj.DB.DBPrimaryKey;
import oop.prj.DB.DBTable;

@DBTable(tableName = "groups")
public class Group implements Sendable {
    private String type = "Group";
    ArrayList<User> ordinaryUsers = new ArrayList<>();
    ArrayList<Message> allMessages = new ArrayList<>();
    ArrayList<User> bannedUsers = new ArrayList<>();

    static ArrayList<Group> allGroups = new ArrayList<>();

    @DBField(name = "Id")
    @DBPrimaryKey
    @DBAutoIncrement
    Integer id = null;

    @DBField(name = "name")
    String name = null;

    @DBField(name = "admin")
    User admin = null;

    public Group(String name, String groupId, User admin) throws IllegalArgumentException {

        for (Group g : allGroups) {
            if (g.getGroupId().equals(groupId)) {
                throw new IllegalArgumentException();
            }
        }
        this.name = name;
        this.admin = admin;
        allGroups.add(this);
    }

    public Integer getGroupId() {
        return id;
    }

    public void addUser(User user, User adder) {
        if (adder.equals(admin) && !bannedUsers.contains(user))
            ordinaryUsers.add(user);
    }

    public void addUsers(ArrayList<User> users, User adder) {
        if (adder.equals(admin)) {
            ordinaryUsers.addAll(users.stream().filter(e -> !bannedUsers.contains(e)).collect(Collectors.toList()));
        }
    }

    public void ban(User user, User banner) {
        if (banner.equals(admin)) {
            bannedUsers.add(user);
        }
    }

    @Override
    public void messageReceived(Message msg) {
        allMessages.add(msg);
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
            if (g.getGroupId() == id) {
                return g;
            }
        }
        throw new NoSuchElementException("No Such a Group exits");
    }
}
