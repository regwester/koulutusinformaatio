/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.dao.entity;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
@Entity("childLearningOpportunityInstances")
public class ChildLearningOpportunityInstanceEntity {

    @Id
    private String id;
    private String applicationSystemId;
    @Reference
    private ApplicationOptionEntity applicationOption;
    @Embedded
    private List<ChildLORefEntity> related;
    @Embedded
    private List<CodeEntity> teachingLanguages;
    private Date startDate;
    @Embedded
    private List<I18nTextEntity> formOfEducation;
    @Embedded
    private Map<String, String> webLinks;
    @Embedded
    private List<I18nTextEntity> formOfTeaching;
    @Embedded
    private I18nTextEntity prerequisite;
    @Embedded
    private List<I18nTextEntity> professionalTitles;

    public ChildLearningOpportunityInstanceEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public ApplicationOptionEntity getApplicationOption() {
        return applicationOption;
    }

    public void setApplicationOption(ApplicationOptionEntity applicationOption) {
        this.applicationOption = applicationOption;
    }

    public List<ChildLORefEntity> getRelated() {
        return related;
    }

    public void setRelated(List<ChildLORefEntity> related) {
        this.related = related;
    }

    public List<CodeEntity> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<CodeEntity> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<I18nTextEntity> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<I18nTextEntity> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Map<String, String> getWebLinks() {
        return webLinks;
    }

    public void setWebLinks(Map<String, String> webLinks) {
        this.webLinks = webLinks;
    }

    public List<I18nTextEntity> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nTextEntity> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public I18nTextEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(I18nTextEntity prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<I18nTextEntity> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<I18nTextEntity> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }
}
