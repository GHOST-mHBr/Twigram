package oop.prj.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

import oop.prj.App;
import oop.prj.DB.DBAutoIncrement;
import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBPrimaryKey;
import oop.prj.DB.DBTable;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;

@DBTable(tableName = "Users")
public class User implements Sendable {
    private String type = "User";
    @DBField(name = "Id")
    @DBPrimaryKey
    @DBAutoIncrement
    private Integer id = 0;

    public static enum UserType {
        BUSINESS, NORMAL
    }

    @DBField(name = "user_type")
    private UserType userType = UserType.NORMAL;

    @DBField(name = "followers")
    private ArrayList<Integer> followersIds = new ArrayList<>();

    @Expose(serialize = false, deserialize = false)
    private ArrayList<User> followers = new ArrayList<>();

    @DBField(name = "followings")
    private ArrayList<Integer> followingsIds = new ArrayList<>();

    @Expose(serialize = false, deserialize = false)
    private ArrayList<User> followings = new ArrayList<>();

    @DBField(name = "sent_messages")
    private ArrayList<Integer> sentMessagesIds = new ArrayList<>();

    @DBField(name = "received_messages")
    private ArrayList<Integer> receivedMessagesIds = new ArrayList<>();

    @Expose(serialize = false, deserialize = false)
    transient static ArrayList<User> allUsers = new ArrayList<>();

    @DBField(name = "banned_users")
    private ArrayList<Integer> bannedUsersIds = new ArrayList<>();

    @DBField(name = "username")
    private String username;

    @DBField(name = "pass")
    private String password;

    @DBField(name = "phone_number")
    private String phoneNumber;

    @DBField(name = "email")
    private String gmailAddr;

    @DBField(name = "user_id")
    private String userId = null;

    @DBField(name = "page_watches")
    private ArrayList<Seen> pageWatches = new ArrayList<>();

    @DBField(name = "posts")
    private ArrayList<Integer> postIds = new ArrayList<>();

    // @Expose(serialize = false, deserialize = false)
    // transient private ArrayList<Post> posts = new ArrayList<>();

    @Expose(serialize = false, deserialize = false)
    private static boolean fetched = false;

    public String getUserId() {
        return userId;
    }

    public User() {
    }

    public User(String userName, String userId, String password, String gmailAddr, String phoneNumber, UserType type) {
        setUserId(userId);
        setUserName(userName);
        setPassword(password);
        setGmailAddr(gmailAddr);
        setPhoneNumber(phoneNumber);
        userType = type;
        allUsers.add(this);
    }

    public void post(String text) {
        Post post = new Post(text, this);
        postIds.add(post.getId());
    }

    public ArrayList<Integer> getPostsIds() {
        return postIds;
    }

