package hms;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Prescription {
    private String id;
    private String medicationName;
    private int quantity;
    private PrescriptionStatus status;


    public Prescription(String id, String medicationName, int quantity, PrescriptionStatus status) {
        this.id = id;
        this.medicationName = medicationName;
        this.quantity = quantity;
        this.status = status;
    }

    public String getID() {
        return this.id;
    }
    public int getQuantity(){return this.quantity;}
    public void setQuantity(int quantity){this.quantity = quantity;}
    public String getName(){return this.medicationName;}

    public String getMedicationName() {
        return medicationName;
    }

    public PrescriptionStatus getStatus() {
        return status;
    }

    public void updateStatus(PrescriptionStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return "Prescription ID: " + this.id +
                ", Medication Name: " + this.medicationName +" Quantity: "+this.quantity+
                ", Status: " + this.status.toString();
    }

    public void save() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(".../data/prescription.csv"));
        try (FileOutputStream output = new FileOutputStream(".../data/prescription.csv")) {
            boolean isEntryFound = false;
            for (int i = 0; i < lines.size(); i++) {
                String[] appt = lines.get(i).split(",");
                if (appt.length == 4 && appt[0].equals(this.id)) {
                    String newEntry = this.id + "," + this.medicationName + "," + this.quantity + "," + this.status.toString().toLowerCase() + "\n";
                    output.write(newEntry.getBytes());
                    isEntryFound = true;
                } else {
                    String line = lines.get(i) + "\n";
                    output.write(line.getBytes());
                }
            }
            if (!isEntryFound) {
                String newEntry = this.id + "," + this.medicationName + "," + this.quantity + "," + this.status.toString().toLowerCase() + "\n";
                output.write(newEntry.getBytes());
            }
        }
    }


    public static List<Prescription> getAll() throws IOException {
        List<Prescription> array = new ArrayList<>();
        try (BufferedReader file = new BufferedReader(new FileReader(".../data/prescription.csv"))) {
            String nextLine;
            boolean isFirstLine = true;
            while ((nextLine = file.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                String[] prescription = nextLine.split(",");
                PrescriptionStatus status = prescription[3].equals("dispensed") ? PrescriptionStatus.Dispensed : PrescriptionStatus.Pending;
                int quantity = Integer.parseInt(prescription[2]);
                array.add(new Prescription(prescription[0], prescription[1], quantity, status));
            }
        }
        return array;
    }

}
