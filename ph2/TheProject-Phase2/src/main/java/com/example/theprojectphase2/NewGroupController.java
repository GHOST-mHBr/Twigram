package com.example.theprojectphase2;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Random;

public class NewGroupController {

    @FXML
    TextField group_name;

    @FXML
    Button add_button;

    @FXML
    ImageView picture;

    @FXML
    Label msg;

    @FXML
    VBox vbox;

    User us;

    Group group ;

    public void initialize(User user, Group g) throws IOException {
        for(User u : User.Users)
            if(u.getID() == user.getID())
                us=u;

        group = g;

        Random random = new Random();
        int r = random.nextInt(4);

        if(r == 0){ group.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\blue-background.png")); }

        else if(r == 1){group.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\green-background.png"));}

        else if(r == 2){group.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\red-background.png"));}

        else {group.setImage(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\orange-background.png"));}

        BufferedImage bImage = SwingFXUtils.fromFXImage(group.getImage(), null);
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", s);
        byte[] res  = s.toByteArray();
        s.close();
        String encodedFile = Base64.getEncoder().encodeToString(res);
        group.setImageString(encodedFile);


        Circle circle = new Circle(50);
        circle.setTranslateX(50);
        circle.setTranslateY(50);

        picture.setClip(circle);

        if(!group.getMembers().contains(user))
          group.getMembers().add(user);

        loadList();
    }

    public void loadList(){
        for(User uu : group.getMembers())
            loadPV(uu);
    }

    public void AddMember(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FollowersView_newGroup.fxml"));
        Parent parent = loader.load();

        Popup popup = new Popup();
        //Scene scene = new Scene(parent);


        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        FollowersViewnewGroup_Controller controller = loader.getController();
        controller.initialize(us,group,s);


        popup.getContent().add(parent);
        popup.setAutoHide(true);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        //Stage MainStage = (Stage) popup1.getOwnerWindow();
        //MainStage.setResizable(false);
        //MainStage.setTitle("Group Profile");
        //MainStage.setX(x + event.getSceneX());
        //MainStage.setY(y + event.getSceneY());

        //MainStage.setScene(scene);
        popup.show(MainStage);
    }

    public void loadPV(User user1){
        TextFlow textFlow = new TextFlow();

        TextFlow container = new TextFlow();

        ImageView profileImage = new ImageView(user1.getImage());
        Circle circle = new Circle(20);
        circle.setTranslateX(30);
        circle.setTranslateY(30);
        profileImage.setClip(circle);
        profileImage.setId(String.valueOf(user1.getID()));
        profileImage.setFitWidth(50);
        profileImage.setFitHeight(50);


        Text text;
        text  = new Text(user1.getUserName());


        textFlow.setTranslateY(-profileImage.getFitHeight()/2);
        container.getChildren().add(profileImage);
        textFlow.getChildren().add(text);

        container.setId(String.valueOf(user1.getID()));
        textFlow.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMinHeight(TextFlow.USE_COMPUTED_SIZE );
        textFlow.setMaxHeight(TextFlow.USE_COMPUTED_SIZE );



        container.setStyle("-fx-background-color: rgb(255,255,255);");

        container.setPrefHeight(60);
        container.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        container.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        container.setMinHeight(TextFlow.USE_PREF_SIZE );
        container.setMaxHeight(TextFlow.USE_COMPUTED_SIZE );

        textFlow.setTranslateX(10);
        container.getChildren().add(textFlow);
        vbox.getChildren().add(container);
    }

    public void Confirm(ActionEvent event) throws IOException {
        group.GroupName = group_name.getText();
        //group.image =
        if(!group.getGroupName().isEmpty() && group.getMembers().size()>0){
            Group.Groups.add(group);


            for(User u : group.getMembers()) {
                u.getGroups().add(group);
            }

            if(picture.getImage() != null){
                group.setImage(picture.getImage());

                BufferedImage bImage = SwingFXUtils.fromFXImage(picture.getImage(), null);
                ByteArrayOutputStream s = new ByteArrayOutputStream();
                ImageIO.write(bImage, "png", s);
                byte[] res  = s.toByteArray();
                s.close();
                String encodedFile = Base64.getEncoder().encodeToString(res);
                group.setImageString(encodedFile);
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

        }

        else if(group.getGroupName().isEmpty())
            msg.setText("Type a Name");

        else if(group.getMembers().isEmpty())
            msg.setText("Choose at Least One Member");
    }

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

        group.setImage(picture.getImage());

        event.consume();
    }

    public void LoginToMainPage(ActionEvent event) throws IOException {
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
