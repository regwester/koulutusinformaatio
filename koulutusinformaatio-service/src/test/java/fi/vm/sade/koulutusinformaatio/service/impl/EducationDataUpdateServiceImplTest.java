package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.DBCollection;
import fi.vm.sade.koulutusinformaatio.converter.KoulutusinformaatioObjectBuilder;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class EducationDataUpdateServiceImplTest {

    private EducationDataUpdateServiceImpl service;
    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private ChildLearningOpportunityInstanceDAO childLearningOpportunityInstanceDAO;
    private ChildLearningOpportunitySpecificationDAO childLearningOpportunitySpecificationDAO;
    private PictureDAO pictureDAO;
    private DBCollection ploCollection;
    private DBCollection aoCollection;
    private DBCollection lopCollection;
    private DBCollection cloiCollection;
    private DBCollection closCollection;

    @Before
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        parentLearningOpportunitySpecificationDAO = mock(ParentLearningOpportunitySpecificationDAO.class);
        ploCollection = mock(DBCollection.class);
        ParentLearningOpportunitySpecificationEntity plo = new ParentLearningOpportunitySpecificationEntity();
        String ploOid = "1.2.3";
        plo.setId(ploOid);
        when(parentLearningOpportunitySpecificationDAO.getCollection()).thenReturn(ploCollection);
        when(parentLearningOpportunitySpecificationDAO.get(eq("1.2.3"))).thenReturn(plo);
        applicationOptionDAO = mock(ApplicationOptionDAO.class);
        aoCollection = mock(DBCollection.class);
        when(applicationOptionDAO.getCollection()).thenReturn(aoCollection);

        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("8.9.0");
        aos.add(ao);
        when(applicationOptionDAO.find(eq("1.1.1"), eq("9.9.9"), eq("1"))).thenReturn(aos);
        learningOpportunityProviderDAO = mock(LearningOpportunityProviderDAO.class);
        lopCollection = mock(DBCollection.class);
        when(learningOpportunityProviderDAO.getCollection()).thenReturn(lopCollection);

        cloiCollection = mock(DBCollection.class);
        childLearningOpportunityInstanceDAO = mock(ChildLearningOpportunityInstanceDAO.class);
        when(childLearningOpportunityInstanceDAO.getCollection()).thenReturn(cloiCollection);

        closCollection = mock(DBCollection.class);
        childLearningOpportunitySpecificationDAO = mock(ChildLearningOpportunitySpecificationDAO.class);
        when(childLearningOpportunitySpecificationDAO.getCollection()).thenReturn(closCollection);

        KoulutusinformaatioObjectBuilder objectBuilder = new KoulutusinformaatioObjectBuilder(modelMapper);

        pictureDAO = mock(PictureDAO.class);

        service = new EducationDataUpdateServiceImpl( modelMapper, parentLearningOpportunitySpecificationDAO,
               applicationOptionDAO, learningOpportunityProviderDAO, childLearningOpportunitySpecificationDAO, childLearningOpportunityInstanceDAO,
                objectBuilder, pictureDAO);
    }

    @Test
    public void testSaveParentLearningOpportunity() {
        Set<ApplicationOption> applicationOptions = Sets.newHashSet();
        Provider lop = new Provider();
        lop.setId("6.7.8");
        ApplicationOption ao = new ApplicationOption();
        ao.setId("3.3.3");
        ao.setProvider(lop);
        applicationOptions.add(ao);
        ParentLOS plo = new ParentLOS();
        plo.setProvider(lop);
        plo.setId("1.2.3");
        plo.setApplicationOptions(applicationOptions);

        ParentLOI parentLOI = new ParentLOI();
        parentLOI.setId("2345");
        parentLOI.setPrerequisite(TestUtil.createI18nText("Peruskoulu", "Peruskoulu", "Peruskoulu"));

        ChildLearningOpportunity clo = new ChildLearningOpportunity();
        clo.setId("5.7.9");
        clo.setApplicationSystemIds(Lists.newArrayList("1.2.3.4.5"));
        clo.setApplicationOptions(Lists.newArrayList(ao));

        parentLOI.setChildren(Lists.newArrayList(clo));

        plo.setLois(Lists.newArrayList(parentLOI));

        service.save(plo);
        verify(parentLearningOpportunitySpecificationDAO, times(1)).save(any(ParentLearningOpportunitySpecificationEntity.class));
        verify(applicationOptionDAO, times(2)).save(any(ApplicationOptionEntity.class));
        verify(learningOpportunityProviderDAO, times(3)).save(any(LearningOpportunityProviderEntity.class));
    }
}
