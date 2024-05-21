package com.in28minutes.microservices.limitsservice.controller;

import com.in28minutes.microservices.limitsservice.bean.ShortedLink;
import com.in28minutes.microservices.limitsservice.dto.request.ShortedLinkRequestDto;
import com.in28minutes.microservices.limitsservice.dto.response.ShortedLinkResponseDto;
import com.in28minutes.microservices.limitsservice.service.ShortedLinkService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.in28minutes.microservices.limitsservice.bean.Limits;
import com.in28minutes.microservices.limitsservice.configuration.Configuration;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LimitsController {

    @Autowired
    private Configuration configuration;

    @Autowired
    private ShortedLinkService shortedLinkService;

    @GetMapping("/limits")
    public Limits retrieveLimits() {
        return new Limits(configuration.getMinimum(),
                configuration.getMaximum());
    }

    @PostMapping("/shortedLinks")
    public ShortedLinkResponseDto addLink(@RequestBody ShortedLinkRequestDto shortedLinkRequestDto) {
        ShortedLink shortedLink = shortedLinkService.addShortedLink(shortedLinkRequestDto);
        return new ShortedLinkResponseDto(shortedLink.getId(), shortedLink.getLinkOriginal(), shortedLink.getShortedDomain(), shortedLink.getShortedUrl());
    }

    @GetMapping("/shortedLinks")
    public List<ShortedLinkResponseDto> getShortedLinks() {
        List<ShortedLink> shortedLinks = shortedLinkService.getShortedLinks();
        return shortedLinks
                .stream()
                .map(shortedLink -> new ShortedLinkResponseDto(shortedLink.getId(), shortedLink.getLinkOriginal(), shortedLink.getShortedDomain(), shortedLink.getShortedUrl()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{url}")
    public ModelAndView redirect(@PathVariable String url) {
        ShortedLink shortedLink = shortedLinkService.findByShortedUrl(url);
        return new ModelAndView("redirect:" + shortedLink.getLinkOriginal());
    }
}