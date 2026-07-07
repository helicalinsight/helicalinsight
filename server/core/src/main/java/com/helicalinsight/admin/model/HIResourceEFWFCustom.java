package com.helicalinsight.admin.model;


import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceEFWFCustom implements Serializable {

    @Column(name="script",table="hi_resource_efwvf_custom", length = Integer.MAX_VALUE)
    @Lob
    private String script;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
