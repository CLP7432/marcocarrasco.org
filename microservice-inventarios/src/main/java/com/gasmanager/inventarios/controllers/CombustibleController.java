package com.gasmanager.inventarios.controllers;

import com.gasmanager.inventarios.dto.CombustibleRequestDTO;
import com.gasmanager.inventarios.dto.CombustibleResponseDTO;
import com.gasmanager.inventarios.dto.PrecioUpdateDTO;
import com.gasmanager.inventarios.services.CombustibleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/combustibles")
public class CombustibleController {

    @Autowired
    private CombustibleService combustibleService;

    @PostMapping
    public ResponseEntity<CombustibleResponseDTO> crearCombustible(
            @Valid @RequestBody CombustibleRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long usuarioId = 1L;
        String usuarioNombre = (userDetails != null) ? userDetails.getUsername() : "sistema";

        CombustibleResponseDTO response =
                combustibleService.crearCombustible(request, usuarioId, usuarioNombre);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<CombustibleResponseDTO> actualizarPrecio(
            @PathVariable Long id,
            @Valid @RequestBody PrecioUpdateDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long usuarioId = 1L;
        String usuarioNombre = (userDetails != null) ? userDetails.getUsername() : "sistema";

        CombustibleResponseDTO response =
                combustibleService.actualizarPrecio(id, request, usuarioId, usuarioNombre);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CombustibleResponseDTO>> listarCombustibles() {
        return ResponseEntity.ok(combustibleService.listarCombustibles());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<CombustibleResponseDTO>> listarActivos() {
        return ResponseEntity.ok(combustibleService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CombustibleResponseDTO> obtenerCombustible(@PathVariable Long id) {
        return ResponseEntity.ok(combustibleService.obtenerCombustible(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCombustible(@PathVariable Long id) {
        combustibleService.eliminarCombustible(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/limpiar-todo")
    public ResponseEntity<Map<String, String>> limpiarTodo() {
        combustibleService.limpiarTodo();
        return ResponseEntity.ok(Map.of("mensaje", "Catálogo e inventario de combustibles limpiado"));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<CombustibleResponseDTO> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(combustibleService.toggleActivo(id));
    }
}