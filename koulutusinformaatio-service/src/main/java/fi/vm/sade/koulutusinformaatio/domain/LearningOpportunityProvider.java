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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityProvider {

    private String id;
    private String name;
    private Set<String> applicationSystemIDs = new HashSet<String>();

    public LearningOpportunityProvider(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public LearningOpportunityProvider() {}

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

    public Set<String> getApplicationSystemIDs() {
        return applicationSystemIDs;
    }

    public void setApplicationSystemIDs(Set<String> applicationSystemIDs) {
        this.applicationSystemIDs = applicationSystemIDs;
    }
}
