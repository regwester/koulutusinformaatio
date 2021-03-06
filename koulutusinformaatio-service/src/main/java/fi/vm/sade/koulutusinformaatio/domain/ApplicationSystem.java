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

package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ApplicationSystem {

    private String id;
    private I18nText name;
    private List<DateRange> applicationDates = new ArrayList<DateRange>();
    private String status;
    private int maxApplications;
    private String applicationFormLink;
    private String hakutapaUri;
    private String hakutyyppiUri;
    private boolean shownAsFacet;
    private DateRange facetRange;
    private boolean useSystemApplicationForm;
    private String ataruFormKey;

    private Date showEducationsUntil;
    private boolean siirtohaku;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public List<DateRange> getApplicationDates() {
        return applicationDates;
    }

    public void setApplicationDates(List<DateRange> applicationDates) {
        this.applicationDates = applicationDates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationSystem)) return false;

        ApplicationSystem that = (ApplicationSystem) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void setStatus(String tila) {
        this.status = tila;
    }

    public String getStatus() {
        return status;
    }

    public int getMaxApplications() {
        return maxApplications;
    }

    public void setMaxApplications(int maxApplications) {
        this.maxApplications = maxApplications;
    }

    public String getApplicationFormLink() {
        return applicationFormLink;
    }

    public void setApplicationFormLink(String applicationFormLink) {
        this.applicationFormLink = applicationFormLink;
    }

    public String getHakutapaUri() {
        return hakutapaUri;
    }

    public void setHakutapaUri(String hakutapaUri) {
        this.hakutapaUri = hakutapaUri;
    }

    public void setHakutyyppiUri(String hakutyyppiUri) {
        this.hakutyyppiUri = hakutyyppiUri;
    }

    public String getHakutyyppiUri() {
        return hakutyyppiUri;
    }

    public boolean isShownAsFacet() {
        return shownAsFacet;
    }

    public void setShownAsFacet(boolean shownAsFacet) {
        this.shownAsFacet = shownAsFacet;
    }

    public DateRange getFacetRange() {
        return facetRange;
    }

    public void setFacetRange(DateRange facetRange) {
        this.facetRange = facetRange;
    }

    public boolean isUseSystemApplicationForm() {
        return useSystemApplicationForm;
    }

    public void setUseSystemApplicationForm(boolean useSystemApplicationForm) {
        this.useSystemApplicationForm = useSystemApplicationForm;
    }

    public String getAtaruFormKey() {
        return ataruFormKey;
    }

    public void setAtaruFormKey(String key) {
        this.ataruFormKey = key;
    }

    public Date getShowEducationsUntil() {
        return showEducationsUntil;
    }

    public void setShowEducationsUntil(Date showEducationsUntil) {
        this.showEducationsUntil = showEducationsUntil;
    }

    public boolean isSiirtohaku() {
        return siirtohaku;
    }

    public void setSiirtohaku(boolean siirtohaku) {
        this.siirtohaku = siirtohaku;
    }
}
