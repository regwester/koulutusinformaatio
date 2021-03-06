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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLOIRefDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ChildLOIRefToDTOTest {

    ChildLOIRef ref;
    @Before
    public void init() {
        ref = new ChildLOIRef();
        ref.setId("refid");
        ref.setLosId("losid");
        ref.setPrerequisite(new Code());
        Map<String, String> qualificationTranslations = Maps.newHashMap();
        qualificationTranslations.put("fi", "qualificationFi");
        ref.setQualification(new I18nText(qualificationTranslations));
        ref.setNameByTeachingLang("name by teaching lang");
        Map<String, String> nameTranslations = Maps.newHashMap();
        nameTranslations.put("fi", "ref name");
        ref.setName(new I18nText(nameTranslations));

        List<I18nText> qualifications = new ArrayList<I18nText>();
        I18nText qualification = new I18nText();
        qualification.put("fi", "Kalanviljelijä");
        qualifications.add(qualification);
        ref.setQualifications(qualifications);
    }

    @Test
    public void testConvert() {
        ChildLOIRefDTO dto = ChildLOIRefToDTO.convert(ref, "fi");
        assertNotNull(dto);
        assertEquals("refid", dto.getId());
        assertEquals("losid", dto.getLosId());
        assertNotNull(dto.getPrerequisite());
        assertEquals("qualificationFi", dto.getQualification());
        assertEquals("name by teaching lang", dto.getName());
        assertTrue(dto.getQualifications().contains("Kalanviljelijä"));
    }

    @Test
    public void testConvertWithNaturalName() {
        ref.setNameByTeachingLang(null);
        ChildLOIRefDTO dto = ChildLOIRefToDTO.convert(ref, "fi");
        assertEquals("ref name", dto.getName());
    }

    @Test
    public void testConvertMultiple() {
        List<ChildLOIRef> refs = Lists.newArrayList(ref);
        List<ChildLOIRefDTO> dtos = ChildLOIRefToDTO.convert(refs, "fi");
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    public void testConvertToI18n() {
        List<ChildLOIRef> refs = Lists.newArrayList(ref);
        List<I18nText> i18nList = ChildLOIRefToDTO.convert(refs);
        assertNotNull(i18nList);
        assertEquals(1, i18nList.size());
        assertEquals("ref name", i18nList.get(0).getTranslations().get("fi"));
    }

    @Test
    public void testConvertToI18nWithNullList() {
        assertNull(ChildLOIRefToDTO.convert(null));
    }
}
