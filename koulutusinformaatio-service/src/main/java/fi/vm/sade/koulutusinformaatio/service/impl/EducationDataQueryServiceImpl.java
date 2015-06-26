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

package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.dao.AdultUpperSecondaryLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.ChildLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.SpecialLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.UpperSecondaryLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.AdultUpperSecondaryLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.CompetenceBasedQualificationParentLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.DataStatusEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.PictureEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.SpecialLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.TutkintoLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.UpperSecondaryLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataQueryServiceImpl implements EducationDataQueryService {

    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private DataStatusDAO dataStatusDAO;
    private ModelMapper modelMapper;
    private PictureDAO pictureDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO;
    private HigherEducationLOSDAO higherEducationLOSDAO;
    private AdultUpperSecondaryLOSDAO adultUpperSecondaryLOSDAO;
    private AdultVocationalLOSDAO adultVocationalLOSDAO;
    private KoulutusLOSDAO koulutusLOSDAO;
    private TutkintoLOSDAO tutkintoLOSDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @Autowired
    public EducationDataQueryServiceImpl(
            ApplicationOptionDAO applicationOptionDAO, ModelMapper modelMapper,
            ChildLearningOpportunityDAO childLearningOpportunityDAO,
            DataStatusDAO dataStatusDAO, PictureDAO pictureDAO,
            UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO,
            SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO, 
            HigherEducationLOSDAO higherEducationLOSDAO, 
            AdultUpperSecondaryLOSDAO adultUpperSecondaryLOSDAO,
            AdultVocationalLOSDAO adultVocationalLOSDAO,
            KoulutusLOSDAO koulutusLOSDAO,
            TutkintoLOSDAO tutkintoLOSDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderDAO) {
        this.applicationOptionDAO = applicationOptionDAO;
        this.modelMapper = modelMapper;
        this.childLearningOpportunityDAO = childLearningOpportunityDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
        this.upperSecondaryLearningOpportunitySpecificationDAO = upperSecondaryLearningOpportunitySpecificationDAO;
        this.specialLearningOpportunitySpecificationDAO = specialLearningOpportunitySpecificationDAO;
        this.higherEducationLOSDAO = higherEducationLOSDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.adultUpperSecondaryLOSDAO = adultUpperSecondaryLOSDAO;
        this.koulutusLOSDAO = koulutusLOSDAO;
        this.tutkintoLOSDAO = tutkintoLOSDAO;
        this.adultVocationalLOSDAO = adultVocationalLOSDAO;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<ApplicationOption> findApplicationOptions(String asId, String lopId, String baseEducation,
            boolean vocational, boolean nonVocational) {
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.findFromSecondary(asId, lopId, baseEducation,
                vocational, nonVocational);
        return Lists.transform(applicationOptions, new Function<ApplicationOptionEntity, ApplicationOption>() {
            @Override
            public ApplicationOption apply(ApplicationOptionEntity applicationOptionEntity) {
                return modelMapper.map(applicationOptionEntity, ApplicationOption.class);
            }
        });
    }

    @Override
    public List<ApplicationOption> getApplicationOptions(List<String> aoIds) throws InvalidParametersException {
        if (aoIds == null || aoIds.isEmpty()) {
            throw new InvalidParametersException("Application option IDs required");
        }
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.findFromSecondary(aoIds);
        return Lists.transform(applicationOptions, new Function<ApplicationOptionEntity, ApplicationOption>() {
            @Override
            public ApplicationOption apply(ApplicationOptionEntity applicationOptionEntity) {
                return modelMapper.map(applicationOptionEntity, ApplicationOption.class);
            }
        });
    }

    @Override
    public ApplicationOption getApplicationOption(String aoId) throws ResourceNotFoundException {
        ApplicationOptionEntity ao = applicationOptionDAO.get(aoId);
        if (ao != null) {
            return modelMapper.map(ao, ApplicationOption.class);
        } else {
            throw new ResourceNotFoundException("Application option not found: " + aoId);
        }
    }

    @Override
    public ChildLOS getChildLearningOpportunity(String childLoId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity childLO = getChildLO(childLoId);
        return modelMapper.map(childLO, ChildLOS.class);
    }

    @Override
    public DataStatus getLatestDataStatus() {
        DataStatusEntity status = dataStatusDAO.getLatest();
        if (status != null) {
            return modelMapper.map(status, DataStatus.class);
        } else {
            return null;
        }
    }

    @Override
    public Picture getPicture(String id) throws ResourceNotFoundException {
        PictureEntity picture = pictureDAO.getFromSecondary(id);
        if (picture != null) {
            return modelMapper.map(picture, Picture.class);
        } else {
            throw new ResourceNotFoundException("Picture not found: " + id);
        }
    }

    @Override
    public UpperSecondaryLOS getUpperSecondaryLearningOpportunity(String id) throws ResourceNotFoundException {
        
        UpperSecondaryLearningOpportunitySpecificationEntity entity =
                upperSecondaryLearningOpportunitySpecificationDAO.getFromSecondary(id);
        
        
        if (entity != null) {
            return modelMapper.map(entity, UpperSecondaryLOS.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Upper secondary learning opportunity specification not found: %s", id));
        }
    }

    @Override
    public SpecialLOS getSpecialLearningOpportunity(String id) throws ResourceNotFoundException {
        SpecialLearningOpportunitySpecificationEntity entity =
                specialLearningOpportunitySpecificationDAO.getFromSecondary(id);
        if (entity != null) {
            return modelMapper.map(entity, SpecialLOS.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Special learning opportunity specification not found: %s", id));
        }
    }

    @Override
    public HigherEducationLOS getHigherEducationLearningOpportunity(String oid) throws ResourceNotFoundException {
        HigherEducationLOSEntity entity = this.higherEducationLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, HigherEducationLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("University of applied science learning opportunity specification not found: %s", oid));
        }
    }
    
    @Override
    public AdultUpperSecondaryLOS getAdultUpperSecondaryLearningOpportunity(String oid) throws ResourceNotFoundException {
        AdultUpperSecondaryLOSEntity entity = this.adultUpperSecondaryLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, AdultUpperSecondaryLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("University of applied science learning opportunity specification not found: %s", oid));
        }
    }
    
    @Override
    public KoulutusLOS getKoulutusLearningOpportunity(String oid) throws ResourceNotFoundException {
        KoulutusLOSEntity entity = this.koulutusLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, KoulutusLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("Koulutus learning opportunity specification not found: %s", oid));
        }
    }
    
    @Override
    public TutkintoLOS getTutkintoLearningOpportunity(String oid) throws ResourceNotFoundException {
        TutkintoLOSEntity entity = this.tutkintoLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, TutkintoLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("Tutkinto learning opportunity specification not found: %s", oid));
        }
    }

    @Override
    public CompetenceBasedQualificationParentLOS getAdultVocationalLearningOpportunity(String oid) throws ResourceNotFoundException {
        CompetenceBasedQualificationParentLOSEntity entity = this.adultVocationalLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, CompetenceBasedQualificationParentLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("University of applied science learning opportunity specification not found: %s", oid));
        }
    }

    @Override
    public Provider getProvider(String id) throws ResourceNotFoundException {
        LearningOpportunityProviderEntity entity = learningOpportunityProviderDAO.get(id);
        if (entity != null) {
            return modelMapper.map(entity, Provider.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Learning opportunity provider not found: %s", id));
        }
    }

    @Override
    public List<LOS> findLearningOpportunitiesByProviderId(String providerId) {
        List<LOS> results = Lists.newArrayList();
        List<ParentLearningOpportunitySpecificationEntity> parentEntites =
                parentLearningOpportunitySpecificationDAO.findByProviderId(providerId);
        List<ParentLOS> parents = Lists.transform(
                parentEntites,
                new Function<ParentLearningOpportunitySpecificationEntity, ParentLOS>() {
                    @Override
                    public ParentLOS apply(ParentLearningOpportunitySpecificationEntity input) {
                        return modelMapper.map(input, ParentLOS.class);
                    }
                }
                );
        List<ChildLOS> children = Lists.newArrayList();
        for (ParentLearningOpportunitySpecificationEntity parentEntity : parentEntites) {
            children.addAll(Lists.transform(
                    parentEntity.getChildren(),
                    new Function<ChildLearningOpportunitySpecificationEntity, ChildLOS>() {
                        @Override
                        public ChildLOS apply(ChildLearningOpportunitySpecificationEntity input) {
                            return modelMapper.map(input, ChildLOS.class);
                        }
                    }
                    ));
        }
        List<UpperSecondaryLOS> upsecs = Lists.transform(
                upperSecondaryLearningOpportunitySpecificationDAO.findByProviderId(providerId),
                new Function<UpperSecondaryLearningOpportunitySpecificationEntity, UpperSecondaryLOS>() {
                    @Override
                    public UpperSecondaryLOS apply(UpperSecondaryLearningOpportunitySpecificationEntity input) {
                        return modelMapper.map(input, UpperSecondaryLOS.class);
                    }
                }
                );
        List<SpecialLOS> specials = Lists.transform(
                specialLearningOpportunitySpecificationDAO.findByProviderId(providerId),
                new Function<SpecialLearningOpportunitySpecificationEntity, SpecialLOS>() {
                    @Override
                    public SpecialLOS apply(SpecialLearningOpportunitySpecificationEntity input) {
                        return modelMapper.map(input, SpecialLOS.class);
                    }
                }
                );

        List<HigherEducationLOS> higherEds = Lists.transform(higherEducationLOSDAO.findByProviderId(providerId),
                new Function<HigherEducationLOSEntity, HigherEducationLOS>() {
                    @Override
                    public HigherEducationLOS apply(HigherEducationLOSEntity input) {
                        return modelMapper.map(input, HigherEducationLOS.class);
                    }
                });

        results.addAll(parents);
        results.addAll(children);
        results.addAll(upsecs);
        results.addAll(specials);
        results.addAll(higherEds);
        return results;
    }

    private ChildLearningOpportunitySpecificationEntity getChildLO(String childLoId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity clo = childLearningOpportunityDAO.getFromSecondary(childLoId);
        if (clo == null) {
            throw new ResourceNotFoundException("Child learning opportunity specification not found: " + childLoId);
        }
        return clo;
    }

    @Override
    public LOS getLos(String losId) {
        
        ParentLearningOpportunitySpecificationEntity losE = this.parentLearningOpportunitySpecificationDAO.get(losId);
        if (losE != null) {
            return modelMapper.map(losE, ParentLOS.class);
        }
        ChildLearningOpportunitySpecificationEntity childE = this.childLearningOpportunityDAO.get(losId);
        if (childE != null) {
            return modelMapper.map(childE, ChildLOS.class);
        }
        UpperSecondaryLearningOpportunitySpecificationEntity upsecE = this.upperSecondaryLearningOpportunitySpecificationDAO.get(losId);
        if (upsecE != null) {
            return modelMapper.map(upsecE, UpperSecondaryLOS.class);
        }
        SpecialLearningOpportunitySpecificationEntity specialLosE = this.specialLearningOpportunitySpecificationDAO.get(losId);
        if (specialLosE != null) {
            return modelMapper.map(specialLosE, SpecialLOS.class);
        }
        
        HigherEducationLOSEntity higherEdE = this.higherEducationLOSDAO.get(losId);
        if (higherEdE != null) {
            return modelMapper.map(higherEdE, HigherEducationLOS.class);
        }
        
        return null;
    }

    @Override
    public List<LOS> findLearningOpportunitiesByLoiId(String loiId) {
        
        
        List<ChildLearningOpportunitySpecificationEntity> childrenE = this.childLearningOpportunityDAO.findByLoiId(loiId);
        if (childrenE != null) {
            
            
            return Lists.transform(
                    childrenE,
                    new Function<ChildLearningOpportunitySpecificationEntity, LOS>() {
                        @Override
                        public LOS apply(ChildLearningOpportunitySpecificationEntity input) {
                            return modelMapper.map(input, ChildLOS.class);
                        }
                    }
                    );
            
        }
        
        List<SpecialLearningOpportunitySpecificationEntity> specialsE = this.specialLearningOpportunitySpecificationDAO.findByLoiId(loiId);
        if (specialsE != null) {
            return Lists.transform(
                    specialsE,
                    new Function<SpecialLearningOpportunitySpecificationEntity, LOS>() {
                        @Override
                        public LOS apply(SpecialLearningOpportunitySpecificationEntity input) {
                            return modelMapper.map(input, SpecialLOS.class);
                        }
                    }
                    );
        }
        
        List<UpperSecondaryLearningOpportunitySpecificationEntity> upsecsE = this.upperSecondaryLearningOpportunitySpecificationDAO.findByLoiId(loiId);
        if (upsecsE != null) {
            return Lists.transform(
                    upsecsE,
                    new Function<UpperSecondaryLearningOpportunitySpecificationEntity, LOS>() {
                        @Override
                        public LOS apply(UpperSecondaryLearningOpportunitySpecificationEntity input) {
                            return modelMapper.map(input, UpperSecondaryLOS.class);
                        }
                    }
                    );
        }
        
        HigherEducationLOSEntity higheredE = this.higherEducationLOSDAO.get(loiId);
        if (higheredE != null) {
            List<LOS> losses = new ArrayList<LOS>();
            losses.add(modelMapper.map(higheredE, HigherEducationLOS.class));
            return losses;
        }
        
        
        
        return null;
    }
    
    @Override
    public DataStatus getLatestSuccessDataStatus() {
        
        DataStatusEntity dataStatusE = this.dataStatusDAO.getLatestSuccess();
        if (dataStatusE != null) {
            return modelMapper.map(dataStatusE, DataStatus.class);
        } else {
            return null;
        }
    }


}
