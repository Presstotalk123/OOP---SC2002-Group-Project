import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    public void removeMedication(String medicationName) {
        medications.removeIf(med -> med.getMedicationName().equals(medicationName));
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

    /**
     * Saves the inventory to a CSV file.
     *
     * @param filePath the path of the CSV file.
     */
    public void saveToCSV(String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (Medication med : medications) {
                printWriter.printf("%s,%d,%d%n",
                        med.getMedicationName(),
                        med.getStockLevel(),
                        med.getLowStockAlertLevel());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the inventory from a CSV file.
     *
     * @param filePath the path of the CSV file.
     */
    public void loadFromCSV(String filePath) {
        medications.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String medicationName = values[0];
                int stockLevel = Integer.parseInt(values[1]);
                int lowStockAlertLevel = Integer.parseInt(values[2]);
                medications.add(new Medication(medicationName, stockLevel, lowStockAlertLevel));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}