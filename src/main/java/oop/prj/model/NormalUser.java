package oop.prj.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

import oop.prj.DB.DBField;
import oop.prj.DB.DBTable;

@DBTable(tableName = "normal_user")
@Deprecated
public class NormalUser extends User {

    @DBField(name = "posts")
    ArrayList<Integer> postIds = new ArrayList<>();

    @Expose(serialize = false, deserialize = false)
    transient ArrayList<Post> allPosts = new ArrayList<>();


    public NormalUser() {
    }

    private NormalUser(String userName, String userId, String password, String gmailAddr, String phoneNumber) {
        // super();
        // setUserId(userId);
        setPassword(password);
        setGmailAddr(gmailAddr);
        setPhoneNumber(phoneNumber);
        setUserName(userName);
    }

    public static NormalUser create(String userName, String userId, String password, String gmailAddr,
            String phoneNumber) {
        try {
            NormalUser nUser = new NormalUser(userName, userId, password, gmailAddr, phoneNumber);
            allUsers.add(nUser);
            return nUser;
        } catch (Exception e) {
            return null;
        }
    }

    // public static void loadAllObjects() {
    //     if (allNormals.size() == 0) {
    //         allNormals.addAll(DBManager.getAllObjects(NormalUser.class));
    //         RawUser.getAllUsers().addAll(allNormals);
    //     }

    // }

    public static void fetchData() {
        Post.fetchData();

        for (User user : allUsers) {
            if (user instanceof NormalUser) {
                NormalUser nUser = (NormalUser) user;
                nUser.allPosts = nUser.postIds.stream().map(e -> Post.getWithId(e))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        }
    }

    public ArrayList<Post> getPosts() {
        return allPosts;
    }

    public ArrayList<Integer> getPostIds() {
        return postIds;
    }

    public void post(String text) {
        Post post = new Post(text, this);
        allPosts.add(post);
    }

    public ArrayList<Post> getAllPosts() {
        return allPosts;
    }

    @Override
    public Class<? extends Sendable> getReceiverClass() {
        return NormalUser.class;
    }
}
