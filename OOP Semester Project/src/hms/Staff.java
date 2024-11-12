package hms;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public abstract class Staff extends User {
    public Gender gender;
    public int age;
    public String phoneNumber;
    public String emailAddress;
    public String specialization = "NA"; // Default to "NA" if not a doctor

    public Staff(Scanner scanner, String role) throws IOException {
        super(scanner, role);
        try {
            super.save();
        } catch (IOException error) {
            System.out.println("Unable to save user " + name + " due to IOException: " + error.getMessage());
        }

        // Gender input
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

        // Age input
        while (true) {
            System.out.print("Enter the age for this user: ");
            String age = scanner.nextLine();
            if (age.matches("^\\d+$")) {
                this.age = Integer.parseInt(age);
                break;
            } else {
                System.out.println("Invalid entry. Please enter a valid age.");
            }
        }

        // Phone number input
        while (true) {
            System.out.print("Enter the phone number for this user (Singaporean Numbers Only, like 91234567): ");
            String number = scanner.nextLine();
            if (number.matches("^\\d{8}$")) {
                this.phoneNumber = number;
                break;
            } else {
                System.out.println("Invalid entry. Please specify in the format 91234567.");
            }
        }

        // Email input
        while (true) {
            System.out.print("Enter the email address for this user: ");
            String email = scanner.nextLine();
            if (email.matches("^[\\w.-]+@[\\w-]+\\.[a-z]{2,}$")) {
                this.emailAddress = email;
                break;
            } else {
                System.out.println("Invalid entry. Please enter a valid email address.");
            }
        }

        // Specialization input (only for doctors)
        if (role.equals("doctor")) {
            while (true) {
                System.out.print("Enter the specialization for this doctor: ");
                String specialization = scanner.nextLine();
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
        this.gender = staffData[1].equalsIgnoreCase("male") ? Gender.Male : Gender.Female;
        this.age = Integer.parseInt(staffData[2]);
        this.phoneNumber = staffData[4];
        this.emailAddress = staffData[5];
        this.specialization = staffData.length > 6 ? staffData[6] : "NA"; // Default to "NA" if no specialization
    }

    public String getRole() {
        return this.role;
    }

    public String getGender() {
        return this.gender.toString().toLowerCase();
    }

    public void updatePhoneNumber(String phoneNumber) throws Exception {
        if (phoneNumber.matches("^\\d{8}$")) {
            this.phoneNumber = phoneNumber;
        } else {
            throw new Exception("Invalid entry. Please specify in the format 91234567.");
        }
    }

    public void updateEmailAddress(String email) throws Exception {
        if (email.matches("^[\\w.-]+@[\\w-]+\\.[a-z]{2,}$")) {
            this.emailAddress = email;
        } else {
            throw new Exception("Invalid entry. Please enter a valid email address.");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void save() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\staff.csv"));
        FileOutputStream output = new FileOutputStream("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\staff.csv");

        boolean isEntryFound = false;
        for (String line : lines) {
            String[] appt = line.split(",");

            if (appt[0].equals(this.id)) {
                String newEntry = this.id + "," + this.gender.toString().toLowerCase() + "," + this.age + ","
                        + this.role + "," + this.phoneNumber + "," + this.emailAddress + "," + this.specialization + "\n";
                output.write(newEntry.getBytes());
                isEntryFound = true;
            } else {
                output.write((line + "\n").getBytes());
            }
        }

        if (!isEntryFound) {
            String newEntry = this.id + "," + this.gender.toString().toLowerCase() + "," + this.age + ","
                    + this.role + "," + this.phoneNumber + "," + this.emailAddress + "," + this.specialization + "\n";
            output.write(newEntry.getBytes());
        }

        output.close();
    }

    private static String[] loadStaffDataFromFile(String id) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\staff.csv"));
        String nextLine;

        while ((nextLine = file.readLine()) != null) {
            String[] staff = nextLine.split(",");
            if (staff[0].equals(id)) {
                file.close();
                return staff;
            }
        }

        file.close();
        throw new IOException("Missing or Invalid Staff Data found in staff.csv for staff with ID: " + id);
    }
}
