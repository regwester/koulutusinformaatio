/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Child level learning opportunity.
 *
 * @author Hannu Lyytikainen
 */
public class ChildLearningOpportunity {

    private String id;
    private I18nText name;
    private List<ChildLOI> childLOIs = new ArrayList<ChildLOI>();
    // koulutusohjelma
    private I18nText degreeTitle;
    //tutkintonimike
    private I18nText qualification;
    //tavoite
    private I18nText degreeGoal;
    private List<ApplicationOption> applicationOptions;
    private List<String> applicationSystemIds;
    private Date startDate;
    // koulutuslaji -> nuorten koulutus
    private List<I18nText> formOfEducation;
    private Map<String, String> webLinks;
    private List<Code> teachingLanguages;
    // opetusmuoto -> lähiopetus
    private List<I18nText> formOfTeaching;
    private I18nText prerequisite;
    private List<I18nText> professionalTitles;
    private I18nText workingLifePlacement;
    private I18nText internationalization;
    private I18nText cooperation;


    public ChildLearningOpportunity(String id, I18nText name) {
        this.id = id;
        this.name = name;
    }

    public ChildLearningOpportunity() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public List<ChildLOI> getChildLOIs() {
        return childLOIs;
    }

    public void setChildLOIs(List<ChildLOI> childLOIs) {
        this.childLOIs = childLOIs;
    }

    public I18nText getQualification() {
        return qualification;
    }

    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }

    public I18nText getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(I18nText degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public I18nText getDegreeGoal() {
        return degreeGoal;
    }

    public void setDegreeGoal(I18nText degreeGoal) {
        this.degreeGoal = degreeGoal;
    }

    public List<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public List<String> getApplicationSystemIds() {
        return applicationSystemIds;
    }

    public void setApplicationSystemIds(List<String> applicationSystemIds) {
        this.applicationSystemIds = applicationSystemIds;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<I18nText> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<I18nText> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Map<String, String> getWebLinks() {
        return webLinks;
    }

    public void setWebLinks(Map<String, String> webLinks) {
        this.webLinks = webLinks;
    }

    public List<Code> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<Code> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public List<I18nText> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nText> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public I18nText getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(I18nText prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<I18nText> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<I18nText> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public I18nText getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(I18nText workingLifePlacement) {
        this.workingLifePlacement = workingLifePlacement;
    }

    public I18nText getInternationalization() {
        return internationalization;
    }

    public void setInternationalization(I18nText internationalization) {
        this.internationalization = internationalization;
    }

    public I18nText getCooperation() {
        return cooperation;
    }

    public void setCooperation(I18nText cooperation) {
        this.cooperation = cooperation;
    }

}
