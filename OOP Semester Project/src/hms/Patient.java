package hms;
import hms.Appointments.Appointment;
import hms.Appointments.AppointmentPatientView;
import hms.Appointments.AppointmentStatus;
import hms.MedicalRecords.MedicalRecordPatientView;
import hms.MedicalRecords.MedicalRecords;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Patient extends User {
    // Store MedicalRecord as a PatientView so only Patient methods are exposed.
    private MedicalRecordPatientView patientRecord;

    // Prompt for information about patient
    public Patient(Scanner scanner) throws IOException {
        super(scanner, "patient"); // Creates base User
        
        try {
            super.save(); // Save to users.csv
        } catch (IOException error) {
            System.out.println("Unable to save user " + name + " due to IOException: " + error.getMessage());
        }
        
        this.patientRecord = new MedicalRecords(scanner, this.id, this.name); // Create Patient Medical Record
        
        // Save the new MedicalRecord to Patient.csv immediately
        try {
            this.patientRecord.saveToFile();
            System.out.println("Medical Record for Patient saved successfully.");
        } catch (IOException error) {
            System.out.println("Error saving Medical Record: " + error.getMessage());
        }
    }
    
    public Patient(String id, String name, String password) throws IOException {
        super(id, name, password, "patient");
    
        this.patientRecord = MedicalRecords.getRecord(this, id);
        
        if (this.patientRecord == null) {
            this.patientRecord = new MedicalRecords(new Scanner(System.in), id, name); 
            this.patientRecord.saveToFile(); // Save new MedicalRecords if not found
        }
    }
    
    
    public boolean eventLoop(Scanner scanner) {
        System.out.print("""
                Patient Menu:
                1. View Medical Records
                2. Update Email or Phone Number
                3. Schedule New Appointment
                4. Reschedule/Cancel Existing Appointment
                5. View all your Appointment Statuses
                6. View Appointment Outcome Records
                7. Give Feedback
                8. Log Out
                Enter your choice:""");
        int choice = scanner.nextInt();
        scanner.nextLine();
        System.out.println("");
        switch (choice) {
            case 1:
                System.out.println(this.patientRecord);
                break;
            case 2:
                System.out.print("Enter a new email address (leave blank to keep existing value): ");
                String newEmail = scanner.nextLine();
                System.out.print("Enter a new phone number (leave blank to keep existing value): ");
                String newPhoneNumber = scanner.nextLine();
                this.patientRecord.updateEmailAddress(newEmail);
                this.patientRecord.updatePhoneNumber(newPhoneNumber);
                try {
                    this.patientRecord.saveToFile();
                    System.out.println("All changes successful!");
                } catch (IOException error) {
                    System.out.println("Error occurred when saving data: ");
                    error.printStackTrace();
                }
                System.out.println("");
                break;
            case 3:
                this.scheduleAppointment(scanner);
                break;
            case 4:
                this.rescheduleOrCancelAppointment(scanner);
                break;
            case 5:
                this.viewAppointmentStatuses();
                break;
            case 6:
                this.viewAppointmentOutcomeRecords(scanner);
                break;
            case 7:
                this.giveFeedback(scanner);
                break;
            case 8:
                return false;
            default:
                System.out.println("Invalid choice. Please enter a number from 1 to 7.");
                break;
        }
        return true;
    }
    private void rescheduleOrCancelAppointment(Scanner scanner) {
        try {
            System.out.println("To reschedule an appointment, you need to cancel an existing appointment.");
            System.out.println("After cancelling, you'll be asked if you want to schedule a new appointment.");
            System.out.println(
                    "Please choose an appointment to cancel:\n");
            List<AppointmentPatientView> appts = AppointmentPatientView.loadAllAppointments();
            Iterator<AppointmentPatientView> it = appts.iterator();
            boolean foundAnyAppts = false;
            while (it.hasNext()) {
                AppointmentPatientView appt = it.next();
                if (appt.getPatientId().isPresent() && appt.getPatientId().get().equals(this.id)) {
                    System.out.println("(" + appt.getId() + ") - " + appt.getDateTime().toString());
                    foundAnyAppts = true;
                }
            }
            if (!foundAnyAppts) {
                System.out.println("You have no booked appointments!\n");
                return;
            }
            System.out.println("");
            System.out.print("Enter the ID of the appointment you want to cancel: ");
            String selectedAppointmentId = scanner.nextLine();
            boolean wasCancellationSuccessful = false;
            it = appts.iterator();
            while (it.hasNext()) {
                AppointmentPatientView appt = it.next();
                if (appt.getId().equals(selectedAppointmentId) && appt.getPatientId().isPresent()
                        && appt.getPatientId().get().equals(this.id)) {
                    appt.cancel();
                    appt.save();
                    wasCancellationSuccessful = true;
                    break;
                }
            }
            if (!wasCancellationSuccessful) {
                System.out.println("Invalid Appointment ID! Returning to main menu...");
            } else {
                System.out.println("Cancellation was successful!\n");
            }
            while (true) {
                System.out.print("""
                        Choose an option to continue:
                        1. Schedule a new appointment
                        2. Back to Patient Menu
                        Enter your choice: """);
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 1) {
                    this.scheduleAppointment(scanner);
                    return;
                } else if (choice == 2) {
                    return;
                } else {
                    System.out.println("Invalid option! Please enter a value between 1-2.");
                }
            }
        } catch (IOException error) {
            System.out.println("Error occurred cancelling new appointment: ");
            error.printStackTrace();
        }
    }
    private void scheduleAppointment(Scanner scanner) {
    try {
        List<AppointmentPatientView> appts = AppointmentPatientView.loadAllAppointments();
        System.out.println("Here are all the available appointments:");
        Iterator<AppointmentPatientView> it = appts.iterator();
        boolean foundAnyAppts = false;
        
        while (it.hasNext()) {
            AppointmentPatientView appt = it.next();
            if (appt.isBookable()) {
                System.out.println("(" + appt.getId() + ") - " + appt.getDateTime().toString());
                foundAnyAppts = true;
            }
        }
        
        if (!foundAnyAppts) {
            System.out.println("No more available appointments!\n");
            return;
        }
        
        System.out.print("Enter the ID of the appointment you want to book: ");
        String selectedAppointmentId = scanner.nextLine();
        boolean wasBookingSuccessful = false;
        
        it = appts.iterator();
        while (it.hasNext()) {
            AppointmentPatientView appt = it.next();
            if (appt.getId().equals(selectedAppointmentId)) {
                // Set patient ID and status to Pending
                appt.schedule(this.id);  // Assign patient ID
                if (appt instanceof Appointment) {  // Ensure it's a modifiable instance
                    ((Appointment) appt).setStatus(Optional.of(AppointmentStatus.pending));
                }
                
                appt.save();  // Save the updated appointment
                wasBookingSuccessful = true;
                break;
            }
        }
        
        if (!wasBookingSuccessful) {
            System.out.println("Invalid Appointment ID! Returning to main menu...");
        } else {
            System.out.println("Booking was successful! Appointment status is now PENDING.");
        }
        
    } catch (IOException error) {
        System.out.println("Error occurred scheduling new appointment: ");
        error.printStackTrace();
    }
}

