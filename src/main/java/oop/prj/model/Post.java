package oop.prj.model;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

@DBTable(tableName = "posts")
public class Post extends RawMessage implements Comparable<Post> {

    @DBField(name = "likes")
    ArrayList<Like> likes = new ArrayList<>();

    @DBField(name = "watches")
    ArrayList<Seen> watches = new ArrayList<>();

    @DBField(name = "comments")
    private ArrayList<Comment> comments = new ArrayList<>();

    private static ArrayList<Post> allPosts = new ArrayList<>();

    private static boolean fetched = false;

    public Post() {
    }

    protected Post(String context, User owner) {
        super(owner, context);
        setId(DBManager.getLastId(Post.class) + 1);
        allPosts.add(this);
    }

    public static Post makePost(String context, User owner) {
        if (context.equals("") || owner.equals(null)) {
            throw new IllegalArgumentException();
        }
        Post instance = new Post(context, owner);
        return instance;
    }

    public Integer getID() {
        return id;
    }

    public boolean like(User liker) {
        // TODO check like conditions
        if (!likes.contains(new Like(liker))) {
            likes.add(new Like(liker));
            return true;
        }
        return false;
    }

    public void comment(String text, User user) {
        comments.add(new Comment(text, user, this));
    }

    public void addSeen(User user) {
        if (user != null && !watches.contains(new Seen(user)))
            watches.add(new Seen(user));
    }

    public List<Like> getAllLikes() {
        return likes;
    }

    public List<Seen> getAllWatches() {
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
        var formatter = DateTimeFormatter.ofPattern("yyyy LLL dd\nhh:mm a");
        String res = "----------\n";
        res += context;
        res += "\n----------";
        res += "\nfrom: " + getOwner().getUserName();
        res += "\nat:\n" + dateTime.format(formatter);
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
            allPosts = DBManager.getAllObjects(Post.class);

            fetched = true;
        }
    }

    public static void saveData() {
        for (int i = 0; i < allPosts.size(); i++) {
            Post post = allPosts.get(i);
            // post.syncData();
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

    public static void loadAllObjects() {
        if (allPosts.size() == 0) {
            allPosts.addAll(DBManager.getAllObjects(Post.class));
        }
    }

}
