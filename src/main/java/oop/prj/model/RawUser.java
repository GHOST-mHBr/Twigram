package oop.prj.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import oop.prj.App;
import oop.prj.DB.DBAutoIncrement;
import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBPrimaryKey;

public class RawUser implements Sendable {
    private String type = "RawUser";
    @DBField(name = "Id")
    @DBPrimaryKey
    @DBAutoIncrement
    private Integer id = 0;

    @DBField(name = "followers")
    private ArrayList<Integer> followersIds = new ArrayList<>();

    private ArrayList<RawUser> followers = new ArrayList<>();

    @DBField(name = "followings")
    private ArrayList<Integer> followingsIds = new ArrayList<>();

    private ArrayList<RawUser> followings = new ArrayList<>();

    @DBField(name = "sent_messages")
    private ArrayList<Integer> sentMessagesIds = new ArrayList<>();

    @DBField(name = "received_messages")
    private ArrayList<Integer> receivedMessagesIds = new ArrayList<>();

    private ArrayList<Message> sentMessages = new ArrayList<>();
    private ArrayList<Message> receivedMessages = new ArrayList<>();
    static ArrayList<RawUser> allUsers = new ArrayList<>();

    @DBField(name = "banned_users")
    ArrayList<Integer> bannedUsersIds = new ArrayList<>();

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

    private static boolean fetched = false;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.replaceAll(" +", "").equals("")) {
            throw new IllegalArgumentException("Bad input for user id\nuser id is null or \"\"");
        }
        if (allUsers.size() == 0) {
            this.userId = userId;
        }
        for (RawUser user : allUsers) {
            if (user != null && user.getUserId() != null && user.getUserId().equals(userId)) {
                throw new IllegalArgumentException("The id has already taken");
            }
        }
        this.userId = userId;
    }

    public RawUser() {
        // allUsers.add(this);
    }

    public List<RawUser> getAllBusinessAccounts() {
        return allUsers.stream().filter(e -> (e instanceof BusinessUser)).collect(Collectors.toList());
    }

    public static void fetchData() {
        Message.fetchData();
        if (!fetched) {
            try {
                fetched = true;
                allUsers.addAll(DBManager.getAllObjects(NormalUser.class));
                allUsers.addAll(DBManager.getAllObjects(BusinessUser.class));
                // DBManager.getAllObjects(BusinessUser.class).stream().map(e ->
                // allUsers.add(e));

                for (RawUser user : allUsers) {
                    user.followers = user.followersIds.stream().map(e -> getWithId(e))
                            .collect(Collectors.toCollection(ArrayList::new));

                    user.followings = user.followingsIds.stream().map(e -> getWithId(e))
                            .collect(Collectors.toCollection(ArrayList::new));

                    user.sentMessages = user.sentMessagesIds.stream().map(e -> Message.getWithId(e))
                            .collect(Collectors.toCollection(ArrayList::new));

                    user.receivedMessages = user.receivedMessagesIds.stream().map(e -> Message.getWithId(e))
                            .collect(Collectors.toCollection(ArrayList::new));
                }

            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void syncWithDB() {
        for (int i = 0; i < allUsers.size(); i++) {
            var user = allUsers.get(i);
            user.followersIds = user.followers.stream().map(e -> e.getID())
                    .collect(Collectors.toCollection(ArrayList::new));
                    
            user.followingsIds = user.followings.stream().map(e -> e.getID())
                    .collect(Collectors.toCollection(ArrayList::new));

            user.sentMessagesIds = user.sentMessages.stream().map(e -> e.getId())
                    .collect(Collectors.toCollection(ArrayList::new));

            user.receivedMessagesIds = user.receivedMessages.stream().map(e -> e.getId())
                    .collect(Collectors.toCollection(ArrayList::new));

            if (user instanceof BusinessUser) {
                var bUser = (BusinessUser) user;
                bUser.postsIds = bUser.posts.stream().map(e -> e.getId())
                        .collect(Collectors.toCollection(ArrayList::new));
            } else {
                var nUser = (NormalUser) user;
                nUser.postIds = nUser.allPosts.stream().map(e -> e.getId())
                        .collect(Collectors.toCollection(ArrayList::new));
            }
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
        return sentMessages;
    }

    public ArrayList<Message> getAllReceivedMessages() {
        return receivedMessages;
    }

    public ArrayList<RawUser> getAllFollowers() {
        return followers;
    }

    public ArrayList<RawUser> getAllFollowings() {
        return followings;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public static RawUser getWithId(Integer id) {
        for (RawUser user : allUsers) {
            if (user.getID().equals(id)) {
                return user;
            }
        }
        return null;
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
        sentMessages.add(msg);
        receiver.messageReceived(msg);
    }

    public void setPassword(String pass) {
        password = pass;
    }

    public ArrayList<Message> getSentMsgsTo(Sendable receiver) {

        ArrayList<Message> result = new ArrayList<>();
        for (Message msg : sentMessages) {
            if (msg.getReceiver().equals(receiver) && msg.getOwner().equals(this)) {
                result.add(msg);
            }
        }
        return result;
    }

    public ArrayList<Message> getReceivedMsgsFrom(RawUser sender) {
        ArrayList<Message> result = new ArrayList<>();
        for (Message msg : receivedMessages) {
            if (msg.getReceiver().equals(this) && msg.getOwner().equals(sender)) {
                result.add(msg);
            }
        }
        return result;
    }

    public void follow(RawUser other) {
        if (!followings.contains(other)) {
            followings.add(other);
            other.getAllFollowers().add(this);
        }
    }

    public void unFollow(RawUser other) {
        if (followings.contains(other)) {
            followings.remove(other);
            other.getAllFollowers().remove(this);
        }
    }

    public static RawUser getUser(String userId) {
        for (RawUser user : allUsers) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        throw new NoSuchElementException("No such a user exist");
    }

    @Override
    public void messageReceived(Message msg) {
        receivedMessages.add(msg);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RawUser)) {
            return false;
        }
        if (other.equals(null)) {
            return false;
        }

        RawUser otherUser = (RawUser) other;
        if (otherUser.getID() == this.getID()) {
            return true;
        }
        return false;
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
        for (RawUser user : followers) {
            res += user.getUserName() + "\n";
        }
        if (followings.size() > 0)
            res += "\nfollowings:\n";
        else
            res += "\nNo following:(\n";

        for (RawUser user : followings) {
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
        for (RawUser user : followers) {
            res += user.getUserName() + "\n";
        }
        if (followings.size() > 0)
            res += "\nfollowings:\n";
        else
            res += "\nNo following:(\n";

        for (RawUser user : followings) {
            res += user.getUserName() + "\n";
        }
        return res;
    }

    public void printMessages() {
        if(receivedMessages.size() > 0){
            for(var m : receivedMessages){
                App.prLn(m.toString());
            }
        }else{
            App.prLn("You don't have any message!");
        }
    }

}
