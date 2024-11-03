package hms;

import java.util.List;
import java.io.IOException;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    System.out.println("Welcome to Hospital Management System!");
  }

  public boolean eventLoop(Scanner scanner) {
    // Load data on program startup
    List<User> users;
    try {
      users = User.loadFromFile("../data/users.csv");
      System.out.println(users);
    } catch (IOException error) {
      System.out.println("Error occurred when loading data: ");
      error.printStackTrace();
      return false;
    }

    System.out.print("""
          What would you like to do?
          1. Log In
          2. Sign Up
        """);

    int input = scanner.nextInt();
    scanner.nextLine();

    switch (input) {
      case 1:

        System.out.print("Please enter your Hospital ID: ");
        String hospitalId = scanner.nextLine().replace("\n", "");
        System.out.print("Please enter your Password: ");
        String password = scanner.nextLine().replace("\n", "");

        User loggedInUser = null;

        for (int i = 0; i < users.size(); i += 1) {

          User user = users.get(i);
          if (user.id.equals(hospitalId) && user.login(password)) {
            loggedInUser = user;
          }

        }

        if (loggedInUser == null) {
          System.out.println("Hospital ID or Password is incorrect! Please try again.");
        } else {
          System.out.println("> Successfully logged in as ".concat(loggedInUser.name));

          // Keep repeating the event loop of the user until eventLoop returns false
          // in other words, user logs out.
          while (loggedInUser.eventLoop(scanner)) {
          }
        }
        break;

      case 2:
        System.out.print("Enter a name for this new user: ");
        String name = scanner.nextLine();
        System.out.print("Enter a password for this new user: ");
        String passwd = scanner.nextLine();
        while (true) {
          System.out.print("""
                Select what kind of user this is:
                1. Patient
                2. Admin
                3. TODO - Teammates pls do this
              """);
          int selection = scanner.nextInt();

          if (selection == 1) {
            break;
          } else {
            break;
          }
          

        }
        break;

      default:
        System.out.println("Invalid Selection. Please try again.");
        break;
    }

    return true;
  }

}