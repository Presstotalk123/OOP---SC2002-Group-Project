package hms.Appointments;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hms.Doctor;


public class Appointment implements AppointmentPatientView, AppointmentDoctorView {
    private String id;
    private String doctorId;
    private LocalDateTime dateTime;

    // I used Optionals to denote that these items COULD potentially be empty.
    // This forces you to check if the values exists or not.
    private Optional<String> patientId;
    private Optional<String> outcomeRecordId; // points to a AppointmentOutcomeRecord id
    private Optional<AppointmentStatus> status; // e.g., confirmed, canceled, completed, this can be empty if doctor
                                                // hasn't accepted or rejected

    public Appointment(String id, Optional<String> patientId, String doctorId, Optional<AppointmentStatus> status,
            LocalDateTime dateTime, Optional<String> outcomeRecordId) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.outcomeRecordId = outcomeRecordId;
        this.status = status.or(() -> patientId.isPresent() ? Optional.of(AppointmentStatus.pending) : Optional.empty());
    }

    protected static List<Appointment> loadAllAppointments() throws IOException {
        List<Appointment> appointments = new ArrayList<Appointment>();

        BufferedReader file = new BufferedReader(new FileReader("../data/appointments.csv"));

        String nextLine = file.readLine();
        while ((nextLine = file.readLine()) != null) {
            String[] appt = nextLine.split(",");
            if (appt.length < 6) {
                continue;
            }

            String status = appt[4];
            Optional<AppointmentStatus> apptStatus;
            if (status.toLowerCase().equals("confirmed"))
                apptStatus = Optional.of(AppointmentStatus.confirmed);
            else if (status.toLowerCase().equals("cancelled"))
                apptStatus = Optional.of(AppointmentStatus.cancelled);
            else if (status.toLowerCase().equals("completed"))
                apptStatus = Optional.of(AppointmentStatus.completed);
            else
                apptStatus = Optional.empty();

            appointments.add(
                    new Appointment(
                            appt[0],
                            Optional.ofNullable(appt[1].equals("") ? null : appt[1]),
                            appt[2],
                            apptStatus,
                            LocalDateTime.parse(appt[5]),
                            Optional.ofNullable(appt[3].equals("") ? null : appt[3])));
        }

        file.close();
        // throw new IOException("Missing or Invalid Patient Data found in
        // patient_record.csv for patient with ID: " + id);

        return appointments;
    }

    public void schedule(String patientId) {
        this.patientId = Optional.of(patientId);
    }

    public void cancel() {
        this.patientId = Optional.empty();
        this.status = Optional.empty();
    }

    public void save() throws IOException {

        List<String> lines = Files.readAllLines(Paths.get("../data/appointments.csv"));
        FileOutputStream output = new FileOutputStream("../data/appointments.csv");

        boolean isEntryFound = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] appt = lines.get(i).split(",");

            if (appt.length == 6 && appt[0].equals(this.id)) {
                String newEntry = this.id + "," + (this.patientId.isEmpty() ? "" : this.patientId.get()) + ","
                        + this.doctorId + "," + (this.outcomeRecordId.isEmpty() ? "" : this.outcomeRecordId.get()) + ","
                        + (this.status.isEmpty() ? "" : this.status.get().toString().toLowerCase()) + ","
                        + this.dateTime + "\n";
                output.write(newEntry.getBytes());
                isEntryFound = true;
            } else {
                String line = lines.get(i) + "\n";
                output.write(line.getBytes());
            }
        }

        if (!isEntryFound) {
            String newEntry = this.id + "," + (this.patientId.isEmpty() ? "" : this.patientId.get()) + ","
                    + this.doctorId + "," + (this.outcomeRecordId.isEmpty() ? "" : this.outcomeRecordId.get()) + ","
                    + (this.status.isEmpty() ? "" : this.status.get().toString().toLowerCase()) + ","
                    + this.dateTime + "\n";
            output.write(newEntry.getBytes());
        }

        output.close();
    }

    public Optional<AppointmentStatus> getStatus() {
        return this.status;
    }

    public String getId() {
        return this.id;
    }

    public Optional<String> getPatientId() {
        return this.patientId;
    }

    public String getDateTime() {
        DateTimeFormatter patternDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter patternTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        return this.dateTime.format(patternDate) + " at " + this.dateTime.format(patternTime);
    }

    public String getDate() {
        DateTimeFormatter patternDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return this.dateTime.format(patternDate);
    }

    public String getTime() {
        DateTimeFormatter patternTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        return this.dateTime.format(patternTime);
    }

    public Doctor getDoctor() {
        try {
            return new Doctor(this.doctorId);
        } catch (IOException error) {
            return null;
        }
    }

    public boolean isBooked() {
        return this.patientId.isPresent();
    }

    public boolean isBookable() {
        if (this.status.isEmpty())
            return this.patientId.isEmpty();
        return this.patientId.isEmpty() && !this.status.get().equals(AppointmentStatus.cancelled);
    }

    // TODO: Doctor to Implement accept()
    public void accept() {

    }

    // TODO: Doctor to Implement decline()
    public void decline() {

    }

    // TODO: Doctor to Implement complete()
    public void complete(String outcome_record_id) {

    }

    @Override
    public String toString() {
        // return "Appointment{" +
        // "Patient ID='" + patientId + '\'' +
        // ", Doctor ID='" + doctorId + '\'' +
        // ", Status='" + status + '\'' +
        // ", DateTime=" + dateTime +
        // ", Outcome Record='" + outcomeRecord + '\'' +
        // '}';
        return "";
    }

    public void setStatus(Optional<AppointmentStatus> status) {
        this.status=status;
    }
}
