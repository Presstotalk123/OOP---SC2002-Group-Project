package hms.Appointments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import hms.Doctor;

public interface AppointmentPatientView {

  String getId();

  String getDateString();

  String getTimeString();

  Doctor getDoctor();

  Optional<String> getPatientId();

  Optional<AppointmentStatus> getStatus();

  void schedule(String patient_id);

  void cancel();

  void save() throws IOException;

  boolean isBooked();

  boolean isBookable();

  // Casts all appointsments into a PatientView
  static List<AppointmentPatientView> loadAllAppointments() throws IOException {
    List<AppointmentPatientView> appts = new ArrayList<AppointmentPatientView>();
    Iterator<Appointment> it = Appointment.loadAllAppointments().iterator();
    while (it.hasNext()) {
      AppointmentPatientView appt = it.next();
      appts.add(appt);
    }
    return appts;
  }
  // A reschedule is just a cancel then schedule.
}
