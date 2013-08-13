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

package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import fi.vm.sade.koulutusinformaatio.service.impl.query.LearningOpportunityQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.MapToSolrQueryTransformer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Component
public class SearchServiceSolrImpl implements SearchService {

    public static final Logger LOG = LoggerFactory.getLogger(SearchServiceSolrImpl.class);

    public static final String ID = "AOId";
    public static final String AS_START_DATE_PREFIX = "asStart_";
    public static final String AS_END_DATE_PREFIX = "asEnd_";

    private HttpSolrServer httpSolrServer;

    private final HttpSolrServer lopHttpSolrServer;
    private final HttpSolrServer loHttpSolrServer;
    private final MapToSolrQueryTransformer mapToSolrQueryTransformer = new MapToSolrQueryTransformer();

    @Autowired
    public SearchServiceSolrImpl(@Qualifier("lopHttpSolrServer") final HttpSolrServer lopHttpSolrServer,
                                 @Qualifier("loHttpSolrServer") final HttpSolrServer loHttpSolrServer) {
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.loHttpSolrServer = loHttpSolrServer;
    }

    @Override
    public List<Provider> searchLearningOpportunityProviders(
            String term, String asId, String prerequisite, boolean vocational) throws SearchException {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(3);
        Set<Provider> providers = new HashSet<Provider>();
        String startswith = term.trim();
        if (!startswith.isEmpty()) {
            // key word params
            parameters.put("name", createParameter(term + "*"));
            parameters.put("asId", createParameter(asId));
            //parameters = addPrerequisite(parameters, prerequisite, vocational);
            SolrQuery query = mapToSolrQueryTransformer.transform(parameters.entrySet());

            QueryResponse queryResponse = null;
            try {
                queryResponse = lopHttpSolrServer.query(query);
            } catch (SolrServerException e) {
                throw new SearchException("Solr search error occured.");
            }

            for (SolrDocument result : queryResponse.getResults()) {
                Provider provider = new Provider();
                provider.setId(result.get("id").toString());

                // TODO: i18n handling
                Map<String, String> texts = Maps.newHashMap();
                texts.put("fi", result.get("name").toString());

                provider.setName(new I18nText(texts));
                providers.add(provider);
            }

        }
        return new ArrayList<Provider>(providers);
    }

    @Override
    public LOSearchResultList searchLearningOpportunities(String term, String prerequisite,
                                                          List<String> cities, int start, int rows) throws SearchException {

        System.out.println("LOGGER IMPL DEBUG: " + LOG.getClass().toString());

        LOSearchResultList searchResultList = new LOSearchResultList();
        String trimmed = term.trim();
        if (!trimmed.isEmpty()) {
            SolrQuery query = new LearningOpportunityQuery(term, prerequisite, cities, start, rows);

            try {
                LOG.debug(
                        URLDecoder.decode(
                                new StringBuilder().append("Searching learning opportunities with query string: ").append(query.toString()).toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                LOG.debug("Could not log search query");
            }

            QueryResponse response = null;
            try {
                response = loHttpSolrServer.query(query);
                searchResultList.setTotalCount(response.getResults().getNumFound());
            } catch (SolrServerException e) {
                throw new SearchException("Solr search error occured.");
            }

            for (SolrDocument doc : response.getResults()) {
                String parentId = doc.get("parentId") != null ? doc.get("parentId").toString() : null;
                String losId = doc.get("losId") != null ? doc.get("losId").toString() : null;
                String id = doc.get("losId") != null ? doc.get("losId").toString() : doc.get("id").toString();

                LOSearchResult lo = null;
                try {
                    lo = new LOSearchResult(
                            id, doc.get("name_fi").toString(),
                            doc.get("lopId").toString(), doc.get("lopName_fi").toString(), parentId, losId);

                    updateAsStatus(lo, doc);
                } catch (Exception e) {
                    continue;
                }
                searchResultList.getResults().add(lo);
            }
        }

        return searchResultList;
    }

    private void updateAsStatus(LOSearchResult lo, SolrDocument doc) {
        lo.setAsOngoing(false);
        Date now = new Date();
        Date nextStarts = null;
        Date nextEnds = null;

        for (String startKey : doc.keySet()) {
            if (startKey.startsWith(AS_START_DATE_PREFIX)) {
                String endKey = new StringBuilder().append(AS_END_DATE_PREFIX)
                        .append(startKey.split("_")[1]).toString();

                Date start = ((List<Date>) doc.get(startKey)).get(0);
                Date end = ((List<Date>) doc.get(endKey)).get(0);

                if (start.before(now) && now.before(end)) {
                    lo.setAsOngoing(true);
                    return;
                }

                if (nextStarts == null || (start.after(now) && start.before(nextStarts))) {
                    nextStarts = start;
                    nextEnds = end;
                }

            }
        }

        lo.setNextAs(new DateRange(nextStarts, nextEnds));

    }

    private MultiValueMap<String, String> addPrerequisite(MultiValueMap<String, String> parameters, String prerequisite, boolean vocational) {
        String realPrerequisite = prerequisite;
        if (realPrerequisite.equals("KESKEYTYNYT") || realPrerequisite.equals("ULKOMAINEN_TUTKINTO")) {
            return parameters; // Ei suodatusta
        }
        if (realPrerequisite.equals("YLIOPPILAS")) {
            parameters.put("LOIPrerequisite", createParameter("(5 OR 9)"));
        } else if (realPrerequisite.equals("PERUSKOULU")) {
            parameters.put("LOIPrerequisite", createParameter("(1 OR 2 OR 4 OR 5)"));
        } else if (realPrerequisite.equals("OSITTAIN_YKSILOLLISTETTY")
                || realPrerequisite.equals("ERITYISOPETUKSEN_YKSILOLLISTETTY")
                || realPrerequisite.equals("YKSILOLLISTETTY")) {
            parameters.put("LOIPrerequisite", createParameter("(1 OR 2 OR 4 OR 5 OR 6)"));
        }
        if (vocational) {
            parameters.put("AOEducationDegree", createParameter("(NOT 32)"));
        }
        return parameters;
    }

    private List<String> createParameter(String value) {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(value);
        return parameters;

    }

}
