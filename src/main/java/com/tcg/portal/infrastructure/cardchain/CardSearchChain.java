package com.tcg.portal.infrastructure.cardchain;

import tools.jackson.databind.ObjectMapper;
import com.tcg.portal.domain.model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CardSearchChain {

    private static final Logger log = LoggerFactory.getLogger(CardSearchChain.class);

    private final List<CardSearchProvider> providers;
    private final ObjectMapper objectMapper;

    public CardSearchChain(List<CardSearchProvider> providers, ObjectMapper objectMapper) {
        this.providers = providers;
        this.objectMapper = objectMapper;
    }

    public ChainResult findByName(String name) {
        List<Map<String, Object>> attempts = new ArrayList<>();

        for (CardSearchProvider provider : providers) {
            ProviderResult result = provider.findByName(name);
            Map<String, Object> attempt = new LinkedHashMap<>();
            attempt.put("provider", result.providerName());
            attempt.put("found", result.found());
            if (result.statusCode() != 0) attempt.put("status", result.statusCode());
            if (result.rawBody() != null) attempt.put("body", result.rawBody());
            attempts.add(attempt);

            if (result.found()) {
                log.debug("Card '{}' resolved by {}", name, result.providerName());
                return new ChainResult(result.card(), toJson(attempts));
            }
            log.debug("Card '{}' not found by {}, trying next provider", name, result.providerName());
        }
        log.warn("Card '{}' not found by any provider", name);
        return new ChainResult(Optional.empty(), toJson(attempts));
    }

    private String toJson(List<Map<String, Object>> attempts) {
        try {
            return objectMapper.writeValueAsString(attempts);
        } catch (Exception e) {
            return "[]";
        }
    }
}
