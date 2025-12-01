package com.asistenteVirtual.modules.system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/ping")
public class PingPongController {

    @GetMapping
    public ResponseEntity<String> pingPong() {
        return ResponseEntity.ok("pong");
    }
}
