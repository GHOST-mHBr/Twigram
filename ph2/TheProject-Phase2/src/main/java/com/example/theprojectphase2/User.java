package com.example.theprojectphase2;

import javafx.scene.image.Image;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

@DBTable(tableName = "users")
public class User implements Comparable<Post>{

    public ArrayList<Double> getFollowersId() {
        return followersId;
    }

    public Boolean getNormal() {
        return isNormal;
    }

    public ArrayList<Double> getFollowedId() {
        return followedId;
    }

    public ArrayList<Double> getGroupsId() {
        return groupsId;
    }

    public ArrayList<Double> getSavedPostsId() {
        return savedPostsId;
    }

    public ArrayList<Double> getLikedPostsId() {
        return likedPostsId;
    }

    public ArrayList<Double> getPostsId() {
        return postsId;
    }

    public ArrayList<Double> getReceivedMessagesId() {
        return receivedMessagesId;
    }

    @DBPrimaryKey
    @DBAutoIncrement
    @DBField(name = "Id")
    Integer ID = 0;

    @DBField(name = "followers")
    public ArrayList<Double> followersId = new ArrayList<>();

    public ArrayList<User> followers = new ArrayList<>();

    public static ArrayList<User> Users = new ArrayList<>();



    @DBField(name = "age")
    Integer Age = 0;

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @DBField(name = "phoneNumber")
    String PhoneNumber;

    public String getImageString() {
        return imageString;
    }

    public Image getImage() {
        return image;
    }

    @DBField(name = "email")
    String Email;

    @DBField(name = "image")
    String imageString;

    Image image;

    @DBField(name = "Username")
    public String UserName;

    @DBField(name = "Password")
    protected String PassWord;

    @DBField(name = "gender")
    Boolean gender; //true for male and false for female


    @DBField(name = "IsNormal")
    Boolean isNormal;

    @DBField(name = "bio")
    String Bio;


    User(String userName,String passWord){this.UserName = userName; this.PassWord=passWord;}
    User(){}//default constructor



    @DBField(name = "followed")
    ArrayList<Double> followedId = new ArrayList<>();

    ArrayList<User>  followed = new ArrayList<>();

    @DBField(name = "myGroups")
    ArrayList<Double> groupsId = new ArrayList<>();

    ArrayList<Group> groups = new ArrayList<>();

    @DBField(name = "mySavedPosts")
    ArrayList<Double> savedPostsId = new ArrayList<>();

    ArrayList<Post> savedPosts = new ArrayList<>();

    @DBField(name = "myLikedPosts")
    ArrayList<Double> likedPostsId = new ArrayList<>();

    ArrayList<Post> likedPosts = new ArrayList<>();

    @DBField(name = "myPosts")
    ArrayList<Double> postsId = new ArrayList<>();

    ArrayList<Post> posts = new ArrayList<>();

    @DBField(name = "receivedMessages")
    ArrayList<Double>  receivedMessagesId = new ArrayList<>();

    ArrayList<Post> receivedMessages = new ArrayList<>();

    public static ArrayList<User> getUsers() {
        return Users;
    }

    public int getID() {
        return ID;
    }

    public int getAge() {
        return Age;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public boolean isGender() {
        return gender;
    }

    public String getBio() {
        return Bio;
    }

    public boolean getGender() {
        return gender;
    }

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public ArrayList<User> getFollowed() {
        return followed;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public ArrayList<Post> getSavedPosts() {
        return savedPosts;
    }

    public ArrayList<Post> getLikedPosts() {
        return likedPosts;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setReceivedMessages(ArrayList<Post> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }



    public ArrayList<Post> getReceivedMessages() {
        return receivedMessages;
    }


    void ChangeUserName(String NewUserName){}
    void ChangePassword(){}
    void SetBio(String NewBio){}

    public void setNormal(boolean normal) {
        isNormal = normal;
    }

    void addFollower(){}
    void addLikedPost(){}
    void addSavedPost(){}
    void Post(){}
    void CreateGroup(){}
    void LikePost(){}
    void DirectMessage(){}
    void DeletePost(){}

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setAge(int age) {
        Age = age;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setBio(String bio) {
        Bio = bio;
    }


    @Override
    public int compareTo(Post o) {
        return 0;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setFollowersId(ArrayList<Double> followersId) {
        this.followersId = followersId;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }

    public void setAge(Integer age) {
        Age = age;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public void setNormal(Boolean normal) {
        isNormal = normal;
    }

    public void setFollowedId(ArrayList<Double> followedId) {
        this.followedId = followedId;
    }

    public void setFollowed(ArrayList<User> followed) {
        this.followed = followed;
    }

    public void setGroupsId(ArrayList<Double> groupsId) {
        this.groupsId = groupsId;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public void setSavedPostsId(ArrayList<Double> savedPostsId) {
        this.savedPostsId = savedPostsId;
    }

    public void setSavedPosts(ArrayList<Post> savedPosts) {
        this.savedPosts = savedPosts;
    }

    public void setLikedPostsId(ArrayList<Double> likedPostsId) {
        this.likedPostsId = likedPostsId;
    }

    public void setLikedPosts(ArrayList<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public void setPostsId(ArrayList<Double> postsId) {
        this.postsId = postsId;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public void setReceivedMessagesId(ArrayList<Double> receivedMessagesId) {
        this.receivedMessagesId = receivedMessagesId;
    }
}
