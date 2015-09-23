package fi.vm.sade.koulutusinformaatio.integrationtest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;

import java.util.List;

/**
 * Created by alexis on 23.9.2015.
 */
public class KoodistoServiceMock implements KoodistoService {
    @Override
    public List<Code> search(String koodiUri) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodiUri));
    }

    @Override
    public List<Code> searchMultiple(List<String> koodiUris) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodiUris.get(0)));
    }

    @Override
    public Code searchFirst(String koodiUri) throws KoodistoException {
        return getSampleCode(koodiUri);
    }

    @Override
    public List<Code> searchByKoodisto(String koodistoUri, Integer version) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodistoUri));
    }

    @Override
    public List<I18nText> searchNames(String koodiUri) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodiUri).getName());
    }

    @Override
    public List<I18nText> searchNamesByKoodisto(String koodistoUri, Integer version) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodistoUri).getName());
    }

    @Override
    public List<I18nText> searchNamesMultiple(List<String> koodiUri) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodiUri.get(0)).getName());
    }

    @Override
    public I18nText searchFirstName(String koodiUri) throws KoodistoException {
        return getSampleCode(koodiUri).getName();
    }

    @Override
    public List<I18nText> searchShortNames(String koodiUri) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodiUri).getName());
    }

    @Override
    public List<I18nText> searchShortNamesByKoodisto(String koodistoUri, Integer version) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodistoUri).getName());
    }

    @Override
    public List<I18nText> searchShortNamesMultiple(List<String> koodiUri) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodiUri.get(0)).getName());
    }

    @Override
    public I18nText searchFirstShortName(String koodiUri) throws KoodistoException {
        return getSampleCode(koodiUri).getName();
    }

    @Override
    public String searchFirstCodeValue(String koodiUri) throws KoodistoException {
        return koodiUri;
    }

    @Override
    public List<String> searchCodeValuesMultiple(List<String> koodiUri) throws KoodistoException {
        return koodiUri;
    }

    @Override
    public List<Code> searchSubCodes(String koodiURIAndVersion, String koodistoURI) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodiURIAndVersion));
    }

    @Override
    public List<Code> searchSuperCodes(String koodiURIAndVersion, String koodistoURI) throws KoodistoException {
        return Lists.newArrayList(getSampleCode(koodiURIAndVersion));
    }

    @Override
    public void clearCache() {

    }

    private static Code getSampleCode(String uri) {
        if (uri == null) {
            uri = "test_value1";
        }

        I18nText sampleI18n = new I18nText();
        sampleI18n.put(uri, uri);

        return new Code(uri, sampleI18n, sampleI18n, sampleI18n, uri);
    }
}
