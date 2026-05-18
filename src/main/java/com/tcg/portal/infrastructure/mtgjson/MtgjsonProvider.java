package com.tcg.portal.infrastructure.mtgjson;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.tcg.portal.infrastructure.cardchain.CardSearchProvider;
import com.tcg.portal.infrastructure.cardchain.ProviderResult;
import com.tcg.portal.infrastructure.scryfall.ScryfallCardMapper;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

@Component
@Order(4)
public class MtgjsonProvider implements CardSearchProvider {

    private static final Logger log = LoggerFactory.getLogger(MtgjsonProvider.class);

    private final RestClient scryfallRestClient;
    private final ScryfallCardMapper mapper;
    private final ObjectMapper objectMapper;

    @Value("${mtgjson.atomic-cards-url:https://mtgjson.com/api/v5/AtomicCards.json.gz}")
    private String atomicCardsUrl;

    /** lowercase card name → scryfall oracle id */
    private final ConcurrentHashMap<String, String> nameIndex = new ConcurrentHashMap<>();
    private final AtomicBoolean indexReady = new AtomicBoolean(false);

    public MtgjsonProvider(RestClient scryfallRestClient,
                           ScryfallCardMapper mapper,
                           ObjectMapper objectMapper) {
        this.scryfallRestClient = scryfallRestClient;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void buildIndex() {
        log.info("MTGJSON: starting AtomicCards index build from {}", atomicCardsUrl);
        try (InputStream raw = URI.create(atomicCardsUrl).toURL().openStream();
             GZIPInputStream gz = new GZIPInputStream(raw)) {

            JsonNode root = objectMapper.readTree(gz);
            JsonNode data = root.path("data");

            int count = 0;
            for (Map.Entry<String, JsonNode> entry : data.properties()) {
                String cardName = entry.getKey();
                JsonNode printings = entry.getValue();
                if (printings.isArray() && !printings.isEmpty()) {
                    JsonNode first = printings.get(0);
                    JsonNode idNode = first.path("identifiers").path("scryfallOracleId");
                    if (!idNode.isMissingNode() && !idNode.isNull()) {
                        nameIndex.put(cardName.toLowerCase(), idNode.asText());
                        count++;
                    }
                }
            }
            indexReady.set(true);
            log.info("MTGJSON: index ready — {} cards indexed", count);
        } catch (Exception e) {
            log.warn("MTGJSON: index build failed (provider will be skipped): {}", e.getMessage());
        }
    }

    @Override
    public ProviderResult findByName(String name) {
        if (!indexReady.get()) {
            log.debug("MTGJSON index not ready, skipping");
            return ProviderResult.skipped(providerName(), "index not ready");
        }
        String oracleId = nameIndex.get(name.toLowerCase());
        if (oracleId == null) {
            log.debug("MTGJSON: '{}' not in index", name);
            return ProviderResult.skipped(providerName(), "not in index");
        }
        try {
            ScryfallSearchResponse response = scryfallRestClient.get()
                    .uri("/cards/search?q=oracleid:{id}&unique=cards&order=released", oracleId)
                    .retrieve()
                    .body(ScryfallSearchResponse.class);
            if (response == null || response.data() == null || response.data().isEmpty()) {
                return ProviderResult.notFound(providerName(), 200, "empty response");
            }
            return ProviderResult.found(providerName(), mapper.toDomain(response.data().get(0)));
        } catch (RestClientResponseException e) {
            log.debug("MTGJSON→Scryfall lookup failed for '{}' (oracleId={}): {}", name, oracleId, e.getMessage());
            return ProviderResult.notFound(providerName(), e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.debug("MTGJSON→Scryfall lookup failed for '{}' (oracleId={}): {}", name, oracleId, e.getMessage());
            return ProviderResult.notFound(providerName(), 0, e.getMessage());
        }
    }

    @Override
    public String providerName() {
        return "MTGJSON";
    }
}
