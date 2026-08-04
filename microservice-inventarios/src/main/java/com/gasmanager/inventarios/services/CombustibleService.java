package com.gasmanager.inventarios.services;


import com.gasmanager.inventarios.dto.CombustibleRequestDTO;
import com.gasmanager.inventarios.dto.CombustibleResponseDTO;
import com.gasmanager.inventarios.dto.PrecioUpdateDTO;

import java.util.List;

public interface CombustibleService {

    CombustibleResponseDTO crearCombustible(
            CombustibleRequestDTO request,
            Long usuarioId,
            String usuarioNombre);

    CombustibleResponseDTO actualizarPrecio(
            Long id,
            PrecioUpdateDTO request,
            Long usuarioId,
            String usuarioNombre);

    List<CombustibleResponseDTO> listarCombustibles();

    CombustibleResponseDTO obtenerCombustible(Long id);

    List<CombustibleResponseDTO> listarActivos();

    void eliminarCombustible(Long id);

    CombustibleResponseDTO toggleActivo(Long id);

    void limpiarTodo();
}
