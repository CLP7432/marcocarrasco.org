package com.gasmanager.ventas.services;

import com.gasmanager.ventas.repositories.*;
import com.gasmanager.ventas.repositories.CaraDispensarioRepository;
import com.gasmanager.ventas.repositories.DispensarioRepository;
import com.gasmanager.ventas.repositories.MangueraRepository;
import com.gasmanager.ventas.clients.InventarioClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimpiezaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final TurnoRepository turnoRepository;
    private final CorteTurnoDetalladoRepository corteTurnoRepository;
    private final LecturaInicialTurnoRepository lecturaInicialRepository;
    private final LecturaFinalTurnoRepository lecturaFinalRepository;
    private final NotaCreditoCorteRepository notaCreditoCorteRepository;
    private final DetalleAceiteCorteRepository detalleAceiteCorteRepository;
    private final MangueraRepository mangueraRepository;
    private final CaraDispensarioRepository caraDispensarioRepository;
    private final DispensarioRepository dispensarioRepository;
    private final InventarioClient inventarioClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void reiniciarSistema() {
        log.info("=== INICIANDO REINICIO DEL SISTEMA ===");

        // 1. Eliminar notas de crédito de cortes
        log.info("Eliminando notas de crédito...");
        notaCreditoCorteRepository.deleteAll();
        notaCreditoCorteRepository.flush();

        // 2. Eliminar detalles de aceite de cortes
        log.info("Eliminando detalles de aceite...");
        detalleAceiteCorteRepository.deleteAll();
        detalleAceiteCorteRepository.flush();

        // 3. Eliminar lecturas finales
        log.info("Eliminando lecturas finales...");
        lecturaFinalRepository.deleteAll();
        lecturaFinalRepository.flush();

        // 4. Eliminar lecturas iniciales
        log.info("Eliminando lecturas iniciales...");
        lecturaInicialRepository.deleteAll();
        lecturaInicialRepository.flush();

        // 5. Eliminar cortes detallados
        log.info("Eliminando cortes...");
        corteTurnoRepository.deleteAll();
        corteTurnoRepository.flush();

        // 6. Eliminar detalles de venta
        log.info("Eliminando detalles de venta...");
        detalleVentaRepository.deleteAll();
        detalleVentaRepository.flush();

        // 7. Eliminar ventas
        log.info("Eliminando ventas...");
        ventaRepository.deleteAll();
        ventaRepository.flush();

        // 8. Eliminar turnos
        log.info("Eliminando turnos...");
        turnoRepository.deleteAll();
        turnoRepository.flush();

        // 9. Eliminar mangueras (hijo de cara) -> caras (hijo de dispensario) -> dispensarios
        log.info("Eliminando mangueras...");
        mangueraRepository.deleteAll();
        mangueraRepository.flush();
        log.info("Eliminando caras de dispensario...");
        caraDispensarioRepository.deleteAll();
        caraDispensarioRepository.flush();
        log.info("Eliminando dispensarios...");
        dispensarioRepository.deleteAll();
        dispensarioRepository.flush();

        // 10. Resetear contadores de IDs
        log.info("Reseteando contadores de IDs...");
        resetearSecuencias();

        // 11. Limpiar catalogo de combustibles y tanques (microservice-inventarios)
        try {
            log.info("Limpiando catalogo de combustibles e inventario en microservice-inventarios...");
            inventarioClient.limpiarTodoCombustibles();
            log.info("Catalogo e inventario de combustibles limpiado");
        } catch (Exception e) {
            log.error("No se pudo limpiar el catalogo de combustibles: {}", e.getMessage());
        }

        log.info("=== SISTEMA REINICIADO CORRECTAMENTE ===");
        log.info("Se elimina todo: ventas, turnos, cortes, lecturas, dispensarios, mangueras");
    }

    private void resetearSecuencias() {
        try {
            entityManager.createNativeQuery("ALTER TABLE ventas AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE turnos AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE detalles_venta AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE cortes_turno_detallado AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE lecturas_iniciales_turno AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE lecturas_finales_turno AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE mangueras AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE caras_dispensario AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE dispensarios AUTO_INCREMENT = 1").executeUpdate();
            log.info("Secuencias reseteadas correctamente");
        } catch (Exception e) {
            log.warn("No se pudieron resetear las secuencias: {}", e.getMessage());
        }
    }
}