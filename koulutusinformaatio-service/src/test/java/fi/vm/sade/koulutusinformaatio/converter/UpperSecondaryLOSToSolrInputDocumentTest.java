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
package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author Markus
 *
 */
public class UpperSecondaryLOSToSolrInputDocumentTest {

	private UpperSecondaryLOS los;
	private UpperSecondaryLOSToSolrInputDocument converter;
	private Code prerequisite;
	private Code lang;
	private Provider provider;
	private ApplicationOption ao;
	
	@Before
	public void setUp() {
		los = new UpperSecondaryLOS();
		
		los.setType(TarjontaConstants.TYPE_UPSEC);
		los.setId("upsecId");
		los.setName(TestUtil.createI18nText("Upsec name fi", "Upsec name sv", "Upsec name en"));
        los.setShortTitle(TestUtil.createI18nText("Upsec short name fi", "Upsec short name sv", "Upsec short name en"));

		los.setCreditValue("80");
		los.setCreditUnit(TestUtil.createI18nText("ov fi", "ov sv", "ov en"));

		provider = new Provider();
		provider.setId("provId");
		provider.setName(TestUtil.createI18nText("prov fi", "prov sv", "prov en"));
		provider.setHomeDistrict(TestUtil.createI18nText("Uusimaa fi", "Uusimaa sv", "Uusimaa en"));
		provider.setHomePlace(TestUtil.createI18nText("Hki fi", "Hki sv", "Hki en"));
		Address addr = new Address();
		addr.setPostalCode("04620");
		provider.setVisitingAddress(addr);
		provider.setDescription(TestUtil.createI18nText("prov descr fi", "prov descr sv", "prov descr en"));
		los.setProvider(provider);
		
		UpperSecondaryLOI upsecLoi = new UpperSecondaryLOI();
		upsecLoi.setId("upsecLoiId");
		upsecLoi.setStartDate(new Date());
		upsecLoi.setKaksoistutkinto(false);
		
		prerequisite = new Code();
		prerequisite.setName(TestUtil.createI18nText("Peruskoulu", "Peruskoulu sv", "Peruskoulu en"));
		prerequisite.setValue("pk");
		prerequisite.setUri("pk_uri");
		upsecLoi.setPrerequisite(prerequisite);
		
		lang = new Code();
		lang.setName(TestUtil.createI18nText("Suomi", "Finska", "Finnish"));
		lang.setValue("FI");
		lang.setUri("fi_uri");
		upsecLoi.setTeachingLanguages(Arrays.asList(lang));
		upsecLoi.setContent(TestUtil.createI18nText("Content fi", "Content sv", "Content en"));
		
		ApplicationSystem as = new ApplicationSystem();
		as.setName(TestUtil.createI18nText("Haku fi", "Haku sv", "Haku en"));
		Date asStart = new Date();
		Date asEnd = new Date();
		DateRange dr = new DateRange();
		dr.setStartDate(asStart);
		dr.setEndDate(asEnd);
		as.setApplicationDates(Arrays.asList(dr));
		ao = new ApplicationOption();
		ao.setSpecificApplicationDates(false);
		ao.setApplicationSystem(as);
		upsecLoi.setApplicationOptions(Arrays.asList(ao));
		List<UpperSecondaryLOI> loiList = new ArrayList<UpperSecondaryLOI>();
		loiList.add(upsecLoi);
		los.setLois(loiList);
		
		los.setQualification(TestUtil.createI18nText("quali fi", "quali sv", "quali en"));
		los.setGoals(TestUtil.createI18nText("Goals child fi", "Goals child sv", "Goals child en"));
		
		List<Code> topics = new ArrayList<Code>();
		Code topic1 = new Code();
		topic1.setName(TestUtil.createI18nText("topiikii 1 fi", "topiikki 1 sv", "topiikki 1 en"));
		topic1.setValue("top1");
		topic1.setUri("top1_uri");
		topics.add(topic1);
		
		Code topic2 = new Code();
		topic2.setName(TestUtil.createI18nText("topiikii 2 fi", "topiikki 2 sv", "topiikki 2 en"));
		topic2.setValue("top2");
		topic2.setUri("top2_uri");
		topics.add(topic2);
		los.setTopics(topics);
		
		List<Code> themes = new ArrayList<Code>();
		Code theme1 = new Code();
		theme1.setName(TestUtil.createI18nText("theme 1 fi", "theme 1 sv", "theme 1 en"));
		theme1.setValue("theme1");
		theme1.setUri("theme1_uri");
		themes.add(theme1);
		
		Code theme2 = new Code();
		theme2.setName(TestUtil.createI18nText("theme 2 fi", "theme 2 sv", "theme 2 en"));
		theme2.setValue("theme2");
		theme2.setUri("theme2_uri");
		themes.add(theme2);
		los.setThemes(themes);
		
		converter = new UpperSecondaryLOSToSolrInputDocument();
		
	}
	
	@Test
	public void testConvert() {
		List<SolrInputDocument> docs = converter.convert(los);
		assertEquals(7, docs.size());
		SolrInputDocument doc = docs.get(0);
		assertEquals(los.getLois().get(0).getId(), doc.get(LearningOpportunity.ID).getValue().toString());
		assertEquals(los.getId(), doc.get(LearningOpportunity.LOS_ID).getValue().toString());
		assertEquals(prerequisite.getValue(), doc.get(LearningOpportunity.PREREQUISITES).getValues().iterator().next().toString());
		assertEquals(los.getCreditValue() + " " + los.getCreditUnit().getTranslations().get("fi"), doc.get(LearningOpportunity.CREDITS).getValue().toString());
        assertEquals(provider.getName().getTranslations().get("fi"), doc.get(LearningOpportunity.LOP_NAME).getValue().toString());
        //assertEquals(SolrConstants.ED_TYPE_LUKIO, doc.get(LearningOpportunity.EDUCATION_TYPE).getValue().toString());
	}
	
}