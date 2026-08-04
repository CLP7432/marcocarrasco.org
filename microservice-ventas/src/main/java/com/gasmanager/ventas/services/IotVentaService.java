package com.gasmanager.ventas.services;

import com.gasmanager.ventas.dto.IotVentaRequest;
import com.gasmanager.ventas.entities.core.DetalleVenta;
import com.gasmanager.ventas.entities.core.Turno;
import com.gasmanager.ventas.entities.core.Venta;
import com.gasmanager.ventas.enums.EstadoTurno;
import com.gasmanager.ventas.enums.EstadoVenta;
import com.gasmanager.ventas.enums.MetodoPagoEnum;
import com.gasmanager.ventas.enums.TipoProductoEnum;
import com.gasmanager.ventas.enums.UnidadMedidaEnum;
import com.gasmanager.ventas.repositories.TurnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IotVentaService {

    private final TurnoRepository turnoRepository;
    private final VentaService ventaService;

    @Transactional
    public Map<String, Object> procesarCarga(IotVentaRequest request) {
        log.info("=== PROCESANDO CARGA IoT EN VENTAS ===");
        log.info("Dispensario: {} ({})", request.getDispensarioId(), request.getDispensarioNombre());
        log.info("Manguera: {} ({})", request.getMangueraId(), request.getMangueraNombre());
        log.info("Despachador: {} ({})", request.getDespachadorId(), request.getDespachadorNombre());
        log.info("Litros: {}, Total: {}", request.getLitros(), request.getTotal());

        if (request.getDespachadorId() == null || request.getDespachadorNombre() == null) {
            throw new IllegalArgumentException(
                    "No se puede completar la carga sin un despachador asignado al dispensario.");
        }

        List<Turno> turnosActivos = turnoRepository.findByEstado(EstadoTurno.ABIERTO);
        if (turnosActivos.isEmpty()) {
            throw new IllegalStateException("No hay un turno activo. El supervisor debe abrir un turno.");
        }
        Turno turno = turnosActivos.get(0);
        log.info("Turno activo: {}", turno.getCodigoTurno());

        if (request.getPrecioUnitario() == null) {
            throw new IllegalArgumentException("No se puede completar la carga sin un precio unitario.");
        }
        BigDecimal precio = request.getPrecioUnitario();

        BigDecimal total = request.getTotal() != null ?
                request.getTotal() : request.getLitros().multiply(precio);

        Venta venta = new Venta();
        venta.setTurno(turno);
        venta.setMetodoPago(MetodoPagoEnum.EFECTIVO);
        venta.setEstado(EstadoVenta.COMPLETADA);
        venta.setDespachadorId(request.getDespachadorId());
        venta.setDespachadorNombre(request.getDespachadorNombre());
        venta.setFechaHora(LocalDateTime.now());
        venta.setTotal(total);
        venta.setSubtotal(total.divide(new BigDecimal("1.16"), 2, RoundingMode.HALF_UP));
        venta.setIva(total.subtract(venta.getSubtotal()));
        venta.setFolio(generarFolioIoT());
        venta.setDispensarioId(request.getDispensarioId() != null ? request.getDispensarioId().intValue() : null);
        venta.setSurtidorId(request.getMangueraId() != null ? request.getMangueraId().intValue() : 1);

        DetalleVenta detalle = DetalleVenta.builder()
                .productoId(request.getMangueraId())
                .productoNombre(request.getMangueraNombre() != null ?
                        request.getMangueraNombre() : request.getTipoCombustible())
                .cantidad(request.getLitros())
                .precioUnitario(precio)
                .importe(request.getLitros().multiply(precio))
                .unidadMedida(UnidadMedidaEnum.LITROS)
                .tipoProducto(mapearTipoProducto(request.getTipoCombustible()))
                .build();
        detalle.setVenta(venta);

        Venta ventaGuardada = ventaService.crearVenta(venta);
        log.info("Venta creada con folio: {}", ventaGuardada.getFolio());

        turno.setNumeroVentas(turno.getNumeroVentas() + 1);
        if (turno.getTotalVentas() == null) {
            turno.setTotalVentas(BigDecimal.ZERO);
        }
        turno.setTotalVentas(turno.getTotalVentas().add(total));
        if (turno.getLitrosVendidos() == null) {
            turno.setLitrosVendidos(BigDecimal.ZERO);
        }
        turno.setLitrosVendidos(turno.getLitrosVendidos().add(request.getLitros()));
        turnoRepository.save(turno);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("folio", ventaGuardada.getFolio());
        respuesta.put("ventaId", ventaGuardada.getId());
        respuesta.put("total", ventaGuardada.getTotal());
        respuesta.put("litros", request.getLitros());
        respuesta.put("tipoCombustible", request.getTipoCombustible());
        respuesta.put("turno", turno.getCodigoTurno());
        respuesta.put("despachador", request.getDespachadorNombre());
        respuesta.put("dispensario", request.getDispensarioNombre());
        respuesta.put("manguera", request.getMangueraNombre());
        respuesta.put("mensaje", "Carga IoT completada exitosamente");

        return respuesta;
    }

    private TipoProductoEnum mapearTipoProducto(String tipo) {
        if (tipo == null) return TipoProductoEnum.COMBUSTIBLE_GASOLINA_MAGNA;
        switch (tipo.toUpperCase()) {
            case "MAGNA": return TipoProductoEnum.COMBUSTIBLE_GASOLINA_MAGNA;
            case "PREMIUM": return TipoProductoEnum.COMBUSTIBLE_GASOLINA_PREMIUM;
            case "DIESEL": return TipoProductoEnum.COMBUSTIBLE_DIESEL;
            default: return TipoProductoEnum.COMBUSTIBLE_GASOLINA_MAGNA;
        }
    }

    private String generarFolioIoT() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return String.format("IOT-%s-%04d", fecha, random);
    }
}