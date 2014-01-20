package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("universityAppliedScienceLOS")
public class UniversityAppliedScienceLOSEntity {
	
	
	//Varmistetut
	@Id
    private String id;
    @Embedded
	private I18nTextEntity infoAboutTeachingLangs;
    @Embedded
	private I18nTextEntity content;
    @Embedded
	private I18nTextEntity goals;
    @Embedded
	private I18nTextEntity majorSelection;
    @Embedded
	private I18nTextEntity structure;
    @Embedded
	private I18nTextEntity finalExam;
    @Embedded
	private I18nTextEntity careerOpportunities;
    @Embedded
	private I18nTextEntity internationalization;
    @Embedded
	private I18nTextEntity cooperation;
    @Embedded
	private I18nTextEntity competence;
    @Embedded
	private I18nTextEntity researchFocus;
    @Embedded
	private I18nTextEntity accessToFurtherStudies;
    @Embedded
	private List<ContactPersonEntity> contactPersons = new ArrayList<ContactPersonEntity>();
    @Embedded
	private I18nTextEntity educationDomain;
    @Embedded
	private I18nTextEntity name;
    @Embedded
	private I18nTextEntity koulutuskoodi;
    @Embedded
	private I18nTextEntity educationDegree;
    @Embedded
    private I18nTextEntity degreeTitle;
	private Date startDate;
	private String plannedDuration;
	
	@Embedded
	private I18nTextEntity plannedDurationUnit;
	private String pduCodeUri;
	private String creditValue;
	@Embedded
	private I18nTextEntity degree;
	@Embedded
	private I18nTextEntity qualification;
	private Boolean chargeable;
	private String educationCode;
	@Embedded
	private List<CodeEntity> teachingLanguages;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public I18nTextEntity getInfoAboutTeachingLangs() {
		return infoAboutTeachingLangs;
	}
	public void setInfoAboutTeachingLangs(I18nTextEntity infoAboutTeachingLangs) {
		this.infoAboutTeachingLangs = infoAboutTeachingLangs;
	}
	public I18nTextEntity getContent() {
		return content;
	}
	public void setContent(I18nTextEntity content) {
		this.content = content;
	}
	public I18nTextEntity getGoals() {
		return goals;
	}
	public void setGoals(I18nTextEntity goals) {
		this.goals = goals;
	}
	public I18nTextEntity getMajorSelection() {
		return majorSelection;
	}
	public void setMajorSelection(I18nTextEntity majorSelection) {
		this.majorSelection = majorSelection;
	}
	public I18nTextEntity getStructure() {
		return structure;
	}
	public void setStructure(I18nTextEntity structure) {
		this.structure = structure;
	}
	public I18nTextEntity getFinalExam() {
		return finalExam;
	}
	public void setFinalExam(I18nTextEntity finalExam) {
		this.finalExam = finalExam;
	}
	public I18nTextEntity getCareerOpportunities() {
		return careerOpportunities;
	}
	public void setCareerOpportunities(I18nTextEntity careerOpportunities) {
		this.careerOpportunities = careerOpportunities;
	}
	public I18nTextEntity getInternationalization() {
		return internationalization;
	}
	public void setInternationalization(I18nTextEntity internationalization) {
		this.internationalization = internationalization;
	}
	public I18nTextEntity getCooperation() {
		return cooperation;
	}
	public void setCooperation(I18nTextEntity cooperation) {
		this.cooperation = cooperation;
	}
	public I18nTextEntity getCompetence() {
		return competence;
	}
	public void setCompetence(I18nTextEntity competence) {
		this.competence = competence;
	}
	public I18nTextEntity getResearchFocus() {
		return researchFocus;
	}
	public void setResearchFocus(I18nTextEntity researchFocus) {
		this.researchFocus = researchFocus;
	}
	public I18nTextEntity getAccessToFurtherStudies() {
		return accessToFurtherStudies;
	}
	public void setAccessToFurtherStudies(I18nTextEntity accessToFurtherStudies) {
		this.accessToFurtherStudies = accessToFurtherStudies;
	}
	public List<ContactPersonEntity> getContactPersons() {
		return contactPersons;
	}
	public void setContactPersons(List<ContactPersonEntity> contactPersons) {
		this.contactPersons = contactPersons;
	}
	public I18nTextEntity getEducationDomain() {
		return educationDomain;
	}
	public void setEducationDomain(I18nTextEntity educationDomain) {
		this.educationDomain = educationDomain;
	}
	public I18nTextEntity getName() {
		return name;
	}
	public void setName(I18nTextEntity name) {
		this.name = name;
	}
	public I18nTextEntity getKoulutuskoodi() {
		return koulutuskoodi;
	}
	public void setKoulutuskoodi(I18nTextEntity koulutuskoodi) {
		this.koulutuskoodi = koulutuskoodi;
	}
	public I18nTextEntity getEducationDegree() {
		return educationDegree;
	}
	public void setEducationDegree(I18nTextEntity educationDegree) {
		this.educationDegree = educationDegree;
	}
	public I18nTextEntity getDegreeTitle() {
		return degreeTitle;
	}
	public void setDegreeTitle(I18nTextEntity degreeTitle) {
		this.degreeTitle = degreeTitle;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getPlannedDuration() {
		return plannedDuration;
	}
	public void setPlannedDuration(String plannedDuration) {
		this.plannedDuration = plannedDuration;
	}
	public I18nTextEntity getPlannedDurationUnit() {
		return plannedDurationUnit;
	}
	public void setPlannedDurationUnit(I18nTextEntity plannedDurationUnit) {
		this.plannedDurationUnit = plannedDurationUnit;
	}
	public String getPduCodeUri() {
		return pduCodeUri;
	}
	public void setPduCodeUri(String pduCodeUri) {
		this.pduCodeUri = pduCodeUri;
	}
	public String getCreditValue() {
		return creditValue;
	}
	public void setCreditValue(String creditValue) {
		this.creditValue = creditValue;
	}
	public I18nTextEntity getDegree() {
		return degree;
	}
	public void setDegree(I18nTextEntity degree) {
		this.degree = degree;
	}
	public I18nTextEntity getQualification() {
		return qualification;
	}
	public void setQualification(I18nTextEntity qualification) {
		this.qualification = qualification;
	}
	public Boolean getChargeable() {
		return chargeable;
	}
	public void setChargeable(Boolean chargeable) {
		this.chargeable = chargeable;
	}
	public String getEducationCode() {
		return educationCode;
	}
	public void setEducationCode(String educationCode) {
		this.educationCode = educationCode;
	}
	public List<CodeEntity> getTeachingLanguages() {
		return teachingLanguages;
	}
	public void setTeachingLanguages(List<CodeEntity> teachingLanguages) {
		this.teachingLanguages = teachingLanguages;
	}
	

}
