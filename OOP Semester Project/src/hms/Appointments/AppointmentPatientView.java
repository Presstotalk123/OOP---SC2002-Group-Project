package hms.Appointments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface AppointmentPatientView {

  String getId();

  String getDateTime();

  Optional<String> getPatientId();

  Optional<AppointmentStatus> getStatus();

  void schedule(String patient_id);

  void cancel();

  void save() throws IOException;

  boolean isBooked();

  boolean isBookable();

  // Casts all appointsments into a PatientView
  static List<AppointmentPatientView> loadAllAppointments() throws IOException {
        List<AppointmentPatientView> appts = new ArrayList<>();
        for (Appointment appt : Appointment.loadAllAppointments()) {
            appts.add(appt);
        }
        return appts;
    }

    // New method to load only bookable appointments (those without a patient ID and not canceled)
    static List<AppointmentPatientView> loadAvailableAppointments() throws IOException {
        return loadAllAppointments().stream()
            .filter(AppointmentPatientView::isBookable)
            .collect(Collectors.toList());
    }
  // A reschedule is just a cancel then schedule.
}
