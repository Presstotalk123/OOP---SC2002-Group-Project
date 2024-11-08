package hms.MedicalRecords;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hms.Doctor;
import hms.Gender;
import hms.Patient;

public class MedicalRecords implements MedicalRecordPatientView, MedicalRecordDoctorView {

    private String id;
    private String name;
    private String dateOfBirth;
    private Gender gender;
    private String phoneNumber;
    private String emailAddress;
    private ArrayList<String> diagnosis;
    private ArrayList<String> prescriptions;
    private ArrayList<String> treatments;
    private String bloodType; // final as this field should not be updated.
    private  ArrayList<String> allergies;

    public MedicalRecords(Scanner scanner, String id, String name) {
        while (true) {
            System.out.print("Enter the Date of Birth for this user in the format (DD-MM-YYYY): ");
            String dateOfBirth = scanner.nextLine();
            if (dateOfBirth.matches("(0[1-9]|[12][0-9]|3[01])\\-(0[1-9]|1[0,1,2])\\-(19|20)\\d{2}")) {
                this.dateOfBirth = dateOfBirth;
                break;
            } else {
                System.out.println("Incorrect format! Please use the format DD-MM-YYYY.");
            }
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

        System.out.print("Enter the blood type for this user (Note: This cannot be changed): ");
        String bloodType = scanner.nextLine();

        this.bloodType = bloodType;

        this.allergies = new ArrayList<>(); // Initialize allergy list

        System.out.print("Enter any allergies for this user (separate by commas if multiple): ");
        String[] allergyArray = scanner.nextLine().split(",");
        for (String allergy : allergyArray) {
            this.allergies.add(allergy.trim());
        }
        

    }

    private MedicalRecords(String id) throws IOException {

        String[] record = MedicalRecords.loadMedicalRecordFromFile(id);

        this.id = id;
        this.name = record[1];
        this.dateOfBirth = record[2];
        this.gender = record[3].equals("male") ? Gender.Male : Gender.Female;
        this.phoneNumber = record[4];
        this.emailAddress = record[5];
        this.bloodType = record[6];
        this.allergies = new ArrayList<>(); // Initialize allergy list

        
        // TODO: Add retrieval of diagnosis, prescriptions and treatments

    }

    public static MedicalRecordPatientView getRecord(Patient caller, String patientId) throws IOException {
        return new MedicalRecords(patientId);
    }

    public static MedicalRecordDoctorView getRecord(Doctor caller, String patientId) throws IOException {
        return new MedicalRecords(patientId);
    }

    private static String[] loadMedicalRecordFromFile(String id) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader("OOP Semester Project/data/Patient.csv"));

        String nextLine = file.readLine();
        while ((nextLine = file.readLine()) != null) {
            String[] patient = nextLine.split(",");

            if (patient.length < 7) {
                continue;
            }

            String currentId = patient[0];

            if (currentId.equals(id)) {
                file.close();
                return patient;
            }
        }

        file.close();
        throw new IOException("Missing or Invalid Patient Data found in MedicalRecords.csv for patient with ID: " + id);
    }

    // From MedicalRecordPatientAccess
    public void updateEmailAddress(String email) {
        // TODO: Maybe add validation?
        if (email.length() > 0) {
            this.emailAddress = email.replaceAll(",", ""); // come up with better solution for escaping commas
        }
    }

    // From MedicalRecordPatientAccess
    public void updatePhoneNumber(String phoneNumber) {
        // TODO: Maybe add validation?
        if (phoneNumber.length() ==8) {
            this.phoneNumber = phoneNumber.replaceAll(",", ""); // come up with better solution for escaping commas
        }
    }

    // From MedicalRecordDoctorAccess
    public void newDiagnosis(String diagnosis) {
        this.diagnosis.add(diagnosis);
    }

    // From MedicalRecordDoctorAccess
    public void newPrescription(String prescription) {
        this.prescriptions.add(prescription);
    }

    // From MedicalRecordDoctorAccess
    public void newTreatmentPlan(String treatmentPlan) {
        this.treatments.add(treatmentPlan);
    }

    // Getter for allergy field
    public List<String> getAllergies() {
        return allergies;
    }

    // Add a new allergy
    public void addAllergy(String allergy) {
        this.allergies.add(allergy);
    }

    public String toString() {
        return new StringBuilder()
                .append("Patient ID: " + this.id + "\n")
                .append("Name: " + this.name + "\n")
                .append("Date of Birth: " + this.dateOfBirth + "\n")
                .append("Gender: " + this.gender + "\n")
                .append("Phone Number: " + this.phoneNumber + "\n")
                .append("Email Address: " + this.emailAddress + "\n")
                .append("Blood Type: " + this.bloodType + "\n")
                .append("Allergies: " + String.join(", ", this.allergies) + "\n") // Added allergy field
                .toString();
    }

    public void saveToFile() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/Patient.csv"));
        FileOutputStream output = new FileOutputStream("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/Patient.csv");

        for (int i = 0; i < lines.size(); i++) {
            String[] patient = lines.get(i).split(",");

            if (patient.length == 8 && patient[0].equals(this.id)) {
                String newEntry = this.id + "," + this.name + "," + this.dateOfBirth + ","
                        + this.gender.toString().toLowerCase() + "," + this.phoneNumber + "," + this.emailAddress + ","
                        + this.bloodType + "," + String.join(",", this.allergies) + "\n";
                output.write(newEntry.getBytes());
            } else {
                String line = lines.get(i) + "\n";
                output.write(line.getBytes());
            }
        }

        output.close();
    }
}

// I use interfaces to limit what different users can do
