package hms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hms.Appointments.Appointment;

public class HMSAdmin extends User {
    public static String role = "admin";
    
    private List<Staff> staffList;
    private List<Appointment> appointmentList;
    private Inventory inventory;

    public HMSAdmin(String id, String name, String password) {
        super(id, name, password);
        staffList = new ArrayList<>();
        appointmentList = new ArrayList<>();
        inventory = new Inventory();
        loadStaffData();
        loadAppointmentData();
        loadInventoryData();
    }

    public boolean eventLoop(Scanner scanner) {
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

        // From Mikko: Changed this as new Scanner(System.in).nextLine() causes a memory
        // leak
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void manageStaff(Scanner scanner) {
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

    private void addStaff(Scanner scanner) {
        System.out.println("Enter Staff ID:");
        String id = scanner.nextLine();
        System.out.println("Enter Staff Name:");
        String name = scanner.nextLine();
        System.out.println("Enter Staff Role:");
        String role = scanner.nextLine();
        System.out.println("Enter Staff Gender:");
        String gender = scanner.nextLine();

        staffList.add(new Staff(id, name, role, gender));
        saveStaffData(); // Save changes to the file
        System.out.println("Staff member added successfully.");
    }

    private void updateStaff(Scanner scanner) {
        System.out.println("Enter Staff ID to update:");
        String id = scanner.nextLine();
        for (Staff staff : staffList) {
            if (staff.getId().equals(id)) {
                System.out.println("Updating Staff: " + staff);
                System.out.println("Enter new Staff Name (or leave blank to keep current):");
                String newName = scanner.nextLine();
                System.out.println("Enter new Staff Role (or leave blank to keep current):");
                String newRole = scanner.nextLine();
                System.out.println("Enter new Staff Gender (or leave blank to keep current):");
                String newGender = scanner.nextLine();

                if (!newName.isEmpty())
                    staff.setName(newName);
                if (!newRole.isEmpty())
                    staff.setRole(newRole);
                if (!newGender.isEmpty())
                    staff.setGender(newGender);

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
            if (staffList.get(i).getId().equals(id)) {
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
        // From Mikko: Changed this as new Scanner(System.in).nextLine() causes a memory
        // leak
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
        inventory.getItems().forEach(System.out::println);
        System.out.println("Press Enter to return to the Manage Inventory menu.");
        // From Mikko: Changed this as new Scanner(System.in).nextLine() causes a memory
        // leak
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void addInventoryItem(Scanner scanner) {
        System.out.println("Enter Medicine Name:");
        String name = scanner.nextLine();
        System.out.println("Enter Initial Stock Level:");
        int stock = scanner.nextInt();
        System.out.println("Enter Low Stock Alert Level:");
        int lowStockLevel = scanner.nextInt();
        inventory.addItem(new InventoryItem(name, stock, lowStockLevel));
        saveInventoryData(); // Save changes to the file
        System.out.println("Inventory item added successfully.");
    }

    private void updateInventoryItem(Scanner scanner) {
        System.out.println("Enter Medicine Name to update:");
        String name = scanner.nextLine();
        InventoryItem item = inventory.findItem(name);
        if (item != null) {
            System.out.println("Updating Inventory Item: " + item);
            System.out.println("Enter new Stock Level (or leave blank to keep current):");
            String newStock = scanner.nextLine();
            System.out.println("Enter new Low Stock Alert Level (or leave blank to keep current):");
            String newLowStock = scanner.nextLine();

            if (!newStock.isEmpty())
                item.setStock(Integer.parseInt(newStock));
            if (!newLowStock.isEmpty())
                item.setLowStockLevel(Integer.parseInt(newLowStock));

            saveInventoryData(); // Save changes to the file
            System.out.println("Inventory item updated successfully.");
        } else {
            System.out.println("Inventory item not found.");
        }
    }

    private void removeInventoryItem(Scanner scanner) {
        System.out.println("Enter Medicine Name to remove:");
        String name = scanner.nextLine();
        if (inventory.removeItem(name)) {
            saveInventoryData(); // Save changes to the file
            System.out.println("Inventory item removed successfully.");
        } else {
            System.out.println("Inventory item not found.");
        }
    }

    private void approveReplenishmentRequest(Scanner scanner) {
        System.out.println("Enter Medicine Name to approve replenishment request:");
        String name = scanner.nextLine();
        InventoryItem item = inventory.findItem(name);
        if (item != null) {
            item.setStock(item.getStock() + 10); // Increase stock by 10 for example
            saveInventoryData(); // Save changes to the file
            System.out.println("Replenishment request approved for " + name);
        } else {
            System.out.println("Inventory item not found.");
        }
    }

    private void loadStaffData() {
        try (BufferedReader br = new BufferedReader(new FileReader("../data/staff_data.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                staffList.add(new Staff(data[0], data[1], data[2], data[3]));
            }
        } catch (IOException e) {
            System.out.println("Error loading staff data: " + e.getMessage());
        }
    }

    private void loadAppointmentData() {
        // Implement file loading logic for appointment data
        // You can follow a similar structure to loadStaffData
    }

    private void loadInventoryData() {
        try (BufferedReader br = new BufferedReader(new FileReader("inventory_data.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                inventory.addItem(new InventoryItem(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
        } catch (IOException e) {
            System.out.println("Error loading inventory data: " + e.getMessage());
        }
    }

    private void saveStaffData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("staff_data.csv"))) {
            for (Staff staff : staffList) {
                bw.write(staff.getId() + "," + staff.getName() + "," + staff.getRole() + "," + staff.getGender());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving staff data: " + e.getMessage());
        }
    }

    private void saveInventoryData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("inventory_data.csv"))) {
            for (InventoryItem item : inventory.getItems()) {
                bw.write(item.getName() + "," + item.getStock() + "," + item.getLowStockLevel());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory data: " + e.getMessage());
        }
    }
}
