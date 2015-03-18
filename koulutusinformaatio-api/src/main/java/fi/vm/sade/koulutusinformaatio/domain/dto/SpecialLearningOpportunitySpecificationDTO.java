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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SpecialLearningOpportunitySpecificationDTO {

    private String id;
    private String name;
    private String subName;
    private String educationDegree;
    private String degreeTitle;
    private List<String> degreeTitles;
    private String qualification;
    private List<String>qualifications;
    private String goals;
    private List<ChildLearningOpportunityInstanceDTO> lois;
    private LearningOpportunityProviderDTO provider;
    private List<LearningOpportunityProviderDTO> additionalProviders = new ArrayList<LearningOpportunityProviderDTO>();
    private String structure;
    private String accessToFurtherStudies;
    private String creditValue;
    private String creditUnit;
    private String translationLanguage;
    private String educationDomain;
    private ParentLOSRefDTO parent;
    private String educationTypeUri;
    private String educationKind;
    
    private List<CodeDTO> topics;
    private List<CodeDTO> themes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(String degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public List<ChildLearningOpportunityInstanceDTO> getLois() {
        return lois;
    }

    public void setLois(List<ChildLearningOpportunityInstanceDTO> lois) {
        this.lois = lois;
    }

    public LearningOpportunityProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderDTO provider) {
        this.provider = provider;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(String accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public String getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(String creditUnit) {
        this.creditUnit = creditUnit;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public String getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(String educationDomain) {
        this.educationDomain = educationDomain;
    }

    public ParentLOSRefDTO getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefDTO parent) {
        this.parent = parent;
    }

    public String getEducationTypeUri() {
        return educationTypeUri;
    }

    public void setEducationTypeUri(String educationTypeUri) {
        this.educationTypeUri = educationTypeUri;
    }

    public void setEducationKind(String convert) {
        this.educationKind = convert;
        
    }

    public String getEducationKind() {
        return educationKind;
    }

    public List<CodeDTO> getTopics() {
        return topics;
    }

    public void setTopics(List<CodeDTO> topics) {
        this.topics = topics;
    }

    public List<CodeDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<CodeDTO> themes) {
        this.themes = themes;
    }

    public List<LearningOpportunityProviderDTO> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(List<LearningOpportunityProviderDTO> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

	public List<String> getDegreeTitles() {
		return degreeTitles;
	}

	public void setDegreeTitles(List<String> degreeTitles) {
		this.degreeTitles = degreeTitles;
	}

    public List<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
    }
}
