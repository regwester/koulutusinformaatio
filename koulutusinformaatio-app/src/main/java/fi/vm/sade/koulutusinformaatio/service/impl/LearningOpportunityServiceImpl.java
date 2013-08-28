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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.converter.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
public class LearningOpportunityServiceImpl implements LearningOpportunityService {

    private EducationDataQueryService educationDataQueryService;
    private ModelMapper modelMapper;
    private static final String LANG_FI = "fi";

    @Autowired
    public LearningOpportunityServiceImpl(EducationDataQueryService educationDataQueryService, ModelMapper modelMapper) {
        this.educationDataQueryService = educationDataQueryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId) throws ResourceNotFoundException {
        ParentLOS parentLOS = educationDataQueryService.getParentLearningOpportunity(parentId);
        String lang = resolveDefaultLanguage(parentLOS);
        return ParentLOSToDTO.convert(parentLOS, lang, lang);
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String uiLang) throws ResourceNotFoundException {
        ParentLOS parentLOS = educationDataQueryService.getParentLearningOpportunity(parentId);
        String lang = resolveDefaultLanguage(parentLOS);
        return ParentLOSToDTO.convert(parentLOS, lang, uiLang);
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang, String uiLang) throws ResourceNotFoundException {
        ParentLOS parentLOS = educationDataQueryService.getParentLearningOpportunity(parentId);
        return ParentLOSToDTO.convert(parentLOS, lang, uiLang);
    }

    @Override
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId) throws ResourceNotFoundException {
        ChildLOS childLOS = educationDataQueryService.getChildLearningOpportunity(cloId);
        String lang = resolveDefaultLanguage(childLOS.getLois().get(0));
        return ChildLOSToDTO.convert(childLOS, lang, lang);
    }

    @Override
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId, String uiLang) throws ResourceNotFoundException {
        ChildLOS childLOS = educationDataQueryService.getChildLearningOpportunity(cloId);
        String lang = resolveDefaultLanguage(childLOS.getLois().get(0));
        return ChildLOSToDTO.convert(childLOS, lang, uiLang);
    }

    @Override
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId, String lang, String uiLang) throws ResourceNotFoundException {
        ChildLOS childLO = educationDataQueryService.getChildLearningOpportunity(cloId);
        return ChildLOSToDTO.convert(childLO, lang, uiLang);
    }

    @Override
    public List<ApplicationOptionSearchResultDTO> searchApplicationOptions(String asId, String lopId, String baseEducation) {
        List<ApplicationOption> applicationOptions = educationDataQueryService.findApplicationOptions(asId, lopId, baseEducation);
        return Lists.transform(applicationOptions, new Function<ApplicationOption, ApplicationOptionSearchResultDTO>() {
            @Override
            public ApplicationOptionSearchResultDTO apply(ApplicationOption applicationOption) {
                return ApplicationOptionToSearchResultDTO.convert(applicationOption, LANG_FI);
            }
        });
    }

    @Override
    public ApplicationOptionDTO getApplicationOption(String aoId, String lang, String uiLang) throws ResourceNotFoundException {
        ApplicationOption ao = educationDataQueryService.getApplicationOption(aoId);
        return ApplicationOptionToDTO.convert(ao, lang, uiLang);
    }

    @Override
    public List<ApplicationOptionDTO> getApplicationOptions(List<String> aoId, final String lang, final String uiLang) throws InvalidParametersException {
        List<ApplicationOption> applicationOptions = educationDataQueryService.getApplicationOptions(aoId);
        return Lists.transform(applicationOptions, new Function<ApplicationOption, ApplicationOptionDTO>() {
            @Override
            public ApplicationOptionDTO apply(ApplicationOption applicationOption) {
                return ApplicationOptionToDTO.convert(applicationOption, lang, uiLang);
            }
        });
    }

    @Override
    public List<BasketItemDTO> getBasketItems(List<String> aoId, String uiLang) throws InvalidParametersException {
        List<ApplicationOption> applicationOptions = educationDataQueryService.getApplicationOptions(aoId);
        return ApplicationOptionsToBasketItemDTOs.convert(applicationOptions, uiLang);
    }

    @Override
    public Date getLastDataUpdated() {
        return educationDataQueryService.getLastUpdated();
    }

    @Override
    public PictureDTO getPicture(String id) throws ResourceNotFoundException {
        Picture pic = educationDataQueryService.getPicture(id);
        return modelMapper.map(pic, PictureDTO.class);
    }


    private String resolveDefaultLanguage(final ParentLOS parentLO) {
        if (parentLO.getName() == null || parentLO.getName().getTranslations() == null || parentLO.getName().getTranslations().containsKey(LANG_FI)) {
            return LANG_FI;
        } else {
            return parentLO.getName().getTranslations().keySet().iterator().next();
        }
    }

    private String resolveDefaultLanguage(final ChildLOI childLOI) {
        if (childLOI.getTeachingLanguages() == null || childLOI.getTeachingLanguages().isEmpty()) {
            return LANG_FI;
        } else {
            for (Code code : childLOI.getTeachingLanguages()) {
                 if (code.getValue().equalsIgnoreCase(LANG_FI)) {
                     return LANG_FI;
                 }
            }
            return childLOI.getTeachingLanguages().get(0).getValue().toLowerCase();
        }
    }
}
