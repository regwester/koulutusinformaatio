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

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mikko Majapuro
 */
public class LearningOpportunityServiceImplTest {

    private LearningOpportunityService learningOpportunityService;
    private EducationDataQueryService educationDataQueryService;
    private ParentLO parentLO;
    private ChildLO childLO;

    @Before
    public void setUp() throws ResourceNotFoundException {
        educationDataQueryService = mock(EducationDataQueryService.class);

        parentLO = new ParentLO();
        parentLO.setId("1234");
        parentLO.setAccessToFurtherStudies(createI18Text("AccessToFurtherStudies"));
        parentLO.setEducationDegree("32");
        parentLO.setName(createI18Text("name"));
        parentLO.setGoals(createI18Text("goals"));
        parentLO.setStructureDiagram(createI18Text("StructureDiagram"));
        parentLO.setEducationDomain(createI18Text("EducationDomain"));
        parentLO.setStydyDomain(createI18Text("StudyDomain"));
        List<ChildLORef> childLORefs = new ArrayList<ChildLORef>();
        childLORefs.add(createChildLORef(createI18Text("c1"), "c1 fi", "as123", "loi123", "los123"));
        childLORefs.add(createChildLORef(createI18Text("c2"), "c2 fi", "as123", "loi124", "los124"));
        childLORefs.add(createChildLORef(createI18Text("c3"), "c3 fi", "as124", "loi125", "los123"));

        ParentLOI parentLOI = new ParentLOI();
        parentLOI.setId("123.123");
        parentLOI.setChildRefs(childLORefs);
        parentLO.setLois(Lists.newArrayList(parentLOI));
        Set<String> asIds = new HashSet<String>();
        asIds.add("as123");
        asIds.add("as124");
        parentLO.setProvider(createProvider("p1234", createI18Text("provider1"), asIds));
        Set<ApplicationOption> aos = new HashSet<ApplicationOption>();
        aos.add(createApplicationOption("ao123 fi", createI18Text("ao name"), "as123", parentLO.getProvider(), new Date(), 100, 25,6, 77, childLORefs, "32"));
        parentLO.setApplicationOptions(aos);

        childLO = new ChildLO();
        childLO.setId("lo123");
        childLO.setName(createI18Text("child 1"));
        childLO.setStartDate(new Date());
        childLO.setDegreeTitle(createI18Text("degree"));
        childLO.setApplicationOptions(Lists.newArrayList(aos));
        childLO.setFormOfEducation(Lists.newArrayList(createI18Text("FormOfEducation"), createI18Text("FormOfEducation2")));
        childLO.setFormOfTeaching(Lists.newArrayList(createI18Text("FormOfTeaching"), createI18Text("FormOfTeaching2")));
        childLO.setPrerequisite(createI18Text("Prerequisite"));
        childLO.setQualification(createI18Text("Qualification"));
        ParentLORef parent = new ParentLORef();
        parent.setId("1234");
        parent.setName(parentLO.getName());
        childLO.setParent(parent);
        childLO.setRelated(childLORefs);
        Code c = new Code();
        c.setValue("fi");
        c.setDescription(createI18Text("suomi"));
        childLO.setTeachingLanguages(Lists.newArrayList(c));
        Map<String, String> links = new HashMap<String, String>();
        links.put("link1", "link1");
        links.put("link2", "link2");
        childLO.setWebLinks(links);

        ModelMapper modelMapper = new ModelMapper();
        learningOpportunityService = new LearningOpportunityServiceImpl(educationDataQueryService, modelMapper);

        when(educationDataQueryService.getParentLearningOpportunity(eq("1234"))).thenReturn(parentLO);
        when(educationDataQueryService.getChildLearningOpportunity(eq("clo123"))).thenReturn(childLO);
    }

