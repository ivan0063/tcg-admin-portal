package com.tcg.portal.adapter.in.web;

import com.tcg.portal.domain.exception.CardNotFoundException;
import com.tcg.portal.domain.exception.CollectionNotFoundException;
import com.tcg.portal.domain.exception.DeckNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CollectionNotFoundException.class, DeckNotFoundException.class, CardNotFoundException.class})
    public String handleNotFound(RuntimeException ex, Model model) {
        model.addAttribute("errorTitle", "Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}
