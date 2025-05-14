package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
// CAMBIAR ROLES CUANDO ESTE FRONTEND.

    @GetMapping("/test/public")
    public String publicEndpoint() {
        return "Este es un endpoint público";
    }

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

    @Autowired
    private EmailService emailService;

    @GetMapping("/public/enviar-correo-prueba")
    public ResponseEntity<String> enviarCorreoPrueba() {
        emailService.enviarCorreoDePrueba("juansr02@outlook.com");
        return ResponseEntity.ok("Correo de prueba enviado.");
    }

}
