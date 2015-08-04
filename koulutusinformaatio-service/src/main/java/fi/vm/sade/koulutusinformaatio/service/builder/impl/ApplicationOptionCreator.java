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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.shared.types.Osoitemuoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionCreator extends ObjectCreator {


    private static final Logger LOG = LoggerFactory.getLogger(ApplicationOptionCreator.class);

    private KoodistoService koodistoService;
    private EducationObjectCreator educationObjectCreator;
    private ApplicationSystemCreator applicationSystemCreator;

    protected ApplicationOptionCreator(KoodistoService koodistoService,
                                       OrganisaatioRawService organisaatioRawService,
                                       ParameterService parameterService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.educationObjectCreator = new EducationObjectCreator(koodistoService, organisaatioRawService);
        this.applicationSystemCreator = new ApplicationSystemCreator(koodistoService, parameterService);
    }

    public ApplicationSystemCreator getApplicationSystemCreator() {
        return applicationSystemCreator;
    }

    public ApplicationOption createV1EducationApplicationOption(KoulutusLOS los,
                                                                HakukohdeV1RDTO hakukohde,
            HakuV1RDTO haku) throws KoodistoException, ResourceNotFoundException {

        ApplicationOption ao = new ApplicationOption();
        ao.setId(hakukohde.getOid());
        if (hakukohde.getHakukohteenNimet() != null) {
            ao.setName(super.getI18nText(hakukohde.getHakukohteenNimet()));
        } else if (hakukohde.getHakukohteenNimiUri() != null) {

            List<I18nText> hakKohdeNames = this.koodistoService.searchNames(hakukohde.getHakukohteenNimiUri());
            if (hakKohdeNames != null && !hakKohdeNames.isEmpty()) {
                ao.setName(hakKohdeNames.get(0));
            }
        }
        
        try {
            ao.setAoIdentifier(koodistoService.searchFirstCodeValue(hakukohde.getHakukohteenNimiUri()));
        } catch (Exception ex) {
            LOG.debug("HakukohdeNimiUri was not codeelement: " + ao.getId() + " name: " + hakukohde.getHakukohteenNimiUri());
        }
        if (ao.getAoIdentifier() == null) {
            ao.setAoIdentifier(hakukohde.getHakukohteenNimiUri());
        }
        
        ao.setAthleteEducation(false);
        ao.setStartingQuota(hakukohde.getAloituspaikatLkm());
        ao.setStartingQuotaDescription(getI18nText(hakukohde.getAloituspaikatKuvaukset()));
        ao.setLowestAcceptedScore(hakukohde.getAlinValintaPistemaara());
        ao.setLowestAcceptedAverage(hakukohde.getAlinHyvaksyttavaKeskiarvo());
        ao.setAttachmentDeliveryDeadline(hakukohde.getLiitteidenToimitusPvm());
        ao.setLastYearApplicantCount(hakukohde.getEdellisenVuodenHakijatLkm());
        ao.setSelectionCriteria(getI18nText(hakukohde.getValintaperusteKuvaukset()));
        ao.setSoraDescription(getI18nText(hakukohde.getSoraKuvaukset()));
        ao.setEligibilityDescription(getI18nText(hakukohde.getHakukelpoisuusVaatimusKuvaukset()));
        ao.setExams(educationObjectCreator.createHigherEducationExams(hakukohde.getValintakokeet()));
        ao.setOrganizationGroups(educationObjectCreator.createOrganizationGroups(hakukohde.getRyhmaliitokset(), hakukohde.getOrganisaatioRyhmaOids()));
        ao.setKaksoistutkinto(hakukohde.getKaksoisTutkinto());
        ao.setVocational(SolrConstants.ED_TYPE_AMMATILLINEN.equals(los.getEducationType()));
        ao.setEducationCodeUri(los.getEducationCode().getUri());
        ao.setPrerequisite(los.getKoulutusPrerequisite());
        
        List<String> baseEducations = new ArrayList<String>();
        for (Code code : los.getPrerequisites()) {
            baseEducations.add(code.getUri());
        }
        baseEducations.addAll(hakukohde.getHakukelpoisuusvaatimusUris());
        if (los.getKoulutusPrerequisite() != null) {
            List<Code> subCodes = koodistoService.searchSubCodes(
                    los.getKoulutusPrerequisite().getUri(),
                    TarjontaConstants.BASE_EDUCATION_KOODISTO_URI
            );
            for (Code subCode : subCodes) {
                baseEducations.add(subCode.getValue());
            }
        }
        ao.setRequiredBaseEducations(baseEducations);

        los.getPrerequisites().addAll(koodistoService.searchMultiple(hakukohde.getHakukelpoisuusvaatimusUris()));

        ApplicationSystem as = applicationSystemCreator.createApplicationSystem(haku);

        HakuaikaV1RDTO aoHakuaika = null;

        if (haku.getHakuaikas() != null) {
            for (HakuaikaV1RDTO ha : haku.getHakuaikas()) {
                DateRange range = new DateRange();
                range.setStartDate(ha.getAlkuPvm());
                range.setEndDate(ha.getLoppuPvm());
                as.getApplicationDates().add(range);

                if (ha.getHakuaikaId().equals(hakukohde.getHakuaikaId())) {
                    aoHakuaika = ha;
                }

            }
        }
        ao.setApplicationSystem(as);
        if (!Strings.isNullOrEmpty(hakukohde.getSoraKuvausKoodiUri())) {
            ao.setSora(true);
        }

        ao.setTeachingLanguages(extractCodeVales(los.getTeachingLanguages()));

        ao.setSpecificApplicationDates(hakukohde.isKaytetaanHakukohdekohtaistaHakuaikaa());
        if (ao.isSpecificApplicationDates()) {
            ao.setApplicationStartDate(hakukohde.getHakuaikaAlkuPvm());
            ao.setApplicationEndDate(hakukohde.getHakuaikaLoppuPvm());
        } else if (aoHakuaika != null) {
            ao.setApplicationStartDate(aoHakuaika.getAlkuPvm());
            ao.setApplicationEndDate(aoHakuaika.getLoppuPvm());
            ao.setInternalASDateRef(aoHakuaika.getHakuaikaId());
        } else if (haku.getHakuaikas() != null && !haku.getHakuaikas().isEmpty()) {
            ao.setApplicationStartDate(haku.getHakuaikas().get(0).getAlkuPvm());
            ao.setApplicationEndDate(haku.getHakuaikas().get(0).getLoppuPvm());
            ao.setApplicationPeriodName(super.getI18nText(haku.getHakuaikas().get(0).getNimet()));
            ao.setInternalASDateRef(haku.getHakuaikas().get(0).getHakuaikaId());
        }

        if (aoHakuaika != null && aoHakuaika.getNimet() != null && !aoHakuaika.getNimet().isEmpty()) {
            ao.setApplicationPeriodName(super.getI18nText(aoHakuaika.getNimet()));
        }

        ao.setAttachmentDeliveryAddress(educationObjectCreator.createAddress(hakukohde.getLiitteidenToimitusOsoite()));

        List<ApplicationOptionAttachment> attachments = Lists.newArrayList();
        if (hakukohde.getHakukohteenLiitteet() != null && !hakukohde.getHakukohteenLiitteet().isEmpty()) {
            for (HakukohdeLiiteV1RDTO liite : hakukohde.getHakukohteenLiitteet()) {

                ApplicationOptionAttachment attach = new ApplicationOptionAttachment();
                attach.setDueDate(liite.getToimitettavaMennessa());
                attach.setUsedInApplicationForm(liite.isKaytetaanHakulomakkeella());
                //attach.setType(koodistoService.searchFirst(liite.getLiitteenTyyppiUri()));
                attach.setType(getTypeText(liite.getLiitteenNimi(), liite.getKieliUri()));
                attach.setDescreption(getI18nText(liite.getLiitteenKuvaukset()));
                attach.setAddress(educationObjectCreator.createAddress(liite.getLiitteenToimitusOsoite()));
                attach.setEmailAddr(liite.getSahkoinenToimitusOsoite());
                attachments.add(attach);
            }
        }
        ao.setAttachments(attachments);
        ao.setAdditionalInfo(getI18nText(hakukohde.getLisatiedot()));
        
        ao.setApplicationOffice(getApplicationOffice(hakukohde.getYhteystiedot()));
        
        return ao;
    }

    private List<String> extractCodeVales(List<Code> teachingLanguages) {
        List<String> vals = new ArrayList<String>();
        for (Code curCode : teachingLanguages) {
            vals.add(curCode.getValue());
        }
        return vals;
    }

    private ApplicationOffice getApplicationOffice(List<YhteystiedotV1RDTO> yhteystiedot) throws KoodistoException {
        if (yhteystiedot == null || yhteystiedot.isEmpty()) {
            return null;
        } else {
            fallbacklang = null;
            langsToBeFallbacked = new ArrayList<String>(Arrays.asList(new String[] { "fi", "sv", "en" }));
            for (YhteystiedotV1RDTO yt : yhteystiedot) {
                yt.setLang(koodistoService.searchFirstCodeValue(yt.getLang()).toLowerCase());
                setFallbacklanguage(yt.getLang());
            }
            I18nText hakutoimistonNimi = getHakutoimistonNimi(yhteystiedot);
            I18nText phone = getPhoneNumber(yhteystiedot);
            I18nText email = getEmail(yhteystiedot);
            I18nText www = getWww(yhteystiedot);
            Address visitingAddress = getLocalizedVisitingAddress(yhteystiedot);
            Address postalAddress = getLocalizedAddress(yhteystiedot);
            return new ApplicationOffice(hakutoimistonNimi, phone, email, www, visitingAddress, postalAddress);
        }
    }

    private I18nText getHakutoimistonNimi(List<YhteystiedotV1RDTO> yhteystiedot) {
        Map<String, String> map = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            map.put(yt.getLang(), yt.getHakutoimistonNimi());
        }
        return getSanitizedI18nText(map);
    }

    private I18nText getWww(List<YhteystiedotV1RDTO> yhteystiedot) {
        Map<String, String> map = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            map.put(yt.getLang(), yt.getWwwOsoite());
        }
        return getSanitizedI18nText(map);
    }

    private I18nText getEmail(List<YhteystiedotV1RDTO> yhteystiedot) {
        Map<String, String> map = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            map.put(yt.getLang(), yt.getSahkopostiosoite());
        }
        return getSanitizedI18nText(map);
    }

    private I18nText getPhoneNumber(List<YhteystiedotV1RDTO> yhteystiedot) {
        Map<String, String> map = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            map.put(yt.getLang(), yt.getPuhelinnumero());
        }
        return getSanitizedI18nText(map);
    }

    private Address getLocalizedAddress(List<YhteystiedotV1RDTO> yhteystiedot) {
        Address a = new Address();
        Map<String, String> streetAddress = new HashMap<String, String>();
        Map<String, String> secondForeignAddr = new HashMap<String, String>();
        Map<String, String> postalCode = new HashMap<String, String>();
        Map<String, String> postOffice = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            if (Osoitemuoto.KANSAINVALINEN.equals(yt.getOsoitemuoto())) {
                streetAddress.put(yt.getLang(), yt.getKansainvalinenOsoite());
            } else {
                streetAddress.put(yt.getLang(), yt.getOsoiterivi1());
                secondForeignAddr.put(yt.getLang(), yt.getOsoiterivi2());
                postOffice.put(yt.getLang(), yt.getPostitoimipaikka());
                postalCode.put(yt.getLang(), yt.getPostinumeroArvo());
            }
        }
        a.setStreetAddress(getSanitizedI18nText(streetAddress));
        a.setSecondForeignAddr(getSanitizedI18nText(secondForeignAddr));
        a.setPostOffice(getSanitizedI18nText(postOffice));
        a.setPostalCode(getSanitizedI18nText(postalCode));
        return a;
    }

    private I18nText getSanitizedI18nText(Map<String, String> translations) {
        for (String key : translations.keySet()) {
            if (translations.get(key) == null)
                translations.put(key, "");
        }
        insertFallbackLanguageValues(translations);
        return new I18nText(translations);
    }

    private Address getLocalizedVisitingAddress(List<YhteystiedotV1RDTO> yhteystiedot) {
        List<YhteystiedotV1RDTO> visitingAddreses = new ArrayList<YhteystiedotV1RDTO>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            YhteystiedotV1RDTO kayntiosoite = yt.getKayntiosoite();
            kayntiosoite.setLang(yt.getLang());
            kayntiosoite.setOsoitemuoto(yt.getOsoitemuoto());
            visitingAddreses.add(kayntiosoite);
        }
        return getLocalizedAddress(visitingAddreses);
    }

    private String fallbacklang;
    private List<String> langsToBeFallbacked;

    private void setFallbacklanguage(String lang) {
        langsToBeFallbacked.remove(lang);
        if (fallbacklang == null || fallbacklang.equals("en") || lang.equals("fi")) {
            fallbacklang = lang;
        }
    }

    private void insertFallbackLanguageValues(Map<String, String> map) {
        if (map.get(fallbacklang) != null) {
            for (String lang : langsToBeFallbacked) {
                map.put(lang, map.get(fallbacklang));
            }
        }
    }

}
