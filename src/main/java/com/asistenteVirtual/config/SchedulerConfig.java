package com.asistenteVirtual.config;

import com.asistenteVirtual.services.estadisticas.EstadisticasServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final EstadisticasServiceImpl estadisticasService;

    public SchedulerConfig(EstadisticasServiceImpl estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    // Actualiza estadísticas cada día a la 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void actualizarEstadisticasProgramado() {
        estadisticasService.actualizarEstadisticas();
        estadisticasService.obtenerEstadisticasGenerales(); // Esto guardará las estadísticas generales
    }
}