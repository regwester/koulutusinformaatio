package fi.vm.sade.koulutusinformaatio.domain.dto;

public class HigherEducationChildLosReferenceDTO {

    private String id;
    private String educationDegree;
    private String name;
    private String status;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEducationDegree() {
        return educationDegree;
    }
    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
