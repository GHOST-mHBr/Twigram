package com.example.theprojectphase2;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;

public class GmessagesOptions_Controller {

    @FXML
    Button edit_button;

    @FXML
    Button delete_button;

    @FXML
    Button reply_button;

    TextFlow textFlow;
    Post post;
    User us;
    TextArea type;
    AnchorPane title;
    ImageView view;

    public void initialize(TextFlow t, Post p, User u, TextArea ta, AnchorPane a, ImageView imageView){
        textFlow = t;

        //for(Post pp : Post.Posts)
            //if(pp.getId() == p.getId())
               // post = pp;
        post = p;

        for(User uu : User.Users)
            if(uu.getID() == u.getID())
                us = uu;

        type = ta;

        title = a;

        view = imageView;


        if(!(post.getOwner().getID() == us.getID())){
            edit_button.setManaged(false);
            delete_button.setManaged(false);
            edit_button.setDisable(true);
            edit_button.setVisible(false);
            delete_button.setDisable(true);
            delete_button.setVisible(false);
        }


    }

    public void Copy(ActionEvent event1){
        VBox vBox1 = (VBox) textFlow.getChildren().get(0);
        Label label = (Label) vBox1.getChildren().get(1);
        String copyText = label.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(copyText);
        clipboard.setContent(content);

        Popup popup = (Popup) ((Node)event1.getSource()).getScene().getWindow();
        popup.hide();
    }

    public void Delete(ActionEvent event1) throws IllegalAccessException {

        if(post.getOwner().equals(us)){
            if(Comment.Comments.contains(post)){
                for(Post p : Post.Posts)
                    p.getComments().remove(post);
                DBManagerTester.deleteRecordIfExist(post);
                VBox vBox = (VBox)(textFlow.getParent().getParent());
                vBox.getChildren().remove(textFlow.getParent());


            }

            else{
                for (Group group : Group.Groups)
                    if (group.getPosts().contains(post)) {
                        group.getPosts().remove(post);
                        DBManagerTester.deleteRecordIfExist(post);
                        VBox vBox = (VBox) (textFlow.getParent().getParent());
                        vBox.getChildren().remove(textFlow.getParent());

                    }
            }
        }


        Popup popup = (Popup) ((Node)event1.getSource()).getScene().getWindow();
        popup.hide();
    }

    public void Edit(ActionEvent event1){
        VBox vBox = (VBox)(textFlow.getParent().getParent());
        type.setDisable(false);
        title.setManaged(true);
        title.setVisible(true);
        title.setDisable(false);
        title.getChildren().get(0).setDisable(true);
        title.getChildren().get(0).setVisible(false);
        title.getChildren().get(1).setDisable(true);
        title.getChildren().get(1).setVisible(false);
        Button confirm = new Button("Confirm");
        Button decline = new Button("Decline");
        title.getChildren().add(decline);
        title.getChildren().add(confirm);
        AnchorPane.setTopAnchor(decline,10.0);
        AnchorPane.setLeftAnchor(decline,10.0);
        AnchorPane.setTopAnchor(confirm,10.0);
        AnchorPane.setRightAnchor(confirm,10.0);
        VBox vBox1 = (VBox) textFlow.getChildren().get(0);
        Label label;

        if( vBox1.getChildren().size() > 2 && vBox1.getChildren().get(2).getClass().getSimpleName().equals("Label")) {
            label = (Label) vBox1.getChildren().get(2);
        }

        else {
            label = (Label) vBox1.getChildren().get(1);
        }
        String content = label.getText();


        type.setText(content);
        //textArea.setEditable(true);
        confirm.setOnAction(event -> {
            label.setText(type.getText());
            if(!textFlow.getId().contains("c")) {
                for (Post post : Post.Posts)
                    if (post.getId() == Integer.parseInt(textFlow.getId())) {
                        post.setContext(type.getText());
                    }
            }

            else{
                for (Comment comment : Comment.Comments)
                    if (comment.getId() == Integer.parseInt(textFlow.getId().substring(0,textFlow.getId().length()-1))) {
                        comment.setContext(type.getText());
                    }
            }

            type.clear();
            title.getChildren().get(0).setDisable(false);
            title.getChildren().get(0).setVisible(true);
            title.getChildren().get(1).setDisable(false);
            title.getChildren().get(1).setVisible(true);
            title.getChildren().remove(confirm);
            title.getChildren().remove(decline);
        });

        decline.setOnAction(event -> {
            label.setText(content);
            type.clear();
            title.getChildren().get(0).setDisable(false);
            title.getChildren().get(0).setVisible(true);
            title.getChildren().get(1).setDisable(false);
            title.getChildren().get(1).setVisible(true);
            title.getChildren().remove(confirm);
            title.getChildren().remove(decline);
        });

        Popup popup = (Popup) ((Node)event1.getSource()).getScene().getWindow();
        popup.hide();


    }

    public Button getButton(){
        return reply_button;
    }



}
