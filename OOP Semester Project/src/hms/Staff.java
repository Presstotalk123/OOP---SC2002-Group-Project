package hms;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public abstract class Staff extends User {
    public Gender gender;
    public int age;
    public String phoneNumber;
    public String emailAddress;

    public String specialization="NA";

    public Staff(Scanner scanner, String role) throws IOException {
        super(scanner, role);
        try {
            super.save();
        } catch (IOException error) {
            System.out.println("Unable to save user " + name + " due to IOException: " + error.getMessage());
        }
        while (true) {
            System.out.print("Enter the gender for this user (male or female): ");
            String gender = scanner.nextLine().toLowerCase();
            if (gender.equals("male")) {
                this.gender = Gender.Male;
                break;
            } else if (gender.equals("female")) {
                this.gender = Gender.Female;
                break;
            } else {
                System.out.println("Invalid entry. Please specify either male or female.");
            }
        }

        while (true) {
            System.out.print("Enter the age for this user: ");
            String age = scanner.nextLine().toLowerCase();
            if (age.matches("^([0-9]+)$")) {
                this.age = Integer.parseInt(age);
                break;
            } else {
                System.out.println("Invalid entry. Please specify in the format 91234567.");
            }
        }

        while (true) {
            System.out.print("Enter the phone number for this user (Singaporean Numbers Only, like 91234567): ");
            String number = scanner.nextLine().toLowerCase();
            if (number.matches("^([0-9]{8})$") && number.length() == 8) {
                this.phoneNumber = number;
                break;
            } else {
                System.out.println("Invalid entry. Please specify in the format 91234567.");
            }
        }

        while (true) {
            System.out.print("Enter the email address for this user: ");
            String email = scanner.nextLine().toLowerCase();
            if (email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                this.emailAddress = email;
                break;
            } else {
                System.out.println("Invalid entry. Please enter a valid email address.");
            }
        }
        if(role=="doctor"){
            while(true){
                System.out.print("Enter the specialization for this doctor: ");
                String specialization = scanner.nextLine().toLowerCase();
                if (specialization.matches("^[a-zA-Z]+$")) {
                    this.specialization = specialization;
                    break;
                } else {
                    System.out.println("Invalid entry. Please enter a valid specialization.");
                }
            }
        }

    }

    public Staff(String id, String name, String password, String role) throws IOException {
        super(id, name, password, role);
        String[] staffData = Staff.loadStaffDataFromFile(id);
        this.gender = staffData[1].equals("male") ? Gender.Male : Gender.Female;
        this.age = Integer.parseInt(staffData[2]);
        this.phoneNumber = staffData[4];
        this.emailAddress = staffData[5];
    }

    public String getRole() {
        return this.role;
    }

    public String getGender() {
        return this.gender.toString().toLowerCase();
    }

    public void updatePhoneNumber(String phoneNumber) throws Exception {
        if (phoneNumber.matches("^([0-9]{8})$") && phoneNumber.length() == 8) {
            this.phoneNumber = phoneNumber;
        } else {
            throw new Exception("Invalid entry. Please specify in the format 91234567.");
        }
    }

    public void updateEmailAddress(String email) throws Exception {
        if (phoneNumber.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            this.emailAddress = email;
        } else {
            throw new Exception("Invalid entry. Please enter a valid email address.");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void save() throws IOException {
        // staff.csv: id,gender,age,role,phoneNumber,email
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\staff.csv"));
        FileOutputStream output = new FileOutputStream("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\staff.csv");

        boolean isEntryFound = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] appt = lines.get(i).split(",");

            if (appt.length == 6 && appt[0].equals(this.id)) {
                String newEntry = this.id + "," + this.gender.toString().toLowerCase() + "," + this.age + ","
                        + this.role + "," + this.phoneNumber + "," + this.emailAddress + ","+ this.specialization + "\n";
                output.write(newEntry.getBytes());
                isEntryFound = true;
            } else {
                String line = lines.get(i) + "\n";
                output.write(line.getBytes());
            }
        }

        if (!isEntryFound) {
            String newEntry = this.id + "," + this.gender.toString().toLowerCase() + "," + this.age + ","
                        + this.role + "," + this.phoneNumber + "," + this.emailAddress + ","+ this.specialization+"\n";
            output.write(newEntry.getBytes());
        }

        output.close();
    }

    private static String[] loadStaffDataFromFile(String id) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\staff.csv"));

        String nextLine = file.readLine();
        while ((nextLine = file.readLine()) != null) {
            String[] staff = nextLine.split(",");

            if (staff.length < 6) {
                continue;
            }

            String currentId = staff[0];

            if (currentId.equals(id)) {
                file.close();
                return staff;
            }
        }

        file.close();
        throw new IOException("Missing or Invalid Patient Data found in staff.csv for patient with ID: " + id);
    }

    // public String getId() {
    // return id;
    // }

    // public String getName() {
    // return name;
    // }

    // public void setName(String name) {
    // this.name = name;
    // }

    // public String getRole() {
    // return role;
    // }

    // public void setRole(String role) {
    // this.role = role;
    // }

    // public String getGender() {
    // return gender;
    // }

    // public void setGender(String gender) {
    // this.gender = gender;
    // }

    // @Override
    // public String toString() {
    // return "Staff{" +
    // "ID='" + id + '\'' +
    // ", Name='" + name + '\'' +
    // ", Role='" + role + '\'' +
    // ", Gender='" + gender + '\'' +
    // '}';
    // }
}
