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

import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class UpdateServiceImpl implements UpdateService {

    public static final Logger LOG = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private EducationAggregatorService educationAggregatorService;
    private IndexerService indexerService;
    private EducationDataUpdateService educationDataUpdateService;
    private TransactionManager transactionManager;
    private static final int MAX_RESULTS = 100;
    private boolean running = false;
    private LocationService locationService;


    @Autowired
    public UpdateServiceImpl(EducationAggregatorService educationAggregatorService,
                             IndexerService indexerService, EducationDataUpdateService educationDataUpdateService,
                             TransactionManager transactionManager, LocationService locationService) {
        this.educationAggregatorService = educationAggregatorService;
        this.indexerService = indexerService;
        this.educationDataUpdateService = educationDataUpdateService;
        this.transactionManager = transactionManager;
        this.locationService = locationService;
    }

    @Override
    @Async
    public synchronized void updateAllEducationData() throws Exception {
        try {
            LOG.info("Starting full education data update");
            running = true;
            this.transactionManager.beginTransaction();

            int count = MAX_RESULTS;
            int index = 0;

            while(count >= MAX_RESULTS) {
                LOG.debug("Searching parent learning opportunity oids count: " + count + ", start index: " + index);
                List<String> parentOids = educationAggregatorService.listParentLearnignOpportunityOids(count, index);
                count = parentOids.size();
                index += count;

               for (String parentOid : parentOids) {
                    List<ParentLOS> parents = null;
                    try {
                        parents = educationAggregatorService.findParentLearningOpportunity(parentOid);
                    } catch (TarjontaParseException e) {
                        LOG.warn("Exception while updating parent learning opportunity, oidMessage: " + e.getMessage());
                        continue;
                    }
                    for (ParentLOS parent : parents) {
                        this.indexerService.addParentLearningOpportunity(parent);
                        this.educationDataUpdateService.save(parent);
                    }
                }
                this.indexerService.commitLOChanges();
            }
            List<Location> locations = locationService.getMunicipalities();
            indexerService.addLocations(locations);
            indexerService.commitLOChanges();
            this.transactionManager.commit();
            LOG.info("Education data update successfully finished");
        } catch (Exception e) {
                LOG.error("Education data update failed ", e);
            this.transactionManager.rollBack();
        } finally {
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
