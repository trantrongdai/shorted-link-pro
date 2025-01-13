package com.in28minutes.microservices.limitsservice.controller;

import com.in28minutes.microservices.limitsservice.bean.ShortedLink;
import com.in28minutes.microservices.limitsservice.constant.ShortedLinkConstants;
import com.in28minutes.microservices.limitsservice.dto.request.ShortedLinkRequestDto;
import com.in28minutes.microservices.limitsservice.dto.response.ShortedLinkResponseDto;
import com.in28minutes.microservices.limitsservice.service.ShortedLinkService;
import com.in28minutes.microservices.limitsservice.util.RegexUtil;
import jakarta.websocket.server.PathParam;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // test 11
    @PostMapping("/shortedLinks")
    public ResponseEntity addLink(@RequestBody ShortedLinkRequestDto shortedLinkRequestDto) {
        if(!RegexUtil.isMatch(shortedLinkRequestDto.getLinkOriginal(), ShortedLinkConstants.HTTP_REGEX)) {
            return ResponseEntity.badRequest().body("http url is invalid");
        }
        ShortedLink shortedLink = shortedLinkService.addShortedLink(shortedLinkRequestDto);
        return ResponseEntity.ok(new ShortedLinkResponseDto(shortedLink.getId(), shortedLink.getLinkOriginal(), shortedLink.getShortedDomain(), shortedLink.getShortedUrl()));
    }

    @PutMapping("/shortedLinks/{id}")
    public ResponseEntity putLink(@PathVariable Long id, @RequestBody ShortedLinkRequestDto shortedLinkRequestDto) {
        if(!RegexUtil.isMatch(shortedLinkRequestDto.getLinkOriginal(), ShortedLinkConstants.HTTP_REGEX)) {
            return ResponseEntity.badRequest().body("http url is invalid");
        }
        ShortedLink shortedLink = shortedLinkService.editShortedLink(id, shortedLinkRequestDto);
        return ResponseEntity.ok(new ShortedLinkResponseDto(shortedLink.getId(), shortedLink.getLinkOriginal(), shortedLink.getShortedDomain(), shortedLink.getShortedUrl()));
    }

    @DeleteMapping("/shortedLinks/{id}")
    public void deleteLink(@PathVariable Long id) {
        shortedLinkService.deleteShortedLink(id);
    }

    @GetMapping("/shortedLinks")
    public List<ShortedLinkResponseDto> getShortedLinks() {
        List<ShortedLink> shortedLinks = shortedLinkService.getShortedLinks();
        return shortedLinks
                .stream()
                .map(shortedLink -> new ShortedLinkResponseDto(shortedLink.getId(), shortedLink.getLinkOriginal(), shortedLink.getShortedDomain(), shortedLink.getShortedUrl()))
                .collect(Collectors.toList());
    }

    @GetMapping("/shortedLinks/check/{url}")
    public ResponseEntity checkCustomerUrl(@PathVariable String url) {
        boolean isHas = shortedLinkService.checkShortedUrl(url);
        return ResponseEntity.ok(isHas);
    }

    @GetMapping("/{url}")
    public ModelAndView redirect(@PathVariable String url) {
        ShortedLink shortedLink = shortedLinkService.findByShortedUrl(url);
        return new ModelAndView("redirect:" + shortedLink.getLinkOriginal());
    }
}