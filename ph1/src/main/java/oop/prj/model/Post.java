package oop.prj.model;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.stream.Collectors;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

import static com.diogonunes.jcolor.Ansi.*;
import static com.diogonunes.jcolor.Attribute.*;

@DBTable(tableName = "posts")
public class Post extends RawMessage implements Comparable<Post> {

    @DBField(name = "likes")
    protected TreeSet<Like> likes = new TreeSet<>();

    @DBField(name = "watches")
    protected TreeSet<Seen> watches = new TreeSet<>();

    @DBField(name = "comments")
    protected ArrayList<Integer> commentsIds = new ArrayList<>();

    private static ArrayList<Post> allPosts = new ArrayList<>();

    private static boolean fetched = false;

    public Post() {
    }

    protected Post(String context, User owner) {
        super(owner, context);
        DBManager.insert(this);
        allPosts.add(this);
    }

    public static Post makePost(String context, User owner) {
        Post instance = new Post(context, owner);
        return instance;
    }

    public void like(User liker) {
        likes.add(new Like(liker));
    }

    public void comment(String text, User user) {
        commentsIds.add(new Comment(text, user, this).getId());
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

    public ArrayList<Comment> getAllComments() {
        return commentsIds.stream().map(e -> Comment.getWithId(e)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public int compareTo(Post o) {
        return dateTime.compareTo(o.getDateTime());
    }

    @Override
    public String toString() {
        var formatter = DateTimeFormatter.ofPattern("hh:mm a\nyyyy LLL dd");
        String res = "";
        res += "----------\n";
        res += context;
        res += "\n----------\n";
        res += colorize(dateTime.format(formatter), MAGENTA_TEXT());
        res += "\n";
        res += (colorize("|likes:" + likes.size(), RED_TEXT(), BOLD()));
        res += (colorize("    |watches:" + watches.size(), GREEN_TEXT(), BOLD()));
        res += (colorize("    |id:" + id, YELLOW_TEXT(), BOLD()));
        if (commentsIds.size() > 0) {
            res += colorize("\n\n" + colorize("  Comments:", TEXT_COLOR(150, 150, 150), ITALIC()) + "\n");
            for (var c : getAllComments()) {
                if (c != null)
                    res += c.toString();
            }
        }

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
