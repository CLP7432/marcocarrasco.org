package com.gasmanager.ventas.services;

import com.gasmanager.ventas.clients.InventarioClient;
import com.gasmanager.ventas.dto.EstadisticasVentasDTO;
import com.gasmanager.ventas.entities.core.DetalleVenta;
import com.gasmanager.ventas.entities.core.Turno;
import com.gasmanager.ventas.entities.core.Venta;
import com.gasmanager.ventas.enums.EstadoTurno;
import com.gasmanager.ventas.enums.EstadoVenta;
import com.gasmanager.ventas.repositories.TurnoRepository;
import com.gasmanager.ventas.repositories.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final TurnoRepository turnoRepository;
    private final InventarioClient inventarioClient;

    public Venta crearVenta(Venta venta) {
        Turno turnoAsignado = null;

        if (venta.getTurnoId() != null) {
            Optional<Turno> turnoOpt = turnoRepository.findById(venta.getTurnoId());
            if (turnoOpt.isPresent()) {
                turnoAsignado = turnoOpt.get();
            }
        }

        if (turnoAsignado == null) {
            List<Turno> turnosActivos = turnoRepository.findByEstado(EstadoTurno.ABIERTO);
            if (!turnosActivos.isEmpty()) {
                turnoAsignado = turnosActivos.get(0);
            }
        }

        if (turnoAsignado == null) {
            throw new IllegalStateException(
                    "No hay un turno activo. El supervisor debe abrir un turno antes de vender.");
        }

        venta.setTurno(turnoAsignado);

        if (venta.getTurno() == null) {
            throw new IllegalStateException("No se pudo asignar un turno a la venta");
        }

        if (venta.getFolio() != null && ventaRepository.findByFolio(venta.getFolio()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una venta con el folio: " + venta.getFolio());
        }

        if (venta.getTotal() != null && venta.getSubtotal() == null) {
            BigDecimal iva = venta.getTotal().multiply(new BigDecimal("0.16"))
                    .divide(new BigDecimal("1.16"), 2, RoundingMode.HALF_UP);
            venta.setIva(iva);
            venta.setSubtotal(venta.getTotal().subtract(iva));
        }

        if (venta.getFechaHora() == null) {
            venta.setFechaHora(LocalDateTime.now());
        }

        if (venta.getEstado() == null) {
            venta.setEstado(EstadoVenta.COMPLETADA);
        }

        if (venta.getFolio() == null || venta.getFolio().isEmpty()) {
            venta.setFolio(generarFolioAutomatico());
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle.getImporte() == null) {
                detalle.setImporte(detalle.getCantidad().multiply(detalle.getPrecioUnitario()));
            }
            detalle.setVenta(venta);
        }

        Venta ventaGuardada = ventaRepository.save(venta);

        Turno turno = ventaGuardada.getTurno();
        if (turno != null) {
            turno.setNumeroVentas(turno.getNumeroVentas() + 1);
            if (turno.getTotalVentas() == null) {
                turno.setTotalVentas(BigDecimal.ZERO);
            }
            turno.setTotalVentas(turno.getTotalVentas().add(ventaGuardada.getTotal()));
            turnoRepository.save(turno);
        }

        for (DetalleVenta detalle : ventaGuardada.getDetalles()) {
            if (detalle.getTipoProducto().name().contains("COMBUSTIBLE")) {
                try {
                    String tipoCombustible = detalle.getTipoProducto().name().replace("COMBUSTIBLE_", "");
                    String tipoParaInventario;
                    switch (tipoCombustible) {
                        case "GASOLINA_MAGNA":
                            tipoParaInventario = "MAGNA";
                            break;
                        case "GASOLINA_PREMIUM":
                            tipoParaInventario = "PREMIUM";
                            break;
                        case "DIESEL":
                            tipoParaInventario = "DIESEL";
                            break;
                        default:
                            tipoParaInventario = "MAGNA";
                    }
                    inventarioClient.descontarStockCombustible(
                            tipoParaInventario,
                            detalle.getCantidad(),
                            "Venta ID: " + ventaGuardada.getId()
                    );
                } catch (Exception e) {
                    // Error silencioso
                }
            }
        }

        return ventaGuardada;
    }

    public Optional<Venta> obtenerVenta(Long id) {
        return ventaRepository.findById(id);
    }

    public Optional<Venta> obtenerPorFolio(String folio) {
        return ventaRepository.findByFolio(folio);
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Page<Venta> listarTodas(Pageable page) {
        return ventaRepository.findAll(page);
    }

    public List<Venta> listarPorEstado(EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }

    public List<Venta> listarPorDespachador(Long despachadorId) {
        return ventaRepository.findByDespachadorId(despachadorId);
    }

    public List<Venta> listarPorTurno(Long turnoId) {
        return ventaRepository.findByTurnoId(turnoId);
    }

    public List<Venta> listarPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaHoraBetween(inicio, fin);
    }

    public Venta actualizarVenta(long id, Venta ventaActualizada) {
        Venta ventaExiste = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la venta con id: " + id));

        if (ventaExiste.getEstado() == EstadoVenta.CANCELADA) {
            throw new IllegalStateException("No se puede modificar una venta cancelada");
        }
        if (ventaActualizada.getEstado() != null) {
            ventaExiste.setEstado(ventaActualizada.getEstado());
        }
        if (ventaActualizada.getFacturada() != null) {
            ventaExiste.setFacturada(ventaActualizada.getFacturada());
        }
        if (ventaActualizada.getFolioFactura() != null) {
            ventaExiste.setFolioFactura(ventaActualizada.getFolioFactura());
        }
        return ventaRepository.save(ventaExiste);
    }

    public boolean cancelarVenta(long id) {
        Optional<Venta> ventaOpt = ventaRepository.findById(id);
        if (ventaOpt.isEmpty()) {
            return false;
        }
        Venta venta = ventaOpt.get();
        venta.setEstado(EstadoVenta.CANCELADA);
        ventaRepository.save(venta);
        return true;
    }

    public EstadisticasVentasDTO obtenerEstadisticas() {
        LocalDateTime inicioHoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finHoy = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<Venta> ventasHoy = ventaRepository.findByFechaHoraBetween(inicioHoy, finHoy);

        double totalHoy = ventasHoy.stream()
                .mapToDouble(v -> v.getTotal() != null ? v.getTotal().doubleValue() : 0.0)
                .sum();

        return new EstadisticasVentasDTO(
                ventaRepository.count(),
                ventasHoy.size(),
                totalHoy,
                ventaRepository.countByEstado(EstadoVenta.COMPLETADA),
                ventaRepository.countByEstado(EstadoVenta.PENDIENTE),
                ventaRepository.countByEstado(EstadoVenta.CANCELADA),
                ventaRepository.findByFacturada(true).size(),
                ventaRepository.findByEsCredito(true).size()
        );
    }

    public boolean puedeFacturar(Long ventaId) {
        Optional<Venta> ventaOpt = obtenerVenta(ventaId);
        if (ventaOpt.isEmpty()) return false;
        Venta venta = ventaOpt.get();
        return venta.getEstado() == EstadoVenta.COMPLETADA &&
                (venta.getFacturada() == null || !venta.getFacturada());
    }

    public boolean puedeCancelar(Long ventaId) {
        Optional<Venta> ventaOpt = ventaRepository.findById(ventaId);
        if (ventaOpt.isEmpty()) return false;
        Venta venta = ventaOpt.get();
        return venta.getEstado() != EstadoVenta.CANCELADA && venta.getEstado() != EstadoVenta.FACTURADA;
    }

    public Map<String, Object> obtenerUltimaVenta() {
        List<Venta> ventas = ventaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaHora"));

        if (ventas == null || ventas.isEmpty()) {
            return Map.of("mensaje", "No hay ventas registradas");
        }

        Venta ultima = ventas.get(0);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("folio", ultima.getFolio());
        respuesta.put("total", ultima.getTotal());
        respuesta.put("fecha", ultima.getFechaHora().toString());
        respuesta.put("metodoPago", ultima.getMetodoPago().toString());
        respuesta.put("despachador", ultima.getDespachadorNombre());

        List<Map<String, Object>> detalles = new ArrayList<>();
        if (ultima.getDetalles() != null) {
            for (DetalleVenta d : ultima.getDetalles()) {
                Map<String, Object> detalle = new HashMap<>();
                detalle.put("producto", d.getProductoNombre());
                detalle.put("cantidad", d.getCantidad());
                detalle.put("importe", d.getImporte());
                detalles.add(detalle);
            }
        }
        respuesta.put("detalles", detalles);

        return respuesta;
    }

    public Map<String, Object> obtenerVentasHoy() {
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<Venta> ventas = ventaRepository.findByFechaHoraBetween(inicio, fin);

        BigDecimal total = ventas.stream()
                .map(Venta::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("cantidad", ventas.size());
        respuesta.put("total", total);
        respuesta.put("ventas", ventas.stream().map(v -> Map.of(
                "folio", v.getFolio(),
                "total", v.getTotal(),
                "hora", v.getFechaHora().toString()
        )).collect(Collectors.toList()));

        return respuesta;
    }

    public Map<String, BigDecimal> obtenerVentasPorTipo() {
        List<Venta> ventas = ventaRepository.findAll();

        Map<String, BigDecimal> ventasPorTipo = new HashMap<>();
        ventasPorTipo.put("MAGNA", BigDecimal.ZERO);
        ventasPorTipo.put("PREMIUM", BigDecimal.ZERO);
        ventasPorTipo.put("DIESEL", BigDecimal.ZERO);

        for (Venta v : ventas) {
            if (v.getDetalles() != null) {
                for (DetalleVenta d : v.getDetalles()) {
                    String nombre = d.getProductoNombre();
                    if (nombre != null) {
                        if (nombre.contains("MAGNA")) {
                            ventasPorTipo.put("MAGNA", ventasPorTipo.get("MAGNA").add(d.getImporte()));
                        } else if (nombre.contains("PREMIUM")) {
                            ventasPorTipo.put("PREMIUM", ventasPorTipo.get("PREMIUM").add(d.getImporte()));
                        } else if (nombre.contains("DIESEL")) {
                            ventasPorTipo.put("DIESEL", ventasPorTipo.get("DIESEL").add(d.getImporte()));
                        }
                    }
                }
            }
        }

        return ventasPorTipo;
    }

    private String generarFolioAutomatico() {
        LocalDateTime ahora = LocalDateTime.now();
        String fecha = String.format("%04d%02d%02d", ahora.getYear(), ahora.getMonthValue(), ahora.getDayOfMonth());
        String hora = String.format("%02d%02d%02d", ahora.getHour(), ahora.getMinute(), ahora.getSecond());
        return String.format("VENTA-%s-%s-%04d", fecha, hora, System.currentTimeMillis() % 10000);
    }
}