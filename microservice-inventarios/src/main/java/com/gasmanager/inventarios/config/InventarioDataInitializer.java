package com.gasmanager.inventarios.config;

import com.gasmanager.inventarios.services.InventarioCombustibleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InventarioDataInitializer implements CommandLineRunner {

    @Autowired
    private InventarioCombustibleService inventarioService;

    @Override
    public void run(String... args) throws Exception {
        // Se removio la siembra automatica de tanques:
        // la gasolinera se entrega en cero y los tanques se crean
        // junto con el catalogo de combustibles desde el modulo.
        System.out.println("=== INVENTARIO INICIADO SIN DATOS (entrega en cero) ===");
    }
}