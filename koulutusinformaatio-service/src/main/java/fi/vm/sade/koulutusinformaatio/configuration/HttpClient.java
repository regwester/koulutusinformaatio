package fi.vm.sade.koulutusinformaatio.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.javautils.httpclient.ApacheOphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClient {

    private OphHttpClient client;

    @Autowired
    public HttpClient(OphProperties urlConfiguration) {
        client = ApacheOphHttpClient.createDefaultOphHttpClient("koulutusinformaatio.koulutusinformaatio-service.backend", urlConfiguration, 60000, 600);
    }

    public static ObjectMapper createJacksonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public OphHttpClient getClient() {
        return client;
    }
}
