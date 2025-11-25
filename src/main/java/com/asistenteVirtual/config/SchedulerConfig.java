package com.asistenteVirtual.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.asistenteVirtual.modules.estadisticas.service.EstadisticasService;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final EstadisticasService estadisticasService;

    public SchedulerConfig(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    // Actualiza estadísticas cada día a la 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void actualizarEstadisticasProgramado() {
        estadisticasService.actualizarTodas();
    }
}