package com.gasmanager.ventas.services;

import com.gasmanager.ventas.clients.InventarioClient;
import com.gasmanager.ventas.dto.*;
import com.gasmanager.ventas.entities.core.*;
import com.gasmanager.ventas.enums.EstadoCorteEnum;
import com.gasmanager.ventas.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorteTurnoDetalladoService {

    private final CorteTurnoDetalladoRepository corteTurnoRepository;
    private final TurnoRepository turnoRepository;
    private final LecturaInicialTurnoRepository lecturaInicialRepository;
    private final LecturaFinalTurnoRepository lecturaFinalRepository;
    private final LecturaBaseRepository lecturaBaseRepository;
    private final InventarioClient inventarioClient;
    private final MangueraRepository mangueraRepository;
    private final VentaRepository ventaRepository;
    private final DispensarioRepository dispensarioRepository;
    private final CaraDispensarioRepository caraDispensarioRepository;

    // ========== CONFIGURACIÓN INICIAL ==========

    @Transactional
    public void guardarLecturasBase(List<LecturaInicialDTO> lecturas) {
        if (lecturaBaseRepository.count() > 0) {
            throw new IllegalStateException("La configuración inicial ya fue realizada.");
        }
        for (LecturaInicialDTO lectura : lecturas) {
            LecturaBase lecturaBase = LecturaBase.builder()
                    .mangueraId(lectura.getMangueraId())
                    .mangueraNombre(lectura.getMangueraNombre())
                    .tipoCombustible(lectura.getTipoCombustible())
                    .lecturaInicial(lectura.getLecturaInicial())
                    .precioPorLitro(lectura.getPrecioPorLitro())
                    .aceiteId(lectura.getAceiteId())
                    .aceiteNombre(lectura.getAceiteNombre())
                    .cantidadInicial(lectura.getCantidadInicial())
                    .precioUnitario(lectura.getPrecioUnitario())
                    .tipo(lectura.getTipo())
                    .build();
            lecturaBaseRepository.save(lecturaBase);
        }
    }

    @Transactional(readOnly = true)
    public boolean yaSeRealizoConfiguracionInicial() {
        return lecturaBaseRepository.count() > 0;
    }

    // ========== OBTENER LECTURAS INICIALES ==========

    @Transactional(readOnly = true)
    public List<LecturaInicialDTO> obtenerLecturasIniciales(Long turnoId) {
        log.info("=== obtenerLecturasIniciales para turno: {} ===", turnoId);

        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado: " + turnoId));

        List<LecturaInicialDTO> resultado = new ArrayList<>();

        List<LecturaInicialTurno> lecturasGuardadas = lecturaInicialRepository.findByTurnoId(turnoId);

        if (!lecturasGuardadas.isEmpty()) {
            for (LecturaInicialTurno l : lecturasGuardadas) {
                resultado.add(LecturaInicialDTO.builder()
                        .mangueraId(l.getMangueraId())
                        .mangueraNombre(l.getMangueraNombre())
                        .tipoCombustible(l.getTipoCombustible())
                        .lecturaInicial(l.getLecturaInicial())
                        .precioPorLitro(l.getPrecioPorLitro())
                        .tipo(l.getTipo())
                        .build());
            }
            return resultado;
        }

        List<Manguera> mangueras = mangueraRepository.findByActivoTrue();
        if (mangueras.isEmpty()) {
            throw new IllegalStateException("No hay mangueras configuradas.");
        }

        Map<String, BigDecimal> preciosCombustibles = obtenerPreciosCombustibles();

        Turno turnoAnterior = turnoRepository.findUltimoTurnoBySupervisor(turno.getSupervisorId()).orElse(null);
        Map<Long, BigDecimal> lecturasFinalesAnteriores = new HashMap<>();

        if (turnoAnterior != null) {
            List<LecturaFinalTurno> lecturasAnteriores = lecturaFinalRepository.findByTurnoId(turnoAnterior.getId());
            for (LecturaFinalTurno lf : lecturasAnteriores) {
                if (lf.getMangueraId() != null) {
                    lecturasFinalesAnteriores.put(lf.getMangueraId(), lf.getLecturaFinal());
                }
            }
        }

        List<LecturaBase> lecturasBase = lecturaBaseRepository.findAll();
        Map<Long, BigDecimal> lecturasBaseMap = new HashMap<>();
        for (LecturaBase lb : lecturasBase) {
            if (lb.getMangueraId() != null) {
                lecturasBaseMap.put(lb.getMangueraId(), lb.getLecturaInicial());
            }
        }

        for (Manguera m : mangueras) {
            BigDecimal lecturaInicial;

            if (lecturasFinalesAnteriores.containsKey(m.getId())) {
                lecturaInicial = lecturasFinalesAnteriores.get(m.getId());
            } else if (lecturasBaseMap.containsKey(m.getId())) {
                lecturaInicial = lecturasBaseMap.get(m.getId());
            } else if (m.getLecturaActual() != null) {
                lecturaInicial = m.getLecturaActual();
            } else {
                lecturaInicial = BigDecimal.ZERO;
            }

            BigDecimal precio = preciosCombustibles.getOrDefault(m.getTipoCombustible(), BigDecimal.ZERO);

            String nombreCompleto = construirNombreManguera(m);

            resultado.add(LecturaInicialDTO.builder()
                    .mangueraId(m.getId())
                    .mangueraNombre(nombreCompleto)
                    .tipoCombustible(m.getTipoCombustible())
                    .lecturaInicial(lecturaInicial)
                    .precioPorLitro(precio)
                    .tipo("COMBUSTIBLE")
                    .build());
        }

        return resultado;
    }

    private String construirNombreManguera(Manguera m) {
        if (m.getCara() != null && m.getCara().getDispensario() != null) {
            return m.getCara().getDispensario().getNombre() + " - " +
                    m.getCara().getNombre() + " - " +
                    m.getNombre();
        }
        return m.getNombre();
    }

    private Map<String, BigDecimal> obtenerPreciosCombustibles() {
        List<InventarioClient.CombustibleDTO> combustibles = inventarioClient.listarCombustiblesActivos();
        Map<String, BigDecimal> precios = new HashMap<>();
        for (InventarioClient.CombustibleDTO c : combustibles) {
            precios.put(c.getTipo(), c.getPrecioActual());
        }
        return precios;
    }

    // ========== OBTENER DISPENSARIOS DISPONIBLES PARA CORTE ==========

    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerDispensariosDisponibles(Long turnoId) {
        log.info("=== obtenerDispensariosDisponibles para turno: {} ===", turnoId);

        List<Map<String, Object>> resultado = new ArrayList<>();

        List<Dispensario> dispensarios = dispensarioRepository.findByActivoTrue();
        log.info("Dispensarios activos encontrados: {}", dispensarios.size());

        List<Venta> ventas = ventaRepository.findByTurnoId(turnoId);
        log.info("Ventas en el turno: {}", ventas.size());

        List<CorteTurnoDetallado> cortes = corteTurnoRepository.findByTurnoId(turnoId);
        Set<Long> dispensariosConCorte = cortes.stream()
                .filter(c -> c.getDispensarioId() != null)
                .map(CorteTurnoDetallado::getDispensarioId)
                .collect(Collectors.toSet());
        log.info("Dispensarios con corte ya realizado: {}", dispensariosConCorte);

        for (Dispensario d : dispensarios) {
            boolean tieneManguerasActivas = false;
            List<Map<String, Object>> manguerasInfo = new ArrayList<>();

            if (d.getCaras() != null) {
                for (CaraDispensario cara : d.getCaras()) {
                    if (cara.getMangueras() != null) {
                        for (Manguera m : cara.getMangueras()) {
                            if (m.getActivo() != null && m.getActivo() && m.getTipoCombustible() != null) {
                                tieneManguerasActivas = true;
                                Map<String, Object> mInfo = new HashMap<>();
                                mInfo.put("id", m.getId());
                                mInfo.put("nombre", m.getNombre());
                                mInfo.put("tipoCombustible", m.getTipoCombustible());
                                manguerasInfo.add(mInfo);
                            }
                        }
                    }
                }
            }

            if (!tieneManguerasActivas) {
                log.info("Dispensario {} no tiene mangueras activas, saltando", d.getNombre());
                continue;
            }

            boolean tieneCorte = dispensariosConCorte.contains(d.getId());

            int ventasCount = 0;
            BigDecimal totalVentas = BigDecimal.ZERO;

            for (Venta v : ventas) {
                Long dispId = obtenerDispensarioIdDeVenta(v);
                if (dispId != null && dispId.equals(d.getId())) {
                    ventasCount++;
                    if (v.getTotal() != null) {
                        totalVentas = totalVentas.add(v.getTotal());
                    }
                }
            }

            Map<String, Object> info = new HashMap<>();
            info.put("id", d.getId());
            info.put("nombre", d.getNombre());
            info.put("numero", d.getNumero());
            info.put("tieneCorte", tieneCorte);
            info.put("mangueras", manguerasInfo);
            info.put("ventasCount", ventasCount);
            info.put("totalVentas", totalVentas);

            resultado.add(info);
            log.info("Dispensario {}: {} mangueras, {} ventas, tieneCorte: {}",
                    d.getNombre(), manguerasInfo.size(), ventasCount, tieneCorte);
        }

        resultado.sort((a, b) -> {
            boolean aTiene = (boolean) a.get("tieneCorte");
            boolean bTiene = (boolean) b.get("tieneCorte");
            return Boolean.compare(aTiene, bTiene);
        });

        log.info("Total dispensarios procesados: {}", resultado.size());
        return resultado;
    }

    // ========== OBTENER DIAGNÓSTICO ==========

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerDiagnostico(Long turnoId) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            List<Dispensario> dispensarios = dispensarioRepository.findByActivoTrue();
            resultado.put("dispensariosActivos", dispensarios.size());
            resultado.put("dispensarios", dispensarios.stream().map(d ->
                    Map.of("id", d.getId(), "nombre", d.getNombre(), "activo", d.isActivo())
            ).collect(Collectors.toList()));

            List<Venta> ventas = ventaRepository.findByTurnoId(turnoId);
            resultado.put("ventasEnTurno", ventas.size());

            List<Manguera> mangueras = mangueraRepository.findByActivoTrue();
            resultado.put("manguerasActivas", mangueras.size());

            List<CorteTurnoDetallado> cortes = corteTurnoRepository.findByTurnoId(turnoId);
            resultado.put("cortesExistentes", cortes.size());

            long dispensariosConMangueras = 0;
            for (Dispensario d : dispensarios) {
                boolean tieneMangueras = false;
                if (d.getCaras() != null) {
                    for (CaraDispensario cara : d.getCaras()) {
                        if (cara.getMangueras() != null) {
                            for (Manguera m : cara.getMangueras()) {
                                if (m.getActivo() != null && m.getActivo() && m.getTipoCombustible() != null) {
                                    tieneMangueras = true;
                                    break;
                                }
                            }
                        }
                        if (tieneMangueras) break;
                    }
                }
                if (tieneMangueras) dispensariosConMangueras++;
            }
            resultado.put("dispensariosConMangueras", dispensariosConMangueras);

            List<Map<String, Object>> detalleDispensarios = new ArrayList<>();
            for (Dispensario d : dispensarios) {
                Map<String, Object> detalle = new HashMap<>();
                detalle.put("id", d.getId());
                detalle.put("nombre", d.getNombre());
                detalle.put("activo", d.isActivo());

                int totalMangueras = 0;
                int manguerasActivas = 0;
                List<Map<String, Object>> manguerasList = new ArrayList<>();

                if (d.getCaras() != null) {
                    for (CaraDispensario cara : d.getCaras()) {
                        if (cara.getMangueras() != null) {
                            totalMangueras += cara.getMangueras().size();
                            for (Manguera m : cara.getMangueras()) {
                                if (m.getActivo() != null && m.getActivo()) {
                                    manguerasActivas++;
                                    Map<String, Object> mInfo = new HashMap<>();
                                    mInfo.put("id", m.getId());
                                    mInfo.put("nombre", m.getNombre());
                                    mInfo.put("tipoCombustible", m.getTipoCombustible());
                                    mInfo.put("activo", m.getActivo());
                                    manguerasList.add(mInfo);
                                }
                            }
                        }
                    }
                }
                detalle.put("totalMangueras", totalMangueras);
                detalle.put("manguerasActivas", manguerasActivas);
                detalle.put("mangueras", manguerasList);

                detalleDispensarios.add(detalle);
            }
            resultado.put("detalleDispensarios", detalleDispensarios);

            resultado.put("success", true);
            resultado.put("mensaje", "Diagnóstico completado exitosamente");

        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("error", e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    // ========== PROCESAR CORTE ==========

    @Transactional
    public CorteTurnoResponseDTO procesarCorte(CorteTurnoRequestDTO request) {
        log.info("=== PROCESANDO CORTE PARA TURNO: {}, DISPENSARIO: {} ===",
                request.getTurnoId(), request.getDispensarioId());

        Turno turno = turnoRepository.findById(request.getTurnoId())
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

        List<CorteTurnoDetallado> cortesExistentes = corteTurnoRepository.findByTurnoId(request.getTurnoId());
        boolean yaExiste = cortesExistentes.stream()
                .anyMatch(c -> c.getDispensarioId() != null && c.getDispensarioId().equals(request.getDispensarioId()));

        if (yaExiste) {
            throw new IllegalStateException("Ya existe un corte para este dispensario en el turno actual");
        }

        List<LecturaInicialDTO> lecturasIniciales = obtenerLecturasIniciales(request.getTurnoId());

        List<Venta> ventasTurno = ventaRepository.findByTurnoId(turno.getId());
        log.info("Ventas encontradas en el turno: {}", ventasTurno.size());

        List<Venta> ventasDispensario = new ArrayList<>();
        for (Venta v : ventasTurno) {
            Long dispId = obtenerDispensarioIdDeVenta(v);
            if (dispId != null && dispId.equals(request.getDispensarioId())) {
                ventasDispensario.add(v);
            }
        }
        log.info("Ventas del dispensario {}: {}", request.getDispensarioId(), ventasDispensario.size());

        Map<Long, BigDecimal> litrosVendidosPorManguera = new HashMap<>();
        Map<String, BigDecimal> ventasPorCombustible = new HashMap<>();
        ventasPorCombustible.put("MAGNA", BigDecimal.ZERO);
        ventasPorCombustible.put("PREMIUM", BigDecimal.ZERO);
        ventasPorCombustible.put("DIESEL", BigDecimal.ZERO);

        Map<String, BigDecimal> litrosPorCombustible = new HashMap<>();
        litrosPorCombustible.put("MAGNA", BigDecimal.ZERO);
        litrosPorCombustible.put("PREMIUM", BigDecimal.ZERO);
        litrosPorCombustible.put("DIESEL", BigDecimal.ZERO);

        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        BigDecimal totalTransferencia = BigDecimal.ZERO;
        BigDecimal totalCredito = BigDecimal.ZERO;

        for (Venta venta : ventasDispensario) {
            if (venta.getTotal() != null) {
                switch (venta.getMetodoPago()) {
                    case EFECTIVO:
                        totalEfectivo = totalEfectivo.add(venta.getTotal());
                        break;
                    case TARJETA_CREDITO:
                    case TARJETA_DEBITO:
                        totalTarjeta = totalTarjeta.add(venta.getTotal());
                        break;
                    case TRANSFERENCIA:
                        totalTransferencia = totalTransferencia.add(venta.getTotal());
                        break;
                    case CREDITO:
                        totalCredito = totalCredito.add(venta.getTotal());
                        break;
                    default:
                        totalEfectivo = totalEfectivo.add(venta.getTotal());
                }
            }

            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    String tipo = detalle.getProductoNombre() != null ? detalle.getProductoNombre() : "";
                    BigDecimal cantidad = detalle.getCantidad() != null ? detalle.getCantidad() : BigDecimal.ZERO;
                    BigDecimal importe = detalle.getImporte() != null ? detalle.getImporte() : BigDecimal.ZERO;
                    Long mangueraId = detalle.getProductoId();

                    if (mangueraId != null && cantidad.compareTo(BigDecimal.ZERO) > 0) {
                        litrosVendidosPorManguera.merge(mangueraId, cantidad, BigDecimal::add);
                    }

                    if (tipo.contains("MAGNA") || tipo.equals("MAGNA")) {
                        ventasPorCombustible.put("MAGNA", ventasPorCombustible.get("MAGNA").add(importe));
                        litrosPorCombustible.put("MAGNA", litrosPorCombustible.get("MAGNA").add(cantidad));
                    } else if (tipo.contains("PREMIUM") || tipo.equals("PREMIUM")) {
                        ventasPorCombustible.put("PREMIUM", ventasPorCombustible.get("PREMIUM").add(importe));
                        litrosPorCombustible.put("PREMIUM", litrosPorCombustible.get("PREMIUM").add(cantidad));
                    } else if (tipo.contains("DIESEL") || tipo.equals("DIESEL")) {
                        ventasPorCombustible.put("DIESEL", ventasPorCombustible.get("DIESEL").add(importe));
                        litrosPorCombustible.put("DIESEL", litrosPorCombustible.get("DIESEL").add(cantidad));
                    }
                }
            }
        }

        List<LecturaFinalDTO> lecturasFinalesCalculadas = new ArrayList<>();

        for (LecturaInicialDTO inicial : lecturasIniciales) {
            if (inicial.getMangueraId() == null) continue;

            Manguera manguera = mangueraRepository.findById(inicial.getMangueraId()).orElse(null);
            if (manguera == null) continue;
            Long dispId = obtenerDispensarioIdDeManguera(manguera);
            if (dispId == null || !dispId.equals(request.getDispensarioId())) continue;

            BigDecimal lecturaInicial = inicial.getLecturaInicial() != null ? inicial.getLecturaInicial() : BigDecimal.ZERO;
            BigDecimal litrosVendidos = litrosVendidosPorManguera.getOrDefault(inicial.getMangueraId(), BigDecimal.ZERO);
            BigDecimal lecturaFinal = lecturaInicial.add(litrosVendidos);

            lecturasFinalesCalculadas.add(LecturaFinalDTO.builder()
                    .mangueraId(inicial.getMangueraId())
                    .mangueraNombre(inicial.getMangueraNombre())
                    .tipoCombustible(inicial.getTipoCombustible())
                    .lecturaFinal(lecturaFinal)
                    .tipo("COMBUSTIBLE")
                    .build());
        }

        CorteTurnoDetallado corte = CorteTurnoDetallado.builder()
                .codigoCorte(generarCodigoCorte())
                .turno(turno)
                .dispensarioId(request.getDispensarioId())
                .dispensarioNombre(request.getDispensarioNombre())
                .despachadorId(request.getDespachadorId())
                .despachadorNombre(request.getDespachadorNombre())
                .estado(EstadoCorteEnum.PENDIENTE)
                .observaciones(request.getObservaciones())
                .build();

        BigDecimal magnaImporte = ventasPorCombustible.getOrDefault("MAGNA", BigDecimal.ZERO);
        BigDecimal premiumImporte = ventasPorCombustible.getOrDefault("PREMIUM", BigDecimal.ZERO);
        BigDecimal dieselImporte = ventasPorCombustible.getOrDefault("DIESEL", BigDecimal.ZERO);
        BigDecimal totalCombustibles = magnaImporte.add(premiumImporte).add(dieselImporte);

        BigDecimal magnaLitros = litrosPorCombustible.getOrDefault("MAGNA", BigDecimal.ZERO);
        BigDecimal premiumLitros = litrosPorCombustible.getOrDefault("PREMIUM", BigDecimal.ZERO);
        BigDecimal dieselLitros = litrosPorCombustible.getOrDefault("DIESEL", BigDecimal.ZERO);

        corte.setMagnaLitrosVendidos(magnaLitros);
        corte.setMagnaImporte(magnaImporte);
        corte.setPremiumLitrosVendidos(premiumLitros);
        corte.setPremiumImporte(premiumImporte);
        corte.setDieselLitrosVendidos(dieselLitros);
        corte.setDieselImporte(dieselImporte);
        corte.setTotalCombustiblesImporte(totalCombustibles);
        corte.setTotalCombustiblesLitros(magnaLitros.add(premiumLitros).add(dieselLitros));

        BigDecimal totalNotasCredito = BigDecimal.ZERO;
        if (request.getNotasCredito() != null) {
            for (NotaCreditoDTO notaDTO : request.getNotasCredito()) {
                NotaCreditoCorte nota = NotaCreditoCorte.builder()
                        .corteTurno(corte)
                        .folioNota(notaDTO.getFolioNota())
                        .clienteNombre(notaDTO.getClienteNombre())
                        .tipoCombustible(notaDTO.getTipoCombustible())
                        .litros(notaDTO.getLitros() != null ? notaDTO.getLitros() : BigDecimal.ZERO)
                        .monto(notaDTO.getMonto() != null ? notaDTO.getMonto() : BigDecimal.ZERO)
                        .autorizadoPor(notaDTO.getAutorizadoPor())
                        .build();
                corte.addNotaCredito(nota);
                totalNotasCredito = totalNotasCredito.add(nota.getMonto());
            }
        }
        corte.setTotalNotasCredito(totalNotasCredito);

        BigDecimal totalAceites = BigDecimal.ZERO;
        List<CorteTurnoResponseDTO.DetalleAceiteDTO> detallesAceites = new ArrayList<>();

        if (request.getAceitesFinales() != null && request.getLecturasInicialesAceites() != null) {
            for (LecturaFinalDTO aceiteFinal : request.getAceitesFinales()) {
                LecturaInicialDTO inicial = request.getLecturasInicialesAceites().stream()
                        .filter(l -> l.getAceiteId() != null && l.getAceiteId().equals(aceiteFinal.getAceiteId()))
                        .findFirst().orElse(null);
                if (inicial != null) {
                    int cantidadInicial = inicial.getCantidadInicial() != null ? inicial.getCantidadInicial() : 0;
                    int cantidadFinal = aceiteFinal.getCantidadFinal() != null ? aceiteFinal.getCantidadFinal() : 0;
                    int cantidadVendida = Math.max(0, cantidadFinal - cantidadInicial);
                    BigDecimal precioUnitario = inicial.getPrecioUnitario() != null ? inicial.getPrecioUnitario() : BigDecimal.ZERO;
                    BigDecimal importe = BigDecimal.valueOf(cantidadVendida).multiply(precioUnitario);
                    totalAceites = totalAceites.add(importe);

                    detallesAceites.add(CorteTurnoResponseDTO.DetalleAceiteDTO.builder()
                            .aceiteNombre(inicial.getAceiteNombre())
                            .cantidadInicial(cantidadInicial)
                            .cantidadFinal(cantidadFinal)
                            .cantidadVendida(cantidadVendida)
                            .precioUnitario(precioUnitario)
                            .importe(importe)
                            .build());
                }
            }
        }
        corte.setTotalAceitesImporte(totalAceites);

        BigDecimal totalVentas = totalCombustibles.add(totalAceites);
        corte.setTotalVentas(totalVentas);

        BigDecimal efectivoReportado = request.getEfectivoRecibido() != null ? request.getEfectivoRecibido() : totalEfectivo;
        BigDecimal tarjetaReportado = request.getTarjetaRecibido() != null ? request.getTarjetaRecibido() : totalTarjeta;
        BigDecimal transferenciaReportado = request.getTransferenciaRecibido() != null ? request.getTransferenciaRecibido() : totalTransferencia;

        corte.setTotalEfectivo(efectivoReportado);
        corte.setTotalTarjeta(tarjetaReportado);
        corte.setTotalTransferencia(transferenciaReportado);
        corte.setTotalCredito(totalCredito);

        BigDecimal efectivoQueDebeEntregar = totalVentas
                .subtract(tarjetaReportado)
                .subtract(transferenciaReportado)
                .subtract(totalNotasCredito)
                .subtract(totalCredito);
        corte.setEfectivoQueDebeEntregar(efectivoQueDebeEntregar.max(BigDecimal.ZERO));

        BigDecimal diferencia = efectivoReportado.subtract(efectivoQueDebeEntregar);
        corte.setDiferencia(diferencia);

        corte = corteTurnoRepository.save(corte);

        for (LecturaFinalDTO lecturaFinal : lecturasFinalesCalculadas) {
            LecturaFinalTurno lectura = LecturaFinalTurno.builder()
                    .turno(turno)
                    .mangueraId(lecturaFinal.getMangueraId())
                    .mangueraNombre(lecturaFinal.getMangueraNombre())
                    .tipoCombustible(lecturaFinal.getTipoCombustible())
                    .lecturaFinal(lecturaFinal.getLecturaFinal())
                    .tipo("COMBUSTIBLE")
                    .build();
            lecturaFinalRepository.save(lectura);
        }

        log.info("Corte {} generado exitosamente para dispensario {}",
                corte.getCodigoCorte(), corte.getDispensarioNombre());

        return mapToResponseDTO(corte, detallesAceites);
    }

    // ===== MÉTODOS AUXILIARES =====

    private Long obtenerDispensarioIdDeVenta(Venta venta) {
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                if (detalle.getProductoId() != null) {
                    try {
                        Manguera manguera = mangueraRepository.findById(detalle.getProductoId()).orElse(null);
                        if (manguera != null) {
                            Long dispId = obtenerDispensarioIdDeManguera(manguera);
                            if (dispId != null) {
                                return dispId;
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Error al buscar manguera para productoId: {}", detalle.getProductoId());
                    }
                }
            }
        }

        if (venta.getDispensarioId() != null) {
            try {
                return venta.getDispensarioId().longValue();
            } catch (Exception e) {
                log.warn("Error al convertir dispensarioId: {}", venta.getDispensarioId());
            }
        }

        if (venta.getSurtidorId() != null) {
            try {
                Long mangueraId = venta.getSurtidorId().longValue();
                Manguera manguera = mangueraRepository.findById(mangueraId).orElse(null);
                if (manguera != null) {
                    Long dispId = obtenerDispensarioIdDeManguera(manguera);
                    if (dispId != null) {
                        return dispId;
                    }
                }
            } catch (Exception e) {
                log.warn("Error al buscar manguera por surtidorId: {}", venta.getSurtidorId());
            }
        }

        return null;
    }

    private Long obtenerDispensarioIdDeManguera(Manguera manguera) {
        if (manguera.getCara() != null && manguera.getCara().getDispensario() != null) {
            return manguera.getCara().getDispensario().getId();
        }
        return null;
    }

    // ========== VALIDAR CORTE ==========
    @Transactional
    public CorteTurnoResponseDTO validarCorte(Long id, Long supervisorId, String supervisorNombre) {
        log.info("=== VALIDANDO CORTE ID: {} ===", id);

        CorteTurnoDetallado corte = corteTurnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corte no encontrado con ID: " + id));

        if (corte.getEstado() != EstadoCorteEnum.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden validar cortes en estado PENDIENTE. Estado actual: " + corte.getEstado());
        }

        corte.setEstado(EstadoCorteEnum.VALIDADO);
        corte = corteTurnoRepository.save(corte);

        log.info("Corte {} validado por supervisor {}", corte.getCodigoCorte(), supervisorNombre);

        return mapToResponseDTO(corte, new ArrayList<>());
    }

    // ========== CERRAR CORTE ==========
    @Transactional
    public CorteTurnoResponseDTO cerrarCorte(Long id) {
        log.info("=== CERRANDO CORTE ID: {} ===", id);

        CorteTurnoDetallado corte = corteTurnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corte no encontrado con ID: " + id));

        if (corte.getEstado() != EstadoCorteEnum.VALIDADO) {
            throw new IllegalStateException("Solo se pueden cerrar cortes validados. Estado actual: " + corte.getEstado());
        }

        corte.setEstado(EstadoCorteEnum.CERRADO);
        corte = corteTurnoRepository.save(corte);

        log.info("Corte {} cerrado", corte.getCodigoCorte());

        return mapToResponseDTO(corte, new ArrayList<>());
    }

    // ========== VERIFICAR SI EXISTE CORTE POR TURNO ==========
    @Transactional(readOnly = true)
    public boolean existeCortePorTurno(Long turnoId) {
        return !corteTurnoRepository.findByTurnoId(turnoId).isEmpty();
    }

    @Transactional(readOnly = true)
    public List<CorteTurnoResponseDTO> listarCortes() {
        return corteTurnoRepository.findAll().stream()
                .map(c -> mapToResponseDTO(c, new ArrayList<>()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CorteTurnoResponseDTO obtenerCorte(Long id) {
        CorteTurnoDetallado corte = corteTurnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corte no encontrado con ID: " + id));
        return mapToResponseDTO(corte, new ArrayList<>());
    }

    private CorteTurnoResponseDTO mapToResponseDTO(CorteTurnoDetallado corte, List<CorteTurnoResponseDTO.DetalleAceiteDTO> detallesAceites) {
        List<CorteTurnoResponseDTO.NotaCreditoDTO> notasDTO = new ArrayList<>();
        for (NotaCreditoCorte nota : corte.getNotasCredito()) {
            notasDTO.add(CorteTurnoResponseDTO.NotaCreditoDTO.builder()
                    .folioNota(nota.getFolioNota())
                    .clienteNombre(nota.getClienteNombre())
                    .tipoCombustible(nota.getTipoCombustible())
                    .litros(nota.getLitros())
                    .monto(nota.getMonto())
                    .autorizadoPor(nota.getAutorizadoPor())
                    .build());
        }

        return CorteTurnoResponseDTO.builder()
                .id(corte.getId())
                .codigoCorte(corte.getCodigoCorte())
                .turnoId(corte.getTurno().getId())
                .turnoNombre(corte.getTurno().getNombre())
                .dispensarioId(corte.getDispensarioId())
                .dispensarioNombre(corte.getDispensarioNombre())
                .despachadorId(corte.getDespachadorId())
                .despachadorNombre(corte.getDespachadorNombre())
                .magnaLitrosVendidos(corte.getMagnaLitrosVendidos())
                .magnaImporte(corte.getMagnaImporte())
                .premiumLitrosVendidos(corte.getPremiumLitrosVendidos())
                .premiumImporte(corte.getPremiumImporte())
                .dieselLitrosVendidos(corte.getDieselLitrosVendidos())
                .dieselImporte(corte.getDieselImporte())
                .totalCombustiblesImporte(corte.getTotalCombustiblesImporte())
                .detallesAceites(detallesAceites)
                .totalAceitesImporte(corte.getTotalAceitesImporte())
                .notasCredito(notasDTO)
                .totalNotasCredito(corte.getTotalNotasCredito())
                .totalVentaCombustiblesYAceites(corte.getTotalVentas())
                .totalEfectivo(corte.getTotalEfectivo())
                .totalTarjeta(corte.getTotalTarjeta())
                .totalTransferencia(corte.getTotalTransferencia())
                .totalCredito(corte.getTotalCredito())
                .efectivoQueDebeEntregar(corte.getEfectivoQueDebeEntregar())
                .diferencia(corte.getDiferencia())
                .estado(corte.getEstado().name())
                .observaciones(corte.getObservaciones())
                .createdAt(corte.getCreatedAt() != null ? corte.getCreatedAt().toString() : null)
                .build();
    }

    private String generarCodigoCorte() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long secuencial = corteTurnoRepository.count() + 1;
        return String.format("CORTE-%s-%04d", fecha, secuencial);
    }

    @Transactional
    public CorteTurnoResponseDTO actualizarCorte(Long id, Map<String, Object> updates) {
        log.info("=== ACTUALIZANDO CORTE ID: {} ===", id);

        CorteTurnoDetallado corte = corteTurnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corte no encontrado con ID: " + id));

        if (corte.getEstado() != EstadoCorteEnum.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden editar cortes en estado PENDIENTE");
        }

        if (updates.containsKey("totalEfectivo")) {
            BigDecimal totalEfectivo = new BigDecimal(updates.get("totalEfectivo").toString());
            corte.setTotalEfectivo(totalEfectivo);
        }

        if (updates.containsKey("totalTarjeta")) {
            BigDecimal totalTarjeta = new BigDecimal(updates.get("totalTarjeta").toString());
            corte.setTotalTarjeta(totalTarjeta);
        }

        if (updates.containsKey("totalTransferencia")) {
            BigDecimal totalTransferencia = new BigDecimal(updates.get("totalTransferencia").toString());
            corte.setTotalTransferencia(totalTransferencia);
        }

        if (updates.containsKey("totalCredito")) {
            BigDecimal totalCredito = new BigDecimal(updates.get("totalCredito").toString());
            corte.setTotalCredito(totalCredito);
        }

        if (updates.containsKey("observaciones")) {
            String observaciones = updates.get("observaciones").toString();
            corte.setObservaciones(observaciones);
        }

        BigDecimal totalVentas = corte.getTotalVentas() != null ? corte.getTotalVentas() : BigDecimal.ZERO;
        BigDecimal tarjeta = corte.getTotalTarjeta() != null ? corte.getTotalTarjeta() : BigDecimal.ZERO;
        BigDecimal transferencia = corte.getTotalTransferencia() != null ? corte.getTotalTransferencia() : BigDecimal.ZERO;
        BigDecimal totalNotasCredito = corte.getTotalNotasCredito() != null ? corte.getTotalNotasCredito() : BigDecimal.ZERO;
        BigDecimal credito = corte.getTotalCredito() != null ? corte.getTotalCredito() : BigDecimal.ZERO;

        BigDecimal efectivoQueDebeEntregar = totalVentas
                .subtract(tarjeta)
                .subtract(transferencia)
                .subtract(totalNotasCredito)
                .subtract(credito)
                .max(BigDecimal.ZERO);

        corte.setEfectivoQueDebeEntregar(efectivoQueDebeEntregar);

        BigDecimal diferencia = corte.getTotalEfectivo().subtract(efectivoQueDebeEntregar);
        corte.setDiferencia(diferencia);

        corte = corteTurnoRepository.save(corte);

        log.info("Corte {} actualizado: Efectivo=${}, Diferencia=${}",
                corte.getCodigoCorte(),
                corte.getTotalEfectivo(),
                corte.getDiferencia());

        return mapToResponseDTO(corte, new ArrayList<>());
    }
}