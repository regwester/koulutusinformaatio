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
package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationOptionIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalLOSIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.SingleParentLOSBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.SingleSpecialLOSBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.SingleUpperSecondaryLOSBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;



/**
 * 
 * @author Markus
 *
 */
@Service
@Profile("default")
public class IncrementalUpdateServiceImpl implements IncrementalUpdateService {

    public static final Logger LOG = LoggerFactory.getLogger(IncrementalUpdateServiceImpl.class);

    private TarjontaRawService tarjontaRawService;

    private EducationIncrementalDataQueryService dataQueryService;
    //private EducationDataQueryService prodDataQueryService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private KoodistoService koodistoService;
    private ProviderService providerService;
    private TarjontaService tarjontaService;
    private IndexerService indexerService;
    private OrganisaatioRawService organisaatioRawService;

    private LOSObjectCreator losCreator;
    private SingleParentLOSBuilder parentLosBuilder;
    private SingleSpecialLOSBuilder specialLosBuilder;
    private SingleUpperSecondaryLOSBuilder upperSecLosBuilder;
    
    private IncrementalApplicationSystemIndexer asIndexer;
    private IncrementalApplicationOptionIndexer aoIndexer;
    private IncrementalLOSIndexer losIndexer;

    // solr client for learning opportunity index
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;
    
    private boolean isRunning = false;
    private long runningSince = 0;

    @Autowired
    public IncrementalUpdateServiceImpl(TarjontaRawService tarjontaRawService, 
            EducationIncrementalDataQueryService dataQueryService,
            EducationIncrementalDataUpdateService dataUpdateService,
            KoodistoService koodistoService,
            ProviderService providerService,
            TarjontaService tarjontaService,
            IndexerService indexerService,
            OrganisaatioRawService organisaatioRawService,
            ParameterService parameterService,
            @Qualifier("lopAliasSolrServer") final HttpSolrServer lopAliasSolrServer,
            @Qualifier("loAliasSolrServer") final HttpSolrServer loAliasSolrServer,
            @Qualifier("locationAliasSolrServer") final HttpSolrServer locationAliasSolrServer) {
        this.tarjontaRawService = tarjontaRawService;
        this.dataQueryService = dataQueryService;
        this.dataUpdateService = dataUpdateService;
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.organisaatioRawService = organisaatioRawService;
        this.loHttpSolrServer = loAliasSolrServer;
        this.lopHttpSolrServer = lopAliasSolrServer;
        this.locationHttpSolrServer = locationAliasSolrServer;

        this.losCreator = new LOSObjectCreator(this.koodistoService, this.tarjontaRawService, this.providerService,
                this.organisaatioRawService, parameterService);
        this.parentLosBuilder = new SingleParentLOSBuilder(losCreator, tarjontaRawService);
        this.specialLosBuilder = new SingleSpecialLOSBuilder(losCreator, tarjontaRawService);
        this.upperSecLosBuilder = new SingleUpperSecondaryLOSBuilder(losCreator, tarjontaRawService);
        this.losIndexer = new IncrementalLOSIndexer(this.tarjontaRawService, 
                this.tarjontaService, 
                this.dataUpdateService,
                this.dataQueryService,
                this.indexerService,
                this.loHttpSolrServer,
                this.lopHttpSolrServer,
                this.locationHttpSolrServer,
                this.parentLosBuilder,
                this.specialLosBuilder,
                this.upperSecLosBuilder);
        this.aoIndexer = new IncrementalApplicationOptionIndexer(this.tarjontaRawService, this.dataQueryService, this.dataUpdateService, this.losIndexer);
        this.asIndexer = new IncrementalApplicationSystemIndexer(this.tarjontaRawService, 
                                                                this.tarjontaService,
                                                                this.dataQueryService, 
                                                                this.koodistoService, 
                                                                parameterService,
                                                                this.aoIndexer, 
                                                                this.losIndexer, 
                                                                this.indexerService,
                                                                this.loHttpSolrServer,
                                                                this.lopHttpSolrServer,
                                                                this.locationHttpSolrServer);
    }

