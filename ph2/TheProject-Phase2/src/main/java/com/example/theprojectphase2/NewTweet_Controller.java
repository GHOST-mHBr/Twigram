package com.example.theprojectphase2;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;


public class NewTweet_Controller {

    @FXML
    TextArea title_text;

    @FXML
    TextArea newTweet_text;

    @FXML
    ImageView image;

    User user;



    public void initialize(User u){
        for(User us : User.Users)
            if(us.getID() == u.getID()){
                user = us;
                break;
            }

        image.setVisible(false);
        image.setManaged(false);

    }

    public void newPost(ActionEvent event) throws IOException {
        Post post;
        post =  new Post(title_text.getText(), newTweet_text.getText(), user);
        post.setOwnerId(user.getID());


        if(image.getImage() != null){
            post.setImage(image.getImage());

            BufferedImage bImage = SwingFXUtils.fromFXImage(image.getImage(), null);
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", s);
            byte[] res  = s.toByteArray();
            s.close();
            String encodedFile = Base64.getEncoder().encodeToString(res);
            post.setImageString(encodedFile);
        }

        else
            post.setImageString("null");

        //post.setImage(image.getImage());


        DBManagerTester.insert(post);
        Post.Posts.add(post);
        user.getPosts().add(post);
        //Because the dataBase doesn't work for now I can't save the massage but whenever it
        // got fixed just uncomment the kine below
        //DBManager.save(post);


        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();

    }

    public void attachment(ActionEvent event){
        Window window = ((Node) (event.getSource())).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");

        image.setManaged(true);
        image.setVisible(true);

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*.gif"));

        File file = fileChooser.showOpenDialog(window);
        if(file != null){
            Image openedImage = new Image(file.toURI().toString());
            image.setImage(openedImage);
        }
        event.consume();
    }

    public void close(ActionEvent event){

    }




}
