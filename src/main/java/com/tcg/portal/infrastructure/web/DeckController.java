package com.tcg.portal.infrastructure.web;

import com.tcg.portal.application.port.in.CardSearchUseCase;
import com.tcg.portal.application.port.in.ManageDeckUseCase;
import com.tcg.portal.application.service.AsyncImportService;
import com.tcg.portal.application.service.ImportJob;
import com.tcg.portal.application.service.ImportJobStore;
import com.tcg.portal.domain.model.DeckFormat;
import org.springframework.http.MediaType;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/decks")
public class DeckController {

    private final ManageDeckUseCase deckUseCase;
    private final CardSearchUseCase cardSearchUseCase;
    private final AsyncImportService asyncImportService;
    private final ImportJobStore importJobStore;

    public DeckController(ManageDeckUseCase deckUseCase,
                          CardSearchUseCase cardSearchUseCase,
                          AsyncImportService asyncImportService,
                          ImportJobStore importJobStore) {
        this.deckUseCase = deckUseCase;
        this.cardSearchUseCase = cardSearchUseCase;
        this.asyncImportService = asyncImportService;
        this.importJobStore = importJobStore;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("decks", deckUseCase.getAllDecks());
        model.addAttribute("pageTitle", "My Decks");
        return "decks/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("formats", DeckFormat.values());
        model.addAttribute("pageTitle", "New Deck");
        return "decks/form";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam DeckFormat format,
                         @RequestParam(required = false) String cardList,
                         RedirectAttributes redirectAttributes) {
        var deck = deckUseCase.createDeck(name, description, format);

        if (cardList != null && !cardList.isBlank()) {
            asyncImportService.startImport(deck.getId(), cardList);
            redirectAttributes.addFlashAttribute("importPending", true);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Deck \"" + name + "\" created!");
        }

        return "redirect:/decks/" + deck.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) String q,
                         Model model) {
        var deck = deckUseCase.getDeck(id);
        model.addAttribute("deck", deck);
        model.addAttribute("query", q);
        model.addAttribute("pageTitle", deck.getName());

        model.addAttribute("importFailures", deckUseCase.getImportFailures(id));

        if (q != null && !q.isBlank()) {
            model.addAttribute("searchResults", cardSearchUseCase.searchCards(q));
        }
        return "decks/detail";
    }

    @PostMapping("/{id}/import-failures/retry")
    public String retryImportFailures(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        List<String> failed = deckUseCase.getImportFailures(id);
        if (failed.isEmpty()) {
            return "redirect:/decks/" + id;
        }
        // Build card-list text from the stored names (qty=1, mainboard)
        String cardList = String.join("\n", failed.stream().map(n -> "1 " + n).toList());
        asyncImportService.startImport(id, cardList);
        redirectAttributes.addFlashAttribute("importPending", true);
        return "redirect:/decks/" + id;
    }

    @PostMapping("/{id}/import-failures/clear")
    public String clearImportFailures(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        deckUseCase.clearImportFailures(id);
        redirectAttributes.addFlashAttribute("successMessage", "Import failure list cleared.");
        return "redirect:/decks/" + id;
    }

    @PostMapping("/{id}/cards")
    public String addCard(@PathVariable Long id,
                          @RequestParam String scryfallId,
                          @RequestParam(defaultValue = "1") int quantity,
                          @RequestParam(defaultValue = "false") boolean sideboard,
                          RedirectAttributes redirectAttributes) {
        deckUseCase.addCard(id, scryfallId, quantity, sideboard);
        redirectAttributes.addFlashAttribute("successMessage", "Card added to deck!");
        return "redirect:/decks/" + id;
    }

    @PostMapping("/{id}/import")
    public String importCards(@PathVariable Long id,
                              @RequestParam String cardList,
                              RedirectAttributes redirectAttributes) {
        asyncImportService.startImport(id, cardList);
        redirectAttributes.addFlashAttribute("importPending", true);
        return "redirect:/decks/" + id;
    }

    /** Polled by the front-end to track async import progress. */
    @GetMapping(value = "/{id}/import-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ImportJob importStatus(@PathVariable Long id) {
        return importJobStore.pollStatus(id);
    }

    @PostMapping("/{id}/cards/{entryId}/remove")
    public String removeCard(@PathVariable Long id,
                             @PathVariable Long entryId,
                             RedirectAttributes redirectAttributes) {
        deckUseCase.removeCard(id, entryId);
        redirectAttributes.addFlashAttribute("successMessage", "Card removed.");
        return "redirect:/decks/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        deckUseCase.deleteDeck(id);
        redirectAttributes.addFlashAttribute("successMessage", "Deck deleted.");
        return "redirect:/decks";
    }
}
