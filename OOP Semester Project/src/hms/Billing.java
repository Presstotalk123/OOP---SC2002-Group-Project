package hms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Billing {
    private static final String BILLING_FILE = "C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\billing.csv";
    private static final String BLOCKCHAIN_FILE = "C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\blockchain.dat";
    private static Blockchain blockchain = new Blockchain();

    public static class Bill {
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
            return id + "," + patientId + "," + description + "," + amount + "," + paid;
        }
    }

    public static void addBill(Bill bill) throws IOException {
        blockchain.addBlock(new Block(bill.toString(), blockchain.getLatestBlock().hash));
        saveBlockchain();
        updateBillingFile(bill);
    }

    public static List<Bill> getAllBills() throws IOException {
        List<Bill> bills = new ArrayList<>();
        for (Block block : blockchain.chain) {
            if (!block.getData().equals("Genesis Block")) {
                String[] parts = block.getData().split(",");
                bills.add(new Bill(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), Boolean.parseBoolean(parts[4])));
            }
        }
        return bills;
    }

    public static void updateBill(String id, boolean paid) throws IOException {
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

    private static void saveBlockchain() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BLOCKCHAIN_FILE))) {
            oos.writeObject(blockchain);
        }
    }

    private static void loadBlockchain() throws IOException, ClassNotFoundException {
        File file = new File(BLOCKCHAIN_FILE);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                blockchain = (Blockchain) ois.readObject();
            }
        } else {
            blockchain = new Blockchain();
        }
    }

    private static void updateBillingFile(Bill bill) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BILLING_FILE, true))) {
            writer.write(bill.toString());
            writer.newLine();
        }
    }

    public static boolean verifyBlockchain() {
        return blockchain.isChainValid();
    }
    
    private static void updateBillingFile(List<Bill> bills) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BILLING_FILE))) {
            writer.write("id,patientId,description,amount,paid");
            writer.newLine();
            for (Bill bill : bills) {
                writer.write(bill.toString());
                writer.newLine();
            }
        }
    }

    static {
        try {
            loadBlockchain();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