    @Override
    @Async
    public void updateChangedEducationData() throws Exception {

        LOG.debug("updateChangedEducationData on its way");
        // Getting get update period
        long updatePeriod = getUpdatePeriod();
        LOG.debug(String.format("Update period: %s", updatePeriod));

        try {
            // Fetching changes within the update period
            runningSince = System.currentTimeMillis();
            isRunning = true;
            Map<String, List<String>> result = listChangedLearningOpportunities(updatePeriod);
            LOG.debug("Starting incremental update");
            if (!hasChanges(result)) {
                isRunning = false;
                runningSince = 0;
                return;
            }

            this.indexerService.clearProcessedLists();
            this.losIndexer.clearCreatedLOS();
            int komoCount = 0, hakuCount = 0, hakukohdeCount = 0, koulutusCount = 0;
            // If there are changes in komo-data, a full update is performed
            if ((result.containsKey("koulutusmoduuli") && !result.get("koulutusmoduuli").isEmpty()) || updatePeriod == 0) {
                LOG.info(String.format("Komos changed. Update period was: %s", updatePeriod));
                indexKomoChanges(result.get("koulutusmoduuli"));
                komoCount = result.get("koulutusmoduuli").size();
            }

            // If changes in haku objects indexing them
            if (result.containsKey("haku")) {
                LOG.debug("Haku changes: " + result.get("haku").size());
                indexHakuChanges(result.get("haku"));
                hakuCount = result.get("haku").size();
            }

            List<String> changedHakukohdeOids = new ArrayList<String>();
            // If changes in hakukohde, indexing them
            if (result.containsKey("hakukohde")) {
                changedHakukohdeOids = result.get("hakukohde");
                LOG.debug("Hakukohde changes: " + changedHakukohdeOids.size());
                indexHakukohdeChanges(result.get("hakukohde"));
                hakukohdeCount = result.get("hakukohde").size();
            }

            // If changes in koulutusmoduuliToteutus, indexing them
            if (result.containsKey("koulutusmoduuliToteutus")) {
                LOG.debug("Changed komotos: " + result.get("koulutusmoduuliToteutus").size());
                indexKomotoChanges(result.get("koulutusmoduuliToteutus"), changedHakukohdeOids);
                koulutusCount = result.get("koulutusmoduuliToteutus").size();
            }

            LOG.debug("Committing to solr");
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            LOG.debug("Saving successful status");
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS"));
            LOG.info(String.format("Incremental indexing finished. Indexed %s komos, %s hakus, %s hakukohdes and %s koulutus", komoCount, hakuCount,
                    hakukohdeCount, koulutusCount));

        } catch (Exception e) {
            LOG.error("Education data update failed ", e);
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, String.format("FAIL: %s", e.getMessage())));
        } finally {
            this.isRunning = false;
            this.runningSince = 0;
        }
    }

    private void indexKomotoChanges(List<String> komotoChanges,
            List<String> changedHakukohdeOids) throws Exception {
        for (String curOid : komotoChanges) {
            List<OidRDTO> aoOidDtos = null;
            try {
                aoOidDtos = this.tarjontaRawService.getHakukohdesByKomoto(curOid);
            
                if (aoOidDtos == null || this.losIndexer.isLoiAlreadyHandled(aoOidDtos, changedHakukohdeOids)) {
                    LOG.debug("Komoto: " + curOid + " was handled during hakukohde process");
                } else {
                    LOG.debug("Will index changed komoto: " + curOid);
                    this.losIndexer.indexLoiData(curOid);
                }
            } catch (Exception ex) {
                LOG.warn("problem indexing komoto: " + curOid, ex);
            }
        }
    }

    private void indexHakukohdeChanges(List<String> hakukohdeChanges) throws Exception {
        for (String curOid : hakukohdeChanges) {
            LOG.debug("Changed hakukohde: " + curOid);
            HakukohdeV1RDTO aoDto = null;
            HakuV1RDTO asDto = null;
            try {
                aoDto = this.tarjontaRawService.getV1EducationHakukohode(curOid).getResult();
                asDto = this.tarjontaRawService.getV1EducationHakuByOid(aoDto.getHakuOid()).getResult();
                this.aoIndexer.indexApplicationOptionData(aoDto, asDto);
            } catch (Exception ex) {
                LOG.warn("Problem indexing hakukohde: " + curOid, ex);
            } 

        }
    }

    private void indexHakuChanges(List<String> hakuChanges) throws Exception {
        for (String curOid : hakuChanges) {
            try {
                LOG.debug("Changed haku: " + curOid);
                this.asIndexer.indexApplicationSystemData(curOid);
            } catch (Exception ex) {
                LOG.warn("Error indexing application system: " + curOid, ex);
            }
        }
        
    }

    private void indexKomoChanges(List<String> komoChanges) throws Exception {
        for (String curKomoOid : komoChanges) {
            try {
                if (this.losIndexer.isHigherEdKomo(curKomoOid)) { 
                    this.losIndexer.indexHigherEdKomo(curKomoOid);
                }
            } catch (Exception ex) {
                LOG.warn("Error indexing komo: " + curKomoOid, ex);
            }
        }
    }

    private boolean hasChanges(Map<String, List<String>> result) {

        return (result.containsKey("koulutusmoduuli") && !result.get("koulutusmoduuli").isEmpty())
                || (result.containsKey("haku") && !result.get("haku").isEmpty())
                || (result.containsKey("hakukohde") && !result.get("hakukohde").isEmpty())
                || (result.containsKey("koulutusmoduuliToteutus") && !result.get("koulutusmoduuliToteutus").isEmpty());

    }
    

    private Map<String, List<String>> listChangedLearningOpportunities(long updatePeriod) {
        Map<String, List<String>> changemap = this.tarjontaRawService.listModifiedLearningOpportunities(updatePeriod);
        LOG.debug("Tarjonta called");

        LOG.debug("Number of changes: " + changemap.size());

        for (Entry<String, List<String>> curEntry : changemap.entrySet()) {
            LOG.debug(curEntry.getKey() + ", " + curEntry.getValue());
        }

        return changemap;
    }

    private long getUpdatePeriod() {
        DataStatus status = this.dataQueryService.getLatestSuccessDataStatus();
        if (status != null) {
            long period = (System.currentTimeMillis() - status.getLastUpdateFinished().getTime()) + status.getLastUpdateDuration();
            return period;
        }
        return 0;
    }
    
    @Override
    public boolean isRunning() {
        return isRunning;
    }
    
    @Override
    public long getRunningSince() {
        return runningSince;
    }
}
