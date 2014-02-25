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

package fi.vm.sade.koulutusinformaatio.resource.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.koulutusinformaatio.converter.ConverterUtil;
import fi.vm.sade.koulutusinformaatio.converter.ProviderToSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import fi.vm.sade.koulutusinformaatio.util.ResourceBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Handles directory calls that lists provider and learning opportunity links
 * for crawlers.
 *
 * @author Hannu Lyytikainen
 */
@Component
@Path("/{lang}/hakemisto")
public class DirectoryResource {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final List<String> alphabets = Lists.newArrayList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Å", "Ä", "Ö");

    private SearchService searchService;
    private EducationDataQueryService educationDataQueryService;
    private LearningOpportunityService learningOpportunityService;
    private String baseUrl;
    private ResourceBundleHelper resourceBundleHelper;


    @Autowired
    public DirectoryResource(SearchService searchService, EducationDataQueryService educationDataQueryService,
                             LearningOpportunityService learningOpportunityService,
                             @Value("${koulutusinformaatio.baseurl.learningopportunity}") String baseUrl) {
        this.searchService = searchService;
        this.educationDataQueryService = educationDataQueryService;
        this.learningOpportunityService = learningOpportunityService;
        this.baseUrl = baseUrl;
        this.resourceBundleHelper = new ResourceBundleHelper();
    }

    @GET
    @Path("oppilaitokset")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response getProviders(@PathParam("lang") String lang) throws URISyntaxException {
        return Response.seeOther(new URI(String.format("%s/hakemisto/oppilaitokset/A", lang))).build();
    }

    @GET
    @Path("oppilaitokset/{letter}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response getProvidersWithFirstLetter(@PathParam("lang") String lang,
                                                @PathParam("letter") String letter) throws URISyntaxException {
        if (alphabets.contains(letter)) {
            Map<String, Object> model = initModel(lang);
            List<Provider> providers = null;
            try {
                providers = searchService.searchLearningOpportunityProviders(letter, lang, true);
            } catch (SearchException e) {
                // error view
            }

            List<ProviderSearchResult> searchResults = ProviderToSearchResult.convertAll(providers);
            model.put("providers", searchResults);
            model.put("alphabets", alphabets);
            model.put("letter", letter);
            model.put("baseUrl", baseUrl);
            model.put("lang", lang);
            return Response.status(Response.Status.OK).entity(new Viewable("/providers.ftl", model)).build();
        } else {
            return getProviders(lang);
        }
    }

    @GET
    @Path("oppilaitokset/{letter}/{providerId}/koulutukset")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getLearningOpportunities(@PathParam("lang") String lang, @PathParam("letter") String letter,
                                             @PathParam("providerId") final String providerId) {
        List<LearningOpportunitySearchResultDTO> resultList = null;
        Provider provider = null;
        resultList = learningOpportunityService.findLearningOpportunitiesByProviderId(providerId, lang);
        try {
            provider = educationDataQueryService.getProvider(providerId);
        } catch (ResourceNotFoundException e) {
            // error page
        }
        Map<String, Object> model = initModel(lang);
        model.put("alphabets", alphabets);
        model.put("letter", letter);
        model.put("provider", ConverterUtil.getTextByLanguageUseFallbackLang(provider.getName(), lang));
        model.put("learningOpportunities", resultList);
        model.put("baseUrl", baseUrl);
        model.put("lang", lang);

        return new Viewable("/education.ftl", model);
    }

    private Map<String, Object> initModel(String lang) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("messages", resourceBundleHelper.getBundle(lang));
        return model;
    }
}