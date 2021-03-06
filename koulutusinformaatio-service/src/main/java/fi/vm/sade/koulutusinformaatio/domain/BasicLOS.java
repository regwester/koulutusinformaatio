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
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class BasicLOS<T extends LOI> extends InstantiatedLOS<T> {

    private List<Provider> additionalProviders = new ArrayList<Provider>(); //muut tarjoajat
    private I18nText structure;                 // rakenne
    private I18nText accessToFurtherStudies;    // jatko-opintomahdollisuudet
    private String educationDegree;             // koulutusaste, 32
    private String creditValue;                 // laajuus arvo, 120
    private I18nText creditUnit;                // laajuus yksikkö op

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public I18nText getStructure() {
        return structure;
    }

    public void setStructure(I18nText structure) {
        this.structure = structure;
    }

    public I18nText getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public I18nText getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nText creditUnit) {
        this.creditUnit = creditUnit;
    }

    public List<Provider> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(List<Provider> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }
}
