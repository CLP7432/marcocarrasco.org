package com.gasmanager.ventas.clients;

import com.gasmanager.ventas.dto.PrecioUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "microservice-inventarios")
public interface InventarioClient {

    // ========== COMBUSTIBLES (precios) ==========
    @GetMapping("/api/combustibles")
    List<CombustibleDTO> listarCombustibles();

    @GetMapping("/api/combustibles/{id}")
    CombustibleDTO obtenerCombustible(@PathVariable("id") Long id);

    @DeleteMapping("/api/combustibles/limpiar-todo")
    void limpiarTodoCombustibles();

    @GetMapping("/api/combustibles/activos")
    List<CombustibleDTO> listarCombustiblesActivos();

    // ========== ACEITES (stock) ==========
    @GetMapping("/api/aceites/{id}/validar-stock")
    Boolean validarStock(@PathVariable("id") Long id,
                         @RequestParam("cantidadRequerida") Integer cantidad);

    @PostMapping("/api/aceites/{id}/disminuir-stock")
    AceiteDTO disminuirStock(@PathVariable("id") Long id,
                             @RequestParam("cantidad") Integer cantidad,
                             @RequestParam("motivo") String motivo);

    // ========== INVENTARIO DE COMBUSTIBLE (tanques) ==========
    @GetMapping("/api/inventario-combustible/tipo/{tipo}")
    InventarioCombustibleDTO obtenerInventarioCombustiblePorTipo(@PathVariable("tipo") String tipo);

    @PostMapping("/api/inventario-combustible/descontar")
    void descontarStockCombustible(@RequestParam("tipo") String tipo,
                                   @RequestParam("cantidad") BigDecimal cantidad,
                                   @RequestParam("motivo") String motivo);

    @GetMapping("/api/inventario-combustible/stock-bajo")
    List<InventarioCombustibleDTO> verificarStockBajoCombustible();

    // ========== PRECIOS ==========
    @PutMapping("/api/combustibles/{id}/precio")
    CombustibleDTO actualizarPrecioCombustible(
            @PathVariable("id") Long id,
            @Valid @RequestBody PrecioUpdateDTO request);

    @GetMapping("/api/combustibles/precio-actual")
    BigDecimal obtenerPrecioActualPorTipo(@RequestParam("tipo") String tipo);

    // ========== DTOs internos ==========
    class CombustibleDTO {
        private Long id;
        private String tipo;
        private String nombre;
        private BigDecimal precioActual;
        private Boolean activo;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public BigDecimal getPrecioActual() { return precioActual; }
        public void setPrecioActual(BigDecimal precioActual) { this.precioActual = precioActual; }
        public Boolean getActivo() { return activo; }
        public void setActivo(Boolean activo) { this.activo = activo; }
    }

    class AceiteDTO {
        private Long id;
        private String codigo;
        private String nombre;
        private BigDecimal precioVenta;
        private Integer stockActual;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public BigDecimal getPrecioVenta() { return precioVenta; }
        public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
        public Integer getStockActual() { return stockActual; }
        public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    }

    class InventarioCombustibleDTO {
        private Long id;
        private String tipoCombustible;
        private String nombre;
        private BigDecimal stockActual;
        private BigDecimal capacidadTanque;
        private BigDecimal stockMinimo;
        private BigDecimal porcentajeOcupacion;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTipoCombustible() { return tipoCombustible; }
        public void setTipoCombustible(String tipoCombustible) { this.tipoCombustible = tipoCombustible; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public BigDecimal getStockActual() { return stockActual; }
        public void setStockActual(BigDecimal stockActual) { this.stockActual = stockActual; }
        public BigDecimal getCapacidadTanque() { return capacidadTanque; }
        public void setCapacidadTanque(BigDecimal capacidadTanque) { this.capacidadTanque = capacidadTanque; }
        public BigDecimal getStockMinimo() { return stockMinimo; }
        public void setStockMinimo(BigDecimal stockMinimo) { this.stockMinimo = stockMinimo; }
        public BigDecimal getPorcentajeOcupacion() { return porcentajeOcupacion; }
        public void setPorcentajeOcupacion(BigDecimal porcentajeOcupacion) { this.porcentajeOcupacion = porcentajeOcupacion; }
    }
}