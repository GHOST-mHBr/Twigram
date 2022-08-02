package oop.prj.model;

import java.time.LocalDateTime;


public class Like {
    LocalDateTime dateTime =null;
    RawUser liker = null;

    public Like(RawUser user){
        liker = user;
        dateTime = LocalDateTime.now();
    }

    public RawUser getLiker(){
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
