package com.example.theprojectphase2;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.joda.time.format.DateTimeFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainPageController {

    @FXML
    Label chat_msg;

    @FXML
    VBox chat_box;

    @FXML
    VBox group_vbox;

    @FXML
    VBox group_vbox1;

    @FXML
    VBox pv_vbox;

    @FXML
    ButtonBar button_bar;

    @FXML
    TabPane the_list;

    @FXML
    TextArea main_type;

    @FXML
    Button send_button;

    @FXML
    ImageView title_image;

    @FXML
    Label title_label;

    @FXML
    AnchorPane title;

    @FXML
    VBox setting;

    @FXML
    ImageView main_image;

    @FXML
    Label main_label;

    @FXML
    Button myposts_button;

    @FXML
    Button followingButton;

    @FXML
    Button followerButton;

    @FXML
    Button search_button;

    @FXML
    TextField search_field;

    @FXML
    Button newGroup_button;

    @FXML
    ScrollPane scroll_bar;

    @FXML
    Button attachment;

    @FXML
    Button discover_people;

    @FXML
    Button changeAccount_button;

    @FXML
    Button logOut_button;

    boolean isInGroup;
    boolean isInPv;
    ToggleSwitch toggleSwitch;

    SimpleBooleanProperty isDark;

    User user; //The user that's logged in his/her account

    ImageView view = new ImageView();


    public void initialize(User u){
        //Initializes the page
        for(User us : User.Users)
           if(us.getID() == u.getID()){
               user = us;
               break;
           }

        toggleSwitch = new ToggleSwitch();
        isDark = toggleSwitch.switchOnProperty();

        //title.setManaged(false);
        title.setVisible(false);

        title_image.setOnMouseClicked(event -> {

            if(isInGroup && !isInPv){
                try {
                    ViewGroupProfile(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if(!isInGroup && isInPv){
                try {
                    ViewUserProfile(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        main_label.setText(user.getUserName()+"\n"+user.getFollowers().size()+" Followers"+"\n"+user.getFollowed().size()+" Followings");
        main_label.setStyle("-fx-font-size: 15;");
        main_image.setId(String.valueOf(user.getID()));
        main_image.setOnMouseClicked(event -> {
            try {
                ViewUserProfile(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Circle circle = new Circle(40);
        circle.setTranslateX(50);
        circle.setTranslateY(45);
        main_image.setClip(circle);
        main_image.setImage(user.getImage());

        Circle circle1 = new Circle(30);
        circle1.setTranslateX(30);
        circle1.setTranslateY(30);
        title_image.setClip(circle1);


        loadData(user);

        myposts_button.setOnAction(event -> ViewMyPosts(user));

        attachment.setOnAction(event -> {
            view.setImage(attachment(event));
        });


        setting.setManaged(false);
        main_image.setManaged(false);
        main_label.setManaged(false);
        myposts_button.setManaged(false);
        followerButton.setManaged(false);
        followingButton.setManaged(false);
        newGroup_button.setManaged(false);
        discover_people.setManaged(false);
        changeAccount_button.setManaged(false);
        logOut_button.setManaged(false);

        setting.setDisable(true);
        main_image.setDisable(true);
        main_label.setDisable(true);
        myposts_button.setDisable(true);
        followerButton.setDisable(true);
        followingButton.setDisable(true);
        newGroup_button.setDisable(true);
        discover_people.setDisable(true);
        changeAccount_button.setDisable(true);
        logOut_button.setDisable(true);

        myposts_button.setVisible(false);
        followerButton.setVisible(false);
        followingButton.setVisible(false);
        main_label.setVisible(false);
        main_image.setVisible(false);
        newGroup_button.setVisible(false);
        discover_people.setVisible(false);
        changeAccount_button.setVisible(false);
        logOut_button.setVisible(false);

        search_button.setOnAction(event -> loadData(user));



        toggleSwitch.setManaged(false);
        toggleSwitch.setDisable(true);
        toggleSwitch.setVisible(false);

        setting.getChildren().add(toggleSwitch);
        title.setStyle("-fx-background-color: rgb(180,216,225)");
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                toggleSwitch.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                title.setStyle("-fx-background-color: #373e43;");
            }

            else {
                toggleSwitch.getScene().getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                title.setStyle("-fx-background-color: rgb(180,216,225)");
            }
        });


    }



    private void loadData (User u) {
        //loads date when loging in

        group_vbox.getChildren().clear();
        group_vbox1.getChildren().clear();
        pv_vbox.getChildren().clear();

        ArrayList<TextFlow> pvs = new ArrayList<>();
        ArrayList<TextFlow> groups = new ArrayList<>();
        ArrayList<TextFlow> chats = new ArrayList<>();

        if (search_field.getText().isEmpty()){

            for (Group g : u.getGroups()) {

                TextFlow container = loadGroup(g);
                TextFlow container1 = loadGroup(g);

                //group_vbox.getChildren().add(container);
                //group_vbox1.getChildren().add(container);
                groups.add(container);
                chats.add(container1);


            }

            ArrayList<User> messengers = new ArrayList<>();

            for (Post post : user.getReceivedMessages())
              if(!messengers.contains(post.getOwner()))
               messengers.add(post.getOwner());

           for (User uu : User.Users) {
              for (Post p : uu.getReceivedMessages())
                   if (p.getOwner().getID() == user.getID() && !messengers.contains(uu))
                    messengers.add(uu);
           }

           for (User user1 : messengers) {
               TextFlow container = loadPV(user1);
               TextFlow container1 = loadPV(user1);
               //group_vbox.getChildren().add(container);
               pvs.add(container);
               chats.add(container1);

           }

      }

        else {

            for (Group g : u.getGroups()) {
                if(g.getGroupName().contains(search_field.getText())) {
                   TextFlow container = loadGroup(g);
                   TextFlow container1 = loadGroup(g);
                    //group_vbox.getChildren().add(container);
                    //group_vbox1.getChildren().add(container);
                    chats.add(container);
                    groups.add(container1);

                }
            }

            for (User user1 : User.Users) {
                if(user1.getUserName().contains(search_field.getText()) && user1.getID() != user.getID()) {
                    TextFlow container = loadPV(user1);
                    TextFlow container1 = loadPV(user1);
                    //group_vbox.getChildren().add(container);
                    pvs.add(container);
                    chats.add(container1);
                }
            }

            search_field.setText("");
        }

        Collections.sort(chats, (o1, o2) -> {
            if (o1.getChildren().get(1).getId() == null || o2.getChildren().get(1).getId() == null)
                return 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int ans = 0;
            try {
                ans =  sdf.parse(o1.getChildren().get(1).getId()).compareTo(sdf.parse(o2.getChildren().get(1).getId()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return ans;
        });

        Collections.sort(pvs, (o1, o2) -> {
            if (o1.getChildren().get(1).getId() == null || o2.getChildren().get(1).getId() == null)
                return 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int ans = 0;
            try {
                ans =  sdf.parse(o1.getChildren().get(1).getId()).compareTo(sdf.parse(o2.getChildren().get(1).getId()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return ans;
        });

        Collections.sort(groups, (o1, o2) -> {
            if (o1.getChildren().get(1).getId() == null || o2.getChildren().get(1).getId() == null)
                return 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int ans = 0;
            try {
                ans =  sdf.parse(o1.getChildren().get(1).getId()).compareTo(sdf.parse(o2.getChildren().get(1).getId()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return ans;
        });

        Collections.reverse(groups);
        Collections.reverse(pvs);
        Collections.reverse(chats);

        for(TextFlow t : chats)
            group_vbox.getChildren().add(t);


        for(TextFlow t : pvs)
            pv_vbox.getChildren().add(t);


        for (TextFlow t : groups)
            group_vbox1.getChildren().add(t);

    }

    public TextFlow loadGroup(Group g){
        TextFlow textFlow = new TextFlow();

        TextFlow container = new TextFlow();

        ImageView profileImage = new ImageView(g.getImage());
        Circle circle = new Circle(20);
        circle.setTranslateX(30);
        circle.setTranslateY(30);
        profileImage.setClip(circle);
        profileImage.setId(String.valueOf(g.getID()));
        profileImage.setFitWidth(50);
        profileImage.setFitHeight(50);
        profileImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    ViewGroupProfile(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Label text;
        if (g.getPosts().isEmpty())
            text = new Label(g.getGroupName() + "\n" + "No Chat Here");

        else {
            if(g.getPosts().get(g.getPosts().size()-1).getSeens().containsKey(String.valueOf(user.getID()))){
                if (g.getPosts().get(g.getPosts().size() - 1).getContext().length() > 10)
                    text = new Label(g.getGroupName() + "\n" + g.getPosts().get(g.getPosts().size() - 1).getContext().substring(0, 10) + "...");

                else
                    text = new Label(g.getGroupName() + "\n" + g.getPosts().get(g.getPosts().size() - 1).getContext());
            }

            else {
                if (g.getPosts().get(g.getPosts().size() - 1).getContext().length() > 10) {
                    text = new Label(g.getGroupName() + "\n" + g.getPosts().get(g.getPosts().size() - 1).getContext().substring(0, 10) + "...");

                }

                else
                    text = new Label(g.getGroupName() + "\n" + g.getPosts().get(g.getPosts().size() - 1).getContext());

                text.setStyle("-fx-font-weight: bold;");
            }
        }

        textFlow.setTranslateY(-profileImage.getFitHeight() / 2);
        container.getChildren().add(profileImage);
        textFlow.getChildren().add(text);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(!g.getPosts().isEmpty())
            textFlow.setId(g.getPosts().get(g.getPosts().size()-1).getPublishDate().format(formatter));

        else
            textFlow.setId(LocalDateTime.now().format(formatter));

        container.setId(String.valueOf(g.getID()));
        textFlow.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMinHeight(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxHeight(TextFlow.USE_COMPUTED_SIZE);


        container.setOnMouseClicked(this::ChooseGroupChat);


        //container.setStyle("-fx-background-color: rgb(255,255,255);");

        container.setPrefHeight(60);
        container.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        container.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        container.setMinHeight(TextFlow.USE_PREF_SIZE);
        container.setMaxHeight(TextFlow.USE_COMPUTED_SIZE);

        textFlow.setTranslateX(10);
        container.getChildren().add(textFlow);

        container.setStyle("-fx-background-color: rgb(200,200,200)");
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                toggleSwitch.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                container.setStyle("-fx-background-color: #373e43;");
            }

            else {
                toggleSwitch.getScene().getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                container.setStyle("-fx-background-color: rgb(200,200,200)");
            }
        });

        return container;
    }


    @FXML
    private void setChat(MouseEvent event){
        //This method is called whenever we want to change the tabs
        //of the tab pane
        //Most likely only will be used with the "New" tab because other tabs don't need immediate filling


        if(the_list.getSelectionModel().getSelectedItem().getText().equals("New")){
            chat_box.getChildren().retainAll(title);
            title.setVisible(false);
            title.setManaged(false);
            //This part is called when we choose the new tab in the tab pane
            //It gets the new posts of the user's followings and sorts them based on their time
            //Then shows the newest ones first
            ArrayList<Post> newPosts = new ArrayList<>();

            for(User u : user.getFollowed()){
                newPosts.addAll(u.getPosts());}

            for(Post p : user.getLikedPosts())
                for(Post pp : p.getOwner().getPosts())
                    if(!newPosts.contains(pp))
                        newPosts.add(pp);


            Collections.sort(newPosts);
            //not sure if it sorted it ascending or descending. if didn't get
            //wanted results, just uncomment the line below
            //Collections.reverse(newPosts);
            main_type.clear();
            main_type.setDisable(true);
            send_button.setDisable(true);
            attachment.setDisable(true);

            for(Post post : newPosts){
                loadPost(post);
            }

        }

        else {
            loadData(user);
        }
    }


    public void ViewMyPosts(User u){
        chat_box.getChildren().retainAll(title);
        title.setVisible(false);
        title.setManaged(false);


        ArrayList<Post> newPosts = new ArrayList<>(u.getPosts());



        Collections.sort(newPosts);


        //not sure if it sorted it ascending or descending. if didn't get
        //wanted results, just uncomment the line below
        Collections.reverse(newPosts);
        main_type.clear();
        main_type.setDisable(true);
        send_button.setDisable(true);
        attachment.setDisable(true);

        for(Post post : newPosts){
           loadPost(post);
        }
    }


    public void ViewComments(MouseEvent event){
        //This method is called whenever we want to view the comments of a post
        //clears the chat box and fills it with comments
        chat_box.getChildren().clear();
        Hyperlink hyperlink = (Hyperlink) event.getSource();

        title.setVisible(true);
        title.setManaged(true);
        title.setDisable(false);
        Button button = new Button("back");
        title.getChildren().add(button);
        AnchorPane.setLeftAnchor(button,20.0);
        AnchorPane.setTopAnchor(button,10.0);

        button.setOnMouseClicked(this::setChat);

        main_type.setDisable(false);
        main_type.clear();
        main_type.setPromptText("Add Comment...");
        send_button.setDisable(false);
        attachment.setDisable(false);
        send_button.setId(String.valueOf(hyperlink.getId()));
        for(Post post : Post.Posts)
            if(post.getId() == Integer.parseInt(hyperlink.getId())){
                for(Comment comment : post.getComments()){
                    loadComment(comment);
                }
                break;
            }
    }


    public void LikePost(MouseEvent event) throws IOException {
        //Called when we want to like a post
        //works with double click

        TextFlow textFlow = (TextFlow) event.getSource();

        if(event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY)
         for(Post post : Post.Posts)
            if(post.getId() == Integer.parseInt(textFlow.getId()))
              {post.like(user);
               user.getLikedPosts().add(post);
               ViewMyPosts(post.getOwner());
              }


    }


    //public void MOPtions(MouseEvent event, TextFlow textFlow, Post post) throws IOException {

        //if(event.getButton() == MouseButton.SECONDARY)
           //OpenMessageOption(event);
    //}


    public void LikeComment(MouseEvent event) throws IOException {
        //Called when we want to like a comment
        //works with double click

        TextFlow textFlow = (TextFlow) event.getSource();

        if(event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY)
            for(Comment comment : Comment.Comments)
                if(comment.getId() == Integer.parseInt(textFlow.getId()))
                   {comment.like(user);

                   textFlow.getChildren().set(0, new Text(comment.getOwner().getUserName() +"       "+"\n" +
                           comment.getContext() +"\n" + comment.getLikers().size() + "  likes"));
                   }

        //else if(event.getButton() == MouseButton.SECONDARY)
                   // OpenMessageOption(event);
    }


    public void OpenOptions(){
        setting.setManaged(!setting.isManaged());
        main_image.setManaged(!main_image.isManaged());
        main_label.setManaged(!main_label.isManaged());
        myposts_button.setManaged(!myposts_button.isManaged());
        followerButton.setManaged(!followerButton.isManaged());
        followingButton.setManaged(!followingButton.isManaged());
        newGroup_button.setManaged(!newGroup_button.isManaged());
        discover_people.setManaged(!discover_people.isManaged());
        toggleSwitch.setManaged(!toggleSwitch.isManaged());
        changeAccount_button.setManaged(!changeAccount_button.isManaged());
        logOut_button.setManaged(!logOut_button.isManaged());

        setting.setDisable(!setting.isDisable());
        main_image.setDisable(!main_image.isDisable());
        main_label.setDisable(!main_label.isDisable());
        myposts_button.setDisable(!myposts_button.isDisable());
        followerButton.setDisable(!followerButton.isDisable());
        followingButton.setDisable(!followingButton.isDisable());
        newGroup_button.setDisable(!newGroup_button.isDisable());
        discover_people.setDisable(!discover_people.isDisable());
        toggleSwitch.setDisable(!toggleSwitch.isDisable());
        changeAccount_button.setDisable(!changeAccount_button.isDisable());
        logOut_button.setDisable(!logOut_button.isDisable());

        myposts_button.setVisible(!myposts_button.isVisible());
        followerButton.setVisible(!followerButton.isVisible());
        followingButton.setVisible(!followingButton.isVisible());
        main_label.setVisible(!main_label.isVisible());
        main_image.setVisible(!main_image.isVisible());
        newGroup_button.setVisible(!newGroup_button.isVisible());
        discover_people.setVisible(!discover_people.isVisible());
        toggleSwitch.setVisible(!toggleSwitch.isVisible());
        changeAccount_button.setVisible(!changeAccount_button.isVisible());
        logOut_button.setVisible(!logOut_button.isVisible());
        //setting.setVisible(true);

    }


    private void ChoosePVChat(MouseEvent event, ArrayList<Post> messages) {
        TextFlow chosen = (TextFlow) event.getSource();
        title.setVisible(true);
        title.setManaged(true);
        chat_box.setVisible(true);
        chat_box.setDisable(false);
        send_button.setDisable(false);
        attachment.setDisable(false);
        isInGroup = false;
        isInPv = true;


        chat_box.getChildren().retainAll(title);

        for (User u : User.Users)
            if (u.getID() == Integer.parseInt(chosen.getId())){

                send_button.setId(String.valueOf(u.getID()));
                main_type.setDisable(false);
                main_type.setPromptText("Type New Massage...");
                title_label.setText(u.getUserName());
                title_image.setId(chosen.getId());
                title_image.setImage(u.getImage());

                for (Post post : messages) {
                   loadGroupMessage(post);
                }
      }
    }


    public void ChooseGroupChat(MouseEvent event){
            //This method is called whenever we want to choose a group
            //it basically loads the chat box with group messages and enables us
            //to send messages in the said group

            TextFlow chosen = (TextFlow) event.getSource();
            title.setVisible(true);
            title.setManaged(true);
            chat_box.setVisible(true);
            chat_box.setDisable(false);
            send_button.setDisable(false);
            attachment.setDisable(false);

            isInGroup = true;
            isInPv = false;


            chat_box.getChildren().retainAll(title);

            for (Group g : user.getGroups())
                if (g.getID() == Integer.parseInt(chosen.getId())) {
                    main_type.setDisable(false);
                    main_type.setPromptText("Type New Massage...");
                    title_label.setText(g.getGroupName()+"\n"+g.getMembers().size()+" members");
                    title_image.setId(chosen.getId());
                    title_image.setImage(g.getImage());

                    send_button.setId(String.valueOf(g.getID()));

                    ArrayList<Post> posts = new ArrayList<>(g.getPosts());

                   Collections.sort(posts);

                    if (!posts.isEmpty())
                        for (Post post : posts) {
                            loadGroupMessage(post);
                            main_type.clear();
                        }


                 break;

                }
    }


    public void ViewGroupProfile(MouseEvent event) throws IOException {
        ImageView view = (ImageView) event.getSource();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ProfileGroup.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        if(isDark.getValue())
            scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                scene.getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });

        ProfileGroupController controller = loader.getController();
        for(Group g : user.getGroups())
            if(g.getID() == Integer.parseInt(view.getId()))
               controller.initialize(g,user, (Stage) view.getScene().getWindow());

        Stage MainStage = new Stage();
        MainStage.setResizable(false);
        MainStage.setTitle("Group Profile");

        MainStage.setScene(scene);
        MainStage.show();
    }


    public void Send() throws IOException {
        //This method will be called whenever we want to send a message to PV,
        //Group, or we want to add a Comment to a post


        //This first part is for group messages
        //if(the_list.getSelectionModel().getSelectedItem().getText().equals("Groups")) {


        if(!send_button.getId().contains("-")) {
            if (isInGroup && !isInPv) {
                for (Group g : user.getGroups())
                    if (g.getID() == Integer.parseInt(send_button.getId()) && (!main_type.getText().isEmpty() || view.getImage() != null)) {

                        Post post = new Post("", main_type.getText(), user);
                        post.setOwnerId(user.getID());


                        if (view.getImage() == null)
                            post.setImageString("null");

                        else {
                            post.setImage(view.getImage());

                            BufferedImage bImage = SwingFXUtils.fromFXImage(view.getImage(), null);
                            ByteArrayOutputStream s = new ByteArrayOutputStream();
                            ImageIO.write(bImage, "png", s);
                            byte[] res = s.toByteArray();
                            s.close();
                            String encodedFile = Base64.getEncoder().encodeToString(res);
                            post.setImageString(encodedFile);
                            view.setImage(null);
                        }

                        DBManagerTester.insert(post);


                        loadGroupMessage(post);
                        g.getPosts().add(post);
                        Post.Posts.add(post);


                        group_vbox.getChildren().clear();
                        loadData(user);

                        //Because the dataBase doesn't work for now I can't save the massage but whenever it
                        // got fixed just uncomment the kine below
                        //DBManager.save(post);
                        break;
                    }
            }
            else if (isInPv && !isInGroup) {
                for (User u : User.Users)
                    if (u.getID() == Integer.parseInt(send_button.getId()) && (!main_type.getText().isEmpty() || view.getImage() != null)) {

                        Post post = new Post("", main_type.getText(), user);
                        post.setOwnerId(user.getID());

                        if (view.getImage() == null)
                            post.setImageString("null");

                        else {
                            post.setImage(view.getImage());

                            BufferedImage bImage = SwingFXUtils.fromFXImage(view.getImage(), null);
                            ByteArrayOutputStream s = new ByteArrayOutputStream();
                            ImageIO.write(bImage, "png", s);
                            byte[] res = s.toByteArray();
                            s.close();
                            String encodedFile = Base64.getEncoder().encodeToString(res);
                            post.setImageString(encodedFile);
                            view.setImage(null);
                        }

                        DBManagerTester.insert(post);

                        loadGroupMessage(post);
                        u.getReceivedMessages().add(post);
                        Post.Posts.add(post);

                        group_vbox.getChildren().clear();
                        loadData(user);

                        //Because the dataBase doesn't work for now I can't save the massage but whenever it
                        // got fixed just uncomment the kine below
                        //DBManager.save(post);
                        break;
                    }
            }
            else if(!isInGroup) {
                    for (Post post : Post.Posts) {
                        if (post.getId() == Integer.parseInt(send_button.getId())) {
                            if (!main_type.getText().isEmpty()) {
                                Comment comment = new Comment(main_type.getText(), user);
                                comment.setOwnerId(user.getID());

                                comment.setImageString("null");

                                DBManagerTester.insert(comment);

                                post.getComments().add(comment);
                                Comment.Comments.add(comment);
                                main_type.clear();

                                TextFlow t = loadComment(comment);
                                t.setTranslateX(40);
                                int index = 0;
                                for (Node n : chat_box.getChildren())
                                    if (n.getId().equals(String.valueOf(post.getId())))
                                        index = chat_box.getChildren().indexOf(n);

                                if(index + post.getComments().size() > chat_box.getChildren().size())
                                    chat_box.getChildren().add(t);

                                else
                                    chat_box.getChildren().add(index + post.getComments().size() , t);

                            }
                        }
                    }
                }
        }

        else {
            if (isInGroup && !isInPv) {
                for (Group g : user.getGroups())
                    if (g.getID() == Integer.parseInt(send_button.getId().substring(0,send_button.getId().indexOf("-"))) && (!main_type.getText().isEmpty() || view.getImage() != null)) {

                        Post post = new Post("", main_type.getText(), user);
                        post.setOwnerId(user.getID());


                        if (view.getImage() == null)
                            post.setImageString("null");

                        else {
                            post.setImage(view.getImage());

                            BufferedImage bImage = SwingFXUtils.fromFXImage(view.getImage(), null);
                            ByteArrayOutputStream s = new ByteArrayOutputStream();
                            ImageIO.write(bImage, "png", s);
                            byte[] res = s.toByteArray();
                            s.close();
                            String encodedFile = Base64.getEncoder().encodeToString(res);
                            post.setImageString(encodedFile);
                            view.setImage(null);
                        }

                        DBManagerTester.insert(post);


                        for(Post p : Post.Posts)
                            if(p.getId() == Integer.parseInt(send_button.getId().substring(send_button.getId().indexOf("-")+1)))
                                p.getReplies().add(post);


                        loadGroupMessage(post);
                        g.getPosts().add(post);
                        Post.Posts.add(post);


                        group_vbox.getChildren().clear();
                        loadData(user);

                        //Because the dataBase doesn't work for now I can't save the massage but whenever it
                        // got fixed just uncomment the kine below
                        //DBManager.save(post);
                        break;
                    }
            }
            else if (isInPv && !isInGroup) {
                for (User u : User.Users)
                    if (u.getID() == Integer.parseInt(send_button.getId().substring(0,send_button.getId().indexOf("-"))) && (!main_type.getText().isEmpty() || view.getImage() != null)) {

                        Post post = new Post("", main_type.getText(), user);
                        post.setOwnerId(user.getID());

                        if (view.getImage() == null)
                            post.setImageString("null");

                        else {
                            post.setImage(view.getImage());

                            BufferedImage bImage = SwingFXUtils.fromFXImage(view.getImage(), null);
                            ByteArrayOutputStream s = new ByteArrayOutputStream();
                            ImageIO.write(bImage, "png", s);
                            byte[] res = s.toByteArray();
                            s.close();
                            String encodedFile = Base64.getEncoder().encodeToString(res);
                            post.setImageString(encodedFile);
                            view.setImage(null);
                        }

                        DBManagerTester.insert(post);

                        for(Post p : Post.Posts)
                            if(p.getId() == Integer.parseInt(send_button.getId().substring(send_button.getId().indexOf("-")+1)))
                                p.getReplies().add(post);

                        loadGroupMessage(post);
                        u.getReceivedMessages().add(post);
                        Post.Posts.add(post);

                        group_vbox.getChildren().clear();
                        loadData(user);

                        //Because the dataBase doesn't work for now I can't save the massage but whenever it
                        // got fixed just uncomment the kine below
                        //DBManager.save(post);
                        break;
                    }
            }
            else if(!isInGroup){
                for (Post post : Post.Posts) {
                    if (post.getId() == Integer.parseInt(send_button.getId().substring(0,send_button.getId().indexOf("-")))) {
                        if (!main_type.getText().isEmpty()) {
                            Comment comment = new Comment(main_type.getText(), user);
                            comment.setOwnerId(user.getID());

                            comment.setImageString("null");

                            DBManagerTester.insert(comment);

                            for(Comment c : Comment.Comments)
                                if(c.getId() == Integer.parseInt(send_button.getId().substring(send_button.getId().indexOf("-")+1)))
                                    c.getComments().add(comment);

                            post.getComments().add(comment);
                            Comment.Comments.add(comment);
                            main_type.clear();

                            TextFlow t = loadComment(comment);
                            t.setTranslateX(40);
                            int index = 0;
                            for (Node n : chat_box.getChildren())
                                if (n.getId().equals(send_button.getId()))
                                    index = chat_box.getChildren().indexOf(n);

                            if(index + post.getComments().size() > chat_box.getChildren().size())
                                chat_box.getChildren().add(t);

                            else
                                chat_box.getChildren().add(index + post.getComments().size() , t);
                        }

                    }
                }
            }

        }


        //}

       //This second part is about adding comments


       main_type.clear();

    }


    public void Post(ActionEvent actionEvent) throws IOException {
        //This opens the new tweet page to maje a new tweet
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NewTweet.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        if(isDark.getValue())
            scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                scene.getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });

        NewTweet_Controller tweetController = loader.getController();
        tweetController.initialize(user);

        Stage MainStage = new Stage();
        MainStage.setTitle("New Tweet");

        MainStage.setScene(scene);
        MainStage.show();


    }


    public void ViewUserProfile(MouseEvent event) throws IOException {
        ImageView view = (ImageView) event.getSource();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UserProfile.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        if(isDark.getValue())
            scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                scene.getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });

        UserProfileController controller = loader.getController();
        for(User u : User.Users)
            if(u.getID() == Integer.parseInt(view.getId()))
                controller.initialize(u,user, (Stage) view.getScene().getWindow());

        Stage MainStage = new Stage();
        MainStage.setResizable(false);
        MainStage.setTitle("User Profile");

        MainStage.setScene(scene);
        MainStage.show();
    }


    public void OpenMessageOption(MouseEvent event, String s) throws IOException {
        TextFlow textFlow = (TextFlow) event.getSource();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(s));
        Parent parent = loader.load();

        Popup popup = new Popup();
        if(isDark.getValue())
            popup.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                popup.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                popup.getScene().getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });
        //Scene scene = new Scene(parent);

        if(s.equals("MessageOptionsMenu.fxml")) {
            MessageOptions_Controller controller = loader.getController();
            for (Post p : Post.Posts)
                if (p.getId() == Integer.parseInt(textFlow.getId())) {
                    controller.initialize(textFlow, p, user, main_type,title);

                    break;
                }
        }

        else if(s.equals("gmessages_options.fxml")){
            GmessagesOptions_Controller controller = loader.getController();
            if(!textFlow.getId().contains("c")){

                for (Post p : Post.Posts)
                    if (p.getId() == Integer.parseInt(textFlow.getId())) {
                        controller.getButton().setOnAction(event1 -> {
                            send_button.setId( send_button.getId() + "-" + p.getId() );
                            popup.hide();
                            main_type.setPromptText("Type Your Reply to "+p.getOwner().getUserName()+"'s Message...");
                        });
                        controller.initialize(textFlow, p, user, main_type, title, view);
                        break;
                    }
            }

            else{
                controller.getButton().setDisable(true);
                controller.getButton().setVisible(false);
                controller.getButton().setManaged(false);
                for (Comment c : Comment.Comments)
                    if (c.getId() == Integer.parseInt(textFlow.getId().substring(0,textFlow.getId().length()-1))) {
                        controller.initialize(textFlow, (Post) c, user, main_type, title, view);
                        break;
                    }
            }


        }

        double x = textFlow.getScene().getWindow().getX();
        double y = textFlow.getScene().getWindow().getY();

        popup.setX(x + event.getSceneX());
        popup.setY(y + event.getSceneY());

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


    public void loadGroupMessage(Post post){
        TextFlow textFlow = new TextFlow();
        TextFlow container = new TextFlow();
        VBox vBox = new VBox();

        ImageView profileImage = new ImageView(post.getOwner().getImage());
        Circle circle = new Circle(20);
        circle.setTranslateX(30);
        circle.setTranslateY(30);
        profileImage.setClip(circle);
        profileImage.setId(String.valueOf(post.getOwner().getID()));
        profileImage.setFitHeight(50);
        profileImage.setFitWidth(50);
        profileImage.setOnMouseClicked(event -> {
            try {
                ViewUserProfile(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //this part is for post images
        ImageView view = null;
        if(post.image != null) {
            view = new ImageView(post.image);
            view.setFitHeight(view.getImage().getHeight()/4);
            view.setFitWidth(view.getImage().getWidth()/4);
            view.setTranslateX(10);
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Label text1 = new Label(post.getOwner().getUserName() + "        " + post.publishDate.format(formatter));
        text1.setStyle("-fx-text-fill: rgb(150,150,150);" + "-fx-font-size: 13");

        ArrayList<User> watchers = new ArrayList<>();
        for(String i : post.getSeens().keySet()){
            for(User uu : User.Users)
                if(uu.getID() == Integer.parseInt(i))
                    watchers.add(uu);
        }

        vBox.getChildren().add(text1);
        container.setId(String.valueOf(post.getId()));
        textFlow.setId(String.valueOf(post.getId()));

        Label text2 = new Label(post.getContext());

        if(!watchers.contains(user))
            text2.setStyle("-fx-font-size: 14;" + "-fx-font-weight: bold;");

        else
            text2.setStyle("-fx-font-size: 14;");

        container.setStyle("-fx-background-color: transparent");
        setStyle(textFlow,post);


       //replies
       for(Post p : Post.Posts)
         if(p.getReplies().contains(post)){

             Label label = new Label("   "+p.getOwner().getUserName() +"\n"+ "   "+p.getContext());

             if(p.getContext().length() > 7)
                 label.setText(p.getOwner().getUserName() +"\n"+ p.getContext().substring(0,7) + "...");


             if(post.getOwner().getID() == user.getID())
                 label.setStyle("-fx-font-size: 12;"+"-fx-background-color: rgb(120,173,192);"+"-fx-text-fill: rgb(84,82,82);");

             else
                 label.setStyle("-fx-font-size: 12;"+"-fx-background-color: rgb(111,113,115);"+"-fx-text-fill: rgb(49,48,48);");


             label.setMaxWidth(Double.MAX_VALUE);


             label.setOnMouseClicked(event -> {
                 for(Node t : chat_box.getChildren())
                     if(t.getId().equals(String.valueOf(p.getId()))){
                         final  Bounds viewBounds = scroll_bar.getViewportBounds();
                          scroll_bar.setVvalue(t.getLayoutY()/viewBounds.getHeight());
                     }
             });

             vBox.getChildren().add(label);

         }



        textFlow.setTranslateY(-profileImage.getFitHeight()/2);

        text1.setTranslateX(10);
        text2.setTranslateX(10);


        vBox.getChildren().add(text2);

        Text t1 = new Text(text1.getText());
        Text t2 = new Text(text2.getText());
        vBox.setMinWidth(Math.max(t1.getLayoutBounds().getWidth()+50,t2.getLayoutBounds().getWidth()+50) );
        //textFlow.setPrefWidth(Math.max(t1.getLayoutBounds().getWidth(),t2.getLayoutBounds().getWidth()) +30);

        if(post.image != null) {
            vBox.getChildren().add(view);
            vBox.setMinWidth(Math.max(t2.getLayoutBounds().getWidth()+50,view.getFitWidth()+50) );
        }



        //textFlow.setPrefWidth(text.getWidth()+100);
        textFlow.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMinHeight(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxHeight(TextFlow.USE_COMPUTED_SIZE);


        textFlow.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                try {
                    OpenMessageOption(event,"gmessages_options.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });





        /*
        if(isDark.getValue())
            vBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

        else
            vBox.setStyle("-fx-background-radius: 15;" + "-fx-background-color: rgb(200,200,200);");

        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                vBox.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                vBox.setStyle("-fx-background-color: #373e43;" + "-fx-background-radius: 15;");
            }

            else {
                vBox.getScene().getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                vBox.setStyle("-fx-background-radius: 15;" + "-fx-background-color: rgb(200,200,200);");
            }
        });*/


        VBox.setMargin(textFlow, new Insets(20, 0, 0, 10));
        textFlow.getChildren().add(vBox);

        if(post.getOwner().getID() == user.getID()){
            container.getChildren().add(textFlow);
            container.getChildren().add(profileImage);
            container.setTextAlignment(TextAlignment.RIGHT);
            profileImage.setTranslateX(-10);
            textFlow.setTranslateX(-10);
        }

        else {
            container.getChildren().add(profileImage);
            container.getChildren().add(textFlow);
            container.setTextAlignment(TextAlignment.LEFT);
            textFlow.setTranslateX(20);
        }


        chat_box.getChildren().add(container);


        ArrayList<Node> nodes = new ArrayList<>();
        for (Node n : ((VBox) (scroll_bar.getContent())).getChildren() ) {
            TextFlow t = (TextFlow) n;
            if(t.getChildren().get(0).getClass().getSimpleName().equals("TextFlow"))
                nodes.add(t.getChildren().get(0));

            else
                nodes.add(t.getChildren().get(1));
        }

        List<Node> visibleNodes = new ArrayList<>();

        Node nn = ((VBox) (scroll_bar.getContent())).getChildren().get(0);
        TextFlow tt = (TextFlow) nn;
        if(tt.getChildren().get(0).getClass().getSimpleName().equals("TextFlow"))
            visibleNodes.add(tt.getChildren().get(0));

        else
            visibleNodes.add(tt.getChildren().get(1));



        Bounds paneBounds = scroll_bar.localToScene(scroll_bar.getBoundsInParent());
        if (scroll_bar.getContent() instanceof Parent) {
            for (Node n : nodes ) {
                Bounds nodeBounds = n.localToScene(n.getBoundsInLocal());
                if (paneBounds.intersects(nodeBounds)) {
                    visibleNodes.add(n);
                }
            }
        }

        for(Node n : visibleNodes)
            for(Post post1 : Post.Posts)
                if(n.getId().equals(String.valueOf(post1.getId())))
                {
                    TextFlow t = (TextFlow) n;
                    if(((VBox)(t.getChildren().get(0))).getChildren().size() > 2 && (((VBox)(t.getChildren().get(0))).getChildren().get(2)).getClass().getSimpleName().equals("Label"))
                        ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(2))).setStyle("-fx-font-size: 15;");

                    else
                        ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(1))).setStyle("-fx-font-size: 15;");
                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String date = LocalDateTime.now().format(formatter1);
                    post1.seens.put(String.valueOf(user.getID()),date);
                }

        scroll_bar.vvalueProperty().addListener( ( observable, oldValue, newValue ) ->
        {
            /*final  Bounds viewBounds = scroll_bar.getViewportBounds();
            final Bounds boundsOnScene = container.localToScene( container.getBoundsInLocal() );
            if(boundsOnScene.getMinY() > 0 && !watchers.contains(user))
               for(Post post1 : Post.Posts)
                   if(post1.getId() == Integer.parseInt(container.getId())){
                       text2.setStyle("-fx-font-size: 15;");
                       DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                       String date = LocalDateTime.now().format(formatter1);
                       post.seens.put(String.valueOf(user.getID()),date);
                   }*/
            ArrayList<Node> nodes1 = new ArrayList<>();
            for (Node n : ((VBox) (scroll_bar.getContent())).getChildren() ) {
               TextFlow t = (TextFlow) n;
               if(t.getChildren().get(0).getClass().getSimpleName().equals("TextFlow"))
                  nodes1.add(t.getChildren().get(0));

               else
                   nodes1.add(t.getChildren().get(1));
            }

            List<Node> visibleNodes1 = new ArrayList<>();

            Node nn1 = ((VBox) (scroll_bar.getContent())).getChildren().get(0);
            TextFlow tt1 = (TextFlow) nn1;
            if(tt1.getChildren().get(0).getClass().getSimpleName().equals("TextFlow"))
                visibleNodes1.add(tt1.getChildren().get(0));

            else
                visibleNodes1.add(tt1.getChildren().get(1));

            Bounds paneBounds1 = scroll_bar.localToScene(scroll_bar.getBoundsInParent());
            if (scroll_bar.getContent() instanceof Parent) {
                for (Node n : nodes1 ) {
                    Bounds nodeBounds1 = n.localToScene(n.getBoundsInLocal());
                    if (paneBounds1.intersects(nodeBounds1)) {
                        visibleNodes1.add(n);
                    }
                }
            }

            for(Node n : visibleNodes1)
                for(Post post1 : Post.Posts)
                    if(n.getId().equals(String.valueOf(post1.getId())))
                    {
                        TextFlow t = (TextFlow) n;
                        if(((VBox)(t.getChildren().get(0))).getChildren().size() > 2 && (((VBox)(t.getChildren().get(0))).getChildren().get(2)).getClass().getSimpleName().equals("Label"))
                            ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(2))).setStyle("-fx-font-size: 15;");

                        else
                            ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(1))).setStyle("-fx-font-size: 15;");
                        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String date = LocalDateTime.now().format(formatter1);
                        post1.seens.put(String.valueOf(user.getID()),date);

                    }
            loadData(user);
        } );

        loadData(user);
    }


    public void loadPost(Post post){
        TextFlow textFlow = new TextFlow();
        TextFlow container = new TextFlow();
        VBox vBox = new VBox();
        HBox hBox = new HBox();


        //for comment logo
        Image commentImage = new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\comment.png");
        ImageView commentLogo = new ImageView(commentImage);
        Button comment_button = new Button("");
        commentLogo.setFitWidth(commentImage.getWidth()/11);
        commentLogo.setFitHeight(commentImage.getHeight()/11);
        comment_button.setGraphic(commentLogo);
        comment_button.setStyle("-fx-background-color: transparent;");


        //for like logo
        Image likeImage = new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\like.png");
        Image likeFillImage = new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\like-fill.png");
        ImageView likeLogo = new ImageView(likeImage);
        if(post.getLikers().contains(user))
            likeLogo.setImage(likeFillImage);
        Button like_button = new Button("");
        likeLogo.setFitWidth(likeImage.getWidth()/5);
        likeLogo.setFitHeight(likeImage.getHeight()/5);
        like_button.setGraphic(likeLogo);
        like_button.setStyle("-fx-background-color: transparent;");

        Label likes = new Label(post.getLikers().size() + "  Likes");
        likes.setStyle("-fx-text-fill: red;");
        likes.setTranslateY(12);

        like_button.setOnAction(event -> {
            if(post.getLikers().contains(user)){
                post.getLikers().remove(user);
                likeLogo.setImage(likeImage);
                user.getLikedPosts().remove(post);
                likes.setText(post.getLikers().size() + "  Likes");
                post.getLikes().remove(String.valueOf(user.getID()));
            }

            else{
                post.getLikers().add(user);
                likeLogo.setImage(likeFillImage);
                user.getLikedPosts().add(post);
                likes.setText(post.getLikers().size() + "  Likes");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String date = LocalDateTime.now().format(formatter);
                post.getLikes().put(String.valueOf(user.getID()),date);
            }

        });



        ImageView profileImage = new ImageView(post.getOwner().getImage());
        Circle circle = new Circle(20);
        circle.setTranslateX(30);
        circle.setTranslateY(30);
        profileImage.setClip(circle);
        profileImage.setId(String.valueOf(post.getOwner().getID()));
        profileImage.setFitHeight(50);
        profileImage.setFitWidth(50);
        profileImage.setOnMouseClicked(event -> {
            try {
                ViewUserProfile(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //this part is for post images
        ImageView view = null;
        if(post.image != null) {
            view = new ImageView(post.image);
            view.setFitHeight((view.getImage().getHeight()*500)/view.getImage().getWidth());
            view.setFitWidth(500);
            view.setTranslateX(10);
            //(800*h)/w
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Label text1 = new Label(post.getOwner().getUserName() + "        " + post.publishDate.format(formatter));

        if(!post.getOwner().isNormal)
            text1.setText(post.getOwner().getUserName() + "        " + post.publishDate.format(formatter) + "       AD");

        text1.setStyle("-fx-text-fill: rgb(150,150,150);" + "-fx-font-size: 13");

        ArrayList<User> watchers = new ArrayList<>();
        for(String i : post.getSeens().keySet()){
            for(User uu : User.Users)
                if(uu.getID() == Integer.parseInt(i))
                    watchers.add(uu);
        }

        Label text2 = new Label(post.getTitle() +"       "+"\n" +
                post.getContext());

        if(!watchers.contains(user))
          text2.setStyle("-fx-font-size: 15;" + "-fx-font-weight: bold;");

        else
            text2.setStyle("-fx-font-size: 15;");

        textFlow.setTranslateY(-profileImage.getFitHeight()/2);
        textFlow.setTranslateX(20);
        text1.setTranslateX(10);
        text2.setTranslateX(10);


        container.getChildren().add(profileImage);
        vBox.getChildren().add(text1);
        vBox.getChildren().add(text2);

        Text t1 = new Text(text1.getText());
        Text t2 = new Text(text2.getText());
        vBox.setMinWidth(Math.max(t1.getLayoutBounds().getWidth()+50,t2.getLayoutBounds().getWidth()+50) );
            //textFlow.setPrefWidth(Math.max(t1.getLayoutBounds().getWidth(),t2.getLayoutBounds().getWidth()) +30);

        if(post.image != null) {
            vBox.getChildren().add(view);
            vBox.setMinWidth(Math.max(t2.getLayoutBounds().getWidth()+50,view.getFitWidth()+50) );
        }



        textFlow.setId(String.valueOf(post.getId()));
        container.setId(String.valueOf(post.getId()));
        //textFlow.setPrefWidth(text.getWidth()+100);
        textFlow.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMinHeight(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxHeight(TextFlow.USE_COMPUTED_SIZE);
        hBox.getChildren().add(comment_button);
        hBox.getChildren().add(like_button);
        hBox.getChildren().add(likes);

        vBox.getChildren().add(hBox);

        //Remember to turn the user.isNormal to !user.isNormal
        if(!user.isNormal && post.getOwner().getID() == user.getID()) {
            Label views = new Label(post.getSeens().size() + " views");
            views.setStyle("-fx-text-fill: rgb(150,150,150);" + "-fx-font-size: 13;");
            views.setTranslateX(10);
            vBox.getChildren().add(views);

            views.setOnMouseClicked(event -> {
                if(!post.getSeens().isEmpty())
                ShowChart(event, post.getSeens());
            });

        }

       likes.setOnMouseClicked(event -> {
           if(event.getButton() == MouseButton.PRIMARY) {
               try {
                   ShowLikers(post);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }

           if(!post.getLikes().isEmpty() && event.getButton() == MouseButton.MIDDLE && user.isNormal && post.getOwner().getID() == user.getID())
               ShowChart(event, post.getLikes());
       });

        textFlow.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                try {
                    LikePost(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if(event.getButton() == MouseButton.SECONDARY) {
                try {
                    OpenMessageOption(event,"MessageOptionsMenu.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        //hyperlink.setTranslateX(10);

        container.setStyle("-fx-background-color: transparent");
        textFlow.setStyle("-fx-background-color: transparent;");

        if(isDark.getValue())
            vBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

        else
            vBox.setStyle("-fx-background-radius: 15;" + "-fx-background-color: rgb(200,200,200);");

        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                vBox.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                vBox.setStyle("-fx-background-color: #373e43;" + "-fx-background-radius: 15;");
            }

            else {
                vBox.getScene().getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                vBox.setStyle("-fx-background-radius: 15;" + "-fx-background-color: rgb(200,200,200);");
            }
        });


        VBox.setMargin(textFlow, new Insets(20, 0, 0, 10));
        textFlow.getChildren().add(vBox);
        container.getChildren().add(textFlow);
        chat_box.getChildren().add(container);


        ArrayList<Node> nodes = new ArrayList<>();
        for (Node n : ((VBox) (scroll_bar.getContent())).getChildren() ) {
            TextFlow t = (TextFlow) n;
            nodes.add(t.getChildren().get(1));
        }

        List<Node> visibleNodes = new ArrayList<>();

        Node nn = ((VBox) (scroll_bar.getContent())).getChildren().get((((VBox) (scroll_bar.getContent())).getChildren()).size()-1);
        TextFlow tt = (TextFlow) nn;
        visibleNodes.add(tt.getChildren().get(1));



        Bounds paneBounds = scroll_bar.localToScene(scroll_bar.getBoundsInParent());
        if (scroll_bar.getContent() instanceof Parent) {
            for (Node n : nodes ) {
                Bounds nodeBounds = n.localToScene(n.getBoundsInLocal());
                if (paneBounds.intersects(nodeBounds)) {
                    visibleNodes.add(n);
                }
            }
        }

        for(Node n : visibleNodes)
            for(Post post1 : Post.Posts)
                if(n.getId().equals(String.valueOf(post1.getId())))
                {
                    TextFlow t = (TextFlow) n;
                    if(((VBox)(t.getChildren().get(0))).getChildren().size() > 2 && (((VBox)(t.getChildren().get(0))).getChildren().get(2)).getClass().getSimpleName().equals("Label"))
                        ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(2))).setStyle("-fx-font-size: 15;");

                    else
                        ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(1))).setStyle("-fx-font-size: 15;");
                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String date = LocalDateTime.now().format(formatter1);
                    if(!watchers.contains(user))
                        post1.seens.put(String.valueOf(user.getID()),date);
                    if(!user.isNormal && post.getOwner().getID() == user.getID())
                        ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(3))).setText(post.getSeens().size() + " views");
                }


        scroll_bar.vvalueProperty().addListener( ( observable, oldValue, newValue ) ->
        {
            /*final  Bounds viewBounds = scroll_bar.getViewportBounds();
            final Bounds boundsOnScene = container.localToScene( container.getBoundsInLocal() );
            if(boundsOnScene.getMinY() > 0 && !watchers.contains(user))
               for(Post post1 : Post.Posts)
                   if(post1.getId() == Integer.parseInt(container.getId())){
                       text2.setStyle("-fx-font-size: 15;");
                       DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                       String date = LocalDateTime.now().format(formatter1);
                       post.seens.put(String.valueOf(user.getID()),date);
                   }*/
            ArrayList<Node> nodes1 = new ArrayList<>();
            for (Node n : ((VBox) (scroll_bar.getContent())).getChildren() ) {
                TextFlow t = (TextFlow) n;
                nodes1.add(t.getChildren().get(1));
            }

            List<Node> visibleNodes1 = new ArrayList<>();
            Bounds paneBounds1 = scroll_bar.localToScene(scroll_bar.getBoundsInParent());
            if (scroll_bar.getContent() instanceof Parent) {
                for (Node n : nodes1 ) {
                    Bounds nodeBounds = n.localToScene(n.getBoundsInLocal());
                    if (paneBounds1.intersects(nodeBounds)) {
                        visibleNodes1.add(n);
                    }
                }
            }

            for(Node n : visibleNodes1)
                for(Post post1 : Post.Posts)
                    if(n.getId().equals(String.valueOf(post1.getId())))
                    {
                        TextFlow t = (TextFlow) n;
                        ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(1))).setStyle("-fx-font-size: 15;");
                        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String date = LocalDateTime.now().format(formatter1);
                        if(!watchers.contains(user))
                        post1.seens.put(String.valueOf(user.getID()),date);
                        if(!user.isNormal && post.getOwner().getID() == user.getID())
                            ((Label)(((VBox)(t.getChildren().get(0))).getChildren().get(3))).setText(post.getSeens().size() + " views");

                    }

        } );


        Collections.sort(post.getComments());
        for(Comment comment : post.getComments()){
            TextFlow t = loadComment(comment);
            chat_box.getChildren().add(t);
            t.setTranslateX(40);
            t.setVisible(false);
            t.setManaged(false);
            t.setDisable(true);

        }

        comment_button.setOnAction(event -> {
            send_button.setId(String.valueOf(post.getId()));
            main_type.setPromptText("Add a Comment to "+post.getOwner().getUserName()+"'s Post...");
            isInPv = false;
            isInGroup = false;
            main_type.setDisable(false);
            attachment.setDisable(true);
            send_button.setDisable(false);
            attachment.setDisable(false);

            int index = chat_box.getChildren().indexOf(container);

            ArrayList<Node> comment_containers = new ArrayList<>(chat_box.getChildren().subList(index+1, index + post.getComments().size() + 1));


            for(Node t : comment_containers){
                t.setVisible(!t.isVisible());
                t.setManaged(!t.isManaged());
                t.setDisable(!t.isDisable());
            }
        });


    }


    public void ShowChart(MouseEvent event, LinkedHashMap<String, String> map){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SeenChart.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Popup popup = new Popup();
        if(isDark.getValue())
            popup.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                popup.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                popup.getScene().getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });
        //Scene scene = new Scene(parent);


        SeenChartController controller = loader.getController();
        controller.initialize(map);

        popup.getContent().add(parent);

        popup.setAutoHide(true);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        popup.show(MainStage);
    }


    public TextFlow loadComment(Comment comment){
        TextFlow textFlow = new TextFlow();
        TextFlow container = new TextFlow();
        VBox vBox = new VBox();
        HBox hBox = new HBox();

        ImageView profileImage = new ImageView(comment.getOwner().getImage());
        Circle circle = new Circle(20);
        circle.setTranslateX(30);
        circle.setTranslateY(30);
        profileImage.setClip(circle);
        profileImage.setId(String.valueOf(comment.getOwner().getID()));
        profileImage.setFitHeight(50);
        profileImage.setFitWidth(50);
        profileImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    ViewUserProfile(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //for comment logo
        Image commentImage = new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\comment.png");
        ImageView commentLogo = new ImageView(commentImage);
        Button comment_button = new Button("");
        commentLogo.setFitWidth(commentImage.getWidth()/11);
        commentLogo.setFitHeight(commentImage.getHeight()/11);
        comment_button.setGraphic(commentLogo);
        comment_button.setStyle("-fx-background-color: transparent;");

        //for like logo
        Image likeImage = new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\like.png");
        Image likeFillImage = new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\like-fill.png");
        ImageView likeLogo = new ImageView(likeImage);
        if(comment.getLikers().contains(user))
            likeLogo.setImage(likeFillImage);
        Button like_button = new Button("");
        likeLogo.setFitWidth(likeImage.getWidth()/5);
        likeLogo.setFitHeight(likeImage.getHeight()/5);
        like_button.setGraphic(likeLogo);
        like_button.setStyle("-fx-background-color: transparent;");

        Label likes = new Label(comment.getLikers().size() + "  Likes");
        likes.setStyle("-fx-text-fill: red;");
        likes.setTranslateY(12);

        like_button.setOnAction(event -> {
            if(comment.getLikers().contains(user)){
                comment.getLikers().remove(user);
                likeLogo.setImage(likeImage);
                user.getLikedPosts().remove(comment);
                likes.setText(comment.getLikers().size() + "  Likes");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String date = LocalDateTime.now().format(formatter);
                comment.getLikes().put(String.valueOf(user.getID()),date);
            }

            else{
                comment.getLikers().add(user);
                likeLogo.setImage(likeFillImage);
                user.getLikedPosts().add(comment);
                likes.setText(comment.getLikers().size() + "  Likes");
                comment.getLikes().remove(String.valueOf(user.getID()));
            }

        });

        hBox.getChildren().add(comment_button);
        hBox.getChildren().add(like_button);
        hBox.getChildren().add(likes);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Label text1 = new Label(comment.getOwner().getUserName() + "        " + comment.publishDate.format(formatter));
        text1.setStyle("-fx-text-fill: rgb(150,150,150);" + "-fx-font-size: 13");


        Label text2 = new Label(comment.getContext());

        text2.setStyle("-fx-font-size: 14;");

        textFlow.setTranslateY(-profileImage.getFitHeight()/2);

        text1.setTranslateX(10);
        text2.setTranslateX(10);

        vBox.getChildren().add(text1);

        for(Comment c : Comment.Comments)
            if(c.getComments().contains(comment)){
                Label label = new Label("   "+c.getOwner().getUserName()+"\n"+"   "+c.getContext());
                //label.setTranslateX(10);
                if(c.getContext().length() > 7)
                    label.setText(c.getOwner().getUserName() +"\n"+ c.getContext().substring(0,7) + "...");

                if(comment.getOwner().getID() == user.getID())
                   label.setStyle("-fx-font-size: 13;"+"-fx-background-color: rgb(120,173,192);"+"-fx-text-fill: rgb(84,82,82);");

                else
                    label.setStyle("-fx-font-size: 13;"+"-fx-background-color: rgb(111,113,115);"+"-fx-text-fill: rgb(49,48,48);");

                //label.setTranslateX(10);
                label.setMaxWidth(Double.MAX_VALUE);

                label.setOnMouseClicked(event -> {
                    for(Node t : chat_box.getChildren())
                        if(t.getId().equals(String.valueOf(c.getId()))){
                            final  Bounds viewBounds = scroll_bar.getViewportBounds();
                            scroll_bar.setVvalue(t.getLayoutY()/viewBounds.getHeight());
                        }
                });

                vBox.getChildren().add(label);


            }

        vBox.getChildren().add(text2);
        vBox.getChildren().add(hBox);

        Text t1 = new Text(text1.getText());
        Text t2 = new Text(text2.getText());
        vBox.setMinWidth(Math.max(t1.getLayoutBounds().getWidth()+50,t2.getLayoutBounds().getWidth()+50) );
        //textFlow.setPrefWidth(Math.max(t1.getLayoutBounds().getWidth(),t2.getLayoutBounds().getWidth()) +30);



        textFlow.setId(String.valueOf(comment.getId()) + "c");
        container.setId(String.valueOf(comment.getId()));
        //textFlow.setPrefWidth(text.getWidth()+100);
        textFlow.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMinHeight(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxHeight(TextFlow.USE_COMPUTED_SIZE);


        textFlow.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                try {
                    OpenMessageOption(event,"gmessages_options.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        container.setStyle("-fx-background-color: transparent");
        setStyle(textFlow,comment);


        /*
        if(isDark.getValue())
            vBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

        else
            vBox.setStyle("-fx-background-radius: 15;" + "-fx-background-color: rgb(200,200,200);");

        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                vBox.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                vBox.setStyle("-fx-background-color: #373e43;" + "-fx-background-radius: 15;");
            }

            else {
                vBox.getScene().getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                vBox.setStyle("-fx-background-radius: 15;" + "-fx-background-color: rgb(200,200,200);");
            }
        });*/


        VBox.setMargin(textFlow, new Insets(20, 0, 0, 10));
        textFlow.getChildren().add(vBox);


        container.getChildren().add(profileImage);
        container.getChildren().add(textFlow);
        container.setTextAlignment(TextAlignment.LEFT);
        textFlow.setTranslateX(20);


        int d =0;
        for(Post p : Post.Posts)
            if(p.getComments().contains(comment))
                d = p.getId();

        int finalD = d;
        comment_button.setOnAction(event -> {
            send_button.setId(finalD +"-"+comment.getId());
            main_type.setPromptText("Add a Reply to "+comment.getOwner().getUserName()+"'s Comment...");
            isInPv = false;
            isInGroup = false;
            main_type.setDisable(false);
            attachment.setDisable(true);
            send_button.setDisable(false);
            attachment.setDisable(false);
            /*
            int index = chat_box.getChildren().indexOf(container);

            ArrayList<Node> comment_containers = new ArrayList<>(chat_box.getChildren().subList(index+1, index + comment.getComments().size() + 1));


            for(Node t : comment_containers){
                t.setVisible(!t.isVisible());
                t.setManaged(!t.isManaged());
                t.setDisable(!t.isDisable());
            }*/
        });

        return container;
    }


    public TextFlow loadPV(User user1){
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
        profileImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    ViewUserProfile(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(!AllPosts.isEmpty())
            textFlow.setId(AllPosts.get(AllPosts.size()-1).getPublishDate().format(formatter));
        else
            textFlow.setId(LocalDateTime.now().format(formatter));

        Label text;
        if(AllPosts.isEmpty())
            text  = new Label(user1.getUserName() + "\n" + "No Chat Here");

        else {
            if(AllPosts.get(AllPosts.size()-1).getSeens().containsKey(String.valueOf(user.getID()))){
                if (AllPosts.get(AllPosts.size() - 1).getContext().length() > 10)
                    text = new Label(user1.getUserName() + "\n" + AllPosts.get(AllPosts.size() - 1).getContext().substring(0, 10) + "...");

                else
                    text = new Label(user1.getUserName() + "\n" + AllPosts.get(AllPosts.size() - 1).getContext());
            }

            else {
                if (AllPosts.get(AllPosts.size() - 1).getContext().length() > 10) {
                    text = new Label(user1.getUserName() + "\n" + AllPosts.get(AllPosts.size() - 1).getContext().substring(0, 10) + "...");

                }

                else
                    text = new Label(user1.getUserName() + "\n" + AllPosts.get(AllPosts.size() - 1).getContext());

                text.setStyle("-fx-font-weight: bold;");
            }
        }

        textFlow.setTranslateY(-profileImage.getFitHeight()/2);
        container.getChildren().add(profileImage);
        textFlow.getChildren().add(text);

        container.setId(String.valueOf(user1.getID()));
        textFlow.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        textFlow.setMinHeight(TextFlow.USE_COMPUTED_SIZE );
        textFlow.setMaxHeight(TextFlow.USE_COMPUTED_SIZE );


        container.setOnMouseClicked(event -> ChoosePVChat(event,AllPosts));


        container.setPrefHeight(60);
        container.setMinWidth(TextFlow.USE_COMPUTED_SIZE);
        container.setMaxWidth(TextFlow.USE_COMPUTED_SIZE);
        container.setMinHeight(TextFlow.USE_PREF_SIZE );
        container.setMaxHeight(TextFlow.USE_COMPUTED_SIZE );

        textFlow.setTranslateX(10);
        container.getChildren().add(textFlow);

        container.setStyle("-fx-background-color: rgb(200,200,200)");
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                toggleSwitch.getScene().getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                container.setStyle("-fx-background-color: #373e43;");
            }

            else {
                toggleSwitch.getScene().getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
                container.setStyle("-fx-background-color: rgb(200,200,200)");
            }
        });

        return  container;
    }

    public void ShowFollowers(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FollowersView.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        if(isDark.getValue())
            scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                scene.getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });


        FollowersController controller = loader.getController();

        Node node = (Node) event.getSource();
        controller.initialize(user, (Stage) node.getScene().getWindow());

        Stage MainStage = new Stage();
        MainStage.setResizable(false);
        MainStage.setTitle("Followers");

        MainStage.setScene(scene);
        MainStage.show();
    }

    public void ShowFollowing(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FollowingView.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        if(isDark.getValue())
            scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                scene.getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });

        FollowingController controller = loader.getController();
        Node node = (Node) event.getSource();
        controller.initialize(user, (Stage) node.getScene().getWindow());

        Stage MainStage = new Stage();
        MainStage.setResizable(false);
        MainStage.setTitle("Followers");

        MainStage.setScene(scene);
        MainStage.show();
    }

    public void ShowDiscoverPeople(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("DiscoverPeople.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        if(isDark.getValue())
            scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                scene.getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });


        DiscoverPeopleController controller = loader.getController();
        controller.initialize(user);

        Stage MainStage = new Stage();
        MainStage.setResizable(false);
        MainStage.setTitle("Discover People");

        MainStage.setScene(scene);
        MainStage.show();
    }

    public void ShowLikers(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("TheLikersList.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        if(isDark.getValue())
            scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                scene.getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });


        TheLikersList_Controller controller = loader.getController();
        controller.initialize(user, post);

        Stage MainStage = new Stage();
        MainStage.setResizable(false);
        MainStage.setTitle("Likers");

        MainStage.setScene(scene);
        MainStage.show();
    }

    public void CreateGroup(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NewGroup.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        if(isDark.getValue())
            scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                scene.getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());

            else
                scene.getRoot().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("DarkMode.css")).toString());
        });

        NewGroupController mainPageController = loader.getController();
        Group group = new Group("");
        group.setOwner(user);
        group.setOwnerId(user.getID());
        DBManagerTester.insert(group);
        mainPageController.initialize(user, group);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        MainStage.setScene(scene);
        MainStage.show();
    }


    public void setStyle(TextFlow textFlow, Post post){

        if(isDark.getValue() && post.getOwner().getID() == user.getID())
            textFlow.setStyle("-fx-background-color: rgb(11,11,69);" + "-fx-background-radius: 15;");

        else if(post.getOwner().getID() == user.getID())
            textFlow.setStyle("-fx-background-color: rgb(131,238,255);" + "-fx-background-radius: 15;");

        else if(!isDark.getValue() && post.getOwner().getID() != user.getID())
            textFlow.setStyle("-fx-background-color: rgb(200,200,200);" + "-fx-background-radius: 15;");

        else if(isDark.getValue() && post.getOwner().getID() != user.getID())
            textFlow.setStyle("-fx-background-color: #373e43;" + "-fx-background-radius: 15;");

        isDark.addListener((observable, oldValue, newValue) -> {
            if(newValue && post.getOwner().getID() == user.getID())
                textFlow.setStyle("-fx-background-color: rgb(11,11,69);" + "-fx-background-radius: 15;");

            else if(post.getOwner().getID() == user.getID())
                textFlow.setStyle("-fx-background-color: rgb(131,238,255);" + "-fx-background-radius: 15;");

            else if(!newValue && post.getOwner().getID() != user.getID())
                textFlow.setStyle("-fx-background-color: rgb(200,200,200);" + "-fx-background-radius: 15;");

            else if(newValue && post.getOwner().getID() != user.getID())
                textFlow.setStyle("-fx-background-color: #373e43;" + "-fx-background-radius: 15;");
        });
    }

    public Image attachment(ActionEvent event){
        Window window = ((Node) (event.getSource())).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");

        Image image = null;


        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*.gif"));

        File file = fileChooser.showOpenDialog(window);
        if(file != null){
            image = new Image(file.toURI().toString());
        }
        event.consume();

        return image;
    }

    public void ChangeAccount(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UserChangeProp.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        UserChangePropController mainPageController = loader.getController();
        mainPageController.initialize(user);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        MainStage.setScene(scene);
        MainStage.show();
    }

    public void LogOut(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("hello-view.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        Stage MainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();


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
