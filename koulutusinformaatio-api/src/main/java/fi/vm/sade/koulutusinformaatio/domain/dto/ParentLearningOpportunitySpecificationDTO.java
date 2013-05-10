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
public class ParentLearningOpportunitySpecificationDTO {

    private String id;
    private String name;
    private List<ChildLearningOpportunitySpecificationDTO> children = new ArrayList<ChildLearningOpportunitySpecificationDTO>();
    private List<ApplicationOptionDTO> applicationOptions = new ArrayList<ApplicationOptionDTO>();
    private LearningOpportunityProviderDTO provider;
    private String educationDegree;
    private String degreeName;

    private DescriptionDTO description;
    private ClassificationDTO classification;
    private CreditsDTO credits;

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

    public List<ChildLearningOpportunitySpecificationDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLearningOpportunitySpecificationDTO> children) {
        this.children = children;
    }

    public List<ApplicationOptionDTO> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOptionDTO> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public LearningOpportunityProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderDTO provider) {
        this.provider = provider;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public CreditsDTO getCredits() {
        return credits;
    }

    public void setCredits(CreditsDTO credits) {
        this.credits = credits;
    }

    public DescriptionDTO getDescription() {
        return description;
    }

    public void setDescription(DescriptionDTO description) {
        this.description = description;
    }

    public ClassificationDTO getClassification() {
        return classification;
    }

    public void setClassification(ClassificationDTO classification) {
        this.classification = classification;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }
}
