package com.example.theprojectphase2;


import javafx.scene.image.Image;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//import oop.prj.model.DB.DBManager;
//import oop.prj.model.DB.Saveable;

@DBTable(tableName = "posts")
public class Post implements Comparable<Post>  {

      @DBPrimaryKey
      @DBAutoIncrement
      @DBField(name = "Id")
      private Integer id = 0;



      @DBField(name = "PostLikedBy")
      public ArrayList<Double> likersId = new ArrayList<>();

      public ArrayList<User> likers = new ArrayList<>();


      @DBField(name = "seen")
      public LinkedHashMap<String, String> seens = new LinkedHashMap<>();


      @DBField(name = "likes")
      public LinkedHashMap<String, String> likes = new LinkedHashMap<>();

      @DBField(name = "publishDate")
      public LocalDateTime publishDate;

      @DBField(name = "comments")
      public   ArrayList<Double> commentsId = new ArrayList<>();

      public ArrayList<Comment> comments = new ArrayList<>();

      @DBField(name = "image")
      String imageString;

      public Image image;

      public void setImageString(String imageString) {
            this.imageString = imageString;
      }

      public void setImage(Image image) {
            this.image = image;
      }

      public String getImageString() {
            return imageString;
      }

      public Image getImage() {
            return image;
      }
//public void setPublishDate(LocalDateTime publishDate) {
            //this.publishDate = publishDate;
     // }

      @DBField(name = "context")
      private String context = "";

      //public LocalDateTime getPublishDate() {
            //return publishDate;
      //}

      //@DBField(name = "publishDate")
      //public LocalDateTime publishDate;

      //public void setPublishDate(LocalDateTime publishDate) {
      //this.publishDate = publishDate;
      //}

      @DBField(name = "title")
      private String title = "";

      @DBField(name = "replies")
      public ArrayList<Double> repliesId = new ArrayList<>();

      public ArrayList<Post> replies = new ArrayList<>();

      @DBField(name = "owner")
      Integer OwnerId = 0;

      User owner;

      public ArrayList<User> getLikers() {
            return likers;
      }


      public static ArrayList<Post> Posts = new ArrayList<>();


      //public void setImage(Image image) {
      //this.image = image;
      //}

      //public Image getImage() {
      //    return image;
      //}

      public void setId(int id) {
            this.id = id;
      }



      /*@Override
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

      }*/

      protected Post(String title, String context, User owner) {
            this.title = title;
            this.context = context;
            this.owner = owner;
            this.publishDate = LocalDateTime.now();
            if (Post.Posts.isEmpty())
                  this.id = 0;
            else
                  this.id = Post.Posts.get(Post.Posts.size() - 1).getId() + 1;

      }

      public Post() {
      }

      public static Post makePost(String title, String context, User owner) {
            if (title.equals("") || context.equals("") || owner.equals(null)) {
                  return null;
            }
            Post instance = new Post(title, context, owner);
            return instance;
      }


      public void like(User liker) {
            // TODO check like conditions
            if (!liker.equals(null) && !likers.contains(liker)) {
                  likers.add(liker);
            }
      }

      public String getTitle() {
            return title;
      }

      public String getContext() {
            return context;
      }

      public User getOwner() {
            return owner;
      }

      //public LocalDateTime getPublishDate() {
      //return publishDate;
      //}

      public ArrayList<Comment> getComments() {
            return comments;
      }

      public void setTitle(String newTitle) {
            title = newTitle;
      }

      public void setContext(String newContext) {
            context = newContext;
      }

      public void comment(Comment comment) {

            comments.add(comment);

      }

      public LinkedHashMap<String, String> getSeens() {
            return seens;
      }

      public LinkedHashMap<String, String> getLikes() {
            return likes;
      }

      public void setSeens(LinkedHashMap<String, String> seens) {
            this.seens = seens;
      }

      public void setLikes(LinkedHashMap<String, String> likes) { this.likes = likes; }

      public void setPublishDate(LocalDateTime publishDate) {
            this.publishDate = publishDate;
      }

      public LocalDateTime getPublishDate() {
            return publishDate;
      }

      @Override
      public int compareTo(Post o) {
             return publishDate.compareTo(o.getPublishDate());
      }

      public int getId() {
            return id;
      }

      public ArrayList<Double> getLikersId() {
            return likersId;
      }

      public void setId(Integer id) {
            this.id = id;
      }

      public void setLikersId(ArrayList<Double> likersId) {
            this.likersId = likersId;
      }

      public void setLikers(ArrayList<User> likers) {
            this.likers = likers;
      }

      public void setCommentsId(ArrayList<Double> commentsId) {
            this.commentsId = commentsId;
      }

      public void setComments(ArrayList<Comment> comments) {
            this.comments = comments;
      }

      public void setOwnerId(Integer ownerId) {
            OwnerId = ownerId;
      }

      public void setOwner(User owner) {
            this.owner = owner;
      }

      public ArrayList<Double> getCommentsId() {
            return commentsId;
      }

      public Integer getOwnerId() {
            return OwnerId;
      }

      public ArrayList<Double> getRepliesId() {
            return repliesId;
      }

      public ArrayList<Post> getReplies() {
            return replies;
      }

      public void setRepliesId(ArrayList<Double> repliesId) {
            this.repliesId = repliesId;
      }

      public void setReplies(ArrayList<Post> replies) {
            this.replies = replies;
      }
}
