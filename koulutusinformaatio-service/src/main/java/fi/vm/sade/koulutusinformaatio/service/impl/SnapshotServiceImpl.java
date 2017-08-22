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

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.exception.IndexingException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;
import fi.vm.sade.koulutusinformaatio.service.SnapshotService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;


/**
 * @author Hannu Lyytikainen
 */
@Component
public class SnapshotServiceImpl implements SnapshotService {

    private static final Logger LOG = LoggerFactory.getLogger(SnapshotServiceImpl.class);

    private final int THREADS_TO_RUN_PHANTOMJS;

    private static final String TYPE_HIGHERED = "korkeakoulu";
    private static final String TYPE_ADULT_VOCATIONAL = "ammatillinenaikuiskoulutus";
    private static final String TYPE_KOULUTUS = "koulutus";
    private static final String TYPE_TUTKINTO = "tutkinto";
    private static final String QUERY_PARAM_LANG = "descriptionLang";

    private HigherEducationLOSDAO higheredDAO;
    private AdultVocationalLOSDAO adultvocDAO;
    private KoulutusLOSDAO koulutusDAO;
    private TutkintoLOSDAO tutkintoLOSDAO;
    private TarjontaRawService tarjontaRawService;
    private EducationIncrementalDataQueryService dataQueryService;
    private SEOSnapshotService seoSnapshotService;

    private String phantomjs;
    private String snapshotScript;
    private String snapshotFolder;
    private String baseUrl;
    private String snapshotTallennaUrl;

    @Autowired
    public SnapshotServiceImpl(@Qualifier("higherEducationLOSDAO") HigherEducationLOSDAO higheredDAO,
                               @Qualifier("adultVocationalLOSDAO") AdultVocationalLOSDAO adultvocDAO,
                               @Qualifier("koulutusLOSDAO") KoulutusLOSDAO koulutusDAO,
                               @Qualifier("tutkintoLOSDAO") TutkintoLOSDAO tutkintoLOSDAO,
                               TarjontaRawService tarjontaRawService,
                               EducationIncrementalDataQueryService dataQueryService,
                               SEOSnapshotService seoSnapshotService,
                               @Value("${koulutusinformaatio.phantomjs}") String phantomjs,
                               @Value("${koulutusinformaatio.snapshot.script}") String script,
                               @Value("${koulutusinformaatio.snapshot.folder}") String prerenderFolder,
                               @Value("${koulutusinformaatio.phantomjs.threads ?: 3}") int threadsToRunPhantomjs,
                               OphProperties urlProperties) {
        this.higheredDAO = higheredDAO;
        this.adultvocDAO = adultvocDAO;
        this.koulutusDAO = koulutusDAO;
        this.tutkintoLOSDAO = tutkintoLOSDAO;
        this.tarjontaRawService = tarjontaRawService;
        this.dataQueryService = dataQueryService;
        this.seoSnapshotService = seoSnapshotService;
        this.phantomjs = phantomjs;
        this.snapshotScript = script;
        this.snapshotFolder = prerenderFolder;
        this.baseUrl = urlProperties.url("koulutusinformaatio-app-web.learningopportunity.base");
        this.snapshotTallennaUrl = urlProperties.url("koulutusinformaatio-service.snapshot.tallenna");
        this.THREADS_TO_RUN_PHANTOMJS = threadsToRunPhantomjs;
    }

    @Override
    public void renderSnapshots() throws IndexingException {
        LOG.info("Rendering html snapshots started");

        LOG.info("Rendering HigherEd LOs");
        prerenderWithTeachingLanguages(TYPE_HIGHERED, higheredDAO.findIds());

        LOG.info("Rendering Adult vocational LOs");
        prerender(TYPE_ADULT_VOCATIONAL, adultvocDAO.findIds());

        LOG.info("Rendering Koulutus LOs");
        prerender(TYPE_KOULUTUS, koulutusDAO.findIds());

        LOG.info("Rendering Tutkinto LOs");
        prerender(TYPE_TUTKINTO, tutkintoLOSDAO.findIds());

        LOG.info("Rendering html snapshots finished");
    }

