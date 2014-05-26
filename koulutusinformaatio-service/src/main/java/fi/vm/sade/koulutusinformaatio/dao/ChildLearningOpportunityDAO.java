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

package fi.vm.sade.koulutusinformaatio.dao;

import java.util.List;

import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunitySpecificationEntity;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

/**
 * @author Mikko Majapuro
 */
public class ChildLearningOpportunityDAO extends LearningOpportunitySpecificationDAO<ChildLearningOpportunitySpecificationEntity, String> {

    public ChildLearningOpportunityDAO(Datastore primaryDatastore, Datastore secondaryDatastore) {
        super(primaryDatastore, secondaryDatastore);
    }
    
    @Override
    public List<ChildLearningOpportunitySpecificationEntity> findByLoiId(String loiId) {
        Query<ChildLearningOpportunitySpecificationEntity> q = this.ds.createQuery(ChildLearningOpportunitySpecificationEntity.class).field("lois.id").equal(loiId);
        return find(q).asList();
        
        
    }
    

}
