package hms;

import hms.Appointments.Appointment;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Administrator extends User {
    private Billing billing;
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
        billing=new Billing();
        loadStaffData();
        loadAppointmentData();
        loadInventoryData();
    }

    public Administrator(String id, String name, String password) {
        super(id, name, password, "administrator");
        staffList = new ArrayList<>();
        appointmentList = new ArrayList<>();
        inventory = new Inventory();
        billing=new Billing();
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
        System.out.println("5. Create Bill");
        System.out.println("6. View Bills");
        System.out.println("7. Verify Blockchain Integrity");
        System.out.println("8. View Feedback");
        System.out.println("9. Log Out");
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
                createBill(scanner);
                break;
            case 6:
                viewBills();
                break;
            case 7:
                verifyBlockchain();
                break;
            case 8:
                viewFeedback();
                break;
            case 9:
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

    private void ensureBillingInitialized() {
        if (billing == null) {
            billing = new Billing();
        }
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
        inventory.loadFromCSV(); // Ensure inventory data is up-to-date
        
        // Print header for better readability
        System.out.printf("%-20s %-10s %-12s %-20s%n", "Medication Name", "Price", "Stock Level", "Low Stock Alert");
        System.out.println("--------------------------------------------------------------");
        
        for (Medication item : inventory.getMedications()) {
            System.out.printf("%-20s %-10d %-12d %-20d%n", 
                              item.getMedicationName(), 
                              item.getPrice(), 
                              item.getStockLevel(), 
                              item.getLowStockAlertLevel());
        }
    
        System.out.println("Press Enter to return to the Manage Inventory menu.");
        scanner.nextLine(); // Wait for user input to return to the menu
    }
    

    private void addInventoryItem(Scanner scanner) {
        System.out.println("Enter Medicine Name:");
        String name = scanner.nextLine();
        System.out.println("Enter Price:");
        int price=scanner.nextInt();
        System.out.println("Enter Initial Stock Level:");
        int stock = scanner.nextInt();
        System.out.println("Enter Low Stock Alert Level:");
        int lowStockLevel = scanner.nextInt();
        inventory.addMedication(new Medication(name, price, stock, lowStockLevel));
        saveInventoryData(); // Save changes to the file
        System.out.println("Inventory item added successfully.");
    }

    private void updateInventoryItem(Scanner scanner) {
        System.out.println("Enter Medicine Name to update:");
        String name = scanner.nextLine();
        Medication item = inventory.getMedication(name);
        if (item != null) {
            System.out.println("Updating Inventory Item: " + item);
            
            // Prompt to update price
            System.out.println("Enter new Price (or leave blank to keep current):");
            String newPrice = scanner.nextLine();
            
            // Prompt to update stock level
            System.out.println("Enter new Stock Level (or leave blank to keep current):");
            String newStock = scanner.nextLine();
            
            // Prompt to update low stock alert level
            System.out.println("Enter new Low Stock Alert Level (or leave blank to keep current):");
            String newLowStock = scanner.nextLine();
    
            // Update item attributes if inputs are provided
            if (!newPrice.isEmpty()) {
                item.setPrice(Integer.parseInt(newPrice));
            }
            if (!newStock.isEmpty()) {
                item.updateStockLevel(Integer.parseInt(newStock));
            }
            if (!newLowStock.isEmpty()) {
                item.setLowStockAlertLevel(Integer.parseInt(newLowStock));
            }
    
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
        List<ReplenishmentRequest> requests = ReplenishmentRequest.loadFromCSV("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\replenishment_requests.csv");

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
        try (PrintWriter writer = new PrintWriter(new FileWriter("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\replenishment_requests.csv"))) {
            writer.println("requestID,medicationName,quantity,status");
            for (ReplenishmentRequest request : requests) {
                writer.printf("%s,%s,%d,%s%n", request.getRequestID(), request.getMedicationName(), request.getQuantity(), request.getStatus());
            }
        } catch (IOException e) {
            System.out.println("Error saving replenishment requests: " + e.getMessage());
        }
    }

    public void createBill(Scanner scanner) throws IOException {
        ensureBillingInitialized();  // Add this line to make sure billing is initialized
    
        System.out.print("Enter patient ID: ");
        String patientId = scanner.nextLine();
    
        List<String> prescriptionIds = new ArrayList<>();
        while (true) {
            System.out.print("Enter prescription ID: ");
            String prescriptionId = scanner.nextLine();
            if (!prescriptionId.isEmpty()) {
                prescriptionIds.add(prescriptionId);
            }
    
            System.out.print("Do you want to add another prescription? (yes/no): ");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("no")) {
                break;
            }
        }
    
        String billId = generateId(); // Generate a unique bill ID
    
        try {
            billing.addBill(billId, patientId, prescriptionIds);
            System.out.println("Bill created successfully with ID: " + billId);
        } catch (Exception e) {
            System.out.println("Failed to create bill: " + e.getMessage());
        }
    }    

    public void viewBills() throws IOException {
        ensureBillingInitialized(); // Ensure that the billing is initialized
    
        List<Billing.Bill> bills = billing.getAllBills();
        if (bills.isEmpty()) {
            System.out.println("No bills found.");
            return;
        }
    
        // Print the header for better readability
        System.out.printf("%-15s %-12s %-50s %-10s %-6s%n", "Bill ID", "Patient ID", "Description", "Amount", "Is Paid");
        System.out.println("---------------------------------------------------------------------------------------------------------");
    
        // Print each bill
        for (Billing.Bill bill : bills) {
            System.out.printf("%-15s %-12s %-50s %-10.2f %-6b%n",
                              bill.id,
                              bill.patientId,
                              bill.description.length() > 50 ? bill.description.substring(0, 47) + "..." : bill.description,
                              bill.amount,
                              bill.paid);
        }
    
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }
    
    private String generateId() {
        return "BILL" + System.currentTimeMillis();
    }

    private void viewFeedback() {
        try {
            List<String> feedbackList = Feedback.viewAllFeedback();
            if (feedbackList.isEmpty()) {
                System.out.println("No feedback available.");
            } else {
                System.out.println("Feedback List:");
                System.out.printf("| %-12s | %-12s | %-50s | %-6s |%n", "Feedback ID", "Patient ID", "Comments", "Rating");
                System.out.println("+--------------+--------------+----------------------------------------------------+--------+");
    
                for (String feedback : feedbackList) {
                    String[] feedbackDetails = feedback.split(",");
                    
                    if (feedbackDetails.length == 4) { // Ensure data integrity
                        String feedbackId = feedbackDetails[0].trim();
                        String patientId = feedbackDetails[1].trim();
                        String comments = feedbackDetails[2].trim();
                        String rating = feedbackDetails[3].trim();
    
                        // Break comments into multiple lines if too long
                        List<String> wrappedComments = wrapText(comments, 50);
                        for (int i = 0; i < wrappedComments.size(); i++) {
                            if (i == 0) {
                                System.out.printf("| %-12s | %-12s | %-50s | %-6s |%n", 
                                                  feedbackId, patientId, wrappedComments.get(i), rating);
                            } else {
                                System.out.printf("| %-12s | %-12s | %-50s | %-6s |%n", 
                                                  "", "", wrappedComments.get(i), "");
                            }
                        }
                    }
                }
                System.out.println("+--------------+--------------+----------------------------------------------------+--------+");
            }
        } catch (IOException e) {
            System.out.println("Error loading feedback: " + e.getMessage());
        }
    }
    
    // Helper method to wrap text to a specified width
    private List<String> wrapText(String text, int width) {
        List<String> wrappedLines = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + width, text.length());
            wrappedLines.add(text.substring(start, end));
            start = end;
        }
        return wrappedLines;
    }
    
    
    
    private void verifyBlockchain() {
        if (Billing.verifyBlockchain()) {
            System.out.println("Blockchain is valid.");
        } else {
            System.out.println("Blockchain integrity compromised!");
        }        
    }
    

    private void loadStaffData() {

        try {
            this.staffList.clear();

            BufferedReader file = new BufferedReader(new FileReader("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\users.csv"));
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
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\inventory.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                String[] data = line.split(",");
                if (data.length >= 3) { // Allow missing lowStockAlertLevel
                    try {
                        String medicationName = data[0];
                        int price = Integer.parseInt(data[1]);
                        int stockLevel = Integer.parseInt(data[2]);
                        int lowStockAlertLevel = (data.length == 4) ? Integer.parseInt(data[3]) : 10; // Default alert level
                        inventory.addMedication(new Medication(medicationName, price, stockLevel, lowStockAlertLevel));
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\inventory.csv"))) {
            // Write the header
            bw.write("medicationName,price,stockLevel,lowStockAlertLevel");
            bw.newLine();
    
            // Write each medication's data
            for (Medication item : inventory.getMedications()) {
                bw.write(item.getMedicationName() + "," + 
                         item.getPrice() + "," + 
                         item.getStockLevel() + "," + 
                         item.getLowStockAlertLevel());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory data: " + e.getMessage());
        }
    }
    
}
