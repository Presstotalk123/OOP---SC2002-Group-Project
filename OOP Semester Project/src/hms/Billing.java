package hms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Billing {
    private static final String BILLING_FILE = "C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\billing.csv";
    private static final String BLOCKCHAIN_FILE = "C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\blockchain.dat";
    private static final String PRESCRIPTION_FILE = "C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\prescription.csv";
    private static final String INVENTORY_FILE = "C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\inventory.csv";

    private static Blockchain blockchain;

    public Billing() {
        try {
            loadBlockchain();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public class Bill {
        public String id;
        public String patientId;
        public String description;
        public double amount;
        public boolean paid;

        public Bill(String id, String patientId, String description, double amount, boolean paid) {
            this.id = id;
            this.patientId = patientId;
            this.description = description;
            this.amount = amount;
            this.paid = paid;
        }

        @Override
        public String toString() {
            return String.format("\"%s\",\"%s\",\"%s\",%.2f,%b", id, patientId, description.replace("\"", "\"\""), amount, paid);
        }
    }

    public void addBill(String billId, String patientId, List<String> prescriptionIds) throws IOException {
        double totalAmount = 0;
        for (String prescriptionId : prescriptionIds) {
            if (!isValidPrescription(prescriptionId)) {
                System.out.println("Invalid prescription ID: " + prescriptionId);
                continue;
            }
            totalAmount += calculateBillAmountWithoutDoctorFee(prescriptionId); // Updated method to exclude doctor's fee
        }
    
        // Add the doctor's fee once for the entire bill
        totalAmount += 50;
    
        String description = "Billing for prescription IDs: " + String.join(", ", prescriptionIds);
    
        Bill bill = new Bill(billId, patientId, description, totalAmount, false);
        blockchain.addBlock(new Block(bill.toString(), blockchain.getLatestBlock().hash));
        saveBlockchain();
        updateBillingFile(bill);
    }
    
    public double calculateBillAmountWithoutDoctorFee(String prescriptionId) throws IOException {
        double totalAmount = 0;
    
        try (BufferedReader prescriptionReader = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            prescriptionReader.readLine(); // Skip header
            while ((line = prescriptionReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(prescriptionId)) {
                    String medicationName = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
    
                    double medicationPrice = getMedicationPriceFromInventory(medicationName);
                    totalAmount += quantity * medicationPrice;
                }
            }
        }
    
        return totalAmount;
    }
    

    public double calculateBillAmount(String prescriptionId) throws IOException {
        double totalAmount = 0;

        try (BufferedReader prescriptionReader = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            prescriptionReader.readLine(); // Skip header
            while ((line = prescriptionReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(prescriptionId)) {
                    String medicationName = parts[1];
                    int quantity = Integer.parseInt(parts[2]);

                    double medicationPrice = getMedicationPriceFromInventory(medicationName);
                    totalAmount += quantity * medicationPrice;
                }
            }
        }

        // Adding doctor's fee
        totalAmount += 50;
        return totalAmount;
    }

    private double getMedicationPriceFromInventory(String medicationName) throws IOException {
        try (BufferedReader inventoryReader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            inventoryReader.readLine(); // Skip header
            while ((line = inventoryReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equalsIgnoreCase(medicationName)) {
                    return Double.parseDouble(parts[1]); // Assuming price is in the second column
                }
            }
        }
        throw new IllegalArgumentException("Medication not found in inventory: " + medicationName);
    }

    public List<Bill> getAllBills() throws IOException {
        List<Bill> bills = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BILLING_FILE))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                // Skip the header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
    
                // Skip blank lines
                if (line.trim().isEmpty()) {
                    continue;
                }
    
                // Split the line using the appropriate pattern to handle quoted fields
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    
                // Ensure the parts array has the correct number of elements
                if (parts.length < 5) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }
    
                try {
                    String id = parts[0].replace("\"", "").trim();
                    String patientId = parts[1].replace("\"", "").trim();
                    String description = parts[2].replace("\"", "").trim();
                    double amount = Double.parseDouble(parts[3].trim());
                    boolean paid = Boolean.parseBoolean(parts[4].trim());
                    
                    bills.add(new Bill(id, patientId, description, amount, paid));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line due to number format error: " + line);
                }
            }
        }
        return bills;
    }
    

    public void updateBill(String id, boolean paid) throws IOException {
        List<Bill> bills = getAllBills();
        for (Bill bill : bills) {
            if (bill.id.equals(id)) {
                bill.paid = paid;
                break;
            }
        }
        blockchain = new Blockchain();
        for (Bill bill : bills) {
            blockchain.addBlock(new Block(bill.toString(), blockchain.getLatestBlock().hash));
        }
        saveBlockchain();
        updateBillingFile(bills);
    }

    private void saveBlockchain() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BLOCKCHAIN_FILE))) {
            oos.writeObject(blockchain);
        }
    }

    private void loadBlockchain() throws IOException, ClassNotFoundException {
        File file = new File(BLOCKCHAIN_FILE);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                blockchain = (Blockchain) ois.readObject();
            }
        } else {
            blockchain = new Blockchain();
        }
    }

    private void updateBillingFile(Bill bill) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BILLING_FILE, true))) {
            writer.write(bill.toString());
            writer.newLine();
        }
    }

    private void updateBillingFile(List<Bill> bills) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BILLING_FILE))) {
            writer.write("id,patientId,description,amount,paid");
            writer.newLine();
            for (Bill bill : bills) {
                writer.write(bill.toString());
                writer.newLine();
            }
        }
    }

    public static boolean verifyBlockchain() {
        return blockchain.isChainValid();
    }

    private boolean isValidPrescription(String prescriptionId) throws IOException {
        try (BufferedReader prescriptionReader = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            prescriptionReader.readLine(); // Skip header
            while ((line = prescriptionReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(prescriptionId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
