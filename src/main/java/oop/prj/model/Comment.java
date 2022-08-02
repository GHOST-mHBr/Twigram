package oop.prj.model;

public class Comment extends Post {

    Post commentedPost = null;

    public Comment() {
        super(null, null);
    }

    public Comment(String context, RawUser owner, Post post) {
        super(context, owner);
        commentedPost = post;
    }

}