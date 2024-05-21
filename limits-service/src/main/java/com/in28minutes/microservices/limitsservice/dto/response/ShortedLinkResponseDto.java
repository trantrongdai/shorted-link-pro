package com.in28minutes.microservices.limitsservice.dto.response;

public class ShortedLinkResponseDto {

    private Long Id;

    private String linkOriginal;

    private String shortedDomain;

    private String shortedUrl;

    public ShortedLinkResponseDto() {

    }

    public ShortedLinkResponseDto(Long id, String linkOriginal, String shortedDomain, String shortedLink) {
        Id = id;
        this.linkOriginal = linkOriginal;
        this.shortedDomain = shortedDomain;
        this.shortedUrl = shortedLink;
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