    public void seenPage(User user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }
        pageWatches.add(new Seen(user));
    }

    public static void loadAllObjects() {
        if (allUsers.size() == 0)
            allUsers.addAll(DBManager.getAllObjects(User.class));
    }

    protected static void syncWithDB() {
        for (int i = 0; i < allUsers.size(); i++) {
            var user = allUsers.get(i);
            user.followersIds = user.followers.stream().map(e -> e.getID())
                    .collect(Collectors.toCollection(ArrayList::new));

            user.followingsIds = user.followings.stream().map(e -> e.getID())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

    }

    public static void saveData() {
        syncWithDB();
        for (int i = 0; i < allUsers.size(); i++) {
            var user = allUsers.get(i);
            try {
                boolean r = DBManager.exists(user);
                if (r)
                    DBManager.update(user);
                else
                    DBManager.insert(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static User getUser(String userId) {
        for (var user : allUsers) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGmailAddr() {
        return gmailAddr;
    }

    public ArrayList<Message> getAllSentMessages() {
        return sentMessagesIds.stream().map(e -> Message.getWithId(e)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Message> getAllReceivedMessages() {
        return receivedMessagesIds.stream().map(e -> Message.getWithId(e))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<User> getAllFollowers() {
        return followers;
    }

    public ArrayList<User> getAllFollowings() {
        return followings;
    }

    public void setUserId(String userId) {
        allUsers.forEach(e -> {
            if (e.getUserId().equals(userId)) {
                throw new IllegalArgumentException("The id has already taken!");
            }
        });
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public static User getWithId(Integer id) {
        for (User user : allUsers) {
            if (user.getID().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public static User getWithId(String id) {
        try {
            Integer id_ = Integer.parseInt(id);
            return getWithId(id_);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad id!");
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber.matches("\\+[0-9]{12}") || phoneNumber.matches("09[0-9]{9}"))
            this.phoneNumber = phoneNumber;
        else
            throw new IllegalArgumentException("Bad phone number\nplease try agin\n");
    }

    public void setGmailAddr(String gmailAddr) {
        if (!gmailAddr.matches(".+@[ge]mail.com")) {
            throw new IllegalArgumentException("Bad gmail address!\nPlease Try again\n");
        }
        this.gmailAddr = gmailAddr;
    }

    public final Integer getID() {
        return id;
    }

    public void sendMessage(String text, Sendable receiver) {
        if (text == null || text.replaceAll(" ", "").equals("")) {
            throw new IllegalArgumentException("the message is empty!");
        }
        if (receiver == null) {
            throw new IllegalArgumentException("No such a receiver");
        }

        Message msg = new Message(text, this, receiver);
        sentMessagesIds.add(msg.getId());
        receiver.messageReceived(msg);
    }

    public void setPassword(String pass) {
        password = pass;
    }

    public ArrayList<Message> getSentMsgsTo(Sendable receiver) {

        ArrayList<Message> result = new ArrayList<>();
        for (Integer msgId : sentMessagesIds) {
            var msg = Message.getWithId(msgId);
            if (msg.getReceiver().equals(receiver) && msg.getOwner().equals(this)) {
                result.add(msg);
            }
        }
        return result;
    }

    public ArrayList<Message> getReceivedMsgsFrom(User sender) {
        ArrayList<Message> result = new ArrayList<>();
        for (Integer msgId : receivedMessagesIds) {
            var msg = Message.getWithId(msgId);
            if (msg.getReceiver().equals(this) && msg.getOwner().equals(sender)) {
                result.add(msg);
            }
        }
        return result;
    }

    public void follow(User other) {
        if (!followings.contains(other)) {
            followings.add(other);
            other.getAllFollowers().add(this);
        }
    }

    public void unFollow(User other) {
        if (followings.contains(other)) {
            followings.remove(other);
            other.getAllFollowers().remove(this);
        }
    }

    public static ArrayList<User> getAllUsers() {
        return allUsers;
    }

    @Override
    public void messageReceived(Message msg) {
        receivedMessagesIds.add(msg.getId());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        if (other.equals(null)) {
            return false;
        }

        User otherUser = (User) other;
        return (otherUser.getID() == this.getID());

    }

    public String getInfo() {
        String res = "\n--------------\n";
        res += "User name: " + username;
        res += "\nId: " + userId;
        res += "\n--------------";

        if (followers.size() > 0)
            res += "\nfollowers:\n";
        else
            res += "\nNo follower:(\n";
        for (User user : followers) {
            res += user.getUserName() + "\n";
        }
        if (followings.size() > 0)
            res += "\nfollowings:\n";
        else
            res += "\nNo following:(\n";

        for (User user : followings) {
            res += user.getUserName() + "\n";
        }
        return res;
    }

    @Override
    public String toString() {
        String res = "\n--------------\n";
        res += "User name: " + username;
        res += "\nId: " + userId;
        res += "\nPassword: " + password;
        res += "\nGmail:" + gmailAddr;
        res += "\nPhone number: " + phoneNumber;
        res += "\n--------------";

        if (followers.size() > 0)
            res += "\nfollowers:\n";
        else
            res += "\nNo follower:(\n";
        for (User user : followers) {
            res += user.getUserName() + "\n";
        }
        if (followings.size() > 0)
            res += "\nfollowings:\n";
        else
            res += "\nNo following:(\n";

        for (User user : followings) {
            res += user.getUserName() + "\n";
        }
        return res;
    }

    public void printAndSeeMessages() {
        if (receivedMessagesIds.size() > 0) {
            for (var m : getAllReceivedMessages()) {
                if (!m.hasSeen(this)) {
                    App.prLn(colorize(m.toString(), BOLD(), BLUE_TEXT()));
                    m.seen(this);
                } else {
                    App.prLn(m.toString());
                }
            }
        } else {
            App.prLn("You don't have any message!");
        }
    }

    public void printPosts(User watcher) {
        for (Post post : postIds.stream().map(e -> Post.getWithId(e)).collect(Collectors.toList())) {
            post.addSeen(watcher);
            if (userType.equals(UserType.BUSINESS))
                App.prLn("\n" + colorize("Advertisement", WHITE_TEXT(), RED_BACK(), ITALIC()));
            App.prLn(post.toString());
            App.prLn(colorize(post.getAllLikes().size() + " likes", RED_TEXT(), BOLD()));
            App.prLn(colorize(post.getAllWatches().size() + " watches", GREEN_TEXT(), BOLD()));
        }
    }

    @Override
    public Integer getReceiverId() {
        return id;
    }

    @Override
    public Class<? extends Sendable> getReceiverClass() {
        return User.class;
    }

}
