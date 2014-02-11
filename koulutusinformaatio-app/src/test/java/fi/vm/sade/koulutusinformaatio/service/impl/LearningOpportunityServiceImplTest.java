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
import com.google.common.collect.Sets;
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
    private ParentLOS parentLOS;
    private ParentLOI parentLOI;
    private ChildLOS childLOS;
    private ChildLOI childLOI;
    private ApplicationOption applicationOption;

    @Before
    public void setUp() throws ResourceNotFoundException {
        educationDataQueryService = mock(EducationDataQueryService.class);

        Code prerequisite = new Code("PK", createI18Text("Peruskoulu"), createI18Text("Peruskoulukoodin kuvaus"));
        parentLOS = new ParentLOS();
        parentLOS.setId("1234");
        parentLOS.setAccessToFurtherStudies(createI18Text("AccessToFurtherStudies"));
        parentLOS.setEducationDegree("32");
        parentLOS.setName(createI18Text("name"));
        parentLOS.setGoals(createI18Text("goals"));
        parentLOS.setStructure(createI18Text("StructureDiagram"));
        parentLOS.setEducationDomain(createI18Text("EducationDomain"));
        parentLOS.setStydyDomain(createI18Text("StudyDomain"));
        List<ChildLOIRef> childLOIRefs = new ArrayList<ChildLOIRef>();
        childLOIRefs.add(createChildLOIRef(createI18Text("c1"), "c1 fi", "as123", "lo123", prerequisite));
        childLOIRefs.add(createChildLOIRef(createI18Text("c2"), "c2 fi", "as123", "lo124", prerequisite));
        childLOIRefs.add(createChildLOIRef(createI18Text("c3"), "c3 fi", "as124", "lo125", prerequisite));

        Set<String> asIds = new HashSet<String>();
        asIds.add("as123");
        asIds.add("as124");
        parentLOS.setProvider(createProvider("p1234", createI18Text("provider1"), asIds));
        applicationOption = createApplicationOption("ao123", createI18Text("ao name"), "as123",
                parentLOS.getProvider(), new Date(), 100, 25, 6, 77, childLOIRefs, "32",
                prerequisite);
        Set<ApplicationOption> aos = Sets.newHashSet(applicationOption);

        parentLOI = new ParentLOI();
        parentLOI.setId("2345");
        parentLOI.setPrerequisite(prerequisite);
        parentLOI.setSelectingDegreeProgram(createI18Text("Valintaperustekuvaus"));
        parentLOI.setApplicationOptions(aos);
        parentLOI.setChildRefs(childLOIRefs);
        parentLOS.setLois(Lists.newArrayList(parentLOI));

        childLOS = new ChildLOS();
        childLOS.setId("lo123");
        childLOS.setName(createI18Text("child 1"));
        childLOS.setDegreeTitle(createI18Text("degree"));
        childLOS.setQualification(createI18Text("Qualification"));
        ParentLOSRef parent = new ParentLOSRef();
        parent.setId("1234");
        parent.setName(parentLOS.getName());
        childLOS.setParent(parent);

        Code c = new Code();
        c.setValue("fi");
        c.setDescription(createI18Text("suomi"));

        Map<String, String> links = new HashMap<String, String>();
        links.put("link1", "link1");
        links.put("link2", "link2");

        childLOI = new ChildLOI();
        childLOI.setStartDate(new Date());
        childLOI.setApplicationOptions(Lists.newArrayList(aos));
        childLOI.setFormOfEducation(Lists.newArrayList(createI18Text("FormOfEducation"), createI18Text("FormOfEducation2")));
        childLOI.setFormOfTeaching(Lists.newArrayList(createI18Text("FormOfTeaching"), createI18Text("FormOfTeaching2")));
        childLOI.setPrerequisite(new Code("PK", createI18Text("Prerequisite"), null));
        childLOI.setTeachingLanguages(Lists.newArrayList(c));
        childLOI.setWebLinks(links);
        childLOI.setRelated(childLOIRefs);
        childLOI.setPrerequisite(prerequisite);
        childLOS.setLois(Lists.newArrayList(childLOI));

        ModelMapper modelMapper = new ModelMapper();

        when(educationDataQueryService.getParentLearningOpportunity(eq("1234"))).thenReturn(parentLOS);
        when(educationDataQueryService.getChildLearningOpportunity(eq("clo123"))).thenReturn(childLOS);
        when(educationDataQueryService.getApplicationOption(eq("ao123"))).thenReturn(applicationOption);

        learningOpportunityService = new LearningOpportunityServiceImpl(educationDataQueryService, modelMapper);
    }

    @Test
    public void testGetParentLearningOpportunity() throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationDTO result = learningOpportunityService.getParentLearningOpportunity("1234");
        checkResult("fi", "fi", result);
    }

    @Test
    public void testGetParentLearningOpportunityEn() throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationDTO result = learningOpportunityService.getParentLearningOpportunity("1234", "en", "en");
        checkResult("en", "fi", result);
    }

    @Test
    public void testGetChildLearningOpportunity() throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationDTO result = learningOpportunityService.getChildLearningOpportunity("clo123");
        checkResult("fi", "fi", result);
    }

    @Test
    public void testGetChildLearningOpportunityEn() throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationDTO result = learningOpportunityService.getChildLearningOpportunity("clo123","en", "en");
        checkResult("en", "fi", result);
    }

    @Test
    public void testGetApplicationOption() throws ResourceNotFoundException {
        ApplicationOptionDTO result = learningOpportunityService.getApplicationOption(applicationOption.getId(), "fi", "fi");
        checkResult("fi", "fi", result);
    }

    @Test
    public void testGetApplicationOptionEn() throws ResourceNotFoundException {
        ApplicationOptionDTO result = learningOpportunityService.getApplicationOption(applicationOption.getId(), "en", "en");
        checkResult("en", "fi", result);
    }


    private void checkResult(String lang, String defaultLang, ApplicationOptionDTO result) {
        assertNotNull(result);
        assertEquals(applicationOption.getId(), result.getId());
        assertEquals(applicationOption.getAoIdentifier(), result.getAoIdentifier());
        assertEquals(applicationOption.getName().getTranslations().get(defaultLang), result.getName());
        assertEquals(applicationOption.getEducationDegree(), result.getEducationDegree());
        assertEquals(applicationOption.getProvider().getId(), result.getProvider().getId());
        assertEquals(applicationOption.getAttachmentDeliveryDeadline(), result.getAttachmentDeliveryDeadline());
        assertEquals(applicationOption.getPrerequisite().getValue(), result.getPrerequisite().getValue());
        assertEquals(applicationOption.getLastYearApplicantCount(), result.getLastYearApplicantCount());
        assertEquals(applicationOption.getLowestAcceptedAverage(), result.getLowestAcceptedAverage());
        assertEquals(applicationOption.getLowestAcceptedScore(), result.getLowestAcceptedScore());
        assertEquals(applicationOption.getStartingQuota(), result.getStartingQuota());
        assertEquals(applicationOption.getChildLOIRefs().size(), result.getChildRefs().size());
    }

    private void checkResult(String lang, String defaultLang, ParentLearningOpportunitySpecificationDTO result) {
        assertNotNull(result);
        assertEquals(parentLOS.getId(), result.getId());
        assertEquals(parentLOS.getName().getTranslations().get(defaultLang), result.getName());
        assertEquals(parentLOS.getAccessToFurtherStudies().getTranslations().get(lang), result.getAccessToFurtherStudies());
        assertEquals(parentLOS.getEducationDegree(), result.getEducationDegree());
        assertEquals(parentLOS.getGoals().getTranslations().get(lang), result.getGoals());
        assertEquals(parentLOS.getEducationDomain().getTranslations().get(defaultLang), result.getEducationDomain());
        assertEquals(parentLOS.getStructure().getTranslations().get(lang), result.getStructure());
        assertEquals(parentLOS.getStydyDomain().getTranslations().get(lang), result.getStydyDomain());
        assertEquals(parentLOS.getProvider().getId(), result.getProvider().getId());
        assertEquals(parentLOS.getProvider().getName().getTranslations().get(defaultLang), result.getProvider().getName());

        assertNotNull(result.getLois());
        assertEquals(1, result.getLois().size());
        ParentLearningOpportunityInstanceDTO loi = result.getLois().get(0);
        assertEquals(parentLOI.getApplicationOptions().iterator().next().getId(),
                loi.getApplicationSystems().iterator().next().getApplicationOptions().get(0).getId());
        assertEquals(parentLOI.getPrerequisite().getValue(), loi.getPrerequisite().getValue());
        assertEquals(parentLOI.getApplicationOptions().iterator().next().getApplicationSystem().getName().getTranslations().get(defaultLang),
                loi.getApplicationSystems().iterator().next().getName());
        assertEquals(parentLOI.getApplicationOptions().iterator().next().getName().getTranslations().get(defaultLang),
                loi.getApplicationSystems().iterator().next().getApplicationOptions().get(0).getName());

        assertEquals(lang, result.getTranslationLanguage());
    }

    private void checkResult(String lang, String defaultLang, ChildLearningOpportunitySpecificationDTO result) {
        assertNotNull(result);
        assertEquals(childLOS.getId(), result.getId());
        assertEquals(childLOS.getName().getTranslations().get(defaultLang), result.getName());

        assertNotNull(result.getLois());
        assertEquals(1, result.getLois().size());


        assertEquals(childLOS.getDegreeTitle().getTranslations().get(defaultLang), result.getDegreeTitle());
        assertEquals(childLOS.getQualification().getTranslations().get(defaultLang), result.getQualification());
        assertEquals(childLOS.getParent().getName().getTranslations().get(defaultLang), result.getParent().getName());
        assertEquals(childLOS.getParent().getId(), result.getParent().getId());

        assertNotNull(result.getLois());
        assertEquals(1, result.getLois().size());
        ChildLearningOpportunityInstanceDTO loi = result.getLois().get(0);
        assertEquals(childLOI.getApplicationOptions().get(0).getId(),
                loi.getApplicationSystems().iterator().next().getApplicationOptions().iterator().next().getId());
        assertEquals(childLOI.getApplicationOptions().get(0).getName().getTranslations().get(defaultLang),
                loi.getApplicationSystems().iterator().next().getApplicationOptions().iterator().next().getName());
        assertEquals(childLOI.getPrerequisite().getValue(),
                loi.getPrerequisite().getValue());
        assertEquals(childLOI.getPrerequisite().getDescription().getTranslations().get(lang),
                loi.getPrerequisite().getDescription());

        assertEquals(childLOI.getStartDate(), loi.getStartDate());
        assertEquals(childLOI.getFormOfEducation().get(0).getTranslations().get(lang), loi.getFormOfEducation().get(0));
        assertEquals(childLOI.getFormOfTeaching().get(0).getTranslations().get(lang), loi.getFormOfTeaching().get(0));
        assertEquals(1, loi.getTeachingLanguages().size());
        assertEquals(2, loi.getWebLinks().size());
    }

    private I18nText createI18Text(String text) {
        Map<String, String> translations = new HashMap<String, String>();
        translations.put("fi", text + " fi");
        translations.put("sv", text + " sv");
        translations.put("en", text + " en");
        return new I18nText(translations);
    }

    private ChildLOIRef createChildLOIRef(I18nText name, String nameByTeachingLang, String asId, String losId, Code prerequisite) {
        ChildLOIRef ref = new ChildLOIRef();
        ref.setName(name);
        ref.setNameByTeachingLang(nameByTeachingLang);
        ref.setAsIds(Lists.newArrayList(asId));
        ref.setLosId(losId);
        ref.setPrerequisite(prerequisite);
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
                                                      int lowestAcceptedScore, int startingQuota, List<ChildLOIRef> childLOIRefs,
                                                      String educationDegree, Code prerequisite) {
        ApplicationOption ao = new ApplicationOption();
        ao.setId(id);
        ao.setName(name);
        ApplicationSystem as = new ApplicationSystem();
        as.setId(asId);
        as.setName(createI18Text("Haun nimi"));
        ao.setApplicationSystem(as);
        ao.setProvider(provider);
        ao.setAttachmentDeliveryDeadline(attDeadline);
        ao.setLastYearApplicantCount(lastYearApplicantCount);
        ao.setLowestAcceptedAverage(lowestAcceptedAverage);
        ao.setLowestAcceptedScore(lowestAcceptedScore);
        ao.setStartingQuota(startingQuota);
        ao.setChildLOIRefs(childLOIRefs);
        ao.setEducationDegree(educationDegree);
        ao.setPrerequisite(prerequisite);
        Exam exam = new Exam();
        exam.setDescription(createI18Text("exam description"));
        exam.setType(createI18Text("exam type"));
        ExamEvent event = new ExamEvent();
        event.setDescription("event description");
        event.setStart(new Date());
        event.setEnd(new Date());
        Address address = new Address();
        address.setPostalCode("00100");
        address.setPostOffice("Helsinki");
        address.setStreetAddress("street address");
        event.setAddress(address);
        exam.setExamEvents(Lists.newArrayList(event));
        ao.setExams(Lists.newArrayList(exam));
        return ao;
    }
}
