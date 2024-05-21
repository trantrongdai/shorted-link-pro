package com.in28minutes.microservices.limitsservice.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ShortedLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String linkOriginal;

    private String shortedDomain;

    private String shortedUrl;

    public ShortedLink() {
    }

    public ShortedLink(Long id, String linkOriginal, String shortedDomain, String shortedUrl) {
        Id = id;
        this.linkOriginal = linkOriginal;
        this.shortedDomain = shortedDomain;
        this.shortedUrl = shortedUrl;
    }

    public ShortedLink(String linkOriginal, String shortedDomain, String shortedUrl) {
        this.linkOriginal = linkOriginal;
        this.shortedDomain = shortedDomain;
        this.shortedUrl = shortedUrl;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getLinkOriginal() {
        return linkOriginal;
    }

    public void setLinkOriginal(String linkOriginal) {
        this.linkOriginal = linkOriginal;
    }

    public String getShortedDomain() {
        return shortedDomain;
    }

    public void setShortedDomain(String shortedDomain) {
        this.shortedDomain = shortedDomain;
    }

    public String getShortedUrl() {
        return shortedUrl;
    }

    public void setShortedUrl(String shortedUrl) {
        this.shortedUrl = shortedUrl;
    }
}
