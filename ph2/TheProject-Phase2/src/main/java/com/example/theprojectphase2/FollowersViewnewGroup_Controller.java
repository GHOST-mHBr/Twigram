package com.example.theprojectphase2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class FollowersViewnewGroup_Controller {
    @FXML
    VBox vbox;

    User us;

    Group group;

    Stage s;



    ArrayList<User> Selected = new ArrayList<>();

    public void initialize(User u, Group g, Stage stage){
        for(User user : User.Users)
            if(user.getID() == u.getID())
                us=user;

        group=g;

        s= stage;

        for(User f : us.getFollowers())
            if(!group.getMembers().contains(f))
               loadPV(f);

    }

    public void loadPV(User user1){

        ArrayList<Post> AllPosts = new ArrayList<>();
        for (Post post1 : us.getReceivedMessages())
            if(post1.getOwner().getID() == user1.getID())
                AllPosts.add(post1);

        for (Post post1 : user1.getReceivedMessages())
            if(post1.getOwner().getID() == us.getID())
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
                Select(container);
            }
        });


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

    public void Select(TextFlow textFlow){
        for(User user : us.getFollowers())
            if(user.getID() == Integer.parseInt(textFlow.getId())){
                Selected.add(user);
                textFlow.setStyle("-fx-background-color: rgb(200,200,200);");
                break;
            }
    }

    public void Confirm(ActionEvent event) throws IOException {
        group.getMembers().addAll(Selected);
        Popup popup = (Popup) ((Node)event.getSource()).getScene().getWindow();
        popup.hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NewGroup.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        NewGroupController controller = loader.getController();
        controller.initialize(us,group);

        Popup popup1 = (Popup) ((Node)event.getSource()).getScene().getWindow();
        Stage MainStage = (Stage) popup1.getOwnerWindow();
        MainStage.setResizable(false);
        MainStage.setTitle("Create New Group");

        MainStage.setScene(scene);
        MainStage.show();
    }

    public void Cancel(ActionEvent event){
        Popup popup = (Popup) ((Node)event.getSource()).getScene().getWindow();
        popup.hide();
    }
}
