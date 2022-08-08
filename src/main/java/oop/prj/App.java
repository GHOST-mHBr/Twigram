package oop.prj;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;


import oop.prj.DB.DBManager;
import oop.prj.model.Comment;
import oop.prj.model.Group;
import oop.prj.model.Message;
import oop.prj.model.Post;
import oop.prj.model.Sendable;
import oop.prj.model.User;
import oop.prj.model.Message.MessageType;
import oop.prj.model.User.UserType;

public class App {

    private static User loggedInUser = null;
    private static Scanner sc = new Scanner(System.in);

    public static String getInput(String message) {
        System.out.print(message + ": ");
        String result = sc.nextLine();
        return result;
    }

    public static void prLn(String str) {
        System.out.println(str);
    }

    public static void pr(String str) {
        System.out.print(str);
    }

    private static void prError(Exception e) {
        if (e != null && e.getMessage() != null && !e.getMessage().equals("")) {
            prLn(e.getMessage());
        } else {
            prLn("\nAn error occurred\nplease try again\n" + e.getClass().getSimpleName());
        }
    }

    public static void main(String[] args) {

        // DBManager.createTableIfNotExist(NormalUser.class);
        // DBManager.createTableIfNotExist(Group.class);
        // DBManager.createTableIfNotExist(Comment.class);
        // DBManager.createTableIfNotExist(User.class);
        // DBManager.createTableIfNotExist(Post.class);
        // DBManager.createTableIfNotExist(BusinessUser.class);
        // DBManager.createTableIfNotExist(AdPost.class);
        DBManager.createTableIfNotExist(Message.class);

        User.loadAllObjects();
        Post.loadAllObjects();
        Comment.loadAllObjects();
        Group.loadAllObjects();
        Message.loadAllObjects();

        // User.fetchData();

        while (true) {
            String line = sc.nextLine();
            if (line.equals("exit")) {
                Post.saveData();
                Message.saveData();
                User.saveData();
                Comment.saveData();
                Group.saveData();
                break;
            }
            line = line.replaceAll("  +", " ");
            line = line.replaceAll("^ +", "");
            line = line.replaceAll(" +$", "");

            String lineParts[] = line.split(" ");

            try {
                switch (lineParts[0]) {
                    case "create_account": {
                        int accountType = 1;
                        switch (getInput("Account mode: \nn for Normal \nb for Business").toLowerCase()) {
                            case "n":
                                accountType = 1;
                                break;
                            case "b":
                                accountType = 2;
                                break;
                            default:
                                throw new InputMismatchException("Error\nBad input\n");
                        }
                        String userName = getInput("User name");
                        String pass = getInput("Password");
                        String userId = getInput("Id");
                        String phoneNumber = getInput("Phone number");
                        String gmail = getInput("Gmail address");
                        if (accountType == 1) {
                            new User(userName, userId, pass, gmail, phoneNumber, UserType.NORMAL);
                        } else {
                            new User(userName, userId, pass, gmail, phoneNumber, UserType.BUSINESS);
                        }
                        prLn("Successful!");
                        break;
                    }

                    case "login_user": {
                        String userId = getInput("Id");
                        String password = getInput("Password");
                        User user = User.getUser(userId);
                        if (user == null || !user.getPassword().equals(password)) {
                            throw new NoSuchElementException("No such a user exist");
                        }
                        loggedInUser = user;
                        prLn("You entered successfully!");
                        break;
                    }

                    case "send_message": {
                        if (loggedInUser == null) {
                            throw new NullPointerException("Please login first");
                        }
                        String dest = getInput("Enter g for groups and p for private message");
                        if (dest.toLowerCase().equals("p") || dest.toLowerCase().equals("g")) {
                            String messageText = getInput("Enter your message");
                            String receiverId = getInput("Input receiver id");
                            if (dest.toLowerCase().equals("p")) {
                                loggedInUser.sendMessage(messageText, User.getUser(receiverId));
                            } else {
                                loggedInUser.sendMessage(messageText, Group.getGroup(receiverId));
                            }
                            prLn("\nThe message sent successfully");
                        } else {
                            throw new IllegalArgumentException("Bad input");
                        }
                        break;
                    }
                    case "read_messages": {
                        if (loggedInUser == null) {
                            throw new NullPointerException("Please login first");
                        }
                        loggedInUser.printAndSeeMessages();
                        break;
                    }

                    case "show_profile": {
                        User user;
                        if (lineParts.length > 1) {
                            user = User.getUser(lineParts[1]);
                        } else if (loggedInUser != null) {
                            user = loggedInUser;
                        } else {
                            user = User.getUser(getInput("Enter the id"));
                        }
                        if (user.equals(loggedInUser)) {
                            prLn(loggedInUser.toString());
                        } else {
                            prLn(user.getInfo());
                        }
                        break;
                    }

                    case "follow":
                        if (loggedInUser == null) {
                            throw new IllegalArgumentException("Please login first!");
                        }
                        if (lineParts.length > 1) {
                            loggedInUser.follow(User.getUser(lineParts[1]));
                        } else {
                            String otherId = getInput("Enter id of who you want to follow");
                            loggedInUser.follow(User.getUser(otherId));
                        }
                        prLn("Successful");
                        break;
                    case "show_page": {
                        if (loggedInUser == null) {
                            throw new IllegalStateException("Please login first!");
                        }
                        User user = User.getUser(getInput("Enter id of the page owner"));
                        user.printPage(loggedInUser);

                        break;
                    }

                    case "post": {
                        if (loggedInUser == null) {
                            throw new NullPointerException("Please login first");
                        }
                        loggedInUser.post(getInput("Enter your post text"));
                        prLn("done");
                        break;
                    }
                    case "like": {
                        if (loggedInUser == null) {
                            throw new IllegalArgumentException("Please login first");
                        }
                        if (lineParts.length < 3 || lineParts[1].equals("") || lineParts[2].equals("")) {
                            throw new IllegalArgumentException(
                                    "bas usage\nuse the following syntax:\nlike [post|comment] [id]\n");
                        }
                        switch (lineParts[1].toLowerCase()) {
                            case "post":
                                Post.getWithId(lineParts[2]).like(loggedInUser);
                                break;

                            case "comment":
                                Comment.getWithId(lineParts[2]).like(loggedInUser);
                                break;
                        }
                        prLn("done.");
                        break;

                    }
                    case "comment": {
                        if (loggedInUser == null) {
                            throw new IllegalArgumentException("Please login first");
                        }
                        if (lineParts.length != 4 || !(lineParts[1].equals("comment") || lineParts[1].equals("post"))) {
                            throw new IllegalArgumentException(
                                    "Bad usage!\nuse the following syntax:\ncomment [comment|post] [commentId|postId] [your comment]");
                        }
                        Post post = null;
                        if (lineParts[1].toLowerCase().equals("comment")) {
                            post = Comment.getWithId(lineParts[2]);
                        } else {
                            post = Post.getWithId(lineParts[2]);
                        }
                        post.comment(lineParts[3], loggedInUser);
                        prLn("Successful");
                        break;
                    }
                    case "group": {
                        if (lineParts.length < 4) {
                            throw new IllegalArgumentException(
                                    "Bad usage\nuse the following syntax:\ngroup [add|remove|ban|unban] [group id] [user id]\nor"
                                            +
                                            "\ngroup [change_name|change_id] [group id] [new name|new id]\nor" +
                                            "\ngroup create [group id] [group name]\nor" +
                                            "\ngroup show [group id] [info|chat]\n");
                        }

                        switch (lineParts[1]) {
                            case "create": {
                                new Group(lineParts[3], lineParts[2], loggedInUser);
                                prLn("done");
                                break;
                            }
                            case "add": {
                                Group.getGroup(lineParts[2]).addUser(User.getUser(lineParts[3]), loggedInUser);
                                prLn("done");
                                break;
                            }
                            case "remove": {
                                Group.getGroup(lineParts[2]).removeUser(User.getUser(lineParts[3]), loggedInUser);
                                prLn("done");
                                break;
                            }
                            case "ban": {
                                Group.getGroup(lineParts[2]).ban(lineParts[3], loggedInUser);
                                prLn("done");
                                break;
                            }
                            case "unban": {
                                Group.getGroup(lineParts[2]).unban(lineParts[3], loggedInUser);
                                break;
                            }
                            case "change_id": {

                                Group.getGroup(lineParts[2]).setGroupId(lineParts[3], loggedInUser);
                                prLn("done");
                                break;
                            }
                            case "change_name": {
                                Group.getGroup(lineParts[2]).setGroupName(lineParts[3], loggedInUser);
                                prLn("done");
                                break;
                            }
                            case "show":
                                switch (lineParts[2]) {
                                    case "info": {
                                        Group.getGroup(lineParts[3]).printInfo();
                                        break;
                                    }
                                    case "chat": {
                                        Group.getGroup(lineParts[3]).printChat(loggedInUser);
                                        break;
                                    }
                                    default: {
                                        throw new IllegalArgumentException("Bad syntax!");
                                    }
                                }
                                break;
                            default: {
                                throw new IllegalArgumentException("Bad command");
                            }
                        }
                        break;
                    }
                    case "edit_message": {
                        if (lineParts.length != 3) {
                            throw new IllegalArgumentException(
                                    "bad input\nuse the following syntax:\nedit_message [message id] [new context]");
                        }
                        if (Message.getWithId(lineParts[1]).getOwnerId() != loggedInUser.getID()) {
                            throw new IllegalAccessException("You are not the owner of this message!");
                        }
                        if(Message.getWithId(lineParts[1]).getType().equals(MessageType.REPLY)){
                            throw new IllegalAccessException("Forwarded messages are not changeable!");
                        }
                        Message.getWithId(lineParts[1]).setContext(lineParts[2]);
                        prLn("done");
                        break;
                    }
                    case "ban_user": {
                        if (loggedInUser == null) {
                            throw new IllegalAccessException("Please login first");
                        }
                        String id = getInput("Who do you want to ban?");
                        loggedInUser.ban(User.getUser(id));
                        break;
                    }
                    case "remove_message": {
                        if (lineParts.length != 2) {
                            throw new IllegalArgumentException(
                                    "bad input\nuse the following syntax:\nremove_message [message id]");
                        }
                        if (Message.getWithId(lineParts[1]).getOwnerId() != loggedInUser.getID()) {
                            throw new IllegalAccessException("You are not the owner of this message!");
                        }
                        Message.removeMessage(lineParts[1]);
                        prLn("done");
                        break;
                    }
                    case "reply": {
                        if (loggedInUser == null) {
                            throw new IllegalArgumentException("Please login first");
                        }
                        if (lineParts.length != 3) {
                            throw new IllegalArgumentException(
                                    "Bad syntax. use the following syntax:\nreply [replied message id] [your reply]");
                        }
                        Message.getWithId(lineParts[1]).reply(lineParts[2], loggedInUser);
                        prLn("done");
                        break;
                    }

                    case "forward": {
                        if (loggedInUser == null) {
                            throw new IllegalArgumentException("Please login first");
                        }
                        if (lineParts.length != 4 || (!lineParts[2].equals("group") && !lineParts[2].equals("user"))) {
                            throw new IllegalArgumentException(
                                    "Bad syntax. use the following syntax:\nforward [forwarded message id] [group|user] [receiver id]");
                        }
                        Sendable receiver = null;
                        if (lineParts[2].toLowerCase().equals("group")) {
                            receiver = Group.getGroup(lineParts[3]);
                        } else {
                            receiver = User.getUser(lineParts[3]);
                        }
                        Message.getWithId(lineParts[1]).forward(receiver,loggedInUser);
                        break;
                    }

                    case "first_page": {
                        if(loggedInUser==null){
                            throw new NullPointerException("Please login first");
                        }
                        loggedInUser.firstPage(loggedInUser);
                        break;
                    }
                    case "show_pv": {
                        if(loggedInUser == null){
                            throw new NullPointerException("Please login first");
                        }
                        String userId = getInput("Enter id of the user");
                        loggedInUser.printPv(User.getUser(userId));
                        break;
                    }
                    case "show_stats":{
                        if(loggedInUser == null){
                            throw new NullPointerException("Please login first");
                        }
                        if(loggedInUser.getType().equals(UserType.NORMAL)){
                            throw new IllegalAccessException("This feature is for business accounts");
                        }
                        loggedInUser.showStats();
                        break;
                    }
                    default: {
                        prLn("Bad command");
                    }
                }
            } catch (Exception e) {
                prError(e);
            }
        }

        sc.close();
    }
}