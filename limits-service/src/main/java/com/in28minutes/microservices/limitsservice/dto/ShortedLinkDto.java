package com.in28minutes.microservices.limitsservice.dto;

public class ShortedLinkDto {

    private Long Id;

    private String linkOriginal;

    private String shortedLink;

    public ShortedLinkDto() {

    }

    public ShortedLinkDto(String linkOriginal, String shortedLink) {
        this.linkOriginal = linkOriginal;
        this.shortedLink = shortedLink;
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

    public String getShortedLink() {
        return shortedLink;
    }

    public void setShortedLink(String shortedLink) {
        this.shortedLink = shortedLink;
    }
}
