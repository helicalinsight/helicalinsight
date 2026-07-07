package com.helicalinsight.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Embeddable
public class HIResourceImages {

    @Column(name = "content_type", table = "hi_resource_images")
    private String contentType;

    @Column(name = "content", table = "hi_resource_images")
    @Lob
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "resource_id", insertable = false, updatable = false)
    private HIResource hiResource;


    @JsonIgnore
    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }

    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }



}
