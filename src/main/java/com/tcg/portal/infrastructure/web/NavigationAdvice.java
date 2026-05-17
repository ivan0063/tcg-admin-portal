package com.tcg.portal.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavigationAdvice {

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
