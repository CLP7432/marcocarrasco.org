package com.gasmanager.inventarios.services;

import com.gasmanager.inventarios.dto.CombustibleRequestDTO;
import com.gasmanager.inventarios.dto.CombustibleResponseDTO;
import com.gasmanager.inventarios.dto.PrecioUpdateDTO;
import com.gasmanager.inventarios.entities.Combustible;
import com.gasmanager.inventarios.entities.PrecioHistorico;
import com.gasmanager.inventarios.enums.TipoCombustible;
import com.gasmanager.inventarios.exceptions.ResourceNotFoundException;
import com.gasmanager.inventarios.exceptions.ValidationException;
import com.gasmanager.inventarios.entities.InventarioCombustible;
import com.gasmanager.inventarios.repositories.CombustibleRepository;
import com.gasmanager.inventarios.repositories.InventarioCombustibleRepository;
import com.gasmanager.inventarios.repositories.PrecioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CombustibleServiceImpl implements CombustibleService {


    @Autowired
    private CombustibleRepository combustibleRepository;
    @Autowired
    private PrecioHistoricoRepository precioHistoricoRepository;
    @Autowired
    private InventarioCombustibleRepository inventarioCombustibleRepository;

    public CombustibleServiceImpl (CombustibleRepository combustibleRepository) {
        this.combustibleRepository = combustibleRepository;
    }

    @Override
    @Transactional
    public CombustibleResponseDTO crearCombustible(
            CombustibleRequestDTO request,
            Long usuarioId,
            String usuarioNombre) {

        if (combustibleRepository.existsByTipo(request.getTipo())) {
            throw new ValidationException("Ya existe un combustible con el tipo: " + request.getTipo());
        }

        Combustible combustible = new Combustible();

        combustible.setTipo(request.getTipo());
        combustible.setNombre(request.getNombre());
        combustible.setDescripcion(request.getDescripcion());
        combustible.setPrecioActual(request.getPrecioActual());
        combustible.setActivo(true);
        combustible.setCreatedBy(usuarioNombre);
        combustible.setUpdatedBy(usuarioNombre);

        combustible = combustibleRepository.save(combustible);

        // Si no existe inventario (tanque) para este tipo, se crea en cero.
        // Asi la gasolinera se entrega sin datos y el tanque nace junto al catalogo.
        if (!inventarioCombustibleRepository.findByTipoCombustible(request.getTipo()).isPresent()) {
            InventarioCombustible tanque = new InventarioCombustible(
                    request.getTipo(),
                    request.getNombre(),
                    new BigDecimal("0"));
            tanque.setStockActual(BigDecimal.ZERO);
            tanque.setStockMinimo(new BigDecimal("0"));
            tanque.setActivo(true);
            inventarioCombustibleRepository.save(tanque);
        }

        return mapToResDTO(combustible);
    }


    @Override
    @Transactional
    public CombustibleResponseDTO actualizarPrecio(
            Long id,
            PrecioUpdateDTO request,
            Long usuarioId,
            String usuarioNombre) {

        Combustible combustible = combustibleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Combustible no encontrado con ID: "
                        + id));

        BigDecimal precioAnterior = combustible.getPrecioActual();

        //Guardar Historico
        PrecioHistorico historico = new PrecioHistorico();
        historico.setCombustible(combustible);
        historico.setPrecioAnterior(precioAnterior);
        historico.setPrecioNuevo(request.getNuevoPrecio());
        historico.setFechaCambio(LocalDateTime.now());
        historico.setMotivoCambio(request.getMotivoCambio());
        historico.setCambiadoPor(usuarioNombre);
        historico.setCambiadoPorId(usuarioId);

        precioHistoricoRepository.save(historico);

        //Actualizar precio actual
        combustible.setPrecioActual(request.getNuevoPrecio());
        combustible.setUpdatedBy(usuarioNombre);

        combustible = combustibleRepository.save(combustible);

        return mapToResDTO(combustible);
    }



    @Override
    @Transactional(readOnly = true)
    public List<CombustibleResponseDTO> listarCombustibles() {
        return combustibleRepository
                .findAll()
                .stream()
                .map(combustible -> mapToResDTO(combustible))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public CombustibleResponseDTO obtenerCombustible(Long id) {

        Combustible combustible = combustibleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Combustible no encontrado con ID: " + id));

        return mapToResDTO(combustible);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CombustibleResponseDTO> listarActivos() {
        return combustibleRepository
                .findByActivoTrue()
                .stream()
                .map(combustible -> mapToResDTO(combustible))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarCombustible(Long id) {
        Combustible combustible = combustibleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Combustible no encontrado con ID: " + id));

        combustibleRepository.delete(combustible);
    }

    @Override
    @Transactional
    public CombustibleResponseDTO toggleActivo(Long id) {

        Combustible combustible = combustibleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Combustible no encontrado con ID: " + id));

        //Cambiar el estado actual
        combustible.setActivo(!combustible.getActivo());
        combustible = combustibleRepository.save(combustible);

        return mapToResDTO(combustible);
    }

    @Override
    @Transactional
    public void limpiarTodo() {
        precioHistoricoRepository.deleteAll();
        inventarioCombustibleRepository.deleteAll();
        combustibleRepository.deleteAll();
    }



    private CombustibleResponseDTO mapToResDTO(Combustible combustible) {

        CombustibleResponseDTO dto = new CombustibleResponseDTO();
        dto.setId(combustible.getId());
        dto.setTipo(combustible.getTipo());
        dto.setNombre(combustible.getNombre());
        dto.setDescripcion(combustible.getDescripcion());
        dto.setPrecioActual(combustible.getPrecioActual());
        dto.setActivo(combustible.getActivo());
        dto.setCreatedAt(combustible.getCreatedAt());
        dto.setUpdatedAt(combustible.getUpdatedAt());

        return dto;
    }


}
