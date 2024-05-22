package com.in28minutes.microservices.limitsservice.service.iml;

import com.in28minutes.microservices.limitsservice.bean.ShortedLink;
import com.in28minutes.microservices.limitsservice.configuration.DomainConfiguration;
import com.in28minutes.microservices.limitsservice.constant.ShortedLinkConstants;
import com.in28minutes.microservices.limitsservice.dto.request.ShortedLinkRequestDto;
import com.in28minutes.microservices.limitsservice.repository.ShortedLinkRepository;
import com.in28minutes.microservices.limitsservice.service.ShortedLinkService;
import com.in28minutes.microservices.limitsservice.util.RegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

import java.util.List;

@Service
public class ShortedLinkServiceImpl implements ShortedLinkService {

    @Autowired
    private ShortedLinkRepository shortedLinkRepository;

    @Autowired
    private DomainConfiguration domainConfiguration;

    @Override
    public ShortedLink addShortedLink(ShortedLinkRequestDto shortedLinkRequestDto) {
        String customerURL = UUID.randomUUID().toString();
        String host = domainConfiguration.isLocal() ? ShortedLinkConstants.CUSTOM_DOMAIN_NAME_LOCAL : domainConfiguration.getHost();
        ShortedLink shortedLink = new ShortedLink(shortedLinkRequestDto.getLinkOriginal(), host, customerURL);
        return shortedLinkRepository.save(shortedLink);
    }

    @Override
    public ShortedLink editShortedLink(Long id, ShortedLinkRequestDto shortedLinkRequestDto) {
        ShortedLink shortedLink = shortedLinkRepository.findById(id).orElse(null);
        if (shortedLink == null) {
            return addShortedLink(shortedLinkRequestDto);
        }
        shortedLink.setLinkOriginal(shortedLinkRequestDto.getLinkOriginal());
        return shortedLinkRepository.save(shortedLink);
    }

    @Override
    public void deleteShortedLink(Long id) {
        shortedLinkRepository.deleteById(id);
    }

    @Override
    public List<ShortedLink> getShortedLinks() {
        return shortedLinkRepository.findAll();
    }

    @Override
    public ShortedLink findByShortedUrl(String url) {
        return shortedLinkRepository.findShortedLinkByShortedUrl(url);
    }
}
