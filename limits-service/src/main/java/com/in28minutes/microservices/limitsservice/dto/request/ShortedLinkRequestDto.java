package com.in28minutes.microservices.limitsservice.dto.request;

public class ShortedLinkRequestDto {

    private String linkOriginal;

    private String shortedUrl;

    public ShortedLinkRequestDto() {

    }

    public ShortedLinkRequestDto(String linkOriginal, String shortedLink) {
        this.linkOriginal = linkOriginal;
        this.shortedUrl = shortedLink;
    }

    public String getLinkOriginal() {
        return linkOriginal;
    }

    public void setLinkOriginal(String linkOriginal) {
        this.linkOriginal = linkOriginal;
    }

    public String getShortedUrl() {
        return shortedUrl;
    }

    public void setShortedUrl(String shortedUrl) {
        this.shortedUrl = shortedUrl;
    }
}
