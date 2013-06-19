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

import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.dao.entity.I18nTextEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Parent level learning opportunity specification.
 *
 * @author Hannu Lyytikainen
 */
public class ParentLOS {

    private String id;
    private I18nText name;
    private List<ParentLOI> lois;
    private Set<ApplicationOption> applicationOptions = Sets.newHashSet();
    private Provider provider;
    // rakenne
    private I18nText structureDiagram;
    // jatko-opintomahdollisuudet
    private I18nText accessToFurtherStudies;
    private I18nText degreeProgramSelection;
    // tavoitteet
    private I18nText goals;
    //koulutusala, Sosiaali-, terveys- ja liikunta-ala
    private I18nText educationDomain;
    //opintoala, Hammaslääketiede ja muu hammashuolto
    private I18nText stydyDomain;
    // koulutusaste, 32
    private String educationDegree;

    public Set<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(Set<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
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

    public List<ParentLOI> getLois() {
        return lois;
    }

    public void setLois(List<ParentLOI> lois) {
        this.lois = lois;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public I18nText getStructureDiagram() {
        return structureDiagram;
    }

    public void setStructureDiagram(I18nText structureDiagram) {
        this.structureDiagram = structureDiagram;
    }

    public I18nText getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public I18nText getDegreeProgramSelection() {
        return degreeProgramSelection;
    }

    public void setDegreeProgramSelection(I18nText degreeProgramSelection) {
        this.degreeProgramSelection = degreeProgramSelection;
    }

    public I18nText getGoals() {
        return goals;
    }

    public void setGoals(I18nText goals) {
        this.goals = goals;
    }

    public I18nText getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(I18nText educationDomain) {
        this.educationDomain = educationDomain;
    }

    public I18nText getStydyDomain() {
        return stydyDomain;
    }

    public void setStydyDomain(I18nText stydyDomain) {
        this.stydyDomain = stydyDomain;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }
}
