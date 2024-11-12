package hms.Appointments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface AppointmentDoctorView {
  void accept();
  void decline();
  void complete(String outcome_record_id);
  void save() throws IOException;

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
