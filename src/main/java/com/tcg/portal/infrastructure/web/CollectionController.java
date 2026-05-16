package com.tcg.portal.infrastructure.web;

import com.tcg.portal.application.port.in.CardSearchUseCase;
import com.tcg.portal.application.port.in.ManageCollectionUseCase;
import com.tcg.portal.domain.model.CardCondition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/collections")
public class CollectionController {

    private final ManageCollectionUseCase collectionUseCase;
    private final CardSearchUseCase cardSearchUseCase;

    public CollectionController(ManageCollectionUseCase collectionUseCase,
                                CardSearchUseCase cardSearchUseCase) {
        this.collectionUseCase = collectionUseCase;
        this.cardSearchUseCase = cardSearchUseCase;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("collections", collectionUseCase.getAllCollections());
        model.addAttribute("pageTitle", "My Collections");
        return "collections/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("pageTitle", "New Collection");
        return "collections/form";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         RedirectAttributes redirectAttributes) {
        var collection = collectionUseCase.createCollection(name, description);
        redirectAttributes.addFlashAttribute("successMessage", "Collection \"" + name + "\" created!");
        return "redirect:/collections/" + collection.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) String q,
                         Model model) {
        model.addAttribute("collection", collectionUseCase.getCollection(id));
        model.addAttribute("conditions", CardCondition.values());
        model.addAttribute("query", q);
        model.addAttribute("pageTitle", collectionUseCase.getCollection(id).getName());

        if (q != null && !q.isBlank()) {
            model.addAttribute("searchResults", cardSearchUseCase.searchCards(q));
        }
        return "collections/detail";
    }

    @PostMapping("/{id}/cards")
    public String addCard(@PathVariable Long id,
                          @RequestParam String scryfallId,
                          @RequestParam(defaultValue = "1") int quantity,
                          @RequestParam(defaultValue = "NEAR_MINT") CardCondition condition,
                          @RequestParam(defaultValue = "false") boolean foil,
                          RedirectAttributes redirectAttributes) {
        collectionUseCase.addCard(id, scryfallId, quantity, condition, foil);
        redirectAttributes.addFlashAttribute("successMessage", "Card added to collection!");
        return "redirect:/collections/" + id;
    }

    @PostMapping("/{id}/cards/{itemId}/remove")
    public String removeCard(@PathVariable Long id,
                             @PathVariable Long itemId,
                             RedirectAttributes redirectAttributes) {
        collectionUseCase.removeCard(id, itemId);
        redirectAttributes.addFlashAttribute("successMessage", "Card removed.");
        return "redirect:/collections/" + id;
    }

    @PostMapping("/{id}/cards/{itemId}/quantity")
    public String updateQuantity(@PathVariable Long id,
                                 @PathVariable Long itemId,
                                 @RequestParam int quantity,
                                 RedirectAttributes redirectAttributes) {
        collectionUseCase.updateItemQuantity(id, itemId, quantity);
        return "redirect:/collections/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        collectionUseCase.deleteCollection(id);
        redirectAttributes.addFlashAttribute("successMessage", "Collection deleted.");
        return "redirect:/collections";
    }
}
