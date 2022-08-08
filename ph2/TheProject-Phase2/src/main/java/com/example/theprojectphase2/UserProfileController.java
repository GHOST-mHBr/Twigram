package com.example.theprojectphase2;

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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class UserProfileController {

    @FXML
    Label UserName_label;

    @FXML
    Label Bio_label;

    @FXML
    VBox followers_vbox;

    @FXML
    VBox following_vbox;

    @FXML
    TextField searchBar;

    @FXML
    Button follow_button;


    @FXML
    ImageView image;

    @FXML
    AnchorPane top_title;



    User user;

    User us;

    Stage s;



    public void initialize(User u, User us1, Stage stage) {
        //Initializes the page
        for (User uu : User.Users)
            if (uu.getID() == u.getID()) {
                user = uu;
                break;
            }

        for (User uu1 : User.Users)
            if (uu1.getID() == us1.getID()) {
                us = uu1;
                break;
            }

        s = stage;


        Circle circle = new Circle(50);
        circle.setTranslateX(50);
        circle.setTranslateY(50);
        image.setClip(circle);
        image.setImage(user.getImage());


        UserName_label.setText(user.getUserName());
        Bio_label.setText(user.getBio());
        this.SearchUsers();
        if(user.equals(us)){
           follow_button.setVisible(false);
           follow_button.setDisable(true);

        }

        if(user.getFollowers().contains(us))
            follow_button.setText("Unfollow");


    }

    public void SearchUsers(){
        followers_vbox.getChildren().clear();
        following_vbox.getChildren().clear();
        String search = searchBar.getText();

        for(User u : user.getFollowers())
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
                followers_vbox.getChildren().add(textFlow);
            }

        for(User u : user.getFollowed())
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
                image.setOnMouseClicked(event -> {
                    try {
                        ViewUserProfile(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                textFlow.setId(String.valueOf(u.getID()));
                textFlow.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
                textFlow.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
                textFlow.setMinHeight(TextFlow.USE_COMPUTED_SIZE );
                textFlow.setMaxHeight(TextFlow.USE_COMPUTED_SIZE );
                following_vbox.getChildren().add(textFlow);
            }
    }

    public void ViewUserProfile(MouseEvent event) throws IOException {
        ImageView view = (ImageView) event.getSource();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UserProfile.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        ArrayList<User> relatives = new ArrayList<>();
        relatives.addAll(user.getFollowers());
        relatives.addAll(user.getFollowed());

        UserProfileController controller = loader.getController();
        for(User u : relatives)
            if(u.getID() == Integer.parseInt(view.getId()))
                controller.initialize(u,us,s);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        MainStage.setResizable(false);
        MainStage.setTitle("User Profile");

        MainStage.setScene(scene);
        MainStage.show();
    }


    public void follow(ActionEvent event) {
        if(!user.getFollowers().contains(us)) {
            user.getFollowers().add(us);
            us.getFollowed().add(user);


            SearchUsers();
            follow_button.setText("Unfollow");
        }

        else {
            user.getFollowers().remove(us);
            us.getFollowed().remove(user);

            SearchUsers();
            follow_button.setText("Follow");
        }
    }

    public void load_PV(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainPage.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        MainPageController mainPageController = loader.getController();
        mainPageController.initialize(us);
        mainPageController.loadPV(user);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        MainStage.close();

        s.setScene(scene);
        s.show();
    }

    public void SeePost(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainPage.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        MainPageController mainPageController = loader.getController();
        mainPageController.initialize(us);
        mainPageController.ViewMyPosts(user);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        MainStage.close();

        s.setScene(scene);
        s.show();
    }




}
