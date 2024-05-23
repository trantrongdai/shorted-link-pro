package com.in28minutes.microservices.limitsservice.service;

import com.in28minutes.microservices.limitsservice.bean.ShortedLink;
import com.in28minutes.microservices.limitsservice.dto.request.ShortedLinkRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ShortedLinkService {

    public ShortedLink addShortedLink(ShortedLinkRequestDto shortedLink);

    public ShortedLink editShortedLink(Long id, ShortedLinkRequestDto shortedLink);

    public void deleteShortedLink(Long id);

    public List<ShortedLink> getShortedLinks();

    public boolean checkShortedUrl(String url);

    public ShortedLink findByShortedUrl(String url);

}
