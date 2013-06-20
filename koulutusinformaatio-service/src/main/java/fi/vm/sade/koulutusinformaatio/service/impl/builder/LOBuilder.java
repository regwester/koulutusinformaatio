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

package fi.vm.sade.koulutusinformaatio.service.impl.builder;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.WebApplicationException;
import java.util.*;

/**
 * Builds learning opportunity instances.
 *
 * @author Hannu Lyytikainen
 */
public class LOBuilder {

    public static final Logger LOG = LoggerFactory.getLogger(LOBuilder.class);

    public static final String MODULE_TYPE_PARENT = "TUTKINTO";
    public static final String MODULE_TYPE_CHILD = "TUTKINTO_OHJELMA";
    public static final String STATE_PUBLISHED = "JULKAISTU";
    public static final String STATE_READY = "VALMIS";
    public static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";


    private KomoResource komoResource;
    private KomotoResource komotoResource;
    private HakukohdeResource hakukohdeResource;
    private ProviderService providerService;
    //private ConversionService conversionService;

    @Autowired
    private KoodistoService koodistoService;

    public LOBuilder(KomoResource komoResource, KomotoResource komotoResource, HakukohdeResource hakukohdeResource,
                     ProviderService providerService) {
        this.komoResource = komoResource;
        this.komotoResource = komotoResource;
        this.hakukohdeResource = hakukohdeResource;
        this.providerService = providerService;
    }

