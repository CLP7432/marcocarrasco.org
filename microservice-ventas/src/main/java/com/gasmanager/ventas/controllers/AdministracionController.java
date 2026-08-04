package com.gasmanager.ventas.controllers;

import com.gasmanager.ventas.services.LimpiezaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdministracionController {

    private final LimpiezaService limpiezaService;

    @PostMapping("/reiniciar-sistema")
    public ResponseEntity<?> reiniciarSistema() {
        try {
            limpiezaService.reiniciarSistema();
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Sistema reiniciado correctamente",
                    "detalle", "Se eliminaron ventas, turnos, cortes, lecturas, dispensarios y mangueras."
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}