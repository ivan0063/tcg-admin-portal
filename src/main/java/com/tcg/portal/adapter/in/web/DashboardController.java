package com.tcg.portal.adapter.in.web;

import com.tcg.portal.application.port.in.ManageCollectionUseCase;
import com.tcg.portal.application.port.in.ManageDeckUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class DashboardController {

    private final ManageCollectionUseCase collectionUseCase;
    private final ManageDeckUseCase deckUseCase;

    public DashboardController(ManageCollectionUseCase collectionUseCase,
                               ManageDeckUseCase deckUseCase) {
        this.collectionUseCase = collectionUseCase;
        this.deckUseCase = deckUseCase;
    }

    @GetMapping
    public String dashboard(Model model) {
        var collections = collectionUseCase.getAllCollections();
        var decks = deckUseCase.getAllDecks();

        model.addAttribute("collections", collections);
        model.addAttribute("decks", decks);
        model.addAttribute("totalCollections", collections.size());
        model.addAttribute("totalDecks", decks.size());
        model.addAttribute("totalCards",
                collections.stream().mapToInt(c -> c.getTotalCards()).sum());
        model.addAttribute("pageTitle", "Dashboard");
        return "index";
    }
}
