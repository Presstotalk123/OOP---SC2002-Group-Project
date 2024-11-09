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
    private PrescriptionStatus status;

    public Prescription(String id, String medicationName, PrescriptionStatus status) {
        this.id = id;
        this.medicationName = medicationName;
        this.status = status;
    }

    public String getID() {
        return this.id;
    }

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
                ", Medication Name: " + this.medicationName +
                ", Status: " + this.status.toString();
    }

    public void save() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("../data/prescription.csv"));
        FileOutputStream output = new FileOutputStream("../data/prescription.csv");

        boolean isEntryFound = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] appt = lines.get(i).split(",");

            if (appt.length == 3 && appt[0].equals(this.id)) {
                String newEntry = this.id + "," + (this.medicationName) + "," + this.status.toString().toLowerCase()
                        + "\n";
                output.write(newEntry.getBytes());
                isEntryFound = true;
            } else {
                String line = lines.get(i) + "\n";
                output.write(line.getBytes());
            }
        }

        if (!isEntryFound) {
            String newEntry = this.id + "," + (this.medicationName) + "," + this.status.toString().toLowerCase() + "\n";
            output.write(newEntry.getBytes());
        }

        output.close();
    }

    public static List<Prescription> getAll() throws IOException {
        List<Prescription> array = new ArrayList<Prescription>();
        BufferedReader file = new BufferedReader(new FileReader("../data/prescription.csv"));
        String nextLine = file.readLine();
        while ((nextLine = file.readLine()) != null) {
            String[] prescription = nextLine.split(",");
            // TOOD: Add support for all kinds of users
            PrescriptionStatus status = prescription[2].equals("dispensed") ? PrescriptionStatus.Dispensed : PrescriptionStatus.Pending;
            array.add(new Prescription(prescription[0], prescription[1], status));
        }
        file.close();
        return array;
    }

}