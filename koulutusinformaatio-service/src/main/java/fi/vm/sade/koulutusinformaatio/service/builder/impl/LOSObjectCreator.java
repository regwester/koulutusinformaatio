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

package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.AdultVocationalLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.ContactPerson;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LanguageSelection;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OppiaineV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiValikoimaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.Koulutus2AsteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusGenericV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NayttotutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.TutkintoonJohtamatonKoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 * @author Hannu Lyytikainen
 */
public class LOSObjectCreator extends ObjectCreator {

    private static final Logger LOG = LoggerFactory.getLogger(LOSObjectCreator.class);

    private static final String UNDEFINED = "undefined";

    private KoodistoService koodistoService;
    private ProviderService providerService;
    private TarjontaRawService tarjontaRawService;
    private ApplicationOptionCreator applicationOptionCreator;

    public LOSObjectCreator(KoodistoService koodistoService, TarjontaRawService tarjontaRawService,
            ProviderService providerService, OrganisaatioRawService organisaatioRawService, ParameterService parameterService, List<String> overriddenASOids) {
        super(koodistoService);
        applicationOptionCreator = new ApplicationOptionCreator(koodistoService, organisaatioRawService, parameterService, overriddenASOids);
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.tarjontaRawService = tarjontaRawService;
    }

    public HigherEducationLOS createHigherEducationLOS(KoulutusKorkeakouluV1RDTO koulutus, boolean checkStatus)
            throws TarjontaParseException, KoodistoException, ResourceNotFoundException {

        HigherEducationLOS los = new HigherEducationLOS();

        los.setType(TarjontaConstants.TYPE_KK);
        los.setId(koulutus.getOid());
        los.setKomoOid(koulutus.getKomoOid());

        // Set<Code> availableLanguagaes = Sets.newHashSet();
        Map<String, Code> availableLanguagesMap = new HashMap<String, Code>();
        List<Code> rawTranslCodes = new ArrayList<Code>();
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA).getTekstis().containsKey(UNDEFINED)) {
            los.setInfoAboutTeachingLangs(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA))));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
            los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA).getTekstis().containsKey(UNDEFINED)) {
            los.setMajorSelection(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA))));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET).getTekstis().containsKey(UNDEFINED)) {
            los.setFinalExam(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstis().containsKey(UNDEFINED)) {
            los.setCareerOpportunities(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN))));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS).getTekstis().containsKey(UNDEFINED)) {
            los.setCompetence(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey(UNDEFINED)) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setResearchFocus(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET))));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET))));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS).getTekstis().containsKey(UNDEFINED)) {
            los.setInfoAboutCharge(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS))));
        }

        los.setTeachingLanguages(createCodes(koulutus.getOpetuskielis()));// koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));

        // fields used to resolve available translation languages
        // content, internationalization, cooperation
        for (Code curCode : rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }

        /*
         * for (Code teachingLanguage : los.getTeachingLanguages()) { availableLanguagesMap.put(teachingLanguage.getUri(), teachingLanguage); }
         */

        los.setAvailableTranslationLanguages(new ArrayList<Code>(availableLanguagesMap.values()));

        if (koulutus.getYhteyshenkilos() != null) {
            for (YhteyshenkiloTyyppi yhteyshenkiloRDTO : koulutus.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getSahkoposti(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                if (yhteyshenkiloRDTO.getHenkiloTyyppi() != null) {
                    contactPerson.setType(yhteyshenkiloRDTO.getHenkiloTyyppi().name());
                }
                los.getContactPersons().add(contactPerson);
            }
        }

        los.setEducationDomain(getI18nTextEnriched(koulutus.getKoulutusala().getMeta()));
        los.setName(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        LOG.debug("Koulutusohjelma for " + koulutus.getOid() + ": " + koulutus.getKoulutusohjelma());
        los.setShortTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        LOG.debug("Short title: {}", los.getShortTitle());
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koodistoService.searchFirst(koulutus.getKoulutuskoodi().getUri()));
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());
        los.setEducationType(getEducationType(koulutus.getKoulutusaste().getUri()));
        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getTutkinto().getMeta())); // muutos: oli koulutus.getKoulutusohjelma()
        los.setDegreeTitles(getI18nTextMultiple(koulutus.getTutkintonimikes())); // uusi
        los.setQualifications(getQualifications(koulutus));
        los.setDegree(getI18nTextEnriched(koulutus.getTutkinto().getMeta()));
        if (koulutus.getKoulutuksenAlkamisPvms() != null && !koulutus.getKoulutuksenAlkamisPvms().isEmpty()) {
            los.setStartDate(koulutus.getKoulutuksenAlkamisPvms().iterator().next());
        }
        if (koulutus.getKoulutuksenAlkamisvuosi() != null) {
            los.setStartYear(koulutus.getKoulutuksenAlkamisvuosi());
        }
        if (koulutus.getKoulutuksenAlkamiskausi() != null) {
            los.setStartSeason(getI18nTextEnriched(koulutus.getKoulutuksenAlkamiskausi().getMeta()));
        }

        los.setPlannedDuration(koulutus.getSuunniteltuKestoArvo());
        los.setPlannedDurationUnit(getI18nTextEnriched(koulutus.getSuunniteltuKestoTyyppi().getMeta()));
        los.setPduCodeUri(koulutus.getSuunniteltuKestoTyyppi().getUri());
        los.setCreditValue(koulutus.getOpintojenLaajuusarvo().getArvo());
        los.setCreditUnit(getI18nTextEnriched(koulutus.getOpintojenLaajuusyksikko().getMeta()));
        los.setChargeable(koulutus.getOpintojenMaksullisuus());

        try {
            Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
            los.setProvider(provider);

            for (String curTarjoaja : koulutus.getOpetusTarjoajat()) {
                if (!curTarjoaja.equals(provider.getId())) {
                    los.getAdditionalProviders().add(providerService.getByOID(curTarjoaja));
                }
            }

            /*
             * if (koulutus.getOpetusTarjoajat() != null) {
             * 
             * }
             */

        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }

        los.setTopics(createCodes(koulutus.getAihees()));
        los.setThemes(getThemes(los));

        los.setFormOfTeaching(getI18nTextMultiple(koulutus.getOpetusmuodos()));
        los.setFotFacet(this.createCodes(koulutus.getOpetusPaikkas()));
        los.setTimeOfTeachingFacet(this.createCodes(koulutus.getOpetusAikas()));
        los.setFormOfStudyFacet(this.createCodes(koulutus.getOpetusmuodos()));

        los.setProfessionalTitles(getI18nTextMultiple(koulutus.getAmmattinimikkeet()));

        los.setTeachingTimes(getI18nTextMultiple(koulutus.getOpetusAikas()));
        los.setTeachingPlaces(getI18nTextMultiple(koulutus.getOpetusPaikkas()));

        boolean existsValidHakukohde = fetchAndCreateHakukohdeData(los, checkStatus);

        // If we are not fetching for preview, an exception is thrown if no valid application options exist
        if (checkStatus && !existsValidHakukohde) {
            throw new TarjontaParseException("No valid application options for education: " + los.getId());
        }
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption ao : los.getApplicationOptions()) {
                ao.setProvider(los.getProvider());
                ao.setEducationDegree(los.getEducationDegree());
                los.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
                los.getPrerequisites().addAll(koodistoService.searchMultiple(ao.getRequiredBaseEducations()));
                ao.setParent(createParentLosRef(los));
                ao.setType(TarjontaConstants.TYPE_KK);
            }
        }

        los.setFacetPrerequisites(getFacetPrequisites(los.getPrerequisites()));
        los.setSubjects(getSubjects(koulutus.getOppiaineet()));

        return los;

    }

    private HashMap<String, List<String>> getSubjects(Set<OppiaineV1RDTO> oppiaines) {
        List<OppiaineV1RDTO> asList = new LinkedList<OppiaineV1RDTO>();
        asList.addAll(oppiaines);
        HashMap<String, List<String>> subjects = new HashMap<String, List<String>>();
        subjects.put("fi", getSubjectsByLang(asList, "kieli_fi"));
        subjects.put("sv", getSubjectsByLang(asList, "kieli_sv"));
        subjects.put("en", getSubjectsByLang(asList, "kieli_en"));
        return subjects;
    }

    private ArrayList<String> getSubjectsByLang(List<OppiaineV1RDTO> list, String lang) {
        ArrayList<String> subjects = new ArrayList<String>();
        for (OppiaineV1RDTO subject : list) {
            if (lang.equals(subject.getKieliKoodi())) {
                subjects.add(subject.getOppiaine());
            }
        }
        return subjects;
    }

    public AdultUpperSecondaryLOS createAdultUpperSeconcaryLOS(KoulutusLukioV1RDTO koulutus, boolean checkStatus)
            throws TarjontaParseException, KoodistoException {

        AdultUpperSecondaryLOS los = new AdultUpperSecondaryLOS();

        los.setType(TarjontaConstants.TYPE_ADULT_UPSEC);
        los.setEducationType(SolrConstants.ED_TYPE_AIKUISLUKIO);
        addKoulutusV1Fields(koulutus, los, checkStatus, TarjontaConstants.TYPE_ADULT_UPSEC, true);
        addKoulutusGenericV1Fields(koulutus, los);
        addKoulutus2AsteV1Fields(koulutus, los);

        if (koulutus.getLukiodiplomit() != null) {
            los.setDiplomas(getI18nTextMultiple(koulutus.getLukiodiplomit()));
        }

        return los;
    }

    public AdultUpperSecondaryLOS createAdultBaseEducationLOS(Koulutus2AsteV1RDTO koulutus, boolean checkStatus) throws KoodistoException,
            TarjontaParseException {
        AdultUpperSecondaryLOS los = new AdultUpperSecondaryLOS();

        los.setType(TarjontaConstants.TYPE_ADULT_BASE);
        los.setEducationType(SolrConstants.ED_TYPE_AIKUISTEN_PERUSOPETUS);
        addKoulutusV1Fields(koulutus, los, checkStatus, TarjontaConstants.TYPE_ADULT_UPSEC, true);
        addKoulutusGenericV1Fields(koulutus, los);
        addKoulutus2AsteV1Fields(koulutus, los);

        return los;
    }

    private void addKoulutus2AsteV1Fields(Koulutus2AsteV1RDTO koulutus, KoulutusLOS los) throws KoodistoException {
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT).getTekstis().containsKey("UNDEFINED")) {
            los.setSubjectsAndCourses(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT)));
        }

        if (koulutus.getKielivalikoima() != null) {

            List<LanguageSelection> languageSelection = Lists.newArrayList();
            KoodiValikoimaV1RDTO kielivalikoima = koulutus.getKielivalikoima();

            for (Map.Entry<String, KoodiUrisV1RDTO> oppiaine : kielivalikoima.entrySet()) {
                List<I18nText> languages = getI18nTextMultiple(oppiaine.getValue());
                languageSelection.add(new LanguageSelection(oppiaine.getKey(), languages));
            }
            los.setLanguageSelection(languageSelection);
        }

        if (koulutus.getTutkintonimike() != null && koulutus.getTutkintonimike().getMeta() != null) {
            los.setDegreeTitle(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta()));
            los.setQualifications(Arrays.asList(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta())));
        }
    }

    private String getEducationType(String uri) {
        if (uri.contains(TarjontaConstants.ED_DEGREE_URI_AMK)) {
            return SolrConstants.ED_TYPE_AMK;
        } else if (uri.contains(TarjontaConstants.ED_DEGREE_URI_YLEMPI_AMK)) {
            return SolrConstants.ED_TYPE_YLEMPI_AMK;
        } else if (uri.contains(TarjontaConstants.ED_DEGREE_URI_KANDI)) {
            return SolrConstants.ED_TYPE_KANDIDAATTI;
        } else if (uri.contains(TarjontaConstants.ED_DEGREE_URI_MAISTERI)) {
            return SolrConstants.ED_TYPE_MAISTERI;
        }
        return null;
    }

    // tutkintonimike
    private List<I18nText> getQualifications(KoulutusKorkeakouluV1RDTO koulutus) throws KoodistoException {

        List<I18nText> qualifications = new ArrayList<I18nText>();

        KoodiV1RDTO kandKoul = koulutus.getKandidaatinKoulutuskoodi();

        List<Code> kandQuals = new ArrayList<Code>();

        if (kandKoul != null
                && kandKoul.getUri() != null
                && kandKoul.getArvo() != null
                && !kandKoul.getArvo().equals(TarjontaConstants.KANDI_TUNTEMATON)) {

            kandQuals = this.koodistoService.searchSubCodes(kandKoul.getUri(), TarjontaConstants.TUTKINTONIMIKE_KK_KOODISTO_URI);
        }

        if (!kandQuals.isEmpty() && kandQuals.get(0).getName() != null) {
            qualifications.add(kandQuals.get(0).getName());
        }

        qualifications.addAll(getI18nTextMultiple(koulutus.getTutkintonimikes()));

        return qualifications;
    }

    // tutkintonimike
    private List<I18nText> getQualificationsForAikuAmm(NayttotutkintoV1RDTO koulutus) throws KoodistoException {

        List<I18nText> qualifications = new ArrayList<I18nText>();

        String osaamisalalUri = koulutus.getKoulutusohjelma().getUri();// getKandidaatinKoulutuskoodi();

        List<Code> quals = new ArrayList<Code>();

        if (osaamisalalUri != null) {

            quals = this.koodistoService.searchSubCodes(osaamisalalUri, TarjontaConstants.TUTKINTONIMIKEET_KOODISTO_URI);
        }

        if (quals != null && !quals.isEmpty()) {
            for (Code curQual : quals) {
                qualifications.add(curQual.getName());
            }
        } else if (koulutus.getTutkintonimike() != null && koulutus.getTutkintonimike().getMeta() != null) {
            qualifications.add(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta()));
        }
        return qualifications;
    }

    private <S extends KoulutusLOS> ParentLOSRef createParentLosRef(S los) {
        ParentLOSRef educationRef = new ParentLOSRef();
        educationRef.setId(los.getId());
        educationRef.setName(los.getName());
        educationRef.setLosType(TarjontaConstants.TYPE_KK);
        return educationRef;
    }

    private Map<String, ApplicationOption> cachedApplicationOptionResults = Maps.newHashMap();
    private Set<String> invalidOids = Sets.newHashSet();

    private boolean fetchAndCreateHakukohdeData(KoulutusLOS los, boolean checkStatus) throws KoodistoException {

        ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> result = tarjontaRawService.findHakukohdesByEducationOid(los.getId(), checkStatus);
        if (result == null
                || result.getResult() == null
                || result.getResult().getTulokset() == null
                || result.getResult().getTulokset().isEmpty())
        {
            return false;
        }
        List<TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO>> hakukohdeTarjoajat = result.getResult().getTulokset();

        List<ApplicationOption> aos = Lists.newArrayList();

        for (TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO> curProvider : hakukohdeTarjoajat) {
            for (HakukohdeHakutulosV1RDTO curHakukoh : curProvider.getTulokset()) {
                String aoId = curHakukoh.getOid();

                ApplicationOption cachedAo = cachedApplicationOptionResults.get(aoId);
                if (cachedAo != null) {
                    aos.add(cachedAo);
                    continue;
                }
                // If Haku or Hakukohde oid is already indexed as invalid, skip this AO.
                if (invalidOids.contains(curHakukoh.getHakuOid()) || invalidOids.contains(aoId)) {
                    continue;
                }

                ResultV1RDTO<HakuV1RDTO> hakuRes = tarjontaRawService.getV1EducationHakuByOid(curHakukoh.getHakuOid());
                HakuV1RDTO hakuDTO = hakuRes.getResult();

                if (checkStatus && (hakuDTO == null || hakuDTO.getTila() == null || !hakuDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString()))) {
                    invalidOids.add(hakuDTO.getOid());
                    continue;
                }

                ResultV1RDTO<HakukohdeV1RDTO> hakukohdeRes = tarjontaRawService.getV1EducationHakukohde(aoId);
                HakukohdeV1RDTO hakukohdeDTO = hakukohdeRes.getResult();

                if (checkStatus
                        && (hakukohdeDTO == null || hakukohdeDTO.getTila() == null || !hakukohdeDTO.getTila().toString()
                                .equals(TarjontaTila.JULKAISTU.toString()))) {
                    invalidOids.add(aoId);
                    continue;
                }

                try {
                    ApplicationOption ao = applicationOptionCreator.createV1EducationApplicationOption(los, hakukohdeDTO, hakuRes.getResult());
                    // If fetching for preview, the status of the application option is added
                    if (!checkStatus) {
                        ao.setStatus(hakukohdeDTO.getTila());
                        ao.getApplicationSystem().setStatus(hakuDTO.getTila());
                    }
                    if (ao.showInOpintopolku()) {
                        aos.add(ao);
                        cachedApplicationOptionResults.put(ao.getId(), ao);
                    } else {
                        invalidOids.add(aoId);
                    }

                } catch (Exception ex) {
                    LOG.debug("Problem fetching ao: {}", ex.getMessage());
                    invalidOids.add(aoId);
                }
            }
        }

        los.setApplicationOptions(aos);

        return !aos.isEmpty();
    }

    public HigherEducationLOS createHigherEducationLOSReference(
            KoulutusKorkeakouluV1RDTO koulutusDTO, boolean b) throws KoodistoException {
        HigherEducationLOS los = new HigherEducationLOS();
        los.setId(koulutusDTO.getOid());
        los.setName(getI18nTextEnriched(koulutusDTO.getKoulutusohjelma()));
        los.setEducationDegree(koulutusDTO.getKoulutusaste().getUri());
        if (!b) {
            los.setStatus(koulutusDTO.getTila().toString());
        }
        return los;
    }

    public HigherEducationLOSRef createHigherEducationLOSRef(
            KoulutusKorkeakouluV1RDTO koulutusDTO, boolean b, ApplicationOption ao) throws TarjontaParseException, KoodistoException {

        HigherEducationLOSRef losRef = new HigherEducationLOSRef();

        losRef.setId(koulutusDTO.getOid());
        losRef.setName(getI18nTextEnriched(koulutusDTO.getKoulutusohjelma()));
        losRef.setQualifications(getI18nTextMultiple(koulutusDTO.getTutkintonimikes()));
        losRef.setPrerequisite(ao.getPrerequisite());

        return losRef;
    }

    public CompetenceBasedQualificationParentLOS createCBQPLOS(String parentKomoOid, List<String> komotoOids, boolean checkStatus)
            throws TarjontaParseException, KoodistoException {

        CompetenceBasedQualificationParentLOS los = new CompetenceBasedQualificationParentLOS();

        los.setType(TarjontaConstants.TYPE_ADULT_VOCATIONAL);
        List<Code> rawTranslCodes = new ArrayList<Code>();

        for (String curKomotoOid : komotoOids) {
            LOG.debug("Cur standalone competence komoto oid: {}", curKomotoOid);
            ResultV1RDTO<AmmattitutkintoV1RDTO> res = this.tarjontaRawService.getAdultVocationalLearningOpportunity(curKomotoOid);
            NayttotutkintoV1RDTO dto = res.getResult();

            LOG.debug("Got dto ");

            if (dto == null || dto.getToteutustyyppi() == null || !isAikuAmm(dto)) {
                LOG.debug("Unfitting komoto, continuing");
                try {
                    LOG.debug("Toteutustyyppi: {}", dto.getToteutustyyppi().name());
                } catch (Exception ex) {
                    LOG.debug("Could not get toteutustyyppi: ");
                }
                continue;
            }
            LOG.debug("Toteutustyyppi: {}", dto.getToteutustyyppi().name());
            LOG.debug("Ok, creating it");
            try {

                AdultVocationalLOS newLos = createAdultVocationalLOS(dto, checkStatus);

                LOG.debug("Updating parnet los data with dto: {}", dto.getOid());

                updateParentLosData(los, rawTranslCodes, dto, parentKomoOid, newLos);
                if (los.getChildren() == null) {
                    los.setChildren(new ArrayList<AdultVocationalLOS>());
                }
                los.getChildren().add(newLos);

                newLos.setParent(new ParentLOSRef(los.getId(), los.getName()));

            } catch (TarjontaParseException e) {
                LOG.warn("Failed to parse AdultVocationalLOS {} for komo {}", curKomotoOid, parentKomoOid, e);
            }
        }

        if (los == null || los.getChildren() == null || los.getChildren().isEmpty()) {
            if (checkStatus) {
                throw new TarjontaParseException("No valid children for parent adult vocational: " + parentKomoOid);
            }
            return null;
        }

        Map<String, Code> availableLanguagesMap = new HashMap<String, Code>();
        for (Code curCode : rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }
        los.setAvailableTranslationLanguages(new ArrayList<Code>(availableLanguagesMap.values()));

        Map<String, ApplicationOption> aoMap = new HashMap<String, ApplicationOption>();
        Map<String, Code> topicMap = new HashMap<String, Code>();
        Map<String, Code> themeMap = new HashMap<String, Code>();

        for (AdultVocationalLOS curChild : los.getChildren()) {
            if (curChild.getApplicationOptions() != null) {
                for (ApplicationOption ao : curChild.getApplicationOptions()) {
                    aoMap.put(ao.getId(), ao);
                }
            }
            for (Code curTopic : curChild.getTopics()) {
                topicMap.put(curTopic.getUri(), curTopic);
            }
            for (Code curTheme : curChild.getThemes()) {
                themeMap.put(curTheme.getUri(), curTheme);
            }
        }

        los.setTopics(new ArrayList<Code>(topicMap.values()));
        los.setThemes(new ArrayList<Code>(themeMap.values()));

        if (!aoMap.isEmpty()) {
            los.setApplicationOptions(new ArrayList<ApplicationOption>(aoMap.values()));
        }

        return los;
    }

    private void updateParentLosData(CompetenceBasedQualificationParentLOS los,
            List<Code> rawTranslCodes, NayttotutkintoV1RDTO dto,
            String parentKomoOid, AdultVocationalLOS newLos) throws KoodistoException {
        if (los.getName() == null) {
            // los.setName(newLos.getName());
            los.setName(getI18nTextEnriched(dto.getKoulutuskoodi().getMeta()));
        }
        if (los.getGoals() == null
                && dto.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null
                && !dto.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setGoals(getI18nTextEnriched(dto.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(dto.getKuvausKomo().get(KomoTeksti.TAVOITTEET))));

        }
        if (los.getAccessToFurtherStudies() == null
                && dto.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null
                && !dto.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(dto.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(dto.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET))));
        }
        if (los.getChoosingCompetence() == null
                && dto.getKuvausKomoto().get(KomotoTeksti.OSAAMISALAN_VALINTA) != null
                && !dto.getKuvausKomoto().get(KomotoTeksti.OSAAMISALAN_VALINTA).getTekstis().containsKey(UNDEFINED)) {
            los.setChoosingCompetence(getI18nTextEnriched(dto.getKuvausKomoto().get(KomotoTeksti.OSAAMISALAN_VALINTA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(dto.getKuvausKomoto().get(KomotoTeksti.OSAAMISALAN_VALINTA))));
        }
        if (los.getDegreeCompletion() == null
                && dto.getKuvausKomoto().get(KomotoTeksti.NAYTTOTUTKINNON_SUORITTAMINEN) != null
                && !dto.getKuvausKomoto().get(KomotoTeksti.NAYTTOTUTKINNON_SUORITTAMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setDegreeCompletion(getI18nTextEnriched(dto.getKuvausKomoto().get(KomotoTeksti.NAYTTOTUTKINNON_SUORITTAMINEN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(dto.getKuvausKomoto().get(KomotoTeksti.NAYTTOTUTKINNON_SUORITTAMINEN))));
        }
        if (los.getProvider() == null) {
            los.setProvider(newLos.getProvider());
        }
        if (los.getId() == null) {
            los.setId(dto.getOid());
        }
        if (los.getEducationDomain() == null) {
            los.setEducationDomain(newLos.getEducationDomain());
        }
        if (los.getEducationKind() == null) {
            los.setEducationKind(getI18nTextEnriched(dto.getKoulutuslaji().getMeta()));
        }
        if (los.getEducationType() == null) {
            los.setEducationType(getI18nTextEnriched(dto.getKoulutustyyppi().getMeta()));
            los.setEdtUri(dto.getKoulutustyyppi().getUri());
        }

        if (dto.getHintaString() != null) {
            LOG.debug("setting charge with los: " + los.getId() + " and dto hinta: {}", dto.getHintaString());
            los.setCharge(dto.getHintaString());
        }
        los.setChargeable(dto.getOpintojenMaksullisuus());
        los.setOsaamisala(!dto.getKoulutusmoduuliTyyppi().name().equals(KoulutusmoduuliTyyppi.TUTKINTO.name()));
        los.setDeterminer(dto.getTarkenne());

    }

    private boolean isAikuAmm(NayttotutkintoV1RDTO dto) {
        return (dto.getToteutustyyppi().name().startsWith(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA.name())
                || dto.getToteutustyyppi().name().startsWith(ToteutustyyppiEnum.AMMATTITUTKINTO.name())
                || dto.getToteutustyyppi().name().startsWith(ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO.name()))
                && dto.getKoulutuslaji().getUri().startsWith("koulutuslaji_a");
    }

    public KoulutusLOS createValmaLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        LOG.debug("Creating Valma los: {}", koulutusDTO.getOid());
        return createValmistavaLOS(koulutusDTO, checkStatus, SolrConstants.ED_TYPE_VALMA);
    }

    public KoulutusLOS createValmaErLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        LOG.debug("Creating Valma Er los: {}", koulutusDTO.getOid());
        return createValmistavaLOS(koulutusDTO, checkStatus, SolrConstants.ED_TYPE_VALMA_ER);
    }

    public KoulutusLOS createValmentavaLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        LOG.debug("Creating Valmentava ja kuntouttava los: {}", koulutusDTO.getOid());
        return createValmistavaLOS(koulutusDTO, checkStatus, SolrConstants.ED_TYPE_VALMENTAVA);
    }

    public KoulutusLOS createTelmaLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        LOG.debug("Creating Telma los: {}", koulutusDTO.getOid());
        return createValmistavaLOS(koulutusDTO, checkStatus, SolrConstants.ED_TYPE_TELMA);
    }

    public KoulutusLOS createKansanopistoLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws KoodistoException, TarjontaParseException {
        LOG.debug("Creating MM kansanopisto los: {}", koulutusDTO.getOid());
        KoulutusLOS los = createValmistavaLOS(koulutusDTO, checkStatus, SolrConstants.ED_TYPE_KANSANOPISTO);
        if ((koulutusDTO.getKoulutusohjelmanNimiKannassa() == null || koulutusDTO.getKoulutusohjelmanNimiKannassa().isEmpty())
                && !(los.getApplicationOptions() == null || los.getApplicationOptions().isEmpty())) {
            ApplicationOption ao = los.getApplicationOptions().get(0);
            los.setName(ao.getName());
            los.setShortTitle(ao.getName());
        }
        return los;
    }

    public KoulutusLOS createKymppiluokkaLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        LOG.debug("Creating Kymppiluokka los: {}", koulutusDTO.getOid());
        return createValmistavaLOS(koulutusDTO, checkStatus, SolrConstants.ED_TYPE_TENTH_GRADE);
    }

    public KoulutusLOS createMMLukioonValmistavaLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws KoodistoException, TarjontaParseException {
        LOG.debug("Creating MM lukioon valmistava los: {}", koulutusDTO.getOid());
        return createValmistavaLOS(koulutusDTO, checkStatus, SolrConstants.ED_TYPE_IMM_UPSEC);
    }

    private KoulutusLOS createValmistavaLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus, String edType) throws KoodistoException,
            TarjontaParseException {
        KoulutusLOS los = createKoulutusGenericV1LOS(koulutusDTO, checkStatus, edType);
        addValmistavaKoulutusV1Fields(koulutusDTO, los);
        return los;
    }

    public KoulutusLOS createAmmatillinenLOS(String oid, boolean checkStatus) throws KoodistoException,
            TarjontaParseException {
        ResultV1RDTO<KoulutusV1RDTO> result = tarjontaRawService.getV1KoulutusLearningOpportunity(oid);
        if (result != null && result.getResult() != null && result.getResult().getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
            if (KoulutusAmmatillinenPerustutkintoV1RDTO.class.isAssignableFrom(result.getResult().getClass())) {
                KoulutusAmmatillinenPerustutkintoV1RDTO koulutusDTO = (KoulutusAmmatillinenPerustutkintoV1RDTO) result.getResult();
                return createAmmatillinenLOS(koulutusDTO, checkStatus);
            } else if (NayttotutkintoV1RDTO.class.isAssignableFrom(result.getResult().getClass())) {
                NayttotutkintoV1RDTO koulutusDTO = (NayttotutkintoV1RDTO) result.getResult();
                return createAdultVocationalLOS(koulutusDTO, checkStatus);
            }
        }
        return null;
    }

    public KoulutusLOS createAmmatillinenLOS(KoulutusAmmatillinenPerustutkintoV1RDTO koulutusDTO, boolean checkStatus) throws KoodistoException,
            TarjontaParseException {

        String edType = SolrConstants.ED_TYPE_AMMATILLINEN;
        if (koulutusDTO.getKoulutustyyppi().getUri().contains("koulutustyyppi_4")) {
            edType = SolrConstants.ED_TYPE_AMM_ER;
        }

        KoulutusLOS los = createKoulutusGenericV1LOS(koulutusDTO, checkStatus, edType);
        addKoulutus2AsteV1Fields(koulutusDTO, los);
        addKoulutusAmmatillinenPerustutkintoV1Fields(koulutusDTO, los);
        if (!los.isOsaamisalaton()) {
            los.setAccessToFurtherStudies(null); // Ammatillisilla koulutuksilla jatko-opinnot näytetään tutkinnon sivulla
        }
        return los;
    }

    public KoulutusLOS createLukioLOS(String oid, boolean checkStatus) throws KoodistoException, TarjontaParseException {
        ResultV1RDTO<KoulutusV1RDTO> result = tarjontaRawService.getV1KoulutusLearningOpportunity(oid);
        if (result != null) {
            KoulutusLukioV1RDTO koulutusDTO = (KoulutusLukioV1RDTO) result.getResult();
            return createLukioLOS(koulutusDTO, checkStatus);
        }
        return null;
    }

    public KoulutusLOS createLukioLOS(KoulutusLukioV1RDTO koulutusDTO, boolean checkStatus) throws KoodistoException,
            TarjontaParseException {
        KoulutusLOS los = createKoulutusGenericV1LOS(koulutusDTO, checkStatus, SolrConstants.ED_TYPE_LUKIO);
        addKoulutus2AsteV1Fields(koulutusDTO, los);
        addKoulutusKoulutusLukioV1Fields(koulutusDTO, los);
        los.setQualifications(null); // aina ylioppilas, ei haluta näyttää kuvauksessa

        Code name = koodistoService.searchFirst(koulutusDTO.getKoulutusohjelma().getUri());
        los.setShortTitle(name.getShortTitle()); // Otsikkoa varten tarvitaan lukiolinjan lyhytnimi

        for (ApplicationOption ao : los.getApplicationOptions()) {
            ao.setEligibilityDescription(null); // Lukioiden hakukohteilla näytetään valintaperusteet (SelectionCriteria) hakukelpoisuustiedon sijaan.
        }
        return los;
    }

    private void addKoulutusKoulutusLukioV1Fields(KoulutusLukioV1RDTO koulutusDTO, KoulutusLOS los) throws KoodistoException {
        if (koulutusDTO.getLukiodiplomit() != null) {
            los.setDiplomas(getI18nTextMultiple(koulutusDTO.getLukiodiplomit()));
        }
    }

    private KoulutusLOS createKoulutusGenericV1LOS(KoulutusGenericV1RDTO koulutusDTO, boolean checkStatus, String edType) throws KoodistoException,
            TarjontaParseException {
        KoulutusLOS los = new KoulutusLOS();
        los.setType(TarjontaConstants.TYPE_KOULUTUS);
        los.setEducationType(edType);
        addKoulutusV1Fields(koulutusDTO, los, checkStatus, TarjontaConstants.TYPE_KOULUTUS, true);
        addKoulutusGenericV1Fields(koulutusDTO, los);
        if (!checkStatus) {
            los.setStatus(koulutusDTO.getTila().toString());
        }
        return los;
    }

    private void addValmistavaKoulutusV1Fields(ValmistavaKoulutusV1RDTO koulutusDTO, KoulutusLOS los) {
        if (koulutusDTO.getKoulutusohjelmanNimiKannassa() != null) {
            los.setName(new I18nText(koulutusDTO.getKoulutusohjelmanNimiKannassa()));
            los.setShortTitle(new I18nText(koulutusDTO.getKoulutusohjelmanNimiKannassa()));
        }
        if (koulutusDTO.getOpintojenLaajuusarvoKannassa() != null) {
            los.setCreditValue(koulutusDTO.getOpintojenLaajuusarvoKannassa());
        }
    }

    private void addTutkintoonJohtamatonKoulutusFields(TutkintoonJohtamatonKoulutusV1RDTO koulutus, KoulutusLOS los) throws KIException {
        try {
            los.setPrerequisites(createCodes(koulutus.getPohjakoulutusvaatimukset()));
            List<Code> facetPrequisites = this.getFacetPrequisites(los.getPrerequisites());
            los.setFacetPrerequisites(facetPrequisites);

            los.setEndDate(koulutus.getKoulutuksenLoppumisPvm());
            los.setCreditValue(koulutus.getOpintojenLaajuusPistetta());
            los.setOpettaja(koulutus.getOpettaja());
            los.setSubjects(getSubjects(koulutus.getOppiaineet()));
            los.setOpinnonTyyppiUri(koulutus.getOpinnonTyyppiUri());

            if (!StringUtils.isEmpty(koulutus.getTarjoajanKoulutus())) {
                ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> vastaavanYliopistonKoulutus = tarjontaRawService.searchEducation(koulutus
                        .getTarjoajanKoulutus());
                String vastaavaOppilaitosOid = vastaavanYliopistonKoulutus.getResult().getTulokset().get(0).getOid();
                los.setVastaavaKorkeakoulu(providerService.getByOID(vastaavaOppilaitosOid).getName());
            }
        } catch (ResourceNotFoundException | IOException e) {
            LOG.warn("Organisaation hakeminen avoimen yliopiston koulutuksesta {} vastaavalle koulutukselle {} epäonnistui.", koulutus.getOid(),
                    koulutus.getTarjoajanKoulutus(), e);
            throw new ResourceNotFoundException("Organisaation hakeminen avoimen yliopiston koulutuksesta vastaavalle organisaatiolle epäonnistui", e);
        }
    }

    private <S extends KoulutusV1RDTO, T extends KoulutusLOS> void addKoulutusV1Fields(S koulutus, T los, boolean checkStatus, String aoType,
            boolean needsAOsToBeValid) throws KoodistoException, TarjontaParseException {
        los.setId(koulutus.getOid());
        los.setToteutustyyppi(koulutus.getToteutustyyppi());
        los.setName(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setShortTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        if (los.getName() == null) {
            los.setName(getI18nTextEnriched(koulutus.getKoulutuskoodi()));
            los.setShortTitle(getI18nTextEnriched(koulutus.getKoulutuskoodi()));
        }
        if (los.getName() == null) {
            throw new TarjontaParseException("Generating LOS for oid " + koulutus.getOid() + " failed. Could not parse name from DTO.");
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
        }
        if (koulutus.getAihees().getUris() != null && !koulutus.getAihees().getUris().isEmpty()) {
            los.setTopics(createCodes(koulutus.getAihees()));
        }
        else if (koulutus.getOpintoala() != null) {
            los.setTopics(getTopics(koulutus.getOpintoala().getUri()));
        }
        if (los.getTopics() != null) {
            los.setThemes(getThemes(los));
        }
        if (koulutus.getAmmattinimikkeet() != null) {
            los.setProfessionalTitles(getI18nTextMultiple(koulutus.getAmmattinimikkeet()));
        }
        
        los.setKomoOid(koulutus.getKomoOid());

        Map<String, Code> availableLanguagesMap = new HashMap<String, Code>();

        List<Code> rawTranslCodes = new ArrayList<Code>();

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
            los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO))));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstis().containsKey(UNDEFINED)) {
            los.setWorkingLifePlacement(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey(UNDEFINED)) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(this.getTranslationUris(koulutus.getKuvausKomoto().get(
                    KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA))));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA).getTekstis().containsKey(UNDEFINED)) {
            los.setTargetGroup(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA).getTekstis().containsKey(UNDEFINED)) {
            los.setSelectingDegreeProgram(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS).getTekstis().containsKey(UNDEFINED)) {
            los.setMaksullisuus(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.EDELTAVAT_OPINNOT) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.EDELTAVAT_OPINNOT).getTekstis().containsKey(UNDEFINED)) {
            los.setEdeltavatOpinnot(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.EDELTAVAT_OPINNOT)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.ARVIOINTIKRITEERIT) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.ARVIOINTIKRITEERIT).getTekstis().containsKey(UNDEFINED)) {
            los.setArviointi(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.ARVIOINTIKRITEERIT)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.OPETUKSEN_AIKA_JA_PAIKKA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.OPETUKSEN_AIKA_JA_PAIKKA).getTekstis().containsKey(UNDEFINED)) {
            los.setOpetuksenAikaJaPaikka(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.OPETUKSEN_AIKA_JA_PAIKKA)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIEDOT) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIEDOT).getTekstis().containsKey(UNDEFINED)) {
            los.setLisatietoja(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIEDOT)));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS).getTekstis().containsKey(UNDEFINED)) {
            los.setCompetence(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS)));
        }

        // TODO: varmista että kaikki KomotoTekstityypit löytyvät tästä generoinnista

        los.setTeachingLanguages(createCodes(koulutus.getOpetuskielis()));

        // fields used to resolve available translation languages
        // content, internationalization, cooperation
        for (Code curCode : rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }

        for (Code teachingLanguage : los.getTeachingLanguages()) {
            availableLanguagesMap.put(teachingLanguage.getUri(), teachingLanguage);
        }
        los.setAvailableTranslationLanguages(new ArrayList<Code>(availableLanguagesMap.values()));

        if (koulutus.getYhteyshenkilos() != null) {
            for (YhteyshenkiloTyyppi yhteyshenkiloRDTO : koulutus.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getSahkoposti(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                if (yhteyshenkiloRDTO.getHenkiloTyyppi() != null) {
                    contactPerson.setType(yhteyshenkiloRDTO.getHenkiloTyyppi().name());
                }
                los.getContactPersons().add(contactPerson);
            }
        }

        los.setEducationDomain(getI18nTextEnriched(koulutus.getKoulutusala()));
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi()));
        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste()));

        los.setDegree(getI18nTextEnriched(koulutus.getTutkinto()));
        if (koulutus.getKoulutuskoodi() != null) {
            los.setEducationCode(koodistoService.searchFirst(koulutus.getKoulutuskoodi().getUri()));
        }
        if (koulutus.getKoulutusaste() != null) {
            los.setEducationDegree(koulutus.getKoulutusaste().getUri());
        }
        if (koulutus.getKoulutuksenAlkamisPvms() != null && !koulutus.getKoulutuksenAlkamisPvms().isEmpty()) {
            los.setStartDate(koulutus.getKoulutuksenAlkamisPvms().iterator().next());
        }
        if (koulutus.getKoulutuksenAlkamisvuosi() != null) {
            los.setStartYear(koulutus.getKoulutuksenAlkamisvuosi());
        }
        if (koulutus.getKoulutuksenAlkamiskausi() != null) {
            los.setStartSeason(getI18nTextEnriched(koulutus.getKoulutuksenAlkamiskausi()));
        }

        los.setPlannedDuration(koulutus.getSuunniteltuKestoArvo());
        if (koulutus.getSuunniteltuKestoTyyppi() != null) {
            los.setPlannedDurationUnit(getI18nTextEnriched(koulutus.getSuunniteltuKestoTyyppi()));
            los.setPduCodeUri(koulutus.getSuunniteltuKestoTyyppi().getUri());
        }
        los.setCreditValue(koulutus.getOpintojenLaajuusarvo().getArvo());
        los.setCreditUnit(getI18nTextEnriched(koulutus.getOpintojenLaajuusyksikko()));

        try {
            Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
            los.setProvider(provider);
        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }

        los.setFormOfTeaching(getI18nTextMultiple(koulutus.getOpetusmuodos()));
        los.setFotFacet(this.createCodes(koulutus.getOpetusPaikkas()));
        los.setTimeOfTeachingFacet(this.createCodes(koulutus.getOpetusAikas()));
        los.setFormOfStudyFacet(this.createCodes(koulutus.getOpetusmuodos()));

        los.setTeachingTimes(getI18nTextMultiple(koulutus.getOpetusAikas()));
        los.setTeachingPlaces(getI18nTextMultiple(koulutus.getOpetusPaikkas()));

        los.setStartDates(Lists.newArrayList(koulutus.getKoulutuksenAlkamisPvms()));
        los.setOsaamisalaton(koulutus.getKoulutusohjelma().getUri() == null);

        if (koulutus.getHinta() != null) {
            los.setHinta("" + koulutus.getHinta());
        } else {
            los.setHinta(koulutus.getHintaString());
        }

        // THIS ACTUALLY CREATES THE HAKUKOHDE!!!
        boolean existsValidHakukohde = fetchAndCreateHakukohdeData(los, checkStatus);

        // If we are not fetching for preview, an exception is thrown if no valid application options exist
        if (checkStatus && needsAOsToBeValid && !existsValidHakukohde) {
            throw new TarjontaParseException("No valid application options for education: " + los.getId());
        }
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption ao : los.getApplicationOptions()) {
                ao.setProvider(los.getProvider());
                ao.setParent(createParentLosRef(los));
                ao.setEducationDegree(los.getEducationDegree());
                los.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
                ao.setType(aoType);
            }
        }

        if (!checkStatus) {
            los.setStatus(koulutus.getTila().toString());
        }
        
    }

    private <S extends KoulutusAmmatillinenPerustutkintoV1RDTO, T extends KoulutusLOS> void addKoulutusAmmatillinenPerustutkintoV1Fields(S koulutus, T los)
            throws KoodistoException, TarjontaParseException {
        if (koulutus.getKoulutuksenTavoitteet() != null) {
            los.setGoals(getI18nText(koulutus.getKoulutuksenTavoitteet()));
        }
        los.setDegreeTitles(getI18nTextMultiple(koulutus.getTutkintonimikes()));
        los.setQualifications(getI18nTextMultiple(koulutus.getTutkintonimikes()));
    }

    private <S extends KoulutusGenericV1RDTO, T extends KoulutusLOS> void addKoulutusGenericV1Fields(S koulutus, T los)
            throws KoodistoException, TarjontaParseException {
        Code requirementsCode = createCode(koulutus.getPohjakoulutusvaatimus());
        if (requirementsCode != null) {
            los.getPrerequisites().add(requirementsCode);
        }
        los.setKoulutusPrerequisite(requirementsCode);
        List<Code> facetPrequisites = this.getFacetPrequisites(los.getPrerequisites());
        los.setFacetPrerequisites(facetPrequisites);
        if (koulutus.getKoulutuslaji() != null) {
            los.setKoulutuslaji(koodistoService.searchFirst(koulutus.getKoulutuslaji().getUri()));
        }
        los.setLinkToCurriculum(koulutus.getLinkkiOpetussuunnitelmaan());
    }

    private AdultVocationalLOS createAdultVocationalLOS(NayttotutkintoV1RDTO koulutus, boolean checkStatus) throws TarjontaParseException, KoodistoException {

        LOG.debug("Creating adult vocational los: {}", koulutus.getOid());

        AdultVocationalLOS los = new AdultVocationalLOS();

        addKoulutusV1Fields(koulutus, los, checkStatus, TarjontaConstants.TYPE_ADULT_VOCATIONAL, true);

        //TODO: Poistetaan addKoulutusV1Fields metodin täyttämät kentät alta.

        los.setStatus(koulutus.getTila().toString());

        los.setType(TarjontaConstants.TYPE_ADULT_VOCATIONAL);
        los.setKomoOid(koulutus.getKomoOid());
        los.setValmistavaKoulutus(koulutus.getValmistavaKoulutus() != null);
        if (koulutus.getToteutustyyppi().name().equals(ToteutustyyppiEnum.AMMATTITUTKINTO.name())) {
            los.setEducationType(SolrConstants.ED_TYPE_AMM_TUTK);
        } else if (koulutus.getToteutustyyppi().name().equals(ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO.name())) {
            los.setEducationType(SolrConstants.ED_TYPE_AMM_ER);
        } else {
            los.setEducationType(SolrConstants.ED_TYPE_AMMATILLINEN);
        }

        // Set<Code> availableLanguagaes = Sets.newHashSet();
        Map<String, Code> availableLanguagesMap = new HashMap<String, Code>();
        List<Code> rawTranslCodes = new ArrayList<Code>();

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
            los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO))));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey(UNDEFINED)) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA))));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS).getTekstis().containsKey(UNDEFINED)) {
            los.setInfoAboutCharge(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS))));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstis().containsKey(UNDEFINED)) {
            los.setCareerOpportunities(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN))));
        }

        los.setTeachingLanguages(createCodes(koulutus.getOpetuskielis()));// koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));

        // fields used to resolve available translation languages
        // content, internationalization, cooperation
        for (Code curCode : rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }

        for (Code teachingLanguage : los.getTeachingLanguages()) {
            availableLanguagesMap.put(teachingLanguage.getUri(), teachingLanguage);
        }
        los.setAvailableTranslationLanguages(new ArrayList<Code>(availableLanguagesMap.values()));

        if (koulutus.getYhteyshenkilos() != null) {
            for (YhteyshenkiloTyyppi yhteyshenkiloRDTO : koulutus.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getSahkoposti(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                if (yhteyshenkiloRDTO.getHenkiloTyyppi() != null) {
                    contactPerson.setType(yhteyshenkiloRDTO.getHenkiloTyyppi().name());
                }
                los.getContactPersons().add(contactPerson);
            }
        }

        los.setEducationDomain(getI18nTextEnriched(koulutus.getKoulutusala().getMeta()));
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koodistoService.searchFirst(koulutus.getKoulutuskoodi().getUri()));
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());

        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta())); // muutos: oli koulutus.getKoulutusohjelma()
        // los.setDegreeTitles(getI18nTextMultiple(koulutus.getTutkintonimikes())); // ei löydy NayttotutkintoV1RDTO:lle
        los.setQualifications(getQualificationsForAikuAmm(koulutus));// Arrays.asList(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta())));

        los.setDegree(getI18nTextEnriched(koulutus.getTutkinto().getMeta()));

        if (koulutus.getKoulutuksenAlkamisPvms() != null && !koulutus.getKoulutuksenAlkamisPvms().isEmpty()) {
            los.setStartDate(koulutus.getKoulutuksenAlkamisPvms().iterator().next());
        }
        if (koulutus.getKoulutuksenAlkamisvuosi() != null) {
            los.setStartYear(koulutus.getKoulutuksenAlkamisvuosi());
        }
        if (koulutus.getKoulutuksenAlkamiskausi() != null) {
            los.setStartSeason(getI18nTextEnriched(koulutus.getKoulutuksenAlkamiskausi().getMeta()));
        }

        if (koulutus.getValmistavaKoulutus() != null) {

            los.setPlannedDuration(koulutus.getValmistavaKoulutus().getSuunniteltuKestoArvo());
            los.setPlannedDurationUnit(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getSuunniteltuKestoTyyppi().getMeta()));
            los.setPduCodeUri(koulutus.getValmistavaKoulutus().getSuunniteltuKestoTyyppi().getUri());
            los.setChargeable(koulutus.getValmistavaKoulutus().getOpintojenMaksullisuus());
            if (koulutus.getValmistavaKoulutus().getHintaString() != null) {
                los.setCharge(koulutus.getValmistavaKoulutus().getHintaString());
            }

            if (koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KOHDERYHMA) != null
                    && !koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KOHDERYHMA).getTekstis().containsKey(UNDEFINED)) {
                los.setTargetGroup(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KOHDERYHMA)));
                rawTranslCodes.addAll(koodistoService.searchMultiple(
                        this.getTranslationUris(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KOHDERYHMA))));
            }

            if (koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.OPISKELUN_HENKILOKOHTAISTAMINEN) != null
                    && !koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.OPISKELUN_HENKILOKOHTAISTAMINEN).getTekstis().containsKey(UNDEFINED)) {
                los.setPersonalization(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.OPISKELUN_HENKILOKOHTAISTAMINEN)));
                rawTranslCodes.addAll(koodistoService.searchMultiple(
                        this.getTranslationUris(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.OPISKELUN_HENKILOKOHTAISTAMINEN))));
            }

            if (koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO) != null
                    && !koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
                los.setContent(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO)));
                rawTranslCodes.addAll(koodistoService.searchMultiple(
                        this.getTranslationUris(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO))));
            }

            if (koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KANSAINVALISTYMINEN) != null
                    && !koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
                los.setInternationalization(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KANSAINVALISTYMINEN)));
                rawTranslCodes.addAll(koodistoService.searchMultiple(
                        this.getTranslationUris(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KANSAINVALISTYMINEN))));
            }

            if (koulutus.getValmistavaKoulutus().getYhteyshenkilos() != null
                    && !koulutus.getValmistavaKoulutus().getYhteyshenkilos().isEmpty()) {
                List<ContactPerson> persons = new ArrayList<ContactPerson>();
                for (YhteyshenkiloTyyppi yhteyshenkiloRDTO : koulutus.getValmistavaKoulutus().getYhteyshenkilos()) {
                    ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                            yhteyshenkiloRDTO.getSahkoposti(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                    if (yhteyshenkiloRDTO.getHenkiloTyyppi() != null) {
                        contactPerson.setType(yhteyshenkiloRDTO.getHenkiloTyyppi().name());
                    }
                    persons.add(contactPerson);
                }
                los.setPreparatoryContactPersons(persons);
            }

            los.setFormOfTeaching(getI18nTextMultiple(koulutus.getValmistavaKoulutus().getOpetusmuodos()));
            los.setFotFacet(this.createCodes(koulutus.getValmistavaKoulutus().getOpetusPaikkas()));
            los.setTimeOfTeachingFacet(this.createCodes(koulutus.getValmistavaKoulutus().getOpetusAikas()));
            los.setFormOfStudyFacet(this.createCodes(koulutus.getValmistavaKoulutus().getOpetusmuodos()));
            los.setTeachingTimes(getI18nTextMultiple(koulutus.getValmistavaKoulutus().getOpetusAikas()));
            los.setTeachingPlaces(getI18nTextMultiple(koulutus.getValmistavaKoulutus().getOpetusPaikkas()));

        }

        if (koulutus.getAmmattinimikkeet() != null) {
            los.setProfessionalTitles(getI18nTextMultiple(koulutus.getAmmattinimikkeet()));// getAmmattinimikeUris()));
        }

        los.setCreditValue(koulutus.getOpintojenLaajuusarvo().getArvo());
        los.setCreditUnit(getI18nTextEnriched(koulutus.getOpintojenLaajuusyksikko().getMeta()));

        try {
            Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
            los.setProvider(provider);
        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }

        try {
            Provider organizer = providerService.getByOID(koulutus.getJarjestavaOrganisaatio().getOid());
            los.setOrganizer(organizer.getName());

        } catch (Exception ex) {
            throw new KoodistoException("Problem reading jarjestava organisaatio: " + ex.getMessage());
        }

        boolean existsValidHakukohde = fetchAndCreateHakukohdeData(los, checkStatus);

        // If we are not fetching for preview, an exception is thrown if no valid application options exist
        if (checkStatus && !existsValidHakukohde) {
            throw new TarjontaParseException("No valid application options for education: " + los.getId());
        }
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption ao : los.getApplicationOptions()) {
                ao.setProvider(los.getProvider());
                ao.setEducationDegree(los.getEducationDegree());
                los.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
                // ao.setParent(createParentLosRef(los));
                ao.setType(TarjontaConstants.TYPE_ADULT_VOCATIONAL);// TarjontaConstants.TYPE_ADULT_UPSEC);
            }
        }

        if (koulutus.getKoulutuslaji() != null) {
            los.setKoulutuslaji(this.koodistoService.searchFirst(koulutus.getKoulutuslaji().getUri()));
        }

        return los;

    }

    public TutkintoLOS createTutkintoLOS(String komoOid, String providerOid, String year, String season) throws KoodistoException, TarjontaParseException {
        ResultV1RDTO<KomoV1RDTO> v1KomoRaw = tarjontaRawService.getV1Komo(komoOid);
        KomoV1RDTO komo = v1KomoRaw.getResult();
        return createTutkintoLOS(komo, providerOid, year, season);
    }

    private TutkintoLOS createTutkintoLOS(KomoV1RDTO komo, String providerOid, String year, String season) throws KoodistoException, TarjontaParseException {
        LOG.debug("Creating provider specific parent (" + providerOid + ") LOS from komo: " + komo.getOid());
        TutkintoLOS tutkintoLOS = new TutkintoLOS();
        tutkintoLOS.setType(TarjontaConstants.TYPE_PARENT);

        NimiV1RDTO structure = komo.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE);
        if (structure != null) {
            tutkintoLOS.setStructure(getI18nText(structure.getTekstis()));
        }
        try {
            tutkintoLOS.setProvider(providerService.getByOID(providerOid));
        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }
        NimiV1RDTO accessToFurtherStudies = komo.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET);
        if (accessToFurtherStudies != null) {
            tutkintoLOS.setAccessToFurtherStudies(getI18nText(accessToFurtherStudies.getTekstis()));
        }
        tutkintoLOS.setEducationDegree(komo.getKoulutusaste().getArvo());

        tutkintoLOS.setId(CreatorUtil.resolveLOSId(komo.getOid(), providerOid, year, season));
        tutkintoLOS.setName(getI18nTextEnriched(komo.getKoulutuskoodi().getMeta()));
        tutkintoLOS.setShortTitle(getI18nTextEnriched(komo.getKoulutuskoodi().getMeta()));
        NimiV1RDTO goals = komo.getKuvausKomo().get(KomoTeksti.TAVOITTEET);
        if (goals != null) {
            tutkintoLOS.setGoals(getI18nText(goals.getTekstis()));
        }
        tutkintoLOS.setCreditValue(komo.getOpintojenLaajuusarvo().getArvo());
        tutkintoLOS.setCreditUnit(getI18nTextEnriched(komo.getOpintojenLaajuusyksikko().getMeta()));

        tutkintoLOS.setEducationDomain(getI18nTextEnriched(komo.getKoulutusala().getMeta()));
        tutkintoLOS.setStydyDomain(getI18nTextEnriched(komo.getOpintoala().getMeta()));
        tutkintoLOS.setTopics(getTopics(komo.getOpintoala().getUri()));
        tutkintoLOS.setThemes(getThemes(tutkintoLOS));
        tutkintoLOS.setEducationCode(createCode(komo.getKoulutuskoodi()));

        return tutkintoLOS;
    }

    public CalendarApplicationSystem createApplicationSystemForCalendar(HakuV1RDTO hakuDTO, boolean shownInCalendar) throws KoodistoException {
        return applicationOptionCreator.getApplicationSystemCreator().createApplicationSystemForCalendar(hakuDTO, shownInCalendar);
    }

    public void clearProcessedLists() {
        this.cachedApplicationOptionResults = Maps.newHashMap();
        this.invalidOids = Sets.newHashSet();
        this.alreadyCreatedKorkeakouluOpintos = Sets.newHashSet();
    }

    public KoulutusLOS createKorkeakouluopinto(String oid, boolean checkStatus, boolean isRecursiveCall) throws KIException {
        ResultV1RDTO<KoulutusV1RDTO> result = tarjontaRawService.getV1KoulutusLearningOpportunity(oid);
        if (result != null) {
            KorkeakouluOpintoV1RDTO koulutusDTO = (KorkeakouluOpintoV1RDTO) result.getResult();
            return createKorkeakouluopinto(koulutusDTO, checkStatus, isRecursiveCall);
        }
        return null;
    }

    private Set<String> alreadyCreatedKorkeakouluOpintos = Sets.newHashSet();

    public KoulutusLOS createKorkeakouluopinto(KorkeakouluOpintoV1RDTO dto, boolean checkStatus, boolean isRecursiveCallForOpintojakso) throws KIException {
        if (checkStatus && alreadyCreatedKorkeakouluOpintos.contains(dto.getOid())) {
            LOG.debug("Korkeakouluopinto on jo käsitelty aiemmin.");
            return null;
        }
        if (!StringUtils.isBlank(dto.getOpintokokonaisuusOid()) && !isRecursiveCallForOpintojakso) {
            LOG.debug("Opintojakso kuuluu opintokokonaisuuteen {} -> luodaan opintokokonaisuus.", dto.getOpintokokonaisuusOid());
            KoulutusLOS opintokokonaisuus = createKorkeakouluopinto(dto.getOpintokokonaisuusOid(), checkStatus, false);
            if (opintokokonaisuus != null) {
                alreadyCreatedKorkeakouluOpintos.add(opintokokonaisuus.getId());
            }
            return opintokokonaisuus;
        }
        LOG.debug("Luodaan korkeakouluopinto {} {}", dto.getKoulutusmoduuliTyyppi().name(), dto.getOid());

        // Jos opintojakso kuuluu kokonaisuuteen, kokonaisuudella on hakukohde ja opintojakso voi olla ilman.
        boolean needsAOsToBeValid = StringUtils.isEmpty(dto.getOpintokokonaisuusOid());

        KoulutusLOS los = new KoulutusLOS();
        los.setType(TarjontaConstants.TYPE_KOULUTUS);

        addKorkeakouluopintoEducationType(dto, los);
        addKoulutusV1Fields(dto, los, checkStatus, TarjontaConstants.TYPE_KOULUTUS, needsAOsToBeValid);
        addTutkintoonJohtamatonKoulutusFields(dto, los);

        List<KoulutusLOS> childOpintojaksos = Lists.newArrayList();
        alreadyCreatedKorkeakouluOpintos.add(los.getId());
        for (String opintojaksoOid : dto.getOpintojaksoOids()) {
            KoulutusLOS opintojakso = createKorkeakouluopinto(opintojaksoOid, checkStatus, true);
            if (opintojakso != null) {
                alreadyCreatedKorkeakouluOpintos.add(opintojakso.getId());
                opintojakso.setOpintokokonaisuus(los);
                opintojakso.getApplicationOptions().addAll(los.getApplicationOptions());
                childOpintojaksos.add(opintojakso);
            }
        }
        for (KoulutusLOS child : childOpintojaksos) {
            child.setSiblings(childOpintojaksos);
        }
        los.setOpintojaksos(childOpintojaksos);
        return los;
    }

    private void addKorkeakouluopintoEducationType(KorkeakouluOpintoV1RDTO dto, KoulutusLOS los) throws ResourceNotFoundException, KIException {
        String tyypinMaarittavaOrganisaatioOid = null;
        if (dto.getTarjoajanKoulutus() != null) { // Koulutustyyppi määräytyy opinnon tarjoajan mukaan
            tyypinMaarittavaOrganisaatioOid = tarjontaRawService.searchEducation(dto.getTarjoajanKoulutus()).getResult().getTulokset().get(0).getOid();
        } else { // tai suoraan organisaation mukaan jos tarjoaja itse järjestää opinnon
            tyypinMaarittavaOrganisaatioOid = dto.getOrganisaatio().getOid();
        }

        // Haetaan organisaatiopalvelun hakurajapinnasta organisaation oppilaitostyyppi
        String oppilaitostyyppi = providerService.getOppilaitosTyyppiByOID(tyypinMaarittavaOrganisaatioOid);
        if (oppilaitostyyppi.contains(TarjontaConstants.OPPILAITOSTYYPPI_AMK)) {
            los.setEducationType(SolrConstants.ED_TYPE_AVOIN_AMK);
        } else if (oppilaitostyyppi.contains(TarjontaConstants.OPPILAITOSTYYPPI_YLIOPISTO)
                || oppilaitostyyppi.contains(TarjontaConstants.OPPILAITOSTYYPPI_SOTILASKK)) {
            los.setEducationType(SolrConstants.ED_TYPE_AVOIN_YO);
        } else {
            LOG.error("Tuntematon korkeakouluopinnon {} oppilaitostyyppi {} organisaatiolla {}", dto.getOid(), oppilaitostyyppi,
                    tyypinMaarittavaOrganisaatioOid);
            throw new KIException(String.format("Tuntematon oppilaitostyyppi %s koulutuksella %s", oppilaitostyyppi, dto.getOid()));
        }
    }

}
