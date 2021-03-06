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

import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.AoSolrSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.ArticleResult;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SuggestedTermsResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;

public interface SearchService {

    List<Provider> searchLearningOpportunityProviders(
            final String term, final String asId, final List<String> baseEducations, final boolean vocational,
            final boolean nonVocational, int start, int rows, String lang, boolean prefix, String type) throws SearchException;

    List<Provider> searchLearningOpportunityProviders(final String term, String lang, boolean prefix, String type) throws SearchException;

    List<AoSolrSearchResult> searchOngoingApplicationOptions(final String asId, List<Provider> learningOpportunityProviders, List<String> baseEducations)
            throws SearchException;

    LOSearchResultList searchLearningOpportunities(final String term, final String prerequisite,
            List<String> cities, List<String> facetFilters,
            List<String> articleFilters, List<String> providerFilters,
            String lang, boolean ongoing, boolean upcoming,
            boolean upcomingLater,
            int start, int rows, String sort, String order,
            String lopFilter, String educationCodeFilter, List<String> excludes, String asId, SearchType searchType) throws SearchException;

    List<Location> searchLocations(final String term, final String lang) throws SearchException;
    List<Location> getLocations(List<String> codes, final String lang) throws SearchException;
    List<Location> getDistricts(final String lang) throws SearchException;
    List<Location> getChildLocations(List<String> districts, final String lang) throws SearchException;
    SuggestedTermsResult searchSuggestedTerms(String term, String lang) throws SearchException;
    List<ArticleResult> searchArticleSuggestions(String filter, String lang) throws SearchException;

    /**
     * Returns a list of characters that the providers' names start with.
     *
     * @param lang language of provider
     * @return list of characters
     */
    List<String> getProviderFirstCharacterList(String lang) throws SearchException;

    /**
     * Fetches the list of provider types that are currently in use.
     *
     * @return list of types as code objects
     */
    List<Code> getProviderTypes(String firstCharacter, String lang) throws SearchException;

    List<CalendarApplicationSystem> findApplicationSystemsForCalendar() throws SearchException;
    List<CalendarApplicationSystem> findApplicationSystemsForCalendar(String targetGroupCode) throws SearchException;
}
