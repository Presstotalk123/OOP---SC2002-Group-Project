package hms;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Random;

import hms.Appointments.Appointment;

public class Doctor extends Staff {

    public Doctor(Scanner scanner) {
        super(scanner, "doctor");
        try {
            super.save();
        } catch (IOException error) {
            System.out.println("Unable to save user " + name + " due to IOException: " + error.getMessage());
        }
    }

    public Doctor(String id, String name, String password) throws IOException {
        super(id, name, password, "doctor");
    }

    // Event loop for Doctor's menu
    public boolean eventLoop(Scanner scanner) {
        System.out.print("""
                Doctor Menu:
                1. View Patient Medical Records
                2. Update Patient Medical Records
                3. View Personal Schedule
                4. Set Availability for Appointments
                5. Accept or Decline Appointment Requests
                6. View Upcoming Appointments
                7. Record Appointment Outcomes
                8. Log Out
                Enter your choice:""");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 3:
                viewPersonalSchedule();
                break;
            case 4:
                setAvailabilityForAppointments();
                break;
            case 6:
                viewUpcomingAppointments();
                break;
            case 7:
                recordAppointmentOutcomes();
                break;
            case 8:
                return false; // Log out
            default:
                System.out.println("Invalid choice. Please enter a number from 1 to 8.");
                break;
        }
        return true;
    }

    // Placeholder method for "View Personal Schedule"
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

    // TODO: Add proper Doctor formatting
    public String toString() {
        return this.id + " - Doctor - " + this.name;
    }
}
