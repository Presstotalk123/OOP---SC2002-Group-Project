package hms;

// import java.io.FileWriter;
import java.io.IOException;
// import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
// import java.util.Date;

class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Welcome to Hospital Management System!");

    Scanner scanner = new Scanner(System.in);

    while (Main.eventLoop(scanner)) {
    }

    System.out.println("Bye!");
  }

  public static boolean eventLoop(Scanner scanner) throws IOException {
    // Load data on program startup
    List<User> users;
    try {
      users = User.loadAllUsers();
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
              3. Exit
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
        outerWhile:
        while (true) {
          System.out.print("""
                    Select what kind of user you want to sign up as:
                    1. Patient
                    2. Administrator
                    3. Doctor
                    4. Pharmacist
                    5. Go Back
                  """);
          try {
            int selection = scanner.nextInt();
            scanner.nextLine();
            switch (selection) {
              case 1:
                new Patient(scanner);
                break outerWhile;
              case 2:
                new Administrator(scanner);
                break outerWhile;
              case 3:
                new Doctor(scanner);
                break outerWhile;
              case 4:
                new Pharmacist(scanner);
                break outerWhile;
              case 5:
                break outerWhile;
              default:
                System.out.println("Invalid input. Please enter a number from 1 to 4.");
                break;
            }
            System.out.println("Sign Up successful!Please log in now.");
          } catch (NoSuchElementException error) {
            System.out.println("Invalid input. Please enter a number from 1 to 4.");
          } catch (IOException e) {
              throw new RuntimeException(e);
          }

        }
        break;

      case 3:
        return false;

      default:
        System.out.println("Invalid Selection. Please try again.");
        break;
    }

    return true;
  }
}
