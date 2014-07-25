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
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class EducationObjectCreator extends ObjectCreator {

    private final OrganisaatioRawService organisaatioRawService;
    private KoodistoService koodistoService;

    protected EducationObjectCreator(KoodistoService koodistoService, OrganisaatioRawService organisaatioRawService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.organisaatioRawService = organisaatioRawService;
    }

    public List<Exam> createVocationalExams(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
        if (valintakoes != null) {
            List<Exam> exams = Lists.newArrayList();
            for (ValintakoeRDTO valintakoe : valintakoes) {
                Exam exam = new Exam();
                exam.setType(koodistoService.searchFirstName(valintakoe.getTyyppiUri()));
                exam.setDescription(getI18nText(valintakoe.getKuvaus()));
                List<ExamEvent> examEvents = Lists.newArrayList();

                for (ValintakoeAjankohtaRDTO valintakoeAjankohta : valintakoe.getValintakoeAjankohtas()) {
                    ExamEvent examEvent = new ExamEvent();
                    examEvent.setAddress(createAddress(valintakoeAjankohta.getOsoite()));
                    examEvent.setDescription(valintakoeAjankohta.getLisatiedot());
                    examEvent.setStart(valintakoeAjankohta.getAlkaa());
                    examEvent.setEnd(valintakoeAjankohta.getLoppuu());
                    examEvents.add(examEvent);
                }
                exam.setExamEvents(examEvents);
                exams.add(exam);
            }
            return exams;
        } else {
            return null;
        }
    }

    public List<Exam> createUpperSecondaryExams(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
        if (valintakoes != null) {
            List<Exam> exams = Lists.newArrayList();
            for (ValintakoeRDTO valintakoe : valintakoes) {
                if (valintakoe.getKuvaus() != null
                        && valintakoe.getValintakoeAjankohtas() != null
                        && !valintakoe.getValintakoeAjankohtas().isEmpty()) {
                    Exam exam = new Exam();
                    exam.setDescription(getI18nText(valintakoe.getKuvaus()));
                    List<ExamEvent> examEvents = Lists.newArrayList();

                    for (ValintakoeAjankohtaRDTO valintakoeAjankohta : valintakoe.getValintakoeAjankohtas()) {
                        ExamEvent examEvent = new ExamEvent();
                        examEvent.setAddress(createAddress(valintakoeAjankohta.getOsoite()));
                        examEvent.setDescription(valintakoeAjankohta.getLisatiedot());
                        examEvent.setStart(valintakoeAjankohta.getAlkaa());
                        examEvent.setEnd(valintakoeAjankohta.getLoppuu());
                        examEvents.add(examEvent);
                    }
                    exam.setExamEvents(examEvents);
                    exam.setScoreLimit(resolvePointLimit(valintakoe, "Paasykoe"));
                    exams.add(exam);
                }
            }

            return exams;
        }
        else {
            return null;
        }
    }

    public AdditionalProof createAdditionalProof(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
        if (valintakoes != null) {
            AdditionalProof additionalProof = new AdditionalProof();
            for (ValintakoeRDTO valintakoe : valintakoes) {
                if (valintakoe.getLisanaytot() != null) {
                    additionalProof.setDescreption(getI18nText(valintakoe.getLisanaytot()));
                    additionalProof.setScoreLimit(resolvePointLimit(valintakoe, "Lisapisteet"));
                    return additionalProof;
                }
            }
        }
        return null;
    }

    public ScoreLimit resolvePointLimit(ValintakoeRDTO valintakoe, String type) {
        for (ValintakoePisterajaRDTO valintakoePisteraja : valintakoe.getValintakoePisterajas()) {
            if (valintakoePisteraja.getTyyppi().equals(type)) {
                return new ScoreLimit(valintakoePisteraja.getAlinPistemaara(),
                        valintakoePisteraja.getAlinHyvaksyttyPistemaara(), valintakoePisteraja.getYlinPistemaara());
            }
        }
        return null;
    }


    public Address createAddress(OsoiteRDTO osoite) throws KoodistoException {
        if (osoite != null) {
            Address attachmentDeliveryAddress = new Address();
            
            Map<String,String> streetAddrTransls = new HashMap<String,String>();
            Map<String,String> streetAddrTransls2 = new HashMap<String,String>();
            Map<String,String> postOfficeTransls = new HashMap<String,String>();
            
            if (osoite.getOsoiterivi1() != null) {
                streetAddrTransls.put("fi", osoite.getOsoiterivi1());
                attachmentDeliveryAddress.setStreetAddress(new I18nText(streetAddrTransls));
            }
            if (osoite.getOsoiterivi2() != null) {
                streetAddrTransls2.put("fi", osoite.getOsoiterivi2());
                attachmentDeliveryAddress.setSecondForeignAddr(new I18nText(streetAddrTransls2));
            }
            attachmentDeliveryAddress.setPostalCode(koodistoService.searchFirstCodeValue(osoite.getPostinumero()));

            if (osoite.getPostitoimipaikka() != null) {
                postOfficeTransls.put("fi", osoite.getPostitoimipaikka());
                attachmentDeliveryAddress.setPostOffice(new I18nText(postOfficeTransls));
            }
            return attachmentDeliveryAddress;
        } else {
            return null;
        }
    }

    public List<Exam> createHigherEducationExams(List<ValintakoeV1RDTO> valintakokeet) throws KoodistoException {
        if (valintakokeet != null && !valintakokeet.isEmpty()) {
            List<Exam> exams = Lists.newArrayList();
            for (ValintakoeV1RDTO valintakoe : valintakokeet) {
                if (valintakoe != null && valintakoe.getValintakokeenKuvaus() != null
                        && valintakoe.getValintakoeAjankohtas() != null
                        && !valintakoe.getValintakoeAjankohtas().isEmpty() && valintakoe.getKieliUri() != null) {
                    Exam exam = new Exam();

                    exam.setType(getTypeText(valintakoe.getValintakoeNimi(), valintakoe.getKieliUri()));
                    exam.setDescription(getI18nTextEnriched(valintakoe.getValintakokeenKuvaus()));
                    List<ExamEvent> examEvents = Lists.newArrayList();

                    for (ValintakoeAjankohtaRDTO valintakoeAjankohta : valintakoe.getValintakoeAjankohtas()) {
                        ExamEvent examEvent = new ExamEvent();
                        examEvent.setAddress(createAddress(valintakoeAjankohta.getOsoite()));
                        examEvent.setDescription(valintakoeAjankohta.getLisatiedot());
                        examEvent.setStart(valintakoeAjankohta.getAlkaa());
                        examEvent.setEnd(valintakoeAjankohta.getLoppuu());
                        examEvents.add(examEvent);
                    }
                    exam.setExamEvents(examEvents);
                    exams.add(exam);
                }
            }
            return exams;
        }
        return null;
    }

    public List<ApplicationOptionAttachment> createApplicationOptionAttachments(List<HakukohdeLiiteDTO> hakukohdeLiiteDTOs) throws KoodistoException {
        if (hakukohdeLiiteDTOs != null) {
            List<ApplicationOptionAttachment> attachments = Lists.newArrayList();
            for (HakukohdeLiiteDTO liite : hakukohdeLiiteDTOs) {
                ApplicationOptionAttachment attach = new ApplicationOptionAttachment();
                attach.setDueDate(liite.getErapaiva());
                attach.setType(koodistoService.searchFirstName(liite.getLiitteenTyyppiUri()));
                attach.setDescreption(getI18nText(liite.getKuvaus()));
                attach.setAddress(createAddress(liite.getToimitusosoite()));
                attachments.add(attach);
            }
            return attachments;
        } else {
            return null;
        }
    }

    private I18nText getI18nTextEnriched(TekstiRDTO valintakokeenKuvaus) {
        if (!Strings.isNullOrEmpty(valintakokeenKuvaus.getArvo()) && !Strings.isNullOrEmpty(valintakokeenKuvaus.getTeksti())) {
            Map<String, String> translations = new HashMap<String, String>();
            translations.put(valintakokeenKuvaus.getArvo().toLowerCase(), valintakokeenKuvaus.getTeksti());
            I18nText text = new I18nText();
            text.setTranslations(translations);
            return text;
        }
        return null;
    }

    public List<OrganizationGroup> createOrganizationGroups(String... organisaatioRyhmaOids) throws ResourceNotFoundException {
        List<OrganizationGroup> groups = new ArrayList<OrganizationGroup>(organisaatioRyhmaOids.length);
        for (int i = 0; i < organisaatioRyhmaOids.length; i++) {
            String oid = organisaatioRyhmaOids[i];
            OrganisaatioRDTO organisaatioRDTO = organisaatioRawService.getOrganisaatio(oid);
            boolean isGroup = false;
            for (String tyyppi : organisaatioRDTO.getTyypit()) {
                if (OrganisaatioTyyppi.RYHMA.value().equals(tyyppi)) {
                    isGroup = true;
                    break;
                }
            }
            if (!isGroup) {
                throw new ResourceNotFoundException("Organization "+oid+" is not group");
            }

            OrganizationGroup group = new OrganizationGroup();
            group.setOid(oid);
            group.setGroupTypes(organisaatioRDTO.getRyhmatyypit());
            group.setUsageGroups(organisaatioRDTO.getKayttoryhmat());
            groups.add(group);
        }
        return groups;
    }
}
