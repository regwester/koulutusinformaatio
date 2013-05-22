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

import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.I18nTextEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationOptionDAOTest {

    @Autowired
    private ApplicationOptionDAO applicationOptionDAO;
    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @After
    public void removeTestData() {
        applicationOptionDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
    }

    @Test
    public void testSave() {
        assertEquals(0, applicationOptionDAO.count());
        ApplicationOptionEntity entity = new ApplicationOptionEntity();
        entity.setId("1.2.3");
        entity.setName(TestUtil.createI18nTextEntity("ao name fi", "ao name sv", "ao name en"));
        entity.setApplicationSystemId("123");
        entity.setEducationDegree("degree");
        List<I18nTextEntity> childLoNames = new ArrayList<I18nTextEntity>();
        childLoNames.add(TestUtil.createI18nTextEntity("clo name fi", "clo name sv", "clo name en"));
        childLoNames.add(TestUtil.createI18nTextEntity("clo name 2 fi", "clo name 2 sv", "clo name 2 en"));
        entity.setChildLOINames(childLoNames);

        LearningOpportunityProviderEntity lop = new LearningOpportunityProviderEntity();
        lop.setId("3.3.3");
        lop.setName(TestUtil.createI18nTextEntity("lop name fi", "lop name sv", "lop name en"));
        entity.setProvider(lop);
        learningOpportunityProviderDAO.save(lop);

        applicationOptionDAO.save(entity);
        assertEquals(1, applicationOptionDAO.count());
        ApplicationOptionEntity fromDB = applicationOptionDAO.get("1.2.3");
        assertNotNull(fromDB);
        assertEquals(entity.getId(), fromDB.getId());
        assertEquals(entity.getName().getTranslations().get("fi"), fromDB.getName().getTranslations().get("fi"));
        assertEquals(entity.getApplicationSystemId(), fromDB.getApplicationSystemId());
        assertEquals(entity.getEducationDegree(), fromDB.getEducationDegree());
        assertNotNull(entity.getProvider());
        assertEquals(lop.getId(), entity.getProvider().getId());
        assertNotNull(entity.getChildLOINames());
        assertEquals(2, entity.getChildLOINames().size());
    }

    @Test
    public void testFind() {
        String asId = "123";
        String lopId = "3.3.3";

        LearningOpportunityProviderEntity lop = new LearningOpportunityProviderEntity();
        lop.setId(lopId);

        ApplicationOptionEntity entity = new ApplicationOptionEntity();
        entity.setId("1.2.3");
        entity.setName(TestUtil.createI18nTextEntity("ao name fi", "ao name sv", "ao name en"));
        entity.setApplicationSystemId(asId);
        entity.setProvider(lop);

        ApplicationOptionEntity entity2 = new ApplicationOptionEntity();
        entity2.setId("1.2.4");
        entity2.setName(TestUtil.createI18nTextEntity("ao2 name fi", "ao2 name sv", "ao2 name en"));
        entity2.setApplicationSystemId(asId);
        entity2.setProvider(lop);

        ApplicationOptionEntity entity3 = new ApplicationOptionEntity();
        entity3.setId("1.2.5");
        entity3.setName(TestUtil.createI18nTextEntity("ao3 name fi", "ao3 name sv", "ao3 name en"));
        entity3.setApplicationSystemId("4.4.4");
        entity3.setProvider(lop);

        learningOpportunityProviderDAO.save(lop);
        applicationOptionDAO.save(entity);
        applicationOptionDAO.save(entity2);
        applicationOptionDAO.save(entity3);

        List<ApplicationOptionEntity> result = applicationOptionDAO.find(asId, lopId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertEquals(asId, result.get(0).getApplicationSystemId());
    }
}
