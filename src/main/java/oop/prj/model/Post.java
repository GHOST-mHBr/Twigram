package oop.prj.model;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;

@DBTable(tableName = "posts")
public class Post extends RawMessage implements Comparable<Post> {

    @DBField(name = "likes")
    protected TreeSet<Like> likes = new TreeSet<>();

    @DBField(name = "watches")
    protected TreeSet<Seen> watches = new TreeSet<>();

    @DBField(name = "comments")
    protected ArrayList<Comment> comments = new ArrayList<>();

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

    public void like(User liker) {
        likes.add(new Like(liker));
    }

    public void comment(String text, User user) {
        comments.add(new Comment(text, user, this));
    }

    public void addSeen(User user) {
        Seen s = new Seen(user);
        watches.add(s);
    }

    public TreeSet<Like> getAllLikes() {
        return likes;
    }

    public TreeSet<Seen> getAllWatches() {
        return watches;
    }

    public static Post getWithId(Integer id) {
        for (Post post : allPosts) {
            if (post.getId().equals(id)) {
                return post;
            }
        }
        throw new NoSuchElementException("No such a post exists");
    }

    public static Post getWithId(String id) {
        try {
            Integer id_ = Integer.parseInt(id);
            return getWithId(id_);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("No such a id exists");
        }
    }

    @Override
    public int compareTo(Post o) {
        return dateTime.compareTo(o.getDateTime());
    }

    @Override
    public String toString() {
        var formatter = DateTimeFormatter.ofPattern("yyyy LLL dd\nhh:mm a");
        String res = "";
        // res += "from: " + getOwner().getUserName();
        res += dateTime.format(formatter);
        res += "\n";
        res += colorize("Post Id: " + id + " ", GREEN_BACK(), ITALIC(), BOLD(), UNDERLINE());
        res += "\n----------\n";
        res += context;
        res += "\n----------";

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
        if (getId() == otherPost.getId()) {
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
