package com.tcg.portal.infrastructure.web;

import com.tcg.portal.application.port.in.CardSearchUseCase;
import com.tcg.portal.application.port.in.ManageCollectionUseCase;
import com.tcg.portal.application.port.in.ManageDeckUseCase;
import com.tcg.portal.application.port.in.SetBrowseUseCase;
import com.tcg.portal.domain.model.CardCondition;
import com.tcg.portal.domain.model.MagicSet;
import com.tcg.portal.domain.model.SetCardPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/sets")
public class SetController {

    private final SetBrowseUseCase setBrowseUseCase;
    private final CardSearchUseCase cardSearchUseCase;
    private final ManageCollectionUseCase collectionUseCase;
    private final ManageDeckUseCase deckUseCase;

    public SetController(SetBrowseUseCase setBrowseUseCase,
                         CardSearchUseCase cardSearchUseCase,
                         ManageCollectionUseCase collectionUseCase,
                         ManageDeckUseCase deckUseCase) {
        this.setBrowseUseCase = setBrowseUseCase;
        this.cardSearchUseCase = cardSearchUseCase;
        this.collectionUseCase = collectionUseCase;
        this.deckUseCase = deckUseCase;
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