    @Override
    public void renderLastModifiedSnapshots() throws IndexingException {
        long updatePeriod = getUpdatePeriod();

        Map<String, List<String>> updatedLearningOpportunities = tarjontaRawService.listModifiedLearningOpportunities(updatePeriod);

        if (updatedLearningOpportunities == null || updatedLearningOpportunities.isEmpty() ||
                updatedLearningOpportunities.get("hakukohde") == null) {
            return;
        }

        final List<String> hakukohteet = updatedLearningOpportunities.get("hakukohde");

        LOG.info("Rendering html snapshots using updatePeriod {} started", updatePeriod);

        LOG.info("Rendering HigherEd LOs using updatePeriod {}", updatePeriod);
        prerenderWithTeachingLanguages(TYPE_HIGHERED, hakukohteet);

        LOG.info("Rendering Adult vocational LOs using updatePeriod {}", updatePeriod);
        prerender(TYPE_ADULT_VOCATIONAL, hakukohteet);

        LOG.info("Rendering Koulutus LOs using updatePeriod {}", updatePeriod);
        prerender(TYPE_KOULUTUS, hakukohteet);

        LOG.info("Rendering Tutkinto LOs using updatePeriod {}", updatePeriod);
        prerender(TYPE_TUTKINTO, hakukohteet);

        LOG.info("Rendering html snapshots using updatePeriod {} finished", updatePeriod);
    }

    private void prerenderWithTeachingLanguages(String type, List<String> ids) throws IndexingException {
        List<String[]> cmds = Lists.newArrayList();
        for (String id : ids) {
            HigherEducationLOSEntity los = higheredDAO.get(id);

            // generate snapshot for each teaching language
            for (CodeEntity teachingLang : los.getTeachingLanguages()) {
                String lang = "";
                if (teachingLang != null && teachingLang.getValue() != null) {
                    lang = teachingLang.getValue().toLowerCase();
                }
                String[] cmd = generatePhantomJSCommand(type, id, lang);
                cmds.add(cmd);
            }
            // generate default snapshot
            String[] cmd = generatePhantomJSCommand(type, id);
            cmds.add(cmd);
        }
        invokePhantomJS(cmds);
    }

    private void prerender(String type, List<String> ids) throws IndexingException {
        List<String[]> cmds = Lists.newArrayList();
        for (String id : ids) {
            cmds.add(generatePhantomJSCommand(type, id));
        }
        invokePhantomJS(cmds);
    }

    private String[] generatePhantomJSCommand(String type, String id) {
        String url = format("%s%s/%s", baseUrl, type, id);
        return new String[]{phantomjs, snapshotScript, url, id, snapshotTallennaUrl};
    }

    private String[] generatePhantomJSCommand(String type, String id, String lang) {
        String url = format("%s%s/%s?%s=%s", baseUrl, type, id, QUERY_PARAM_LANG, lang);
        return new String[]{phantomjs, snapshotScript, url, id, snapshotTallennaUrl};
    }

    private void invokePhantomJS(List<String[]> cmds) throws IndexingException {
        LOG.info("Invoking phantomjs for {} commands", cmds.size());
        ExecutorService executor = Executors.newFixedThreadPool(THREADS_TO_RUN_PHANTOMJS);
        for (String[] cmd : cmds) {
            executor.execute(new InvokePhantomJs(cmd));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IndexingException("Failed to render snapshots in time", e);
        }
    }

    private class InvokePhantomJs implements Runnable {
        private String[] cmd;

        public InvokePhantomJs(String[] cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            try {
                LOG.debug(Arrays.toString(cmd));

                // "/usr/bin/phantomjs /path/to/script.js http://www.opintopolku.fi/app/#!/koulutus/1.2.3.4.5 1.2.3.4.5 /path/to/save/snapshot/service"
                ProcessBuilder ps = new ProcessBuilder(cmd);

                ps.redirectErrorStream(true);

                Process pr = ps.start();
                BufferedReader phantomOutput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line;
                while ((line = phantomOutput.readLine()) != null) {
                    LOG.info(line);
                }
                try {
                    int exitStatus = pr.waitFor();
                    if (exitStatus != 0) {
                        LOG.warn(format("Rendering %s failed with exit status: %d.",
                                Arrays.toString(cmd), exitStatus));
                    }
                } catch (InterruptedException e) {
                    LOG.error("Error waiting for phantomJS", e);
                    Thread.currentThread().interrupt();
                } finally {
                    phantomOutput.close();
                }
            } catch (IOException e) {
                LOG.error(format("Rendering %s failed.", Arrays.toString(cmd)), e);
            }
        }
    }

    private long getUpdatePeriod() {
        DataStatus status = dataQueryService.getLatestSEOIndexingSuccessDataStatus();

        if (status != null && status.getLastSEOIndexingFinished() != null) {
            return (System.currentTimeMillis() - status.getLastSEOIndexingFinished().getTime()) + status.getLastUpdateDuration();
        }

        return 0;
    }
}