private void viewAppointmentOutcomeRecords(Scanner scanner) {
    System.out.print("Enter the appointment ID to search: ");
    String searchAppointmentId = scanner.nextLine();
    String filePath = "C:\\Users\\welcome\\Desktop\\sam2\\OOP---SC2002-Group-Project-sam2\\OOP Semester Project\\data\\appointment_outcome_records.csv";

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;
        int recordFound = 0;

        while ((line = br.readLine()) != null) {
            String[] record = line.split(",");

            if (record.length < 5) {
                System.err.println("Skipping malformed record: " + line);
                continue;  // Skip malformed records
            }

            if (record[0].equals(searchAppointmentId)) {
                System.out.println("Appointment ID: " + record[0]);
                System.out.println("Date of Appointment: " + new Date(Long.parseLong(record[1])));
                System.out.println("Service Type: " + record[2]);
                System.out.println("Prescribed Medications: " + record[3].replace(";", ", "));
                System.out.println("Consultation Notes: " + record[4]);
                System.out.println();  // Blank line between records
                recordFound +=1;
            }
        }

        if (recordFound==0) {
            System.out.println("No record found for Appointment ID: " + searchAppointmentId);
        }

    } catch (IOException e) {
        System.out.println("Error reading appointment outcome records: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("Error parsing date in record. Record might be malformed.");
}
}
private void giveFeedback(Scanner scanner) {
    System.out.println("What was your experience with our hospital management system? How can we improve?");
    String comments = scanner.nextLine().trim();

    // Validate non-empty feedback.
    while (comments.isEmpty()) {
        System.out.println("Feedback cannot be empty. Please provide your feedback:");
        comments = scanner.nextLine().trim();
    }

    int rating = 0;
    while (true) {
        System.out.print("Please rate your experience from 1 to 5: ");
        try {
            rating = Integer.parseInt(scanner.nextLine().trim());
            if (rating < 1 || rating > 5) {
                throw new NumberFormatException();
            }
            break;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number between 1 and 5.");
        }
    }

    // Create a Feedback object and save it.
    Feedback feedback = new Feedback(this.id, comments, rating);
    try {
        feedback.save();
        System.out.println("Thank you for your feedback!");
    } catch (IOException e) {
        System.out.println("An error occurred while saving your feedback: " + e.getMessage());
    }
}


    private void viewAppointmentStatuses() {
        try {
            System.out.println("Here are all your booked appointments and their statuses:\n");
            List<AppointmentPatientView> appts = AppointmentPatientView.loadAllAppointments();
            Iterator<AppointmentPatientView> it = appts.iterator();
            boolean foundAnyAppts = false;
            while (it.hasNext()) {
                AppointmentPatientView appt = it.next();
                if (appt.getPatientId().isPresent() && appt.getPatientId().get().equals(this.id)) {
                    Optional<AppointmentStatus> status = appt.getStatus();
                    System.out.println(
                            "(" + appt.getId() + ") - " + appt.getDateTime().toString() + " - " + (status.isEmpty()
                                    ? "PENDING"
                                    : status.get().toString().toUpperCase()));
                    foundAnyAppts = true;
                }
            }
            if (!foundAnyAppts) {
                System.out.println("You have no booked appointments!\n");
                return;
            }
            System.out.println("");
            
        } catch (IOException error) {
            System.out.println("Error occurred retrieving your appointment: ");
            error.printStackTrace();
        }
    }
}
