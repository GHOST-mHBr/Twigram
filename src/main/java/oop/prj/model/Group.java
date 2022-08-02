package oop.prj.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

import oop.prj.DB.DBAutoIncrement;
import oop.prj.DB.DBField;
import oop.prj.DB.DBPrimaryKey;
import oop.prj.DB.DBTable;

@DBTable(tableName = "groups")
public class Group implements Sendable {
    private String type = "Group";
    ArrayList<RawUser> ordinaryUsers = new ArrayList<>();
    ArrayList<Message> allMessages = new ArrayList<>();
    ArrayList<RawUser> bannedUsers = new ArrayList<>();

    static ArrayList<Group> allGroups = new ArrayList<>();

    @DBField(name = "Id")
    @DBPrimaryKey
    @DBAutoIncrement
    Integer id = null;

    @DBField(name = "groupId")
    String groupId;

    @DBField(name = "name")
    String name = null;

    @DBField(name = "admin")
    RawUser admin = null;

    public Group(String name, String groupId, RawUser admin) throws IllegalArgumentException {

        for (Group g : allGroups) {
            if (g.getGroupId().equals(groupId)) {
                throw new IllegalArgumentException();
            }
        }

        this.name = name;
        this.admin = admin;
        this.groupId = groupId;
        allGroups.add(this);
    }

    public String getGroupId() {
        return new String(groupId);
    }

    public void addUser(RawUser user, RawUser adder) {
        if (adder.equals(admin) && !bannedUsers.contains(user))
            ordinaryUsers.add(user);
    }

    public void addUsers(ArrayList<RawUser> users, RawUser adder) {
        if (adder.equals(admin)) {
            ordinaryUsers.addAll(users.stream().filter(e -> !bannedUsers.contains(e)).collect(Collectors.toList()));
        }
    }

    public void ban(RawUser user, RawUser banner) {
        if (banner.equals(admin)) {
            bannedUsers.add(user);
        }
    }

    @Override
    public void messageReceived(Message msg) {
        allMessages.add(msg);
    }
}
