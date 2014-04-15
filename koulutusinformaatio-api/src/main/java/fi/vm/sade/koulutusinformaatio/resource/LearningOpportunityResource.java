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

package fi.vm.sade.koulutusinformaatio.resource;

import fi.vm.sade.koulutusinformaatio.domain.dto.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Path("/lo")
public interface LearningOpportunityResource {

    /**
     * Searches learning opportunities.
     *
     * @param text search key
     * @param prerequisite base education prerequisite (pohjakoulutusvaatimus) filter
     *                     if none is provided, no filtering is used
     * @return list of search results
     */
    @GET
    @Path("search/{text}")
    @Produces(MediaType.APPLICATION_JSON)
    public LOSearchResultListDTO searchLearningOpportunities(@PathParam("text") String text,
                                                             @QueryParam("prerequisite") String prerequisite,
                                                             @QueryParam("city") List<String> cities,
                                                             @QueryParam("facetFilters") List<String> facetFilters,
                                                             @QueryParam("articleFacetFilters") List<String> articleFilters,
                                                             @QueryParam("lang") String lang,
                                                             @DefaultValue(value = "false") @QueryParam("ongoing") boolean ongoing,
                                                             @DefaultValue(value = "false") @QueryParam("upcoming") boolean upcoming,
                                                             @DefaultValue(value = "false") @QueryParam("upcomingLater") boolean upcomingLater,
                                                             @DefaultValue(value = "0") @QueryParam("start") int start,
                                                             @DefaultValue(value = "100") @QueryParam("rows") int rows,
                                                             @QueryParam("sort") String sort, 
                                                             @DefaultValue(value = "asc") @QueryParam("order") String order,
                                                             @QueryParam("lopFilter") String lopFilter,
                                                             @QueryParam("educationCodeFilter") String educationCodeFilter,
                                                             @QueryParam("excludes") List<String> excludes,
                                                             @QueryParam("searchType") SearchType searchType);

    /**
     * Fetches a parent learning opportunity. Contains parent information and
     * references to all the child learning opportunities that belong to the parent.
     *
     * Parent lo texts are translated to language corresponding given lang parameter.
     * If the given language is not found or the parameter is null it will try to fall back to default 'fi' or other
     * language found
     *
     * @param parentId learning opportunity id
     * @param lang translation language (optional)
     * @param uiLang user interface language, used to translate some information
     * @return parent learning opportunity dto object
     */
    @GET
    @Path("parent/{parentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(@PathParam("parentId") String parentId,
                                                                                  @QueryParam("lang") String lang,
                                                                                  @QueryParam("uiLang") String uiLang);

    /**
     * Fetches a child learning opportunity that belongs to the specified parent.
     * Child lo texts are translated to language corresponding given lang parameter.
     * If the given language is not found or the parameter is null it fall back to default (education) language.
     *
     * @param cloId child learning opportunity id
     * @param lang translation language (optional)
     * @param uiLang user interface language, used to translate some information
     * @return child learning opportunity dto object
     */
    @GET
    @Path("child/{cloId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(@PathParam("cloId") String cloId,
                                                                                @QueryParam("lang") String lang,
                                                                                @QueryParam("uiLang") String uiLang);
    /**
     * Fetches an upper secondary (lukio) learning opportunity
     *
     * @param id learning opportunity identifier
     * @param lang language
     * @param uiLang user interface language
     * @return upper secondary learning opportunity
     */
    @GET
    @Path("upsec/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(@PathParam("id") String id,
                                                                                                  @QueryParam("lang") String lang,
                                                                                                  @QueryParam("uiLang") String uiLang);

    /**
     * Fetches a special learning opportunity specification.
     *
     * @param id los id
     * @param lang translation language
     * @param uiLang secondary translation language
     * @return special learning opportunity specification
     */
    @GET
    @Path("special/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpecialLearningOpportunitySpecificationDTO getSpecialLearningOpportunity(@PathParam("id") String id,
                                                                                    @QueryParam("lang") String lang,
                                                                                    @QueryParam("uiLang") String uiLang);
    
    /**
     * Fetches a university of applied science learning opportunity specification.
     *
     * @param id los id
     * @param lang translation language
     * @param uiLang secondary translation language
     * @return university of applied science learning opportunity specification
     */
    @GET
    @Path("highered/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HigherEducationLOSDTO getHigherEducationLearningOpportunity(@PathParam("id") String id,
                                                                                    @QueryParam("lang") String lang,
                                                                                    @QueryParam("uiLang") String uiLang);

    /**
     * Fetches suggested terms to be used in free text search. 
     * The returned terms match the term given as parameter.
     *
     * @param term for which matching terms are searched
     * @param lang language
     * @return upper secondary learning opportunity
     */
    @GET
    @Path("autocomplete/{term}")
    @Produces(MediaType.APPLICATION_JSON)
    public SuggestedTermsResultDTO getSuggestedTerms(@PathParam("term") String term,
                                                     @QueryParam("lang") String lang);
    
    /**
     * Fetches a higher education learning opportunity. To be used in preview for learning opportunity.
     *
     * @param oid of the learning opportunity
     * @param lang language
     * @return higher education learning opportunity
     */
    @GET
    @Path("preview/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public HigherEducationLOSDTO previewLearningOpportunity(@PathParam("oid") String oid,
                                                     @QueryParam("lang") String lang,
                                                     @QueryParam("uiLang") String uiLang);
}
