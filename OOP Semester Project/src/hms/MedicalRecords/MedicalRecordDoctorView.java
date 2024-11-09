package hms.MedicalRecords;

import java.io.IOException;

public interface MedicalRecordDoctorView {
  String toString();

  // void newDiagnosis(String diagnosis);

  // void newPrescription(String prescription);

  // void newTreatmentPlan(String treatmentPlan);

  void saveToFile() throws IOException;
}
