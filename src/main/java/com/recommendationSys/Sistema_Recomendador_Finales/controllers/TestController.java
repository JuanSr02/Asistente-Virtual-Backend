package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
// CAMBIAR ROLES CUANDO ESTE FRONTEND.

    @GetMapping("/student")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public String studentEndpoint() {
        return "Este es un endpoint solo para estudiantes";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('AUTHENTICATED')")
    public String adminEndpoint() {
        return "Este es un endpoint solo para administradores";
    }


}
