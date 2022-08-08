package com.example.theprojectphase2;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class FollowingController {

    @FXML
    VBox vbox;

    User user;

    Stage s;


    public void initialize(User u, Stage stage){
        for(User us : User.Users)
            if(us.getID() == u.getID())
                user=us;

        for(User f : user.getFollowed())
            loadPV(f);

        s = stage;

    }

    public void loadPV(User user1){

        ArrayList<Post> AllPosts = new ArrayList<>();
        for (Post post1 : user.getReceivedMessages())
            if(post1.getOwner().getID() == user1.getID())
                AllPosts.add(post1);

        for (Post post1 : user1.getReceivedMessages())
            if(post1.getOwner().getID() == user.getID())
                AllPosts.add(post1);

        Collections.sort(AllPosts);
        //Collections.reverse(AllPosts);


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


        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    ViewUserProfile(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        container.setPrefHeight(60);
        container.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        container.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        container.setMinHeight(TextFlow.USE_PREF_SIZE );
        container.setMaxHeight(TextFlow.USE_COMPUTED_SIZE );

        textFlow.setTranslateX(10);
        container.getChildren().add(textFlow);
        vbox.getChildren().add(container);
    }

    public void ViewUserProfile(MouseEvent event) throws IOException {
        TextFlow view = (TextFlow) event.getSource();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UserProfile.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        UserProfileController controller = loader.getController();
        for(User u : User.Users)
            if(u.getID() == Integer.parseInt(view.getId()))
                controller.initialize(u,user, s);

        Stage MainStage = new Stage();
        MainStage.setResizable(false);
        MainStage.setTitle("User Profile");

        MainStage.setScene(scene);
        MainStage.show();
    }




}
