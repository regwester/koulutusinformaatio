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

package fi.vm.sade.koulutusinformaatio.dao.entity;


import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author Mikko Majapuro
 */
@Entity("pictures")
public class PictureEntity {

    @Id
    private String id;
    private String pictureEncoded;
    private String thumbnailEncoded;

    public PictureEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPictureEncoded() {
        return pictureEncoded;
    }

    public void setPictureEncoded(String pictureEncoded) {
        this.pictureEncoded = pictureEncoded;
    }

    public String getThumbnailEncoded() {
        return thumbnailEncoded;
    }

    public void setThumbnailEncoded(String thumbnailEncoded) {
        this.thumbnailEncoded = thumbnailEncoded;
    }
}
