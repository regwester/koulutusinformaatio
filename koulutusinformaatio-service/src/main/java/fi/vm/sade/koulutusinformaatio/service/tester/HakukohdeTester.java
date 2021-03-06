package fi.vm.sade.koulutusinformaatio.service.tester;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.domain.AoSolrSearchResult;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.impl.SearchServiceSolrImpl;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Profile("default")
public class HakukohdeTester {
    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeTester.class);

    @Autowired
    private TarjontaRawService tarjontaRawService;

    @Autowired
    private ApplicationOptionDAO applicationOptionDAO;

    @Autowired
    private SearchServiceSolrImpl searchServiceSolr;

    private boolean running = false;

    public void testHakukohteet() {
        try {
            if (running) {
                LOG.debug("Testi on jo ajossa.");
                return;
            }
            running = true;

            LOG.info("Käynnistetään hakukohdetesti.");

            ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> rawRes = tarjontaRawService.findHakukohdes();
            if (rawRes == null
                    || rawRes.getResult() == null
                    || rawRes.getResult().getTulokset() == null
                    || rawRes.getResult().getTulokset().isEmpty()) {
                LOG.warn("Tarjonnasta ei löytynyt hakukohteita. Päätetään hakukohdetesti.");
                return;
            }

            Set<String> hakukohdeOiditKannasta = Sets.newHashSet();
            List<AoSolrSearchResult> indexedAos = searchServiceSolr.searchOngoingApplicationOptions(null, null, null);
            for (AoSolrSearchResult ao : indexedAos) {
                hakukohdeOiditKannasta.add(ao.getId());
            }

            LOG.debug("Kannasta löytyi {} hakukohdetta.", hakukohdeOiditKannasta.size());
            LOG.debug("Tarjonnasta löytyi {} julkaistua hakukohdetta.", rawRes.getResult().getTuloksia());

            Set<String> hakukohdeOiditTarjonnasta = Sets.newHashSet();
            for (String oid : hakukohdehakutuloksetToOidSet(rawRes)) {
                HakukohdeV1RDTO hakukohde = tarjontaRawService.getV1Hakukohde(oid).getResult();
                if (isAoOngoing(hakukohde) && atLastOneKoulutusIsJulkaistuForHakukohde(hakukohde.getOid())) {
                    hakukohdeOiditTarjonnasta.add(oid);
                    if (!hakukohdeOiditKannasta.contains(oid)) {
                        LOG.debug("HakukohdeOid {} puuttuu KIsta!", oid);
                    }
                } else if (hakukohdeOiditKannasta.contains(oid)) {
                    LOG.debug("HakukohdeOid {} puuttuu Tarjonnasta!", oid);
                }
            }
            LOG.debug("Karsinnan jälkeen jäi {} oidia.", hakukohdeOiditTarjonnasta.size());

            logMissingAos(hakukohdeOiditKannasta, hakukohdeOiditTarjonnasta);
        } catch (Exception e) {
            LOG.error("Hakukohteiden testaaminen epäonnistui.", e);
        } finally {
            running = false;
        }
    }

    private boolean atLastOneKoulutusIsJulkaistuForHakukohde(String oid) {
        try {
            List<NimiJaOidRDTO> koulutusByAoId = tarjontaRawService.getV1KoulutusByAoId(oid).getResult();
            for (NimiJaOidRDTO nimiJaOidRDTO : koulutusByAoId) {
                List<KoulutusHakutulosV1RDTO> koulutusList = tarjontaRawService.searchEducation(nimiJaOidRDTO.getOid()).getResult().getTulokset().get(0).getTulokset();
                for (KoulutusHakutulosV1RDTO koulutus : koulutusList) {
                    if ("JULKAISTU".equals(koulutus.getTila().toString()))
                        return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false; // ignore for test, propably NPE from searchEducation...
        }
    }

    private void logMissingAos(Set<String> hakukohdeOiditKannasta, Set<String> hakukohdeOiditTarjonnasta) {
        LOG.info("Verrataan tarjonnan julkaistuja hakukohteita indeksistä löytyviin:");
        HashSet<String> missingFromTarjonta = new HashSet<>(hakukohdeOiditTarjonnasta);
        missingFromTarjonta.removeAll(hakukohdeOiditKannasta);
        HashSet<String> missingFromKanta = new HashSet<>(hakukohdeOiditKannasta);
        missingFromKanta.removeAll(hakukohdeOiditTarjonnasta);

        if (!missingFromTarjonta.isEmpty()) {
            LOG.warn("Tarjonnasta puuttuvat hakukohdeoidit, jotka on indeksoitu koulutusinformaatioon: " + StringUtils.join(missingFromTarjonta, ", "));
        } else {
            LOG.info("Kaikki indeksin hakukohdeoidit löytyivät tarjonnasta. Koulutusinformaatiossa ei näy ylimääräisiä hakukohteita.");
        }
        if (!missingFromKanta.isEmpty()) {
            LOG.warn("Indeksistä puuttuvat hakukohteiden oidit, jotka on julkaistu tarjonnassa: " + StringUtils.join(missingFromKanta, ", "));
        } else {
            LOG.info("Kaikki tarjonnan hakukohteiden oidit löytyivät indeksistä. Tarjonnassa ei ole julkaistuja hakukohteita, joiden pitäisi näkyä koulutusinformaatiossa.");
        }

    }

    private boolean isAoOngoing(HakukohdeV1RDTO hakukohde) {
        if (!isJulkaistu(hakukohde)) {
            return false;
        }

        HakuV1RDTO haku = tarjontaRawService.getV1HakuByOid(hakukohde.getHakuOid()).getResult();
        if (!haku.getTila().equals("JULKAISTU") || haku.getHakuaikas().isEmpty())
            return false;
        HakuaikaV1RDTO aoHakuaika = haku.getHakuaikas().get(0);
        for (HakuaikaV1RDTO hakuaika : haku.getHakuaikas()) {
            if (hakuaika.getHakuaikaId().equals(hakukohde.getHakuaikaId())) {
                aoHakuaika = hakuaika;
            }
        }
        if (hakukohde.getKaytetaanHakukohdekohtaistaHakuaikaa()) {
            return isOngoing(hakukohde.getHakuaikaAlkuPvm(), hakukohde.getHakuaikaLoppuPvm(), haku.getOpintopolunNayttaminenLoppuu());
        }
        return isOngoing(aoHakuaika.getAlkuPvm(), aoHakuaika.getLoppuPvm(), haku.getOpintopolunNayttaminenLoppuu());
    }

    private boolean isJulkaistu(HakukohdeV1RDTO hakukohde) {
        for (String oid : hakukohde.getHakukohdeKoulutusOids()) {
            ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawResult = tarjontaRawService.searchEducation(oid);
            if (rawResult == null ||
                    rawResult.getResult() == null ||
                    rawResult.getResult().getTulokset() == null ||
                    rawResult.getResult().getTulokset().isEmpty() ||
                    rawResult.getResult().getTulokset().get(0) == null ||
                    rawResult.getResult().getTulokset().get(0).getTulokset() == null ||
                    rawResult.getResult().getTulokset().get(0).getTulokset().isEmpty() ||
                    rawResult.getResult().getTulokset().get(0).getTulokset().get(0) == null)
                return false;
            KoulutusHakutulosV1RDTO koulutus = rawResult.getResult().getTulokset().get(0).getTulokset().get(0);
            if (koulutus.getTila().name().equals("JULKAISTU") && koulutusIsValidType(koulutus))
                return true;
        }
        return false;
    }

    private boolean koulutusIsValidType(KoulutusHakutulosV1RDTO koulutus) {
        return !koulutus.getToteutustyyppiEnum().name().equals("AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS"); // peruttu
    }

    private boolean isOngoing(Date alkuPvm, Date loppuPvm, Date hakuEnd) {
        Date now = new Date();
        if (alkuPvm.after(now)) { // In Future
            return true;
        }
        if (loppuPvm == null || loppuPvm.after(now)) {
            return true;
        }
        if (getLastDayToShow(loppuPvm, hakuEnd).after(now)) {
            return true;
        }
        return false;
    }

    private Date getLastDayToShow(Date endDate, Date hakuEnd) {
        Calendar cal = Calendar.getInstance();

        if (hakuEnd == null) {
            cal.setTime(endDate);
            cal.add(Calendar.MONTH, 10);
        } else {
            cal.setTime(hakuEnd);
        }

        // End of day
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }

    private ImmutableSet<String> hakukohdehakutuloksetToOidSet(ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> rawRes) {
        return FluentIterable.from(rawRes.getResult().getTulokset())
                .transformAndConcat(new Function<TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO>, Set<String>>() {
                    public Set<String> apply(TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO> input) {
                        return FluentIterable.from(input.getTulokset())
                                .transform(new Function<HakukohdeHakutulosV1RDTO, String>() {
                                    public String apply(HakukohdeHakutulosV1RDTO input) {
                                        return input.getOid();
                                    }
                                }).toSet();
                    }
                }).toSet();
    }

}
