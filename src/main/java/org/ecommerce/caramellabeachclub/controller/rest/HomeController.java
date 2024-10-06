package org.ecommerce.caramellabeachclub.controller.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller; // Cambiato da @RestController a @Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HomeController {


    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index"); // Assicurati che il nome della vista sia corretto
        return modelAndView;
    }

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home_page"); // Assicurati che "home_page" esista in templates
        return modelAndView;
    }

    @GetMapping("/me")
    public String homePage(@RequestParam(value = "message", required = false) String message, Model model) {
        model.addAttribute("welcomeMessage", message);
        return "home"; // Assicurati che "home" esista in templates
    }
}
