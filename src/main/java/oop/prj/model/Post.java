package oop.prj.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

@DBTable(tableName = "posts")
public class Post extends RawMessage implements Comparable<Post> {

    @DBField(name = "likers")
    ArrayList<RawUser> likers = new ArrayList<>();

    @DBField(name = "watches")
    ArrayList<RawUser> watches = new ArrayList<>();

    @DBField(name = "comments")
    protected ArrayList<Comment> comments = new ArrayList<>();

    private static ArrayList<Post> allPosts = new ArrayList<>();

    private static boolean fetched = false;

    protected Post(String context, RawUser owner) {
        super(owner, context);
        allPosts.add(this);
    }

    public static Post makePost(String context, RawUser owner) {
        if (context.equals("") || owner.equals(null)) {
            throw new IllegalArgumentException();
        }
        Post instance = new Post(context, owner);
        return instance;
    }

    public Integer getID() {
        return id;
    }

    public boolean like(RawUser liker) {
        // TODO check like conditions
        if (likers != null && !likers.contains(liker)) {
            likers.add(liker);
            return true;
        }
        return false;
    }

    public void comment(String text, RawUser user) {
        comments.add(new Comment(text, user, this));
    }

    public void addSeen(RawUser user) {
        if (user != null && !watches.contains(user))
            watches.add(user);
    }

    public List<RawUser> getAllLikes() {
        return likers;
    }

    public List<RawUser> getAllWatches() {
        return watches;
    }

    public static Post getWithId(Integer id) {
        for (Post post : allPosts) {
            if (post.getID().equals(id)) {
                return post;
            }
        }
        throw new NoSuchElementException("No such a post exists");
    }

    @Override
    public int compareTo(Post o) {
        return dateTime.compareTo(o.getDateTime());
    }

    @Override
    public String toString() {
        String res = "Post:\n";
        res += "----------\n";
        res += context;
        res += "\n----------";
        res += "\nfrom: " + owner.getUserName();
        res += "\nat:" + dateTime.toString();
        res += "\n----------\n";

        return res;
    }

    @Override
    public boolean equals(Object other) {
        if (other.equals(null)) {
            return false;
        }
        if (!(other instanceof Post)) {
            return false;
        }

        Post otherPost = (Post) other;
        if (getID() == otherPost.getID()) {
            return true;
        }
        return false;

    }

    public static void fetchData() {
        if (!fetched) {
            try {
                allPosts = DBManager.getAllObjects(Post.class);
                fetched = true;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void saveData() {
        for (int i = 0; i < allPosts.size(); i++) {
            Post post = allPosts.get(i);
            try {
                if (DBManager.exists(post)) {
                    DBManager.update(post);
                } else {
                    DBManager.insert(post);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
