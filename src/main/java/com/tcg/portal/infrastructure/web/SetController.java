package com.tcg.portal.infrastructure.web;

import com.tcg.portal.application.port.in.CardSearchUseCase;
import com.tcg.portal.application.port.in.ManageCollectionUseCase;
import com.tcg.portal.application.port.in.ManageDeckUseCase;
import com.tcg.portal.application.port.in.SetBrowseUseCase;
import com.tcg.portal.application.service.CollectionPdfExportService;
import com.tcg.portal.domain.model.Card;
import com.tcg.portal.domain.model.CardCondition;
import com.tcg.portal.domain.model.MagicSet;
import com.tcg.portal.domain.model.SetCardPage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sets")
public class SetController {

    private final SetBrowseUseCase setBrowseUseCase;
    private final CardSearchUseCase cardSearchUseCase;
    private final ManageCollectionUseCase collectionUseCase;
    private final ManageDeckUseCase deckUseCase;
    private final CollectionPdfExportService pdfExportService;

    public SetController(SetBrowseUseCase setBrowseUseCase,
                         CardSearchUseCase cardSearchUseCase,
                         ManageCollectionUseCase collectionUseCase,
                         ManageDeckUseCase deckUseCase,
                         CollectionPdfExportService pdfExportService) {
        this.setBrowseUseCase = setBrowseUseCase;
        this.cardSearchUseCase = cardSearchUseCase;
        this.collectionUseCase = collectionUseCase;
        this.deckUseCase = deckUseCase;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String type, Model model) {
        List<MagicSet> allSets = setBrowseUseCase.getAllSets();

        List<String> setTypes = allSets.stream()
                .map(MagicSet::setType)
                .filter(t -> t != null && !t.isBlank())
                .distinct()
                .sorted()
                .toList();

        List<MagicSet> displayed = (type != null && !type.isBlank())
                ? setBrowseUseCase.getSetsByType(type)
                : allSets;

        model.addAttribute("sets", displayed);
        model.addAttribute("setTypes", setTypes);
        model.addAttribute("selectedType", type);
        return "sets/list";
    }

    @GetMapping("/{setCode}")
    public String detail(@PathVariable String setCode,
                         @RequestParam(defaultValue = "1") int page,
                         Model model) {
        MagicSet set = setBrowseUseCase.getSet(setCode);
        SetCardPage cardPage = setBrowseUseCase.getCardsInSet(setCode, page);

        model.addAttribute("set", set);
        model.addAttribute("cards", cardPage.cards());
        model.addAttribute("hasMore", cardPage.hasMore());
        model.addAttribute("currentPage", page);
        return "sets/detail";
    }

    @GetMapping("/{setCode}/export-pdf")
    @ResponseBody
    public ResponseEntity<byte[]> exportSetPdf(@PathVariable String setCode) {
        MagicSet set = setBrowseUseCase.getSet(setCode);

        List<Card> allCards = new ArrayList<>();
        int page = 1;
        SetCardPage result;
        do {
            result = setBrowseUseCase.getCardsInSet(setCode, page++);
            allCards.addAll(result.cards());
        } while (result.hasMore());

        String subtitle = set.cardCount() + " cards  ·  "
                + set.releasedAtFull()
                + "  ·  exported " + java.time.LocalDate.now();
        byte[] pdf = pdfExportService.exportCardList(set.name(), subtitle, allCards);
        String filename = set.code().replaceAll("[^a-zA-Z0-9_-]", "_") + "-" +
                set.name().replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{setCode}/cards/{scryfallId}")
    public String cardDetail(@PathVariable String setCode,
                             @PathVariable String scryfallId,
                             Model model) {
        MagicSet set = setBrowseUseCase.getSet(setCode);
        var card = cardSearchUseCase.getCardById(scryfallId);

        model.addAttribute("set", set);
        model.addAttribute("card", card);
        model.addAttribute("collections", collectionUseCase.getAllCollections());
        model.addAttribute("decks", deckUseCase.getAllDecks());
        model.addAttribute("conditions", CardCondition.values());
        return "sets/card-detail";
    }

    @PostMapping("/{setCode}/cards/{scryfallId}/collect")
    public String addToCollection(@PathVariable String setCode,
                                  @PathVariable String scryfallId,
                                  @RequestParam Long collectionId,
                                  @RequestParam(defaultValue = "1") int quantity,
                                  @RequestParam(defaultValue = "NEAR_MINT") CardCondition condition,
                                  @RequestParam(defaultValue = "false") boolean foil) {
        collectionUseCase.addCard(collectionId, scryfallId, quantity, condition, foil);
        return "redirect:/sets/" + setCode + "/cards/" + scryfallId + "?added=collection";
    }

    @PostMapping("/{setCode}/cards/{scryfallId}/deck")
    public String addToDeck(@PathVariable String setCode,
                            @PathVariable String scryfallId,
                            @RequestParam Long deckId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(defaultValue = "false") boolean sideboard) {
        deckUseCase.addCard(deckId, scryfallId, quantity, sideboard);
        return "redirect:/sets/" + setCode + "/cards/" + scryfallId + "?added=deck";
    }
}
