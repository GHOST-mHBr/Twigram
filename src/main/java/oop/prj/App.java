package oop.prj;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import oop.prj.DB.DBManager;
import oop.prj.model.Message;
import oop.prj.model.Post;
import oop.prj.model.User;
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
        DBManager.createTableIfNotExist(Message.class);
        // DBManager.createTableIfNotExist(User.class);
        // DBManager.createTableIfNotExist(Post.class);
        // DBManager.createTableIfNotExist(BusinessUser.class);
        // DBManager.createTableIfNotExist(AdPost.class);

        User.loadAllObjects();
        Message.loadAllObjects();
        Post.loadAllObjects();

        // User.fetchData();

        while (true) {
            String line = sc.nextLine();
            if (line.equals("exit")) {
                Post.saveData();
                Message.saveData();
                User.saveData();
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
                        String messageText = getInput("Enter your message");
                        String receiverId = getInput("Input receiver id");
                        loggedInUser.sendMessage(messageText, User.getUser(receiverId));
                        prLn("\nmessage sent successfully");
                        break;
                    }
                    case "read_messages": {
                        if (loggedInUser != null) {
                            loggedInUser.printAndSeeMessages();
                        }
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
                    case "show_posts": {
                        if (loggedInUser == null) {
                            throw new IllegalArgumentException("Please login first!");
                        }
                        loggedInUser.printPosts();
                        break;
                    }
                    case "create_group":

                        break;

                    case "post":
                        if (loggedInUser == null) {
                            throw new NullPointerException("Please login first");
                        }
                        loggedInUser.post(getInput("Enter your post text"));
                        break;

                    case "comment":

                        break;

                }
            } catch (Exception e) {
                prError(e);
            }
        }

        sc.close();
    }
}