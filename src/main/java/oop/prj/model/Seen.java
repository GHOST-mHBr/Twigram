package oop.prj.model;

import java.time.LocalDateTime;


public class Seen {
    LocalDateTime dateTime = null;
    User watcher = null;

    public Seen(User user){
        watcher = user;
        dateTime = LocalDateTime.now();
    }

    public User getUser(){
        return watcher;
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    @Override
    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        if(other instanceof Seen){
            Seen seen = (Seen)other;
            return seen.getUser().equals(watcher);
        }

        return false;
    }

}
