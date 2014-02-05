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

package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public interface TarjontaService {

    /**
     * Finds a parent learning opportunity by oid.
     *
     *
     * @param oid
     * @return
     */
    public List<LOS> findParentLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException;

    /**
     * Returns a list of all parent learning opportunity oids.
     *
     * @return list of oids
     */
    public List<String> listParentLearnignOpportunityOids();

    /**
     * Returns a list of parent learning opportunity oids
     * @param count maximum count of results
     * @param startIndex start index of results
     * @return list of oids
     */
    public List<String> listParentLearnignOpportunityOids(int count, int startIndex);
    
    public List<HigherEducationLOS> findHigherEducations() throws KoodistoException;

	public HigherEducationLOS findHigherEducationLearningOpportunity(
			String oid) throws TarjontaParseException, KoodistoException;
}
