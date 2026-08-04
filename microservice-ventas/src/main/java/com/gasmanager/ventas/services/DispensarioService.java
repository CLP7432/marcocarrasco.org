package com.gasmanager.ventas.services;

import com.gasmanager.ventas.dto.DispensarioCreacionDTO;
import com.gasmanager.ventas.entities.core.CaraDispensario;
import com.gasmanager.ventas.entities.core.Dispensario;
import com.gasmanager.ventas.entities.core.Manguera;
import com.gasmanager.ventas.enums.EstadoDispensarioEnum;
import com.gasmanager.ventas.enums.TipoCombustibleEnum;
import com.gasmanager.ventas.repositories.CaraDispensarioRepository;
import com.gasmanager.ventas.repositories.DispensarioRepository;
import com.gasmanager.ventas.repositories.MangueraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DispensarioService {

    private final DispensarioRepository dispensarioRepository;
    private final MangueraRepository mangueraRepository;
    private final CaraDispensarioRepository caraDispensarioRepository;

    // ========== MÉTODOS EXISTENTES ==========

    public Dispensario crearDispensario(Dispensario dispensario) {
        System.out.println("=== CREANDO DISPENSARIO ===");
        System.out.println("Número: " + dispensario.getNumero());
        System.out.println("Nombre: " + dispensario.getNombre());

        if (dispensario.getNumero() != null && dispensarioRepository.findByNumero(dispensario.getNumero()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un dispensario con el número: " + dispensario.getNumero());
        }

        if (dispensario.getEstado() == null) {
            dispensario.setEstado(EstadoDispensarioEnum.ACTIVO);
        }
        if (dispensario.getLecturaInicial() == null) {
            dispensario.setLecturaInicial(BigDecimal.ZERO);
        }
        if (dispensario.getLecturaActual() == null) {
            dispensario.setLecturaActual(BigDecimal.ZERO);
        }

        Dispensario guardado = dispensarioRepository.save(dispensario);
        System.out.println("Dispensario guardado con ID: " + guardado.getId());
        return guardado;
    }

    public Optional<Dispensario> obtenerDispensario(Long id) {
        return dispensarioRepository.findById(id);
    }

    public Optional<Dispensario> obtenerPorNumero(String numero) {
        return dispensarioRepository.findByNumero(numero);
    }

    public List<Dispensario> listarTodos() {
        return dispensarioRepository.findAll();
    }

    public List<Dispensario> listarActivos() {
        return dispensarioRepository.findByEstado(EstadoDispensarioEnum.ACTIVO);
    }

    public List<Dispensario> listarPorTipoCombustible(TipoCombustibleEnum tipo) {
        return dispensarioRepository.findByTipoCombustible(tipo);
    }

    public Dispensario actualizarLectura(Long id, BigDecimal nuevaLectura) {
        Dispensario dispensario = dispensarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado"));
        dispensario.setLecturaActual(nuevaLectura);
        return dispensarioRepository.save(dispensario);
    }

    public Dispensario actualizarEstado(Long id, EstadoDispensarioEnum estado) {
        Dispensario dispensario = dispensarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado"));
        dispensario.setEstado(estado);
        return dispensarioRepository.save(dispensario);
    }

    public BigDecimal calcularVentaPeriodo(Long id, LocalDateTime inicio, LocalDateTime fin) {
        Dispensario dispensario = dispensarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado"));
        BigDecimal lecturaInicialPeriodo = dispensario.getLecturaInicial();
        BigDecimal lecturaFinalPeriodo = dispensario.getLecturaActual();
        return lecturaFinalPeriodo.subtract(lecturaInicialPeriodo);
    }

    // ========== MÉTODOS CON DTO PARA CREAR/ACTUALIZAR ==========

    @Transactional
    public Dispensario crearDispensarioCompletoDesdeDTO(DispensarioCreacionDTO dto) {
        System.out.println("=== crearDispensarioCompletoDesdeDTO ===");
        System.out.println("Número: " + dto.getNumero());
        System.out.println("Nombre: " + dto.getNombre());
        System.out.println("Caras: " + (dto.getCaras() != null ? dto.getCaras().size() : 0));

        if (dto.getNumero() != null && dispensarioRepository.findByNumero(dto.getNumero()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un dispensario con el número: " + dto.getNumero());
        }

        Dispensario nuevoDispensario = new Dispensario();
        nuevoDispensario.setNumero(dto.getNumero());
        nuevoDispensario.setNombre(dto.getNombre());
        nuevoDispensario.setUbicacion(dto.getUbicacion());
        nuevoDispensario.setActivo(true);
        nuevoDispensario.setEstado(EstadoDispensarioEnum.ACTIVO);
        nuevoDispensario.setLecturaInicial(BigDecimal.ZERO);
        nuevoDispensario.setLecturaActual(BigDecimal.ZERO);
        nuevoDispensario.setTieneDosCaras(dto.getTieneDosCaras() != null ? dto.getTieneDosCaras() : true);
        nuevoDispensario.setMangueras(4);

        Dispensario saved = dispensarioRepository.save(nuevoDispensario);
        System.out.println("✅ Dispensario guardado con ID: " + saved.getId());

        if (dto.getCaras() != null && !dto.getCaras().isEmpty()) {
            for (DispensarioCreacionDTO.CaraCreacionDTO caraDTO : dto.getCaras()) {
                if (caraDTO.getMangueras() == null || caraDTO.getMangueras().isEmpty()) {
                    continue;
                }

                CaraDispensario nuevaCara = new CaraDispensario();
                nuevaCara.setDispensario(saved);
                nuevaCara.setCodigo(caraDTO.getCodigo());
                nuevaCara.setNombre(caraDTO.getNombre() != null ? caraDTO.getNombre() : "Cara " + caraDTO.getCodigo());
                nuevaCara.setActivo(true);

                CaraDispensario caraGuardada = caraDispensarioRepository.save(nuevaCara);
                System.out.println("  ✅ Cara guardada con ID: " + caraGuardada.getId());

                for (DispensarioCreacionDTO.MangueraCreacionDTO mangueraDTO : caraDTO.getMangueras()) {
                    if (mangueraDTO.getTipoCombustible() == null || mangueraDTO.getTipoCombustible().isEmpty()) {
                        continue;
                    }

                    Manguera nuevaManguera = new Manguera();
                    nuevaManguera.setCara(caraGuardada);
                    nuevaManguera.setCodigo(mangueraDTO.getCodigo());
                    nuevaManguera.setNombre(mangueraDTO.getNombre());
                    nuevaManguera.setTipoCombustible(mangueraDTO.getTipoCombustible());
                    nuevaManguera.setCombustibleId(mangueraDTO.getCombustibleId());
                    nuevaManguera.setLecturaActual(BigDecimal.ZERO);
                    nuevaManguera.setActivo(true);

                    mangueraRepository.save(nuevaManguera);
                    System.out.println("    ✅ Manguera guardada: " + mangueraDTO.getCodigo());
                }
            }
        }

        return dispensarioRepository.findById(saved.getId())
                .orElseThrow(() -> new IllegalArgumentException("Error al recargar dispensario"));
    }

    @Transactional
    public Dispensario actualizarDispensarioCompletoDesdeDTO(Long id, DispensarioCreacionDTO dto) {
        System.out.println("=== actualizarDispensarioCompletoDesdeDTO para ID: " + id);

        Dispensario existente = dispensarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado con ID: " + id));

        if (dto.getNumero() != null &&
                !existente.getNumero().equals(dto.getNumero()) &&
                dispensarioRepository.findByNumero(dto.getNumero()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro dispensario con el número: " + dto.getNumero());
        }

        if (dto.getNumero() != null) existente.setNumero(dto.getNumero());
        if (dto.getNombre() != null) existente.setNombre(dto.getNombre());
        if (dto.getUbicacion() != null) existente.setUbicacion(dto.getUbicacion());
        if (dto.getTieneDosCaras() != null) existente.setTieneDosCaras(dto.getTieneDosCaras());

        Dispensario saved = dispensarioRepository.save(existente);

        if (existente.getCaras() != null && !existente.getCaras().isEmpty()) {
            for (CaraDispensario cara : existente.getCaras()) {
                if (cara.getMangueras() != null && !cara.getMangueras().isEmpty()) {
                    mangueraRepository.deleteAll(cara.getMangueras());
                }
            }
            caraDispensarioRepository.deleteAll(existente.getCaras());
        }

        caraDispensarioRepository.flush();
        mangueraRepository.flush();

        if (dto.getCaras() != null && !dto.getCaras().isEmpty()) {
            for (DispensarioCreacionDTO.CaraCreacionDTO caraDTO : dto.getCaras()) {
                CaraDispensario nuevaCara = new CaraDispensario();
                nuevaCara.setDispensario(saved);
                nuevaCara.setCodigo(caraDTO.getCodigo());
                nuevaCara.setNombre(caraDTO.getNombre());
                nuevaCara.setActivo(true);

                CaraDispensario caraGuardada = caraDispensarioRepository.save(nuevaCara);

                if (caraDTO.getMangueras() != null && !caraDTO.getMangueras().isEmpty()) {
                    for (DispensarioCreacionDTO.MangueraCreacionDTO mangueraDTO : caraDTO.getMangueras()) {
                        if (mangueraDTO.getTipoCombustible() != null && !mangueraDTO.getTipoCombustible().isEmpty()) {
                            Manguera nuevaManguera = new Manguera();
                            nuevaManguera.setCara(caraGuardada);
                            nuevaManguera.setCodigo(mangueraDTO.getCodigo());
                            nuevaManguera.setNombre(mangueraDTO.getNombre());
                            nuevaManguera.setTipoCombustible(mangueraDTO.getTipoCombustible());
                            nuevaManguera.setCombustibleId(mangueraDTO.getCombustibleId());
                            nuevaManguera.setLecturaActual(BigDecimal.ZERO);
                            nuevaManguera.setActivo(true);

                            mangueraRepository.save(nuevaManguera);
                        }
                    }
                }
            }
        }

        return dispensarioRepository.findById(saved.getId())
                .orElseThrow(() -> new IllegalArgumentException("Error al recargar dispensario"));
    }

    @Transactional(readOnly = true)
    public Dispensario obtenerDispensarioCompleto(Long id) {
        return dispensarioRepository.findById(id)
                .map(d -> {
                    if (d.getCaras() != null) {
                        d.getCaras().size();
                        for (CaraDispensario cara : d.getCaras()) {
                            if (cara.getMangueras() != null) {
                                cara.getMangueras().size();
                            }
                        }
                    }
                    return d;
                })
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Dispensario> listarDispensariosCompletos() {
        List<Dispensario> dispensarios = dispensarioRepository.findAll();
        for (Dispensario d : dispensarios) {
            if (d.getCaras() != null) {
                d.getCaras().size();
                for (CaraDispensario cara : d.getCaras()) {
                    if (cara.getMangueras() != null) {
                        cara.getMangueras().size();
                    }
                }
            }
        }
        return dispensarios;
    }

    @Transactional(readOnly = true)
    public List<Manguera> obtenerManguerasActivas() {
        //  USAR EL MÉTODO CON FETCH
        List<Manguera> todas = mangueraRepository.findAllWithRelations();

        //  FORZAR LA CARGA DE LAS RELACIONES
        for (Manguera m : todas) {
            // Forzar carga de cara
            if (m.getCara() != null) {
                m.getCara().getId();
                // Forzar carga de dispensario
                if (m.getCara().getDispensario() != null) {
                    m.getCara().getDispensario().getId();
                    m.getCara().getDispensario().getNombre();
                }
            }
        }

        return todas.stream()
                .filter(m -> m.getActivo() != null && m.getActivo())
                .filter(m -> m.getTipoCombustible() != null && !m.getTipoCombustible().isEmpty())
                .collect(Collectors.toList());
    }
    @Transactional
    public void actualizarLecturaManguera(Long mangueraId, BigDecimal nuevaLectura) {
        Manguera manguera = mangueraRepository.findById(mangueraId)
                .orElseThrow(() -> new IllegalArgumentException("Manguera no encontrada"));
        manguera.setLecturaActual(nuevaLectura);
        mangueraRepository.save(manguera);
    }

    // ========== ELIMINAR Y DESHABILITAR DISPENSARIOS ==========

    @Transactional
    public void eliminarDispensario(Long id) {
        Dispensario dispensario = dispensarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado con ID: " + id));

        System.out.println("🗑️ Eliminando dispensario: " + dispensario.getNombre());

        if (dispensario.getCaras() != null) {
            for (CaraDispensario cara : dispensario.getCaras()) {
                if (cara.getMangueras() != null && !cara.getMangueras().isEmpty()) {
                    mangueraRepository.deleteAll(cara.getMangueras());
                }
            }
            caraDispensarioRepository.deleteAll(dispensario.getCaras());
        }

        dispensarioRepository.delete(dispensario);
        System.out.println("✅ Dispensario eliminado");
    }

    @Transactional
    public Dispensario cambiarEstado(Long id, boolean activo) {
        Dispensario dispensario = dispensarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado con ID: " + id));

        String accion = activo ? "Habilitando" : "Deshabilitando";
        System.out.println(accion + " dispensario: " + dispensario.getNombre());

        dispensario.setActivo(activo);
        if (activo) {
            dispensario.setEstado(EstadoDispensarioEnum.ACTIVO);
        } else {
            dispensario.setEstado(EstadoDispensarioEnum.INACTIVO);
        }

        if (dispensario.getCaras() != null) {
            for (CaraDispensario cara : dispensario.getCaras()) {
                for (Manguera manguera : cara.getMangueras()) {
                    manguera.setActivo(activo);
                    mangueraRepository.save(manguera);
                }
            }
        }

        return dispensarioRepository.save(dispensario);
    }

    @Transactional
    public Dispensario asignarDespachador(Long id, Long despachadorId, String despachadorNombre) {
        Dispensario dispensario = dispensarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado con ID: " + id));

        if (despachadorId == null) {
            dispensario.setDespachadorId(null);
            dispensario.setDespachadorNombre(null);
        } else {
            dispensario.setDespachadorId(despachadorId);
            dispensario.setDespachadorNombre(despachadorNombre);
        }

        return dispensarioRepository.save(dispensario);
    }

    @Transactional
    public Dispensario cambiarEstadoMantenimiento(Long id) {
        Dispensario dispensario = dispensarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensario no encontrado con ID: " + id));

        System.out.println("🔧 Poniendo en mantenimiento: " + dispensario.getNombre());

        dispensario.setActivo(false);
        dispensario.setEstado(EstadoDispensarioEnum.MANTENIMIENTO);

        if (dispensario.getCaras() != null) {
            for (CaraDispensario cara : dispensario.getCaras()) {
                for (Manguera manguera : cara.getMangueras()) {
                    manguera.setActivo(false);
                    mangueraRepository.save(manguera);
                }
            }
        }

        return dispensarioRepository.save(dispensario);
    }
}