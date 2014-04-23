package fi.vm.sade.koulutusinformaatio.service.impl.query;


import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderQueryTest {

    private final String TERM = "term";
    private final String AS_ID = "asId";
    private final String BASE_EDUCATION = "base_education";
    private final String TYPE = "provider_type";

    @Test
    public void testProviderQuery() {
        ProviderQuery pq = new ProviderQuery(TERM, AS_ID, BASE_EDUCATION, 0, 100, true, true, "fi", false, TYPE);
        assertEquals("name_fi:" + TERM + "*", pq.getQuery());
        assertEquals(3, pq.getFilterQueries().length);
        assertEquals("asIds:" + AS_ID, pq.getFilterQueries()[0]);
        assertEquals("requiredBaseEducations:" + BASE_EDUCATION, pq.getFilterQueries()[1]);
        assertEquals(String.format("%s:%s", SolrUtil.ProviderFields.TYPE_VALUE, TYPE), pq.getFilterQueries()[2]);
    }
}
