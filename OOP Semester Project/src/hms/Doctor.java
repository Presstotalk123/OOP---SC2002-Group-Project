package hms;

import java.util.Scanner;

public class Doctor extends User {
    public static String role = "doctor";

    private String specialty;
    

    public Doctor(String id, String name, String password) {
        super(id, name, password);
        this.specialty = specialty;
    }

    public boolean eventLoop(Scanner scanner) {
        System.out.println("Not Implemented Yet");
        return false;
    }

    

    // Getters and setters for doctor-specific fields
}
