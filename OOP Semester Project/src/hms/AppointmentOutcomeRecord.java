package hms;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
        createPrescriptions(); // Automatically save prescriptions when creating the record
    }

    // Save prescribed medications to their respective CSV
    private void createPrescriptions() {
        for (Prescription prescription : prescribedMedications) {
            try {
                System.out.println("Attempting to save prescription: " + prescription);
                System.out.println("Saving prescription: " + this);
                prescription.save();
            } catch (IOException e) {
                e.printStackTrace();  // Log errors to avoid silent failures
            }
        }
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

    public static List<AppointmentOutcomeRecord> getAllRecords() {
        List<AppointmentOutcomeRecord> records = new ArrayList<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\appointment_outcome_records.csv"))) {
            String line;
    
            // Skip the header row
            br.readLine();
    
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 5) {
                    System.err.println("Skipping malformed record: " + line);
                    continue;
                }
    
                String appointmentID = values[0];
                Date dateOfAppointment = new Date(Long.parseLong(values[1])); // Parse timestamp
                String serviceType = values[2];
                List<String> prescriptionIds = Arrays.asList(values[3].split(";"));
    
                List<Prescription> prescribedMedications = Prescription.getAll().stream()
                        .filter(p -> prescriptionIds.contains(p.getID()))
                        .collect(Collectors.toList());
    
                String consultationNotes = values[4];
                records.add(new AppointmentOutcomeRecord(appointmentID, dateOfAppointment, serviceType, prescribedMedications, consultationNotes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        }
    
        return records;
    }
    

    public void saveToCSV(String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
                String medicationsFormatted = prescribedMedications.stream()
                .map(prescription -> prescription.getMedicationName() + ":" + prescription.getQuantity())
                .collect(Collectors.joining(";"));
            printWriter.printf("%s,%d,%s,%s,%s%n",
                    appointmentID,
                    dateOfAppointment.getTime(), // Store date as a timestamp
                    serviceType,
                    medicationsFormatted,
                    consultationNotes
            );
        } catch (IOException e) {
            System.out.println("Error saving to CSV: " + e.getMessage());
        }
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
