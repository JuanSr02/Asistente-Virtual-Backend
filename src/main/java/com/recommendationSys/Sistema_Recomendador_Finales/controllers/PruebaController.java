package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PruebaController{

    @GetMapping
    public ResponseEntity<String> publicTest() {
        return ResponseEntity.ok("Esta de die");
    }
}