    public List<ParentLOS> buildParentLOSs(String oid) throws TarjontaParseException, KoodistoException, WebApplicationException {
        // parents

        List<ParentLOS> parentLOSs = Lists.newArrayList();

        KomoDTO parentKomo = komoResource.getByOID(oid);

        validateParentKomo(parentKomo);

        List<OidRDTO> parentKomotoOids = komoResource.getKomotosByKomoOID(parentKomo.getOid(), Integer.MAX_VALUE, 0);

        if (parentKomotoOids == null || parentKomotoOids.size() == 0) {
            throw new TarjontaParseException("No instances found in parent LOS " + parentKomo.getOid());
        }

        ArrayListMultimap<String, KomotoDTO> parentKomotosByProviderId = ArrayListMultimap.create();

        for (OidRDTO parentKomotoOid : parentKomotoOids) {

            KomotoDTO parentKomoto = komotoResource.getByOID(parentKomotoOid.getOid());
            try {
                validateParentKomoto(parentKomoto);
            } catch (TarjontaParseException e) {
                continue;
            }
            parentKomotosByProviderId.put(parentKomoto.getTarjoajaOid(), parentKomoto);
        }

        for (String key : parentKomotosByProviderId.keySet()) {
            parentLOSs.add(constructParentLOS(parentKomo, key, parentKomotosByProviderId.get(key)));
        }


        // children

        // parent loi id -> List<ChildLearningOpportunity>
        ArrayListMultimap<String, ChildLearningOpportunity> childLOsByParentLOIId = ArrayListMultimap.create();

        List<String> childKomoIds = parentKomo.getAlaModuulit();
        for (String childKomoId : childKomoIds) {
            KomoDTO childKomo = komoResource.getByOID(childKomoId);

            try {
                validateChildKomo(childKomo);
            } catch (TarjontaParseException e) {
                continue;
            }


            List<OidRDTO> childKomotoOIds = komoResource.getKomotosByKomoOID(childKomoId, Integer.MAX_VALUE, 0);
            for (OidRDTO childKomotoOId : childKomotoOIds) {
                KomotoDTO childKomoto = komotoResource.getByOID(childKomotoOId.getOid());

                try {
                    validateChildKomoto(childKomoto);
                } catch (TarjontaParseException e) {
                    continue;
                }

                ChildLearningOpportunity childLO = new ChildLearningOpportunity();
                childLO.setId(childKomo.getOid());
                childLO.setName(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
                childLO.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
                childLO.setDegreeTitle(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
                childLO.setDegreeGoal(getI18nText(childKomo.getTavoitteet()));
                childLO.setStartDate(childKomoto.getKoulutuksenAlkamisDate());
                childLO.setFormOfEducation(koodistoService.searchMultiple(childKomoto.getKoulutuslajiUris()));
                childLO.setWebLinks(childKomoto.getWebLinkkis());
                childLO.setTeachingLanguages(koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));
                childLO.setFormOfTeaching(koodistoService.searchMultiple(childKomoto.getOpetusmuodotUris()));
                childLO.setPrerequisite(koodistoService.searchFirst(childKomoto.getPohjakoulutusVaatimusUri()));
                childLO.setProfessionalTitles(koodistoService.searchMultiple(childKomoto.getAmmattinimikeUris()));
                childLO.setWorkingLifePlacement(getI18nText(childKomoto.getSijoittuminenTyoelamaan()));
                childLO.setInternationalization(getI18nText(childKomoto.getKansainvalistyminen()));
                childLO.setCooperation(getI18nText(childKomoto.getYhteistyoMuidenToimijoidenKanssa()));

                List<ApplicationOption> applicationOptions = Lists.newArrayList();
                List<String> applicationSystemIds = Lists.newArrayList();
                List<OidRDTO> aoIdDTOs = komotoResource.getHakukohdesByKomotoOID(childKomotoOId.getOid());

                for (OidRDTO aoIdDTO : aoIdDTOs) {

                    // application option
                    String aoId = aoIdDTO.getOid();
                    HakukohdeDTO hakukohdeDTO = hakukohdeResource.getByOID(aoId);
                    ApplicationOption ao = new ApplicationOption();

                    //ao.setParent(parentRef);

                    ao.setId(hakukohdeDTO.getOid());
                    ao.setName(koodistoService.searchFirst(hakukohdeDTO.getHakukohdeNimiUri()));
                    ao.setAoIdentifier(koodistoService.searchFirstCodeValue(hakukohdeDTO.getHakukohdeNimiUri()));
                    ao.setStartingQuota(hakukohdeDTO.getAloituspaikatLkm());
                    ao.setLowestAcceptedScore(hakukohdeDTO.getAlinValintaPistemaara());
                    ao.setLowestAcceptedAverage(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo());
                    ao.setAttachmentDeliveryDeadline(hakukohdeDTO.getLiitteidenToimitusPvm());
                    ao.setLastYearApplicantCount(hakukohdeDTO.getEdellisenVuodenHakijatLkm());
                    ao.setSelectionCriteria(getI18nText(hakukohdeDTO.getValintaperustekuvaus()));

                    List<Code> subCodes = koodistoService.searchSubCodes(childKomoto.getPohjakoulutusVaatimusUri(),
                            BASE_EDUCATION_KOODISTO_URI);
                    List<String> baseEducations = Lists.transform(subCodes, new Function<Code, String>() {
                        @Override
                        public String apply(Code code) {
                            return code.getValue();
                        }
                    });
                    ao.setRequiredBaseEducations(baseEducations);

                    HakuDTO hakuDTO = hakukohdeResource.getHakuByHakukohdeOID(aoId);
                    ApplicationSystem as = new ApplicationSystem();
                    as.setId(hakuDTO.getOid());
                    as.setName(getI18nText(hakuDTO.getNimi()));
                    if (hakuDTO.getHakuaikas() != null) {
                        for (HakuaikaRDTO ha : hakuDTO.getHakuaikas()) {
                            DateRange range = new DateRange();
                            range.setStartDate(ha.getAlkuPvm());
                            range.setEndDate(ha.getLoppuPvm());
                            as.getApplicationDates().add(range);
                        }
                    }

                    ao.setApplicationSystem(as);

                    if (!Strings.isNullOrEmpty(hakukohdeDTO.getSoraKuvausKoodiUri())) {
                        ao.setSora(true);
                    }

                    //education degree code value
                    ao.setEducationDegree(koodistoService.searchFirstCodeValue(childKomoto.getKoulutusAsteUri()));
                    //set teaching language codes
                    ao.setTeachingLanguages(koodistoService.searchCodeValuesMultiple(childKomoto.getOpetuskieletUris()));
                    ao.setPrerequisite(childLO.getPrerequisite());

                    // set child loi names to application option
                    List<OidRDTO> komotosByHakukohdeOID = hakukohdeResource.getKomotosByHakukohdeOID(aoId);

                    for (OidRDTO s : komotosByHakukohdeOID) {
                        KomoDTO komoByKomotoOID = komotoResource.getKomoByKomotoOID(s.getOid());
                        ChildLORef cRef = new ChildLORef();
                        cRef.setChildLOId(s.getOid());
                        cRef.setName(koodistoService.searchFirst(komoByKomotoOID.getKoulutusOhjelmaKoodiUri()));
                        cRef.setQualification(koodistoService.searchFirst(komoByKomotoOID.getTutkintonimikeUri()));
                        cRef.setPrerequisite(childLO.getPrerequisite());
                        ao.getChildLORefs().add(cRef);
                    }

                    applicationOptions.add(ao);
                    applicationSystemIds.add(hakuDTO.getOid());
                }

                childLO.setApplicationOptions(applicationOptions);
                childLO.setApplicationSystemIds(applicationSystemIds);

                childLOsByParentLOIId.put(childKomoto.getParentKomotoOid(), childLO);
            }
        }

        // parent loss
        for (ParentLOS parentLOS : parentLOSs) {
            // parent lois
            for (ParentLOI parentLOI : parentLOS.getLois()) {
                // add children to parent loi
                List<ChildLearningOpportunity> children = childLOsByParentLOIId.get(parentLOI.getId());
                for (ChildLearningOpportunity child : children) {
                    // add provider to ao + as id to provider
                    for (ApplicationOption ao : child.getApplicationOptions()) {
                        ao.setProvider(parentLOS.getProvider());
                        ao.setParent(new ParentLORef(parentLOS.getId(), parentLOS.getName()));
                        parentLOS.getProvider().getApplicationSystemIDs().add(ao.getApplicationSystem().getId());
                    }
                    parentLOS.getApplicationOptions().addAll(child.getApplicationOptions());
                }
                parentLOI.setChildren(children);
            }
        }

        return parentLOSs;
    }

    private ParentLOS constructParentLOS(KomoDTO parentKomo, String providerId, List<KomotoDTO> parentKomotos) throws KoodistoException {

        ParentLOS parentLOS = new ParentLOS();

        // parent info
        parentLOS.setId(new StringBuilder().append(parentKomo.getOid()).append("_").append(providerId).toString());
        parentLOS.setName(koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri()));
        parentLOS.setStructureDiagram(getI18nText(parentKomo.getKoulutuksenRakenne()));
        parentLOS.setAccessToFurtherStudies(getI18nText(parentKomo.getJatkoOpintoMahdollisuudet()));
        //parentLOS.setAccessToFurtherStudies(getI18nText(parentKomo.getK));
        parentLOS.setGoals(getI18nText(parentKomo.getTavoitteet()));
        parentLOS.setEducationDomain(koodistoService.searchFirst(parentKomo.getKoulutusAlaUri()));
        parentLOS.setStydyDomain(koodistoService.searchFirst(parentKomo.getOpintoalaUri()));
        parentLOS.setEducationDegree(koodistoService.searchFirstCodeValue(parentKomo.getKoulutusAsteUri()));

        Provider provider = providerService.getByOID(providerId);
        parentLOS.setProvider(provider);

        List<ParentLOI> lois = Lists.newArrayList();

        for (KomotoDTO komoto : parentKomotos) {
            ParentLOI loi = new ParentLOI();
            loi.setId(komoto.getOid());
            loi.setPrerequisite(koodistoService.searchFirst(komoto.getPohjakoulutusVaatimusUri()));
            lois.add(loi);
        }
        parentLOS.setLois(lois);
        return parentLOS;
    }

