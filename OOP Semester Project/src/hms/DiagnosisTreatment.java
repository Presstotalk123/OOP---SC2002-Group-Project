package hms;

import java.util.Date;

public class DiagnosisTreatment {
    private String diagnosisID;
    private String treatmentID;
    private Date dateDiagnosed;
    private Date startDate;
    private Date endDate;
    private String description;

    public DiagnosisTreatment(String diagnosisID, String treatmentID, Date dateDiagnosed, Date startDate, Date endDate, String description) {
        this.diagnosisID = diagnosisID;
        this.treatmentID = treatmentID;
        this.dateDiagnosed = dateDiagnosed;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public String getDiagnosisID() {
        return diagnosisID;
    }

    public void setDiagnosisID(String diagnosisID) {
        this.diagnosisID = diagnosisID;
    }

    public String getTreatmentID() {
        return treatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        this.treatmentID = treatmentID;
    }

    public Date getDateDiagnosed() {
        return dateDiagnosed;
    }

    public void setDateDiagnosed(Date dateDiagnosed) {
        this.dateDiagnosed = dateDiagnosed;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DiagnosisTreatment{" +
                "diagnosisID='" + diagnosisID + '\'' +
                ", treatmentID='" + treatmentID + '\'' +
                ", dateDiagnosed=" + dateDiagnosed +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                '}';
    }
}
