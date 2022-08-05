package oop.prj.model;

import java.time.LocalDateTime;


public class Like {
    LocalDateTime dateTime =null;
    User liker = null;

    public Like(User user){
        liker = user;
        dateTime = LocalDateTime.now();
    }

    public User getLiker(){
        return liker;
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    @Override
    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        if(other instanceof Like){
            Like like = (Like)other;
            return like.getLiker().equals(liker);
        }

        return false;
    }
}
