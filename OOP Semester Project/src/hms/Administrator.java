package hms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hms.Appointments.Appointment;

public class Administrator extends User {
    private List<Staff> staffList;
    private List<Appointment> appointmentList;
    private Inventory inventory;

    public Administrator(Scanner scanner) throws IOException {
        super(scanner, "administrator");
        try {
            super.save();
        }
        catch (IOException error) {
            System.out.println("Unable to save user " + name + " due to IOException: " + error.getMessage());
        }
        staffList = new ArrayList<>();
        appointmentList = new ArrayList<>();
        inventory = new Inventory();
        loadStaffData();
        loadAppointmentData();
        loadInventoryData();
    }

    public Administrator(String id, String name, String password) {
        super(id, name, password, "administrator");
        staffList = new ArrayList<>();
        appointmentList = new ArrayList<>();
        inventory = new Inventory();
        loadStaffData();
        loadAppointmentData();
        loadInventoryData();
    }

    public boolean eventLoop(Scanner scanner) throws IOException {
        int choice;
        System.out.println("Administrator Menu:");
        System.out.println("1. View Staff");
        System.out.println("2. Manage Staff");
        System.out.println("3. View Appointments");
        System.out.println("4. Manage Inventory");
        System.out.println("5. Log Out");
        choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                viewStaff(scanner);
                break;
            case 2:
                manageStaff(scanner);
                break;
            case 3:
                viewAppointments(scanner);
                break;
            case 4:
                manageInventory(scanner);
                break;
            case 5:
                // Returning false just means "I want to log out and go back to the login menu"
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        // Returning true just means "Yes I want to continue as this user"
        return true;
    }

    private void viewStaff(Scanner scanner) {
        System.out.println("Viewing staff...");
        for (Staff staff : staffList) {
            System.out.println(staff);
        }
        System.out.println("Press Enter to return to the admin menu.");

        scanner.nextLine(); // Wait for user to press Enter
    }

    private void manageStaff(Scanner scanner) throws IOException {
        int choice;
        do {
            System.out.println("Manage Staff Menu:");
            System.out.println("1. Add Staff");
            System.out.println("2. Update Staff");
            System.out.println("3. Remove Staff");
            System.out.println("4. Filter Staff");
            System.out.println("5. Back to Admin Menu");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addStaff(scanner);
                    break;
                case 2:
                    updateStaff(scanner);
                    break;
                case 3:
                    removeStaff(scanner);
                    break;
                case 4:
                    filterStaff(scanner);
                    break;
                case 5:
                    System.out.println("Returning to Admin Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }

    private void addStaff(Scanner scanner) throws IOException {
        System.out.println("Enter Staff Role:");
        String role = scanner.nextLine();

        if (role.equals("doctor")) {
            staffList.add(new Doctor(scanner));
        } else if (role.equals("pharmacist")) {
            staffList.add(new Pharmacist(scanner));
        } else {
            System.out.println("Invalid staff role.");
            return;
        }

        saveStaffData(); // Save changes to the file
        System.out.println("Staff member added successfully.");
    }

    private void updateStaff(Scanner scanner) {
        System.out.println("Enter Staff ID to update:");
        String id = scanner.nextLine();
        for (Staff staff : staffList) {
            if (staff.id.equals(id)) {
                System.out.println("Updating Staff: " + staff);

                // TODO: Confirm this. Are "role" and "gender" supposed to be updatable?
                System.out.print("Enter new Staff Name (or leave blank to keep current): ");
                String newName = scanner.nextLine();
                // System.out.println("Enter new Staff Role (or leave blank to keep current):");
                // String newRole = scanner.nextLine();
                // System.out.println("Enter new Staff Gender (or leave blank to keep
                // current):"); // We being progressive fr
                // String newGender = scanner.nextLine();
                while (true) {
                    System.out.print("Enter new Staff Phone Number (or leave blank to keep current): ");
                    String phoneNumber = scanner.nextLine();
                    if (phoneNumber.isEmpty())
                        break;
                    try {
                        staff.updatePhoneNumber(phoneNumber);
                    } catch (Exception error) {
                        System.out.println(error.getMessage());
                    }
                }

                while (true) {
                    System.out.print("Enter new Staff Email Address (or leave blank to keep current): ");
                    String email = scanner.nextLine();
                    if (email.isEmpty())
                        break;
                    try {
                        staff.updateEmailAddress(email);
                    } catch (Exception error) {
                        System.out.println(error.getMessage());
                    }
                }

                if (!newName.isEmpty())
                    staff.setName(newName);
                // if (!newRole.isEmpty())
                // staff.setRole(newRole);
                // if (!newGender.isEmpty())
                // staff.setGender(newGender);

                saveStaffData(); // Save changes to the file
                System.out.println("Staff member updated successfully.");
                return;
            }
        }
        System.out.println("Staff member not found.");
    }

    private void removeStaff(Scanner scanner) {
        System.out.println("Enter Staff ID to remove:");
        String id = scanner.nextLine();
        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).id.equals(id)) {
                staffList.remove(i);
                saveStaffData(); // Save changes to the file
                System.out.println("Staff member removed successfully.");
                return;
            }
        }
        System.out.println("Staff member not found.");
    }

