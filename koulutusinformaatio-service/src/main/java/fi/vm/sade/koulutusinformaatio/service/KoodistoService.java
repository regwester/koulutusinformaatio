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

package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public interface KoodistoService {

    /**
     * Search localized texts by given koodi uri
     * @param koodiUri koodi uri with specified version e.g. "tutkintonimikkeet_10129#1" or the latest version "tutkintonimikkeet_10129"
     * @return list of I18nText objects
     * @throws KoodistoException
     */
    List<I18nText> search(final String koodiUri) throws KoodistoException;

    /**
     * Search localized texts by given koodi uri, returns the first search result
     * @param koodiUri koodi uri with specified version e.g. "tutkintonimikkeet_10129#1" or the latest version "tutkintonimikkeet_10129"
     * @return I18nText object
     * @throws KoodistoException
     */
    I18nText searchFirst(final String koodiUri) throws KoodistoException;
}
