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

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
public class GeneralUpdateServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralUpdateServiceImpl.class);

    private TarjontaService tarjontaService;
    private IndexerService indexerService;
    private EducationDataUpdateService educationDataUpdateService;
    private ArticleService articleService;
    private ProviderService providerService;

    private LocationService locationService;

    @Autowired
    public GeneralUpdateServiceImpl(TarjontaService tarjontaService, IndexerService indexerService,
                                    EducationDataUpdateService educationDataUpdateService,
                                    LocationService locationService,
                                    ProviderService providerService,
                                    ArticleService articleService) {
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.educationDataUpdateService = educationDataUpdateService;
        this.locationService = locationService;
        this.articleService = articleService;
        this.providerService = providerService;
    }

    public synchronized void updateGeneralData(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws Exception {
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        indexProviders(lopUpdateSolr, loUpdateSolr, locationUpdateSolr);
        LOG.info("Providers indexed");

        List<Code> edTypeCodes = this.tarjontaService.getEdTypeCodes();
        indexerService.addFacetCodes(edTypeCodes, loUpdateSolr);
        LOG.info("Education types indexded.");

        List<Code> edBaseEdCodes = this.tarjontaService.getEdBaseEducationCodes();
        indexerService.addFacetCodes(edBaseEdCodes, loUpdateSolr);
        LOG.info("Base educations indexded.");

        List<Location> locations = locationService.getMunicipalities();
        LOG.debug("Got locations");
        indexerService.addLocations(locations, locationUpdateSolr);
        LOG.info("Location indexed");

        List<CalendarApplicationSystem> applicationSystems = this.tarjontaService.findApplicationSystemsForCalendar();
        for (CalendarApplicationSystem curAs : applicationSystems) {
            LOG.debug("Indexing application system: {}", curAs.getId());
            this.indexerService.indexASToSolr(curAs, loUpdateSolr);
        }
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        LOG.info("Application systems indexed");

        List<Article> articles = this.articleService.fetchArticles();
        LOG.debug("Articles fetched");
        indexerService.addArticles(loUpdateSolr, articles);
        LOG.info("Articles indexed to solr");

        indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, true);
    }

    /*
     * 
     * Handles the indexing of providers from organisaatio service to solr and MongoDB.
     * This method is used when indexing organizations (Oppilaitos, Toimipiste) which 
     * are not providers of learning opportunities.
     * 
     */
    private void indexProviders(HttpSolrServer lopUpdateSolr, HttpSolrServer loUpdateSolr, HttpSolrServer locationUpdateSolr) throws IOException, SolrServerException, KIException {

        List<OrganisaatioPerustieto> orgBasics = this.providerService.fetchOpplaitokset();
        LOG.debug("Oppilaitokset fetched");
        createAndSaveProviders(orgBasics, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        LOG.debug("Oppilaitokset saved");
        orgBasics = this.providerService.fetchToimipisteet();
        createAndSaveProviders(orgBasics, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        orgBasics = this.providerService.fetchOppisopimusToimipisteet();
        createAndSaveProviders(orgBasics, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        LOG.debug("toimipisteet saved");
    }

    /*
     * Indexes and saves the given list of organizations. 
     */
    private void createAndSaveProviders(List<OrganisaatioPerustieto> orgBasics,
                                        HttpSolrServer lopUpdateSolr) throws IOException, SolrServerException, KIException {
        LOG.debug("organisations length: {}", orgBasics.size());
        for (OrganisaatioPerustieto curOrg : orgBasics) {

            LOG.debug("Fetching org {}", curOrg.getOid());
            if (!indexerService.isDocumentInIndex(curOrg.getOid(), lopUpdateSolr)) {
                LOG.debug("Indexing organisaatio: {}", curOrg.getOid());
                Provider curProv = null;
                try {
                    curProv = this.providerService.getByOID(curOrg.getOid());
                } catch (Exception ex) {
                    LOG.warn("Problem indexing organization: " + curOrg.getOid(), ex);
                    continue;
                }
                if (curProv.getOlTypeFacets() != null && !curProv.getOlTypeFacets().isEmpty()) {
                    this.educationDataUpdateService.save(curProv);
                    this.indexerService.createProviderDocs(curProv, lopUpdateSolr, new HashSet<String>(), new HashSet<String>(), new HashSet<String>(),
                            new HashSet<String>());
                    LOG.debug("Indexed and saved organisaatio: {}", curOrg.getOid());
                }

            }
        }
    }
}