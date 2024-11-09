package hms;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppointmentOutcomeRecord {
    private String appointmentID;
    private LocalDateTime dateOfAppointment;
    private String serviceType;
    private String patientID;
    private String diagnosis;
    private String treatmentPlan;
    private List<Prescription> prescriptions;
    private String consultationNotes;

    public AppointmentOutcomeRecord(String appointmentID, LocalDateTime dateOfAppointment, String serviceType,
            List<Prescription> prescriptions, String consultationNotes, String patientID, String diagnosis,
            String treatmentPlan) {
        this.appointmentID = appointmentID;
        this.dateOfAppointment = dateOfAppointment;
        this.serviceType = serviceType;
        this.prescriptions = prescriptions;
        this.consultationNotes = consultationNotes;
        this.patientID = patientID;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public LocalDateTime getDateOfAppointment() {
        return dateOfAppointment;
    }

    public void setDateOfAppointment(LocalDateTime dateOfAppointment) {
        this.dateOfAppointment = dateOfAppointment;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public List<Prescription> getPrescribedMedications() {
        return prescriptions;
    }

    public void setPrescribedMedications(List<Prescription> prescribedMedications) {
        this.prescriptions = prescribedMedications;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }

    public String getPatientID() {
        return this.patientID;
    }

    public String getDiagnosis() {
        return this.diagnosis;
    }

    public String getTreatmentPlan() {
        return this.treatmentPlan;
    }

    public void save() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("../data/appointment_outcome_records.csv"));
        FileOutputStream output = new FileOutputStream("../data/appointment_outcome_records.csv");

        boolean isEntryFound = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] patient = lines.get(i).split(",");

            if (patient.length == 8 && patient[0].equals(this.appointmentID)) {
                String newEntry = this.appointmentID + "," + this.dateOfAppointment + "," + this.serviceType + "," +
                String.join(";", this.prescriptions.stream().map(p -> p.getID()).toList()) + "," + this.consultationNotes + "," +
                this.patientID + "," + this.diagnosis + "," + this.treatmentPlan + "\n";
                output.write(newEntry.getBytes());
                isEntryFound = true;
            } else {
                String line = lines.get(i) + "\n";
                output.write(line.getBytes());
            }
        }

        // If the patient is not found, append a new entry
        if (!isEntryFound) {
            String newEntry = this.appointmentID + "," + this.dateOfAppointment + "," + this.serviceType + "," +
                    String.join(";", this.prescriptions.stream().map(p -> p.getID()).toList()) + "," + this.consultationNotes + "," +
                    this.patientID + "," + this.diagnosis + "," + this.treatmentPlan + "\n";
            output.write(newEntry.getBytes());
        }

        output.close();
    }

    // Taken from Pharmacist
    public static List<AppointmentOutcomeRecord> getAllRecords() {
        List<AppointmentOutcomeRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("../data/appointment_outcome_records.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 5) {
                    System.err.println("Skipping malformed record: " + line);
                    continue; // Skip malformed records
                }
                String appointmentID = values[0];
                LocalDateTime dateOfAppointment = LocalDateTime.parse(values[1]); // You may need to parse the date
                                                                                  // string
                // appropriately
                String serviceType = values[2];
                List<String> prescriptionIds = Arrays.asList(values[3].split(";"));
                List<Prescription> prescribedMedications = Prescription.getAll();
                prescribedMedications.removeIf(p -> prescriptionIds.contains(p.getID())); // Implement
                                                                                          // parsePrescriptions method
                String consultationNotes = values[4];
                String patientID = values[5];
                String diagnosis = values[6];
                String treatmentPlan = values[7];

                AppointmentOutcomeRecord record = new AppointmentOutcomeRecord(appointmentID, dateOfAppointment,
                        serviceType, prescribedMedications, consultationNotes, patientID, diagnosis, treatmentPlan);
                records.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Appointment ID: ").append(appointmentID).append("\n");
        sb.append("Date of Appointment: ").append(dateOfAppointment).append("\n");
        sb.append("Service Type: ").append(serviceType).append("\n");
        sb.append("Prescribed Medications: ");
        for (Prescription prescription : this.prescriptions) {
            sb.append(prescription.toString()).append("; ");
        }
        sb.append("\nConsultation Notes: ").append(consultationNotes);
        return sb.toString();
    }

}
