fpackage hms;


import hms.Appointments.Appointment;
import hms.Appointments.AppointmentStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class Doctor extends Staff {


    // I removed this as I'm not sure what your intention for specialisation is.
    // It doesn't add any functionality so idk what you're planning to do with it.


    public Doctor(Scanner scanner) throws IOException {
        super(scanner, "doctor");
        try {
            super.save();
        } catch (IOException error) {
            System.out.println("Unable to save user " + name + " due to IOException: " + error.getMessage());
        }
    }
    // Doctor.java
    @Override
    public void save() throws IOException {
        // staff.csv: id,gender,age,role,phoneNumber,email,specialization
        List<String> lines = Files.readAllLines(Paths.get("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/staff.csv"));
        FileOutputStream output = new FileOutputStream("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/staff.csv");

        boolean isEntryFound = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] staff = lines.get(i).split(",");

            if (staff.length == 7 && staff[0].equals(this.id)) {
                String newEntry = this.id + "," + this.gender.toString().toLowerCase() + "," + this.age + ","
                        + this.role + "," + this.phoneNumber + "," + this.emailAddress + "," + this.specialization + "\n";
                output.write(newEntry.getBytes());
                isEntryFound = true;
            } else {
                String line = lines.get(i) + "\n";
                output.write(line.getBytes());
            }
        }

        if (!isEntryFound) {
            String newEntry = this.id + "," + this.gender.toString().toLowerCase() + "," + this.age + ","
                    + this.role + "," + this.phoneNumber + "," + this.emailAddress + "," + this.specialization + "\n";
            output.write(newEntry.getBytes());
        }

        output.close();
    }
    public Doctor(String id, String name, String password) throws IOException {
        super(id, name, password, "doctor");
    }

    // TODO: Add EventLoop for all Doctor Menu items
    public boolean eventLoop(Scanner scanner) {
        System.out.print("""
                Doctor Menu:
                1. View Patient Medical Records
                2. Update Patient Medical Records
                3. View Personal Schedule
                4. Set Availability for Appointments
                5. Accept or Decline Appointment Requests
                6. Record Appointment Outcome\s
                7. Log Out
                Enter your choice:""");

        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                System.out.print("Enter the patient ID: ");
                String patientId = scanner.nextLine();
                try {
                    List<String[]> medicalRecords = viewMedicalRecord(patientId);
                    for (String[] record : medicalRecords) {
                        System.out.println(String.join(", ", record));
                    }
                } catch (IOException e) {
                    System.out.println("Error reading medical records: " + e.getMessage());
                }
                break;
            case 2:
                System.out.print("Enter the patient ID: ");
                String patientId2 = scanner.nextLine();
                try {
                    List<String[]> medicalRecords = viewMedicalRecord(patientId2);
                    for (String[] record : medicalRecords) {
                        System.out.println(String.join(", ", record));
                    }
                    System.out.print("Enter the index of the record to update: ");
                    int index = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter the updated record: ");
                    String updatedRecord = scanner.nextLine();
                    medicalRecords.get(index)[1] = updatedRecord;
                    updateMedicalRecord(patientId2, medicalRecords.get(index));
                } catch (IOException e) {
                    System.out.println("Error reading medical records: " + e.getMessage());
                }
                break;
            case 3:
                List<Appointment> appointments = viewAppointments();
                for (Appointment appointment : appointments) {
                    System.out.println(appointment);
                }
                break;
            case 4:
                setAvailabilityForAppointments();
                break;
            case 5:
                System.out.print("Enter the appointment ID: ");
                String appointmentID = scanner.nextLine();
                System.out.print("Accept (A) or Decline (D) the appointment: ");
                String action = scanner.nextLine();
                if (action.equalsIgnoreCase("A")) {
                    accept(appointmentID);
                } else if (action.equalsIgnoreCase("D")) {
                    decline(appointmentID);
                } else {
                    System.out.println("Invalid action. Please enter 'A' to accept or 'D' to decline.");
                }
                break;
            case 6:
                System.out.print("Enter the appointment ID: ");
                String appointmentID2 = scanner.nextLine();
                System.out.print("Enter the date of the appointment: ");
                String dateOfAppointment = scanner.nextLine();
                System.out.print("Enter the service type: ");
                String serviceType = scanner.nextLine();
                System.out.print("Enter the prescribed medications (separated by commas): ");
                String[] prescribedMedications = scanner.nextLine().split(",");

                
                //code to retreive the allergies in medicalRecord with the patientid associated - do not know how
                ArrayList<String> allergies = getAllergies(appointmentID);// this have to see how to retreive
                boolean proceedWithPrescriptions = false;
                while (!proceedWithPrescriptions) {
                
                List<String> prescribedList = Arrays.asList(prescribedMedications);
                List<String> commonAllergens = new ArrayList<>(allergies);
                commonAllergens.retainAll(prescribedList);

                if (!commonAllergens.isEmpty()) {
                // Step 4: Display warning and ask for doctor confirmation
                System.out.println("Warning! Patient is allergic to: " + String.join(", ", commonAllergens) + ". Override? Y / N");
                String response = scanner.nextLine().trim().toUpperCase();

                if (response.equals("Y")) {
                    proceedWithPrescriptions = true; // Continue with the current prescription list
                } else {
                    Prompt to re-enter prescriptions
                    System.out.println("Re-enter all prescribed medications. Make sure medications are compatible with each other.");
                    System.out.print("Enter the prescribed medications (separated by commas): ");
                    prescribedMedications = scanner.nextLine().split(",");
                }
            } else {
                // No allergy conflict
                System.out.println("No allergy conflicts with prescribed medications.");
                proceedWithPrescriptions = true;
            }
        }
                
                List<Prescription> prescriptions = new ArrayList<>();
                for (String medication : prescribedMedications) {
                    int prescriptionID = new Random().nextInt(9000) + 1000;
                    String prescriptionId = Integer.toString(prescriptionID);
                    prescriptions.add(new Prescription(prescriptionId,medication,PrescriptionStatus.Pending));
                }
                System.out.print("Enter the consultation notes: ");
                String consultationNotes = scanner.nextLine();
                recordAppointmentOutcome(appointmentID2, new Date(Long.parseLong(dateOfAppointment)), serviceType, prescriptions, consultationNotes);
                break;
            case 7:
                return false;
            default:
                System.out.println("Invalid choice. Please enter a number from 1 to 7.");
                break;
        }
        return true;
    }

    // TODO: Add proper Doctor formatting
    public String toString() {
        // System.out.printf("%s - %s - %s, %s, %s", this.id, this.role, this.name, this.age, this.gender);
        return this.id + " - Doctor - " + this.name;
    }


    public List<Appointment> viewAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String line;
        String separator = ",";

        try (BufferedReader br = new BufferedReader(new FileReader("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/appointments.csv"))) {
            // Skip header row
            br.readLine();

            // Process each line in the CSV file
            while ((line = br.readLine()) != null) {
                String[] values = line.split(separator);
                if (values[0].equals(this.id)) {
                    // Ensure values array has the correct length before accessing specific indices
                    if (values.length >= 6) {
                        String status = values[4];
                        Optional<AppointmentStatus> apptStatus;
                        if (status.toLowerCase().equals("confirmed"))
                            apptStatus = Optional.of(AppointmentStatus.Confirmed);
                        else if (status.toLowerCase().equals("cancelled"))
                            apptStatus = Optional.of(AppointmentStatus.Cancelled);
                        else if (status.toLowerCase().equals("completed"))
                            apptStatus = Optional.of(AppointmentStatus.Completed);
                        else
                            apptStatus = Optional.empty();

                        appointments.add(
                                new Appointment(
                                        values[0],
                                        Optional.ofNullable(values[1].equals("") ? null : values[1]),
                                        values[2],
                                        apptStatus,
                                        LocalDateTime.parse(values[5]),
                                        Optional.ofNullable(values[3].equals("") ? null : values[3])));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public void accept(String appointmentID) {
        List<Appointment> appointments = viewAppointments();
        for (Appointment appointment : appointments) {
            if (appointment.getId().equals(appointmentID)) {
                appointment.setStatus(Optional.of(AppointmentStatus.Confirmed));
                try {
                    appointment.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public void decline(String appointmentID) {
        List<Appointment> appointments = viewAppointments();
        for (Appointment appointment : appointments) {
            if (appointment.getId().equals(appointmentID)) {
                appointment.setStatus(Optional.of(AppointmentStatus.Cancelled));
                try {
                    appointment.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public void complete(String appointmentID) {
        List<Appointment> appointments = viewAppointments();
        for (Appointment appointment : appointments) {
            if (appointment.getId().equals(appointmentID)) {
                appointment.setStatus(Optional.of(AppointmentStatus.Completed));
                try {
                    appointment.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public void recordAppointmentOutcome(String appointmentID, Date dateOfAppointment, String serviceType, List<Prescription> prescribedMedications, String consultationNotes) {
        AppointmentOutcomeRecord outcomeRecord = new AppointmentOutcomeRecord(appointmentID, dateOfAppointment, serviceType, prescribedMedications, consultationNotes);

        // Save the outcome record to the CSV file
        try (PrintWriter pw = new PrintWriter(new FileWriter("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/appointment_outcome_records.csv", true))) {
            StringBuilder sb = new StringBuilder();
            sb.append(outcomeRecord.getAppointmentID()).append(",");
            sb.append(outcomeRecord.getDateOfAppointment().getTime()).append(",");
            sb.append(outcomeRecord.getServiceType()).append(",");
            sb.append(outcomeRecord.getPrescribedMedications().stream().map(Prescription::getID).collect(Collectors.joining(";"))).append(",");
            sb.append(outcomeRecord.getConsultationNotes());
            pw.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String[]> viewMedicalRecord(String patientId) throws IOException {
        List<String[]> medicalRecords = new ArrayList<>();
        String line;
        String separator = ",";

        try (BufferedReader br = new BufferedReader(new FileReader("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/Patient.csv"))) {
            // Skip header row
            br.readLine();

            // Process each line in the CSV file
            while ((line = br.readLine()) != null) {
                String[] record = line.split(separator);
                if (record.length > 0 && record[0].equals(patientId)) {
                    medicalRecords.add(record);
                }
            }
        }

        return medicalRecords;
    }
    public void updateMedicalRecord(String patientId, String[] updatedRecord) throws IOException {
        List<String[]> medicalRecords = new ArrayList<>();
        String line;
        String separator = ",";

        try (BufferedReader br = new BufferedReader(new FileReader("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/Patient.csv"))) {
            // Read all lines and store them in a list
            while ((line = br.readLine()) != null) {
                String[] record = line.split(separator);
                if (record.length > 0 && record[0].equals(patientId)) {
                    medicalRecords.add(updatedRecord); // Add updated record
                } else {
                    medicalRecords.add(record); // Add existing record
                }
            }
        }

        // Write the updated records back to the CSV file
        try (PrintWriter pw = new PrintWriter(new FileWriter("/Users/sam/programming/OOP---SC2002-Group-Project/OOP Semester Project/data/Patient.csv"))) {
            for (String[] record : medicalRecords) {
                pw.println(String.join(separator, record));
            }
        }
    }
    private void viewPersonalSchedule() {
        System.out.println("Viewing personal schedule... (placeholder)");
    }

    // Placeholder method for "Set Availability for Appointments"
    private void setAvailabilityForAppointments() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter start date and time (e.g., 2024-12-01 09:00): ");
            String startInput = scanner.nextLine();
            LocalDateTime startDateTime = LocalDateTime.parse(startInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            System.out.print("Enter end time (e.g., 2024-12-01 17:00): ");
            String endInput = scanner.nextLine();
            LocalDateTime endDateTime = LocalDateTime.parse(endInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            System.out.print("Enter slot duration in minutes (e.g., 30 for 30-minute slots): ");
            int slotDuration = scanner.nextInt();
            scanner.nextLine(); // consume newline

            List<Appointment> newSlots = generateTimeSlots(startDateTime, endDateTime, slotDuration);
            saveTimeSlots(newSlots);

            System.out.println("Availability has been set successfully!");
        } catch (Exception e) {
            System.out.println("Error parsing date/time. Please try again.");
        }
    }

    //Making helper function to generate time slots
    private List<Appointment> generateTimeSlots(LocalDateTime start, LocalDateTime end, int intervalMinutes) {
        List<Appointment> slots = new ArrayList<>();
        LocalDateTime slotTime = start;
        Random random=new Random();
        while (slotTime.isBefore(end)) {
            int AppointmentID=random.nextInt(9000)+1000;
            String appointmentID = Integer.toString(AppointmentID);
            Appointment slot = new Appointment(
                    appointmentID,          // appointmentId
                    Optional.empty(),       // patientId (no patient yet)
                    this.id,                // doctor's ID
                    Optional.empty(),       // status
                    slotTime,
                    Optional.empty()        // outcomeRecordId
            );
            slots.add(slot);
            slotTime = slotTime.plusMinutes(intervalMinutes);
        }
        return slots;
    }
    //Making helper function to save time slots
    private void saveTimeSlots(List<Appointment> slots) {
        for (Appointment slot : slots) {
            try {
                slot.save();
            } catch (IOException e) {
                System.out.println("Error saving appointment slot: " + e.getMessage());
            }
        }
    }



    // Placeholder method for "View Upcoming Appointments"
    private void viewUpcomingAppointments() {
        System.out.println("Viewing upcoming appointments... (placeholder)");
    }

    // Placeholder method for "Record Appointment Outcomes"
    private void recordAppointmentOutcomes() {
        System.out.println("Recording appointment outcomes... (placeholder)");
    }

}

//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         return null; // Return null if username not found
//     }

//     public String getPhoneNumber(String Username) {
//         String line;
//         String separator = ",";

//         try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//             br.readLine(); // Skip header row

//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(separator);

//                 if (values.length >= 9 && values[8].equals(Username)) { // Username at index 8
//                     return values[5]; // phoneNumber at index 5
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         return null; // Return null if username not found
//     }

//     public String getEmailAddress(String Username) {
//         String line;
//         String separator = ",";

//         try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//             br.readLine(); // Skip header row

//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(separator);

//                 if (values.length >= 9 && values[8].equals(Username)) { // Username at index 8
//                     return values[6]; // emailAddress at index 6
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         return null; // Return null if username not found
//     }
// }
