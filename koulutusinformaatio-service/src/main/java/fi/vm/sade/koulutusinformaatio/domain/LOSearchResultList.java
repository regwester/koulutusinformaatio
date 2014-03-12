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

package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class LOSearchResultList {

    private List<LOSearchResult> results = new ArrayList<LOSearchResult>();
    private List<ArticleResult> articleresults = new ArrayList<ArticleResult>();
    private long totalCount;
    private Facet teachingLangFacet;
    private Facet filterFacet;
    private Facet appStatusFacet;
    private Facet edTypeFacet;
    private Facet prerequisiteFacet;
    private Facet topicFacet;
    private FacetValue lopRecommendationFilter;
    private FacetValue educationCodeRecommendationFilter;


    public List<LOSearchResult> getResults() {
        return results;
    }

    public void setResults(List<LOSearchResult> results) {
        this.results = results;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Facet getTeachingLangFacet() {
        return teachingLangFacet;
    }

    public void setTeachingLangFacet(Facet teachingLangFacet) {
        this.teachingLangFacet = teachingLangFacet;
    }

    public Facet getFilterFacet() {
        return filterFacet;
    }

    public void setFilterFacet(Facet filterFacet) {
        this.filterFacet = filterFacet;
    }

    public void setAppStatusFacet(Facet haunTila) {
        this.appStatusFacet = haunTila;
    }
    
    public Facet getAppStatusFacet() {
        return this.appStatusFacet;
    }

    public Facet getEdTypeFacet() {
        return edTypeFacet;
    }

    public void setEdTypeFacet(Facet edTypeFacet) {
        this.edTypeFacet = edTypeFacet;
    }

    public Facet getPrerequisiteFacet() {
        return prerequisiteFacet;
    }

    public void setPrerequisiteFacet(Facet prerequisiteFacet) {
        this.prerequisiteFacet = prerequisiteFacet;
    }

    public Facet getTopicFacet() {
        return topicFacet;
    }

    public void setTopicFacet(Facet topicFacet) {
        this.topicFacet = topicFacet;
    }

    public FacetValue getLopRecommendationFilter() {
        return lopRecommendationFilter;
    }

    public void setLopRecommendationFilter(FacetValue lopRecommendationFilter) {
        this.lopRecommendationFilter = lopRecommendationFilter;
    }

    public FacetValue getEducationCodeRecommendationFilter() {
        return educationCodeRecommendationFilter;
    }

    public void setEducationCodeRecommendationFilter(
            FacetValue educationCodeRecommendationFilter) {
        this.educationCodeRecommendationFilter = educationCodeRecommendationFilter;
    }

    public List<ArticleResult> getArticleresults() {
        return articleresults;
    }

    public void setArticleresults(List<ArticleResult> articleresults) {
        this.articleresults = articleresults;
    }
}
