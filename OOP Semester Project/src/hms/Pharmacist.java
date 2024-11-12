package hms;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

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
        System.out.print("""
                Pharmacist Menu:
                1. View Appointment Outcome Record
                2. Update Prescription Status
                3. View Medication Inventory
                4. Submit Replenishment Request
                5. Log Out
                Enter your choice:""");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                List<AppointmentOutcomeRecord> records = AppointmentOutcomeRecord.getAllRecords();
                // Print the records
                if (records.isEmpty()) {
                    System.out.println("No appointment outcome records found.");
                } else {
                    System.out.println("Appointment Outcome Records:");
                    for (AppointmentOutcomeRecord record : records) {
                        System.out.println(record); // Assumes AppointmentOutcomeRecord has a toString() method
                    }
                }
                break;
            case 2:
                System.out.print("Enter Prescription ID to update: ");
                String prescriptionId = scanner.nextLine();
                // Why are you asking for medication name?
                // Also Prescriptions should be updated individually, not all at once.
                // System.out.print("Enter Medication Name to update: "); // Ask for medication
                // name
                // String medicationName = scanner.nextLine(); // Collect medication name
                try {
                    while (true) {
                        System.out.print("Enter new status (e.g., Pending/Dispensed): ");
                        String status = scanner.nextLine();
                        if (status.toLowerCase().equals("pending")) {
                            updatePrescriptionStatus(prescriptionId, PrescriptionStatus.Pending);
                        } else if (status.toLowerCase().equals("dispensed")) {
                            updatePrescriptionStatus(prescriptionId, PrescriptionStatus.Dispensed);
                        } else {
                            System.out.println("Invalid choice! Please choosse between Pending or Dispensed");
                        }
                    }

                } catch (IOException error) {
                    System.out.println("Failed to update Prescription Status: " + error.getMessage());
                }
                break;
            case 3:
                displayMedicationInventory();
                break;
            case 4:
                System.out.print("Enter Medication Name: ");
                String medicationName = scanner.nextLine();
                System.out.print("Enter Quantity: ");
                int quantity = scanner.nextInt();
                submitReplenishmentRequest(medicationName, quantity);
                break;
            case 5:
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }

        return true;
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
                    int newStockLevel = med.getStockLevel() - 1; // Assume 1 unit is dispensed per prescription
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

    // In the submitReplenishmentRequest method
    public void submitReplenishmentRequest(String medicationName, int quantity) {
        // Create a new replenishment request
        ReplenishmentRequest request = new ReplenishmentRequest(
                generateRequestID(), medicationName, quantity, "Pending");
        // Submit the request (this will also create the file if it doesn't exist)
        request.saveToCSV("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\replenishment_requests.csv");
    }

    private String generateRequestID() {
        // Generates a unique request ID
        return "REQ" + System.currentTimeMillis();
    }

}