    private I18nText getI18nText(final Map<String, String> texts) throws KoodistoException {
        if (texts != null && !texts.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> i = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    String key = koodistoService.searchFirstCodeValue(entry.getKey());
                    translations.put(key.toLowerCase(), entry.getValue());
                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
    }

    private void validateParentKomo(KomoDTO komo) throws TarjontaParseException {

        // tmp parent check
        if (!komo.getModuuliTyyppi().equals(MODULE_TYPE_PARENT)) {
            throw new TarjontaParseException("LOS not of type " + MODULE_TYPE_PARENT);
        }

        // published
        if (!komo.getTila().equals(STATE_PUBLISHED)) {
            throw new TarjontaParseException("LOS state not " + STATE_PUBLISHED);
        }

        if (komo.getNimi() == null) {
            //throw new TarjontaParseException("KomoDTO name is null");
            Map<String, String> name = Maps.newHashMap();
            name.put("fi", "fi dummy name");
            name.put("sv", "sv dummy name");
            komo.setNimi(name);
        }
    }

    private void validateParentKomoto(KomotoDTO komoto) throws TarjontaParseException {
//        if (!komoto.getTila().equals(STATE_PUBLISHED) && !komoto.getTila().equals(STATE_READY)) {
//            throw new TarjontaParseException("LOI " + komoto.getOid() + " not of type " + MODULE_TYPE_PARENT);
//        }

    }

    private void validateChildKomo(KomoDTO komo) throws TarjontaParseException {
        if (!komo.getTila().equals(STATE_PUBLISHED)) {
            throw new TarjontaParseException("LOS " + komo.getOid() + " not of type " + MODULE_TYPE_PARENT);
        }
        if (komo.getNimi() == null) {
            throw new TarjontaParseException("Child KomoDTO nimi is null");
        }
        if (komo.getTutkintonimikeUri() == null) {
            throw new TarjontaParseException("Child KomoDTO tutkinto nimike uri is null");
        }
        if (komo.getKoulutusOhjelmaKoodiUri() == null) {
            throw new TarjontaParseException("Child KomoDTO koulutusohjelma koodi uri is null");
        }
    }

    private void validateChildKomoto(KomotoDTO komoto) throws TarjontaParseException {
        if (!komoto.getTila().equals(STATE_PUBLISHED)) {
            throw new TarjontaParseException("LOI " + komoto.getOid() + " not of type " + MODULE_TYPE_PARENT);
        }

    }


    ////////////////////////////////////////////////
    // parent loi fix

//    public List<ParentLOS> postProcess(ParentLOS p) {
//
//        List<ParentLOS> nParents = Lists.newArrayList();
//
//        // parent loi id -> childlos
//        Map<String, List<ChildLOS>> childMap = Maps.newHashMap();
//
//        for (ChildLOS clos : p.getChildren()) {
//            for (ChildLOI cloi : clos.getChildLOIs()) {
//
//                ChildLOS nclos = new ChildLOS();
//                nclos.setId(clos.getId() + "_" + cloi.getId());
//                nclos.setName(clos.getName());
//                nclos.setDegreeTitle(clos.getDegreeTitle());
//                nclos.setQualification(clos.getQualification());
//                nclos.setDegreeGoal(clos.getDegreeGoal());
//
//                nclos.setChildLOIs(Lists.newArrayList(cloi));
//
//                List<ChildLOS> l = childMap.get(cloi.getParentLOI());
//                if (l == null) {
//                    l = Lists.newArrayList(nclos);
//                } else {
//                    l.add(nclos);
//                }
//
//                childMap.put(cloi.getParentLOI(), l);
//
//            }
//        }
//
//
//        for (ParentLOI loi : p.getLois()) {
//            ParentLOS np = new ParentLOS();
//            np.setId(loi.getId());
//            np.setName(p.getName());
//            np.setStructureDiagram(p.getStructureDiagram());
//            np.setAccessToFurtherStudies(p.getAccessToFurtherStudies());
//            //np.setDegreeProgramSelection(p.getDegreeProgramSelection());
//            np.setGoals(p.getGoals());
//            np.setEducationDomain(p.getEducationDomain());
//            np.setStydyDomain(p.getStydyDomain());
//            np.setEducationDegree(p.getEducationDegree());
//
//            Provider provider = loi.getProvider();
//            List<ChildLOS> children = childMap.get(loi.getId());
//
//            if (children != null) {
//                np.setChildren(children);
//
//                // asid to provider
//                // ao to parent
//                for (ChildLOS childLOS : children) {
//                    for (ChildLOI childLOI : childLOS.getChildLOIs()) {
//                        provider.getApplicationSystemIDs().add(childLOI.getApplicationSystemId());
//
//
//                        np.getApplicationOptions().add(childLOI.getApplicationOption());
//
//                        for (ApplicationOption ao : np.getApplicationOptions()) {
//                            ao.setProvider(provider);
//                        }
//
//
//                    }
//                }
//            }
//
//            np.setProvider(provider);
//            np.setLois(Lists.newArrayList(loi));
//
//            nParents.add(np);
//        }
//
//        return nParents;
//    }


}
