package com.example.theprojectphase2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileGroupController {

    @FXML
    Label GroupName_label;

    @FXML
    Label numMembers_label;

    @FXML
    Label GroupDescription_label;

    @FXML
    Label NumPosts_label;

    @FXML
    VBox members_list;

    @FXML
    ImageView profile;

    @FXML
    TextField searchBar;

    Group group;

    User us;

    Stage s;

    public void initialize(Group g, User u, Stage stage){
        //Initializes the page
        for(Group group1 : Group.Groups)
            if(group1.getID() == g.getID()){
                group = group1;
                break;
            }

        for(User user1 : User.Users)
            if(user1.getID() == u.getID()){
                us = user1;
                break;
            }

        s = stage;


        loadData(group);

    }

    public void loadData(Group g)
    {
        GroupName_label.setText(g.getGroupName());
        NumPosts_label.setText(g.getPosts().size() + " Posts");
        numMembers_label.setText(g.getMembers().size() + " Members");

        Circle circle = new Circle(40);
        circle.setTranslateX(50);
        circle.setTranslateY(50);
        profile.setClip(circle);

        profile.setImage(g.getImage());
        //GroupDescription_label.setText(g.getDescription());

        //DON'T FORGET TO IMPLEMENT THE PICTURES

        SearchUsers();

    }

    public void loadImage(Image image){
        profile.setImage(image);
    }

    public void SearchUsers(){
        members_list.getChildren().clear();
        String search = searchBar.getText();

        for(User u : group.getMembers())
            if(u.getUserName().contains(search)){
                TextFlow textFlow = new TextFlow();
                ImageView image = new ImageView(u.getImage());
                Circle circle = new Circle(20);
                circle.setTranslateX(30);
                circle.setTranslateY(30);
                image.setClip(circle);

                Text text = new Text(u.getUserName());
                text.setTranslateY(-25);

                textFlow.getChildren().add(image);
                textFlow.getChildren().add(text);

                image.setFitHeight(60);
                image.setFitWidth(60);

                image.setId(String.valueOf(u.getID()));
                image.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            ViewUserProfile(event);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                textFlow.setId(String.valueOf(u.getID()));
                textFlow.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
                textFlow.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
                textFlow.setMinHeight(TextFlow.USE_COMPUTED_SIZE );
                textFlow.setMaxHeight(TextFlow.USE_COMPUTED_SIZE );
                textFlow.setStyle("-fx-background-color: rgb(255,255,255);");
                members_list.getChildren().add(textFlow);
            }
    }

    public void ViewUserProfile(MouseEvent event) throws IOException {
        ImageView view = (ImageView) event.getSource();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UserProfile.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        UserProfileController controller = loader.getController();
        for(User u : group.getMembers())
            if(u.getID() == Integer.parseInt(view.getId()))
                controller.initialize(u,us,s);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        MainStage.setResizable(false);
        MainStage.setTitle("User Profile");

        MainStage.setScene(scene);
        MainStage.show();
    }

    public void OpenOptions(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GroupOptions.fxml"));
        Parent parent = loader.load();

        Popup popup = new Popup();
        //Scene scene = new Scene(parent);


        GroupOptionsController controller = loader.getController();
        controller.initialize(us,group,s);


        popup.getContent().add(parent);
        popup.setAutoHide(true);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        //MainStage.setResizable(false);
        //MainStage.setTitle("Group Profile");
        //MainStage.setX(x + event.getSceneX());
        //MainStage.setY(y + event.getSceneY());

        //MainStage.setScene(scene);
        popup.show(MainStage);
    }

    public void Leave(ActionEvent event){
        group.getMembers().remove(us);
        us.getGroups().remove(group);
        SearchUsers();
    }

}
