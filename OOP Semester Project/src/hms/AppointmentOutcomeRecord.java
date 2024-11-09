package hms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AppointmentOutcomeRecord {
    private String appointmentID;
    private Date dateOfAppointment;
    private String serviceType;
    private List<Prescription> prescribedMedications;
    private String consultationNotes;

    public AppointmentOutcomeRecord(String appointmentID, Date dateOfAppointment, String serviceType, List<Prescription> prescribedMedications, String consultationNotes) {
        this.appointmentID = appointmentID;
        this.dateOfAppointment = dateOfAppointment;
        this.serviceType = serviceType;
        this.prescribedMedications = prescribedMedications;
        this.consultationNotes = consultationNotes;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Date getDateOfAppointment() {
        return dateOfAppointment;
    }

    public void setDateOfAppointment(Date dateOfAppointment) {
        this.dateOfAppointment = dateOfAppointment;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public List<Prescription> getPrescribedMedications() {
        return prescribedMedications;
    }

    public void setPrescribedMedications(List<Prescription> prescribedMedications) {
        this.prescribedMedications = prescribedMedications;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }

    // Taken from Pharmacist
    public static List<AppointmentOutcomeRecord> getAllRecords() {
        List<AppointmentOutcomeRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("../data/appointment_outcome_records.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 5) {
                    System.err.println("Skipping malformed record: " + line);
                    continue; // Skip malformed records
                }
                String appointmentID = values[0];
                Date dateOfAppointment = new Date(Long.parseLong(values[1])); // You may need to parse the date string
                                                                              // appropriately
                String serviceType = values[2];
                List<String> prescriptionIds = Arrays.asList(values[3].split(";"));
                List<Prescription> prescribedMedications = Prescription.getAll();
                prescribedMedications.removeIf(p -> prescriptionIds.contains(p.getID())); // Implement
                                                                                          // parsePrescriptions method
                String consultationNotes = values[4];

                AppointmentOutcomeRecord record = new AppointmentOutcomeRecord(appointmentID, dateOfAppointment,
                        serviceType, prescribedMedications, consultationNotes);
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
    for (Prescription prescription : prescribedMedications) {
        sb.append(prescription.toString()).append("; ");
    }
    sb.append("\nConsultation Notes: ").append(consultationNotes);
    return sb.toString();
}

}
