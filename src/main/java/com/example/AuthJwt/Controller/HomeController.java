package com.example.AuthJwt.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/rec")
    public String message(){
        return "bonjour monsieur";
    }

    @GetMapping("/recs")
    public String messages(){
        return "bonjour monsieur";
    }
}
