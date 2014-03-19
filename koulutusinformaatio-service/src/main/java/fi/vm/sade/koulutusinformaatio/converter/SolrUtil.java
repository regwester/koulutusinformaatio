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

package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koulutusinformaatio.domain.*;

import org.apache.solr.common.SolrInputDocument;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public final class SolrUtil {

    private SolrUtil() {
    }

    private static final String FALLBACK_LANG = "fi";
    private static final String TYPE_FACET = "FASETTI";

    public static String resolveTranslationInTeachingLangUseFallback(List<Code> teachingLanguages, Map<String, String> translations) {
        String translation = null;
        for (Code teachingLanguage : teachingLanguages) {
            for (Map.Entry<String, String> availableTranslation : translations.entrySet()) {
                if (teachingLanguage.getValue().equalsIgnoreCase(availableTranslation.getKey())) {
                    translation = availableTranslation.getValue();
                }
            }
        }
        if (translation == null) {
            translation = translations.get(FALLBACK_LANG);
        }
        if (translation == null) {
            translation = translations.values().iterator().next();
        }

        return translation;
    }

    public static void addApplicationDates(SolrInputDocument doc, List<ApplicationOption> applicationOptions) {
        int parentApplicationDateRangeIndex = 0;
        for (ApplicationOption ao : applicationOptions) {
            if (ao.isSpecificApplicationDates()) {
                doc.addField(new StringBuilder().append("asStart").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), ao.getApplicationStartDate());
                doc.addField(new StringBuilder().append("asEnd").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), ao.getApplicationEndDate());
                parentApplicationDateRangeIndex++;
            } else {
                for (DateRange dr : ao.getApplicationSystem().getApplicationDates()) {
                    doc.addField(new StringBuilder().append("asStart").append("_").
                            append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getStartDate());
                    doc.addField(new StringBuilder().append("asEnd").append("_").
                            append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getEndDate());
                    parentApplicationDateRangeIndex++;
                }
            }
        }
    }
    
    /*
     * Creates a facet document for the given code, and adds to the list of docs given.
     */
    public static void indexCodeAsFacetDoc(Code code, List<SolrInputDocument> docs, boolean useValueAsId) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.ID, useValueAsId ? code.getValue() : code.getUri());
        doc.addField(LearningOpportunity.TYPE, TYPE_FACET);
        doc.addField(LearningOpportunity.FI_FNAME, resolveTextWithFallback("fi", code.getName().getTranslations()));
        doc.addField(LearningOpportunity.SV_FNAME, resolveTextWithFallback("sv", code.getName().getTranslations()));
        doc.addField(LearningOpportunity.EN_FNAME, resolveTextWithFallback("en", code.getName().getTranslations())); 
        docs.add(doc);
    }

    public static String resolveTextWithFallback(String lang,
            Map<String, String> translations) {
        String translation = translations.get(lang);
        if (translation == null) {
            translation = translations.get(FALLBACK_LANG);
        }
        if ((translation == null) 
                && !translations.isEmpty() 
                && !translations.values().isEmpty()) {
            translation = translations.values().iterator().next();
        }
        return translation;
    }

    public static class LearningOpportunity {

        public static final String TYPE = "type";
        public static final String ID = "id";
        public static final String FI_FNAME = "fi_fname"; //finnish name of a facet value
        public static final String SV_FNAME = "sv_fname"; //swedish name of a facet value
        public static final String EN_FNAME = "en_fname"; //english name of a facet value
        public static final String TEACHING_LANGUAGE = "teachingLangCode_ffm"; //The teaching language used in the learning opportunity
        public static final String EDUCATION_TYPE = "educationType_ffm"; //The education type of the learning opportunity
        public static final String TOPIC = "topic_ffm"; //The topic of the learning opportunity
        public static final String THEME = "theme_ffm"; //The theme of the learning opportunity
        public static final String LOP_ID = "lopId";
        public static final String NAME = "name";
        public static final String NAME_FI = "name_fi";
        public static final String NAME_SV = "name_sv";
        public static final String NAME_EN = "name_en";
        public static final String CHILD_NAME = "childName";
        public static final String CHILD_NAME_FI = "childName_fi";
        public static final String CHILD_NAME_SV = "childName_sv";
        public static final String CHILD_NAME_EN = "childName_en";
        public static final String LOP_NAME = "lopName";
        public static final String LOP_NAME_FI = "lopName_fi";
        public static final String LOP_NAME_SV = "lopName_sv";
        public static final String LOP_NAME_EN = "lopName_en";
        public static final String LOP_HOMEPLACE = "lopHomeplace";
        public static final String LOP_ADDRESS_FI = "lopAddress_fi";
        public static final String LOP_DESCRIPTION_FI = "lopDescription_fi";
        public static final String LOP_DESCRIPTION_SV = "lopDescription_sv";
        public static final String LOP_DESCRIPTION_EN = "lopDescription_en";
        public static final String GOALS_FI = "goals_fi";
        public static final String GOALS_SV = "goals_sv";
        public static final String GOALS_EN = "goals_en";
        public static final String AS_NAME_FI = "asName_fi";
        public static final String AS_NAME_SV = "asName_sv";
        public static final String AS_NAME_EN = "asName_en";
        public static final String PREREQUISITES = "prerequisites";
        public static final String LOS_ID = "losId";
        public static final String PARENT_ID = "parentId";
        public static final String PREREQUISITE = "prerequisite";
        public static final String PREREQUISITE_CODE = "prerequisiteCode";
        public static final String PROFESSIONAL_TITLES_FI = "professionalTitles_fi";
        public static final String PROFESSIONAL_TITLES_SV = "professionalTitles_sv";
        public static final String PROFESSIONAL_TITLES_EN = "professionalTitles_en";
        public static final String QUALIFICATION_FI = "qualification_fi";
        public static final String QUALIFICATION_SV = "qualification_sv";
        public static final String QUALIFICATION_EN = "qualification_en";
        public static final String CONTENT_FI = "content_fi";
        public static final String CONTENT_SV = "content_sv";
        public static final String CONTENT_EN = "content_en";
        public static final String CREDITS = "credits_ss";
        public static final String EDUCATION_DEGREE = "educationDegree_ss";
        public static final String EDUCATION_DEGREE_CODE = "educationDegreeCode_ss";
        public static final String EDUCATION_DEGREE_FI = "educationDegree_fi_ss";
        public static final String EDUCATION_DEGREE_SV = "educationDegree_sv_ss";
        public static final String EDUCATION_DEGREE_EN = "educationDegree_en_ss";
        public static final String NAME_DISPLAY_FI = "name_fi_ss";
        public static final String NAME_DISPLAY_SV = "name_sv_ss";
        public static final String NAME_DISPLAY_EN = "name_en_ss";
        public static final String LOP_NAME_DISPLAY_FI = "lopName_fi_ssort";
        public static final String LOP_NAME_DISPLAY_SV = "lopName_sv_ssort";
        public static final String LOP_NAME_DISPLAY_EN = "lopName_en_ssort";
        public static final String HOMEPLACE_DISPLAY_FI = "homeplace_fi_ss";
        public static final String HOMEPLACE_DISPLAY_SV = "homeplace_sv_ss";
        public static final String HOMEPLACE_DISPLAY_EN = "homeplace_en_ss";
        public static final String HOMEPLACE_DISPLAY = "homeplace_ss";
        public static final String EDUCATION_CODE_DISPLAY_FI = "educationCode_fi_ssort";
        public static final String EDUCATION_CODE_DISPLAY_SV = "educationCode_sv_ssort";
        public static final String EDUCATION_CODE_DISPLAY_EN = "educationCode_en_ssort";
        
        public static final String EDUCATION_TYPE_DISPLAY = "educationCode_en_ss";
        
        public static final String ARTICLE_URL = "article_url_ss";
        public static final String ARTICLE_PICTURE = "article_picture_ss";
        public static final String ARTICLE_EXCERPT = "article_excerpt_ss";
        
        public static final String ARTICLE_NAME_INDEX_FI = "article_name_fi_ssort";
        public static final String ARTICLE_NAME_INDEX_SV = "article_name_sv_ssort";
        public static final String ARTICLE_NAME_INDEX_EN = "article_name_en_ssort";

        //Fields for sorting
        public static final String START_DATE_SORT = "startDate_dsort";
        public static final String NAME_SORT = "name_ssort";
        public static final String DURATION_SORT = "duration_isort";

        //Fields for autocomplete
        public static final String NAME_AUTO = "name_auto";
        public static final String FREE_AUTO = "free_auto";
        
        

    }

    public static class LocationFields {
        public static final String TYPE = "type"; //Type of area, i.e. kunta or maakunta
        public static final String ID = "id";
        public static final String NAME = "name"; 
        public static final String LANG = "lang"; 
        public static final String CODE = "code"; 
        public static final String PARENT = "parent"; //The parent area of the municipality
    }

    public static class SolrConstants {
        //Constants related to core swap
        public static final String ALIAS_ACTION = "/admin/collections?action=CREATEALIAS&name=";
        public static final String COLLECTIONS = "&collections=";
        public static final String GET = "GET";

        //value constants
        /*public static final String ED_TYPE_LUKIO = "et1"; 
        public static final String ED_TYPE_KAKSOIS = "et2";
        public static final String ED_TYPE_AMMATILLINEN = "et3";
        public static final String ED_TYPE_AMM_ER = "et4";
        public static final String ED_TYPE_VALMENTAVA = "et5";
        public static final String ED_TYPE_AMKS = "et6";
        public static final String ED_TYPE_AMK = "et6.et7";
        public static final String ED_TYPE_YLEMPI_AMK = "et6.et8";
        public static final String ED_TYPE_YOS = "et9";
        public static final String ED_TYPE_KANDIDAATTI = "et9.et10";
        public static final String ED_TYPE_MAISTERI = "et9.et11";
        public static final String ED_TYPE_VOC_PREP = "et12.et14";
        public static final String ED_TYPE_TENTH_GRADE = "et12.et13";
        public static final String ED_TYPE_IMM_VOC = "et12.et15";
        public static final String ED_TYPE_IMM_UPSEC = "et12.et16";
        public static final String ED_TYPE_MUU = "et12";*/
        
        //value constants
        public static final String ED_TYPE_TUTKINTOON = "et01";
        public static final String ED_TYPE_LUKIO = "et01.01";
        public static final String ED_TYPE_KAKSOIS = "et01.02";
        public static final String ED_TYPE_AMMATILLISET = "et01.03";
        public static final String ED_TYPE_AMMATILLINEN = "et01.03.01";
        public static final String ED_TYPE_AMM_ER = "et01.03.02";

        public static final String ED_TYPE_AMKS = "et01.04";
        public static final String ED_TYPE_AMK = "et01.04.01";
        public static final String ED_TYPE_YLEMPI_AMK = "et01.04.02";
        public static final String ED_TYPE_YOS = "et01.05";
        public static final String ED_TYPE_KANDIDAATTI = "et01.05.01";
        public static final String ED_TYPE_MAISTERI = "et01.05.02";
        
        public static final String ED_TYPE_MUU = "et02";
        public static final String ED_TYPE_PK_JALK = "et02.01";
        public static final String ED_TYPE_TENTH_GRADE = "et02.01.01";
        public static final String ED_TYPE_VOC_PREP = "et02.01.02";
        public static final String ED_TYPE_IMM_VOC = "et02.01.03";
        public static final String ED_TYPE_IMM_UPSEC = "et02.01.04";
        public static final String ED_TYPE_KANSANOPISTO = "et02.01.05";
        public static final String ED_TYPE_VALMENTAVA = "et02.02";
        
        public static final String SPECIAL_EDUCATION = "ER";
        public static final String TIMESTAMP_DOC = "loUpdateTimestampDocument";
        public static final String TYPE_FACET = "FASETTI";
        public static final String TYPE_ARTICLE = "ARTIKKELI";
        public static final String KESTOTYYPPI_VUOSI = "suunniteltukesto_01";
        public static final String KESTOTYYPPI_KK = "suunniteltukesto_02";
        public static final String PK = "pk";
        public static final String DISTRICT_UNKNOWN = "99";
        public static final String MUNICIPALITY_UNKNOWN = "99";


    }


}
