package com.example.bcfips;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FulltlsController {
    @GetMapping("/")
    public String welcome() {
        return "Welcome to our secured app!";
    }
}
