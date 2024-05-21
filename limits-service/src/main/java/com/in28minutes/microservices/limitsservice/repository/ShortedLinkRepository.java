package com.in28minutes.microservices.limitsservice.repository;

import com.in28minutes.microservices.limitsservice.bean.ShortedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortedLinkRepository extends JpaRepository<ShortedLink, Long> {

    ShortedLink findShortedLinkByShortedUrl(String shortedUrl);
}
