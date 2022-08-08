package oop.prj.model;

import java.time.LocalDateTime;

public class Like implements Comparable<Like> {
    LocalDateTime dateTime = null;
    Integer likerId = null;

    public Like(User user) {
        if (user == null) {
            throw new IllegalArgumentException("No such a liker exists");
        }
        likerId = user.getID();
        dateTime = LocalDateTime.now();
    }

    public User getLiker() {
        return User.getWithId(likerId);
    }

    public Integer getLikerId() {
        return likerId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof Like) {
            Like like = (Like) other;
            return likerId.equals(like.getLikerId());
        }

        return false;
    }

    @Override
    public int compareTo(Like o) {
        return (likerId > o.getLikerId() ? 1 : likerId == o.getLikerId() ? 0 : -1);
    }
}
