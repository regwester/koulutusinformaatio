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

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.LanguageSelection;
import fi.vm.sade.koulutusinformaatio.domain.dto.LanguageSelectionDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class LanguageSelectionToDTO {

    private LanguageSelectionToDTO() {
    }

    private static LanguageSelectionDTO convert(LanguageSelection ls, String lang) {
        if (ls != null) {
            LanguageSelectionDTO dto = new LanguageSelectionDTO();
            dto.setSubjectCode(ls.getSubjectCode());
            dto.setLanguages(ConverterUtil.getTextsByLanguage(ls.getLanguages(), lang));
            return dto;
        } else {
            return null;
        }
    }

    public static List<LanguageSelectionDTO> convertAll(final List<LanguageSelection> languageSelections, final String lang) {
        if (languageSelections != null) {
            return Lists.transform(languageSelections, new Function<LanguageSelection, LanguageSelectionDTO>() {
                @Override
                public LanguageSelectionDTO apply(LanguageSelection languageSelection) {
                    return convert(languageSelection, lang);
                }
            });
        } else {
            return null;
        }
    }
}