    private void filterStaff(Scanner scanner) {
        System.out.println("Filter Staff Menu:");
        System.out.println("1. Filter by Role");
        System.out.println("2. Filter by Gender");
        System.out.println("3. Back to Manage Staff Menu");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.println("Enter Role to filter:");
                String role = scanner.nextLine();
                staffList.stream()
                        .filter(staff -> staff.getRole().equalsIgnoreCase(role))
                        .forEach(System.out::println);
                break;
            case 2:
                System.out.println("Enter Gender to filter:");
                String gender = scanner.nextLine();
                staffList.stream()
                        .filter(staff -> staff.getGender().equalsIgnoreCase(gender))
                        .forEach(System.out::println);
                break;
            case 3:
                System.out.println("Returning to Manage Staff Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        System.out.println("Press Enter to return to the Manage Staff menu.");
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void viewAppointments(Scanner scanner) {
        System.out.println("Viewing appointments...");
        for (Appointment appointment : appointmentList) {
            System.out.println(appointment);
        }
        System.out.println("Press Enter to return to the admin menu.");
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void manageInventory(Scanner scanner) {
        int choice;
        do {
            System.out.println("Manage Inventory Menu:");
            System.out.println("1. View Inventory");
            System.out.println("2. Add Inventory Item");
            System.out.println("3. Update Inventory Item");
            System.out.println("4. Remove Inventory Item");
            System.out.println("5. Approve Replenishment Request");
            System.out.println("6. Back to Admin Menu");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewInventory(scanner);
                    break;
                case 2:
                    addInventoryItem(scanner);
                    break;
                case 3:
                    updateInventoryItem(scanner);
                    break;
                case 4:
                    removeInventoryItem(scanner);
                    break;
                case 5:
                    approveReplenishmentRequest(scanner);
                    break;
                case 6:
                    System.out.println("Returning to Admin Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }

    private void viewInventory(Scanner scanner) {
        System.out.println("Current Inventory:");
        inventory.loadFromCSV();
        inventory.getMedications().forEach(System.out::println);
        System.out.println("Press Enter to return to the Manage Inventory menu.");
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void addInventoryItem(Scanner scanner) {
        System.out.println("Enter Medicine Name:");
        String name = scanner.nextLine();
        System.out.println("Enter Initial Stock Level:");
        int stock = scanner.nextInt();
        System.out.println("Enter Low Stock Alert Level:");
        int lowStockLevel = scanner.nextInt();
        inventory.addMedication(new Medication(name, stock, lowStockLevel));
        saveInventoryData(); // Save changes to the file
        System.out.println("Inventory item added successfully.");
    }

    private void updateInventoryItem(Scanner scanner) {
        System.out.println("Enter Medicine Name to update:");
        String name = scanner.nextLine();
        Medication item = inventory.getMedication(name);
        if (item != null) {
            System.out.println("Updating Inventory Item: " + item);
            System.out.println("Enter new Stock Level (or leave blank to keep current):");
            String newStock = scanner.nextLine();
            System.out.println("Enter new Low Stock Alert Level (or leave blank to keep current):");
            String newLowStock = scanner.nextLine();

            if (!newStock.isEmpty())
                item.updateStockLevel(Integer.parseInt(newStock));
            if (!newLowStock.isEmpty())
                item.setLowStockAlertLevel(Integer.parseInt(newLowStock));

            saveInventoryData(); // Save changes to the file
            System.out.println("Inventory item updated successfully.");
        } else {
            System.out.println("Inventory item not found.");
        }
    }

    private void removeInventoryItem(Scanner scanner) {
        System.out.println("Enter Medicine Name to remove:");
        String name = scanner.nextLine();
        if (inventory.removeMedication(name)) {
            saveInventoryData(); // Save changes to the file
            System.out.println("Inventory item removed successfully.");
        } else {
            System.out.println("Inventory item not found.");
        }
    }

    private void approveReplenishmentRequest(Scanner scanner) {
        System.out.println("Enter Replenishment Request ID to approve:");
        String requestID = scanner.nextLine();
        List<ReplenishmentRequest> requests = ReplenishmentRequest.loadFromCSV("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/replenishment_requests.csv");

        for (ReplenishmentRequest request : requests) {
            if (request.getRequestID().equals(requestID)) {
                Medication item = inventory.getMedication(request.getMedicationName());
                if (item != null) {
                    item.updateStockLevel(item.getStockLevel() + request.getQuantity());
                    request.setStatus("Approved");
                    saveInventoryData(); // Save changes to the inventory file
                    saveReplenishmentRequests(requests); // Save changes to the replenishment requests file
                    System.out.println("Replenishment request approved for " + request.getMedicationName());
                } else {
                    System.out.println("Inventory item not found.");
                }
                return;
            }
        }
        System.out.println("Replenishment request not found.");
    }

    private void saveReplenishmentRequests(List<ReplenishmentRequest> requests) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/replenishment_requests.csv"))) {
            writer.println("requestID,medicationName,quantity,status");
            for (ReplenishmentRequest request : requests) {
                writer.printf("%s,%s,%d,%s%n", request.getRequestID(), request.getMedicationName(), request.getQuantity(), request.getStatus());
            }
        } catch (IOException e) {
            System.out.println("Error saving replenishment requests: " + e.getMessage());
        }
    }

    private void loadStaffData() {

        try {
            this.staffList.clear();

            BufferedReader file = new BufferedReader(new FileReader("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/users.csv"));
            String nextLine = file.readLine();
            while ((nextLine = file.readLine()) != null) {
                String[] user = nextLine.split(",");
                String role = user[3];

                // Ignore all other types of users. Only handle Doctor and Pharmacist
                if (role.equals("doctor")) {
                    this.staffList.add(new Doctor(user[0], user[1], user[2]));
                } else if (role.equals("pharmacist")) {
                    this.staffList.add(new Pharmacist(user[0], user[1], user[2]));
                }
            }
            file.close();
        } catch (IOException error) {
            System.out.println("Error loading staff data: " + error.getMessage());
        }

    }

    private void loadAppointmentData() {
        // Implement file loading logic for appointment data
        // You can follow a similar structure to loadStaffData
    }

    private void loadInventoryData() {
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/inventory.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                String[] data = line.split(",");
                if (data.length == 3) {
                    try {
                        int stockLevel = Integer.parseInt(data[1]);
                        int lowStockAlertLevel = Integer.parseInt(data[2]);
                        inventory.addMedication(new Medication(data[0], stockLevel, lowStockAlertLevel));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data format in inventory file: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in inventory file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading inventory data: " + e.getMessage());
        }
    }
    private void saveStaffData() {
        for (Staff staff : staffList) {
            try {
                staff.save();
            } catch (IOException e) {
                System.out.println("Error saving staff data: " + e.getMessage());
            }
        }
        // try (BufferedWriter bw = new BufferedWriter(new
        // FileWriter("staff_data.csv"))) {
        // for (Staff staff : staffList) {
        // bw.write(staff.id + "," + staff.name + "," + staff.getRole() + "," +
        // staff.getGender());
        // bw.newLine();
        // }
        // } catch (IOException e) {
        // System.out.println("Error saving staff data: " + e.getMessage());
        // }
    }

    private void saveInventoryData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/inventory.csv"))) {
            // Write the header
            bw.write("medicationName,stockLevel,lowStockAlertLevel");
            bw.newLine();

            // Write the medication data
            for (Medication item : inventory.getMedications()) {
                bw.write(item.getMedicationName() + "," + item.getStockLevel() + "," + item.getLowStockAlertLevel());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory data: " + e.getMessage());
        }
    }
}