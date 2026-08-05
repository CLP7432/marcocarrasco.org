package com.gasmanager.ventas.controllers;

import com.gasmanager.ventas.dto.EstadisticasVentasDTO;
import com.gasmanager.ventas.entities.core.Venta;
import com.gasmanager.ventas.enums.EstadoVenta;
import com.gasmanager.ventas.services.VentaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllVentas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaHora") String sortBy,
            @RequestParam(defaultValue = "desc") String direccion) {

        try {
            Sort.Direction sortDirection = direccion.equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<Venta> ventasPage = ventaService.listarTodas(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("ventas", ventasPage.getContent());
            response.put("currentPage", ventasPage.getNumber());
            response.put("totalItems", ventasPage.getTotalElements());
            response.put("totalPages", ventasPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> getVentaById(@PathVariable Long id) {
        return ventaService.obtenerVenta(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/folio/{folio}")
    public ResponseEntity<Venta> getVentaByFolio(@PathVariable String folio) {
        return ventaService.obtenerPorFolio(folio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Venta> createVenta(@Valid @RequestBody Venta venta) {
        try {
            Venta nuevaVenta = ventaService.crearVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venta> updateVenta(@PathVariable Long id, @Valid @RequestBody Venta venta) {
        try {
            Venta ventaActualizada = ventaService.actualizarVenta(id, venta);
            return ResponseEntity.ok(ventaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteVenta(@PathVariable Long id) {
        try {
            if (ventaService.cancelarVenta(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filtro/fechas")
    public ResponseEntity<List<Venta>> getVentasByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<Venta> ventas = ventaService.listarPorFecha(inicio, fin);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Venta>> getVentasByEstado(@PathVariable EstadoVenta estado) {
        List<Venta> ventas = ventaService.listarPorEstado(estado);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/despachador/{despachadorId}")
    public ResponseEntity<List<Venta>> getVentasByDespachador(@PathVariable Long despachadorId) {
        List<Venta> ventas = ventaService.listarPorDespachador(despachadorId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/turno/{turnoId}")
    public ResponseEntity<List<Venta>> getVentasByTurno(@PathVariable Long turnoId) {
        List<Venta> ventas = ventaService.listarPorTurno(turnoId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasVentasDTO> getEstadisticas() {
        return ResponseEntity.ok(ventaService.obtenerEstadisticas());
    }

    @GetMapping("/{id}/puede-facturar")
    public ResponseEntity<Boolean> puedeFacturar(@PathVariable Long id) {
        boolean puede = ventaService.puedeFacturar(id);
        return ResponseEntity.ok(puede);
    }

    @GetMapping("/{id}/puede-cancelar")
    public ResponseEntity<Map<String, Boolean>> puedeCancelar(@PathVariable Long id) {
        boolean puede = ventaService.puedeCancelar(id);
        return ResponseEntity.ok(Map.of("puedeCancelar", puede));
    }

    @PatchMapping("/{id}/marcar-facturada")
    public ResponseEntity<Void> marcarComoFacturada(
            @PathVariable Long id,
            @RequestParam String folioFactura) {

        Optional<Venta> ventaOpt = ventaService.obtenerVenta(id);
        if (ventaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Venta venta = ventaOpt.get();
        venta.setFacturada(true);
        venta.setFolioFactura(folioFactura);
        venta.setEstado(EstadoVenta.FACTURADA);
        ventaService.actualizarVenta(id, venta);

        return ResponseEntity.ok().build();
    }

    // ========== ENDPOINTS PARA CONSULTAS DE IA ==========

    @GetMapping("/consultas/ultima-venta")
    public ResponseEntity<Map<String, Object>> consultarUltimaVenta() {
        try {
            return ResponseEntity.ok(ventaService.obtenerUltimaVenta());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/consultas/ventas-hoy")
    public ResponseEntity<Map<String, Object>> consultarVentasHoy() {
        try {
            return ResponseEntity.ok(ventaService.obtenerVentasHoy());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/consultas/ventas-por-tipo")
    public ResponseEntity<?> consultarVentasPorTipo() {
        try {
            return ResponseEntity.ok(ventaService.obtenerVentasPorTipo());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}