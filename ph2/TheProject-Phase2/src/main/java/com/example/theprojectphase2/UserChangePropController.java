package com.example.theprojectphase2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

public class UserChangePropController {

    @FXML
    TextField username_text;

    @FXML
    PasswordField password_text;

    @FXML
    PasswordField password2_text;

    @FXML
    TextField email_text;

    @FXML
    TextField phone_text;

    @FXML
    TextField age_text;

    @FXML
    TextField bio_text;

    @FXML
    ImageView picture;

    @FXML
    ComboBox<String> gender_choose;

    @FXML
    ComboBox<String> type_choose;

    @FXML
    Label msg;

    User us;

    User u;

    ObservableList<String> genderList = FXCollections.observableArrayList("Male","Female");
    ObservableList<String> typeList = FXCollections.observableArrayList("Normal","Business");

    public void initialize(User user){
        username_text.setText(user.getUserName());

        password_text.setText(user.getPassWord());

        password2_text.setText(user.getPassWord());

        email_text.setText(user.getEmail());

        phone_text.setText(user.getPhoneNumber());

        age_text.setText(String.valueOf(user.getAge()));

        bio_text.setText(user.getBio());

        picture.setImage(user.getImage());

        if(user.getGender())
            gender_choose.setValue("Male");
        else
            gender_choose.setValue("Female");

        if(user.getNormal())
            type_choose.setValue("Normal");
        else
            type_choose.setValue("Business");

        us = user;

        u = user;

    }

