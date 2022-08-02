package oop.prj.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

import oop.prj.DB.DBField;
import oop.prj.DB.DBTable;

@DBTable(tableName = "business_user")
public class BusinessUser extends RawUser {

    @DBField(name = "ads")
    ArrayList<Integer> postsIds = new ArrayList<>();

    ArrayList<AdPost> posts = new ArrayList<>();

    @DBField(name = "watches")
    ArrayList<Seen> pageWatches = new ArrayList<>();

    private static boolean fetched = false;

    public BusinessUser() {
    }

    private BusinessUser(String userName, String userId, String pass, String gmailAddr, String phoneNumber) {
        setUserName(userName);
        setUserId(userId);
        setGmailAddr(gmailAddr);
        setPassword(pass);
        setPhoneNumber(phoneNumber);
    }

    public static BusinessUser create(String userName, String userId, String pass, String gmailAddr,
            String phoneNumber) {
        try {
            BusinessUser bUser = new BusinessUser(userName, userId, pass, gmailAddr, phoneNumber);
            allUsers.add(bUser);
            return bUser;
        } catch (Exception e) {
            return null;
        }
    }

    public static void fetchData() {
        if (!fetched) {
            RawUser.fetchData();
            AdPost.fetchData();
            for (RawUser user : allUsers) {
                if (user instanceof BusinessUser) {
                    var bUser = (BusinessUser) user;
                    bUser.posts = bUser.postsIds.stream().map(e -> AdPost.getWithId(e))
                            .collect(Collectors.toCollection(ArrayList::new));
                }
            }
            fetched = true;
        }
    }

    public static void saveData(){
        RawUser.syncWithDB();


    }

    public void post(String text) {
        if (text == null || text.equals("")) {
            throw new IllegalArgumentException();
        }
        AdPost adPost = new AdPost(text, this);
        posts.add(adPost);
    }

    public ArrayList<AdPost> getAllPosts() {
        return posts;
    }

    public void seen(RawUser user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }
        pageWatches.add(new Seen(user));
    }
}