    @Test
    public void testGetParentLearningOpportunity() throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationDTO result = learningOpportunityService.getParentLearningOpportunity("1234");
        checkResult("fi", result);
    }

    @Test
    public void testGetParentLearningOpportunityEn() throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationDTO result = learningOpportunityService.getParentLearningOpportunity("1234", "en");
        checkResult("en", result);
    }

    @Test
    public void testGetChildLearningOpportunity() throws ResourceNotFoundException {
        ChildLearningOpportunityDTO result = learningOpportunityService.getChildLearningOpportunity("clo123");
        checkResult("fi", result);
    }

    @Test
    public void testGetChildLearningOpportunityEn() throws ResourceNotFoundException {
        ChildLearningOpportunityDTO result = learningOpportunityService.getChildLearningOpportunity("clo123", "en");
        checkResult("en", result);
    }

    private void checkResult(String lang, ParentLearningOpportunitySpecificationDTO result) {
        assertNotNull(result);
        assertEquals(parentLO.getId(), result.getId());
        assertEquals(parentLO.getName().getTranslations().get(lang), result.getName());
        assertEquals(parentLO.getAccessToFurtherStudies().getTranslations().get(lang), result.getAccessToFurtherStudies());
        assertEquals(parentLO.getEducationDegree(), result.getEducationDegree());
        assertEquals(parentLO.getGoals().getTranslations().get(lang), result.getGoals());
        assertEquals(parentLO.getEducationDomain().getTranslations().get(lang), result.getEducationDomain());
        assertEquals(parentLO.getStructureDiagram().getTranslations().get(lang), result.getStructureDiagram());
        assertEquals(parentLO.getStydyDomain().getTranslations().get(lang), result.getStydyDomain());
        assertEquals(parentLO.getProvider().getId(), result.getProvider().getId());
        assertEquals(parentLO.getProvider().getName().getTranslations().get(lang), result.getProvider().getName());
        assertEquals(parentLO.getApplicationOptions().iterator().next().getName().getTranslations().get(lang),
                result.getApplicationOptions().iterator().next().getName());
        assertEquals(3, result.getAvailableTranslationLanguages().size());
        assertEquals(lang, result.getTranslationLanguage());
    }

    private void checkResult(String lang, ChildLearningOpportunityDTO result) {
        assertNotNull(result);
        assertEquals(childLO.getId(), result.getId());
        assertEquals(childLO.getName().getTranslations().get(lang), result.getName());
        assertEquals(childLO.getApplicationOptions().get(0).getId(), result.getApplicationOptions().get(0).getId());
        assertEquals(childLO.getApplicationOptions().get(0).getName().getTranslations().get(lang), result.getApplicationOptions().get(0).getName());
        assertEquals(childLO.getDegreeTitle().getTranslations().get(lang), result.getDegreeTitle());
        assertEquals(childLO.getPrerequisite().getTranslations().get(lang), result.getPrerequisite());
        assertEquals(childLO.getQualification().getTranslations().get(lang), result.getQualification());
        assertEquals(childLO.getStartDate(), result.getStartDate());
        assertEquals(childLO.getParent().getName().getTranslations().get(lang), result.getParent().getName());
        assertEquals(childLO.getParent().getId(), result.getParent().getId());
        assertEquals(childLO.getFormOfEducation().get(0).getTranslations().get(lang), result.getFormOfEducation().get(0));
        assertEquals(childLO.getFormOfTeaching().get(0).getTranslations().get(lang), result.getFormOfTeaching().get(0));
        assertEquals(1, result.getTeachingLanguages().size());
        assertEquals(2, result.getWebLinks().size());
    }

    private I18nText createI18Text(String text) {
        Map<String, String> translations = new HashMap<String, String>();
        translations.put("fi", text + " fi");
        translations.put("sv", text + " sv");
        translations.put("en", text + " en");
        return new I18nText(translations);
    }

    private ChildLORef createChildLORef(I18nText name, String nameByTeachingLang, String asId, String loiId, String losId) {
        ChildLORef ref = new ChildLORef();
        ref.setName(name);
        ref.setNameByTeachingLang(nameByTeachingLang);
        ref.setAsId(asId);
        ref.setLoiId(loiId);
        ref.setLosId(losId);
        return ref;
    }

    private Provider createProvider(String id, I18nText name, Set<String> asIds) {
        Provider provider = new Provider();
        provider.setId(id);
        provider.setName(name);
        provider.setApplicationSystemIDs(asIds);
        return provider;
    }

    private ApplicationOption createApplicationOption(String id, I18nText name, String asId, Provider provider, Date attDeadline,
                                                      int lastYearApplicantCount, double lowestAcceptedAverage,
                                                      int lowestAcceptedScore, int startingQuota, List<ChildLORef> childLORefs,
                                                      String educationDegree) {
        ApplicationOption ao = new ApplicationOption();
        ao.setId(id);
        ao.setName(name);
        ApplicationSystem as = new ApplicationSystem();
        as.setId(asId);
        ao.setApplicationSystem(as);
        ao.setProvider(provider);
        ao.setAttachmentDeliveryDeadline(attDeadline);
        ao.setLastYearApplicantCount(lastYearApplicantCount);
        ao.setLowestAcceptedAverage(lowestAcceptedAverage);
        ao.setLowestAcceptedScore(lowestAcceptedScore);
        ao.setStartingQuota(startingQuota);
        ao.setChildLORefs(childLORefs);
        ao.setEducationDegree(educationDegree);
        return ao;
    }
}
