package com.example.simplemicroservice.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

    @GetMapping("/")
    String getHome(Model model){
        model.addAttribute("versionNumber",3);
        model.addAttribute("color","#fefefe");
        model.addAttribute("color","#19d6ac");
        return "home";
    }

}
