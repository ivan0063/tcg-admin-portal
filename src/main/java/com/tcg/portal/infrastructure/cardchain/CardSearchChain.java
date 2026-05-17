package com.tcg.portal.infrastructure.cardchain;

import com.tcg.portal.domain.model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CardSearchChain {

    private static final Logger log = LoggerFactory.getLogger(CardSearchChain.class);

    private final List<CardSearchProvider> providers;

    public CardSearchChain(List<CardSearchProvider> providers) {
        this.providers = providers;
    }

    public Optional<Card> findByName(String name) {
        for (CardSearchProvider provider : providers) {
            Optional<Card> result = provider.findByName(name);
            if (result.isPresent()) {
                log.debug("Card '{}' resolved by {}", name, provider.providerName());
                return result;
            }
            log.debug("Card '{}' not found by {}, trying next provider", name, provider.providerName());
        }
        log.warn("Card '{}' not found by any provider", name);
        return Optional.empty();
    }
}
