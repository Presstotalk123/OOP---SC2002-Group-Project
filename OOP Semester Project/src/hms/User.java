package hms;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class User {
  public String id;
  public String name;
  private String password;
  public final String role;

  // When a constructor with a scanner is used, it is expected for the subclass to
  // prompt for data.
  // All subclasses should implement the scanner constructor pattern to create new
  // instance.
  public User(Scanner scanner, String role) throws IOException {
    System.out.print("Enter a name for this new user: ");
    String name = scanner.nextLine();
    while(true){
    System.out.print("Enter a password for this new user: ");
    String password = scanner.nextLine();
    System.out.print("Re-enter the password: ");
    String password2 = scanner.nextLine();
    if(password.equals(password2)){
      this.password = password;
      break;
    } else {
      System.out.println("Passwords do not match. Please try again.");
    }
    }

    Random rand = new Random();
    List<String> existingIds = Files.readAllLines(Paths.get("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/users.csv"))
            .stream()
            .map(line -> line.split(",")[0])
            .collect(Collectors.toList());

    while (true) {
      int id = rand.nextInt(9000) + 1000;
      if (!existingIds.contains(Integer.toString(id))) {
        System.out.println("Your ID is: " + id);
        this.id = Integer.toString(id);
        break;
      }
    }
    this.name = name;
    this.password = password;
    this.role = role;

  }

  public User(String id, String name, String password, String role) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.role = role;
  }

  public boolean login(String password) {
    return this.password.equals(password);
  }

  public static List<User> loadFromFile(String path) throws IOException {
    List<User> userArray = new ArrayList<User>();
    String absolutePath = "/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/users.csv";
    BufferedReader file = new BufferedReader(new FileReader(path));
    String nextLine = file.readLine();
    while ((nextLine = file.readLine()) != null) {
      String[] user = nextLine.split(",");
      String role = user[3];
      // TOOD: Add support for all kinds of users
      if (role.equals("patient")) {
        userArray.add(new Patient(user[0], user[1], user[2]));
      } else if (role.equals("administrator")) {
        userArray.add(new Administrator(user[0], user[1], user[2]));
      } else if (role.equals("doctor")) {
        userArray.add(new Doctor(user[0], user[1], user[2]));
      } else if (role.equals("pharmacist")) {
        userArray.add(new Pharmacist(user[0], user[1], user[2]));
      }
    }
    file.close();
    return userArray;
  }

  public void save() throws IOException {
    List<String> lines = Files.readAllLines(Paths.get("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/users.csv"));
    FileOutputStream output = new FileOutputStream("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/users.csv");

    boolean isEntryFound = false;
    for (int i = 0; i < lines.size(); i++) {
      String[] appt = lines.get(i).split(",");

      if (appt.length == 6 && appt[0].equals(this.id)) {
        String newEntry = this.id + "," + (this.name) + "," + this.password + "," + this.role + "\n";
        output.write(newEntry.getBytes());
        isEntryFound = true;
      } else {
        String line = lines.get(i) + "\n";
        output.write(line.getBytes());
      }
    }

    if (!isEntryFound) {
      String newEntry = this.id + "," + (this.name) + "," + this.password + "," + this.role + "\n";
      output.write(newEntry.getBytes());
    }

    output.close();
  }

  // Return true to logout
  public abstract boolean eventLoop(Scanner scanner) throws IOException;
}