package hms;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Pharmacist extends Staff {

    public Pharmacist(Scanner scanner) throws IOException {
        super(scanner, "pharmacist");
        try {
            super.save();
        } catch (IOException error) {
            System.out.println("Unable to save user " + name + " due to IOException: " + error.getMessage());
        }
    }

    public Pharmacist(String id, String name, String password) throws IOException {
        super(id, name, password, "pharmacist");
    }

    public boolean eventLoop(Scanner scanner) {
        while (true) {
            System.out.print("""
                    Pharmacist Menu:
                    1. View Appointment Outcome Record
                    2. Update Prescription Status
                    3. View Medication Inventory
                    4. Submit Replenishment Request
                    5. Log Out
                    Enter your choice: """);

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAppointmentOutcomeRecords(scanner);
                    break;
                case 2:
                    updatePrescription(scanner);
                    break;
                case 3:
                    displayMedicationInventory();
                    break;
                case 4:
                    submitReplenishmentRequest(scanner);
                    break;
                case 5:
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAppointmentOutcomeRecords(Scanner scanner) {
        System.out.print("Enter Appointment ID to view: ");
        String appointmentId = scanner.nextLine();
    
        List<AppointmentOutcomeRecord> records = AppointmentOutcomeRecord.getAllRecords();
    
        // Filter records with the given appointment ID
        List<AppointmentOutcomeRecord> matchingRecords = records.stream()
                .filter(r -> r.getAppointmentID().equals(appointmentId))
                .collect(Collectors.toList());
    
        if (matchingRecords.isEmpty()) {
            System.out.println("No records found for Appointment ID: " + appointmentId);
        } else {
            System.out.println("Appointment Outcome Records:");
            for (AppointmentOutcomeRecord record : matchingRecords) {
                displayFormattedRecord(record); 
                System.out.println("--------------------------------------------------");
            }
        }
    }
    
    

    private void displayFormattedRecord(AppointmentOutcomeRecord record) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
        System.out.println("Appointment ID: " + record.getAppointmentID());
        System.out.println("Date of Appointment: " + dateFormat.format(record.getDateOfAppointment()));
        System.out.println("Service Type: " + record.getServiceType());
        
        // Use the updated formatting logic here
        System.out.println("Prescribed Medications: " + formatMedications(record.getPrescribedMedications()));
        System.out.println("Consultation Notes: " + record.getConsultationNotes());
    }

    private String formatMedications(List<Prescription> prescriptions) {
        if (prescriptions.isEmpty()) {
            return "None";
        }
    
        // This mirrors the style from Patient, properly displaying medications.
        return prescriptions.stream()
                .map(p -> p.getMedicationName() + ": " + p.getQuantity())
                .collect(Collectors.joining(", "));
    }
    

    private void updatePrescription(Scanner scanner) {
        System.out.print("Enter Prescription ID to update: ");
        String prescriptionId = scanner.nextLine();

        try {
            System.out.print("Enter new status (Pending/Dispensed): ");
            String statusInput = scanner.nextLine().toLowerCase();

            PrescriptionStatus status;
            if (statusInput.equals("pending")) {
                status = PrescriptionStatus.Pending;
            } else if (statusInput.equals("dispensed")) {
                status = PrescriptionStatus.Dispensed;
            } else {
                System.out.println("Invalid status entered.");
                return;
            }

            updatePrescriptionStatus(prescriptionId, status);

        } catch (IOException e) {
            System.out.println("Error updating prescription: " + e.getMessage());
        }
    }

    public void updatePrescriptionStatus(String prescriptionId, PrescriptionStatus status) throws IOException {
        List<Prescription> records = Prescription.getAll();
        Inventory inventory = new Inventory();
        inventory.loadFromCSV(); // Load the current inventory
        boolean found = false;

        for (Prescription record : records) {
            if (record.getID().equals(prescriptionId)) {
                record.updateStatus(status);
                record.save();
                found = true;

                // Update inventory if the status is set to dispensed
                if (status == PrescriptionStatus.Dispensed) {
                    Medication med = inventory.getMedication(record.getMedicationName());
                    if (med != null) {
                        int quantityDispensed=record.getQuantity();
                        int newStockLevel = med.getStockLevel() - quantityDispensed; // Assume 1 unit is dispensed per prescription
                        inventory.updateStockLevel(record.getMedicationName(), newStockLevel);
                        inventory.saveToCSV("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\inventory.csv");
                    }
                }
                break; // Exit the loop as we found the prescription
            }
        }

        if (found) {
            System.out.println("Prescription status updated successfully.");
        } else {
            System.out.println("Prescription not found for the given prescription ID.");
        }
    }

    public void displayMedicationInventory() {
        Inventory inventory = new Inventory();
        inventory.loadFromCSV(); // Load the inventory from the CSV file

        List<Medication> medications = inventory.getMedications();

        if (medications.isEmpty()) {
            System.out.println("No medications in inventory.");
        } else {
            System.out.println("Medication Inventory:");
            for (Medication med : medications) {
                System.out.printf("Name: %s, Quantity: %d%n", med.getMedicationName(), med.getStockLevel());
            }
        }
    }

    private void submitReplenishmentRequest(Scanner scanner) {
        System.out.print("Enter Medication Name: ");
        String medicationName = scanner.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        submitReplenishmentRequest(medicationName, quantity);
        System.out.println("Replenishment request submitted successfully.");
    }

    public void submitReplenishmentRequest(String medicationName, int quantity) {
        ReplenishmentRequest request = new ReplenishmentRequest(
                generateRequestID(), medicationName, quantity, "Pending");

        request.saveToCSV("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\replenishment_requests.csv");
    }

    private String generateRequestID() {
        return "REQ" + System.currentTimeMillis();
    }

    @Override
public String toString() {
    return this.id + " - Pharmacist - " + this.name;
}


}
