package com.gasmanager.ventas.controllers;

import com.gasmanager.ventas.dto.DispensarioCreacionDTO;
import com.gasmanager.ventas.entities.core.Dispensario;
import com.gasmanager.ventas.entities.core.Manguera;
import com.gasmanager.ventas.enums.EstadoDispensarioEnum;
import com.gasmanager.ventas.enums.TipoCombustibleEnum;
import com.gasmanager.ventas.services.DispensarioService;
import com.gasmanager.ventas.repositories.MangueraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dispensarios")
@RequiredArgsConstructor
public class DispensarioController {

    private final DispensarioService dispensarioService;
    private final MangueraRepository mangueraRepository;

    // ========== ENDPOINTS BÁSICOS ==========

    @PostMapping
    public ResponseEntity<Dispensario> crearDispensario(@RequestBody Dispensario dispensario) {
        try {
            Dispensario nuevo = dispensarioService.crearDispensario(dispensario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Dispensario>> listarTodos() {
        return ResponseEntity.ok(dispensarioService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Dispensario>> listarActivos() {
        return ResponseEntity.ok(dispensarioService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispensario> obtenerDispensario(@PathVariable Long id) {
        return dispensarioService.obtenerDispensario(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<Dispensario> obtenerPorNumero(@PathVariable String numero) {
        return dispensarioService.obtenerPorNumero(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Dispensario>> listarPorTipo(@PathVariable TipoCombustibleEnum tipo) {
        return ResponseEntity.ok(dispensarioService.listarPorTipoCombustible(tipo));
    }

    @PutMapping("/{id}/lectura")
    public ResponseEntity<Dispensario> actualizarLectura(@PathVariable Long id, @RequestBody BigDecimal nuevaLectura) {
        try {
            return ResponseEntity.ok(dispensarioService.actualizarLectura(id, nuevaLectura));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Dispensario> actualizarEstado(@PathVariable Long id, @RequestBody EstadoDispensarioEnum estado) {
        try {
            return ResponseEntity.ok(dispensarioService.actualizarEstado(id, estado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ENDPOINTS PARA DISPENSARIOS COMPLETOS ==========

    @GetMapping("/completo/{id}")
    public ResponseEntity<Dispensario> obtenerDispensarioCompleto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(dispensarioService.obtenerDispensarioCompleto(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/completo")
    public ResponseEntity<Dispensario> crearDispensarioCompleto(@RequestBody DispensarioCreacionDTO dto) {
        try {
            System.out.println("=== POST /completo recibido ===");
            Dispensario nuevo = dispensarioService.crearDispensarioCompletoDesdeDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/completo/{id}")
    public ResponseEntity<Dispensario> actualizarDispensarioCompleto(@PathVariable Long id, @RequestBody DispensarioCreacionDTO dto) {
        try {
            Dispensario actualizado = dispensarioService.actualizarDispensarioCompletoDesdeDTO(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/completos")
    public ResponseEntity<List<Dispensario>> listarDispensariosCompletos() {
        return ResponseEntity.ok(dispensarioService.listarDispensariosCompletos());
    }

    @GetMapping("/mangueras/activas")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Manguera>> obtenerManguerasActivas() {
        List<Manguera> mangueras = dispensarioService.obtenerManguerasActivas();

        // Forzar la carga de relaciones para evitar lazy loading
        for (Manguera m : mangueras) {
            if (m.getCara() != null) {
                m.getCara().getId();
                if (m.getCara().getDispensario() != null) {
                    m.getCara().getDispensario().getId();
                }
            }
        }

        return ResponseEntity.ok(mangueras);
    }

    @PutMapping("/mangueras/{mangueraId}/lectura")
    public ResponseEntity<Void> actualizarLecturaManguera(
            @PathVariable Long mangueraId,
            @RequestBody BigDecimal nuevaLectura) {
        dispensarioService.actualizarLecturaManguera(mangueraId, nuevaLectura);
        return ResponseEntity.ok().build();
    }

    // ========== ELIMINAR Y DESHABILITAR DISPENSARIOS ==========

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDispensario(@PathVariable Long id) {
        try {
            dispensarioService.eliminarDispensario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/habilitar")
    public ResponseEntity<Dispensario> habilitarDispensario(@PathVariable Long id) {
        try {
            Dispensario dispensario = dispensarioService.cambiarEstado(id, true);
            return ResponseEntity.ok(dispensario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/deshabilitar")
    public ResponseEntity<Dispensario> deshabilitarDispensario(@PathVariable Long id) {
        try {
            Dispensario dispensario = dispensarioService.cambiarEstado(id, false);
            return ResponseEntity.ok(dispensario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/mantenimiento")
    public ResponseEntity<Dispensario> ponerEnMantenimiento(@PathVariable Long id) {
        try {
            Dispensario dispensario = dispensarioService.cambiarEstadoMantenimiento(id);
            return ResponseEntity.ok(dispensario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/despachador")
    public ResponseEntity<Dispensario> asignarDespachador(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            Long despachadorId = body.get("despachadorId") != null
                    ? Long.valueOf(body.get("despachadorId").toString()) : null;
            String despachadorNombre = (String) body.get("despachadorNombre");
            Dispensario actualizado = dispensarioService.asignarDespachador(id, despachadorId, despachadorNombre);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/activos-para-venta")
    public ResponseEntity<List<Dispensario>> listarDispensariosActivosParaVenta() {
        List<Dispensario> todos = dispensarioService.listarDispensariosCompletos();
        List<Dispensario> activos = todos.stream()
                .filter(d -> d.isActivo())
                .filter(d -> d.getEstado() == EstadoDispensarioEnum.ACTIVO)
                .collect(Collectors.toList());
        System.out.println("DISPENSARIOS ACTIVOS PARA VENTA: " + activos.size());
        return ResponseEntity.ok(activos);
    }

    // ========== ENDPOINTS DE DEBUG ==========

    @GetMapping("/debug/todas-mangueras")
    public ResponseEntity<?> debugTodasMangueras() {
        List<Manguera> todas = mangueraRepository.findAll();
        System.out.println("=== TODAS LAS MANGUERAS EN BD ===");
        System.out.println("Total: " + todas.size());
        for (Manguera m : todas) {
            System.out.println("ID: " + m.getId() +
                    ", Nombre: " + m.getNombre() +
                    ", Codigo: " + m.getCodigo() +
                    ", TipoCombustible: '" + m.getTipoCombustible() + "'" +
                    ", Activo: " + m.getActivo());
        }
        return ResponseEntity.ok(todas);
    }

    @PostMapping("/debug/verificar-datos")
    public ResponseEntity<?> verificarDatos(@RequestBody Map<String, Object> datos) {
        System.out.println("=== DATOS RECIBIDOS EN VERIFICAR ===");
        System.out.println("Datos completos: " + datos);
        return ResponseEntity.ok(Map.of("recibido", true, "mensaje", "Datos recibidos correctamente"));
    }

    @GetMapping("/debug/contar-mangueras")
    public ResponseEntity<?> debugContarMangueras() {
        long total = mangueraRepository.count();
        System.out.println("=== TOTAL MANGUERAS EN BD: " + total);
        return ResponseEntity.ok(Map.of("total", total));
    }
}