package hms.Appointments;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface AppointmentDoctorView {
  void accept();
  void decline();
  void complete(String outcome_record_id);
  void save() throws IOException;

  void setStatus(Optional<AppointmentStatus> status);

  LocalDateTime getDate();

  String getDateTime();

  Optional<AppointmentStatus> getStatus();

  Optional<String> getPatientId();

  String getId();

  // Casts all appointsments into a DoctorView
  static List<AppointmentDoctorView> loadAllAppointments() throws IOException {
    List<AppointmentDoctorView> appts = new ArrayList<AppointmentDoctorView>();
    Iterator<Appointment> it = Appointment.loadAllAppointments().iterator();
    while (it.hasNext()) {
      AppointmentDoctorView appt = it.next();
      appts.add(appt);
    }
    return appts;
  }
}