    @FXML
    private void BackToLogIn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainPage.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        MainPageController mainPageController = loader.getController();
        mainPageController.initialize(u);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        MainStage.setScene(scene);
        MainStage.show();
    }

    @FXML
    private void DoSignUp(ActionEvent event) throws SQLException, IOException {

        String s1 = username_text.getText();

        String s2 = password_text.getText();
        String s2r = password2_text.getText();

        if(picture.getImage() == null) {
            Random random = new Random();
            int r = random.nextInt(4);

            if (r == 0) {
                us.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\blue-background.png"));
            } else if (r == 1) {
                us.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\green-background.png"));
            } else if (r == 2) {
                us.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\red-background.png"));
            } else {
                us.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\orange-background.png"));
            }
        }

        else
            us.setImage(picture.getImage());

        BufferedImage bImage = SwingFXUtils.fromFXImage(us.getImage(), null);
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", s);
        byte[] res  = s.toByteArray();
        s.close();
        String encodedFile = Base64.getEncoder().encodeToString(res);
        us.setImageString(encodedFile);

        if(username_text.getText().isEmpty())
            msg.setText("Please Enter a UserName");

        else if(password_text.getText().isEmpty())
            msg.setText("Please Enter a Password");

        else if(password2_text.getText().isEmpty())
            msg.setText("Please Repeat your Password");

        else {
            boolean alreadyExists = false;
            for(User u : User.Users)
                if (u.getUserName().equals(username_text.getText()) && u.getID() != u.getID()) {
                    alreadyExists = true;
                    break;
                }

            if(alreadyExists)
                msg.setText("This Username Is Already In Use.");

            else {
                if(!password_text.getText().equals(password2_text.getText()))
                    msg.setText("The Passwords Are Not The Same.");

                else{
                    if(password_text.getText().length() < 8)
                        msg.setText("Password Should Be at Least 8 Characters Long.");

                    else{
                        if(!age_text.getText().matches("[0-9]+") || Integer.parseInt(age_text.getText())<1)
                            msg.setText("Please Enter a Valid Age");

                        else {
                            if(gender_choose.getValue() == null || type_choose.getValue() == null)
                                msg.setText("Please Specify Your Gender and Account Type");

                            else{
                                us.setPassWord(password_text.getText());
                                us.setUserName(username_text.getText());

                                if (!email_text.getText().isEmpty()) us.setEmail(email_text.getText());
                                else us.setEmail("");

                                if (!phone_text.getText().isEmpty()) us.setPhoneNumber(phone_text.getText());
                                else us.setPhoneNumber("");

                                if (!age_text.getText().isEmpty()) us.setAge(Integer.parseInt(age_text.getText()));
                                else us.setAge(18);

                                if (!bio_text.getText().isEmpty()) us.setBio(bio_text.getText());
                                else us.setBio("");

                                if (gender_choose.getValue().equals("Male"))
                                    us.setGender(true);

                                else if (gender_choose.getValue().equals("Female"))
                                    us.setGender(false);

                                if (type_choose.getValue().equals("Normal"))
                                    us.setNormal(true);

                                else if (type_choose.getValue().equals("Business"))
                                    us.setNormal(false);

                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(getClass().getResource("MainPage.fxml"));
                                Parent parent = loader.load();

                                Scene scene = new Scene(parent);

                                MainPageController mainPageController = loader.getController();
                                mainPageController.initialize(us);

                                Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();


                                MainStage.setOnCloseRequest(event1 -> {

                                    //update All Posts
                                    for(Post post : Post.Posts){

                                        post.getLikersId().clear();
                                        for(User user1 : post.getLikers())
                                        {
                                            //post.getLikersId().retainAll(user.getID());
                                            //if(!post.getLikersId().contains((double) user1.getID()))
                                            post.getLikersId().add((double) user1.getID());
                                        }


                                        post.getCommentsId().clear();
                                        for(Comment comment : post.getComments())
                                        {
                                            post.getCommentsId().add((double)comment.getId());
                                            //if(!post.getCommentsId().contains((double)comment.getId()))
                                            //post.getCommentsId().add((double)comment.getId());
                                        }

                                        post.getRepliesId().clear();
                                        for(Post p : post.getReplies())
                                            post.getRepliesId().add((double)p.getId());

                                        DBManagerTester.update(post);
                                    }

                                    //update All Comments
                                    for(Comment comment : Comment.Comments){
                                        comment.setOwnerId(comment.getOwner().getID());

                                        comment.getLikersId().clear();
                                        for(User user1 : comment.getLikers())
                                        {
                                            //post.getLikersId().retainAll(user.getID());
                                            comment.getLikersId().add((double) user1.getID());
                                        }

                                        comment.getCommentsId().clear();
                                        for(Comment c : comment.getComments())
                                        {
                                            comment.getCommentsId().add((double)c.getId());
                                            //if(!post.getCommentsId().contains((double)comment.getId()))
                                            //post.getCommentsId().add((double)comment.getId());
                                        }


                                        DBManagerTester.update(comment);
                                    }


                                    //update All Groups
                                    for(Group group : Group.Groups){

                                        group.getMembersId().clear();
                                        for(User user1 : group.getMembers())
                                        {
                                            //post.getLikersId().retainAll(user.getID());
                                            group.getMembersId().add((double) user1.getID());
                                        }

                                        group.getPostsId().clear();
                                        for(Post post : group.getPosts())
                                        {
                                            //post.getLikersId().retainAll(user.getID());
                                            group.getPostsId().add((double)post.getId());
                                        }

                                        DBManagerTester.update(group);
                                    }

                                    //update all users
                                    for(User user1 : User.Users)
                                    {
                                        user1.getPostsId().clear();
                                        for(Post post : user1.getPosts())
                                        {
                                            //post.getLikersId().retainAll(user.getID());
                                            user1.getPostsId().add((double)post.getId());
                                        }

                                        user1.getSavedPostsId().clear();
                                        for(Post post : user1.getSavedPosts())
                                        {
                                            //post.getLikersId().retainAll(user.getID());
                                            user1.getSavedPostsId().add((double)post.getId());
                                        }

                                        user1.getLikedPostsId().clear();
                                        for(Post post : user1.getLikedPosts())
                                        {
                                            //post.getLikersId().retainAll(user.getID());
                                            user1.getLikedPostsId().add((double)post.getId());
                                        }

                                        user1.getReceivedMessagesId().clear();
                                        for(Post post : user1.getReceivedMessages())
                                        {
                                            //post.getLikersId().retainAll(user.getID());
                                            user1.getReceivedMessagesId().add((double)post.getId());
                                        }

                                        user1.getGroupsId().clear();
                                        for(Group group : user1.getGroups())
                                        {
                                            user1.getGroupsId().add((double)group.getID());
                                        }

                                        user1.getFollowersId().clear();
                                        for(User u : user1.getFollowers())
                                        {
                                            user1.getFollowersId().add((double)u.getID());
                                        }

                                        user1.getFollowedId().clear();
                                        for(User u : user1.getFollowed())
                                        {
                                            user1.getFollowedId().add((double)u.getID());
                                        }

                                        DBManagerTester.update(user1);
                                    }
                                });

                                MainStage.setScene(scene);
                                MainStage.show();
                            }
                        }
                    }
                }

            }
        }


    }


    /*@FXML
    private void DoSignUp(ActionEvent event) throws SQLException, IOException {
        gender_choose.setItems(genderList);
        type_choose.setItems(typeList);


        String s1 = username_text.getText();

        String s2 = password_text.getText();
        String s2r = password2_text.getText();


        if(picture.getImage() == null) {
            Random random = new Random();
            int r = random.nextInt(4);

            if (r == 0) {
                us.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\blue-background.png"));
            } else if (r == 1) {
                us.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\green-background.png"));
            } else if (r == 2) {
                us.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\red-background.png"));
            } else {
                us.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\orange-background.png"));
            }
        }

        else
            us.setImage(picture.getImage());

        BufferedImage bImage = SwingFXUtils.fromFXImage(us.getImage(), null);
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", s);
        byte[] res  = s.toByteArray();
        s.close();
        String encodedFile = Base64.getEncoder().encodeToString(res);
        us.setImageString(encodedFile);

        if(username_text.getText().isEmpty())
            msg.setText("Please Enter a UserName");

        else if(password_text.getText().isEmpty())
            msg.setText("Please Enter a Password");

        else if(password2_text.getText().isEmpty())
            msg.setText("Please Repeat your Password");

        else {
            boolean alreadyExists = false;
            for(User u : User.Users)
                if (u.getUserName().equals(s1)) {
                    alreadyExists = true;
                    break;
                }

            if(alreadyExists)
                msg.setText("This Username Is Already In Use.");

            else {
                if(!password_text.getText().equals(password2_text.getText()))
                    msg.setText("The Passwords Are Not The Same.");

                else{


                    if(!email_text.getText().isEmpty()) us.setEmail(email_text.getText());
                    else us.setEmail("");

                    if(!phone_text.getText().isEmpty()) us.setPhoneNumber(phone_text.getText());
                    else us.setPhoneNumber("");

                    if(!age_text.getText().isEmpty()) us.setAge(Integer.parseInt(age_text.getText()));
                    else us.setAge(18);

                    if(!bio_text.getText().isEmpty()) us.setBio(bio_text.getText());
                    else us.setBio("");

                    if (gender_choose.getValue().equals("male"))
                        us.setGender(true);

                    else if (gender_choose.getValue().equals("female"))
                        us.setGender(false);

                    if (type_choose.getValue().equals("Normal"))
                        us.setNormal(true);

                    else if (type_choose.getValue().equals("Business"))
                        us.setNormal(false);

                }

            }
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainPage.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        MainPageController mainPageController = loader.getController();
        mainPageController.initialize(us);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        MainStage.setScene(scene);
        MainStage.show();
    }*/


    public void ChoosePicture(MouseEvent event){
        Window window = ((Node) (event.getSource())).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*.gif"));

        File file = fileChooser.showOpenDialog(window);
        if(file != null){
            Image openedImage = new Image(file.toURI().toString());
            picture.setImage(openedImage);
        }

        event.consume();
    }


}
