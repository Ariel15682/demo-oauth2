package com.example.controllers;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller // controlador mvc
public class MvcController {


    @GetMapping("/page1")
    public String page1(Model model){
        model.addAttribute("message", "Welcome to Securized page");

        return "page1";
    }

    @GetMapping("/page2")
    public String page(Model model, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client,
                       @AuthenticationPrincipal OAuth2User user){

        model.addAttribute("clientName", client.getClientRegistration().getClientName());
        model.addAttribute("userName", user.getName());
        model.addAttribute("userAttributes", user.getAttributes());

        return "page2";
    }
}
