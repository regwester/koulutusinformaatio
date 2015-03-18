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

package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
@Component
public class LearningOpportunityDirector {

    public List<LOS> constructLearningOpportunities(LearningOpportunityBuilder builder) throws TarjontaParseException, KoodistoException {
        return builder.resolveParentLOSs().resolveChildLOSs().reassemble().filter().build();
    }

    public List<LOS> constructUpperSecondaryLearningOpportunities(LearningOpportunityBuilder builder) {
        return null;
    }

    public HigherEducationLOS constructHigherEducationLOs(
            KoulutusV1RDTO result) {

        return null;
    }

}