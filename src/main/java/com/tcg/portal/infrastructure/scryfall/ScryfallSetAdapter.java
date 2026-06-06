package com.tcg.portal.infrastructure.scryfall;

import com.tcg.portal.application.port.out.SetBrowsePort;
import com.tcg.portal.domain.model.MagicSet;
import com.tcg.portal.domain.model.SetCardPage;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallSetDto;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallSetListResponse;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class ScryfallSetAdapter implements SetBrowsePort {

    private static final Logger log = LoggerFactory.getLogger(ScryfallSetAdapter.class);

    private final RestClient restClient;
    private final ScryfallCardMapper mapper;

    public ScryfallSetAdapter(RestClient scryfallRestClient, ScryfallCardMapper mapper) {
        this.restClient = scryfallRestClient;
        this.mapper = mapper;
    }

    @Override
    public List<MagicSet> findAllSets() {
        try {
            ScryfallSetListResponse response = restClient.get()
                    .uri("/sets")
                    .retrieve()
                    .body(ScryfallSetListResponse.class);
            if (response == null || response.data() == null) return List.of();
            return response.data().stream()
                    .map(this::toMagicSet)
                    .sorted(Comparator.comparing(MagicSet::releasedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();
        } catch (RestClientException e) {
            log.warn("Failed to fetch sets from Scryfall: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public SetCardPage findCardsBySetCode(String setCode, int page) {
        try {
            ScryfallSearchResponse response = restClient.get()
                    .uri("/cards/search?q=set:{code}&order=collector_number&page={page}", setCode, page)
                    .retrieve()
                    .body(ScryfallSearchResponse.class);
            if (response == null || response.data() == null) return new SetCardPage(List.of(), false);
            return new SetCardPage(
                    response.data().stream().map(mapper::toDomain).toList(),
                    response.hasMore()
            );
        } catch (RestClientException e) {
            log.warn("Failed to fetch cards for set '{}' page {}: {}", setCode, page, e.getMessage());
            return new SetCardPage(List.of(), false);
        }
    }

    private MagicSet toMagicSet(ScryfallSetDto dto) {
        LocalDate date = null;
        if (dto.releasedAt() != null && !dto.releasedAt().isBlank()) {
            try {
                date = LocalDate.parse(dto.releasedAt());
            } catch (Exception e) {
                log.debug("Could not parse release date '{}' for set '{}'", dto.releasedAt(), dto.code());
            }
        }
        return new MagicSet(dto.code(), dto.name(), dto.setType(), date, dto.cardCount(), dto.iconSvgUri());
    }
}
