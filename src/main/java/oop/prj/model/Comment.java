package oop.prj.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

import static com.diogonunes.jcolor.Ansi.*;
import static com.diogonunes.jcolor.Attribute.*;

@DBTable(tableName = "comments")
public class Comment extends Post {

    @DBField(name = "commented_post")
    Integer commentedPostId = null;

    @Expose(serialize = false, deserialize = false)
    transient static ArrayList<Comment> allComments = new ArrayList<>();

    public Comment() {
    }

    public Comment(String context, User owner, Post post) {
        this.context = context;
        dateTime = LocalDateTime.now();
        ownerId = owner.getID();
        commentedPostId = post.getId();
        DBManager.insert(this);
        allComments.add(this);
    }

    public static Comment getWithId(Integer id) {
        for (var c : allComments) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public static Comment getWithId(String id) {
        try {
            Integer id_ = Integer.parseInt(id);
            return getWithId(id_);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("bad id for comment!");
        }
    }

    public static void loadAllObjects() {
        if (allComments.size() == 0) {
            allComments = DBManager.getAllObjects(Comment.class);
        }
    }

    public static void saveData() {
        for (var c : allComments) {
            if (DBManager.exists(c)) {
                DBManager.update(c);
            } else {
                DBManager.insert(c);
            }
        }
    }

    @Override
    public String toString() {
        String res = "     ------\n";
        res += "    |" + getOwner().getUserName() + ":\n";
        res += "    |" + context + "\n";
        res += colorize("    |likes:" + likes.size(), RED_TEXT(), BOLD());
        res += colorize("    |watches:" + watches.size(), GREEN_TEXT(), BOLD());
        res += colorize("    |id:" + id, YELLOW_TEXT());

        res += colorize(dateTime.format(DateTimeFormatter.ofPattern("\n    |yyyy LLL dd\n    |hh:mm a")),
                MAGENTA_TEXT());
        res += "\n     ------";
        for (var ci : commentsIds) {
            var c = Comment.getWithId(ci);
            String rr = c.toString().lines().map(e -> "\n    " + e).collect(Collectors.joining());
            res += rr;
        }
        return res;
    }

}
