package oop.prj.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import oop.prj.DB.DBField;
import oop.prj.DB.DBManager;
import oop.prj.DB.DBTable;

@DBTable(tableName = "ads")
public class AdPost extends RawMessage {
    @DBField(name = "likes")
    private ArrayList<Like> likes = new ArrayList<>();

    @DBField(name = "watches")
    private ArrayList<Seen> watches = new ArrayList<>();

    public AdPost() {
    }

    private static ArrayList<AdPost> allAds = new ArrayList<>();

    public AdPost(String context, RawUser owner) {
        super(owner, context);
        allAds.add(this);
    }

    public static void fetchData() {
        try {
            allAds = DBManager.getAllObjects(AdPost.class);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void saveData() {
        for (int i = 0; i < allAds.size(); i++) {
            var post = allAds.get(i);
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

    public void addSeen(RawUser user) {
        if (!watches.contains(new Seen(user)))
            watches.add(new Seen(user));
    }

    public boolean like(RawUser user) {
        if (!likes.contains(new Like(user))) {
            likes.add(new Like(user));
            return true;
        }
        return false;
    }

    public ArrayList<Seen> getAllWatches() {
        return watches;
    }

    public ArrayList<Like> getAllLikes() {
        return likes;
    }

    @Override
    public String toString() {
        String res = "\\e[31m-----=== Ad Post ===-----\\e[0m";
        res += owner.getUserName() + ":\n" + context + "\nAd id: " + id + "\n";
        return res;
    }

    public static AdPost getWithId(Integer id) {
        for (var adPost : allAds) {
            if (adPost.getId().equals(id)) {
                return adPost;
            }
        }
        throw new NoSuchElementException("No such ad exists");
    }
}
