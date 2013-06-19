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

import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ParentLearningOpportunitySpecificationDAOTest {

    @Autowired
    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    @Autowired
    private ApplicationOptionDAO applicationOptionDAO;
    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    @Autowired
    private ChildLearningOpportunitySpecificationDAO childLearningOpportunitySpecificationDAO;
    @Autowired
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;

    @After
    public void removeTestData() {
        parentLearningOpportunitySpecificationDAO.getCollection().drop();
        applicationOptionDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
        childLearningOpportunityDAO.getCollection().drop();
        childLearningOpportunitySpecificationDAO.getCollection().drop();
    }

    @Test
    public void testSave() {
        assertEquals(0, parentLearningOpportunitySpecificationDAO.count());
        assertEquals(0, applicationOptionDAO.count());
        assertEquals(0, learningOpportunityProviderDAO.count());
        assertEquals(0, childLearningOpportunityDAO.count());
        assertEquals(0, childLearningOpportunitySpecificationDAO.count());

        ParentLearningOpportunitySpecificationEntity entity = new ParentLearningOpportunitySpecificationEntity();
        entity.setId("1.2.3.4.5");
        entity.setName(TestUtil.createI18nTextEntity("parent name fi", "parent name sv", "parent name en"));
        entity.setEducationDegree("32");
        List<ChildLearningOpportunitySpecificationEntity> children = new ArrayList<ChildLearningOpportunitySpecificationEntity>();

        ChildLearningOpportunitySpecificationEntity childLOS = new ChildLearningOpportunitySpecificationEntity();
        childLOS.setId("2.2.2");
        childLOS.setName(TestUtil.createI18nTextEntity("child name fi", "child name sv", "child name en"));

        LearningOpportunityProviderEntity provider = new LearningOpportunityProviderEntity();
        provider.setId("5.5.5");
        provider.setName(TestUtil.createI18nTextEntity("provider name fi", "provider name sv", "provider name en"));
        Set<String> aoIds = new HashSet<String>();
        aoIds.add("6.7.5.3");
        aoIds.add("4.7.9.3");
        provider.setApplicationSystemIds(aoIds);

        Set<ApplicationOptionEntity> aos = new HashSet<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("7.7.7");
        ApplicationSystemEntity as = new ApplicationSystemEntity();
        as.setId("sysId");
        ao.setApplicationSystem(as);
        ao.setEducationDegree("degree");
        ao.setName(TestUtil.createI18nTextEntity("ao name fi", "ao name sv", "ao name en"));
        ao.setProvider(provider);
        aos.add(ao);

        ChildLearningOpportunityEntity childLOI = new ChildLearningOpportunityEntity();
        childLOI.setId("34345");
        childLOI.setApplicationSystemId("1.2.3.4.5");
        childLOI.setApplicationOption(ao);

        List<ChildLearningOpportunityEntity> childLOIs = new ArrayList<ChildLearningOpportunityEntity>();
        childLOIs.add(childLOI);
        childLOS.setChildLOIs(childLOIs);

        entity.setApplicationOptions(aos);

        children.add(childLOS);
        entity.setChildren(children);
        entity.setProvider(provider);

        learningOpportunityProviderDAO.save(provider);
        applicationOptionDAO.save(ao);

        childLearningOpportunityDAO.save(childLOI);
        childLearningOpportunitySpecificationDAO.save(childLOS);
        parentLearningOpportunitySpecificationDAO.save(entity);

        assertEquals(1, applicationOptionDAO.count());
        assertEquals(1, parentLearningOpportunitySpecificationDAO.count());
        assertEquals(1, learningOpportunityProviderDAO.count());
        ParentLearningOpportunitySpecificationEntity fromDB = parentLearningOpportunitySpecificationDAO.get("1.2.3.4.5");
        assertNotNull(fromDB);
        assertNotNull(fromDB.getChildren());
        assertNotNull(fromDB.getApplicationOptions());
        assertEquals(1, fromDB.getChildren().size());
        assertEquals(1, fromDB.getApplicationOptions().size());
        assertEquals(ao.getId(), fromDB.getApplicationOptions().iterator().next().getId());
        assertNotNull(fromDB.getChildren().get(0).getChildLOIs());
        assertEquals(ao.getId(), fromDB.getChildren().get(0).getChildLOIs().get(0).getApplicationOption().getId());
        assertEquals(entity.getId(), fromDB.getId());
        assertEquals(entity.getChildren().get(0).getId(), fromDB.getChildren().get(0).getId());
        assertEquals(entity.getApplicationOptions().iterator().next().getId(),
                fromDB.getApplicationOptions().iterator().next().getId());
        assertNotNull(fromDB.getProvider());
        assertEquals(provider.getId(), fromDB.getProvider().getId());
    }

    @Test
    public void testGetParentLearningOpportunity() {
        String oid = "1.2.3";
        ParentLearningOpportunitySpecificationEntity plo = new ParentLearningOpportunitySpecificationEntity();
        plo.setId(oid);
        parentLearningOpportunitySpecificationDAO.save(plo);
        ParentLearningOpportunitySpecificationEntity fromDB = parentLearningOpportunitySpecificationDAO.get(oid);
        assertNotNull(fromDB);
        assertEquals(oid, fromDB.getId());
    }

    @Test
    public void testGetParentLearningOpportunityNotFound() {
        ParentLearningOpportunitySpecificationEntity entity = parentLearningOpportunitySpecificationDAO.get("1.1.1");
        assertNull(entity);
    }
}
