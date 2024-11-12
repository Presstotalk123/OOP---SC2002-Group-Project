package hms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Medication> medications;

    public Inventory() {
        medications = new ArrayList<>();
    }

    public void addMedication(Medication medication) {
        medications.add(medication);
    }

    public boolean removeMedication(String medicationName) {
        medications.removeIf(med -> med.getMedicationName().equals(medicationName));
        return true;
    }

    public void updateStockLevel(String medicationName, int quantity) {
        for (Medication med : medications) {
            if (med.getMedicationName().equals(medicationName)) {
                med.updateStockLevel(quantity);
                break;
            }
        }
    }

    public Medication getMedication(String medicationName) {
        loadFromCSV();
        for (Medication med : medications) {
            if (med.getMedicationName().equals(medicationName)) {
                return med;
            }
        }
        return null;
    }

    public List<Medication> getMedications() {
        return medications;
    }
    // Inventory.java


    /**
     * Saves the inventory to a CSV file.
     *
     * @param filePath the path of the CSV file.
     */
    public void saveToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Name,Price,StockLevel,LowStockAlertLevel\n"); // Write the header
            for (Medication med : medications) {
                writer.write(String.format("%s,%d,%d,%d\n", med.getMedicationName(), med.getPrice(), med.getStockLevel(), med.getLowStockAlertLevel()));
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory data: " + e.getMessage());
        }
    }



    public void loadFromCSV() {
        medications.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\inventory.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                String[] values = line.split(",");
                if (values.length == 4) {
                    try {
                        String medicationName = values[0];
                        int price = Integer.parseInt(values[1]);
                        int stockLevel = Integer.parseInt(values[2]);
                        int lowStockAlertLevel = Integer.parseInt(values[3]);
                        medications.add(new Medication(medicationName, price, stockLevel, lowStockAlertLevel));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data format in inventory file: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in inventory file: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
