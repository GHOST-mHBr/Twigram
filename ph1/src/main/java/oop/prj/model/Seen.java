package oop.prj.model;

import java.time.LocalDateTime;


public class Seen implements Comparable<Seen>{
    LocalDateTime dateTime = null;
    Integer watcherId = null;

    public Seen(User user){
        if(user == null){
            throw new IllegalArgumentException("No such a user as watcher exists");
        }
        watcherId = user.getID();
        dateTime = LocalDateTime.now();
    }

    public User getUser(){
        return User.getWithId(watcherId);
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public Integer getWatcherId(){
        return watcherId;
    }

    @Override
    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        if(other instanceof Seen){
            Seen seen = (Seen)other;
            return seen.getUser().getID() == watcherId;
        }

        return false;
    }

    @Override
    public int compareTo(Seen o) {
        if(watcherId > o.getWatcherId()){
            return 1;
        }
        if(watcherId < o.getWatcherId()){
            return -1;
        }
        return 0;
    }

}
